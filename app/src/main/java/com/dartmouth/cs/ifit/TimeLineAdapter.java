package com.dartmouth.cs.ifit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dartmouth.cs.ifit.Model.TimelineEntry;
import com.github.vipulasri.timelineview.TimelineView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris61015 on 5/14/17.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> implements View.OnClickListener{

    private List<TimelineEntry> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, TimelineEntry entry);
    }

    public TimeLineAdapter() {
        mFeedList = new ArrayList<TimelineEntry>();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;
        view = mLayoutInflater.inflate(R.layout.item_timeline, parent, false);
        view.setOnClickListener(this);

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        TimelineEntry entry = mFeedList.get(position);

        //decode photo and put it onto timeline
//        Bitmap decodedByte;
//        if (entry.getPhoto() != null){
//            byte[] decodedString = Base64.decode(entry.getPhoto().replace(' ','+'), Base64.DEFAULT);
//            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            holder.mImgView.setImageBitmap(decodedByte);
//        }

        SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String time = formatTime.format(new Date(entry.getDateTime()));

        holder.mName.setText("Test");
        holder.mDate.setText("Time");
        holder.mComment.setText("Comment");

        //store the data into the tag of itemview, so we could retrieve it on click
        holder.itemView.setTag(entry);
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //listen to view.onclick and trigger another self-defined OnItemClickListener listener,
            // tricky part on implementing timeline!
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClick(v, (TimelineEntry)v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void clear(){
        mFeedList.clear();
    }

    public void addAll(List<TimelineEntry> entries){
        mFeedList.addAll(entries);
    }
}
