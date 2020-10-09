package com.com.zhoujian.zhbj.fragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.zhbj.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zhoujian.zhbj.activity.MainUI;
import com.zhoujian.zhbj.controller.news.NewsListController;
import com.zhoujian.zhbj.controller.tab.GovController;
import com.zhoujian.zhbj.controller.tab.HomeController;
import com.zhoujian.zhbj.controller.tab.NewsCenterController;
import com.zhoujian.zhbj.controller.tab.SettingController;
import com.zhoujian.zhbj.controller.tab.SmartServiceController;
import com.zhoujian.zhbj.controller.tab.TabController;
import com.zhoujian.zhbj.view.LazyViewPager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

/**
 * 内容区域的Fragment加载
 */
public class ContentFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG="ContentFragment";
    private LazyViewPager mContentPager;
    private List<TabController> mPagerDatas;
    private RadioGroup mRgTabs;
    private int mCurrentTab;

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.content, null);
        mRgTabs = view.findViewById(R.id.content_rg);
        mContentPager = view.findViewById(R.id.content_pager);
        return view;
    }

    @Override
    protected void initData() {
        mPagerDatas = new ArrayList<TabController>();
        mPagerDatas.add(new HomeController(mActivity));
        mPagerDatas.add(new NewsCenterController(mActivity));
        mPagerDatas.add(new SmartServiceController(mActivity));
        mPagerDatas.add(new GovController(mActivity));
        mPagerDatas.add(new SettingController(mActivity));
        //设置pager页面的监听
        mContentPager.setAdapter(new ContentPagerAdapter());

        //设置radiogroup的监听，根据选中的条目不同，设置不同的标记，然后根据标记来显示相应的pager条目
        mRgTabs.setOnCheckedChangeListener(this);
        //默认选中首页
        mRgTabs.check(R.id.content_rb_home);

    }

    //radiogroup的点击事件
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.content_rb_home:
                mCurrentTab=0;
                setSlidingMenuTouchEnable(false);
                    break;
            case R.id.content_rb_news:
                mCurrentTab=1;
                setSlidingMenuTouchEnable(true);
                break;
            case R.id.content_rb_smartsevice:
                mCurrentTab=2;
                setSlidingMenuTouchEnable(true);
                break;
            case R.id.content_rb_gov:
                mCurrentTab=3;
                setSlidingMenuTouchEnable(true);
                break;
            case R.id.content_rb_setting:
                mCurrentTab=4;
                setSlidingMenuTouchEnable(false);
                break;
        }

        //设置选中的选项卡
        mContentPager.setCurrentItem(mCurrentTab);
    }

    /**
     * 设置是否可以拖出菜单页面
     * @param enable
     */
    private void setSlidingMenuTouchEnable(boolean enable){
        //通过宿主获取SlidingMenu对象
        SlidingMenu menu = ((MainUI) mActivity).getSlidingMenu();
        menu.setTouchModeAbove(enable?SlidingMenu.TOUCHMODE_FULLSCREEN:SlidingMenu.TOUCHMODE_NONE);
    }
    /**
     * Adapter的适配器
     */
    class ContentPagerFragmentAdapter extends FragmentPagerAdapter{

        public ContentPagerFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }

    class ContentPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            if (mPagerDatas != null) {
                return mPagerDatas.size();
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
            Log.d(TAG,"加载了第"+position+"个页面");
            TabController controller = mPagerDatas.get(position);
            controller.initData();//加载数据
            View rootView = controller.getRootView();//加载完数据之后拿到他的view

            container.addView(rootView);//把拿到的view添加到viewpager中显示出来。
            return rootView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            Log.d(TAG,"销毁了第"+position+"个页面");
            container.removeView((View) object);
        }

    }

    /**
     * 切换菜单页面的方法
     * @param menuItem
     */
    public void switchMenuItem(int menuItem){
        TabController tabController = mPagerDatas.get(mCurrentTab);
        tabController.switchItem(menuItem);
    }
}
