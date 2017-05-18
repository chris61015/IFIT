package com.dartmouth.cs.ifit;

/**
 * Created by chris61015 on 5/17/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dartmouth.cs.ifit.model.TimelineEntry;

import java.util.ArrayList;
import java.util.List;


/**
 * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
 * The individual pages are simple and just display two lines of text. The important section of
 * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
 */
public class SamplePagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<TimelineEntry> mDataList = new ArrayList<>();


    public SamplePagerAdapter (Context context, List<TimelineEntry> list){
        super();
        mContext = context;
        mDataList = list;
    }

    /**
     * @return the number of pages to display
     */
    @Override
    public int getCount() {
        return mDataList.size();
    }

    /**
     * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
     */
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    // BEGIN_INCLUDE (pageradapter_getpagetitle)
    /**
     * Return the title of the item at {@code position}. This is important as what this method
     * <p>
     * Here we construct one using the position value, but for real application the title should
     * refer to the item's contents.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return "Record " + (position + 1);
    }
    // END_INCLUDE (pageradapter_getpagetitle)

    /**
     * Instantiate the {@link View} which should be displayed at {@code position}. Here we
     * inflate a layout from the apps resources and then change the text view to signify the position.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate a new layout from our resources
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.pager_item,
                container, false);
        // Add the newly created View to the ViewPager
        container.addView(view);

        // Retrieve a TextView from the inflated View, and update its text
        TextView txtWeight = (TextView) view.findViewById(R.id.item_weight);
        TextView txtBodyFat = (TextView) view.findViewById(R.id.item_body_fat_rate);
        ImageView imgView = (ImageView) view.findViewById(R.id.ImageProfile);

        Double weight = mDataList.get(position).getWeight();
        Double bodyfatrate = mDataList.get(position).getBodyFatRate();
        byte[] photo = mDataList.get(position).getPhoto();

        StringBuilder sb = new StringBuilder();
        txtWeight.setText(sb.append("Weight: ").append(weight).toString());
        sb.setLength(0);
        txtBodyFat.setText(sb.append("Body Fat Rate: ").append(bodyfatrate).toString());

        Bitmap decodedByte = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        imgView.setImageBitmap(decodedByte);

        // Return the View
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
//            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    public void addData(TimelineEntry entry) {
        mDataList.add(entry);
//        notifyDataSetChanged();
    }

    public void addAll(List<TimelineEntry> lst) {
        mDataList.clear();
        mDataList.addAll(lst);
        notifyDataSetChanged();
    }
}
