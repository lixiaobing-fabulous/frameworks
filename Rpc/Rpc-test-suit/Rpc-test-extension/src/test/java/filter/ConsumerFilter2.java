package filter;


import api.Filter;
import com.lxb.extension.Extension;

@Extension("filter2")
public class ConsumerFilter2 implements Filter {
    @Override
    public boolean isConsumer() {
        return true;
    }
}
