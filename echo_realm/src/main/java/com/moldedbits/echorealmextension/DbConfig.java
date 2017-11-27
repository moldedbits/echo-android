package com.moldedbits.echorealmextension;

import android.content.Context;

import com.moldedbits.echo.chat.BaseExtensionConfig;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Author viveksingh
 * Date 16/06/17.
 */

public final class DbConfig implements BaseExtensionConfig{
    private static DbConfig instance;

    private RealmConfiguration realmConfiguration;
    private RealmMigration realmMigration;

    private DbConfig() {
    }

    public static DbConfig getInstance() {
        if (instance == null) {
            instance = new DbConfig();
        }
        return instance;
    }

    public RealmConfiguration getRealmConfiguration1() {
        return realmConfiguration;
    }

    private void setRealmConfiguration(RealmConfiguration realmConfiguration1) {
        this.realmConfiguration = realmConfiguration1;
    }

    public RealmMigration getRealmMigration() {
        return realmMigration;
    }

    private void setRealmMigration(RealmMigration realmMigration) {
        this.realmMigration = realmMigration;
    }

    @Override
    public void init(Context context) {
        Realm.init(context);
    }

    @Override
    public void init() {

    }

    public static class Builder {

        private RealmConfiguration configuration;
        private RealmMigration migration;

        private RealmConfiguration getConfiguration() {
            return configuration;
        }

        public Builder setConfiguration(RealmConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        private RealmMigration getMigration() {
            return migration;
        }

        public Builder setMigration(RealmMigration migration) {
            this.migration = migration;
            return this;
        }

        public DbConfig build() {
            DbConfig config = getInstance();
            config.setRealmConfiguration(this.getConfiguration());
            config.setRealmMigration(this.getMigration());
            return config;
        }
    }
}
