package com.darkdev.ki;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.util.Pair;
import android.widget.Toast;

import java.util.List;

public class Utils {

    public static Notification.Builder createNotifation(Context ctx, String channel, String name) {
        if (Build.VERSION.SDK_INT < 26) return new Notification.Builder(ctx);
        NotificationChannel nc = new NotificationChannel(channel, name, NotificationManager.IMPORTANCE_LOW);
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.createNotificationChannel(nc);
        return new Notification.Builder(ctx, channel);
    }

    public static void vibrate(Context ctx, final int leng) {
        Vibrator vs = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            vs.vibrate(VibrationEffect.createOneShot(leng, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vs.vibrate(leng);
        }
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

    public static Pair<String, String>[] getAllContacts(Context ctx) {
        try {
            Cursor cursor = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            final Pair<String, String>[] result = new Pair[cursor.getCount()];
            cursor.moveToFirst();
            result[cursor.getPosition()] = new Pair(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            while (cursor.moveToNext()) {
                result[cursor.getPosition()] = new Pair(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
            return result;
        } catch (Exception e) {
//            toast("getAllContacts\n" + e.toString());
        }
        return null;
    }

    public static String getWeatherInfo(Context ctx, double[] pos) {
        try {
            String data = org.jsoup.Jsoup.connect(Ki.WEATHER_API_URL)
                    .header("Content-Type", "application/json")
                    .data("x", pos[0] + "")
                    .data("y", pos[1] + "")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .post().wholeText();
            Toast.makeText(ctx, data, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ctx, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
