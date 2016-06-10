package io.lf.pulltorefresh;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adly on 2016/6/10.
 */
public class ListViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = View.inflate(getContext(), R.layout.fragment_listview, null);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setVerticalScrollBarEnabled(false);

        CustomAdapter adapter = new CustomAdapter();
        for (int i = 0; i < 16; i++) {
            adapter.lists.add("这是ListView中的item: " + i);
        }
        listView.setAdapter(adapter);

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
