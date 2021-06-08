package com.yindantech.nftplay.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.LogUtils;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.common.base.BaseActivity;
import com.yindantech.nftplay.common.event.MessageEvent;
import com.yindantech.nftplay.fragment.MusicPlayFragment;
import com.yindantech.nftplay.fragment.VideoPlayFragment;
import com.yindantech.nftplay.model.db.DBUtils;
import com.yindantech.nftplay.model.db.table.AssetTable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.yindantech.nftplay.common.utils.AppConstant.Code.PLAY_REQUEST_CODE;
import static com.yindantech.nftplay.common.utils.AppConstant.Key.ASSET_ID;
import static com.yindantech.nftplay.common.utils.AppConstant.Key.IS_AUTO_BACK;
import static com.yindantech.nftplay.common.utils.AppConstant.Key.MEDIA_TYPE;
import static com.yindantech.nftplay.common.utils.AppConstant.Key.MEDIA_TYPE_MUSIC;
import static com.yindantech.nftplay.common.utils.AppConstant.Key.MEDIA_TYPE_VIDEO;

/**
 * MediaPlayActivity
 * This is a media playback page that uses Google TV library for media playback
 * Start the activity with startVideo() or startMusic()
 */
public class MediaPlayActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_media;
    }

    @Override
    protected void create() {
        setResult(Activity.RESULT_OK);
        EventBus.getDefault().register(this);
        String mediaType = getIntent().getStringExtra(MEDIA_TYPE);

        Fragment fragment = null;
        if (mediaType.equals(MEDIA_TYPE_VIDEO)) {
            fragment = new VideoPlayFragment();
        } else if (mediaType.equals(MEDIA_TYPE_MUSIC)) {
            fragment = new MusicPlayFragment();
        }

        if (null == fragment) {
            back();
            return;
        }
        FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.frameLayout);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        LogUtils.d("MediaPlayActivity onEvent", event.eventType);
        switch (event.eventType) {
            case MessageEvent.TYPE_GET_ASSETS_SUCCESS:
                assetsDataUpdate();
                break;
        }
    }

    /**
     * assetsDataUpdate
     */
    private void assetsDataUpdate() {
        AssetTable assetTable = DBUtils.getAsset(getIntent().getLongExtra(ASSET_ID, 0));
        if (null == assetTable) {
            LogUtils.d("assetsDataUpdate asset delete exist!");
            back();
        } else {
            LogUtils.d("assetsDataUpdate asset yes");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * startVideo
     *
     * @param activity
     * @param assetId
     * @param isAutoBack
     */
    public static void startVideo(Activity activity, Long assetId, boolean isAutoBack) {
        start(activity, assetId, MEDIA_TYPE_VIDEO, isAutoBack);
    }

    /**
     * startMusic
     *
     * @param activity
     * @param assetId
     * @param isAutoBack
     */
    public static void startMusic(Activity activity, Long assetId, boolean isAutoBack) {
        start(activity, assetId, MEDIA_TYPE_MUSIC, isAutoBack);
    }

    /**
     * start
     *
     * @param activity
     * @param assetId
     */
    private static void start(Activity activity, Long assetId, String mediaType, boolean isAutoBack) {
        Intent intent = new Intent(activity, MediaPlayActivity.class);
        intent.putExtra(ASSET_ID, assetId);
        intent.putExtra(MEDIA_TYPE, mediaType);
        intent.putExtra(IS_AUTO_BACK, isAutoBack);
        ActivityUtils.startActivityForResult(activity, intent, PLAY_REQUEST_CODE);
    }

}
