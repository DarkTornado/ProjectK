package com.darkdev.uilib;

/*
BottomNavigationLayout
© 2020 Dark Tornado, All rights reserved.

MIT License

Copyright (c) 2020 Dark Tornado

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class BottomNavigationLayout extends FrameLayout {

    private Context ctx;
    private LinearLayout layout;
    private LinearLayout bottom;

    public BottomNavigationLayout(Context context) {
        super(context);
        init(context);
    }

    public BottomNavigationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
        layout = new LinearLayout(ctx);
        layout.setOrientation(1);
        layout.setPadding(0, 0, 0, dip2px(50));
        super.addView(layout);
        bottom = new LinearLayout(ctx);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2);
        params.gravity = Gravity.BOTTOM;
        bottom.setLayoutParams(params);
        int pad = dip2px(3);
        bottom.setPadding(pad, pad, pad, pad);
        super.addView(bottom);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    }

    public void setBottomBackgroundColor(int color) {
        bottom.setBackgroundColor(color);
    }

    public void setBackgroundDrawable(Drawable drawable) {
        bottom.setBackgroundDrawable(drawable);
    }

    public void setBackground(Drawable drawable) {
        bottom.setBackgroundDrawable(drawable);
    }

    public void addBottomButton(String text, int res, Drawable drawable, View.OnClickListener listener) {
        if (drawable == null) drawable = new ColorDrawable(Color.TRANSPARENT);
        addBottomButton(text, res, drawable, listener, 12, Color.BLACK);
    }

    public void addBottomButton(String text, int res, Drawable drawable, View.OnClickListener listener, float size, int color) {
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(1);
        layout.setGravity(Gravity.CENTER);
        TextView img = new TextView(ctx);
        img.setText("");
        img.setBackgroundResource(res);
        img.setGravity(Gravity.CENTER);
        img.setLayoutParams(new LinearLayout.LayoutParams(dip2px(27), dip2px(27)));
        layout.addView(img);
        TextView txt = new TextView(ctx);
        txt.setText(text);
        txt.setTextSize(size);
        txt.setTextColor(color);
        txt.setGravity(Gravity.CENTER);
        layout.addView(txt);
        layout.setOnClickListener(listener);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
        bottom.addView(layout);
        bottom.setWeightSum(bottom.getChildCount());
        layout.setBackgroundDrawable(drawable);
    }

    public void replace(View view) {
        layout.removeAllViews();
        layout.addView(view);
    }

    @Override
    public void addView(View view) {
        layout.addView(view);
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }

}
