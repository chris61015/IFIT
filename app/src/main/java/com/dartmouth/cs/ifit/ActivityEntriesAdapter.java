package com.dartmouth.cs.ifit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.dartmouth.cs.ifit.model.CollectionEntry;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
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
        mEntryList = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.collection_item, null,true);


        TextView txtTitle = (TextView) view.findViewById(R.id.Itemname);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);

        CollectionEntry entry = mEntryList.get(position);
        txtTitle.setText(entry.getCollectionName());

        ByteArrayInputStream imageStream = new ByteArrayInputStream(entry.getIcon());
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        imageView.setImageBitmap(theImage);

        return view;
    }

    public void add(CollectionEntry entry){
        mEntryList.add(entry);
        notifyDataSetChanged();
    }

    public void removeEntry(int pos){
        mEntryList.remove(pos);
        notifyDataSetChanged();
    }

    public void changeEntryName(int pos, String newName) {
        mEntryList.get(pos).setCollectionName(newName);
        notifyDataSetChanged();
    }

    public void changeEntryIcon(int pos, byte[] icon) {
        mEntryList.get(pos).setIcon(icon);
        notifyDataSetChanged();
    }

    public void changeEntryIcon(CollectionEntry entry, byte[] icon) {
        for (CollectionEntry c : mEntryList) {
            if (c.getId() == entry.getId()) {
                c.setIcon(icon);
                break;
            }
        }
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
