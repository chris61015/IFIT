package com.dartmouth.cs.ifit.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dartmouth.cs.ifit.DB.TimelineInfoDAO;
import com.dartmouth.cs.ifit.R;
import com.dartmouth.cs.ifit.fragment.SlidingTabsBasicFragment;
import com.dartmouth.cs.ifit.model.TimelineEntry;
import com.dartmouth.cs.ifit.notification.NotificationPublisher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chris61015 on 5/14/17.
 */

public class ShowTimelineActivity extends AppCompatActivity {

    public static String TIMELINE_ID = "timeline_id";
    public static final String GROUP_ID = "group_id";

    private TimelineInfoDAO datasource;
    private SlidingTabsBasicFragment mFragment = new SlidingTabsBasicFragment();
    private long G_ID;
    private List<TimelineEntry> mList = new ArrayList<>();
    int mYear = -1, mMonth = -1, mDay = -1, mHour = -1, mMinute = -1;
    String remindText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_timeline);

        //Fetch Data
        Intent intent = getIntent();
        final Long groupId = intent.getLongExtra(GROUP_ID,0L);
        G_ID = groupId;

        //Get Data From DB
        datasource = new TimelineInfoDAO(this);
        datasource.open();

        final Button recordButton = (Button) findViewById(R.id.btnAddPhoto);
        recordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);

                Bundle bundle = new Bundle();
                bundle.putLong(ShowTimelineActivity.GROUP_ID, groupId);
                intent.putExtras(bundle);

                startActivityForResult(intent, 1);
//                // Perform action on click
//                TimelineEntry entry = datasource.getEntryById(45);
//                if (entry != null) {
//                    cancelNotification(entry);
//                    datasource.removeEntry(entry.getId());
//                }
                finish();
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

        final Button deleteButton = (Button) findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (mList.size() > 0) {
                    int pagePos = mFragment.getPageSelected();
                    TimelineEntry entry = mList.get(pagePos);
                    datasource.removeEntry(entry.getId());
                    mList.clear();
                    mList.addAll(datasource.fetchEntryByGroupId(G_ID));
                    mFragment.setData(mList);
                    if (entry.getRemind() == 1)
                        cancelNotification(entry);

                    Intent intent = getIntent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(), "No record selected", Toast.LENGTH_SHORT).show();

            }
        });

        final Button modifyButton = (Button) findViewById(R.id.btnModify);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (mList.size() > 0) {
                    int pagePos = mFragment.getPageSelected();
                    TimelineEntry entry = mList.get(pagePos);

                    if (entry.getRemind() == 1) {
                        Toast.makeText(getApplicationContext(), "We cannot modify reminders", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(getApplicationContext(), RecordActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putLong(ShowTimelineActivity.TIMELINE_ID, entry.getId());
                    intent.putExtras(bundle);

                    startActivityForResult(intent, 1);
                }

                else
                    Toast.makeText(getApplicationContext(), "No record selected", Toast.LENGTH_SHORT).show();
            }
        });

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.sample_content_fragment, mFragment);
            transaction.commit();
        }
        mList.addAll(datasource.fetchEntryByGroupId(G_ID));
        mFragment.setData(mList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mList.clear();
        mList.addAll(datasource.fetchEntryByGroupId(G_ID));
        mFragment.setData(mList);
    }

//    private class AsyncTaskLoad extends AsyncTask<Long, Void, Void>
//    {
//        @Override
//        protected Void doInBackground(Long... params)
//        {
//            mDataList.addAll(datasource.fetchEntryByGroupId(params[0]));
//            return null;
//        }
//
//    }

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
                        AlertDialog dialog = new AlertDialog.Builder(ShowTimelineActivity.this)
                                .setTitle("Add Reminder Note")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        remindText = remind.getText().toString();
                                        if (mYear != -1 && mMonth != -1 && mDay != -1 && mHour != -1 && mMinute != -1) {
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
                                            entry.setGroudId(G_ID);
                                            datasource.insertEntry(entry);
                                            scheduleNotification(entry);
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }).create();
                        dialog.setView(remind, 60, 0, 60, 0);
                        dialog.show();
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
