package com.trappedinauniverse.gold.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trappedinauniverse.gold.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ListView adapter for displaying search results.
 */
public class SearchResultsListViewAdapter extends ArrayAdapter<JSONObject> {

    /** Instance Variables **/
    private Context context;
    private List<JSONObject> list;

    public SearchResultsListViewAdapter (Context context, List<JSONObject> list) {
        super(context, R.layout.listview_search_result, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_search_result, parent, false);
        }
        try {

            // Get the view elements in the row.
            TextView textViewSongTitle = convertView.findViewById(R.id.listview_search_result_songTitle);
            TextView textViewArtistName = convertView.findViewById(R.id.listview_search_result_artistName);
            WebView webViewThumbnail = convertView.findViewById(R.id.listview_search_result_webView);

            // Populate the title and artist.
            textViewSongTitle.setText(list.get(position).getJSONObject("result").getString("title"));
            textViewArtistName.setText(
                    list.get(position).getJSONObject("result").getJSONObject("primary_artist").getString("name")
            );

            // Set web view to display result thumbnail.
            webViewThumbnail.setWebViewClient(new WebViewClient());
            webViewThumbnail.getSettings().setLoadWithOverviewMode(true);
            webViewThumbnail.getSettings().setUseWideViewPort(true);
            webViewThumbnail.loadUrl(list.get(position).getJSONObject("result").getString("header_image_thumbnail_url"));

            // Set row tag
            convertView.setTag(list.get(position));

        } catch (Exception e) {
            ((TextView) convertView.findViewById(R.id.listview_search_result_songTitle)).setText("An error occurred.");
        }
        return convertView;
    }

}