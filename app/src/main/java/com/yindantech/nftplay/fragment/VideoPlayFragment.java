package com.yindantech.nftplay.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.app.PlaybackSupportFragment;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackGlue;
import androidx.leanback.widget.PlaybackControlsRow;
import androidx.leanback.widget.PlaybackSeekDataProvider;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.model.db.DBUtils;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.common.utils.MyUtils;
import com.yindantech.nftplay.presenter.MediaPlayerControlGlue;

import static com.yindantech.nftplay.common.utils.AppConstant.Key.ASSET_ID;
import static com.yindantech.nftplay.common.utils.AppConstant.Key.IS_AUTO_BACK;

/**
 * VideoPlayFragment
 */
public class VideoPlayFragment extends VideoSupportFragment {

    MediaPlayerControlGlue<MediaPlayerAdapter> mTransportControlGlue;
    AssetTable mAssetTable;
    ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //add preview bg image
        ViewGroup root = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        mImageView = new ImageView(getActivity());
        root.addView(mImageView, 1);
        MyUtils.loadImage(getActivity(), mImageView, mAssetTable.getAsset_image_original_url(), R.mipmap.ic_full_placeholder);
        setBackgroundType(PlaybackSupportFragment.BG_LIGHT);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAssetTable = DBUtils.getAsset(getActivity().getIntent().getLongExtra(ASSET_ID, 0));

        MediaPlayerAdapter playerAdapter = new MediaPlayerAdapter(getActivity());
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);

        mTransportControlGlue = new MediaPlayerControlGlue<>(getActivity(), playerAdapter);
        mTransportControlGlue.setHost(new VideoSupportFragmentGlueHost(VideoPlayFragment.this));
        mTransportControlGlue.setTitle(MyUtils.getAssetName(mAssetTable));
        mTransportControlGlue.setSubtitle(mAssetTable.getAsset_description());
        mTransportControlGlue.setSeekProvider(new PlaybackSeekDataProvider());
//        mTransportControlGlue.playWhenPrepared();
        playerAdapter.setDataSource(Uri.parse(mAssetTable.getAsset_animation_url()));

        mTransportControlGlue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
            @Override
            public void onPreparedStateChanged(PlaybackGlue glue) {
                if (glue.isPrepared() && null != getActivity() && !getActivity().isFinishing()) {
                    glue.play();
                    ThreadUtils.getMainHandler().postDelayed(() ->
                    {
                        if (null != mImageView)
                            mImageView.setVisibility(View.GONE);
                    }, 300);
                }
            }

            @Override
            public void onPlayCompleted(PlaybackGlue glue) {
                LogUtils.d("video onPlayCompleted");
                if (getActivity().getIntent().getBooleanExtra(IS_AUTO_BACK, false)) {
                    getActivity().setResult(Activity.RESULT_OK, getActivity().getIntent());
                    getActivity().finish();
                } else {
//                    glue.play();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //add preview bg image  :The SurfaceView will be full screen by default, so if the image is not full screen, the image will be stretched. If the SurfaceView is not full screen, the image will be stretched, so the SurfaceView will be addImage, and the image will be hidden when it is ready to play
/*
        if (null != getSurfaceView() && null == getSurfaceView().getBackground()) {
            Glide
                    .with(getActivity())
                    .load(mAssetTable.getAsset_image_url())
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            getSurfaceView().setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
*/
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();
        }
    }
}
