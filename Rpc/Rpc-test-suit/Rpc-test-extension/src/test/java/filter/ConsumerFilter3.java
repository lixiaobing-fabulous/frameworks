package filter;


import api.Filter;
import com.lxb.extension.Extension;

@Extension("filter3")
public class ConsumerFilter3 implements Filter {
    @Override
    public boolean isConsumer() {
        return true;
    }
}
