package com.zhoujian.zhbj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.zhbj.R;
import com.zhoujian.zhbj.utils.CacheUtils;
import com.zhoujian.zhbj.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GuideUI extends Activity {

    private ViewPager mPager;
    private int[] imgRes = new int[]{R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3};
    private List<ImageView> imgDatas;
    private LinearLayout mPontContainer;
    private View mSelectedPoint;
    private int mPointSpace;
    @BindView(R.id.guide_btn_start)
    Button mGuideBtnStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_ui);
        ButterKnife.bind(this);
        initView();//初始化布局
        initData();//初始化数据

    }

    /**
     * 初始化布局
     */
    private void initView() {
        mPager = findViewById(R.id.guide_pager);
        mPontContainer = findViewById(R.id.guide_point_container);
        mSelectedPoint = findViewById(R.id.guide_point_selected);
//        mGuideBtnStart = findViewById(R.id.guide_btn_start);
        //计算出两个点之间的距离，然后移动选中的点

        mSelectedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPointSpace = mPontContainer.getChildAt(1).getLeft() - mPontContainer.getChildAt(0).getLeft();//计算出两个点之间的距离
                mSelectedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        //将图片设置好添加到集合里面
        imgDatas = new ArrayList<ImageView>();
        for (int i = 0; i < imgRes.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(imgRes[i]);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            imgDatas.add(iv);
            //动态的添加点
            View view = new View(this);
            view.setBackgroundResource(R.drawable.point_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(this, 10f), DensityUtil.dip2px(this, 10f));
            if (i != 0) {//如果不是第一个点，就让它离左边10个dp
                params.leftMargin = 10;
            }
            mPontContainer.addView(view, params);
        }
        mPager.setAdapter(new GuidePagerAdapter());
        //监听viewPager
        mPager.addOnPageChangeListener(new mOnPageChangeListener());
        //设置开始体验按钮的监听
        mGuideBtnStart.setOnClickListener(new mGuideBtnStartListener());
    }

    class mGuideBtnStartListener implements View.OnClickListener {
        //如果点击了开始体验按钮就跳转到内容页面
        @Override
        public void onClick(View v) {
            jump();

        }

        /**
         * 跳转到内容页面的逻辑
         */
        private void jump() {
            //跳转到主界面
    Intent intent = new Intent(GuideUI.this,MainUI.class);
    startActivity(intent);
    finish();
    //修改标记，记录不是第一次使用软件
            CacheUtils.setBoolean(GuideUI.this,WelcomeUI.IS_FIRST,false);
        }
    }

    class mOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        //根据viewPager的滑动距离，计算出选择的点需要移动的距离
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int left = (int) (mPointSpace * positionOffset + 0.5f);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSelectedPoint.getLayoutParams();

            params.leftMargin = left + mPointSpace * position;
            mSelectedPoint.setLayoutParams(params);
        }

        @Override
        //如果滑到最后一页，就显示开始体验按钮
        public void onPageSelected(int position) {
            if (position==imgRes.length-1) {
                mGuideBtnStart.setVisibility(View.VISIBLE);
            }else{
                mGuideBtnStart.setVisibility(View.GONE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    //设置viewPager的适配器
    class GuidePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (imgDatas != null) {
                return imgDatas.size();
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
            ImageView iv = imgDatas.get(position);
            container.addView(iv);

            return iv;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
