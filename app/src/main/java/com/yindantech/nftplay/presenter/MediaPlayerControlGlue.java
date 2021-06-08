package com.yindantech.nftplay.presenter;

import android.content.Context;

import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackBaseControlGlue;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.PlaybackRowPresenter;
import androidx.leanback.widget.PlaybackTransportRowPresenter;
import androidx.leanback.widget.RowPresenter;

/**
 * MediaPlayerControlGlue
 * I need to customize the title, so I have this class
 */
public class MediaPlayerControlGlue<T extends MediaPlayerAdapter> extends PlaybackTransportControlGlue<T> {

    /**
     * Constructor for the glue.
     *
     * @param context
     * @param impl    Implementation to underlying media player.
     */
    public MediaPlayerControlGlue(Context context, T impl) {
        super(context, impl);
    }

    @Override
    protected PlaybackRowPresenter onCreateRowPresenter() {

        MediaPlayTitlePresenter detailsPresenter = new MediaPlayTitlePresenter() {
            @Override
            protected void onBindDescription(MediaPlayTitlePresenter.MyViewHolder vh, Object obj) {
                PlaybackBaseControlGlue glue = (PlaybackBaseControlGlue) obj;
                vh.getTitle().setText(glue.getTitle());
                vh.getSubtitle().setText(glue.getSubtitle());
            }
        };

        PlaybackTransportRowPresenter rowPresenter = new PlaybackTransportRowPresenter() {
            @Override
            protected void onBindRowViewHolder(RowPresenter.ViewHolder vh, Object item) {
                super.onBindRowViewHolder(vh, item);
                vh.setOnKeyListener(MediaPlayerControlGlue.this);
            }

            @Override
            protected void onUnbindRowViewHolder(RowPresenter.ViewHolder vh) {
                super.onUnbindRowViewHolder(vh);
                vh.setOnKeyListener(null);
            }
        };
        rowPresenter.setDescriptionPresenter(detailsPresenter);
        return rowPresenter;
    }
}
