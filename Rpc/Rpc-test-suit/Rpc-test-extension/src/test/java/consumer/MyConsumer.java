package consumer;


import api.Consumer;
import com.lxb.extension.Extension;

@Extension(value = "myConsumer", provider = "test")
public class MyConsumer implements Consumer {

    @Override
    public int order() {
        return ORDER;
    }
}
