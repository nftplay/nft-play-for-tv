package com.yindantech.nftplay.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.Presenter;

import com.yindantech.nftplay.R;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.common.utils.MyUtils;

/**
 * AssetPlayPreviewPresenter
 */
public class AssetPlayPreviewPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset_play_preview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        AssetTable asset = (AssetTable) item;
        MyViewHolder vh = (MyViewHolder) viewHolder;

        MyUtils.loadImage(vh.imageView.getContext(), vh.imageView, asset.getAsset_image_url(), R.mipmap.ic_preview_placeholder);

        if (MyUtils.isVideo(asset)) {
            vh.iv_media_type.setImageResource(R.mipmap.ic_video);
            vh.iv_media_type.setVisibility(View.VISIBLE);
        } else if (MyUtils.isMusic(asset)) {
            vh.iv_media_type.setImageResource(R.mipmap.ic_music);
            vh.iv_media_type.setVisibility(View.VISIBLE);
        } else {
            vh.iv_media_type.setVisibility(View.GONE);
        }

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }

    /**
     * MyViewHolder
     */
    static class MyViewHolder extends Presenter.ViewHolder {
        ImageView imageView;
        ImageView iv_media_type;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_image);
            iv_media_type = view.findViewById(R.id.iv_media_type);
        }
    }
}
