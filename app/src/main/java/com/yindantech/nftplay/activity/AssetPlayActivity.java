package com.yindantech.nftplay.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.rxjava.rxlife.RxLife;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.adapter.AssetPlayAdapter;
import com.yindantech.nftplay.common.base.BaseActivity;
import com.yindantech.nftplay.common.event.MessageEvent;
import com.yindantech.nftplay.common.utils.AppConstant;
import com.yindantech.nftplay.model.db.DBUtils;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.presenter.AssetPlayPreviewPresenter;
import com.yindantech.nftplay.common.utils.MyUtils;
import com.yindantech.nftplay.view.PlayIntervalSetDialog;

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
import static com.yindantech.nftplay.common.utils.AppConstant.Key.IS_AUTO_BACK;

/**
 * AssetPlayActivity
 * Play the selected asset data, you can set the playing time, the confirmation key can be locked, the menu key can view the menu
 */
public class AssetPlayActivity extends BaseActivity {

    @BindView(R.id.recyclerView_play)
    RecyclerView mRecyclerViewPlay;
    @BindView(R.id.rl_play_action_bar)
    RelativeLayout rl_play_action_bar;
    @BindView(R.id.tv_play_settings)
    View tv_play_settings;
    @BindView(R.id.iv_lock)
    View iv_lock;
    @BindView(R.id.horizontalGridView)
    HorizontalGridView mHorizontalGridView;
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
    @BindView(R.id.ll_info_action)
    View ll_info_action;
    @BindView(R.id.tv_asset_more_info)
    TextView tv_asset_more_info;
    @BindView(R.id.layout_play_key_tip)
    View layout_play_key_tip;
    @BindView(R.id.scrollView)
    View scrollView;

