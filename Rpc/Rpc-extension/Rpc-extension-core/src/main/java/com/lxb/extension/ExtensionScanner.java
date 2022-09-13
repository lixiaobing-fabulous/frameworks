package com.lxb.extension;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 扩展点扫描
 */
public interface ExtensionScanner {

    /**
     * 扫描扩展点
     *
     * @return 扩展点类型
     */
    Set<Class<?>> scan();


    class DefaultScanner implements ExtensionScanner {

        @Override
        public Set<Class<?>> scan() {
            Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                Enumeration<URL> urls = classLoader.getResources("META-INF/com.lxb.extension");
                while (urls.hasMoreElements()) {
                    scan(classLoader, urls.nextElement(), classes);
                }
            } catch (IOException ignored) {
            }
            return classes;
        }

        /**
         * 加载文件中的类
         *
         * @param classLoader 类加载器
         * @param url         url
         * @param classes     类集合
         */
        protected void scan(final ClassLoader classLoader, final URL url, final Set<Class<?>> classes) {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    try {
                        classes.add(classLoader.loadClass(line));
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            } catch (IOException ignored) {
            }
        }
    }
}
