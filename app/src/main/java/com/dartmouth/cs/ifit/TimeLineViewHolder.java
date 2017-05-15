package com.dartmouth.cs.ifit;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

/**
 * Created by chris61015 on 5/14/17.
 */

public class TimeLineViewHolder extends RecyclerView.ViewHolder{
    public TextView mDate, mName, mCity, mComment;
    public ImageView mImgView;
    public TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        //view initializer!
        super(itemView);
        mImgView = (ImageView) itemView.findViewById(R.id.image_timeline_photo);
        mDate = (TextView) itemView.findViewById(R.id.text_timeline_date);
        mName = (TextView) itemView.findViewById(R.id.text_timeline_name);
        mCity = (TextView) itemView.findViewById(R.id.text_timeline_city);
        mComment = (TextView) itemView.findViewById(R.id.text_timeline_comment);
        mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }
}