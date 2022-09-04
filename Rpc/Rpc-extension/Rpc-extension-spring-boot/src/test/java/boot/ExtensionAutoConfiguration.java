package boot;



import api.Consumer;
import api.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExtensionAutoConfiguration {

    @Bean
    public Consumer myConsumer3() {
        return new MyConsumer3();
    }

    @Bean
    public Producer myProducer1() {
        return new MyProducer1();
    }
}
