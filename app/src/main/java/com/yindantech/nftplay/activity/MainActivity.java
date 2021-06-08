package com.yindantech.nftplay.activity;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.leanback.widget.VerticalGridView;
import androidx.leanback.widget.Visibility;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.rxjava.rxlife.RxLife;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.adapter.AssetBrowseAdapter;
import com.yindantech.nftplay.common.base.BaseActivity;
import com.yindantech.nftplay.common.event.MessageEvent;
import com.yindantech.nftplay.common.http.ErrorInfo;
import com.yindantech.nftplay.common.http.RequestCallback;
import com.yindantech.nftplay.model.db.DBUtils;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.model.db.table.ContractsTable;
import com.yindantech.nftplay.model.db.table.UserTable;
import com.yindantech.nftplay.common.utils.ApiUtils;
import com.yindantech.nftplay.common.utils.AppConstant;
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
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.yindantech.nftplay.common.utils.AppConstant.Key.ASSET_ID;
import static com.yindantech.nftplay.common.utils.AppConstant.Key.IS_RESTART;


/**
 * MainActivity
 * This is the main interface of the program, will load the current login user data, browse the display, you can enter the playback, edit, Settings and other menu options
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.layout_loading)
    View layout_loading;
    @BindView(R.id.tv_main_play)
    View tv_main_play;
    @BindView(R.id.tv_main_edit)
    View tv_main_edit;
    @BindView(R.id.tv_mian_refresh)
    View tv_mian_refresh;
    @BindView(R.id.tv_main_settings)
    View tv_main_settings;
    @BindView(R.id.tv_play_settings_select_all)
    TextView tv_play_settings_select_all;
    @BindView(R.id.layout_main_action_bar)
    View layout_main_action_bar;
    @BindView(R.id.layout_play_settings_actionbar)
    View layout_play_settings_actionbar;
    @BindView(R.id.verticalGridView)
    VerticalGridView mVerticalGridView;
    @BindView(R.id.frame_content)
    FrameLayout frame_content;
    View layout_loading_placeholder;

    UserTable mUser;
    AssetBrowseAdapter mAssetBrowseAdapter;
    Disposable mAutoRefreshDataDisposable;
    PlayIntervalSetDialog mPlayIntervalSetDialog;
    boolean mIsAllowClickKey = true;
    boolean mIsShowMaxLoadAssetCountTip;
    boolean mIsRestart;
    boolean mIsAutoLoading;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void create() {
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    /**
     * initView
     */
    private void initView() {
        mAssetBrowseAdapter = new AssetBrowseAdapter(new ArrayList<>(), (isSelectAll -> updateSelectAllUI(isSelectAll)));
        mVerticalGridView.setAdapter(mAssetBrowseAdapter);
        mVerticalGridView.setVerticalSpacing(SizeUtils.dp2px(20));
    }

    /**
     * initData
     */
    private void initData() {
        mIsRestart = getIntent().getBooleanExtra(IS_RESTART, false);
        mUser = DBUtils.getLoginUser();
        //Determine if there is data
        List<ContractsTable> contracts = DBUtils.getContracts(mUser.getAddress());

        if (!contracts.isEmpty()) {
            //Have the data, update the UI, and then initialize the playback
            updateUI(contracts, true);
            initPlay();
            autoRefreshData(true);
        } else {
            //No data, show placeholder, request data (no placeholder is needed if data is available locally)
            layout_loading_placeholder = View.inflate(this, R.layout.layout_loading_placeholder, null);
            frame_content.addView(layout_loading_placeholder);
            requestData();
            autoRefreshData(false);
        }
        reportLogin();
    }

    /**
     * reportLogin
     */
    private void reportLogin() {
        if (!mIsRestart)
            ApiUtils.login(mUser.getAddress());
    }

    /**
     * setPlayMenuVisibility
     *
     * @param visibilit
     */
    private void setPlayMenuVisibility(@Visibility int visibilit) {
        tv_main_play.setVisibility(visibilit);
        tv_main_edit.setVisibility(visibilit);

        //Prevent key pressing to lose focus when no data is available
        if (visibilit == View.VISIBLE) {
            tv_mian_refresh.setNextFocusDownId(R.id.verticalGridView);
            tv_main_settings.setNextFocusDownId(R.id.verticalGridView);
        } else {
            tv_mian_refresh.setNextFocusDownId(R.id.tv_mian_refresh);
            tv_main_settings.setNextFocusDownId(R.id.tv_main_settings);
        }
    }

    /**
     * updateUI
     */
    private void updateUI(List<ContractsTable> contractsTableList, boolean isScrollTop) {
        try {
            mAssetBrowseAdapter.setNewInstance(contractsTableList);
            if (contractsTableList.isEmpty()) {
                mAssetBrowseAdapter.setEmptyView(R.layout.item_home_no_data);
                setPlayMenuVisibility(View.GONE);
            } else {
                setPlayMenuVisibility(View.VISIBLE);
            }

            //If you update data manually, roll back to the top
            if (isScrollTop)
                mVerticalGridView.scrollToPosition(0);

            //Remove placeholder views
            if (null != layout_loading_placeholder) {
                frame_content.removeView(layout_loading_placeholder);
                layout_loading_placeholder = null;
            }

            //Determine that the data load exceeded the maximum limit
            if (mIsShowMaxLoadAssetCountTip && !contractsTableList.isEmpty()) {
                int assetsCount = 0;
                for (ContractsTable contractsTable : contractsTableList) {
                    assetsCount += contractsTable.getAssets().size();
                }
                if (assetsCount >= AppConstant.Config.MAX_LOAD_ASSET_COUNT) {
                    ToastUtils.showLong(getString(R.string.max_load_asset_count, AppConstant.Config.MAX_LOAD_ASSET_COUNT));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * initPlay
     */
    private void initPlay() {
        if (!mIsRestart) {
            if (mUser.isPlayList() && mUser.isPlayTime()) {
                //There is a play message and the time is set, then jump to the play page
                ActivityUtils.startActivity(AssetPlayActivity.class);
            } else {
                LogUtils.d("Main initPlay isRestart  no playlist or no time");
            }
        } else {
            LogUtils.d("Main initPlay no  isRestart=true");
        }
    }

    /**
     * reset
     */
    private void resetAutoRefreshData() {
        stopAutoRefreshData();
        autoRefreshData(false);
    }

    /**
     * stopAutoRefreshData
     */
    private void stopAutoRefreshData() {
        if (null != mAutoRefreshDataDisposable && !mAutoRefreshDataDisposable.isDisposed()) {
            mAutoRefreshDataDisposable.dispose();
            LogUtils.d("stop AutoRefreshData");
        }
    }

    /**
     * autoRefreshData
     * Automatic data refresh, the initial first time if there is a local data delay of 1 second, no data is 60 minutes (no data is requested through RequestData), subsequent every 60 minutes
     */
    private void autoRefreshData(boolean isInitial) {
        LogUtils.d("start AutoRefreshData isInitial", isInitial);
        mAutoRefreshDataDisposable = Observable
                .interval(isInitial ? 1000 : AppConstant.Config.AUTO_REFRESH_DATA_INTERVAL_TIME, AppConstant.Config.AUTO_REFRESH_DATA_INTERVAL_TIME, TimeUnit.MILLISECONDS)
                .to(RxLife.to(this)) //Automatically manage the lifecycle
                .subscribe((count) -> {
                    //Request data, send events, and other interfaces can be dynamically refreshed
                    LogUtils.d("autoRefreshData ing count = ", count);
                    ApiUtils.getAssets(this, mUser.getAddress());
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        LogUtils.d("MainActivity onEvent", event.eventType);
        switch (event.eventType) {
            case MessageEvent.TYPE_GET_ASSETS_SUCCESS:
                mIsAutoLoading = false;
                List<ContractsTable> contractsTableList = (List<ContractsTable>) event.value;
                LogUtils.d("onEvent contractsTableList", contractsTableList.size());
                updateUI(contractsTableList, false);
                hideLoading();
                break;
            case MessageEvent.TYPE_GET_ASSETS_START:
                mIsAutoLoading = true;
                break;
            case MessageEvent.TYPE_GET_ASSETS_ERROR:
                mIsAutoLoading = false;
                hideLoading();
                break;
        }
    }

    /**
     * updateSelectAllUI
     *
     * @param isSelectAll
     */
    private void updateSelectAllUI(boolean isSelectAll) {
        tv_play_settings_select_all.setText(isSelectAll ? getString(R.string.cancel_select_all) : getString(R.string.select_all));
        tv_play_settings_select_all.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(isSelectAll ? R.mipmap.ic_select_all_blue : R.mipmap.ic_select_all_white), null, null, null);
    }

    /**
     * showLoading
     */
    private void showLoading() {
        if (null != layout_loading) {
            layout_loading.setVisibility(View.VISIBLE);
        }
        mIsAllowClickKey = false;
        mIsShowMaxLoadAssetCountTip = true;
    }

    /**
     * hideLoading
     */
    private void hideLoading() {
        if (null != layout_loading) {
            layout_loading.setVisibility(View.GONE);
        }
        mIsAllowClickKey = true;
        mIsShowMaxLoadAssetCountTip = false;
    }


    /**
     * requestData
     */
    private void requestData() {
        ApiUtils.getAssets(this, mUser.getAddress(), new RequestCallback<ContractsTable>() {
            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(List<ContractsTable> dataList) {
                updateUI(dataList, true);
                hideLoading();
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                //Update the UI if you don't have data before
                hideLoading();
                if (mAssetBrowseAdapter.getData().isEmpty()) {
                    updateUI(new ArrayList<>(), false);
                }
                errorInfo.show();
            }
        });
    }


    /**
     * Play Settings menu item click
     *
     * @param view
     */
    @OnClick({R.id.tv_play_settings_select_all, R.id.tv_play_settings_player})
    public void onPlaySettingsMenuClick(View view) {
        switch (view.getId()) {
            case R.id.tv_play_settings_select_all://select all
                updateSelectAllUI(mAssetBrowseAdapter.selectAll());
                break;
            case R.id.tv_play_settings_player://Edit the play of the page
                if (mUser.isPlayList()) {
                    if (mUser.isPlayTime()) {
                        ActivityUtils.startActivity(AssetPlayActivity.class);
                        ThreadUtils.getMainHandler().postDelayed(() -> switchMode(), 500);//Switch back to browse mode (Delay to avoid seeing the switch first and then jumping to the page)
                    } else {
                        showPlayIntervalTimeDialog();
                    }
                } else {
                    ToastUtils.showLong(R.string.no_select_nft);
                }
                break;
        }
    }

    /**
     * Home menu item click
     *
     * @param view
     */
    @OnClick({R.id.tv_mian_refresh, R.id.tv_main_play, R.id.tv_main_settings, R.id.tv_main_edit})
    public void onMainMenuClick(View view) {
        if (!mIsAllowClickKey) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_main_edit://edit
                switchMode();
                break;
            case R.id.tv_mian_refresh://Refresh (If you are in the process of an automatic refresh request, you can simply display loading and wait for the automatic refresh to complete the hidden loading to update the UI.Rather than re-initiating the request, which can be large.)
                if (mIsAutoLoading) {
                    showLoading();
                } else {
                    requestData();
                    resetAutoRefreshData();
                }
                break;
            case R.id.tv_main_play://Home play button
                //If there is only one NFT, no need to edit and set the interval, just play
                List<AssetTable> assetList = DBUtils.getAssetList(mUser.getAddress());
                if (assetList.size() == 1) {
                    Intent intent = new Intent(MainActivity.this, AssetPlayActivity.class);
                    intent.putExtra(ASSET_ID, assetList.get(0).getAsset_id());
                    ActivityUtils.startActivity(intent);
                } else if (mUser.isPlayList()) {
                    //We have playback data
                    if (mUser.isPlayTime()) {
                        ActivityUtils.startActivity(AssetPlayActivity.class);
                    } else {
                        showPlayIntervalTimeDialog();
                    }
                } else {
                    //No playback data: Go directly to edit mode
                    switchMode();
                }
                break;

            case R.id.tv_main_settings://settings
                ActivityUtils.startActivity(SettingsActivity.class);
                break;

        }
    }

    /**
     * showPlayIntervalTimeDialog
     */
    private void showPlayIntervalTimeDialog() {
        if (null == mPlayIntervalSetDialog) {
            mPlayIntervalSetDialog = new PlayIntervalSetDialog(MainActivity.this);
            mPlayIntervalSetDialog.setOnDismissListener((dialog -> {
                if (DBUtils.getLoginUser().isPlayTime()) {
                    ActivityUtils.startActivity(AssetPlayActivity.class);
                    switchMode();
                }
            }));
        }
        mPlayIntervalSetDialog.show();
    }

    /**
     * Toggle mode (default browse mode, switch to edit mode)
     */
    private void switchMode() {
        mAssetBrowseAdapter.switchMode();
        if (mAssetBrowseAdapter.getIsEditorMode()) {
            layout_main_action_bar.setVisibility(View.GONE);
            layout_play_settings_actionbar.setVisibility(View.VISIBLE);
        } else {
            layout_play_settings_actionbar.setVisibility(View.GONE);
            layout_main_action_bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean clickKeyBack() {
        if (mAssetBrowseAdapter.getIsEditorMode()) {
            switchMode();
            return true;
        }
        MyUtils.back2ExitApp((v) -> ActivityUtils.finishActivity(this));
        return true;
    }

    @Override
    protected boolean clickKeyMenu() {
        ActivityUtils.startActivity(SettingsActivity.class);
        return true;
    }


    @Override
    protected boolean isAllowClickKey() {
        //Loading does not respond to a click while displayed
        return mIsAllowClickKey;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = DBUtils.getLoginUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopAutoRefreshData();
    }
}