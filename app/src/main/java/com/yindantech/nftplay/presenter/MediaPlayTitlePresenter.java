package com.yindantech.nftplay.presenter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.yindantech.nftplay.R;

/**
 * MediaPlayTitlePresenter
 * The default presenter: {@AbstractDetailsDescriptionPresenter}
 * I don't use the default because the default Chinese display is problematic
 */
public abstract class MediaPlayTitlePresenter extends Presenter {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_media_play_title, parent, false);
        return new MyViewHolder(v);
    }

    protected abstract void onBindDescription(MyViewHolder vh, Object item);

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        MyViewHolder vh = (MyViewHolder) viewHolder;
        onBindDescription(vh, item);
        vh.tv_title.setVisibility(TextUtils.isEmpty(vh.tv_title.getText()) ? View.GONE : View.VISIBLE);
        vh.tv_subtitle.setVisibility(TextUtils.isEmpty(vh.tv_subtitle.getText()) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }


    /**
     * MyViewHolder
     */
    public static class MyViewHolder extends ViewHolder {
        TextView tv_title;
        TextView tv_subtitle;

        public MyViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_title);
            tv_subtitle = view.findViewById(R.id.tv_subtitle);

        }

        public TextView getTitle() {
            return tv_title;
        }

        public TextView getSubtitle() {
            return tv_subtitle;
        }

    }

}
