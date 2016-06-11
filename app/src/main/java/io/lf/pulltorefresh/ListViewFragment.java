package io.lf.pulltorefresh;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.lf.pulltorefresh.view.PullToRefreshListView;

/**
 * Created by adly on 2016/6/10.
 */
public class ListViewFragment extends Fragment {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            listView.onRefreshComplete();
        }
    };
    private CustomAdapter adapter;
    private PullToRefreshListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = View.inflate(getContext(), R.layout.fragment_listview, null);
        listView = (PullToRefreshListView) rootView.findViewById(R.id.listView);
        listView.setVerticalScrollBarEnabled(false);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }

        adapter = new CustomAdapter();
        for (int i = 0; i < 16; i++) {
            adapter.lists.add("这是ListView中的item: " + i);
        }
        listView.setAdapter(adapter);

        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefreshing() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        adapter.lists.add(0, "刷新加载的数据 0");
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });

        return rootView;
    }

    private class CustomAdapter extends BaseAdapter {
        private ArrayList<String> lists;

        public CustomAdapter() {
            lists = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(getContext());
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                float density = getResources().getDisplayMetrics().density;
                int padding = (int) (12 * density);
                convertView.setPadding(padding, padding, padding, padding);
            }
            ((TextView) convertView).setText(lists.get(position));

            return convertView;
        }
    }
}
