package com.darkdev.ki;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
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

import com.darkdev.ai.CustomAI;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.Nullable;

public class MainService extends Service {

    public static TextToSpeech tts;
    public static CustomAI ai;

    private Handler handler;
    static Button btn;
    private AppData[] appList;
    private LocationSaver ls;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        prepareService();
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prepareService();
        if (intent.getBooleanExtra("auto_started", false)) {
            toast("자동 실행됨");
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        ai = new CustomAI(this);
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
            String[] cmd = msg.split(" ");
            String data = msg.replaceFirst(cmd[0] + " ", "");
            String data2 = "";
            if (cmd.length > 1) data2 = msg.replaceFirst(cmd[0] + " " + cmd[1] + " ", "");

            boolean called = false;

            /* 설치된 앱 실행 */
            if (msg.contains("실행") || msg.contains("켜") || msg.contains("키라고")) {
                try {
                    for (AppData app : appList) {
                        if (msg.replace(" ", "").contains(app.name.replace(" ", ""))) {
                            PackageManager pm = getPackageManager();
                            startActivity(pm.getLaunchIntentForPackage(app.pack));
                            say("" + app.name + " 실행중...");
                            called = true;
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
                    say("연락처 목록을 불러오지 못했어요.");
                } else {
                    boolean called2 = false;
                    for (Pair<String, String> contact : contacts) {
                        if (contact.first.equals(name.trim())) {
                            Uri uri = Uri.parse("tel:" + contact.second);
                            Intent intent = new Intent(Intent.ACTION_CALL, uri);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            say("" + name + "에게 전화를 걸고 있어요.");
                            called2 = true;
                            break;
                        }
                    }
                    if (!called2) say("" + name + "(이)라는 이름으로 저장된 전화번호가 없어요.");
                }
                called = true;
            }

            /* 검색 */
            if (cmd[1].equals("검색")) {
                String _custom = Ki.readData(this, "search_engine");
                String[] custom;
                if (_custom == null) custom = new String[]{"", "", ""};
                else custom = _custom.split("\n");
                String url;
                switch (cmd[0]) {
                    case "네이버":
                        url = "https://m.search.naver.com/search.naver?query=" + data2;
                        break;
                    case "구글":
                    case "Google":
                        url = "https://www.google.com/search?q=" + data2;
                        break;
                    case "다음":
                        url = "https://search.daum.net/search?q=" + data2;
                        break;
                    default:
                        if (cmd[0].equals(custom[0])) {
                            url = custom[1].replace("KEY_WORD", data2);
                        } else {
                            url = "https://m.search.naver.com/search.naver?query=" + data2;
                        }
                        break;
                }
                Intent intent = new Intent(this, WebActivity.class);
                intent.setData(Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                say("검색 결과를 띄우고 있어요.");
                called = true;
            }

            /* 길찾기 */
            if (cmd[0].equals("길찾기")) {
                if (Ki.devModeEnabled) toast(ls.loc + "\n" + ls.lat + ", " + ls.lon);
                LocationSaver dest = LocationSaver.createWithAddress(this, data);
                if (dest == null) {
                    say("목적지를 찾을 수 없어요.");
                } else {
                    String url = "https://m.map.naver.com/directions/#/publicTransit/list/" +
                            "현재%20위치," + ls.lon + "," + ls.lat + "," + ls.lon + "," + ls.lat + ",false,/" +
                            "" + data + "," + dest.lon + "," + dest.lat + "," + dest.lon + "," + dest.lat + ",false,/0";
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.setData(Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    say("길찾기 결과를 띄우고 있어요.");
                }
                called = true;
            }

            /* 날씨 */
            if (cmd[0].equals("날씨")) {
                LocationSaver location = LocationSaver.createWithAddress(this, data);
                if (location == null) {
                    say("해당 지역을 찾을 수 없어요.");
                } else {
                    new Thread(() -> {
                        String result = Utils.getWeatherInfo(location);
                        if (result == null) {
                            say("날씨 정보를 불러오지 못했어요.");
                        } else {
                            Intent intent = new Intent(this, WeatherActivity.class);
                            intent.putExtra("pos", data);
                            intent.putExtra("loc", location.loc);
                            intent.putExtra("data", result);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            say("날씨 정보를 불러오고 있어요.");
                        }
                    }).start();
                }
                called = true;
            }

            /* 블루투스 */
            if (cmd[0].equals("블루투스")) {
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                if (btAdapter == null) {
                    say("이 기기는 블루투스를 지원하지 않는 기기 같아요.");
                    return;
                }
                boolean btOn = btAdapter.isEnabled();
                if (data.equals("설정")) {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    say("블루투스 설정창으로 이동하고 있어요");
                } else if (data.equals("꺼")) {
                    if (btOn) {
                        btAdapter.disable();
                        say("블루투스를 껐어요.");
                    } else {
                        say("이미 블루투스가 꺼진 상태에요.");
                    }
                } else {
                    if (!btOn) {
                        btAdapter.enable();
                        say("블루투스를 켰어요.");
                    } else {
                        say("이미 블루투스가 켜진 상태에요.");
                    }
                }
                called = true;
            }

            /* 버스 */
            if (cmd[0].equals("버스")) {
                new Thread(() -> {
                    String busId = Utils.getBusId(this, data);
                    if (busId == null) {
                        say("해당 버스를 찾기 못했어요.");
                    } else {
                        Intent intent = new Intent(this, BusActivity.class);
                        intent.putExtra("bus", data);
                        intent.putExtra("busId", busId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        say("버스 운행 정보를 불러오고 있어요.");
                    }
                }).start();
                called = true;
            }

            /* 전철 노선도 */
            if (cmd[0].equals("노선도") || cmd[1].equals("노선도")) {
                Intent intent = new Intent(this, SubwayActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                say("노선도를 띄우고 있어요.");
                called = true;
            }

            /* 카카오톡 */
            if (cmd[0].equals("카톡") || cmd[0].equals("카카오톡")) {
                KakaoTalk chat = KakaoTalkListener.chat;
                if (data.equals("읽어 줘")) {
                    if (chat == null) {
                        say("케이아이가 실행된 이후에 수신된 카카오톡 메시지가 없어요.");
                    } else {
                        if (chat.isGroupChat) say(chat.sender + "(이)가 " + chat.msg + "(이)라고 보냈어요.");
                        say(chat.room + "에서 " + chat.sender + "(이)가 " + chat.msg + "(이)라고 보냈어요.");
                    }
                } else if (cmd[1].equals("답장")) {
                    chat.reply(data2);
                    say(chat.room + "(으)로 " + data2 + "(이)라고 답장을 보냈어요");
                }
                called = true;
            }
            
            /* 맛집 */
            if (cmd[0].equals("맛집")) {
                say("맛집 정보를 불러오고 있어요");
                Intent intent = new Intent(this, FoodActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("input", data);
                startActivity(intent);
                called = true;
            }

            /* 커스텀 AI */
            if(Ki.loadSettings(this, "ca_on", false)) {
                ai.callResponse(msg, called);
            }

        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void say(String msg) {
        toast("[Ki] " + msg);
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        removeButton();
    }

    private void toast(final String msg) {
        runOnUiThread(() -> Toast.makeText(MainService.this, msg, Toast.LENGTH_SHORT).show());
    }

    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

}
