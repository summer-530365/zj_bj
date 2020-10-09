package com.zhoujian.zhbj.controller.tab;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class SmartServiceController extends TabController {

    public SmartServiceController(Context context) {
        super(context);
    }

    @Override
    protected View initContentView(Context context) {
        TextView tv = new TextView(context);
        tv.setText("智慧服务");
        tv.setTextSize(24);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.RED);
        return tv;
    }
}
