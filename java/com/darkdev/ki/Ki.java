package com.darkdev.ki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class Ki {
    public static final String COPYRIGHT_YEAR = "2021";
    public static final String COPYRIGHT_HOLDER = "Dark Tornado";
    public static final String NOTI_MAIN_CHANNEL = "ki.main.channel";
    public static final int NOTI_ID_MAIN_SERVICE = 1;
    public static final String PREFERENCES_NAME = "ki_settings";
    public static final int DEFAULT_ALPHA = 90;

    public static void saveData(Context ctx, String name, String value) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public static void saveSettings(Context ctx, String name, boolean value) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    public static void saveSettings(Context ctx, String name, int value) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    public static String readData(Context ctx, String name) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sp.getString(name, null);
    }


    public static boolean loadSettings(Context ctx, String name, boolean defaultSettings) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(name, defaultSettings);
    }

    public static int loadSettings(Context ctx, String name, int defaultSettings) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sp.getInt(name, defaultSettings);
    }

    public static AppData[] getAllApps(Context ctx) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pm = ctx.getPackageManager();
            List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
            AppData[] appList = new AppData[apps.size()];
            for (int n = 0; n < apps.size(); n++) {
                ResolveInfo pack = apps.get(n);
                appList[n] = new AppData(pack.loadLabel(pm).toString(), pack.activityInfo.applicationInfo.packageName);
            }
            return appList;
        } catch (Exception e) {
//            toast("getAllApps\n" + e.toString());
        }
        return null;
    }

}
