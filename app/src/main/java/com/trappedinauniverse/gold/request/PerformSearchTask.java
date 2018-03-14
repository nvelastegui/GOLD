package com.trappedinauniverse.gold.request;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.trappedinauniverse.gold.R;
import com.trappedinauniverse.gold.adapter.SearchResultsListViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * AsyncTask for performing a search and displaying results in a listview.
 */
public class PerformSearchTask extends AsyncTask<String, Integer, String> {

    /** Instance Variables **/
    private Context context;
    private String searchTerm;
    private SearchResultsListViewAdapter adapter;
    private List<JSONObject> listviewData;

    /**
     * Constructor.
     * @param context Calling activity.
     * @param searchTerm Search text.
     */
    public PerformSearchTask (Context context, SearchResultsListViewAdapter adapter, List<JSONObject> listviewData, String searchTerm) {
        this.context = context;
        this.adapter = adapter;
        this.listviewData = listviewData;
        this.searchTerm = searchTerm;
    }

    /**
     * Empty constructor hidden.
     */
    private PerformSearchTask () {}

    @Override
    protected String doInBackground(String... terms) {

        // Create search Request. Attach client key to header.
        Request request = new Request.Builder()
                .url("https://api.genius.com/search?q=" + searchTerm)
                .addHeader("Authorization", "Bearer " + context.getString(R.string.clientKey))
                .build();

        // Send request and parse response to string.
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception e) {
            // TODO: add better error-handling.
            return "";
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {

            // Separate response into 'meta' and 'response'
            JSONObject searchReturn = new JSONObject(s);
            JSONArray searchResults = ((JSONObject) searchReturn.get("response")).getJSONArray("hits");

            // Parse the search results in 'response' into listview's data.
            listviewData.clear();
            for (int i = 0 ; i < searchResults.length() ; i++) {
                listviewData.add((JSONObject) searchResults.get(i));
            }

            // Update listview adapter (refresh view).
            adapter.notifyDataSetChanged();

        } catch (Exception e) {

            // If something goes wrong above, clear the list view.
            adapter.clear();

            // And pray for now cause no error-handling.
            Log.d("debug","e: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(context, "Well, you failed.", Toast.LENGTH_LONG).show();
        }

    }


}
