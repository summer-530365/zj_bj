package com.zhoujian.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 重新定义一个类，继承viewpager重写他的dispatchTouchEvent方法，实现不拦截子类的触摸事件
 */
public class TouchedViewPager extends ViewPager {

    private float mDownX;
    private float mDownY;

    public TouchedViewPager(@NonNull Context context) {
        super(context);
    }

    public TouchedViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int positon = getCurrentItem(); //算出是第几个页面
        int action = ev.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                //如果是按下的操作，就让父容器不要去拦截，子控件自己实现
                //requestDisallowInterceptTouchEvent，这个方法的意思是是否请求父容器不允许拦截
                // true就代表父容器不拦截，子控件去响应。false就代表拦截，父控件去响应触摸事件
                getParent().requestDisallowInterceptTouchEvent(true);//
                break;

            case MotionEvent.ACTION_MOVE:
                float mMoveX = ev.getX();
                float mMoveY = ev.getY();
                float diffX = mMoveX - mDownX;
                float diffY = mMoveY - mDownY;
                //如果是水平滑动，才考虑滑动情况
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (positon == 0) {//1，如果是第一个页面，从左往右滑动，父控件响应；从右往左滑子控件响应
                        if (diffX > 0) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }

                    } else if (positon == getAdapter().getCount() - 1) {//如果是最后一个页面
                        //如果是最后一个页面，从左往后滑，自己实现；从右往左滑父容器实现
                        if (diffX > 0) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }

                    } else {//如果是中间页面，就自己相应，父容器不拦截
                        //拿到父容器，请求父容器不要拦截孩子
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }




                break;
            case MotionEvent.ACTION_UP:

                break;
        }


        return super.dispatchTouchEvent(ev);
    }

}
