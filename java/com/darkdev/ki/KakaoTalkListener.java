package com.darkdev.ki;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class KakaoTalkListener  extends NotificationListenerService {

    static KakaoTalk chat;

    @Override
    public void onCreate() {
        if (Ki.loadSettings(this, "ki_on", false)) {
            Intent intent = new Intent(this, MainService.class);
            intent.putExtra("auto_started", true);
            intent.putExtra("start", true);
            startService(intent);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (sbn.getPackageName().equals("com.kakao.talk")) {
            Notification.WearableExtender wExt = new Notification.WearableExtender(sbn.getNotification());
            for (Notification.Action act : wExt.getActions()) {
                if (act.getRemoteInputs() != null && act.getRemoteInputs().length > 0) {
                    if (act.title.toString().toLowerCase().contains("reply") ||
                            act.title.toString().toLowerCase().contains("답장")) {
                        Bundle data = sbn.getNotification().extras;
                        chat = new KakaoTalk(this, data, act);
                    }
                }
            }
        }
    }

}
