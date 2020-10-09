package com.zhoujian.zhbj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.zhbj.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zhoujian.zhbj.utils.CacheUtils;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * listView条目的详情页面
 */
public class DetailUI extends Activity {
    public static final String URL_KEY = "url";//intent传数据的时候的key
    private static final String TAG = "DetailUI";//Log日志的标记
    private static final String KEY_TEXT_SIZE = "text_size";//sp默认字体大小的缓存标记
    private int mCheckedItem;//默认选中了哪个字号条目
    @BindView(R.id.detail_iv_back)
    ImageView mIvBack;//返回控件
    @BindView(R.id.detail_iv_textsize)
     ImageView mIvTextSize;//字体大小控件
    @BindView(R.id.detail_iv_share)
   ImageView mIvShare;//分享控件
    @BindView(R.id.detail_wv)
     WebView mWv;//网页控件
    @BindView(R.id.detail_pb)
   ProgressBar mPb;//加载页面的进度条控件
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//加载布局的时候不显示ZHBJ的头
        setContentView(R.layout.detail_ui);
//        ViewUtils.inject(this);//注入找到控件变量
        ButterKnife.bind(this);
        initData();//加载数据
    }

    /**
     * 设置网页activity的数据
     */
    private void initData() {
        //去读取缓存设置字体大小
        mCheckedItem = CacheUtils.getInt(this, KEY_TEXT_SIZE);
        if (mCheckedItem==-1){//如果是第一次进来，之前没有设置过，就设置为默认的2
            mCheckedItem=2;
        }
        updateTextSize();//设置字体大小

        String url = getIntent().getStringExtra(URL_KEY);//拿到详细页面的url的链接
//        Log.d(TAG, "URL:" + url);
        WebSettings settings = mWv.getSettings();// 获取网页的设置对象
        settings.setJavaScriptEnabled(true);// 设置js可用
        settings.setBuiltInZoomControls(true);// 放大缩小的控件显示
        settings.setUseWideViewPort(true);// 设置可以双击放大或缩小
        mWv.loadUrl(url);//加载链接的网页
        mWv.setWebViewClient(new WebViewClient() {//加载页面的回调
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "页面开始加载");
                mPb.setVisibility(View.VISIBLE);//显示进度条
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "页面加载完成");
                mPb.setVisibility(View.GONE);//隐藏进度条
            }
        });

        mWv.setWebChromeClient(new WebChromeClient() {//页面加载进度的回调
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
//                Log.d(TAG, "progress" + newProgress);

            }
        });


    }

    @butterknife.OnClick(R.id.detail_iv_back)
    public void clickBack(View view){//点击返回按钮
    }
    @butterknife.OnClick(R.id.detail_iv_share)
    public void clickShare(View view){//点击分享按钮
    }
    @butterknife.OnClick(R.id.detail_iv_textsize)
    public void clickTextSize(View view){//点击字体设置按钮
        //1，弹出一个对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置字体大小");//设置对话框的头
        CharSequence[] items = new CharSequence[]{
                "超大字体", "大字体", "正常字体", "小字体", "超小字体"
        };//字符序列组

        mCheckedItem = 2;//默认选中的条目
        builder.setSingleChoiceItems(items, mCheckedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//点击条目的监听
                mCheckedItem = which;//点击了哪个条目，对话框就选中哪个条目
            }
        });
        //设置对话框的尾，确定
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //2，设置字体
                Log.d(TAG,"设置字体大小");

                updateTextSize();//设置字体的大小
                CacheUtils.setInt(DetailUI.this,KEY_TEXT_SIZE,mCheckedItem);//把设置的字体大小存入缓存

            }
        });
        builder.show();

    }

    /**
     * 设置字体大小的方法
     */
    private void updateTextSize() {
        WebSettings settings = mWv.getSettings();//拿到网页设置对象
        WebSettings.TextSize textSize = null;
        switch(mCheckedItem){
            case 0:
                textSize = WebSettings.TextSize.LARGEST;
                break;
            case 1:
                textSize = WebSettings.TextSize.LARGER;
                break;
            case 2:
                textSize = WebSettings.TextSize.NORMAL;
                break;
            case 3:
                textSize = WebSettings.TextSize.SMALLER;
                break;
            case 4:
                textSize = WebSettings.TextSize.SMALLEST;
                break;
            default:
                textSize = WebSettings.TextSize.NORMAL;
                break;

        }
        settings.setTextSize(textSize);//设置字体大小
    }
}
