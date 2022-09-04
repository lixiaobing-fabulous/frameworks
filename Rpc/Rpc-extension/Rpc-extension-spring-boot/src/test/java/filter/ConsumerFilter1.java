package filter;


import api.Filter;
import com.lxb.extension.Extension;

@Extension("filter1")
public class ConsumerFilter1 implements Filter {
    @Override
    public boolean isConsumer() {
        return true;
    }
}
