package com.yindantech.nftplay.common.http;

import android.text.TextUtils;


import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonSyntaxException;
import com.yindantech.nftplay.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import rxhttp.wrapper.exception.HttpStatusCodeException;
import rxhttp.wrapper.exception.ParseException;

/**
 * Http Request error message
 */
public class ErrorInfo {

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", throwable=" + throwable.toString() +
                '}';
    }

    private int errorCode;  //Refers only to the error code returned by the server
    private String errorMsg; //Error copy, network error, request failure error, error returned by the server, etc
    private Throwable throwable; //Exception information

    public ErrorInfo(Throwable throwable) {
        this.throwable = throwable;
        String errorMsg = null;
        if (throwable instanceof UnknownHostException) {

            if (NetworkUtils.isConnected()) {
                errorMsg = getString(R.string.network_error);
            } else {
                errorMsg = getString(R.string.notify_no_network);
            }

        } else if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
            //The former is an exception thrown by a timeout set by OkHttpClient, and the latter is a timeout exception thrown by calling a timeout method on a single request
            errorMsg = getString(R.string.time_out_please_try_again_later);
        } else if (throwable instanceof ConnectException) {
            errorMsg = getString(R.string.esky_service_exception);
        } else if (throwable instanceof HttpStatusCodeException) { //Request failure exception
            String code = throwable.getLocalizedMessage();
            if ("416".equals(code)) {
                errorMsg = getString(R.string.request_exception_416);
            } else {
                errorMsg = throwable.getMessage();
            }
        } else if (throwable instanceof JsonSyntaxException) { //The request succeeded, but the JSON syntax was abnormal, resulting in parsing failure
            errorMsg = getString(R.string.json_data_exception);
        } else if (throwable instanceof ParseException) { // ParseException An exception indicates that the request succeeded, but the data is incorrect
            String errorCode = throwable.getLocalizedMessage();
            this.errorCode = Integer.parseInt(errorCode);
            errorMsg = throwable.getMessage();
            if (TextUtils.isEmpty(errorMsg)) errorMsg = errorCode;//errorMsg is null，show errorCode
        } else {
            errorMsg = throwable.getMessage();
        }
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean show() {
//        ToastUtils.showShort(TextUtils.isEmpty(errorMsg) ? throwable.getMessage() : errorMsg);
        show(R.string.network_error);
        return true;

    }

    /**
     * @param standbyMsg Alternate prompt copy
     */
    public boolean show(String standbyMsg) {
        ToastUtils.showShort(TextUtils.isEmpty(errorMsg) ? standbyMsg : errorMsg);
        return true;
    }

    /**
     * @param standbyMsg Alternate prompt copy
     */
    public boolean show(int standbyMsg) {
        ToastUtils.showLong(TextUtils.isEmpty(errorMsg) ? StringUtils.getString(standbyMsg) : errorMsg);
        return true;
    }

    public String getString(int resId) {
        return StringUtils.getString(resId);
    }
}