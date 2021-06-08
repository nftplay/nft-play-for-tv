package com.yindantech.nftplay.adapter;


import android.text.TextUtils;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.activity.AssetDetailsActivity;
import com.yindantech.nftplay.model.db.DBUtils;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.model.db.table.ContractsTable;
import com.yindantech.nftplay.presenter.AssetBrowsePresenter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * AssetBrowseAdapter
 */
public class AssetBrowseAdapter extends BaseQuickAdapter<ContractsTable, BaseViewHolder> {

    boolean mIsAllSelect = false;
    boolean mIsEditMode = false;
    List<String> mSelectList = new ArrayList<>();
    OnSelectAllListener mOnSelectAllListener;

    /**
     * AssetBrowseAdapter
     *
     * @param data
     * @param listener
     */
    public AssetBrowseAdapter(@Nullable List<ContractsTable> data, OnSelectAllListener listener) {
        super(R.layout.item_contract_browse, data);
        this.mOnSelectAllListener = listener;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder viewHolder, ContractsTable item) {

        String text = String.format("%s (%d)", item.getName(), item.getAssets().size());
        viewHolder.setText(R.id.tv_head_title, text);

        HorizontalGridView mHorizontalGridView = viewHolder.getView(R.id.horizontalGridView);
        mHorizontalGridView.setHorizontalSpacing(SizeUtils.dp2px(20));
        AssetBrowsePresenter previewPresenter = new AssetBrowsePresenter(this);
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(previewPresenter);
        List<AssetTable> assetsList = DBUtils.getAssetsByContract(item.getAddress());
        arrayObjectAdapter.addAll(0, assetsList);
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);

        previewPresenter.setClickListener((asset -> {
            if (getIsEditorMode()) {
                select(String.valueOf(asset.getAsset_id()));
                itemBridgeAdapter.notifyItemChanged(assetsList.indexOf(asset));
            } else {
                AssetDetailsActivity.start(getContext(), asset.getAsset_id());
            }
        }));

        mHorizontalGridView.setAdapter(itemBridgeAdapter);
    }


    /**
     * initSelect
     */
    private void initSelect() {
        //Echoes selected data
        mSelectList.clear();
        List<String> playList = DBUtils.getLoginUser().getPlayList();
        if (null != playList && !playList.isEmpty()) {
            for (String assetId : playList) {
                if (!TextUtils.isEmpty(assetId))
                    mSelectList.add(assetId);
            }
            updateSelectAll(checkIsSelectAll());
        }
    }

    /**
     * select
     *
     * @param assetId
     */
    private void select(String assetId) {
        if (TextUtils.isEmpty(assetId)) {
            return;
        }
        if (mSelectList.contains(assetId)) {
            mSelectList.remove(assetId);
        } else {
            mSelectList.add(assetId);
        }
        //save db
        DBUtils.updatePlayList(mSelectList);

        updateSelectAll(checkIsSelectAll());
    }

    /**
     * Determine whether all are selected
     *
     * @return
     */
    private boolean checkIsSelectAll() {
        List<String> playList = DBUtils.getLoginUser().getPlayList();
        List<AssetTable> assetList = DBUtils.getAssetList(DBUtils.getLoginUser().getAddress());
        mIsAllSelect = playList.size() >= assetList.size();
        return mIsAllSelect;
    }

    /**
     * Update all
     *
     * @param isAllSelect
     */
    private void updateSelectAll(boolean isAllSelect) {
        mOnSelectAllListener.onSelectAll(isAllSelect);
    }

    /**
     * getCheckList
     *
     * @return
     */
    public List<String> getSelectList() {
        return mSelectList;
    }

    /**
     * getIsEditorMode
     *
     * @return
     */
    public boolean getIsEditorMode() {
        return mIsEditMode;
    }


    /**
     * switchMode
     */
    public void switchMode() {
        if (!mIsEditMode)
            initSelect();//Switch to edit mode before echo data
        this.mIsEditMode = !mIsEditMode;
        notifyDataSetChanged();
    }


    /**
     * Select all, deselect all
     */
    public boolean selectAll() {
        this.mIsAllSelect = !mIsAllSelect;
        if (mIsAllSelect) {
            //select all
            mSelectList.clear(); //Clear first, then add in order
            for (ContractsTable contractsTable : getData()) {
                List<AssetTable> assets = DBUtils.getAssetsByContract(contractsTable.getAddress());
                for (AssetTable assetTable : assets) {
                    String assetId = String.valueOf(assetTable.getAsset_id());
                    if (!mSelectList.contains(assetId)) {
                        mSelectList.add(assetId);
                    }
                }
            }
        } else {
            //To cancel all
            mSelectList.clear();
        }

        updateSelectAll(mIsAllSelect);
        notifyDataSetChanged();
        //save db
        DBUtils.updatePlayList(mSelectList);
        return mIsAllSelect;
    }

    /**
     * OnSelectAllListener
     */
    public interface OnSelectAllListener {
        void onSelectAll(boolean isSelectAll);
    }
}
