package api;


import com.lxb.extension.Extensible;

@Extensible("filter")
public interface Filter {

    boolean isConsumer();

    enum FilterType {
        PROCEDURE,
        CONSUMER
    }
}
