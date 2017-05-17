package com.dartmouth.cs.ifit.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dartmouth.cs.ifit.DB.TimelineInfoDAO;
import com.dartmouth.cs.ifit.R;
import com.dartmouth.cs.ifit.model.TimelineEntry;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.GregorianCalendar;

import static com.dartmouth.cs.ifit.R.id.imageView;


public class RecordActivity extends Activity {

    private EditText mETweight, mETbfr;
    private Button mBSave, mBChange, mBCancel;
    private ImageView mIVimage;

    private String weight, bfr;
    private boolean isTakenFromCamera;
    private Uri mImageCapturedUri, mCroppedImageUri;
    private TimelineInfoDAO datasource;
    private TimelineEntry entry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        MainActivity.verifyStoragePermissions(this);
        datasource = new TimelineInfoDAO(this);
        datasource.open();

        Intent intent = getIntent();
        Long timelineId = intent.getLongExtra(ShowTimelineActivity.TIMELINE_ID, -1);
        Long groupId = intent.getLongExtra(ShowTimelineActivity.GROUP_ID, -1);

        mETweight = (EditText) findViewById(R.id.editText);
        mETbfr = (EditText) findViewById(R.id.editText2);
        mETweight.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
        mETbfr.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);

        mIVimage = (ImageView) findViewById(imageView);
        mBSave = (Button) findViewById(R.id.button);
        mBChange = (Button) findViewById(R.id.button3);
        mBCancel = (Button) findViewById(R.id.button2);

        if (timelineId != -1) {
            entry = datasource.getEntryById(timelineId);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(entry.getPhoto());
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            mIVimage.setImageBitmap(theImage);
            mETweight.setText(String.valueOf(entry.getWeight()));
            mETbfr.setText(String.valueOf(entry.getBodyFatRate()));
        }
        else {
            entry = new TimelineEntry();
            entry.setGroudId(groupId);
            mIVimage.setImageResource(R.drawable.icon);
        }

        mBChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Open Camera", "Select from Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
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
            }
        });

        mBSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mBCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveProfile() {
        weight = mETweight.getText().toString();
        bfr = mETbfr.getText().toString();

        if (weight.length() == 0)
            weight = "0";
        if (bfr.length() == 0)
            bfr = "0";

        saveImageToEntry();
        entry.setWeight(Double.parseDouble(weight));
        entry.setBodyFatRate(Double.parseDouble(bfr));
        entry.setDateTime(GregorianCalendar.getInstance());
        if (entry.getId() != -1){
            datasource.updateEntry(entry);
        } else {
            datasource.insertEntry(entry);
        }
    }


    private void onImageChange() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mImageCapturedUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCapturedUri);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, MainActivity.REQUEST_CODE_TAKE_FROM_CAMERA);
        isTakenFromCamera = true;
    }

    private void onImageChangeFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), MainActivity.SELECT_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MainActivity.REQUEST_CODE_TAKE_FROM_CAMERA:
                    beginCrop(mImageCapturedUri);
                    break;

                case MainActivity.SELECT_FILE:
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
            mIVimage.setImageResource(0);
            mIVimage.setImageURI(mCroppedImageUri);
        }
    }

    private void saveImageToEntry(){
//        byte[] inputData = null;
//        try {
//            Uri targetUri = (mCroppedImageUri==null)? mImageCapturedUri: mCroppedImageUri;
//            InputStream iStream = getContentResolver().openInputStream(targetUri);
//            inputData = MainActivity.getBytes(iStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Bitmap bitmap = ((BitmapDrawable)mIVimage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        entry.setPhoto(byteArray);
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(MainActivity.URI_CROPPED_KEY, mCroppedImageUri);
    }


}