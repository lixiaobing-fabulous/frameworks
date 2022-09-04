package filter;


import api.Filter;
import com.lxb.extension.Extension;

@Extension("filter4")
public class ConsumerFilter4 implements Filter {
    @Override
    public boolean isConsumer() {
        return true;
    }
}
