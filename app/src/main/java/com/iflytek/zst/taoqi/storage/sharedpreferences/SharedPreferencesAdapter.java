package com.iflytek.zst.taoqi.storage.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DELL-5490 on 2018/12/13.
 */

public abstract class SharedPreferencesAdapter {
    private final String TAG = getClass().getSimpleName();

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    protected SharedPreferencesAdapter(Context context) {
        mSharedPreferences = context.getSharedPreferences(getSharePreferencesName(), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    /**
     * @return SP存储的文件名
     */
    protected abstract String getSharePreferencesName();

    public void putLong(String key, long value) {
        mEditor.putLong(key, value).apply();
    }

    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    public void putInt(String key, int value) {
        mEditor.putInt(key, value).apply();
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public void putFloat(String key, float value) {
        mEditor.putFloat(key, value).apply();
    }

    public float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value).apply();
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public void clear() {
        mEditor.clear().apply();
    }

    public void remove(String key) {
        mEditor.remove(key).commit();
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

}
