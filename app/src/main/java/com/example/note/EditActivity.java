package com.example.note;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/*
 *
 * Name: Leo Yao
 * ID: 16287261
 *
 * */

public class EditActivity extends AppCompatActivity {

    EditText editText;
    String content;
    String time;
    String path;
    FloatingActionButton floatingActionButton;
    long id;
    Toolbar toolbar;
    int openModel = 0;
    Intent intent = new Intent();
    ImageView img;
    File phone;
    Uri uri;
    Uri uri2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        init();
    }

    // used to init view
    private void init() {
        Bitmap bitmap = null;
        editText = findViewById(R.id.edit);
        Intent getIntent = getIntent();
        floatingActionButton = findViewById(R.id.floatingActionButton2);
        toolbar = findViewById(R.id.edit_toolBar);
        img = findViewById(R.id.edit_img);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // open picture document
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                checkPermission();

            }
        });

        openModel = getIntent.getIntExtra("model", 0);

        // not a new note
        if (openModel == 3) {
            // get the note information
            id = getIntent.getLongExtra("id", 0);
            time = getIntent.getStringExtra("time");
            content = getIntent.getStringExtra("content");
            path = getIntent.getStringExtra("path");
            if (path != null ) {
                uri2 = Uri.parse(path);
                try {
                    bitmap = Bitmap.createScaledBitmap(
                            MediaStore.Images.Media.getBitmap(getContentResolver(), uri2),500,500,
                            true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img.setImageBitmap(bitmap);
            }
            editText.setText(content);
            editText.setSelection(content.length());
        }
    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)   {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
            return;
        }else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        }else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // ask user whether delete the note
    public static class SureDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final EditActivity activity = (EditActivity) getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage("Are you Sure ?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (activity.openModel == 4) {
                                // new note
                                activity.intent.putExtra("model", -1);
                            } else {
                                // exist note
                                activity.intent.putExtra("model", 2);
                                activity.intent.putExtra("id", activity.id);
                            }
                            activity.setResult(RESULT_OK, activity.intent);
                            activity.finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            new SureDialog().show(getSupportFragmentManager(), null);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void message() {
        //new note
        if (openModel == 4) {
            // No choice to insert any pictures
            if (uri == null){
                // No text added
                if (editText.getText().length()==0) {
                    //return -1 that means do nothing
                    intent.putExtra("model", -1);
                }else { // some words added
                    intent.putExtra("content", editText.getText().toString());
                    intent.putExtra("time", getTime());
                    intent.putExtra("model", 0);
                }

            }else { //user selected a picture from the phone
                intent.putExtra("path", uri.toString());
                intent.putExtra("content", editText.getText().toString());
                intent.putExtra("time", getTime());
                intent.putExtra("model", 0);
            }
        } else if (openModel == 3) { // The user clicks on an existing note and prepares to edit
            if(path!=null){//No picture inserted in note
                if (uri==null){ // user does not add a picture on the note
                    // user does nothing return -1
                    if (editText.getText().toString().equals(content)) {
                        intent.putExtra("model", -1);
                    }else { // user edits the note
                        intent.putExtra("content", editText.getText().toString());
                        intent.putExtra("time", getTime());
                        intent.putExtra("model", 1);
                        intent.putExtra("id", id);
                    }
                }else { // user add a picture on the note
                    intent.putExtra("path", uri.toString());
                    intent.putExtra("content", editText.getText().toString());
                    intent.putExtra("time", getTime());
                    intent.putExtra("model", 1);
                    intent.putExtra("id", id);
                }
                // there is picture inserted in note
            }else {
                // the user dose not update the note
                if (uri==null) {
                    // user does nothing return -1
                    if (editText.getText().toString().equals(content)) {
                        intent.putExtra("model", -1);
                    }else {
                        intent.putExtra("content", editText.getText().toString());
                        intent.putExtra("time", getTime());
                        intent.putExtra("model", 1);
                        intent.putExtra("id", id);
                    }
                }
                // the user updates the note picture
                if (uri != null){
                    intent.putExtra("path", uri.toString());
                    intent.putExtra("content", editText.getText().toString());
                    intent.putExtra("time", getTime());
                    intent.putExtra("model", 1);
                    intent.putExtra("id", id);
                }
            }
        }
    }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1) {
                if (data!=null) {
                    uri = data.getData();
                    //String path=getFilePathFromContentUri(originalUri,resolver);

                    Bitmap bitmap = null;
                    //Bitmap scalled = null;
                    try {
                        bitmap = Bitmap.createScaledBitmap(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 500, 500,
                                true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("EDIT", "URI-------" + uri);

                    img.setImageBitmap(bitmap);

                }
            }
        }
        //Returns the current time of the system
        public String getTime () {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            return format.format(date);
        }
}

