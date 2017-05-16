package com.dartmouth.cs.ifit;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.dartmouth.cs.ifit.DB.TimelineInfoDAO;
import com.dartmouth.cs.ifit.Model.TimelineEntry;
import com.dartmouth.cs.ifit.Notification.NotificationPublisher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chris61015 on 5/14/17.
 */

public class ShowTimelineActivity extends AppCompatActivity implements TimeLineAdapter.OnRecyclerViewItemClickListener{
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private static List<TimelineEntry> mDataList = new ArrayList<>();
    public static String TIMELINE = "timeline";
    private TimelineInfoDAO datasource;

    int mYear = -1, mMonth = -1, mDay = -1, mHour = -1, mMinute = -1;
    String remindText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_timeline);
        //Fetch Data
        Intent intent = getIntent();
        Long groupId = intent.getLongExtra(MainActivity.ID,0L);

        //Get Data From DB
        datasource = new TimelineInfoDAO(this);
        datasource.open();

        AsyncTaskLoad loadFromDB = new AsyncTaskLoad();
        loadFromDB.execute(groupId);

        //Timeline view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);

        final Button recordButton = (Button) findViewById(R.id.btnAddPhoto);
        recordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                TimelineEntry entry = datasource.getEntryById(45);
                if (entry != null) {
                    cancelNotification(entry);
                    datasource.removeEntry(entry.getId());
                }
            }
        });

        final Button reminderButton = (Button) findViewById(R.id.btnAddReminder);
        reminderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mYear = -1;
                mMonth = -1;
                mDay = -1;
                mHour = -1;
                mMinute = -1;
                remindText = null;

                datePicker();
            }
        });
        initView();
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    }

    private void initView() {
        mTimeLineAdapter = new TimeLineAdapter();
        mTimeLineAdapter.addAll(mDataList);
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

    private class AsyncTaskLoad extends AsyncTask<Long, Void, Void>
    {
        @Override
        protected Void doInBackground(Long... params)
        {

            mDataList.addAll(datasource.fetchEntryByGroupId(params[0]));
            return null;
        }

    }

    private void datePicker(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        //*************Call Time Picker Here ********************
                        timePicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void timePicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;

                        final EditText remind = new EditText(ShowTimelineActivity.this);
                        new AlertDialog.Builder(ShowTimelineActivity.this)
                                .setTitle("Add Reminder Note")
                                .setView(remind)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        remindText = remind.getText().toString();
                                        if (mYear != -1 && mMonth != -1 && mDay != -1 && mHour != -1 && mMinute != -1 && remindText != null) {
                                            TimelineEntry entry = new TimelineEntry();
                                            Calendar c = Calendar.getInstance();
                                            c.setTimeInMillis(System.currentTimeMillis());
                                            c.set(Calendar.YEAR, mYear);
                                            c.set(Calendar.MONTH, mMonth);
                                            c.set(Calendar.DAY_OF_MONTH, mDay);
                                            c.set(Calendar.HOUR_OF_DAY, mHour);
                                            c.set(Calendar.MINUTE, mMinute);
                                            entry.setDateTime(c);
                                            entry.setRemind(1);
                                            entry.setRemindText(remindText);
                                            datasource.insertEntry(entry);
                                            scheduleNotification(entry);
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                })
                                .show();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void scheduleNotification(TimelineEntry entry) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("iFit Reminder");
        builder.setContentText(entry.getRemindText());
        builder.setSmallIcon(R.drawable.marker);
        Notification notification = builder.build();

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, (int) entry.getId());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) entry.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, entry.getDateTime().getTimeInMillis(), pendingIntent);
    }

    private void cancelNotification(TimelineEntry entry) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, (int) entry.getId(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        datasource.close();
        super.onDestroy();
    }

}
