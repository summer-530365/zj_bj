package com.zhoujian.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class NoScrollViewPager extends LazyViewPager {

    public NoScrollViewPager(@NonNull Context context) {
        super(context);
    }
    public NoScrollViewPager(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
    }

    /**
     * 重写父类是否拦截的方法
     * @param ev
     * @return
     */

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;//不拦截，把事件丢给他的孩子处理
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;//不消费事件，把事件丢给父亲处理，又由于他的父亲是个relativelayout没有滑动功能，所以他就滑不了了
    }

}
