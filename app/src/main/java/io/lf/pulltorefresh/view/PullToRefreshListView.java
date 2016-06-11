package io.lf.pulltorefresh.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.lf.pulltorefresh.R;

/**
 * Created by adly on 2016/6/10.
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private ImageView iv_arrow;
    private ProgressBar progress;
    private TextView tv_state;
    private TextView tv_stamp;
    private int headerViewHeight;
    private RotateAnimation upAnimation;
    private RotateAnimation downAnimation;

    private final int PULL_REFRESH = 0;
    private final int RELEASE_REFRESH = 1;
    private final int REFRESHING = 2;
    private int currentState = PULL_REFRESH;
    private int downY;
    private View headerView;
    private View footerView;
    private int footerViewHeight;

    private boolean isLoadingMore = false;

    public PullToRefreshListView(Context context) {
        this(context, null);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnScrollListener(this);
        initHeaderView();
        initRotateAnimation();
        initFooterView();
    }

    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.layout_header, null);

        iv_arrow = (ImageView) headerView.findViewById(R.id.iv_arrow);
        progress = (ProgressBar) headerView.findViewById(R.id.progress);
        tv_state = (TextView) headerView.findViewById(R.id.tv_state);
        tv_stamp = (TextView) headerView.findViewById(R.id.tv_stamp);
        headerView.measure(0, 0);
        headerViewHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerViewHeight, 0, 0);

        addHeaderView(headerView);
    }

    private void initRotateAnimation() {
        upAnimation = new RotateAnimation(0, -180,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180, -360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);

    }

    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.layout_footer, null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        addFooterView(footerView);
    }

    // fixes in listView.setNestedScrollingEnabled(true);
    //private static final int MAXIMUM_LIST_ITEMS_VIEWABLE = 99;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /*super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int newHeight = 0;
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            ListAdapter listAdapter = getAdapter();
            if (listAdapter != null && !listAdapter.isEmpty()) {
                int listPosition = 0;
                for (listPosition = 0; listPosition < listAdapter.getCount()
                        && listPosition < MAXIMUM_LIST_ITEMS_VIEWABLE; listPosition++) {
                    View listItem = listAdapter.getView(listPosition, null,
                            this);
                    // now it will not throw a NPE if listItem is a ViewGroup
                    // instance
                    if (listItem instanceof ViewGroup) {
                        listItem.setLayoutParams(new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
                    }
                    listItem.measure(widthMeasureSpec, heightMeasureSpec);
                    newHeight += listItem.getMeasuredHeight();
                }
                newHeight += getDividerHeight() * listPosition;
            }
            if ((heightMode == MeasureSpec.AT_MOST) && (newHeight > heightSize)) {
                if (newHeight > heightSize) {
                    newHeight = heightSize;
                }
            }
        } else {
            newHeight = getMeasuredHeight();
        }
        setMeasuredDimension(getMeasuredWidth(), newHeight);
        */
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentState == REFRESHING) {
                    break;
                }
                int deltaY = (int) (ev.getY() - downY);
                //Log.v(PullToRefreshListView.class.getSimpleName(), "downy: " + downY + "\ty: " + ev.getY() + "\tdy: " + deltaY + "\theight: " + headerViewHeight);
                if(getFirstVisiblePosition() == 0 && deltaY > 0) {
                    headerView.setPadding(0, -headerViewHeight + deltaY, 0, 0);

                    if(deltaY > headerViewHeight && currentState == PULL_REFRESH){
                        currentState = RELEASE_REFRESH;
                        refreshHeaderView();
                    }else if(deltaY < headerViewHeight && currentState == RELEASE_REFRESH){
                        currentState = PULL_REFRESH;
                        refreshHeaderView();
                    }
                    return true;
                }

            break;
            case MotionEvent.ACTION_UP:
                if (currentState == PULL_REFRESH) {
                    headerView.setPadding(0, -headerViewHeight, 0, 0);
                }else if(currentState == RELEASE_REFRESH){
                    headerView.setPadding(0, 0, 0, 0);
                    currentState = REFRESHING;
                    refreshHeaderView();

                    if(listener != null){
                        listener.onRefreshing();
                    }
                }

                //break;
        }
        return super.onTouchEvent(ev);
    }

    private void refreshHeaderView() {
        switch (currentState){
            case PULL_REFRESH:
                tv_state.setText("下拉刷新");
                iv_arrow.startAnimation(downAnimation);
                break;
            case RELEASE_REFRESH:
                tv_state.setText("松开刷新");
                iv_arrow.startAnimation(upAnimation);
                break;
            case REFRESHING:
                iv_arrow.clearAnimation();
                iv_arrow.setVisibility(INVISIBLE);
                progress.setVisibility(VISIBLE);
                tv_state.setText("正在刷新...");
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.v(PullToRefreshListView.class.getSimpleName(), "lastVisiblePosition: " + getLastVisiblePosition() +
                "\tgetCount: " + getCount());
        if(scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && getLastVisiblePosition() == (getCount() - 1) && !isLoadingMore){

            isLoadingMore = !isLoadingMore;
            footerView.setPadding(0, 0, 0, 0);
            setSelection(getCount());
            if(listener!=null){
                listener.onLoadingMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private OnRefreshListener listener;

    public void setOnRefreshListener(OnRefreshListener listener){
        this.listener = listener;
    }

    public interface OnRefreshListener{
        void onRefreshing();
        void onLoadingMore();
    }

    public void onRefreshComplete(){
        if(isLoadingMore){
            footerView.setPadding(0, -footerViewHeight, 0, 0);
            isLoadingMore = !isLoadingMore;
        }else {
            headerView.setPadding(0, -headerViewHeight, 0, 0);
            currentState = PULL_REFRESH;
            progress.setVisibility(INVISIBLE);
            iv_arrow.setVisibility(VISIBLE);
            tv_state.setText("下拉刷新");
            tv_stamp.setText("最后更新时间: " + getCurrentTime());
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
