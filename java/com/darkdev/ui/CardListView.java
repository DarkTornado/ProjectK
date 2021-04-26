package com.darkdev.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

public class CardListView extends LinearLayout {

    private Context ctx;
    private LinearLayout layout;
    private int pad = dip2px(10);

    public CardListView(Context context) {
        super(context);
        ctx = context;
        setOrientation(1);
        int mar = dip2px(10);
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -2);
        margin.setMargins(mar, mar, mar, mar);
        setLayoutParams(margin);
        setBackgroundColor(Color.WHITE);
        ViewCompat.setElevation(this, dip2px(3));

        layout = new LinearLayout(ctx);
        layout.setOrientation(1);
        layout.setPadding(pad, pad, pad, pad);
        addView(layout);
    }

    public void setTitle(String title) {
        TextView txt = new TextView(ctx);
        txt.setText(title);
        txt.setTextColor(Color.WHITE);
        txt.setBackgroundColor(Color.parseColor("#4FC3F7"));
        txt.setTextSize(21);
        txt.setPadding(pad, pad, pad, pad);
        addView(txt, 0);
    }

    public void addSwitch(String text, int id, CompoundButton.OnCheckedChangeListener listener, boolean isChecked) {
        if (layout.getChildCount() > 0) addLine();
        Switch txt = new Switch(ctx);
        txt.setText(text);
        txt.setTextSize(20);
        txt.setTextColor(Color.BLACK);
        txt.setId(id);
        txt.setPadding(pad, pad, pad, pad);
        txt.setChecked(isChecked);
        txt.setOnCheckedChangeListener(listener);
        layout.addView(txt);
    }

    public void addText(String text, int id, View.OnClickListener listener) {
        if (layout.getChildCount() > 0) addLine();
        TextView txt = new TextView(ctx);
        txt.setText(text);
        txt.setTextSize(20);
        txt.setTextColor(Color.BLACK);
        txt.setId(id);
        txt.setPadding(pad, pad, pad, pad);
        txt.setOnClickListener(listener);
        layout.addView(txt);
    }

    private void addLine() {
        TextView txt = new TextView(ctx);
        txt.setWidth(-1);
        txt.setHeight(dip2px(1));
        txt.setBackgroundColor(Color.LTGRAY);
        layout.addView(txt);
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }
}
