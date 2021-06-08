package com.yindantech.nftplay.common.base;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.yindantech.nftplay.model.db.DBHelper;

import com.yindantech.nftplay.model.db.table.DaoMaster;
import com.yindantech.nftplay.model.db.table.DaoSession;
import com.yindantech.nftplay.common.utils.AppConstant;
import com.yindantech.nftplay.common.utils.language.MultiLanguageUtils;


import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import rxhttp.RxHttp;
import rxhttp.wrapper.callback.IConverter;
import rxhttp.wrapper.converter.FastJsonConverter;
import rxhttp.wrapper.ssl.HttpsUtils;

/**
 * Application
 */
public class BaseApp extends Application {

    private DaoSession mDaoSession;
    public static BaseApp insatnce;

    @Override
    public void onCreate() {
        super.onCreate();
        insatnce = this;
        Utils.init(this);
        MultiLanguageUtils.init(this);
        initRxJava();
        initHttp();
        initDatabase();
    }

    /**
     * initRxJava
     */
    private void initRxJava() {
        RxJavaPlugins.setErrorHandler(throwable -> throwable.printStackTrace());
    }

    /**
     * initHttp
     */
    private void initHttp() {
        RxHttp.setDebug(AppConstant.Config.DEBUG);
        RxHttp.init(getHttpClient()); //Use the default
        IConverter converter = FastJsonConverter.create();
        RxHttp.setConverter(converter);
    }

    /**
     * getHttpClient
     *
     * @return
     */
    public static OkHttpClient getHttpClient() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    /**
     * initDatabase
     */
    private void initDatabase() {
        DBHelper mHelper = new DBHelper(this);
        DaoMaster daoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();//IdentityScopeType.None
    }

    /**
     * getDaoSession
     *
     * @return
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * getInstance
     *
     * @return
     */
    public static BaseApp getInstance() {
        return insatnce;
    }
}
