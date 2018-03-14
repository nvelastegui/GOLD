package com.trappedinauniverse.gold.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Defines the 'Lyric' table.
 */
public class Lyric extends RealmObject {

    /**
     * id matches song ID returned from Genius API.
     */
    @PrimaryKey
    public int id;

    /**
     * HTML description of lyrics.
     */
    public String description;

    /**
     * HTML to embed lyrics into page. (Genius does not return raw lyrics.)
     */
    public String lyricsEmbed;

    public String artist;
    public String title;
    public String album;
    public String album_thumbnail;
    public String fullJSON;

}
