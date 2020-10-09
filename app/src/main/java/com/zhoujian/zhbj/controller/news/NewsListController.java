package com.zhoujian.zhbj.controller.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhbj.R;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhoujian.zhbj.activity.DetailUI;
import com.zhoujian.zhbj.bean.NewsCenterBean;
import com.zhoujian.zhbj.bean.NewsPagerBean;
import com.zhoujian.zhbj.controller.menu.MenuController;
import com.zhoujian.zhbj.utils.CacheUtils;
import com.zhoujian.zhbj.utils.Constans;
import com.zhoujian.zhbj.view.RefreshListView;

import java.util.List;


import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * 这是新闻菜单页面下面，小的页面。
 */
public class NewsListController extends MenuController implements RefreshListView.OnRefreshListener, AdapterView.OnItemClickListener {

    public static final String TAG = "NewsListController";
    NewsCenterBean.NewsCenterMenuBean.NewsCenterNewsBean mData;
    @ViewInject(R.id.news_list_pager)
    private ViewPager mPager;
    @ViewInject(R.id.news_list_title)
    private TextView mTvTitle;
    @ViewInject(R.id.news_list_container)
    private LinearLayout mPointContainer;
    private List<NewsPagerBean.NewsPagerTopnewsBean> mTopData;
//    private final BitmapUtils mBitmapUtils;
    private AutoSwitchTask mSwitchTask;
    @ViewInject(R.id.news_list_list_view)
    private RefreshListView mListView;
    private List<NewsPagerBean.NewsPagerNewsBean> mNewsDatas;
    private String moreUrl;
    private NewsAdapter mNewsAdapter;


    /**
     * 构造方法
     *
     * @param context
     * @param newsCenterNewsBean
     */
    public NewsListController(Context context, NewsCenterBean.NewsCenterMenuBean.NewsCenterNewsBean newsCenterNewsBean) {
        super(context);
        mData = newsCenterNewsBean;
//        mBitmapUtils = new BitmapUtils(mContext);
        mSwitchTask = new AutoSwitchTask();

//        new AutoSwitchTask2().sendEmptyMessageDelayed(1001,2000);
    }


    /**
     * 加载布局
     *
     * @param context
     * @return
     */
    @Override
    protected View initView(Context context) {
        View view = View.inflate(context, R.layout.news_list_viewpager, null);
        ViewUtils.inject(this, view);
        View topView = View.inflate(context, R.layout.news_top, null);
        ViewUtils.inject(this, topView);

//        mListView.addHeaderView(topView);
        //
        mListView.addCustomHeaderView(topView);
        mListView.setOnRefreshListener(this);
        mListView.setOnItemClickListener(this);

        return view;
    }

    /**
     * 加载布局的数据
     * 1，先去网络获取数据
     * 2，把数据加载到viewpager上面
     */

