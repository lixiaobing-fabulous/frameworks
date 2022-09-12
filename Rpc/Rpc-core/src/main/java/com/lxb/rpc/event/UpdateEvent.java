package com.lxb.rpc.event;



import com.lxb.extension.URL;

import java.util.function.BiConsumer;

import static com.lxb.rpc.constants.Constants.PROTECT_NULL_DATUM_OPTION;


/**
 * 数据更新事件
 */
public abstract class UpdateEvent<T> extends AbstractEvent {

    /**
     * 事件类型
     */
    protected UpdateType type;

    /**
     * 版本
     */
    protected long version;

    /**
     * 配置
     */
    protected T datum;

    /**
     * 构造函数
     *
     * @param source
     * @param target
     * @param type
     * @param version
     * @param datum
     */
    public UpdateEvent(Object source, Object target, UpdateType type, long version, T datum) {
        super(source, target);
        this.type = type;
        this.version = version;
        this.datum = datum;
    }

    public UpdateType getType() {
        return type;
    }

    public long getVersion() {
        return version;
    }

    public T getDatum() {
        return datum;
    }

    /**
     * 更新事件类型
     */
    public enum UpdateType {
        /**
         * 增量更新
         */
        UPDATE {
            @Override
            public void update(URL url, BiConsumer<Boolean, Boolean> consumer) {
                consumer.accept(false, url.getBoolean(PROTECT_NULL_DATUM_OPTION));
            }
        },
        /**
         * 全量更像
         */
        FULL {
            @Override
            public void update(URL url, BiConsumer<Boolean, Boolean> consumer) {
                consumer.accept(true, url.getBoolean(PROTECT_NULL_DATUM_OPTION));
            }
        },
        /**
         * 清空
         */
        CLEAR {
            @Override
            public void update(URL url, BiConsumer<Boolean, Boolean> consumer) {
                consumer.accept(true, false);
            }
        };

        /**
         * 事件类型对应的更新操作
         *
         * @param url
         * @param consumer 参数1：是否为全量数据 参数2：是否需要空保护
         */
        public void update(URL url, BiConsumer<Boolean, Boolean> consumer) {
        }


    }
}
