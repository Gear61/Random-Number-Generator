package com.randomappsinc.randomnumbergeneratorplus.Persistence.Database;

import com.randomappsinc.randomnumbergeneratorplus.Utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.RealmSchema;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class DatabaseManager {
    private static final int CURRENT_DB_VERSION = 2;

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
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(MyApplication.getAppContext())
                .schemaVersion(CURRENT_DB_VERSION)
                .migration(getMigrationModule())
                .build();
        realm = Realm.getInstance(realmConfiguration);
    }

    private RealmMigration getMigrationModule() {
        return new RealmMigration() {
            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                RealmSchema schema = realm.getSchema();

                if (oldVersion == 0) {
                    schema.get("RNGConfiguration")
                            .addField("sortIndex", int.class)
                            .addField("showSum", boolean.class);
                    oldVersion++;
                }

                if (oldVersion == 1) {
                    schema.get("RNGConfiguration")
                            .addField("hideExcludes", boolean.class);
                }
            }
        };
    }

    public void addOrUpdateConfig(RNGConfiguration RNGConfiguration) {
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(RNGConfiguration);
            realm.commitTransaction();
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public String[] getAllConfigs() {
        RealmResults<RNGConfiguration> configs = realm.where(RNGConfiguration.class).findAll();
        configs.sort("configName");
        List<String> configNames = new ArrayList<>();
        for (RNGConfiguration rngConfiguration : configs) {
            configNames.add(rngConfiguration.getConfigName());
        }
        return configNames.toArray(new String[configNames.size()]);
    }

    public RNGConfiguration getConfig(String configName) {
        return realm.where(RNGConfiguration.class).equalTo("configName", configName).findFirst();
    }
}
