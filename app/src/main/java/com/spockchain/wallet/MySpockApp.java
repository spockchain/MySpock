package com.spockchain.wallet;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDexApplication;

import com.spockchain.wallet.domain.DaoMaster;
import com.spockchain.wallet.domain.DaoSession;
import com.spockchain.wallet.repository.RepositoryFactory;
import com.spockchain.wallet.repository.SharedPreferenceRepository;
import com.spockchain.wallet.utils.AppFilePath;
import com.spockchain.wallet.utils.LogInterceptor;
import com.google.gson.Gson;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatCrashReporter;
import com.tencent.stat.StatService;

import io.realm.Realm;
import okhttp3.OkHttpClient;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class MySpockApp extends MultiDexApplication {

    private static MySpockApp sInstance;

    private RefWatcher refWatcher;

    private DaoSession daoSession;
    private static OkHttpClient httpClient;
    public static RepositoryFactory repositoryFactory;
    public static SharedPreferenceRepository sp;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static RefWatcher getRefWatcher(Context context) {
        MySpockApp application = (MySpockApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        init();

        Realm.init(this);

        refWatcher = LeakCanary.install(this);

        AppFilePath.init(this);

        // [可选]设置是否打开debug输出，上线时请关闭，Logcat标签为"MtaSDK"
        StatConfig.setDebugEnable(false);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this);
        // Crash report
        StatCrashReporter.getStatCrashReporter(getApplicationContext()).setJavaCrashHandlerStatus(true);
    }


    public static MySpockApp getsInstance() {
        return sInstance;
    }

    protected void init() {

        sp = SharedPreferenceRepository.init(getApplicationContext());

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .build();

        Gson gson = new Gson();

        repositoryFactory = RepositoryFactory.init(sp, httpClient, gson);


        //创建数据库表
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "wallet", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();


    }


    public static OkHttpClient okHttpClient() {
        return httpClient;
    }

    public static RepositoryFactory repositoryFactory() {
        return  repositoryFactory;
    }


}
