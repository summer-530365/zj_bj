package com.zhoujian.zhbj.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.example.zhbj.R;
import com.zhoujian.zhbj.utils.CacheUtils;

/**
 * 欢迎界面的activity
 */
public class WelcomeUI extends Activity {
//修改1
    //修改4
    //修改5
    //修改2
    //修改3
    //修改10
    public static final String IS_FIRST = "is_first";
    private View mRootView;//welcomeui根布局
    private static final long DURATION = 1000;//动画时长
    private String TAG = WelcomeUI.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_ui);
        mRootView = findViewById(R.id.welcome_root);

        //做动画
        //旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(DURATION);
        rotateAnimation.setFillAfter(true);//动画完成后保留状态

        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(DURATION);
        rotateAnimation.setFillAfter(true);//动画完成后保留状态

        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        rotateAnimation.setDuration(DURATION);
        rotateAnimation.setFillAfter(true);//动画完成后保留状态

        //动画合集
        AnimationSet animationSet = new AnimationSet(true);//需要设置插入器
        animationSet.setInterpolator(new BounceInterpolator());//回弹效果的插入器
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        //启动动画
        mRootView.startAnimation(animationSet);

        //监听动画，动画做完后进行相应的跳转
        animationSet.setAnimationListener(new WelcomeAnimationListener());

    }




    class WelcomeAnimationListener implements Animation.AnimationListener {



        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //动画结束的时候调用
            //判断标记，isFirst为true的话，就进入引导页面，false的话进入内容页面

            if (CacheUtils.getBoolean(WelcomeUI.this, IS_FIRST, true)) {
                //进入到引导页面
                Log.d(TAG,"进入引导页面");
                Intent intent = new Intent(WelcomeUI.this,GuideUI.class);
                startActivity(intent);
                finish();
            } else {
                //进入到内容页面
                Log.d(TAG,"进入内容页面");
                Intent intent = new Intent(WelcomeUI.this,MainUI.class);
                startActivity(intent);
                finish();
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
