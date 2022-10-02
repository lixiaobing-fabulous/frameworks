package com.lxb.rpc.apm.health;


import com.lxb.rpc.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static com.lxb.rpc.Plugin.DOCTOR;
import static com.lxb.rpc.util.Timer.timer;


/**
 * 监控状态探针
 */
public class HealthProbe {

    protected static final Logger logger = LoggerFactory.getLogger(HealthProbe.class);
    protected static volatile HealthProbe INSTANCE;

    /**
     * 健康状态
     */
    protected volatile HealthState state = HealthState.HEALTHY;

    /**
     * 构造函数
     */
    protected HealthProbe() {
        timer().add(new DiagnoseTask(s -> state = s));
    }

    public HealthState getState() {
        return state;
    }

    /**
     * 获取单例
     *
     * @return 探针
     */
    public static HealthProbe getInstance() {
        if (INSTANCE == null) {
            synchronized (HealthProbe.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HealthProbe();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 诊断任务
     */
    protected static class DiagnoseTask implements Timer.TimeTask {
        /**
         * 消费者
         */
        protected Consumer<HealthState> consumer;

        /**
         * 构造函数
         *
         * @param consumer
         */
        public DiagnoseTask(Consumer<HealthState> consumer) {
            this.consumer = consumer;
        }

        @Override
        public String getName() {
            return "DiagnoseTask";
        }

        @Override
        public long getTime() {
            return 5000L;
        }

        @Override
        public void run() {
            //调用插件进行诊断
            HealthState result = HealthState.HEALTHY;
            HealthState state;
            int plugins = 0;
            for (Doctor doctor : DOCTOR.extensions()) {
                plugins++;
                state = doctor.diagnose();
                if (state.ordinal() > result.ordinal()) {
                    result = state;
                }
                if (state == HealthState.DEAD) {
                    break;
                }
            }
            //有插件继续执行
            if (plugins > 0) {
                consumer.accept(result);
                timer().add(this);
            }
        }
    }

}
