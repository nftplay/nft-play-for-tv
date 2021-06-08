package com.yindantech.nftplay.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.app.PlaybackSupportFragment;
import androidx.leanback.app.PlaybackSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackGlue;
import androidx.leanback.widget.PlaybackControlsRow;
import androidx.leanback.widget.PlaybackSeekDataProvider;

import com.blankj.utilcode.util.LogUtils;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.model.db.DBUtils;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.common.utils.MyUtils;
import com.yindantech.nftplay.presenter.MediaPlayerControlGlue;

import static com.yindantech.nftplay.common.utils.AppConstant.Key.ASSET_ID;
import static com.yindantech.nftplay.common.utils.AppConstant.Key.IS_AUTO_BACK;

/**
 * MusicPlayFragment
 */
public class MusicPlayFragment extends PlaybackSupportFragment {

    MediaPlayerControlGlue<MediaPlayerAdapter> mTransportControlGlue;
    AssetTable mAssetTable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //add preview bg image
        ViewGroup root = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        ImageView imageView = new ImageView(getActivity());
        root.addView(imageView, 0);
        MyUtils.loadImage(getActivity(), imageView, mAssetTable.getAsset_image_original_url(), R.mipmap.ic_full_placeholder);
        setBackgroundType(PlaybackSupportFragment.BG_LIGHT);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAssetTable = DBUtils.getAsset(getActivity().getIntent().getLongExtra(ASSET_ID, 0));

        MediaPlayerAdapter playerAdapter = new MediaPlayerAdapter(getActivity());
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_ONE);

        mTransportControlGlue = new MediaPlayerControlGlue(getActivity(), playerAdapter);
        mTransportControlGlue.setHost(new PlaybackSupportFragmentGlueHost(this));
        mTransportControlGlue.setTitle(MyUtils.getAssetName(mAssetTable));
        mTransportControlGlue.setSubtitle(mAssetTable.getAsset_description());
//        mTransportControlGlue.setSeekProvider(new PlaybackSeekDataProvider());
        mTransportControlGlue.setControlsOverlayAutoHideEnabled(false);
//        mTransportControlGlue.playWhenPrepared();
        playerAdapter.setDataSource(Uri.parse(mAssetTable.getAsset_animation_url()));

        //music Repeat
        mTransportControlGlue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
            @Override
            public void onPreparedStateChanged(PlaybackGlue glue) {
                if (glue.isPrepared() && null != getActivity() && !getActivity().isFinishing()) {
                    glue.play();
                }
            }

            @Override
            public void onPlayCompleted(PlaybackGlue glue) {
                if (getActivity().getIntent().getBooleanExtra(IS_AUTO_BACK, false)) {
                    getActivity().setResult(Activity.RESULT_OK, getActivity().getIntent());
                    getActivity().finish();
                } else {
                    glue.play();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();
        }
    }
}
