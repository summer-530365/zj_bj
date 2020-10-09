package com.zhoujian.zhbj.activity;

import android.os.Bundle;

import com.com.zhoujian.zhbj.fragment.ContentFragment;
import com.com.zhoujian.zhbj.fragment.MenuFragment;
import com.example.zhbj.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainUI extends SlidingFragmentActivity {
    private static final String	TAG_LEFT = "left";
    private static final String	TAG_CONTENT = "content";
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //设置右边的布局
        setContentView(R.layout.main_ui);
        //设置左边的布局
        setBehindContentView(R.layout.main_left);
        //获得slidingmenu对象
        SlidingMenu menu = getSlidingMenu();
        //设置左侧布局的宽度
        menu.setBehindOffset(500);
        //设置它的模式
        menu.setMode(SlidingMenu.LEFT);
        //设置触摸方法
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //加载左右布局的页面
        initFragment();

    }

    /**
     *加载左右两边的fragment
     */
    //Todo
    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_left_container,new MenuFragment(),TAG_LEFT);
        fragmentTransaction.add(R.id.main_content_container,new ContentFragment(),TAG_CONTENT);
        fragmentTransaction.commit();

    }

    /**
     * 获得左侧菜单fragment的方法
     * @return
     */
    public MenuFragment getMenuFragment(){
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MenuFragment fragmentleft = (MenuFragment) supportFragmentManager.findFragmentByTag(TAG_LEFT);
        return fragmentleft;

    }

    /**
     * 获得内容fragment的方法
     * @return
     */
    public ContentFragment getContentFragment(){
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        ContentFragment fragmentContent = (ContentFragment) supportFragmentManager.findFragmentByTag(TAG_CONTENT);
        return fragmentContent;

    }

}
