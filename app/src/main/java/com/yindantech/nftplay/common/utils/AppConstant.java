package com.yindantech.nftplay.common.utils;

import com.yindantech.nftplay.BuildConfig;

/**
 * AppConstant
 */
public class AppConstant {

    /**
     * Config
     */
    public static class Config {
        /**
         * debug
         * Change to false when publishing
         */
        public static final boolean DEBUG = BuildConfig.DEBUG;

        /**
         * opensea api host
         */
        public static final String OPENSEA_API_HOST = AppConstant.Config.DEBUG ? "https://testnets-api.opensea.io" : "https://api.opensea.io";
        //NFT Play host
        public static final String NFTPLAY_HOST = "https://www.nftplay.net";
        //Login page address：Log in using Wallet-Connect to get the wallet address
        public static final String LOGIN_HTML = NFTPLAY_HOST + "/nftplay/tvapp/login/index.html";
        //login api
        public static final String LOGIN_API = NFTPLAY_HOST + "/nftplay/dataStatisticeApi/statisticeUserInfo";

        //Automatic refresh interval time (60 minutes), debug mode is set to 10 minutes
        public static final long AUTO_REFRESH_DATA_INTERVAL_TIME = 1000 * 60 * (DEBUG ? 10 : 60);
        //Start page delay jump time
        public static final long SPLASH_START_DELAY_TIME = 1500;
        //asset api limit ， The asset requests the amount of data returned per page
        public static final int ASSET_REQUEST_LIMIT = 50;
        //The maximum number of NFT assets to load must be a multiple of ASSET_REQUEST_LIMIT per page!
        public static int MAX_LOAD_ASSET_COUNT = 500;

    }

    /**
     * Key
     */
    public static class Key {
        /**
         * The wallet address
         */
        public static final String WALLET_ADDRESS = "wallet_address";
        /**
         * assetId
         */
        public static final String ASSET_ID = "assetId";
        /**
         * Enter the main interface whether to automatically play : default=true
         */
        public static final String IS_AUTO_PLAY = "isAutoPlay";
        /**
         * Whether the activity is restarted value boolean :default=false
         */
        public static final String IS_RESTART = "isRestart";

        /**
         * nft asset media type : value String : {MEDIA_TYPE_VIDEO}{MEDIA_TYPE_MUSIC}
         */
        public static final String MEDIA_TYPE = "mediaType";
        public static final String MEDIA_TYPE_VIDEO = "video";
        public static final String MEDIA_TYPE_MUSIC = "music";

        //Whether to automatically return to the page :value boolean
        public static final String IS_AUTO_BACK = "isAutoBack";
    }

    /**
     * code
     */
    public static class Code {
        public static final int PLAY_REQUEST_CODE = 1000;
    }
}
