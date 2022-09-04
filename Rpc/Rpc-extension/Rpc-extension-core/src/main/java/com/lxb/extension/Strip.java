package com.lxb.extension;


/**
 * 裁剪接口
 */
public interface Strip {

    /**
     * 简单裁剪函数
     */
    Strip SIMPLE_STRIP = new SimpleStrip();

    /**
     * 应用
     *
     * @param prefix 前缀
     * @param value  完整值
     * @return
     */
    String apply(String prefix, String value);

    /**
     * 简单前缀裁剪
     */
    class SimpleStrip implements Strip {

        @Override
        public String apply(final String prefix, final String value) {
            return value.substring(prefix.length());
        }
    }
}
