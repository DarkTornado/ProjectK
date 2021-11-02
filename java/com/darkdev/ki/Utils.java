package com.darkdev.ki;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.util.Pair;
import android.widget.Toast;

import com.darktornado.library.SimpleRequester;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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

    public static String getWeatherInfo(LocationSaver ls) {
        try {
            String data = Jsoup.connect(Ki.WEATHER_API_URL)
                    .header("Content-Type", "application/json")
                    .data("x", ls.lat + "")
                    .data("y", ls.lon + "")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .post().wholeText();
//            Toast.makeText(ctx, data, Toast.LENGTH_SHORT).show();
            return data;
        } catch (Exception e) {
//            Toast.makeText(ctx, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static String getBusId(Context ctx, String input) {
        try {
            String url = "https://m.map.kakao.com/actions/searchView?q=" + input.replace(" ", "%20") + "%20버스";
            Document data = Jsoup.connect(url).ignoreContentType(true).get();
            String busId = data.select("div.search_result_wrap").select("li").get(0).attr("data-id");
            if (busId.equals("")) return null;
            return busId;
        } catch (Exception e) {
            if (Ki.devModeEnabled) Toast.makeText(ctx, e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static Bitmap getImageFromWeb(String link) {
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            if (con != null) {
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                bis.close();
                return bitmap;
            }
        } catch (Exception e) {
            //toast(e.toString());
        }
        return null;
    }

    public static String getWebText(String link) {
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            if (con != null) {
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String str = br.readLine();
                String line = "";
                while ((line = br.readLine()) != null) {
                    str += "\n" + line;
                }
                br.close();
                isr.close();
                return str;
            }
        } catch (Exception e) {
            //toast(e.toString());
        }
        return null;
    }

    public static String findRoute(LocationSaver start, LocationSaver end, String dest) {
        try {
            return SimpleRequester.create("https://pt.map.naver.com/api/pubtrans-search")
                    .data("phase", "real")
                    .data("mode", "")
                    .data("departureTime", "")
                    .data("departure", URLEncoder.encode(URLEncoder.encode(start.lon + "," + start.lat + ",name=Start", "UTF-8"), "UTF-8"))
                    .data("arrival", URLEncoder.encode(URLEncoder.encode(end.lon + "," + end.lat + ",name=" + dest, "UTF-8"), "UTF-8"))
                    .execute().body;
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

}