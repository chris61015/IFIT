package com.dartmouth.cs.ifit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.dartmouth.cs.ifit.Model.CollectionEntry;

import java.util.List;

/**
 * Created by chris61015 on 1/30/17.
 */

public class ActivityEntriesAdapter extends ArrayAdapter {

    private Context mContext;
    private List<CollectionEntry> mEntryList;

    @Override
    public int getCount() {
        return mEntryList.size();
    }

    public CollectionEntry getItem(int position) {
        return mEntryList.get(position);
    }

    public ActivityEntriesAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.collection_item, parent, false);

        TextView nameView = (TextView) view.findViewById(R.id.col_name);

        CollectionEntry entry = mEntryList.get(position);
        nameView.setText(entry.getCollectionName());

        return view;
    }

    public void add(CollectionEntry entry){
        mEntryList.add(entry);
        notifyDataSetChanged();
    }

    public void remove(int pos){
        mEntryList.remove(pos);
        notifyDataSetChanged();
    }

//    public int findPosById(int id){
//        for (int i = 0 ; i < mEntryList.size();i++){
//            if (id == mEntryList.get(i).getId()) return i;
//        }
//        return -1;
//
//    }

    public void setEntryList(List<CollectionEntry> list){
        mEntryList = list;
    }

}
