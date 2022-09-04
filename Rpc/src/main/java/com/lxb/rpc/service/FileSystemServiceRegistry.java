package com.lxb.rpc.service;

import com.lxb.rpc.serializer.Serializer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认实现
 *
 * @author lixiaobing
 * @since 1.0.0
 */
public class FileSystemServiceRegistry implements ServiceRegistry {

    private final Serializer serializer = Serializer.DEFAULT;

    private File rootDirectory;

    @Override
    public void initialize(Map<String, Object> config) {
        rootDirectory = new File(System.getProperty("java.io.tmpdir"));
    }

    @Override
    public void register(ServiceInstance serviceInstance) {
        String serviceName = serviceInstance.getServiceName();
        File serviceDirectory = new File(rootDirectory, serviceName);
        File serviceInstanceFile = new File(serviceDirectory, serviceInstance.getId());
        try {
            byte[] bytes = serializer.serialize(serviceInstance);
            FileUtils.writeByteArrayToFile(serviceInstanceFile, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deregister(ServiceInstance serviceInstance) {
        String serviceName = serviceInstance.getServiceName();
        File serviceDirectory = new File(rootDirectory, serviceName);
        File serviceInstanceFile = new File(serviceDirectory, serviceInstance.getId());
        FileUtils.deleteQuietly(serviceInstanceFile);
    }

    @Override
    public List<ServiceInstance> getServiceInstances(String serviceName) {
        File serviceDirectory = new File(rootDirectory, serviceName);
        Collection<File> files = FileUtils.listFiles(serviceDirectory, null, false);
        return (List) files.stream().map(file -> {
            try {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                return serializer.deserialize(bytes, ServiceInstance.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public void close() {
        FileUtils.deleteQuietly(rootDirectory);
    }
}
