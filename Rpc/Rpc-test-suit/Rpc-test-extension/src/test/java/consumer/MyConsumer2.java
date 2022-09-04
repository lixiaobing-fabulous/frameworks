package consumer;


import api.Consumer;
import com.lxb.extension.Extension;
import com.lxb.extension.Ordered;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.extension.condition.ConditionalOnJava;

@Extension("myConsumer1")
@ConditionalOnJava("1.6")
@ConditionalOnClass("xxx.ddf123.df")
public class MyConsumer2 implements Consumer, Ordered {

    @Override
    public int order() {
        return -1;
    }
}
