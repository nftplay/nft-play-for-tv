package com.yindantech.nftplay.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.activity.MainActivity;
import com.yindantech.nftplay.common.utils.language.LanguageType;
import com.yindantech.nftplay.common.utils.language.MultiLanguageUtils;

import static com.yindantech.nftplay.common.utils.AppConstant.Key.IS_RESTART;

/**
 * LanguageSetDialog
 */
public class LanguageSetDialog extends Dialog {

    RadioGroup mRadioGroup;
    int savedLanguageType;

    public LanguageSetDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_language_set);
        mRadioGroup = findViewById(R.id.radioGroup);
        mRadioGroup.requestFocus();
        initView();
    }

    /**
     * initView
     */
    private void initView() {
        savedLanguageType = MultiLanguageUtils.getInstance().getLanguageType();
        int childIndex = 0;
        if (savedLanguageType == LanguageType.LANGUAGE_EN) {
            mRadioGroup.check(R.id.rb_en);
            childIndex = 0;
        } else if (savedLanguageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            mRadioGroup.check(R.id.rb_zh);
            childIndex = 1;
        } else if (savedLanguageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            mRadioGroup.check(R.id.rb_zh_hk);
            childIndex = 2;
        }
        mRadioGroup.getChildAt(childIndex).requestFocus();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_en:
                        savedLanguageType = LanguageType.LANGUAGE_EN;
                        break;
                    case R.id.rb_zh:
                        savedLanguageType = LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
                        break;
                    case R.id.rb_zh_hk:
                        savedLanguageType = LanguageType.LANGUAGE_CHINESE_TRADITIONAL;
                        break;
                }
                dismiss();
                MultiLanguageUtils.getInstance().updateLanguage(savedLanguageType);
                Intent intent = new Intent(Utils.getApp(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(IS_RESTART, true);
                ActivityUtils.startActivity(intent);
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
