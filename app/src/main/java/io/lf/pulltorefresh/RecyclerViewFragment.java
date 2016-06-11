package io.lf.pulltorefresh;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.lf.pulltorefresh.view.PullToRefreshRecyclerView;

/**
 * Created by adly on 2016/6/10.
 */
public class RecyclerViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = View.inflate(getContext(), R.layout.fragment_recyclerview, null);
        PullToRefreshRecyclerView pullToRefreshRecyclerView = (PullToRefreshRecyclerView) rootView.findViewById(R.id.recyclerView);

        pullToRefreshRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CustomAdapter adapter = new CustomAdapter();
        for (int i = 0; i < 16; i++) {
            adapter.lists.add("这是RecyclerView中的item: " + i);
        }
        pullToRefreshRecyclerView.setAdapter(adapter);

        return rootView;
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
        private ArrayList<String> lists;

        public CustomAdapter(){
            lists = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            float density = getResources().getDisplayMetrics().density;
            int padding = (int) (12 * density);
            textView.setPadding(padding, 0, padding, padding);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f);

            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(lists.get(position));
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }
}
