package com.trappedinauniverse.gold;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.trappedinauniverse.gold.database.Lyric;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class History extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Realm db = Realm.getDefaultInstance();

        RealmQuery<Lyric> query = db.where(Lyric.class);

        RealmResults<Lyric> results = query.findAll();

        Log.d("debug", "results: " + results.toString());

    }


}
