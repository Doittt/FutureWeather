package com.doittt.futureweather;

import android.app.Application;

import com.baidu.location.LocationClient;
import com.doittt.futureweather.db.AppDatabase;
import com.doittt.futureweather.utils.MVUtils;
import com.doittt.library.BaseApplication;
import com.doittt.library.network.NetworkApi;
import com.tencent.mmkv.MMKV;

/**
 * 隐私访问授权类
 * 使用百度地图SDK时需要用户同意隐私协议
 */
public class WeatherApp extends BaseApplication {

    //数据库
    private static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        //使用定位需要同意隐私合规政策
        LocationClient.setAgreePrivacy(true);
        //初始化网络框架
        NetworkApi.init(new NetworkRequiredInfo(this));
        //MMKV初始化
        MMKV.initialize(this);
        //工具类初始化
        MVUtils.getInstance();
        //初始化Room数据库
        db = AppDatabase.getInstance(this);


    }

    public static AppDatabase getDb() {
        return db;
    }
}
