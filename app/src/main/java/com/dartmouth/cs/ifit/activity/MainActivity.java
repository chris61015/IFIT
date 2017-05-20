package com.dartmouth.cs.ifit.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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

import com.dartmouth.cs.ifit.adaptor.ActivityEntriesAdapter;
import com.dartmouth.cs.ifit.DB.CollectionInfoDAO;
import com.dartmouth.cs.ifit.R;
import com.dartmouth.cs.ifit.model.CollectionEntry;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityEntriesAdapter mEntryAdaptor;
    private List<CollectionEntry> values = new ArrayList<>();
    private CollectionInfoDAO datasource;;
    private byte[] defaultIcon;
    private CollectionEntry currentSelectedEntry = null;

    private boolean isTakenFromCamera;
    private Uri mImageCapturedUri, mCroppedImageUri;
    public static final String URI_CROPPED_KEY = "cropped_image";
    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 100;
    public static final int SELECT_FILE = 10;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        //Tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        defaultIcon = stream.toByteArray();

        //Get Data From DB
        datasource = new CollectionInfoDAO(this);
        datasource.open();

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
                    entry.setIcon(defaultIcon);
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
                bundle.putLong(ShowTimelineActivity.GROUP_ID, entry.getId());
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
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.add(0, 0, 0,"Rename");
            menu.add(0, 1, 1,"Change Icon");
            menu.add(0, 2, 2,"Delete Collection");
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
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Rename")
                        .setMessage("Type the new name for the collection.")
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
                        }).create();
                dialog.setView(renameText, 60, 0, 60, 0);
                dialog.show();
                break;
            case 1:
                currentSelectedEntry = entry;
                final CharSequence[] items = {"Open Camera", "Select from Gallery"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Pick Profile Picture");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Open Camera")) {
                            onImageChange();
                        } else {
                            onImageChangeFromGallery();
                        }
                    }
                });
                builder.show();
                break;
            case 2:
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
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify icon parent activity in AndroidManifest.xml.
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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    private void onImageChange() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mImageCapturedUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCapturedUri);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        isTakenFromCamera = true;
    }

    private void onImageChangeFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_FROM_CAMERA:
                    beginCrop(mImageCapturedUri);
                    break;

                case SELECT_FILE:
                    Uri selected = data.getData();
                    beginCrop(selected);
                    break;

                case Crop.REQUEST_CROP:
                    handleCrop(resultCode, data);
                    if (isTakenFromCamera) {
                        File f = new File(mImageCapturedUri.getPath());
                        if (f.exists())
                            f.delete();
                    }
                    break;
            }
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mCroppedImageUri = Crop.getOutput(result);

            byte[] inputData = null;
            try {
                InputStream iStream = getContentResolver().openInputStream(mCroppedImageUri);
                inputData = MainActivity.getBytes(iStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (inputData != null) {
                currentSelectedEntry.setIcon(inputData);
                mEntryAdaptor.changeEntryIcon(currentSelectedEntry, inputData);
                datasource.updateEntryIcon(currentSelectedEntry);
            }
        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(URI_CROPPED_KEY, mCroppedImageUri);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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
