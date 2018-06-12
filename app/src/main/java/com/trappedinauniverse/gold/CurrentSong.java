package com.trappedinauniverse.gold;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 * Update period is specified to daily. Widget self-updates whenever music change is detected.
 */
public class CurrentSong extends AppWidgetProvider {

    /**
     * Called when the widget is first placed and any time it's resized. Automatically triggers
     * update (to define button actions).
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     * @param newOptions
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        // Set currently-playing-song information to no-song-playing.
        SharedPreferences currentSongInformation = context.getSharedPreferences("CurrentSongInformation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorCurrentSongInformation = currentSongInformation.edit();
        editorCurrentSongInformation.putString("title", null);
        editorCurrentSongInformation.putString("artist", null);
        editorCurrentSongInformation.apply();

        // Do initial set up of UI (ie. set buttons and text).
        int[] appWidgetIds = new int[appWidgetId];
        onUpdate(context, appWidgetManager, appWidgetIds);

    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Inflate app widget layout.
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.current_song);

        // Get current-song information from preferences.
        SharedPreferences currentSongInformation = context.getSharedPreferences("CurrentSongInformation", Context.MODE_PRIVATE);
        String title = currentSongInformation.getString("title", null);
        String artist = currentSongInformation.getString("artist", null);

        // If there's no music playing, set buttons to do nothing and set them invisible.
        if (title == null || artist == null) {

            views.setOnClickPendingIntent(R.id.widget_current_song_button_search_artist, null);
            views.setViewVisibility(R.id.widget_current_song_button_search_artist, View.INVISIBLE);

            views.setOnClickPendingIntent(R.id.widget_current_song_button_view_lyrics, null);
            views.setViewVisibility(R.id.widget_current_song_button_view_lyrics, View.INVISIBLE);

            views.setOnClickPendingIntent(R.id.widget_current_song_button_save_song, null);
            views.setViewVisibility(R.id.widget_current_song_button_save_song, View.INVISIBLE);

        }
        // Otherwise set buttons to open app accordingly.
        else {

            // Add intent for Button: Search Artist
            Intent intentSearchArtist = new Intent(context, Main.class);
            intentSearchArtist.setAction("search-artist"); // SETTING ACTION DISTINGUISHES INTENTS
            intentSearchArtist.putExtra("artist", artist);
            intentSearchArtist.putExtra("title", title);
            PendingIntent pendingIntentSearchArtist = PendingIntent.getActivity(
                    context,
                    0,
                    intentSearchArtist,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            views.setOnClickPendingIntent(R.id.widget_current_song_button_search_artist, pendingIntentSearchArtist);
            views.setViewVisibility(R.id.widget_current_song_button_search_artist, View.VISIBLE);

            // Add intent for Button: View Lyrics
            Intent intentViewLyrics = new Intent(context, Main.class);
            intentViewLyrics.setAction("view-lyrics");
            intentViewLyrics.putExtra("artist", artist);
            intentViewLyrics.putExtra("title", title);
            PendingIntent pendingIntentViewLyrics = PendingIntent.getActivity(
                    context,
                    0,
                    intentViewLyrics,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            views.setOnClickPendingIntent(R.id.widget_current_song_button_view_lyrics, pendingIntentViewLyrics);
            views.setViewVisibility(R.id.widget_current_song_button_view_lyrics, View.VISIBLE);

            // Add intent for Button: Save Song
            Intent intentSaveSong = new Intent(context, Main.class);
            intentSaveSong.setAction("save-song"); // SETTING ACTION DISTINGUISHES INTENTS
            intentSaveSong.putExtra("artist", artist);
            intentSaveSong.putExtra("title", title);
            PendingIntent pendingIntentSaveSong = PendingIntent.getActivity(
                    context,
                    0,
                    intentSaveSong,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            views.setOnClickPendingIntent(R.id.widget_current_song_button_save_song, pendingIntentSaveSong);
            views.setViewVisibility(R.id.widget_current_song_button_save_song, View.VISIBLE);

        }

        //
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Implemented for receiving updates on currently playing music.
     * - Called for every broadcast and before every other method in this class.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Get relevant parameters from the broadcast.
        boolean playing = intent.getBooleanExtra("playing", false);
        String artist = intent.getStringExtra("artist");
        String track = intent.getStringExtra("track");

        // If the broadcast says no music is playing, update the song information to null.
        if (!playing) {
             Toast.makeText(context, "music not playing", Toast.LENGTH_LONG).show();

            RemoteViews control = new RemoteViews(context.getPackageName(), R.layout.current_song);
            control.setTextViewText(R.id.widget_current_song_artist, "");
            control.setTextViewText(R.id.widget_current_song_title, "No Music Playing");

            // Update currently playing songs in preferences.
            SharedPreferences currentSongInformation = context.getSharedPreferences("CurrentSongInformation", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorCurrentSongInformation = currentSongInformation.edit();
            editorCurrentSongInformation.putString("title", null);
            editorCurrentSongInformation.putString("artist", null);
            editorCurrentSongInformation.apply();

            ComponentName cn = new ComponentName(context, CurrentSong.class);
            AppWidgetManager.getInstance(context).updateAppWidget(cn,
                    control);

        }
        // If the broadcast says music is playing, update the song information to match.
        else {
            Toast.makeText(context, "music playing: " + track + " by " + artist, Toast.LENGTH_LONG).show();

            RemoteViews control = new RemoteViews(context.getPackageName(), R.layout.current_song);
            control.setTextViewText(R.id.widget_current_song_artist, "by " + artist);
            control.setTextViewText(R.id.widget_current_song_title, track);

            // Update currently playing songs in preferences.
            SharedPreferences currentSongInformation = context.getSharedPreferences("CurrentSongInformation", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorCurrentSongInformation = currentSongInformation.edit();
            editorCurrentSongInformation.putString("title", track);
            editorCurrentSongInformation.putString("artist", artist);
            editorCurrentSongInformation.apply();

            ComponentName cn = new ComponentName(context, CurrentSong.class);
            AppWidgetManager.getInstance(context).updateAppWidget(cn,
                    control);


        }

        // Call onUpdate() on Widget.
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), CurrentSong.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);

    }
}

