package com.yindantech.nftplay.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.yindantech.nftplay.activity.MainActivity;
import com.yindantech.nftplay.activity.SplashActivity;
import com.yindantech.nftplay.common.utils.MyUtils;

/**
 * BootReceiver
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtils.d("BootReceiver onReceive", intent.getAction());

        //Here, determine whether the boot auto startup is used, and if so, start the APP
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && MyUtils.isAutoStartApp()) {
            ActivityUtils.startActivity(MyUtils.isLogin() ? MainActivity.class : SplashActivity.class);
        }
    }
}