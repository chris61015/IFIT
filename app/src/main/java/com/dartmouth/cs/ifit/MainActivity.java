package com.dartmouth.cs.ifit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dartmouth.cs.ifit.DB.CollectionInfoDAO;
import com.dartmouth.cs.ifit.Model.CollectionEntry;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityEntriesAdapter mEntryAdaptor;
    private List<CollectionEntry> values = new ArrayList<>();
    private CollectionInfoDAO datasource;
    public static final String ID = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get Data From DB
        datasource = new CollectionInfoDAO(this);

        AsyncTaskLoad loadFromDB = new AsyncTaskLoad();
        loadFromDB.execute();

        mEntryAdaptor = new ActivityEntriesAdapter(this,R.layout.collection_item, values);
//        mEntryAdaptor.setEntryList(values);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                    CollectionEntry entry = new CollectionEntry();
                    entry.setCollectionName("Collection");
                    mEntryAdaptor.add(entry);
            }
        });

        ListView listView = (ListView) findViewById(R.id.collection_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ShowTimelineActivity.class);
                CollectionEntry entry = mEntryAdaptor.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putLong(ID, entry.getId());
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
        listView.setAdapter(mEntryAdaptor);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncTaskLoad extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            synchronized (values) {
                values.addAll(datasource.fetchEntries());
            }
            return null;
        }

    }
}
