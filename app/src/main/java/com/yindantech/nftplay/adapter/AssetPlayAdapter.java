package com.yindantech.nftplay.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.common.utils.MyUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * AssetPlayAdapter
 */
public class AssetPlayAdapter extends BaseQuickAdapter<AssetTable, BaseViewHolder> {
    public AssetPlayAdapter(@Nullable List<AssetTable> data) {
        super(R.layout.item_asset_play, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder viewHolder, AssetTable item) {
        MyUtils.loadImage(getContext(), viewHolder.getView(R.id.iv_image), item.getAsset_image_url(), R.mipmap.ic_full_placeholder);
    }
}
