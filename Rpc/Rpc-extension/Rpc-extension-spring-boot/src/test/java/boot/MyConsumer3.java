package boot;


import api.Consumer;

public class MyConsumer3 implements Consumer {

    @Override
    public int order() {
        return ORDER;
    }
}
