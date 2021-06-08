package com.yindantech.nftplay.common.http;


import io.reactivex.rxjava3.functions.Consumer;


/**
 * RxJava Error callback, add network exception handling
 */
public interface OnError extends Consumer<Throwable> {

    @Override
    default void accept(Throwable throwable) throws Exception {
        onError(new ErrorInfo(throwable));
    }

    void onError(ErrorInfo error) throws Exception;
}