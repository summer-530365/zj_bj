package com.zhoujian.zhbj.controller.menu;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.zhoujian.zhbj.bean.NewsPagerBean;
import com.zhoujian.zhbj.utils.Constans;
import com.zhoujian.zhbj.utils.ImageHelper;

import java.util.List;


public class PictureMenuController extends MenuController implements View.OnClickListener {
//    private BitmapUtils mBitmapUtils;

    private static final String TAG = "PictureMenuController";
    @ViewInject(R.id.newscenter_pic_list_view)
    private ListView mListView;
    @ViewInject(R.id.newscenter_pic_grid_view)
    private GridView mGridView;
    private ImageView mIvListOrGrid;
    private ImageHelper mHelper;

    private boolean					isList	= true;
    private List<NewsPagerBean.NewsPagerNewsBean> mPicDatas;//listView的数据

    public PictureMenuController(Context context) {
        super(context);
    }

    @Override
    protected View initView(Context context) {
//        mBitmapUtils = new BitmapUtils(mContext);
        mHelper = new ImageHelper(mContext);
        View view = View.inflate(context, R.layout.newscenter_pic, null);
        ViewUtils.inject(this, view);//注入


        return view;
    }

    @Override
    public void initData() {
        //去网络加载数据
        String url = Constans.PHOTO_URL;
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                //解析数据
                performJson(result);


            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });

        //设置数据


    }

    public void setSwitchButton(ImageView iv)
    {
        this.mIvListOrGrid = iv;

        this.mIvListOrGrid.setOnClickListener(this);
    }
    /**
     * 解析json数据
     * @param json
     */
    private void performJson(String json){
        Gson gson = new Gson();
        NewsPagerBean bean = gson.fromJson(json, NewsPagerBean.class);
        mPicDatas = bean.data.news;


        PicAdapter mAdapter = new PicAdapter();
        mListView.setAdapter(mAdapter);
        mGridView.setAdapter(mAdapter);
        mIvListOrGrid.setImageResource(isList ? R.drawable.icon_pic_grid_type : R.drawable.icon_pic_list_type);


    }
//组图网格切换按钮的点击事件
    @Override
    public void onClick(View v) {
        if (v == mIvListOrGrid)
        {
            doSwitch();
        }
    }

    private void doSwitch() {

        // 状态取反
        isList = !isList;

        // listview和gridView显示的切换
        mListView.setVisibility(isList ? View.VISIBLE : View.GONE);
        mGridView.setVisibility(isList ? View.GONE : View.VISIBLE);

        // 图标的切换
        mIvListOrGrid.setImageResource(isList ? R.drawable.icon_pic_grid_type : R.drawable.icon_pic_list_type);


    }

    /**
     * listView的数据适配器
     */
    class PicAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (mPicDatas!=null){
                return mPicDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mPicDatas!=null){
                mPicDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView==null){
                convertView = View.inflate(mContext,R.layout.item_pic,null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                 holder.iv= convertView.findViewById(R.id.item_pic_iv_icon);
                 holder.tv = convertView.findViewById(R.id.item_pic_tv_icon);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            //设置数据
            String title = mPicDatas.get(position).title;
            holder.tv.setText(title);
            String listimage = mPicDatas.get(position).listimage;

//            mBitmapUtils.display(holder.iv,listimage);
                mHelper.display(holder.iv,listimage);

            return convertView;
        }
    }
    class ViewHolder{
        ImageView iv;
        TextView tv;

    }
}
