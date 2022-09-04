package test;

import api.Consumer;
import api.Producer;
import boot.ExtensionAutoConfiguration;
import com.lxb.extension.ExtensionPoint;
import com.lxb.extension.ExtensionPointLazy;
import com.lxb.spring.boot.SpringLoaderAutoConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringLoaderAutoConfiguration.class, ExtensionAutoConfiguration.class})
public class SpringTest {

    @Test
    public void testPlugin() {
        ExtensionPoint<Consumer, String> consumer = new ExtensionPointLazy<>(Consumer.class);
        Assertions.assertNotNull(consumer.get("myConsumer3"));
        ExtensionPoint<Producer, String> producer = new ExtensionPointLazy<>(Producer.class);
        Assertions.assertNotNull(producer.get("myProducer1"));
    }
}
