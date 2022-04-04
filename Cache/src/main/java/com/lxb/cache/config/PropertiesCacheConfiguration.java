/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lxb.cache.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.lxb.cache.provider.CachingProvider;
import com.lxb.cache.provider.DefaultCachingProvider;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-01-25
 */
public class PropertiesCacheConfiguration<K, V> implements PropertyConfiguration<K, V> {

    private final Map<String, String> config;

    public PropertiesCacheConfiguration(Properties properties) {
        this.config = new HashMap<>(properties.size());
        for (String propertyName : properties.stringPropertyNames()) {
            config.put(propertyName, properties.getProperty(propertyName));
        }
    }

    @Override
    public String getProperty(String propertyName) {
        return config.get(propertyName);
    }

    public static void main(String[] args) {
        CachingProvider cachingProvider = new DefaultCachingProvider();
        PropertyConfiguration configuration = new PropertiesCacheConfiguration(cachingProvider.getDefaultProperties());
        System.out.println(configuration.getKeyType());
        System.out.println(configuration.getValueType());
        System.out.println(configuration.isManagementEnabled());
        System.out.println(configuration.isReadThrough());
        System.out.println(configuration.isWriteThrough());
        System.out.println(configuration.isStatisticsEnabled());
        System.out.println(configuration.isManagementEnabled());
    }

}
