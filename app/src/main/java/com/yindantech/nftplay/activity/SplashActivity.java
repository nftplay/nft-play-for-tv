package com.yindantech.nftplay.activity;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.common.base.BaseActivity;
import com.yindantech.nftplay.common.utils.AppConstant;
import com.yindantech.nftplay.common.utils.MyUtils;


import java.io.InputStream;

import butterknife.BindView;


/**
 * SplashActivity
 * Start the page, check if there is a network connection and the network is available, otherwise prompt, when the page switch back, check again
 * The login page loads if you are not logged in，
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.view_splash_no_network)
    View view_splash_no_network;
    @BindView(R.id.iv_splash_image)
    ImageView iv_splash_image;
    @BindView(R.id.layout_loading)
    View layout_loading;
    WebView mWebView;
    @BindView(R.id.iv_debug)
    View iv_debug;
    @BindView(R.id.tv_splash_key_tip)
    View tv_splash_key_tip;
    @BindView(R.id.layout_web)
    FrameLayout layout_web;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void create() {
        iv_debug.setVisibility(AppConstant.Config.DEBUG ? View.VISIBLE : View.GONE);
        checkNetWork();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkNetWork();
    }

    /**
     * checkNetWork
     */
    private void checkNetWork() {
        updateUI(NetworkUtils.isConnected());
    }

    /**
     * updateUI
     *
     * @param isNetWork
     */
    private void updateUI(boolean isNetWork) {
        if (isNetWork) {
            view_splash_no_network.setVisibility(View.GONE);
            iv_splash_image.setImageResource(R.mipmap.ic_logo_splash);
            boolean isLogin = MyUtils.isLogin();
            ThreadUtils.getMainHandler().postDelayed(() -> {
                if (isLogin) {
                    ActivityUtils.startActivity(MainActivity.class);
                    ActivityUtils.finishActivity(this, false);
                } else {
                    initWebView();
                    loadLoginUI();
                }
            }, isLogin ? AppConstant.Config.SPLASH_START_DELAY_TIME : 0);
        } else {
            //no network
            layout_loading.setVisibility(View.GONE);
            view_splash_no_network.setVisibility(View.VISIBLE);
            iv_splash_image.setImageResource(R.mipmap.ic_no_network);
        }

    }

    /**
     * Loading the login interface UI
     */
    private void loadLoginUI() {
        if (null != mWebView) {
            layout_loading.setVisibility(View.VISIBLE);
            mWebView.loadUrl(AppConstant.Config.LOGIN_HTML);
        }

        //test code
        // You can log in directly from the specified wallet address here, but you need to pay attention to the environment that the OpenSea API points to
        // If you log in to the wrong environment, you may not be able to load the data correctly for you

//        new JsFunc().login("0x65c1b9ae4e4d8dcccfd3dc41b940840fe8570f2a");//Main net
//        new JsFunc().login("0xC1358Eb94b891754D6acf9dDa2f64B9133265c10");//Rinkeby net
    }

    /**
     * initWebView
     */
    private void initWebView() {

        if (null != mWebView) {
            return;
        }
        mWebView = new WebView(Utils.getApp());
        layout_web.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mWebView.setAlpha(0f);
        //webview settings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        String oriUserAgent = webSettings.getUserAgentString();
        //If you do not change to Windows, the mobile connection will appear on Android
        LogUtils.d("oriUserAgent", oriUserAgent);
//        Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36
        String newUserAgent = oriUserAgent.replace("Android", "Windows");
        webSettings.setUserAgentString(newUserAgent);
        //js interface
        mWebView.addJavascriptInterface(new JsFunc(), "yindantech");
        //client
        mWebView.setWebViewClient(new MyWebViewClient());
    }

    @Override
    protected void onDestroy() {
        destroyWebView();
        if (null != tv_splash_key_tip)
            tv_splash_key_tip.clearAnimation();
        super.onDestroy();
    }

    /**
     * destroyWebView
     */
    public void destroyWebView() {
        LogUtils.d("destroyWebView");
        try {
            if (null != mWebView) {
                mWebView.loadUrl("about:blank");
                mWebView.stopLoading();
                mWebView.onPause();
                mWebView.clearHistory();
                ViewParent parent = mWebView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(mWebView);
                }
                mWebView.removeAllViews();
                mWebView.destroy();
                mWebView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * JsFunc
     */
    private class JsFunc {
        @JavascriptInterface
        public void login(String address) {
            LogUtils.d("JsFunc login", address);
            if (TextUtils.isEmpty(address)) {
                ToastUtils.showShort(getString(R.string.address_invalid));
            } else {
                MyUtils.login(address);
                ActivityUtils.startActivity(MainActivity.class);
                ActivityUtils.finishActivity(SplashActivity.this);
            }
        }
    }

    /**
     * WebViewClient
     */
    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtils.v("web onPageStarted url:" + url);
            if (layout_loading.getVisibility() == View.GONE)
                layout_loading.setVisibility(View.VISIBLE);
            mWebView.setAlpha(0f);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            LogUtils.v("web onPageFinished url:" + url);
            //The reason for the delay of 1s here is that the UI of the login page Wallet - Connect is written in the library, so it cannot be customized. The front end can only modify the style through JS after loading the UI, which will cause the problem of UI flash (there is zoom animation in itself)
            //Failure to pass the setVisibility control is because it will cause the WebView interface elements to be misplaced.Also because the launch page can load the login UI immediately, without waiting for a delay to display the launch UI, and does not overwrite the launch UI.      ThreadUtils.getMainHandler().postDelayed(() -> {
            ThreadUtils.getMainHandler().postDelayed(() -> {
                if (null != layout_loading) {
                    layout_loading.setVisibility(View.GONE);
//                    mWebView.setVisibility(View.VISIBLE);
                    mWebView.setAlpha(1f);
                    if (!mIsShowKeyTip) {
                        MyUtils.showKeyTipView(tv_splash_key_tip, 1000 * 15);
                        mIsShowKeyTip = true;
                    }
                }
            }, 1500);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            LogUtils.v("web shouldOverrideUrlLoading url:" + url);
            view.loadUrl(url);
            return true;
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            //Intercept the log-in page background image and use a local image,
            //Optimize image loading speed
            try {
//                LogUtils.d("shouldInterceptRequest", request.getUrl().getPath());
                if (request.getUrl().getPath().contains("login_backup.")) {
                    InputStream localCopy = getAssets().open("login_backup.jpg");
                    return new WebResourceResponse("image/png", "UTF-8", localCopy);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.shouldInterceptRequest(view, request);
        }
    }

    private boolean mIsShowKeyTip;

    @Override
    protected boolean clickKeyEnter() {
        //Press OK to refresh the QR code of the login page
        if (null != mWebView && mWebView.getAlpha() == 1f) {
            mWebView.reload();
            LogUtils.d("webview reload");
            return true;
        }
        LogUtils.d("webview no reload");
        return super.clickKeyEnter();
    }
}
