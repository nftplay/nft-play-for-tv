package com.yindantech.nftplay.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.yindantech.nftplay.common.utils.language.MultiLanguageUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BaseActivity
 */
public abstract class BaseActivity extends FragmentActivity {

    Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBind = ButterKnife.bind(this);
        create();
    }

    /**
     * view layout id
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * onCreate
     */
    protected abstract void create();


    /**
     * back finish
     */
    protected void back() {
        //..You can do something here, if you want
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBind) {
            mBind.unbind();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtils.attachBaseContext(newBase));
    }

    protected boolean clickKeyEnter() {
        return false;
    }

    protected boolean clickKeyDown() {
        return false;
    }

    protected boolean clickKeyLeft() {
        return false;
    }

    protected boolean clickKeyRight() {
        return false;
    }

    protected boolean clickKeyUp() {
        return false;
    }

    protected boolean clickKeyBack() {
        return false;
    }

    protected boolean clickKeySettings() {
        return false;
    }

    protected boolean clickKeyMenu() {
        return false;
    }

    protected boolean clickKeyNext() {
        return false;
    }

    protected boolean clickKeyPrevious() {
        return false;
    }

    protected boolean clickKeyPlayPause() {
        return false;
    }


    /**
     * Whether to allow clicking. Default is true
     *
     * @return
     */
    protected boolean isAllowClickKey() {
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //We handle click-intercept events uniformly in the superclass function, if necessary
        //This is done by overriding the superclass function

        if (!isAllowClickKey()) {
            return true;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (clickKeyEnter())
                    return true;
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (clickKeyDown())
                    return true;
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (clickKeyLeft())
                    return true;
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (clickKeyRight())
                    return true;
                break;

            case KeyEvent.KEYCODE_DPAD_UP:
                if (clickKeyUp())
                    return true;
                break;

            case KeyEvent.KEYCODE_BACK:
                if (clickKeyBack())
                    return true;
                break;

            case KeyEvent.KEYCODE_SETTINGS:
                if (clickKeySettings())
                    return true;
                break;

            case KeyEvent.KEYCODE_MENU:
                if (clickKeyMenu())
                    return true;
                break;

            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                if (clickKeyNext())
                    return true;
                break;

            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                if (clickKeyPrevious())
                    return true;
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (clickKeyPlayPause())
                    return true;
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
