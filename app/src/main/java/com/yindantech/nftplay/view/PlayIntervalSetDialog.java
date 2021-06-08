package com.yindantech.nftplay.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.yindantech.nftplay.R;
import com.yindantech.nftplay.model.db.DBUtils;

/**
 * PlayIntervalSetPopwindow
 */
public class PlayIntervalSetDialog extends Dialog {

    RadioGroup mRadioGroup;
    long playIntervalTime;

    public PlayIntervalSetDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_play_interval_set);
        mRadioGroup = findViewById(R.id.radioGroup);
        mRadioGroup.requestFocus();
        initView();
    }

    public long TIME_10_S = 1000 * 10;
    public long TIME_30_S = TIME_10_S * 3;
    public long TIME_1_M = TIME_30_S * 2;
    public long TIME_5_M = TIME_1_M * 5;
    public long TIME_10_M = TIME_5_M * 2;
    public long TIME_30_M = TIME_10_M * 3;
    public long TIME_1_H = TIME_30_M * 2;
    public long TIME_1_D = TIME_1_H * 24;


    /**
     * initView
     */
    private void initView() {
        playIntervalTime = DBUtils.getLoginUser().getPlayIntervalTime();
        int childIndex = 0;
        if (playIntervalTime == TIME_10_S) {
            mRadioGroup.check(R.id.rb_10s);
            childIndex = 0;
        } else if (playIntervalTime == TIME_30_S) {
            mRadioGroup.check(R.id.rb_30s);
            childIndex = 1;
        } else if (playIntervalTime == TIME_1_M) {
            mRadioGroup.check(R.id.rb_1m);
            childIndex = 2;
        } else if (playIntervalTime == TIME_5_M) {
            mRadioGroup.check(R.id.rb_5m);
            childIndex = 3;
        } else if (playIntervalTime == TIME_10_M) {
            mRadioGroup.check(R.id.rb_10m);
            childIndex = 4;
        } else if (playIntervalTime == TIME_30_M) {
            mRadioGroup.check(R.id.rb_30m);
            childIndex = 5;
        } else if (playIntervalTime == TIME_1_H) {
            mRadioGroup.check(R.id.rb_1h);
            childIndex = 6;
        } else if (playIntervalTime == TIME_1_D) {
            mRadioGroup.check(R.id.rb_1d);
            childIndex = 7;
        }
        mRadioGroup.getChildAt(childIndex).requestFocus();


        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_10s:
                        playIntervalTime = TIME_10_S;
                        break;
                    case R.id.rb_30s:
                        playIntervalTime = TIME_30_S;
                        break;
                    case R.id.rb_1m:
                        playIntervalTime = TIME_1_M;
                        break;
                    case R.id.rb_5m:
                        playIntervalTime = TIME_5_M;
                        break;
                    case R.id.rb_10m:
                        playIntervalTime = TIME_10_M;
                        break;
                    case R.id.rb_30m:
                        playIntervalTime = TIME_30_M;
                        break;
                    case R.id.rb_1h:
                        playIntervalTime = TIME_1_H;
                        break;
                    case R.id.rb_1d:
                        playIntervalTime = TIME_1_D;
                        break;
                }
                DBUtils.updateIntervalTime(playIntervalTime);
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
