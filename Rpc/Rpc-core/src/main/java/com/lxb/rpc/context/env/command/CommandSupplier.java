package com.lxb.rpc.context.env.command;


import com.lxb.extension.Extension;
import com.lxb.rpc.context.EnvironmentSupplier;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令行参数变量
 */
@Extension(value = "command", order = EnvironmentSupplier.COMMAND_ORDER)
public class CommandSupplier implements EnvironmentSupplier {

    @Override
    public Map<String, String> environment() {
        //从系统环境获取
        Map<String, String> result = new HashMap<>();
        //从命令行获取
        List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : args) {
            if (arg.startsWith("--") && arg.length() > 2) {
                String optionText = arg.substring(2);
                int pos = optionText.indexOf('=');
                if (pos > 0) {
                    String optionName = optionText.substring(0, pos);
                    String optionValue = optionText.substring(pos + 1);
                    result.put(optionName, optionValue);
                }
            }
        }

        return result;
    }

}
