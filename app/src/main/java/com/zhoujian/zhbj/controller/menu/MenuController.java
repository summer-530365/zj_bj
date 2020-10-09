package com.zhoujian.zhbj.controller.menu;

import android.content.Context;
import android.view.View;

public abstract class MenuController {
    protected View mRootView;
    protected Context mContext;

    public MenuController(Context context){
        mContext=context;
        mRootView = initView(context);
    }

    /**
     * 子类初始化view
     * @return
     */
    protected abstract View initView(Context context);

    /**
     * 获取根view
     * @return
     */

    public View getmRootView(){
        return mRootView;
    }

    /**
     * 初始化数据
     */
    public void initData(){

    }

}
