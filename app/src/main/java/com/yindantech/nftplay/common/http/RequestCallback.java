package com.yindantech.nftplay.common.http;


import java.util.List;

/**
 * list
 * RequestCallback
 */
public interface RequestCallback<T> {
    void onStart();
    void onSuccess(List<T> dataList);
    void onError(ErrorInfo errorInfo);
}
