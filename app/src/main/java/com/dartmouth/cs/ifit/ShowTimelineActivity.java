package com.dartmouth.cs.ifit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dartmouth.cs.ifit.Model.TimelineEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris61015 on 5/14/17.
 */

public class ShowTimelineActivity extends AppCompatActivity implements TimeLineAdapter.OnRecyclerViewItemClickListener{
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private static List<TimelineEntry> mDataList = new ArrayList<>();
    public static String TIMELINE = "timeline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_timeline);
        //Fetch Data
        Intent intent = getIntent();
        Long id = intent.getLongExtra(MainActivity.ID,0L);


        //Timeline view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);

        initView();
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    }

    private void initView() {
        mTimeLineAdapter = new TimeLineAdapter();
//        setDataListItems();
        mTimeLineAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

//    private void setDataListItems() {
//        Intent intent = getIntent();
//        long TreeId = intent.getLongExtra(TREEID, 0);
//    }

    @Override
    public void onItemClick(View view, TimelineEntry entry) {

//        Intent intent = new Intent(this, UpdateDetailsActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(TIMELINE, entry);
//
//        intent.putExtras(bundle);
//
//        startActivity(intent);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_timeline);
//        setSupportActionBar(mActionBarToolbar);
//        getSupportActionBar().setTitle("Timeline");
//    }
}
