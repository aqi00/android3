package com.example.chapter06.util;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class DatastoreUtil {
    private static DatastoreUtil instance; // 声明一个数据仓库工具的实例
    private RxDataStore<Preferences> mDataStore; // 声明一个数据仓库实例

    private DatastoreUtil(Context context) {
        mDataStore = new RxPreferenceDataStoreBuilder(context.getApplicationContext(), "datastore").build();
    }

    // 获取数据仓库工具的实例
    public static DatastoreUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DatastoreUtil(context);
        }
        return instance;
    }

    // 获取指定名称的字符串值
    public String getStringValue(String key) {
        Preferences.Key<String> keyId = PreferencesKeys.stringKey(key);
        Flowable<String> flow = mDataStore.data().map(prefs -> prefs.get(keyId));
        try {
            return flow.blockingFirst();
        } catch (Exception e) {
            return "";
        }
    }

    // 设置指定名称的字符串值
    public void setStringValue(String key, String value) {
        Preferences.Key<String> keyId = PreferencesKeys.stringKey(key);
        Single<Preferences> result = mDataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePrefs = prefs.toMutablePreferences();
            //String oldValue = prefs.get(keyId);
            mutablePrefs.set(keyId, value);
            return Single.just(mutablePrefs);
        });
    }

    // 获取指定名称的整型数
    public Integer getIntValue(String key) {
        Preferences.Key<Integer> keyId = PreferencesKeys.intKey(key);
        Flowable<Integer> flow = mDataStore.data().map(prefs -> prefs.get(keyId));
        try {
            return flow.blockingFirst();
        } catch (Exception e) {
            return 0;
        }
    }

    // 设置指定名称的整型数
    public void setIntValue(String key, Integer value) {
        Preferences.Key<Integer> keyId = PreferencesKeys.intKey(key);
        Single<Preferences> result = mDataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePrefs = prefs.toMutablePreferences();
            //Integer oldValue = prefs.get(keyId);
            mutablePrefs.set(keyId, value);
            return Single.just(mutablePrefs);
        });
    }

    // 获取指定名称的双精度数
    public Double getDoubleValue(String key) {
        Preferences.Key<Double> keyId = PreferencesKeys.doubleKey(key);
        Flowable<Double> flow = mDataStore.data().map(prefs -> prefs.get(keyId));
        try {
            return flow.blockingFirst();
        } catch (Exception e) {
            return 0.0;
        }
    }

    // 设置指定名称的双精度数
    public void setDoubleValue(String key, Double value) {
        Preferences.Key<Double> keyId = PreferencesKeys.doubleKey(key);
        Single<Preferences> result = mDataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePrefs = prefs.toMutablePreferences();
            //Double oldValue = prefs.get(keyId);
            mutablePrefs.set(keyId, value);
            return Single.just(mutablePrefs);
        });
    }

    // 获取指定名称的布尔值
    public Boolean getBooleanValue(String key) {
        Preferences.Key<Boolean> keyId = PreferencesKeys.booleanKey(key);
        Flowable<Boolean> flow = mDataStore.data().map(prefs -> prefs.get(keyId));
        try {
            return flow.blockingFirst();
        } catch (Exception e) {
            return false;
        }
    }

    // 设置指定名称的布尔值
    public void setBooleanValue(String key, Boolean value) {
        Preferences.Key<Boolean> keyId = PreferencesKeys.booleanKey(key);
        Single<Preferences> result = mDataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePrefs = prefs.toMutablePreferences();
            //Boolean oldValue = prefs.get(keyId);
            mutablePrefs.set(keyId, value);
            return Single.just(mutablePrefs);
        });
    }

}
