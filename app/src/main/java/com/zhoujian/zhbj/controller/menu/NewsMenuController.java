package com.zhoujian.zhbj.controller.menu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.zhbj.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;
import com.zhoujian.zhbj.activity.MainUI;
import com.zhoujian.zhbj.bean.NewsCenterBean;
import com.zhoujian.zhbj.controller.news.NewsListController;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class NewsMenuController extends MenuController implements ViewPager.OnPageChangeListener {
    @ViewInject(R.id.newscenter_new_pager)
    private ViewPager mPager;

    private List<NewsCenterBean.NewsCenterMenuBean.NewsCenterNewsBean> mDatas;
    private TabPageIndicator mIndicator;//viewpager的指针控件
    private ImageView newsIndicatorIv;//向右滑动的箭头控件
    private String TAG = "NewsMenuController";

    /**
     * 构造方法，需要上下文和一个集合
     *
     * @param context
     * @param datas
     */
    public NewsMenuController(Context context, List<NewsCenterBean.NewsCenterMenuBean.NewsCenterNewsBean> datas) {
        super(context);
        mDatas = datas;
    }

    /**
     * 布局初始化，找到那些控件
     *
     * @param context
     * @return
     */
    @Override
    protected View initView(Context context) {
        View view = View.inflate(mContext, R.layout.newscenter_news, null);
        ViewUtils.inject(this, view);//注入方法找到对应的view
        mIndicator = view.findViewById(R.id.news_indicator);
        return view;
    }

    /**
     * 加载布局的数据
     */
    @Override
    public void initData() {

        //给viewpager加载数据
        mPager.setAdapter(new NewsMenuPagerAdapter());
        mIndicator.setViewPager(mPager);
        //设置TabPageIndicator mIndicator的滑动监听，如果不是第一个页面就不能划出菜单栏
        mIndicator.setOnPageChangeListener(this);
    }

    //利用xutils注入的方法实现箭头的点击事件
    @OnClick(R.id.news_indicator_iv)
    public void clickArrow(View view) {
        //点击箭头的时候，往后面移动一个
        int currentItem = mPager.getCurrentItem();
        mPager.setCurrentItem(++currentItem);
    }

    //设置viewpager，TabPageIndicator mIndicator的滑动监听，如果不是第一个页面就不能划出菜单栏
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //页面选中项的监听
    @Override
    public void onPageSelected(int position) {
        //如果不是第一个页面就不能划出菜单栏
        if (position == 0) {
            ((MainUI) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            ((MainUI) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

    }

    //页面滑动状态改变的监听
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //mPager的适配器
    class NewsMenuPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            //模拟ui数据
//            TextView tv = new TextView(mContext);
//            tv.setText(mDatas.get(position).title);
//            tv.setTextSize(24);
//            tv.setGravity(Gravity.CENTER);
//            tv.setTextColor(Color.RED);
//            container.addView(tv);
//            return tv;
            NewsCenterBean.NewsCenterMenuBean.NewsCenterNewsBean newsCenterNewsBean = mDatas.get(position);
            NewsListController controller = new NewsListController(mContext, newsCenterNewsBean);
            controller.initData();//加载controller的数据
            View rootView = controller.getmRootView();//加载完数据拿到他的根布局
            container.addView(rootView);   //把布局挂载到pager上
            return rootView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }


        /**
         * 返回这个滑动控件上方的标题
         *
         * @param position
         * @return
         */
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (mDatas != null) {
                NewsCenterBean.NewsCenterMenuBean.NewsCenterNewsBean newsCenterNewsBean = mDatas.get(position);
                return newsCenterNewsBean.title;

            }
            return super.getPageTitle(position);
        }
    }
}
