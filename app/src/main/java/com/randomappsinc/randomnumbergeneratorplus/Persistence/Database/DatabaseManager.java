package com.randomappsinc.randomnumbergeneratorplus.Persistence.Database;

import android.content.Context;

import com.randomappsinc.randomnumbergeneratorplus.Utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class DatabaseManager {
    private static DatabaseManager instance;

    public static DatabaseManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized DatabaseManager getSync() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private Realm realm;

    private DatabaseManager() {
        Context context = MyApplication.getAppContext();
        realm = Realm.getInstance(context);
    }

    public void addOrUpdateConfig(RNGConfiguration RNGConfiguration) {
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(RNGConfiguration);
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public void renameSet(String oldName, String newName) {
        try {
            realm.beginTransaction();
            RNGConfiguration oldConfig = realm.where(RNGConfiguration.class)
                    .equalTo("configName", oldName)
                    .findFirst();
            oldConfig.setConfigName(newName);
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public boolean doesConfigExist(String configName) {
        return realm.where(RNGConfiguration.class).equalTo("configName", configName).findFirst() != null;
    }

    public void deleteConfig(String configName) {
        try {
            realm.beginTransaction();
            RNGConfiguration config = realm.where(RNGConfiguration.class).equalTo("configName", configName).findFirst();
            config.removeFromRealm();
            realm.commitTransaction();
        }
        catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public String[] getAllConfigs() {
        List<RNGConfiguration> configs = realm.where(RNGConfiguration.class).findAll();
        List<String> configNames = new ArrayList<>();
        for (RNGConfiguration rngConfiguration : configs) {
            configNames.add(rngConfiguration.getConfigName());
        }
        return configNames.toArray(new String[configNames.size()]);
    }
}

