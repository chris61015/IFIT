package com.dartmouth.cs.ifit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dartmouth.cs.ifit.DB.CollectionEntryDbHelper;
import com.dartmouth.cs.ifit.DB.CollectionInfoDAO;
import com.dartmouth.cs.ifit.DB.TimelineEntryDbHelper;
import com.dartmouth.cs.ifit.DB.TimelineInfoDAO;
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
                    datasource.insertEntry(entry);
                    mEntryAdaptor.add(entry);
                    values.add(entry);
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

        registerForContextMenu(listView);

        listView.setAdapter(mEntryAdaptor);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu,View v, ContextMenu.ContextMenuInfo menuInfo){
        if (v.getId() == R.id.collection_list){
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.add(0,0,0,"Rename");
            menu.add(0,1,1,"Delete Collection");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo();
        final int selectid = (int) info.id;
        final CollectionEntry entry = values.get(selectid);
        switch (menuItem.getItemId()) {
            case 0:
                final EditText renameText = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("Rename")
                        .setMessage("Type the new name for the collection.")
                        .setView(renameText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newName = renameText.getText().toString();
                                mEntryAdaptor.changeEntryName(selectid, newName);
                                entry.setCollectionName(newName);
                                datasource.updateEntryName(entry);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
                break;
            case 1:
                mEntryAdaptor.removeEntry(selectid);
                datasource.removeEntry(entry.getId());
                Toast.makeText(this, "Collection Deleted", Toast.LENGTH_LONG).show();
                break;

            default:
                break;

        }
        return true;
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
                for (CollectionEntry c : values)
                        mEntryAdaptor.add(c);
            }
            return null;
        }

    }
}
