package com.yindantech.nftplay.common.utils;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.rxjava.rxlife.RxLife;
import com.yindantech.nftplay.common.event.EventUtils;
import com.yindantech.nftplay.common.event.MessageEvent;
import com.yindantech.nftplay.common.http.OnError;
import com.yindantech.nftplay.common.http.RequestCallback;
import com.yindantech.nftplay.model.AssetsBean;
import com.yindantech.nftplay.model.LoginBean;
import com.yindantech.nftplay.model.db.DBUtils;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.model.db.table.CollectionsTable;
import com.yindantech.nftplay.model.db.table.ContractsTable;
import com.yindantech.nftplay.model.db.table.UserTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import rxhttp.RxHttp;

/**
 * ApiUtils
 */
public class ApiUtils {


    /**
     * Get all NFTs the wallet address （Receive data through Callback {@link RequestCallback}）
     *
     * @param context
     * @param address
     * @param callback
     */
    public static void getAssets(Context context, String address, RequestCallback<ContractsTable> callback) {
        getAssets(context, address, callback, false);
    }


    /**
     * Get all NFTs the wallet address  （Data is received through an Event {@link MessageEvent}）
     *
     * @param context
     * @param address
     */
    public static void getAssets(Context context, String address) {
        getAssets(context, address, null, true);
    }

    /**
     * Get all NFTs the wallet address  （You can choose to receive data through an event or callback, or both）
     *
     * @param context
     * @param address
     * @param callback
     * @param isPostEvent
     */
    public static void getAssets(Context context, String address, RequestCallback<ContractsTable> callback, boolean isPostEvent) {
        getAssets(context, address, 1, new ArrayList<>(), callback, isPostEvent);
    }

