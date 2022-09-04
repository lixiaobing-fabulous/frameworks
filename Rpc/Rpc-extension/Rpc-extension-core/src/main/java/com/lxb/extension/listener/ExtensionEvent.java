package com.lxb.extension.listener;

import java.util.EventObject;

/**
 * 扩展点事件
 */
public class ExtensionEvent extends EventObject {

    public ExtensionEvent(Object source) {
        super(source);
    }
}
