package com.dartmouth.cs.ifit.fragment;

/**
 * Created by chris61015 on 5/16/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dartmouth.cs.ifit.R;
import com.dartmouth.cs.ifit.adaptor.SamplePagerAdapter;
import com.dartmouth.cs.ifit.model.TimelineEntry;
import com.dartmouth.cs.ifit.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic sample which shows how to use
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";
    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;
    private SamplePagerAdapter mAdaptor;
    private List<TimelineEntry> mDataList = new ArrayList<TimelineEntry>();
    private int pageSelected;

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAdaptor = new SamplePagerAdapter(getContext(),mDataList);
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set its PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mAdaptor);
        // END_INCLUDE (setup_viewpager)
        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // its PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pageSelected = position;
            }
        };
        mSlidingTabLayout.setOnPageChangeListener(pageChangeListener);
        // END_INCLUDE (setup_slidingtablayout);
    }
    // END_INCLUDE (fragment_onviewcreated)

    public void setData(List<TimelineEntry> list){
        if (mAdaptor != null){
            mAdaptor.addAll(list);
        } else {
            mDataList.addAll(list);
        }
    }

    public int getPageSelected(){
        return pageSelected;
    }
}

