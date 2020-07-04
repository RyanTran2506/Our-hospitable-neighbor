package com.example.ourhospitableneighbor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class History extends ListActivity {

    String[] jobHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] tempJobs = {"job 1" , "job 2"};

        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_history, R.id.jobHistoryList, tempJobs));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(this, position, Toast.LENGTH_SHORT).show();
    }
}