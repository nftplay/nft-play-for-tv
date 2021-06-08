package com.yindantech.nftplay.common.utils.language;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.yindantech.nftplay.R;

import java.util.Locale;

/**
 * MultiLanguageUtils
 */
public class MultiLanguageUtils {

    private static final String TAG = "MultiLanguageUtil";
    private static MultiLanguageUtils instance;
    private Context mContext;
    public static final String SAVE_LANGUAGE = "save_language";

    /**
     * init
     *
     * @param mContext
     */
    public static void init(Context mContext) {
        if (instance == null) {
            synchronized (MultiLanguageUtils.class) {
                if (instance == null) {
                    instance = new MultiLanguageUtils(mContext);
                }
            }
        }
    }

    /**
     * getInstance
     *
     * @return
     */
    public static MultiLanguageUtils getInstance() {
        if (instance == null) {
            throw new IllegalStateException("You must be init MultiLanguageUtil first");
        }
        return instance;
    }

    private MultiLanguageUtils(Context context) {
        this.mContext = context;
    }

    /**
     * setConfiguration 设置语言
     */
    public void setConfiguration() {
        Locale targetLocale = getLanguageLocale();
        Configuration configuration = mContext.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(targetLocale);
        } else {
            configuration.locale = targetLocale;
        }
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }

    /**
     * getLanguageLocale
     *
     * @return
     */
    private Locale getLanguageLocale() {
        int languageType = SPUtils.getInstance().getInt(MultiLanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_EN);
//        if (languageType == LanguageType.LANGUAGE_FOLLOW_SYSTEM) {
//            Locale sysLocale = getSysLocale();
//            return sysLocale;
//        } else
        if (languageType == LanguageType.LANGUAGE_EN) {
            return Locale.ENGLISH;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            return Locale.SIMPLIFIED_CHINESE;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return Locale.TRADITIONAL_CHINESE;
        }
        getSystemLanguage(getSysLocale());
        LogUtils.d(TAG, "getLanguageLocale" + languageType + languageType);
        return Locale.ENGLISH;
    }

    /**
     * getSystemLanguage
     *
     * @param locale
     * @return
     */
    private String getSystemLanguage(Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();

    }

    /**
     * The above fetching methods require special treatment
     *
     * @return
     */
    public Locale getSysLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

    /**
     * Update language
     *
     * @param languageType
     */
    public void updateLanguage(int languageType) {
        SPUtils.getInstance().put(MultiLanguageUtils.SAVE_LANGUAGE, languageType);
        MultiLanguageUtils.getInstance().setConfiguration();
    }


    /**
     * getLanguageName
     *
     * @param context
     * @return
     */
    public String getLanguageName(Context context) {
        int languageType = SPUtils.getInstance().getInt(MultiLanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_EN);
        if (languageType == LanguageType.LANGUAGE_EN) {
            return mContext.getString(R.string.setting_language_english);
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            return mContext.getString(R.string.setting_simplified_chinese);
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return mContext.getString(R.string.setting_traditional_chinese);
        }
        return mContext.getString(R.string.setting_language_english);
    }

    /**
     * Gets the language type saved by the user
     *
     * @return
     */
    public int getLanguageType() {
        int languageType = SPUtils.getInstance().getInt(MultiLanguageUtils.SAVE_LANGUAGE, LanguageType.LANGUAGE_EN);
        if (languageType == LanguageType.LANGUAGE_EN) {
            return LanguageType.LANGUAGE_EN;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            return LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return LanguageType.LANGUAGE_CHINESE_TRADITIONAL;
        }
        LogUtils.d(TAG, "getLanguageType" + languageType);
        return languageType;
    }

    /**
     * attachBaseContext
     *
     * @param context
     * @return
     */
    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createConfigurationResources(context);
        } else {
            MultiLanguageUtils.getInstance().setConfiguration();
            return context;
        }
    }

    /**
     * createConfigurationResources
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.N)
    private static Context createConfigurationResources(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = getInstance().getLanguageLocale();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }
}
