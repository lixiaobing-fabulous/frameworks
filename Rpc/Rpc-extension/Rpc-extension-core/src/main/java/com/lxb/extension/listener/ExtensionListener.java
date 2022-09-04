package com.lxb.extension.listener;


import java.util.EventListener;

/**
 * 事件监听器
 */
public interface ExtensionListener extends EventListener {

    /**
     * 加载器发生变化
     */
    void onEvent(ExtensionEvent event);
}
