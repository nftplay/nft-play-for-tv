package com.yindantech.nftplay.fragment;


import android.os.Bundle;
import android.text.TextUtils;

import androidx.leanback.preference.LeanbackPreferenceFragment;
import androidx.leanback.preference.LeanbackSettingsFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;

import com.blankj.utilcode.util.AppUtils;
import com.yindantech.nftplay.R;
import com.yindantech.nftplay.common.utils.MyUtils;
import com.yindantech.nftplay.view.LanguageSetDialog;
import com.yindantech.nftplay.view.PlayIntervalSetDialog;


/**
 * SettingsFragment
 */
public class SettingsFragment extends LeanbackSettingsFragment {

    @Override
    public void onPreferenceStartInitialScreen() {
        startPreferenceFragment(buildPreferenceFragment(R.xml.prefs, null));
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        return false;
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragment caller, PreferenceScreen pref) {
        PreferenceFragment frag = buildPreferenceFragment(R.xml.prefs, pref.getKey());
        startPreferenceFragment(frag);
        return true;
    }


    /**
     * buildPreferenceFragment
     *
     * @param preferenceResId
     * @param root
     * @return
     */
    private PreferenceFragment buildPreferenceFragment(int preferenceResId, String root) {
        PreferenceFragment fragment = new PrefFragment();
        Bundle args = new Bundle();
        args.putInt("preferenceResource", preferenceResId);
        args.putString("root", root);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * PrefFragment
     */
    public static class PrefFragment extends LeanbackPreferenceFragment {

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            String root = getArguments().getString("root", null);
            int prefResId = getArguments().getInt("preferenceResource");
            if (root == null) {
                addPreferencesFromResource(prefResId);
            } else {
                setPreferencesFromResource(prefResId, root);
            }

            //login out preference . show address
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
                Preference preference = preferenceScreen.getPreference(i);
                if (preference.getKey().equals("prefs_exit_login")) {
                    String walletAddress = MyUtils.getWalletAddress();
                    if (!TextUtils.isEmpty(walletAddress)) {
                        if (walletAddress.length() >= 20) {
                            preference.setSummary(String.format("%s...%s", walletAddress.substring(0, 6), walletAddress.substring(walletAddress.length() - 4)));
                        } else {
                            preference.setSummary(walletAddress);
                        }
                    }
                } else if (preference.getKey().equals("prefs_version")) {
                    preference.setTitle(" ");
                    preference.setSummary(String.format("%s  %s.%d", getString(R.string.app_name), AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));
                }
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
//            LogUtils.d("onPreferenceTreeClick", preference.getKey());
            switch (preference.getKey()) {
                case "prefs_play_setting":
                    new PlayIntervalSetDialog(getActivity()).show();
                    return true;
                case "prefs_language_setting":
                    new LanguageSetDialog(getActivity()).show();
                    return true;
                case "prefs_exit_login":
                    MyUtils.exitLogin();
                    return true;
            }
            return super.onPreferenceTreeClick(preference);
        }
    }
}
