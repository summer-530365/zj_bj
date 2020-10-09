package com.zhoujian.zhbj.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 工具类
 */
public class CacheUtils {
    private static final String SP_NAME ="zhbj";
    private static SharedPreferences sp;

    /**
     * 获取sp的方法
     * @param context
     * @return返回sp的对象
     */
    private static SharedPreferences getSp(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME,context.MODE_PRIVATE);
        }
        return sp;

    }

    /**
     * 获取boolean数据的方法
     * @param context
     * @param key
     * @return如果没有值，返回false
     */
    public static boolean getBoolean(Context context,String key){
        SharedPreferences sp = getSp(context);
        boolean aBoolean = sp.getBoolean(key, false);
        return aBoolean;
    }

    /**
     * 获取boolean数据的方法，重载方法，可以自己设置默认值
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context context,String key,Boolean defValue){
        SharedPreferences sp = getSp(context);
        boolean aBoolean = sp.getBoolean(key, defValue);
        return aBoolean;
    }

    /**
     * 存入缓存的方法
     * @param context
     * @param key
     * @param value
     */
    public static void setBoolean(Context context, String key, Boolean value){
        SharedPreferences sp = getSp(context);
        SharedPreferences.Editor edit = sp.edit();//获取sp的编辑器
        edit.putBoolean(key,value);
        edit.commit();
    }

    /**
     * 获取String 数据
     *
     * @param context
     * @param key
     * @return 如果没有值，返回null
     */
    public static String getString(Context context, String key)
    {
        SharedPreferences sp = getSp(context);
        return sp.getString(key, null);
    }

    /**
     * 获取String 数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue)
    {
        SharedPreferences sp = getSp(context);
        return sp.getString(key, defValue);
    }

    /**
     * 存String缓存
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setString(Context context, String key, String value)
    {
        SharedPreferences sp = getSp(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }





    /**
     * 获取int 数据
     *
     * @param context
     * @param key
     * @return 如果没有值，返回-1
     */
    public static int getInt(Context context, String key)
    {
        SharedPreferences sp = getSp(context);
        return sp.getInt(key, -1);
    }

    /**
     * 获取int 数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context context, String key, int defValue)
    {
        SharedPreferences sp = getSp(context);
        return sp.getInt(key, defValue);
    }

    /**
     * 存int缓存
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setInt(Context context, String key, int value)
    {
        SharedPreferences sp = getSp(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

}
