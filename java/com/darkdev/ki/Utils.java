package com.darkdev.ki;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

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
}
