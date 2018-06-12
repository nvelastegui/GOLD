package com.trappedinauniverse.gold;

import android.app.Activity;
import android.content.Intent;
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
        getIntent(); // Trigger 'first' Intent get so onNewIntent() is called for ea widget button

        Realm db = Realm.getDefaultInstance();

        RealmQuery<Lyric> query = db.where(Lyric.class);

        RealmResults<Lyric> results = query.findAll();

        Log.d("debug", "results: " + results.toString());

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


}
