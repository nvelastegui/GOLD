package com.trappedinauniverse.gold;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.trappedinauniverse.gold.database.Lyric;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Lyrics extends Activity {

    /** Instance Variables **/
    private WebView webViewDescription;
    private WebView webViewLyrics;
    private JSONObject songData;
    private JSONObject songLyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getIntent(); // Trigger 'first' Intent get so onNewIntent() is called for ea widget button

        // Get the data from the calling activity. If empty, quit activity.
        try {
            songData = new JSONObject(getIntent().getStringExtra("songData"));
            getActionBar().setTitle(songData.getJSONObject("result").getString("title"));
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_loadingLyricsActivity), Toast.LENGTH_LONG).show();
            finish();
        }

        // Set references to view elements.
        webViewDescription = findViewById(R.id.activity_lyrics_descriptionWebView);
        webViewLyrics = findViewById(R.id.activity_lyrics_lyricsWebView);

        // Download song data and display on screen.
        DownloadSongDataAsyncTask dsd = new DownloadSongDataAsyncTask(this);
        dsd.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: If already saved, display 'saved' not 'save'
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lyrics, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finishAffinity();
        Intent pushToMain = new Intent(this, Main.class);
        pushToMain.setAction(intent.getAction());
        pushToMain.putExtras(intent.getExtras());
        startActivity(pushToMain);
    }

    /**
     * Called by 'save' menu item to save current song into history.
     * @param item Calling menu item.
     */
    public void saveCurrentSong(MenuItem item) {
        Realm db = Realm.getDefaultInstance();
        try {

            // Pull relevant details from song data.
            final int id = Integer.parseInt(songLyrics.getJSONObject("response").getJSONObject("song").getString("id"));
            final String description = songLyrics.getJSONObject("response").getJSONObject("song").getJSONObject("description").getString("html");
            final String lyricsEmbed = songLyrics.getJSONObject("response").getJSONObject("song").getString("embed_content");
            final String artist = songLyrics.getJSONObject("response").getJSONObject("song").getJSONObject("primary_artist").getString("name");
            final String title = songLyrics.getJSONObject("response").getJSONObject("song").getString("title");
            final String album = songLyrics.getJSONObject("response").getJSONObject("song").getJSONObject("album").getString("name");
            final String album_thumbnail = songLyrics.getJSONObject("response").getJSONObject("song").getString("header_image_thumbnail_url");
            final String fullJSON = songData.toString();

            // Create a new entry in the database and save.
            db.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // parameter changes in song are automatically reflected in database.
                    Lyric song = new Lyric();
                    song.id = id;
                    song.description = description;
                    song.lyricsEmbed = lyricsEmbed;
                    song.artist = artist;
                    song.title = title;
                    song.album = album;
                    song.album_thumbnail = album_thumbnail;
                    song.fullJSON = fullJSON;
                    realm.copyToRealmOrUpdate(song); // update older entry with same id if duplicate.
                }
            });

            // Notify user.
            Toast.makeText(this, getString(R.string.success_savedSong), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.d("debug", "e: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_savingSong), Toast.LENGTH_LONG).show();
        }
        finally {
            db.close();
        }
    }

    /**
     *
     */
    private class DownloadSongDataAsyncTask extends AsyncTask<String, Integer, String> {

        private Context context;

        DownloadSongDataAsyncTask (Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            // Send request and parse response to string.
            try {
                Request request = new Request.Builder()
                        .url("https://api.genius.com/songs/" + songData.getJSONObject("result").getString("id") + "?text_format=html")
                        .addHeader("Authorization", "Bearer " + context.getString(R.string.clientKey))
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            catch (Exception e) {
                // TODO: add better error-handling.
                return "";
            }
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected void onPostExecute(String s) {
            try {
                songLyrics = new JSONObject(s);

                // Set the description to display the HTML in the song data.
                webViewDescription.setWebViewClient(new WebViewClient());
                webViewDescription.getSettings().setJavaScriptEnabled(true);
                webViewDescription.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                StringBuilder htmlDescription = new StringBuilder(
                        "<html><head><meta charset=\"utf-8\"/><title>Description</title>"
                );
                htmlDescription.append(
                        "<style>\niframe {\ndisplay: none;\n}\n</style>"
                        // "<style>\niframe {\ndisplay: block;\nmax-width:100%;\n}\n</style>"
                );
                htmlDescription.append(
                        "</head><body bgcolor=\"#263238\">"
                );
                htmlDescription.append(
                        songLyrics.getJSONObject("response").getJSONObject("song").getJSONObject("description").getString("html")
                );
                htmlDescription.append("</body></html>");
                webViewDescription.loadData(htmlDescription.toString(), "text/html", null);


                // Set the description to display the HTML in the song data.
                webViewLyrics.setWebViewClient(new WebViewClient());
                webViewLyrics.setWebChromeClient(new WebChromeClient());
                webViewLyrics.getSettings().setJavaScriptEnabled(true);
                webViewLyrics.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                htmlDescription.delete(0, htmlDescription.length() - 1);
                htmlDescription.append(
                        "<html><head><meta charset=\"utf-8\"/><title>Lyrics</title></head><body>"
                );
                String embed_content = songLyrics.getJSONObject("response").getJSONObject("song").getString("embed_content");
                embed_content = embed_content.replace("//genius.com", "https://genius.com");
                htmlDescription.append(
                    embed_content
                );
                Log.d("debug", "embed_content: " + songLyrics.getJSONObject("response").getJSONObject("song").getString("embed_content"));
                htmlDescription.append("</body></html>");
                webViewLyrics.loadData(htmlDescription.toString(), "text/html", null);


            } catch (Exception e) {
                Log.d("debug", e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
