package com.example.ourhospitableneighbor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class History extends ListActivity {

    String[] postHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] tempPosts = {"post 1" , "post 2"};

        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_history, R.id.postHistoryList, tempPosts));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(this, position, Toast.LENGTH_SHORT).show();
    }
}