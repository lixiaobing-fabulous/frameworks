package test;


import com.lxb.extension.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class UrlTest {

    @Test
    public void testValueOf() {
        URL url = URL.valueOf("http://${user.name}:${user.password}@xxxxx/sadfsfasf");
        System.out.println(url.toString(true, true));
        List<String> params = new ArrayList<String>(3);
        url = URL.valueOf("http://${xxxx:user.name}:${user.password}@xxxxx/sadfsfasf?name=3&key=x", "http", params);
        System.out.println(url.getUser());
        Assertions.assertTrue(params.size() == 2);
        url = URL.valueOf("file:/D:\\config");
        Assertions.assertEquals(url.getPath(), "D:\\config");
        Assertions.assertEquals(url.getAbsolutePath(), "D:\\config");
    }

    @Test
    public void testToString() {
        URL url = URL.valueOf("http://yyy?xxx=b");
        String a = url.toString(false, true);
        String a1 = url.toString(false, true, "xxx");
        Assertions.assertEquals(a, a1);
    }
}
