package org.newstand.datamigration;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.google.common.io.Closer;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.provider.SettingsProvider;

import java.io.Closeable;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataMigrationApp extends Application {

    private static final Closer sCloser = Closer.create();

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static void registerClosable(Closeable closeable) {
        sCloser.register(closeable);
    }

    public void cleanup() {
        Logger.d("Cleaning up");
        try {
            sCloser.close();
        } catch (IOException e) {
            Logger.e("Fail to close %s", e.getLocalizedMessage());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SettingsProvider.init(this);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        Logger.init(getClass().getSimpleName())
                .methodCount(3)
                .logLevel(LogLevel.FULL);
        Logger.d("DataMigrationApp comes up.");
    }
}
