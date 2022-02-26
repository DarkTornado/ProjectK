package com.darkdev.ki;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class KakaoTalk {
    private Context ctx;
    private Notification.Action act;

    String room, msg, sender;
    boolean isGroupChat;

    public KakaoTalk(Context ctx, Bundle data, Notification.Action act) {
        this.ctx = ctx;
        this.act = act;
        sender = data.getString("android.title");
        msg = data.getString("android.text");
        room = data.getString("android.subText");
        if (room == null) room = data.getString("android.summaryText");
        isGroupChat = room != null;
        if (room == null) room = sender;
    }

    public void reply(String value) {
        Intent sendIntent = new Intent();
        Bundle msg = new Bundle();
        for (RemoteInput input : act.getRemoteInputs()) {
            msg.putCharSequence(input.getResultKey(), value);
        }
        RemoteInput.addResultsToIntent(act.getRemoteInputs(), sendIntent, msg);
        try {
            act.actionIntent.send(ctx, 0, sendIntent);
        } catch (PendingIntent.CanceledException e) {
        }
    }
}
