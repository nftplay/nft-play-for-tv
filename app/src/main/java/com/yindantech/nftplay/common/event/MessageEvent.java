package com.yindantech.nftplay.common.event;


/**
 * MessageEvent EventBus
 */
public class MessageEvent {


    //The asset acquisition request start
    public static final int TYPE_GET_ASSETS_START = 1;
    //Asset Acquisition Success. value= List<T>
    public static final int TYPE_GET_ASSETS_SUCCESS = 2;
    //Asset acquisition error
    public static final int TYPE_GET_ASSETS_ERROR = 3;

    /**
     * event type
     */
    public int eventType;
    /**
     * value
     */
    public Object value;


    /**
     * create
     *
     * @param eventType
     * @param value
     * @return
     */
    public static MessageEvent create(int eventType, Object value) {
        MessageEvent event = new MessageEvent();
        event.eventType = eventType;
        event.value = value;
        return event;
    }

    /**
     * create
     *
     * @param eventType
     * @return
     */
    public static MessageEvent create(int eventType) {
        MessageEvent event = new MessageEvent();
        event.eventType = eventType;
        return event;
    }
}
