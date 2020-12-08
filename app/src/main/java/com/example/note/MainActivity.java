package com.example.note;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/*
*
* Name: Leo Yao
* ID: 16287261
*
* */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Context context = this;
    private NoteAdapter adapter;

    FloatingActionButton button;
    Toolbar toolbar;
    ListView listView;
    List<NoteObj> noteObjList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       init();
    }

    // init view
    public void init(){

        button = findViewById(R.id.floatingActionButton);
        listView= findViewById(R.id.text);
        toolbar = findViewById(R.id.toolBar);
        adapter = new NoteAdapter(context,noteObjList);
        refreshListView();
        listView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        listView.setOnItemClickListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("model",4);
                startActivityForResult(intent,0);
            }
        });
    }
    // used to update the adapter view
    private void refreshListView() {
        DAO DAO = new DAO(context);
        DAO.Write();

        if (noteObjList.size() >0){
            noteObjList.clear();
        }
        noteObjList.addAll(DAO.getAll());
        DAO.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode ==0) {
            int model;
            String path = null;
            long id;
            if (data != null) {
                // Get the returned model value from EditActivity
                model = data.getIntExtra("model", -1);
                id = data.getLongExtra("id", 0);
                if (model == 1) {
                    //update selected note from database
                    String input = data.getStringExtra("content");
                    String time = data.getStringExtra("time");
                    if (data.getStringExtra("path") != null) {
                        path = data.getStringExtra("path");
                    }
                    NoteObj noteObj = new NoteObj(input, time, path);
                    noteObj.setId(id);
                    DAO dao = new DAO(context);
                    dao.Write();
                    dao.update(noteObj);
                    dao.close();
                } else if (model == 0) {
                    //add new note to the database
                    if (data.getStringExtra("path") != null) {
                        path = data.getStringExtra("path");
                    }
                    String input = data.getStringExtra("content");
                    String time = data.getStringExtra("time");
                    NoteObj noteObj = new NoteObj(input, time, path);
                    DAO dao = new DAO(context);
                    dao.Write();
                    dao.add(noteObj);
                    dao.close();
                } else if (model == 2) {
                    //delete selected note form database
                    NoteObj noteObj = new NoteObj();
                    noteObj.setId(id);
                    DAO dao = new DAO(context);
                    dao.Write();
                    dao.remove(noteObj);
                    dao.close();
                }
            }
        }
        //change listView
        refreshListView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.text) {
            NoteObj noteObj = (NoteObj) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(MainActivity.this, EditActivity.class);

            intent.putExtra("path", noteObj.getPath());
            intent.putExtra("content", noteObj.getContent());
            intent.putExtra("id", noteObj.getId());
            intent.putExtra("time", noteObj.getContent());
            intent.putExtra("model", 3);

            startActivityForResult(intent, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // used to determine whether the user deletes all notes
    public static class SureDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final MainActivity activity = (MainActivity) getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage("Are you sure ?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DAO dao = new DAO(activity);
                            dao.Write();
                            dao.removeAll();
                            dao.close();
                            activity.refreshListView();
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create();

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_clear){
            new SureDialog().show(getSupportFragmentManager(),null);
        }
        return super.onOptionsItemSelected(item);
    }

    // release application's RAM
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}