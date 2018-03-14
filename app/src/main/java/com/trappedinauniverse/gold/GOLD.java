package com.trappedinauniverse.gold;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 *
 */
public class GOLD extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Realm
        Realm.init(this);

        // Create a default configuration.
        RealmConfiguration config = new RealmConfiguration.Builder().name("GOLD.realm").build();
        Realm.setDefaultConfiguration(config);
    }
}
