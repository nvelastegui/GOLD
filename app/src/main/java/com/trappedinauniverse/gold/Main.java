package com.trappedinauniverse.gold;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.trappedinauniverse.gold.adapter.SearchResultsListViewAdapter;
import com.trappedinauniverse.gold.request.PerformSearchTask;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Main extends Activity {

    /** Views **/
    private EditText searchBox;
    private Button searchButton;

    private ListView listview;
    private SearchResultsListViewAdapter listviewAdapter;
    private List<JSONObject> listviewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set references to view elements.
        searchBox = findViewById(R.id.activity_main_searchBox);
        searchButton = findViewById(R.id.acitvity_main_searchButton);

        // Set reference to listview and set up adapter / data list.
        listview = findViewById(R.id.activity_main_searchResults);
        listviewData = new ArrayList<>();
        listviewAdapter = new SearchResultsListViewAdapter(this, listviewData);

        listview.setAdapter(listviewAdapter);

//        final Context context = this;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Intent intent = new Intent(Main.this, Lyrics.class);
                    intent.putExtra("songData", ((JSONObject) view.getTag()).toString());
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(Main.this, getString(R.string.error_launchingLyrics), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Run search for entry in search box.
     * @param button Calling button.
     */
    public void doSearch (View button) {
        (new PerformSearchTask(this, listviewAdapter, listviewData, searchBox.getText().toString())).execute();
    }

    /**
     * Open the history view.
     * @param button
     */
    public void openSaved (View button) {
        startActivity(new Intent(this, History.class));
    }


}
