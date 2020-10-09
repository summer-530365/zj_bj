package com.zhoujian.zhbj.controller.tab;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhbj.R;
import com.zhoujian.zhbj.activity.MainUI;
import com.zhoujian.zhbj.controller.menu.MenuController;

public abstract class TabController implements View.OnClickListener {
    protected Context mContext;
    protected View mRootView;
    protected ImageView mIvMenu;
    protected TextView mTvTitle;
    protected FrameLayout mContentContainer;
    protected ImageView mIvListOrGrid;

    public TabController(Context context) {
        this.mContext = context;
        mRootView = initView(context);
    }

    /**
     * 子类共有的布局view
     *
     * @param context
     * @return
     */
    private View initView(Context context) {
        View view = View.inflate(mContext, R.layout.base_tab, null);
        mIvMenu = view.findViewById(R.id.base_tab_iv_menu);
        mTvTitle = view.findViewById(R.id.base_tab_tv_title);
        mIvListOrGrid = view.findViewById(R.id.base_tab_iv_listorgrid);
        mContentContainer = view.findViewById(R.id.base_tab_content_container);
        //初始化子类特有的view
        View contentView = initContentView(context);
        mContentContainer.addView(contentView);
        //设置菜单按钮的点击事件
        mIvMenu.setOnClickListener(this);
        return view;

    }

    /**
     * 子类去实现这个方法，完成特有布局的设置
     *
     * @param context
     * @return
     */
    protected abstract View initContentView(Context context);

    /**
     * 如果子类有需要初始化数据的话，就重写这个方法
     */
    public void initData() {

    }

    /**
     * 获取基类的根布局view
     *
     * @return
     */
    public View getRootView() {
        return mRootView;
    }


    @Override
    public void onClick(View v) {
        //如果点击的是菜单按钮就打开页面，否则就关闭
        if (v == mIvMenu) {
            ((MainUI)mContext).getSlidingMenu().toggle();

        }
    }

    /**
     * 切换菜单页面的方法
     * @param menuItem
     */
    public void switchItem(int menuItem) {

    }
}