    /**
     * Get all NFTs the wallet address
     *
     * @param context
     * @param address
     * @param page     Start from 1
     * @param callback
     */
    private static void getAssets(Context context, String address, int page, List<AssetsBean.Asset> assetList, RequestCallback<ContractsTable> callback, boolean isPostEvent) {

        RxHttp.get(AppConstant.Config.OPENSEA_API_HOST + "/api/v1/assets")
                .addQuery("owner", address)
                .addQuery("order_direction", "desc")
                .addQuery("offset", (page - 1) * AppConstant.Config.ASSET_REQUEST_LIMIT)
                .addQuery("limit", AppConstant.Config.ASSET_REQUEST_LIMIT)
                .addHeader("X-API-KEY", "acee0e72b69142dfaa445fe5310d9d70")
                .asClass(AssetsBean.class)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    if (page == 1) {
//                        LogUtils.d("getAssets", "onStart");
                        if (isPostEvent)
                            EventUtils.post(MessageEvent.TYPE_GET_ASSETS_START);
                        if (null != callback)
                            callback.onStart();
                    }
                })
                .doFinally(() -> {
//                    LogUtils.d("getAssets", "doFinally");
                })
                .to(RxLife.toMain((LifecycleOwner) context))
                .subscribe(obj -> {
                    //Get data:
                    LogUtils.d("getAssets size or page", obj.getAssets().size(), page);
                    assetList.addAll(obj.getAssets());
                    if (obj.getAssets().size() >= AppConstant.Config.ASSET_REQUEST_LIMIT && assetList.size() < AppConstant.Config.MAX_LOAD_ASSET_COUNT) {
                        //Fetch the next page of data (the API may reject the request)
//                        ThreadUtils.getMainHandler().postDelayed(() -> {
                        getAssets(context, address, page + 1, assetList, callback, isPostEvent);
//                        }, 0);

                    } else {
                        //Retrieved all data or exceeded the maximum load amount
                        LogUtils.d("getAssets all data count", assetList.size());
                        //save db back
                        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<List<ContractsTable>>() {
                            @Override
                            public List<ContractsTable> doInBackground() {
                                return saveDB(assetList);
                            }

                            @Override
                            public void onSuccess(List<ContractsTable> result) {
                                if (null != callback)
                                    callback.onSuccess(result);
                                if (isPostEvent)
                                    EventUtils.post(MessageEvent.TYPE_GET_ASSETS_SUCCESS, result);
                            }
                        });
                    }

                }, (OnError) error -> {
                    //Failed callback, currently in the main thread callback
                    LogUtils.e("getAssets OnError", error.toString());
                    if (null != callback)
                        callback.onError(error);
                    if (isPostEvent)
                        EventUtils.post(MessageEvent.TYPE_GET_ASSETS_ERROR);
                });
    }


    /**
     * saveDB
     *
     * @param assetList
     * @return
     */
    private static List<ContractsTable> saveDB(List<AssetsBean.Asset> assetList) {

        //Convert data
        Map<String, CollectionsTable> collectionsTableMap = new HashMap<>();
        Map<String, ContractsTable> contractsTableMap = new HashMap<>();
        List<AssetTable> assetTableList = new ArrayList<>();

        UserTable loginUser = DBUtils.getLoginUser();
        if (null == loginUser) {
            return new ArrayList<>();
        }

        for (int i = 0; i < assetList.size(); i++) {
            AssetsBean.Asset asset = assetList.get(i);
            AssetTable assetTable = new AssetTable();
            //nft asset
            assetTable.setAsset_id(asset.getId());
            assetTable.setAsset_token_id(asset.getToken_id());
            assetTable.setAsset_image_url(asset.getImage_url());
            assetTable.setAsset_image_preview_url(asset.getImage_preview_url());
            assetTable.setAsset_image_thumbnail_url(asset.getImage_thumbnail_url());
            assetTable.setAsset_image_original_url(asset.getImage_original_url());
            assetTable.setAsset_name(asset.getName());
            assetTable.setAsset_description(asset.getDescription());
            assetTable.setAsset_external_link(asset.getExternal_link());
            assetTable.setAsset_animation_url(asset.getAnimation_url());
            assetTable.setAsset_animation_original_url(asset.getAnimation_original_url());
            //owner
            assetTable.setOwner_address(loginUser.getAddress());//Write yourself directly, may be no data on the chain
            //user update
            if (loginUser.getAddress().equals(asset.getOwner().getAddress()) && null != asset.getOwner().getUser()) {
                loginUser.setUsername(asset.getOwner().getUser().getUsername());
                loginUser.setProfile_img_url(asset.getOwner().getProfile_img_url());
            }

            //creator
            assetTable.setCreator_address(asset.getCreator().getAddress());
            assetTable.setCreator_profile_img_url(asset.getCreator().getProfile_img_url());
            if (null != asset.getCreator().getUser()) {
                assetTable.setCreator_username(asset.getCreator().getUser().getUsername());
            }
            //contract
            assetTable.setContract_address(asset.getAsset_contract().getAddress());
            //Disposal contract table
            ContractsTable contractsTable = new ContractsTable();
            contractsTable.setAddress(asset.getAsset_contract().getAddress());
            contractsTable.setAsset_contract_type(asset.getAsset_contract().getAsset_contract_type());
            contractsTable.setCreated_date(asset.getAsset_contract().getCreated_date());
            contractsTable.setCreated_time(TimeUtils.string2Millis(contractsTable.getCreated_date(), "yyyy-MM-dd'T'HH:mm:ss.SSS"));
            contractsTable.setDescription(asset.getAsset_contract().getDescription());
            contractsTable.setName(asset.getAsset_contract().getName());
            contractsTable.setSchema_name(asset.getAsset_contract().getSchema_name());
            contractsTable.setSymbol(asset.getAsset_contract().getSymbol());
            contractsTable.setExternal_link(asset.getAsset_contract().getExternal_link());
            contractsTable.setImage_url(asset.getAsset_contract().getImage_url());
            contractsTable.setOwner_address(loginUser.getAddress());//直接标记为所属用户

            //collection
            assetTable.setCollection_name(asset.getCollection().getName());
            //Disposal collection table
            CollectionsTable collectionsTable = new CollectionsTable();
            collectionsTable.setName(asset.getCollection().getName());
            collectionsTable.setImage_url(asset.getCollection().getImage_url());
            collectionsTable.setCreated_date(asset.getCollection().getCreated_date());
            collectionsTable.setCreated_time(TimeUtils.string2Millis(collectionsTable.getCreated_date(), "yyyy-MM-dd'T'HH:mm:ss.SSS"));
            collectionsTable.setOwner_address(loginUser.getAddress());

            //add
            contractsTableMap.put(contractsTable.getAddress(), contractsTable);
            collectionsTableMap.put(collectionsTable.getName(), collectionsTable);
            assetTableList.add(assetTable);
        }
        //save db
        DBUtils.saveUser(loginUser);
        DBUtils.updateData(contractsTableMap, collectionsTableMap, assetTableList);
        return DBUtils.getContracts(DBUtils.getLoginUser().getAddress());
    }


    /**
     * login
     *
     * @param address
     */
    public static void login(String address) {

        String manufacturer = DeviceUtils.getManufacturer();
        String model = DeviceUtils.getModel();
        int sdkVersionCode = DeviceUtils.getSDKVersionCode();
        String sdkVersionName = DeviceUtils.getSDKVersionName();
        int appVersionCode = AppUtils.getAppVersionCode();
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        int screenDensityDpi = ScreenUtils.getScreenDensityDpi();

        String channel = MyUtils.getAppChannel();
        LoginBean loginBean = new LoginBean(address, manufacturer, model, sdkVersionCode, sdkVersionName, appVersionCode, screenWidth, screenHeight, screenDensityDpi, channel);
        String loginJsonStr = JSON.toJSONString(loginBean);

//        LogUtils.d("loginJsonStr", loginJsonStr);

        RxHttp.postJson(AppConstant.Config.LOGIN_API)
                .addAll(loginJsonStr)
                .asString()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    LogUtils.d("start login");
                })
                .doFinally(() -> {
                    LogUtils.d("end login");
                })
                .subscribe(s -> {
                    LogUtils.d("success login", s);
                }, (OnError) error -> {
                    LogUtils.d("error login", error.getErrorMsg());
                });
    }
}
