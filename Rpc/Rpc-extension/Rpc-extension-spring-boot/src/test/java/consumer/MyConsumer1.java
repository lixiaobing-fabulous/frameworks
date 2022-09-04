package consumer;


import api.Consumer;
import com.lxb.extension.Extension;
import com.lxb.extension.Ordered;

@Extension("myConsumer1")
public class MyConsumer1 implements Consumer, Ordered {

    @Override
    public int order() {
        return 0;
    }
}
