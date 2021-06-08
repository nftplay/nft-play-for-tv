package com.yindantech.nftplay.activity;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.common.base.BaseActivity;
import com.yindantech.nftplay.common.event.MessageEvent;
import com.yindantech.nftplay.model.db.DBUtils;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.model.db.table.UserTable;
import com.yindantech.nftplay.common.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.yindantech.nftplay.common.utils.AppConstant.Key.ASSET_ID;

/**
 * AssetDetailsActivity
 * This is a page that displays details of NFT assets. By default, it displays images. If NFT has supported media files, it will display a play button.
 * The current page is started with the start() function
 */
public class AssetDetailsActivity extends BaseActivity {

    @BindView(R.id.ll_play_check)
    View ll_play_check;
    @BindView(R.id.tv_play_check)
    TextView tv_play_check;
    @BindView(R.id.iv_play_check)
    ImageView iv_play_check;
    @BindView(R.id.iv_image)
    ImageView iv_image;
    @BindView(R.id.layout_asset_info)
    View layout_asset_info;
    @BindView(R.id.tv_asset_name)
    TextView tv_asset_name;
    @BindView(R.id.tv_asset_identifier)
    TextView tv_asset_identifier;
    @BindView(R.id.tv_asset_owner)
    TextView tv_asset_owner;
    @BindView(R.id.tv_asset_creator)
    TextView tv_asset_creator;
    @BindView(R.id.tv_asset_des)
    TextView tv_asset_des;
    @BindView(R.id.tv_asset_more_info)
    TextView tv_asset_more_info;
    @BindView(R.id.iv_media_play)
    View iv_media_play;

