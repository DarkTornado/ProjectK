package com.darkdev.ki;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darkdev.uilib.CardListView;

import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkPermission()) {
            setContentView(requestPermission());
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        CardListView ki = new CardListView(this);
        ki.setTitle("상시 대기 설정");
        ki.addSwitch("케이아이 활성화", 0, (view, isChecked) -> {
            Intent intent = new Intent(this, MainService.class);
            intent.putExtra("type", isChecked ? "start" : "stop");
            startService(intent);
            toast("케이아이가 " + (!isChecked ? "비" : "") + "활성화되었어요.");
            Ki.saveSettings(this, "ki_on", isChecked);
        }, Ki.loadSettings(this, "ki_on", false));
        ki.addText("버튼 불투명도 설정", 0, v -> inputAlpha());
        ki.addText("명령어 목록", 0, v -> {

        });
        layout.addView(ki);
        ki.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Ki.devModeEnabled = !Ki.devModeEnabled;
                toast("개발자모드: " + Ki.devModeEnabled);
                return true;
            }
        });

        CardListView si = new CardListView(this);
        si.setTitle("커스텀 AI 설정");
        si.addSwitch("커스텀 AI 활성화", 0, (view, isChecked) -> {
            toast("커스텀 AI가 " + (!isChecked ? "비" : "") + "활성화되었어요.");
            Ki.saveSettings(this, "ca_on", isChecked);
        }, Ki.loadSettings(this, "ca_on", false));
        si.addText("API 목록", 0, v -> {

        });
        layout.addView(si);

        CardListView misc = new CardListView(this);
        misc.setTitle("기타 기능 & 설정");
        misc.addText("검색 엔진 설정", 0, v -> searchEngineSettings());
        misc.addText("앱 정보", 0, v -> toast("test"));
        misc.addText("깃허브", 0, v -> {
            Uri uri = Uri.parse("https://github.com/DarkTornado/ProjectK");
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);
            toast("깃허브로 이동하고 있어요...");
        });
        misc.addText("라이선스", 0, v -> toast("test"));
        misc.addText("오픈 소스 라이선스", 0, v -> toast("test"));
        layout.addView(misc);

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
    }


    private void inputAlpha() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("버튼 불투명도 설정");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        final TextView txt = new TextView(this);
        txt.setText("불투명도 : " + Ki.loadSettings(this, "alpha", Ki.DEFAULT_ALPHA));
        txt.setTextSize(18);
        layout.addView(txt);

        final BitmapDrawable back = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        back.setAlpha(Ki.loadSettings(this, "alpha", Ki.DEFAULT_ALPHA));

        final SeekBar bar = new SeekBar(this);
        bar.setMax(255);
        bar.setProgress(Ki.loadSettings(this, "alpha", Ki.DEFAULT_ALPHA));
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt.setText("불투명도 : " + progress);
                back.setAlpha(progress);
                MainService.btn.setBackgroundDrawable(back);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        layout.addView(bar);

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);

        dialog.setView(scroll);
        dialog.setNegativeButton("취소", (_dialog, whick) -> {
            back.setAlpha(Ki.loadSettings(this, "alpha", Ki.DEFAULT_ALPHA));
            MainService.btn.setBackgroundDrawable(back);

        });
        dialog.setPositiveButton("확인", (_dialog, which) -> {
            int alpha = bar.getProgress();
            Ki.saveSettings(this, "alpha", alpha);
            toast("불투명도가 " + alpha + "(으)로 설정되었어요.");
        });
        dialog.show();
    }

    private void searchEngineSettings() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("검색 엔진 설정");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        TextView txt1 = new TextView(this);
        txt1.setText("이름 : ");
        txt1.setTextSize(18);
        layout.addView(txt1);
        final EditText txt2 = new EditText(this);
        txt2.setHint("이름 입력...");
        layout.addView(txt2);

        TextView txt3 = new TextView(this);
        txt3.setText("주소 : ");
        txt3.setTextSize(18);
        layout.addView(txt3);
        final EditText txt4 = new EditText(this);
        txt4.setHint("'KEY_WORD'를 검색한 내용 입력...");
        layout.addView(txt4);

        String _custom = Ki.readData(this, "search_engine");
        if (_custom != null) {
            String[] custom = _custom.split("\n");
            txt2.setText(custom[0]);
            txt4.setText(custom[1]);
        }

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        dialog.setView(scroll);
        dialog.setNegativeButton("취소", null);
        dialog.setPositiveButton("확인", (_dialog, which) -> {
            String name = txt2.getText().toString();
            String url = URLDecoder.decode(txt4.getText().toString());
            Ki.saveData(this, "search_engine", name + "\n" + url);
            toast("저장되었습니다.");
        });
        dialog.show();
    }


    private boolean checkPermission() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        for (String per : permissions) {
            if (ContextCompat.checkSelfPermission(this, per) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        if (!checkNotiPermission()) return false;
        if (Build.VERSION.SDK_INT < 23) return true;
        return Settings.canDrawOverlays(this);
    }

    private boolean checkNotiPermission() {
        String enl = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (enl == null) return false;
        return enl.contains(BuildConfig.APPLICATION_ID);
    }

    private ScrollView requestPermission() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        TextView txt1 = new TextView(this);
        txt1.setText(Html.fromHtml("&nbsp;<b>음성 녹음 권한</b>은 인터넷 권한과 함께 음성 인식에 사용되는거에요. 음성 인식을 위해서는 마이크를 통해 하는 말을 녹음해야겠지요?<br>" +
                "<br>&nbsp;<b>전화 권한</b>은 말 그대로 전화를 걸 때 필요한 권한이에요.<br>" +
                "<br>&nbsp;<b>연락처 접근 권한</b>은 사용자가 말한 이름을 연락처에서 찾아서, 그 사람의 전화번호를 알아내서 전화를 걸 때 필요한 권한이에요.<br>" +
                "<br>&nbsp;<b>위치 권한</b>은 길 찾기 기능이 필요로 하는 권한이에요.<br>" +
                "<br>&nbsp;아래 버튼을 눌러서 권한을 허용해주세요. 이미 권한이 허용되어 있는 경우에는 '권한 허용하기' 버튼을 눌렀을 때 아무것도 뜨지 않을거에요.<br>"));
        txt1.setTextSize(18);
        txt1.setTextColor(Color.BLACK);
        layout.addView(txt1);
        Button btn1 = new Button(this);
        btn1.setText("권한 허용하기");
        btn1.setTransformationMethod(null);
        btn1.setOnClickListener(v -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 5));
        layout.addView(btn1);

        if (Build.VERSION.SDK_INT >= 23) {
            TextView txt2 = new TextView(this);
            txt2.setText(Html.fromHtml("<br>&nbsp;<b>다른 앱 위에 그리기</b> 또는 <b>시스템 알림 표시</b>라고 불리는 권한은 화면에 버튼을 띄우기 위해서 필요한 기능이에요.<br>"));
            txt2.setTextSize(18);
            txt2.setTextColor(Color.BLACK);
            layout.addView(txt2);
            Button btn2 = new Button(this);
            btn2.setText("권한 허용하기");
            btn2.setTransformationMethod(null);
            btn2.setOnClickListener(v -> {
                Uri uri = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri);
                startActivityForResult(intent, 5469);
            });
            layout.addView(btn2);
        }

        TextView txt3 = new TextView(this);
        txt3.setText(Html.fromHtml("<br>&nbsp;<b>알림 접근 권한</b>은 상단바에 뜬 카톡 알림에 접근하여 누가 어디서 뭐라고 보냈는지 알아올 때 필요한 기능이에요.<br>"));
        txt3.setTextSize(18);
        txt3.setTextColor(Color.BLACK);
        layout.addView(txt3);
        Button btn4 = new Button(this);
        btn4.setText("권한 허용하기");
        btn4.setTransformationMethod(null);
        btn4.setOnClickListener(v -> {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        });
        layout.addView(btn4);

        TextView txt4 = new TextView(this);
        txt4.setText("\n권한 허용을 다 하셨다면 앱을 다시 시작해주세요.\n");
        txt4.setTextSize(18);
        txt4.setTextColor(Color.BLACK);
        layout.addView(txt4);

        Button restart = new Button(this);
        restart.setText("앱 재시작");
        restart.setTransformationMethod(null);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                toast("앱을 다시 시작하고 있어요...");
            }
        });
        layout.addView(restart);
        TextView maker = new TextView(this);
        maker.setText("\nⓒ " + Ki.COPYRIGHT_YEAR + " " + Ki.COPYRIGHT_HOLDER + ", All rights reserved.\n");
        maker.setTextSize(13);
        maker.setTextColor(Color.BLACK);
        maker.setGravity(Gravity.CENTER);
        layout.addView(maker);
        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        return scroll;
    }

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

}