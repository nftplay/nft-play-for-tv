package com.yindantech.nftplay.common.utils;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.preference.PreferenceManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.MetaDataUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.activity.MediaPlayActivity;
import com.yindantech.nftplay.activity.SplashActivity;
import com.yindantech.nftplay.common.glide.svg.SvgSoftwareLayerSetter;
import com.yindantech.nftplay.model.db.DBUtils;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.model.db.table.UserTable;

import java.util.List;
/**
 * MyUtils
 */
public class MyUtils {

    /**
     * Whether the login
     *
     * @return isLogin
     */
    public static boolean isLogin() {
        return !TextUtils.isEmpty(getWalletAddress());
    }


    /**
     * Get the wallet address
     *
     * @return wallet address
     */
    public static String getWalletAddress() {
        return SPUtils.getInstance().getString(AppConstant.Key.WALLET_ADDRESS);
    }

    /**
     * Save your wallet address
     */
    public static void saveWalletAddress(String walletAddress) {
        SPUtils.getInstance().put(AppConstant.Key.WALLET_ADDRESS, walletAddress);
    }


    /**
     * login
     *
     * @param address
     */
    public static UserTable login(String address) {
        address = address.toLowerCase();

        saveWalletAddress(address);
        UserTable user = DBUtils.getUser(address);
        if (null == user) {
            //Login with new account
            user = new UserTable();
            user.setAddress(address);
            DBUtils.saveUser(user);
        }
        return user;
    }

    private static final long TIP_DURATION = 2000L;
    private static long sLastClickMillis;
    private static int sClickCount;

    /**
     * click back 2 exit app
     */
    public static void back2ExitApp(View.OnClickListener clickListener) {
        long nowMillis = SystemClock.elapsedRealtime();
        if (Math.abs(nowMillis - sLastClickMillis) < TIP_DURATION) {
            sClickCount++;
            if (sClickCount == 2) {
                ToastUtils.cancel();
                clickListener.onClick(null);
                sLastClickMillis = 0;
            }
        } else {
            sClickCount = 1;
            sLastClickMillis = nowMillis;
            ToastUtils.showLong(R.string.click2back);
        }
    }


    /**
     * Check if the character is null, otherwise return ""
     *
     * @param str
     * @return
     */
    public static String formatStrNotNull(String str) {
        return TextUtils.isEmpty(str) ? "" : str;
    }

    /**
     * Gets the user's nickname, or returns the wallet address if there is none
     *
     * @param user
     * @return
     */
    public static String getOwnerUserName(UserTable user) {
        return TextUtils.isEmpty(user.getUsername()) ? user.getAddress() : user.getUsername();
    }

    /**
     * Gets the user's nickname, or returns the wallet address if there is none
     *
     * @param asset
     * @return
     */
    public static String getCreatorUserName(AssetTable asset) {
        return TextUtils.isEmpty(asset.getCreator_username()) ? asset.getCreator_address() : asset.getCreator_username();
    }

    /**
     * Gets the name of the asset, or returns tokenID if there is none
     *
     * @param asset
     * @return
     */
    public static String getAssetName(AssetTable asset) {
        return TextUtils.isEmpty(asset.getAsset_name()) ? asset.getAsset_token_id() : asset.getAsset_name();
    }

    /**
     * Access to App channels
     *
     * @return
     */
    public static String getAppChannel() {
        return MetaDataUtils.getMetaDataInApp("APP_CHANNEL");
    }


    /**
     * showKeyTipView
     * Opacity changed from 0 to 1 =2 seconds
     * Opacity for 1 =2 seconds
     * Change from 1 to 0 =2 seconds
     *
     * @param view Set alpha=0 in the layout
     */
    public static void showKeyTipView(View view, long time) {
        view.clearAnimation();
        view.animate().alpha(1f).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.animate().alpha(0f).setDuration(1000).setStartDelay(time).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }


    /**
     * exitLogin
     */
    public static void exitLogin() {
        MyUtils.saveWalletAddress("");
        DBUtils.deleteAllData((result) -> {
            Intent intent = new Intent(Utils.getApp(), SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityUtils.startActivity(intent);
        });
    }


    /**
     * isAutoStartApp
     *
     * @return true yes ,false no
     * @see {prefs.xml}
     */
    public static boolean isAutoStartApp() {
        return PreferenceManager.getDefaultSharedPreferences(Utils.getApp()).getBoolean("prefs_auto_start_app_key", true);
    }


    /**
     * isAutoPlayMedia
     *
     * @return true yes ,false no
     * @see {prefs.xml}
     */
    public static boolean isAutoPlayMedia() {
        return PreferenceManager.getDefaultSharedPreferences(Utils.getApp()).getBoolean("prefs_auto_play_media_key", true);
    }


    /**
     * is svg file
     *
     * @param url
     * @return
     */
    public static boolean isSVG(String url) {
        return (!TextUtils.isEmpty(url) && url.endsWith(".svg"));
    }


    /**
     * loadImage   jpg、png、gif / svg
     *
     * @param context
     * @param imageView
     * @param imageUrl
     * @param placeholderResId
     */
    public static void loadImage(Context context, ImageView imageView, String imageUrl, @DrawableRes int placeholderResId) {
        if (MyUtils.isSVG(imageUrl)) {
            Glide.with(context).as(PictureDrawable.class)
                    .listener(new SvgSoftwareLayerSetter())
                    .load(imageUrl)
                    .placeholder(placeholderResId)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(placeholderResId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }

    /**
     * listToString
     *
     * @param stringList
     * @return
     */
    public static String listToString(List<String> stringList) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < stringList.size(); i++) {
                sb.append(stringList.get(i));
                if (i != stringList.size() - 1) {
                    sb.append(",");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * isVideo
     *
     * @param
     * @return
     */
    public static boolean isVideo(AssetTable asset) {
        return (null != asset
                && !TextUtils.isEmpty(asset.getAsset_animation_url())
                && (asset.getAsset_animation_url().endsWith(".mp4")
                || asset.getAsset_animation_url().endsWith(".webm")));
    }


    /**
     * isMusic
     *
     * @param
     * @return
     */
    public static boolean isMusic(AssetTable asset) {
        return (null != asset
                && !TextUtils.isEmpty(asset.getAsset_animation_url())
                && (asset.getAsset_animation_url().endsWith(".mp3")
                || asset.getAsset_animation_url().endsWith(".wav")
                || asset.getAsset_animation_url().endsWith(".ogg")
                || asset.getAsset_animation_url().endsWith(".oga")));
    }


    /**
     * playMedia
     * Play media files by type
     */
    public static boolean playMedia(Activity activity, AssetTable asset, boolean isAutoBack) {
        if (MyUtils.isVideo(asset)) {
            MediaPlayActivity.startVideo(activity, asset.getAsset_id(), isAutoBack);
            return true;
        } else if (MyUtils.isMusic(asset)) {
            MediaPlayActivity.startMusic(activity, asset.getAsset_id(), isAutoBack);
            return true;
        }
        return false;
    }

}
