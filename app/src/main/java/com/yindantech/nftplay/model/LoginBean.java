package com.yindantech.nftplay.model;

/**
 * LoginBean
 */
public class LoginBean {

    String address;
    String manufacturer;
    String model;
    int sdkVersionCode;
    String sdkVersionName;
    int appVersionCode;
    int screenWidth;
    int screenHeight;
    int screenDensityDpi;
    String channel;

    public LoginBean(String address, String manufacturer, String model, int sdkVersionCode, String sdkVersionName, int appVersionCode, int screenWidth, int screenHeight, int screenDensityDpi, String channel) {
        this.address = address;
        this.manufacturer = manufacturer;
        this.model = model;
        this.sdkVersionCode = sdkVersionCode;
        this.sdkVersionName = sdkVersionName;
        this.appVersionCode = appVersionCode;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenDensityDpi = screenDensityDpi;
        this.channel = channel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSdkVersionCode() {
        return sdkVersionCode;
    }

    public void setSdkVersionCode(int sdkVersionCode) {
        this.sdkVersionCode = sdkVersionCode;
    }

    public String getSdkVersionName() {
        return sdkVersionName;
    }

    public void setSdkVersionName(String sdkVersionName) {
        this.sdkVersionName = sdkVersionName;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenDensityDpi() {
        return screenDensityDpi;
    }

    public void setScreenDensityDpi(int screenDensityDpi) {
        this.screenDensityDpi = screenDensityDpi;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