    @Override
    public void initData() {
        final String url = Constans.BASE_SERVER + mData.url;
        //先去本地取缓存
        String gson = CacheUtils.getString(mContext, url);
        if (!TextUtils.isEmpty(gson)) {
            performData(gson);
        }

        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.d(TAG,result);
                //存缓存
                CacheUtils.setString(mContext, url, result);
                //处理从网络拿到的数据
                performData(result);
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });


    }

    /**
     * 处理从网络拿到的数据的方法
     *
     * @param json
     */
    @SuppressLint("ClickableViewAccessibility")
    private void performData(String json) {
        Gson gson = new Gson();
        NewsPagerBean bean = gson.fromJson(json, NewsPagerBean.class);
        //校验数据
        Log.v(TAG, "" + bean.data.news.get(0).title);
        mTopData = bean.data.topnews;
        mNewsDatas = bean.data.news;
        moreUrl = bean.data.more;
        //设置上面这个pager的数据
        mPager.setAdapter(new TopNewsAdapter());
        //动态的去添加点
        //添加点之前，先清除一下之前的缓存
        mPointContainer.removeAllViews();
        int size = mTopData.size();//需要添加几个点
        for (int i = 0; i < size; i++) {
            View child = new View(mContext);
            child.setBackgroundResource(R.drawable.dot_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            if (i != 0) {
                params.leftMargin = 20;
            } else {
                child.setBackgroundResource(R.drawable.dot_focus);
                mTvTitle.setText(mTopData.get(i).title);//设置默认的标题
            }
            mPointContainer.addView(child, params);
        }

        mPager.addOnPageChangeListener(new NewsOnPageChangeListener());//设置mPager页面改变的监听
        mSwitchTask.start();//实现pager页面的自动轮播
        //自动轮播的bug解决，当手指按下触摸pager页面的时候，停止自动轮播
        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        //如果对pager是按下的操作，那么就让他停止自动轮播操作
                        mSwitchTask.stop();

                        break;
                    case MotionEvent.ACTION_UP:
                        //如果对pager是抬起的操作，那么就让他又开始自动轮播操作
                        mSwitchTask.start();
                        break;
                }
                return false;
            }
        });
        mNewsAdapter = new NewsAdapter();
        mListView.setAdapter(mNewsAdapter);

    }



    /**
     * viewppager滑动改变的监听，动态的移动点。
     */

    class NewsOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int count = mPointContainer.getChildCount();//获取pager一共有几个孩子，就有几个点
            //设置每个点的选中状态
            for (int i = 0; i < count; i++) {
                View child = mPointContainer.getChildAt(i);
                //当前选中的position这个的点设置为红色，其他的点设置为默认白色的。
                child.setBackgroundResource(i == position ? R.drawable.dot_focus : R.drawable.dot_normal);

            }
            //设置下面状态栏的标题
            mTvTitle.setText(mTopData.get(position).title);
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onRefreshing() {//正在下拉刷新时候的操作
        {
            Log.d(TAG, "正在刷新，去网络重新获取数据");
            final String url = Constans.BASE_SERVER + mData.url;
            HttpUtils utils = new HttpUtils();
            utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {

                    String result = responseInfo.result;
                    //存缓存
                    CacheUtils.setString(mContext, url, result);
                    //处理从网络拿到的数据
                    performData(result);
                    Log.d(TAG, "成功重新刷新数据");
                    //成功重新刷新数据后，还应该更新ui让刷新头消失，
                    //可以在refreshlistview里面自定义一个方法，这里调用那个方法来实现这个操作
                    mListView.refreshFinish();
                    Log.d(TAG, "隐藏刷新头");
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.d(TAG, "没有重新刷新数据");

                }
            });
        }

    }

    @Override
    public void onLoadingMore() {//上拉加载的逻辑操作
        {
            if (TextUtils.isEmpty(moreUrl)) {
                Log.d(TAG, "没有更多数据了，别再滑了");
                Toast.makeText(mContext, "没有更多数据，别再滑了", Toast.LENGTH_SHORT).show();
                mListView.refreshFinish();//告诉refreshlistview我加载完成了，你可以隐藏UI了
                return;
            }
            //去网络加载数据
            final String url = Constans.BASE_SERVER + moreUrl;
            HttpUtils utils = new HttpUtils();
            utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String result = responseInfo.result;
                    //拿到json数据后，想办法把数据添加到listview的后面
                    Gson gson = new Gson();
                    NewsPagerBean bean = gson.fromJson(result, NewsPagerBean.class);
                    List<NewsPagerBean.NewsPagerNewsBean> list = bean.data.news;
                    moreUrl = bean.data.more;
                    mNewsDatas.addAll(list);
                    //更新listview
                    mNewsAdapter.notifyDataSetChanged();
                    Log.d(TAG, "成功加载更多数据");
                    mListView.refreshFinish();
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.d(TAG, "失败的加载更多");

                    Toast.makeText(mContext, "加载更多失败", Toast.LENGTH_SHORT).show();
                    mListView.refreshFinish();
                }
            });


        }

    }

    /**
     * listView条目点击的监听操作
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //因为listview算position是包含头和尾，所以再算position的时候要排除掉头和尾
        position = position - mListView.getHeaderViewsCount();//减去头的数目
        if (position >= mNewsAdapter.getCount()) {//如果点的位置数字比adapter的数目还多，说明点了尾部，就不操作
            return;
        }

        Intent intent = new Intent(mContext, DetailUI.class);
        NewsPagerBean.NewsPagerNewsBean bean = mNewsDatas.get(position);
//        Log.d(TAG,bean.title);
        intent.putExtra(DetailUI.URL_KEY, bean.url);//跳转到新页面的时候把html链接的带过去
        mContext.startActivity(intent);//当点击了条目之后，就跳转到条目详情页面
        //文本已读和未读颜色的变化。拿到这条记录的id，然后把id当成key存到sp里面，点过就为true。
        long itemId = bean.id;
        CacheUtils.setBoolean(mContext, "" + itemId, true);
        mNewsAdapter.notifyDataSetChanged();//更新变化
    }

    /**
     *
     * listview的adapter
     * 思路：
     */

    class NewsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mNewsDatas != null) {
                return mNewsDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mNewsDatas != null) {
                return mNewsDatas.get(position);
            }

            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public int getItemViewType(int position) {
            if (position==1||position==2){
                return 10001;
            }else{
                return 1002;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (getItemViewType(position)==1001){}
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_news, null);
                holder = new ViewHolder();
                holder.ivIcon = convertView.findViewById(R.id.item_news_iv_icon);
                holder.tvTittle = convertView.findViewById(R.id.item_news_tv_tittle);
                holder.tvPubdate = convertView.findViewById(R.id.item_news_tv_pubdate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //设置默认图片
            holder.ivIcon.setImageResource(R.drawable.news_pic_default);
            //设置从网络上获取到的图片
            NewsPagerBean.NewsPagerNewsBean bean = mNewsDatas.get(position);
//            mBitmapUtils.display(holder.ivIcon, mNewsDatas.get(position).listimage);
            Glide.with(mContext).load( mNewsDatas.get(position).listimage).placeholder(R.drawable.home_scroll_default).into(holder.ivIcon);
            holder.tvTittle.setText(mNewsDatas.get(position).title);
            //去sp里面取id缓存的数据，根据值来设置颜色
            boolean flag = CacheUtils.getBoolean(mContext, "" + bean.id, false);
            holder.tvTittle.setTextColor(flag ? Color.GRAY : Color.BLACK);

            holder.tvPubdate.setText(mNewsDatas.get(position).pubdate);

            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvTittle;
        TextView tvPubdate;


    }

    /**
     * 自动轮播实现2，用一个类封装这些功能来实现
     */
    class AutoSwitchTask extends Handler implements Runnable {
        public void start() {
            //移除缓存
//            stop();
            removeCallbacks(this);
            //每两秒去执行以下这个操作
            postDelayed(this, 2000);
        }

        //移除缓存的方法
        public void stop() {
            removeCallbacks(this);
        }

        //每两秒要执行的操作
        @Override
        public void run() {
            int currentItem = mPager.getCurrentItem();//看当前是第几个页面
            if (currentItem == mPager.getAdapter().getCount() - 1) {//如果是最后一个页面，就把当前的item设置为0
                currentItem = 0;
            } else {
                currentItem++;//如果不是最后一个条目，就把这个条目+1
            }
            //设置当前需要显示的条目
            mPager.setCurrentItem(currentItem);
            postDelayed(this, 2000);

        }


    }

    class AutoSwitchTask2 extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:

                    int currentItem = mPager.getCurrentItem();//看当前是第几个页面
                    if (currentItem == mPager.getAdapter().getCount() - 1) {//如果是最后一个页面，就把当前的item设置为0
                        currentItem = 0;
                    } else {
                        currentItem++;//如果不是最后一个条目，就把这个条目+1
                    }
                    //设置当前需要显示的条目
                    mPager.setCurrentItem(currentItem);
                    sendEmptyMessageDelayed(1001,2000);
                    break;
            }
        }
    }


    /**
     * PagerAdapter的数据加载
     */
    class TopNewsAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            if (mTopData != null) {
                return mTopData.size();
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
            ImageView iv = new ImageView(mContext);
            //设置图片填充viewpager页面
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            //设置默认图片
            iv.setImageResource(R.drawable.home_scroll_default);
            //去网络获取图片显示出来
            NewsPagerBean.NewsPagerTopnewsBean bean = mTopData.get(position);
            //用工具把一个地址链接的图片，加载到imageview上
//            mBitmapUtils.display(iv, bean.topimage);
            Glide.with(mContext).load(bean.topimage).placeholder(R.drawable.home_scroll_default).into(iv);
//            Glide.with(mContext).load(bean.topimage).placeholder()
            //把imageview布局挂载到pager页面上
            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

            container.removeView((View) object);
        }

    }
}