    AssetTable mAssetTable;
    boolean mIsExistPlayList;
    UserTable mUser;
    List<String> mPlayList = new ArrayList<>();
    Disposable mAutoHideViewDisposable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_asset_details;
    }

    /**
     * start
     * The current page is started with the start() function
     *
     * @param context
     * @param assetId
     */
    public static void start(Context context, Long assetId) {
        Intent intent = new Intent(context, AssetDetailsActivity.class);
        intent.putExtra(ASSET_ID, assetId);
        ActivityUtils.startActivity(intent);
    }


    @Override
    protected void create() {
        EventBus.getDefault().register(this);
        Long asset_id = getIntent().getLongExtra(ASSET_ID, 0);
        if (asset_id == 0) {
            ToastUtils.showLong(R.string.assetid_invalid);
            back();
        } else {
            mAssetTable = DBUtils.getAsset(asset_id);
            if (null != mAssetTable) {
                initData();
            } else {
                ToastUtils.showLong(R.string.asset_no_exist);
                back();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoHideView();
    }

    /**
     * initData
     */
    private void initData() {
        mUser = DBUtils.getLoginUser();

        if (null != mUser.getPlayList()) {
            mPlayList.addAll(mUser.getPlayList());
        }
        MyUtils.loadImage(this, iv_image, mAssetTable.getAsset_image_original_url(), R.mipmap.ic_full_placeholder);
        //Expand more by default
        tv_asset_name.setText(MyUtils.getAssetName(mAssetTable));
        tv_asset_identifier.setText(getString(R.string.asset_identifier, mAssetTable.getAsset_token_id(), mAssetTable.getContract_address()));
        tv_asset_owner.setText(getString(R.string.asset_owner, MyUtils.getOwnerUserName(mUser)));
        tv_asset_creator.setText(getString(R.string.asset_creator, MyUtils.getCreatorUserName(mAssetTable)));
        if (TextUtils.isEmpty(mAssetTable.getAsset_description())) {
            tv_asset_des.setVisibility(View.GONE);
        } else {
            tv_asset_des.setText(getString(R.string.asset_description, mAssetTable.getAsset_description()));
        }
        //animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_more);
        tv_asset_more_info.startAnimation(animation);
//        LogUtils.d(mAssetTable.getAsset_animation_url());
        //Determine whether there are media files (video or audio)
        if (MyUtils.isVideo(mAssetTable) || MyUtils.isMusic(mAssetTable)) {
            iv_media_play.setVisibility(View.VISIBLE);
        }
        setPlayCheckState();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        LogUtils.d("PlayActivity onEvent", event.eventType);
        switch (event.eventType) {
            case MessageEvent.TYPE_GET_ASSETS_SUCCESS:
                assetsDataUpdate();
                break;
        }
    }


    /**
     * startAutoHideView
     */
    private void startAutoHideView() {
//        Anyway, it should always be shown first, right
        tv_asset_more_info.setVisibility(View.VISIBLE);
        stopAutoHideView();
        //Automatically hides after 5 seconds
        mAutoHideViewDisposable = Observable.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> {
                    tv_asset_more_info.setVisibility(View.GONE);
                });
    }

    /**
     * stopAutoHideView
     */
    private void stopAutoHideView() {
        if (null != mAutoHideViewDisposable && !mAutoHideViewDisposable.isDisposed()) {
            mAutoHideViewDisposable.dispose();
        }
    }


    /**
     * assetsDataUpdate
     */
    private void assetsDataUpdate() {
        mAssetTable = DBUtils.getAsset(getIntent().getLongExtra(ASSET_ID, 0));
        if (null == mAssetTable) {
            LogUtils.d("assetsDataUpdate asset delete exist!");
            ToastUtils.showLong(R.string.asset_no_exist);
            back();
        } else {
            LogUtils.d("assetsDataUpdate asset yes");
        }
    }


    /**
     * setPlayCheckState
     */
    private void setPlayCheckState() {
        mIsExistPlayList = mPlayList.contains(String.valueOf(mAssetTable.getAsset_id()));
        if (mIsExistPlayList) {
            tv_play_check.setText(getString(R.string.has_add));
            iv_play_check.setImageResource(R.mipmap.ic_love_yes);
            ll_play_check.setBackground(getDrawable(R.drawable.play_check_transparent_radius_16_selector));
            tv_play_check.setTextColor(ContextCompat.getColor(this, R.color.white));

        } else {
            tv_play_check.setText(getString(R.string.add_play_list));
            iv_play_check.setImageResource(R.mipmap.ic_love_no);
            ll_play_check.setBackground(getDrawable(R.drawable.play_check_white_radius_16_selector));
            tv_play_check.setTextColor(ContextCompat.getColor(this, R.color.text_black));
        }
    }

    /**
     * isShowInfo
     *
     * @return
     */
    private boolean isShowInfo() {
        return layout_asset_info.getVisibility() == View.VISIBLE;
    }

    /**
     * showInfo
     */
    private void showInfo() {
        layout_asset_info.setVisibility(View.VISIBLE);
        tv_asset_more_info.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.mipmap.ic_down_arrow));
        tv_asset_more_info.setText(getString(R.string.see_more_info_close));
    }

    /**
     * hideInfo
     */
    private void hideInfo() {
        layout_asset_info.setVisibility(View.GONE);
        tv_asset_more_info.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.mipmap.ic_up_arrow));
        tv_asset_more_info.setText(getString(R.string.see_more_info));
    }

    @Override
    protected boolean isAllowClickKey() {
        startAutoHideView();//Intercept button click here
        return super.isAllowClickKey();
    }

    @Override
    protected boolean clickKeyBack() {
        if (isShowInfo()) {
            hideInfo();
            return true;
        }
        return super.clickKeyBack();
    }

    @Override
    protected boolean clickKeyUp() {
        if (!isShowInfo()) {
            //The top key is clicked. If it is hidden, it is displayed. If it is displayed, it is not handled
            showInfo();
            return true;
        }
        return super.clickKeyUp();
    }

    @Override
    protected boolean clickKeyPlayPause() {
        return MyUtils.playMedia(this, mAssetTable, false);
    }

    @Override
    protected boolean clickKeyDown() {
        View currentFocus = getCurrentFocus();
        if (isShowInfo() && (null == currentFocus || currentFocus.getId() == R.id.ll_play_check || currentFocus.getId() == R.id.iv_media_play)) {
            //The lower key is clicked. If it is showing, it is hidden. If it is hiding, it is not handled
            hideInfo();
            return true;
        }
        return super.clickKeyDown();
    }

    @OnClick({R.id.ll_play_check, R.id.iv_media_play})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_play_check: //Add to playlist
                if (mIsExistPlayList) {
                    mPlayList.remove(String.valueOf(mAssetTable.getAsset_id()));
                } else {
                    mPlayList.add(String.valueOf(mAssetTable.getAsset_id()));
                }
                DBUtils.updatePlayList(mPlayList);
                setPlayCheckState();
                break;
            case R.id.iv_media_play: //Play the media file, if one exists
                MyUtils.playMedia(this, mAssetTable, false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (null != tv_asset_more_info) {
            tv_asset_more_info.clearAnimation();
        }
        super.onDestroy();
        stopAutoHideView();
        EventBus.getDefault().unregister(this);
    }
}
