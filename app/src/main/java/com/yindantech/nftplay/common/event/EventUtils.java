package com.yindantech.nftplay.common.event;

import org.greenrobot.eventbus.EventBus;

/**
 * EventUtils
 */
public class EventUtils {

    /**
     * post
     *
     * @param eventType
     * @param value
     */
    public static void post(int eventType, Object value) {
        EventBus.getDefault().post(MessageEvent.create(eventType, value));
    }

    /**
     * post
     *
     * @param eventType
     */
    public static void post(int eventType) {
        EventBus.getDefault().post(MessageEvent.create(eventType));
    }
}
