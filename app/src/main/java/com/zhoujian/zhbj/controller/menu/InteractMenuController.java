package com.zhoujian.zhbj.controller.menu;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class InteractMenuController extends MenuController {
    public InteractMenuController(Context context) {
        super(context);
    }

    @Override
    protected View initView(Context context) {
        TextView tv = new TextView(context);
        tv.setText("互动菜单页面");
        tv.setTextSize(24);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.RED);
        return tv;
    }
}
