package com.bebeep.wisdompb.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.wisdompb.MyApplication;


/**
 * Created by Bebeep
 * Time 2018/4/25 10:38
 * Email 424468648@qq.com
 * Tips
 */

public class PreferenceUtils {

    public static String getPrefString(final String key, final String defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        return settings.getString(key, defaultValue);
    }

    public static void setPrefString(final String key, final String value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        settings.edit().putString(key, value).commit();
    }

    public static boolean getPrefBoolean(final String key, final boolean defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        return settings.getBoolean(key, defaultValue);
    }


    public static void setPrefBoolean(final String key, final boolean value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        settings.edit().putBoolean(key, value).commit();
    }

    public static void setPrefInt(final String key, final int value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        settings.edit().putInt(key, value).commit();
    }

    public static int getPrefInt(final String key, final int defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        return settings.getInt(key, defaultValue);
    }

    public static void setPrefFloat(final String key, final float value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        settings.edit().putFloat(key, value).commit();
    }

    public static float getPrefFloat(final String key, final float defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        return settings.getFloat(key, defaultValue);
    }

    public static void setSettingLong(final String key, final long value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        settings.edit().putLong(key, value).commit();
    }

    public static long getPrefLong(final String key, final long defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        return settings.getLong(key, defaultValue);
    }

    public static void clearPreference() {
        final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        final SharedPreferences.Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }


    public static void clearPreferenceByName(Context context,String name) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(name, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.clear().commit();
    }
}
