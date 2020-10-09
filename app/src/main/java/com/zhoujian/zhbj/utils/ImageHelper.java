package com.zhoujian.zhbj.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.Handler;
import android.util.LruCache;
import android.widget.ImageView;


public class ImageHelper
{

	// 数据池--->list,map
	// private Map<String, SoftReference<Bitmap>> mCaches = new
	// LinkedHashMap<String, SoftReference<Bitmap>>();

	// 全局的缓存
	private static LruCache<String, Bitmap> mCache;
	private static ExecutorService				mThreadPool;
	private static Map<ImageView, Future<?>>	mFutures	= new LinkedHashMap<ImageView, Future<?>>();
	private static Map<ImageView, String>		mFlags		= new LinkedHashMap<ImageView, String>();

	private Context								mContext;
	private Handler								handler		= new Handler();

	public ImageHelper(Context context) {
		if (mCache == null)
		{
			// 初始化
			int maxSize = (int) (Runtime.getRuntime().freeMemory() / 4);// 缓存空间有多大
			mCache = new LruCache<String, Bitmap>(maxSize) {

				// 用来记录添加进来的数据的内存大小
				@Override
				protected int sizeOf(String key, Bitmap value)
				{
					return value.getRowBytes() * value.getHeight();
				}
			};
		}

		this.mContext = context;

		if (mThreadPool == null)
		{
			mThreadPool = Executors.newFixedThreadPool(3);
		}
	}

	public void display(ImageView iv, String url)
	{
		// 1. 内存中取数据
		Bitmap bitmap = mCache.get(url);
		if (bitmap != null)
		{
			// 有
			iv.setImageBitmap(bitmap);
			return;
		}

		// 2. 到本地取
		bitmap = getFromLocal(url);
		if (bitmap != null)
		{
			// 有
			iv.setImageBitmap(bitmap);
			return;
		}

		// 3. 到网络中获取
		loadFromNet(url, iv);
	}

	private void loadFromNet(String url, ImageView iv)
	{
		// 开线程去网络获取图片
		// new Thread(new LoadImageTask(url, iv)).start();

		// ### 方案1
		// Future<?> future = mFutures.get(iv);
		// if (future != null && !future.isDone() && !future.isCancelled())
		// {
		// // 正在执行
		// future.cancel(true);// 取消线程任务,不一定能成功
		// future = null;
		// }
		// future = mThreadPool.submit(new LoadImageTask(url, iv));
		// // 存储到标记中
		// mFutures.put(iv, future);

		// ### 方案2
		mFlags.put(iv, url);
		mThreadPool.submit(new LoadImageTask(url, iv));
	}

	class LoadImageTask implements Runnable
	{
		private String		url;
		private ImageView	iv;

		public LoadImageTask(String url, ImageView iv) {
			this.url = url;
			this.iv = iv;
		}

		@Override
		public void run()
		{
			// 网络代码
			try
			{
				URL URL = new URL(url);
				// 创建连接
				HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
				conn.setConnectTimeout(30 * 1000);// 请求的耗时时间
				conn.setReadTimeout(30 * 1000);// response读取的耗时时间

				conn.connect();

				// 服务器输出给client，client输入
				InputStream stream = conn.getInputStream();

				// stream ---> bitmap
				Bitmap bitmap = BitmapFactory.decodeStream(stream);

				// 1. 存储到本地// bitmap--->file
				write2Local(url, bitmap);

				// 2. 存储到内存
				mCache.put(url, bitmap);

				// 3.展示数据--->子线程中执行
				// iv.setImageBitmap(bitmap);

				handler.post(new Runnable() {

					@Override
					public void run()
					{
						String newUrl = mFlags.get(iv);
						if (url.equals(newUrl))
						{
							display(iv, url);
						}
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private Bitmap getFromLocal(String url)
	{
		// file ---》bitmap
		String name;
		try
		{
			name = MD5Encoder.encode(url);
			File file = new File(getCacheDir(mContext), name);

			// 不存在
			if (!file.exists()) { return null; }

			// decode---> 是否压缩的判断
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

			if (bitmap != null)
			{

				// 存储到内存
				mCache.put(url, bitmap);

				return bitmap;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void write2Local(String url, Bitmap bitmap)
	{

		// bitmap--->file
		String name;
		FileOutputStream fos = null;
		try
		{
			name = MD5Encoder.encode(url);
			File file = new File(getCacheDir(mContext), name);

			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				fos = null;
			}
		}

	}

	private File getCacheDir(Context context)
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			// 挂载
			File dir = new File(Environment.getExternalStorageDirectory(), "/Android/data/" + context.getPackageName() + "/icon");
			if (!dir.exists())
			{
				dir.mkdirs();
			}
			return dir;
		}
		else
		{
			File dir = new File(context.getCacheDir(), "/icon");
			if (!dir.exists())
			{
				dir.mkdirs();
			}
			return dir;
		}
	}
}
