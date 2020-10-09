package com.zhoujian.zhbj.controller.tab;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.com.zhoujian.zhbj.fragment.MenuFragment;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhoujian.zhbj.activity.MainUI;
import com.zhoujian.zhbj.bean.NewsCenterBean;
import com.zhoujian.zhbj.bean.NewsCentreBean;
import com.zhoujian.zhbj.controller.menu.InteractMenuController;
import com.zhoujian.zhbj.controller.menu.MenuController;
import com.zhoujian.zhbj.controller.menu.NewsMenuController;
import com.zhoujian.zhbj.controller.menu.PictureMenuController;
import com.zhoujian.zhbj.controller.menu.TopicMenuController;
import com.zhoujian.zhbj.utils.CacheUtils;
import com.zhoujian.zhbj.utils.Constans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsCenterController extends TabController {
    final static String TAG = "NewsCenterController";
    private List<NewsCenterBean.NewsCenterMenuBean> mMenuDatas;
    private List<MenuController> mMenuControllers;
    private FrameLayout mContainer;
    public NewsCenterController(Context context) {
        super(context);
    }

    @Override
    protected View initContentView(Context context) {

//        initDataOKHttp2();
        initDataOKHttpPost();
        mContainer = new FrameLayout(mContext);
        return mContainer;
    }

    /**
     *
     */
    public void initDataOKHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = Constans.NEWSCENTER_URL;
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(url).get().build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    if (response.isSuccessful()){
                        String s = response.body().toString();
                        Log.d(TAG,s+"同步调用");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void initDataOKHttp2(){
        final String url = Constans.NEWSCENTER_URL;
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                Log.d(TAG,s+"异步调用");
            }
        });

    }


    public void initDataOKHttpPost(){

        OkHttpClient client = new OkHttpClient();
        final String url = Constans.NEWSCENTER_URL;
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, "zhoujian");
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).post(requestBody).build();//请求对象
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                Log.d(TAG,s+"POST异步调用");

            }
        });


    }

    public void volleyGet(){
        /**
         * 1，创建请求队列
         * 2，创建请求对象
         * 3，将请求对象添加到请求队列里面
          */


    }



    @Override
    public void initData() {
        final String url = Constans.NEWSCENTER_URL;
        //1,去访问网络获取数据
        //先去本地缓存取
        // 先去获取本地的缓存，将缓存展示出来
        String json = CacheUtils.getString(mContext, url);
        if (!TextUtils.isEmpty(json))
        {
            Log.d(TAG, "读取缓存");
            // 拿到json数据，展示数据
            performData(json);
//            performData2(json);
        }
        //如果没有缓存，那就先去网络上取到json，然后再格式化展示数据
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                String result = responseInfo.result;
                //json解析数据，然后展示出来。
                //将数据存储到sp中
                CacheUtils.setString(mContext,url,result);
                performData(result);
//                performData2(result);

            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(mContext, "联网失败", Toast.LENGTH_LONG);
                Log.d(TAG, "失败" + s);
            }
        });



    }

//    private void performData2(String json){
//        Gson gson = new Gson();
//        NewsCentreBean newsCentreBean = gson.fromJson(json, NewsCentreBean.class);
//        String title2 = newsCentreBean.getData().get(0).getChildren().get(2).getTitle();
//        Log.d(TAG, "校验" + title2);
//    }
    /**
     * //json解析数据，然后展示出来。
     *
     * @param json
     */
    private void performData(String json) {

        Gson gson = new Gson();
        NewsCenterBean newsCenterBean = gson.fromJson(json, NewsCenterBean.class);
        Log.d(TAG, "校验" + newsCenterBean.data.get(0).children.get(0).title);
        //展示数据：1，将数据添加到左侧菜单上面
        MenuFragment menuFragment = ((MainUI) mContext).getMenuFragment();//拿到菜单的fragment对象
        mMenuDatas = newsCenterBean.data;
        menuFragment.setDatas(mMenuDatas);
        //2，将菜单页面的数据加载到右边页面上
        mMenuControllers = new ArrayList<MenuController>();
        for (int i = 0; i < mMenuDatas.size(); i++) {
            NewsCenterBean.NewsCenterMenuBean newsCenterMenuBean = mMenuDatas.get(i);
            int type = newsCenterMenuBean.type;
            MenuController controller = null;
            switch (type) {
                case 1:
                    //新闻
                    controller = new NewsMenuController(mContext,newsCenterMenuBean.children);
                    break;
                case 10:
                    //专题
                    controller = new TopicMenuController(mContext);
                    break;
                case 2:
                    //组图
                    controller = new PictureMenuController(mContext);
                    break;
                case 3:
                    //互动
                    controller = new InteractMenuController(mContext);
                    break;
            }
            mMenuControllers.add(controller);
        }
       // 默认选中第一个
        switchItem(0);
    }

    /**
     * 切换菜单页面的方法
     *
     * @param menuItem
     */

    public void switchItem(int menuItem) {
        //切换之前先清空一下之前的view
        mContainer.removeAllViews();
        //把标题也改变成和菜单页面对应的标题
        String title = mMenuDatas.get(menuItem).title;
        mTvTitle.setText(title);
        MenuController controller = mMenuControllers.get(menuItem);
        //加载数据
        controller.initData();
        View view = controller.getmRootView();
        mContainer.addView(view);
        // 如果是 组图页面，显示listOrGrid按钮
        if (controller instanceof PictureMenuController)
        {
            mIvListOrGrid.setVisibility(View.VISIBLE);
            ((PictureMenuController) controller).setSwitchButton(mIvListOrGrid);

        }
        else
        {
            mIvListOrGrid.setVisibility(View.GONE);
        }

    }

}