    PlayIntervalSetDialog mPlayIntervalSetDialog;
    AssetPlayAdapter mAssetPlayAdapter;
    ItemBridgeAdapter mAssetPreviewAdapter;
    List<AssetTable> mAssetList = new ArrayList<>();
    int mPlayIndex = 0;
    Disposable mAutoStartPlayDisposable;
    Disposable mAutoHideViewDisposable;
    boolean isLock = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }


    @Override
    protected void create() {
        EventBus.getDefault().register(this);

        long assetId = getIntent().getLongExtra(ASSET_ID, 0);
        if (assetId == 0) {
            List<String> playList = DBUtils.getLoginUser().getPlayList();
            mAssetList = DBUtils.getAssetList(playList);
        } else {
            mAssetList.add(DBUtils.getAsset(assetId));
        }
//        LogUtils.d("playlist", mAssetList);
        if (!mAssetList.isEmpty()) {
            initView();
            initPlay();
        } else {
            ToastUtils.showLong(R.string.play_data_no_exist);
            DBUtils.updatePlayList(null);
            back();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoHideView();
    }

    /**
     * initView
     */
    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerViewPlay.setLayoutManager(linearLayoutManager);
        mAssetPlayAdapter = new AssetPlayAdapter(mAssetList);
        mRecyclerViewPlay.setAdapter(mAssetPlayAdapter);
        //preview
        mHorizontalGridView.setHorizontalSpacing(SizeUtils.dp2px(40));
        AssetPlayPreviewPresenter assetPlayPreviewPresenter = new AssetPlayPreviewPresenter();
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(assetPlayPreviewPresenter);
        arrayObjectAdapter.addAll(0, mAssetList);
        mAssetPreviewAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        //itemClick
        mAssetPreviewAdapter.setAdapterListener(new ItemBridgeAdapter.AdapterListener() {
            @Override
            public void onCreate(ItemBridgeAdapter.ViewHolder viewHolder) {
                super.onCreate(viewHolder);

                viewHolder.itemView.setOnFocusChangeListener((view, hasFocus) -> {
                    if (hasFocus) {
                        int position = viewHolder.getAdapterPosition();
                        mRecyclerViewPlay.scrollToPosition(position);
                        mPlayIndex = position;
                        setAssetInfo();
                    }
                });
                viewHolder.itemView.setOnClickListener(v -> MyUtils.playMedia(AssetPlayActivity.this, getCurrentAsset(), false));
            }
        });
        mHorizontalGridView.setAdapter(mAssetPreviewAdapter);
        FocusHighlightHelper.setupHeaderItemFocusHighlight(mAssetPreviewAdapter);
        //asset info
        tv_asset_owner.setText(getString(R.string.asset_owner, MyUtils.getOwnerUserName(DBUtils.getLoginUser())));
        ll_info_action.setFocusable(false);
        ll_info_action.setVisibility(View.INVISIBLE);
        layout_asset_info.setVisibility(View.GONE);
        tv_asset_more_info.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.mipmap.ic_up_arrow));
        tv_asset_more_info.setText(getString(R.string.see_more_info));
        //animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_more);
        tv_asset_more_info.startAnimation(animation);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) scrollView.getLayoutParams();
        layoutParams.bottomMargin = 0;
        scrollView.setLayoutParams(layoutParams);

        MyUtils.showKeyTipView(layout_play_key_tip, 3000);
    }

    /**
     * initPlay
     */
    private void initPlay() {
        //The default is to start playing
        startAutoPlay();
        setAssetInfo();
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
     * startAutoHideView
     */
    private void startAutoHideView() {
//        Anyway, it should always be shown first, right
        if (isLock) {
            iv_lock.setVisibility(View.VISIBLE);
        }
        tv_asset_more_info.setVisibility(View.VISIBLE);
        stopAutoHideView();
        //Automatically hides after 5 seconds
        mAutoHideViewDisposable = Observable.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> {
                    iv_lock.setVisibility(View.GONE);
                    tv_asset_more_info.setVisibility(View.GONE);
                });
    }


    /**
     * setAssetInfo
     */
    private void setAssetInfo() {
        AssetTable assetTable = mAssetList.get(mPlayIndex);
        tv_asset_name.setText(MyUtils.getAssetName(assetTable));
        tv_asset_identifier.setText(getString(R.string.asset_identifier, assetTable.getAsset_token_id(), assetTable.getContract_address()));
        tv_asset_creator.setText(getString(R.string.asset_creator, MyUtils.getCreatorUserName(assetTable)));

        if (TextUtils.isEmpty(assetTable.getAsset_description())) {
            tv_asset_des.setVisibility(View.GONE);
        } else {
            tv_asset_des.setVisibility(View.VISIBLE);
            tv_asset_des.setText(getString(R.string.asset_description, MyUtils.formatStrNotNull((assetTable.getAsset_description()))));
        }
    }


    /**
     * startAutoPlay
     */
    private void startAutoPlay() {
        if (isLock || isShowMenu()) { //Do not start playing while locked
            return;
        }
        stopAutoPlay();//Stop for a while before you start
        LogUtils.d("startAutoPlay");
        long playIntervalTime = DBUtils.getLoginUser().getPlayIntervalTime();
        if (playIntervalTime <= 0) {
            playIntervalTime = 1000 * 10;
        }
        mAutoStartPlayDisposable = Observable
                .interval(playIntervalTime, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLife.toMain(this))
                .subscribe((count) -> itemNext());
    }

    /**
     * stopAutoPlay
     */
    private void stopAutoPlay() {
        if (null != mAutoStartPlayDisposable && !mAutoStartPlayDisposable.isDisposed()) {
            mAutoStartPlayDisposable.dispose();
            LogUtils.d("stopAutoPlay");
        }
    }


    @OnClick({R.id.tv_play_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_play_settings:
                showPlayIntervalTimeDialog();
                break;
        }
    }


    /**
     * showPlayIntervalTimeDialog
     */
    private void showPlayIntervalTimeDialog() {
        tv_play_settings.clearFocus();
        if (null == mPlayIntervalSetDialog) {
            mPlayIntervalSetDialog = new PlayIntervalSetDialog(AssetPlayActivity.this);
            mPlayIntervalSetDialog.setOnDismissListener((dialog -> updatePlayInterval()));
        }
        mPlayIntervalSetDialog.show();
    }

    /**
     * updatePlayInterval
     */
    private void updatePlayInterval() {
        startAutoPlay();//Simply reboot
        tv_play_settings.requestFocus();
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
     * Asset data update
     * Handle playlists
     * assetsDataUpdate
     */
    private void assetsDataUpdate() {
        //The data is loaded successfully, and the playback information is judged
        List<String> newPlayList = DBUtils.getLoginUser().getPlayList();
        if (null == newPlayList || newPlayList.isEmpty()) {
            //Close the page without playing data
            LogUtils.d("assetsDataUpdate no playList data , back");
            ToastUtils.showLong(R.string.asset_no_exist);
            back();
        } else {
            //Determine whether the playback data is updated.Reset if what is currently playing does not exist in the new data
            boolean isReStart = false;
            for (AssetTable asset : mAssetList) {
                if (!newPlayList.contains(String.valueOf(asset.getAsset_id()))) {
                    isReStart = true;
                    break;
                }
            }
            if (isReStart) {
                LogUtils.d("assetsDataUpdate playList update reStart Activity!");
                ToastUtils.showLong(R.string.asset_data_update);
                ActivityUtils.startActivity(AssetPlayActivity.class);
                ActivityUtils.finishActivity(this);
            } else {
                LogUtils.d("assetsDataUpdate playList no update , no reStart Activity");
            }
        }
    }

    @Override
    protected boolean isAllowClickKey() {
        startAutoHideView();//Intercept button click here
        return super.isAllowClickKey();
    }

    @Override
    protected boolean clickKeyMenu() {
        if (isShowMenu()) {
            //Close the menu
            rl_play_action_bar.setVisibility(View.GONE);
            mHorizontalGridView.setVisibility(View.GONE);
            rl_play_action_bar.clearFocus();
            startAutoPlay();
            if (isShowInfo()) {
                scrollView.setFocusable(true);
                scrollView.requestFocus();
            }
        } else {
            //According to the menu
            rl_play_action_bar.setVisibility(View.VISIBLE);
            mHorizontalGridView.setVisibility(View.VISIBLE);
            mHorizontalGridView.requestFocus();
            stopAutoPlay();
            if (isShowInfo()) {
                scrollView.setFocusable(false);
            }
        }
        return true;
    }

    @Override
    protected boolean clickKeyBack() {
        if (isShowMenu()) {
            clickKeyMenu();
        } else if (isShowInfo()) {
            hideInfo();
        } else {
            back();
        }
        return true;
    }


    /**
     * Item switches to the next item, or back to the first item if not
     */
    private void itemNext() {
        mPlayIndex++;
        if (mPlayIndex <= mAssetList.size() - 1) {
            mRecyclerViewPlay.smoothScrollToPosition(mPlayIndex);
        } else {
            mPlayIndex = 0;
            mRecyclerViewPlay.scrollToPosition(0);
        }
        mHorizontalGridView.setSelectedPosition(mPlayIndex);
        setAssetInfo();
    }


    /**
     * Item Returns the previous one, or goes to the last one if there is none
     */
    private void itemBack() {
        mPlayIndex--;
        if (mPlayIndex >= 0 && mPlayIndex <= mAssetList.size() - 1) {
            mRecyclerViewPlay.smoothScrollToPosition(mPlayIndex);
        } else {
            mPlayIndex = mAssetList.size() - 1;
            mRecyclerViewPlay.scrollToPosition(mPlayIndex);
        }
        mHorizontalGridView.setSelectedPosition(mPlayIndex);
        setAssetInfo();
    }


    /**
     * getCurrentAsset
     *
     * @return
     */
    private AssetTable getCurrentAsset() {
        return mAssetList.get(mPlayIndex);
    }

    @Override
    protected boolean clickKeyLeft() {
      /*  if (!isShowMenu() && !isLock) {
            //If no menu is displayed, go to previous page: stop first, go to previous page, continue again
            stopAutoPlay();
            itemBack(false);
            startAutoPlay();
            return true;
        }*/
        return super.clickKeyLeft();
    }

    @Override
    protected boolean clickKeyRight() {
    /*    if (!isShowMenu() && !isLock) {
            //Go to the next page if no menu is displayed: stop first, go to the next page, and continue
            stopAutoPlay();
            itemNext(false);
            startAutoPlay();
            return true;
        }*/
        return super.clickKeyRight();
    }

    @Override
    protected boolean clickKeyUp() {
        if (!isShowMenu() && !isShowInfo()) {
            //The top key is clicked. If it is hidden, it is displayed. If it is displayed, it is not handled
            showInfo();
            return true;
        }
        return super.clickKeyUp();
    }

    @Override
    protected boolean clickKeyDown() {
        if (!isShowMenu() && isShowInfo()) {
            //The lower key is clicked. If it is showing, it is hidden. If it is hiding, it is not handled
            hideInfo();
            return true;
        }
        return super.clickKeyDown();
    }


    @Override
    protected boolean clickKeyEnter() {
        if (!isShowMenu()) {
            isLock = !isLock;
            if (isLock) {
                //lock
                stopAutoPlay();
                iv_lock.setVisibility(View.VISIBLE);
            } else {
                //unlock
                startAutoPlay();
                iv_lock.setVisibility(View.GONE);
            }
            return true;
        }
        return super.clickKeyEnter();
    }

    @Override
    protected boolean clickKeyNext() {
        return clickKeyRight();
    }

    @Override
    protected boolean clickKeyPrevious() {
        return clickKeyLeft();
    }

    @Override
    protected boolean clickKeyPlayPause() {
        return clickKeyEnter();
    }

    /**
     * isShowMenu
     *
     * @return
     */
    private boolean isShowMenu() {
        return rl_play_action_bar.getVisibility() == View.VISIBLE;
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
        scrollView.requestFocus();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AppConstant.Code.PLAY_REQUEST_CODE:
                    startAutoPlay();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        if (null != layout_play_key_tip)
            layout_play_key_tip.clearAnimation();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopAutoPlay();
        stopAutoHideView();
    }
}
