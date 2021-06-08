package com.yindantech.nftplay.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.yindantech.nftplay.R;
import com.yindantech.nftplay.adapter.AssetBrowseAdapter;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.common.utils.MyUtils;

/**
 * AssetBrowsePresenter
 */
public class AssetBrowsePresenter extends Presenter {

    AssetBrowseAdapter mAssetBrowseAdapter;
    OnAssetClickListener mClickListener;

    /**
     * AssetBrowsePresenter
     *
     * @param assetBrowseAdapter
     */
    public AssetBrowsePresenter(AssetBrowseAdapter assetBrowseAdapter) {
        this.mAssetBrowseAdapter = assetBrowseAdapter;

    }

    /**
     * setClickListener
     *
     * @param clickListener
     */
    public void setClickListener(OnAssetClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset_browse, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        AssetTable asset = (AssetTable) item;
        MyViewHolder vh = (MyViewHolder) viewHolder;
        vh.tv_name.setText(MyUtils.getAssetName(asset));
        vh.cb_check.setVisibility(mAssetBrowseAdapter.getIsEditorMode() ? View.VISIBLE : View.GONE);
        vh.cb_check.setChecked(mAssetBrowseAdapter.getSelectList().contains(String.valueOf(asset.getAsset_id())));
        vh.imageView.setAlpha(mAssetBrowseAdapter.getIsEditorMode() ? 0.6f : 1f);
        vh.iv_media_type.setAlpha(mAssetBrowseAdapter.getIsEditorMode() ? 0.8f : 1f);
        if (MyUtils.isVideo(asset)) {
            vh.iv_media_type.setImageResource(R.mipmap.ic_video);
            vh.iv_media_type.setVisibility(View.VISIBLE);
        } else if (MyUtils.isMusic(asset)) {
            vh.iv_media_type.setImageResource(R.mipmap.ic_music);
            vh.iv_media_type.setVisibility(View.VISIBLE);
        } else {
            vh.iv_media_type.setVisibility(View.GONE);
        }

        MyUtils.loadImage(vh.imageView.getContext(), vh.imageView, asset.getAsset_image_preview_url(), R.mipmap.ic_preview_placeholder);
        vh.frame_asset_box.setOnClickListener(v -> mClickListener.onClick(asset));
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }

    /**
     * MyViewHolder
     */
    static class MyViewHolder extends ViewHolder {
        ImageView imageView;
        ImageView iv_media_type;
        CheckBox cb_check;
        TextView tv_name;
        View frame_asset_box;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_image);
            iv_media_type = view.findViewById(R.id.iv_media_type);
            cb_check = view.findViewById(R.id.cb_check);
            tv_name = view.findViewById(R.id.tv_name);
            frame_asset_box = view.findViewById(R.id.frame_asset_box);
        }
    }

    /**
     * interface
     */
    public interface OnAssetClickListener {
        void onClick(AssetTable asset);
    }
}
