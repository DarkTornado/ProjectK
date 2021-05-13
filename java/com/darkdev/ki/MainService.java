package com.darkdev.ki;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.service.notification.NotificationListenerService;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainService extends NotificationListenerService {

    private TextToSpeech tts;
    private Handler handler;
    static Button btn;
    private AppData[] appList;
    private LocationSaver ls;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        if (Ki.loadSettings(this, "ki_on", false)) {
            prepareService();
        }
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String type = intent.getStringExtra("type");
        if (type.equals("start")) {
            prepareService();
        } else if (type.equals("stop")) {
            stopForeground(true);
            removeButton();
        }
        return START_NOT_STICKY;
    }

    private void prepareService() {
        Notification.Builder noti = Utils.createNotifation(this, Ki.NOTI_MAIN_CHANNEL, "Ki Service");
        noti.setSmallIcon(R.mipmap.ic_launcher);
        noti.setContentTitle("Project Ki");
        noti.setContentText("케이아이가 실행중이에요...");
        noti.setAutoCancel(true);
        noti.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        startForeground(Ki.NOTI_ID_MAIN_SERVICE, noti.build());
        tts = new TextToSpeech(this, status -> tts.setLanguage(Locale.KOREAN));
        createButton();
        appList = Utils.getAllApps(this);
        ls = new LocationSaver(this);
    }

    private void createButton() {
        try {
            final WindowManager mManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            if (btn != null) mManager.removeView(btn);

            final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                    dip2px(48), dip2px(48),
                    Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            btn = new Button(this);
            btn.setText(" ");
            BitmapDrawable back = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            back.setAlpha(Ki.loadSettings(this, "alpha", Ki.DEFAULT_ALPHA));
            btn.setBackgroundDrawable(back);
            btn.setOnClickListener(v -> {
                inputVoice();
                Utils.vibrate(this, 50);
            });
            final boolean[] longClick = {false};
            btn.setOnTouchListener((v, ev) -> {
                if (longClick[0]) {
                    switch (ev.getAction()) {
                        case MotionEvent.ACTION_UP:
                            longClick[0] = false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            mParams.x = (int) ev.getRawX() - dip2px(24);
                            mParams.y = (int) ev.getRawY() - dip2px(24);
                            mParams.gravity = Gravity.LEFT | Gravity.TOP;
                            mManager.updateViewLayout(btn, mParams);
                            break;
                    }
                } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    new Handler().postDelayed(() -> {
                        if (!longClick[0]) longClick[0] = true;
                    }, 100);
                }
                return false;
            });

            mManager.addView(btn, mParams);

        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void removeButton() {
        WindowManager mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager.removeView(btn);
        btn = null;
    }

    public void inputVoice() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            final SpeechRecognizer stt = SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    toast("Input Ready");
                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {
                    toast("Input End");
                }

                @Override
                public void onError(int error) {
                    try {
                        toast("Error Code : " + error);
                        stt.destroy();
                    } catch (Exception e) {
                        toast("OnError\n" + e.toString());
                    }
                }

                @Override
                public void onResults(Bundle results) {
                    final ArrayList<String> result = (ArrayList<String>) results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    final String que = (String) result.get(0);
                    stt.destroy();
                    toast("입력: " + que);
                    new Handler().postDelayed(() -> response(que), 500);
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            stt.startListening(intent);
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void response(String msg) {
        try {
            if (msg.startsWith("길 찾기")) msg = msg.replaceFirst("길 찾기", "길찾기");
            String cmd = msg.split(" ")[0];
            String data = msg.replaceFirst(cmd + " ", "");

            /* 설치된 앱 실행 */
            if (msg.contains("실행") || msg.contains("켜") || msg.contains("키라고")) {
                try {
                    for (AppData app : appList) {
                        if (msg.replace(" ", "").contains(app.name.replace(" ", ""))) {
                            PackageManager pm = getPackageManager();
                            startActivity(pm.getLaunchIntentForPackage(app.pack));
                            toast("[Ki] " + app.name + " 실행중...");
                        }
                    }
                } catch (Exception e) {
                    toast(e.toString());
                }
            }

            /* 전화 걸기 */
            if (msg.contains("한테 전화") || msg.contains("에게 전화")) {
                String name;
                if (msg.contains("한테 전화")) name = msg.split("한테 전화")[0];
                else name = msg.split("에게 전화")[0];
                Pair<String, String>[] contacts = Utils.getAllContacts(this);
                if (contacts == null) {
                    toast("[Ki] 연락처 목록을 불러오지 못했어요.");
                } else {
                    boolean called = false;
                    for (Pair<String, String> contact : contacts) {
                        if (contact.first.equals(name.trim())) {
                            Uri uri = Uri.parse("tel:" + contact.second);
                            Intent intent = new Intent(Intent.ACTION_CALL, uri);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            toast("[Ki] " + name + "에게 전화를 걸고 있어요.");
                            called = true;
                            break;
                        }
                    }
                    if (!called) toast("[Ki] " + name + "(이)라는 이름으로 저장된 전화번호가 없어요.");
                }
            }

            /* 검색 */
            if (cmd.equals("검색")) {
                Intent intent = new Intent(this, WebActivity.class);
                intent.setData(Uri.parse("https://m.search.naver.com/search.naver?query=" + data));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                toast("[Ki] 검색 결과를 띄우고 있어요.");
            }

            /* 길찾기 */
            if (cmd.equals("길찾기")) {
                if (Ki.devModeEnabled) toast(ls.loc + "\n" + ls.lat + ", " + ls.lon);
                double[] destination = LocationSaver.addr2pos(this, data);
                if (destination == null) {
                    toast("목적지를 찾을 수 없어요.");
                } else {
                    String url = "https://m.map.naver.com/directions/#/publicTransit/list/" +
                            "현재%20위치," + ls.lon + "," + ls.lat + "," + ls.lon + "," + ls.lat + ",false,/" +
                            "" + data + "," + destination[1] + "," + destination[0] + "," + destination[1] + "," + destination[0] + ",false,/0";
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.setData(Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    toast("[Ki] 길찾기 결과를 띄우고 있어요.");
                }
            }

            /* 날씨 */
            if (cmd.equals("날씨")) {
                StrictMode.enableDefaults();
                double[] location = LocationSaver.addr2pos(this, data);
                if (location == null) {
                    toast("해당 지역을 찾을 수 없어요.");
                } else {
                    String result = Utils.getWeatherInfo(this, location);
                    if (result == null) {
                        toast("[Ki] 날씨 정보를 불러오지 못했어요.");
                    } else {
                        Intent intent = new Intent(this, WeatherActivity.class);
                        intent.putExtra("data", result);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        toast("[Ki] 날씨 정보를 불러오고 있어요.");
                    }
                }
            }


        } catch (Exception e) {
            toast(e.toString());
        }
    }


    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainService.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

}
