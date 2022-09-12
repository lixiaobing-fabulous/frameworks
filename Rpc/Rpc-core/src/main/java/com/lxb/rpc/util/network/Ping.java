/**
 *
 */
package com.lxb.rpc.util.network;


import com.lxb.rpc.exception.ConnectionException;
import com.lxb.rpc.util.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lxb.rpc.util.Resource.lines;
import static java.util.regex.Pattern.compile;

/**
 * 网络工具栏
 *
 *
 */
public class Ping {

    /**
     * Linux的PING响应
     */
    public static final Pattern LINUX = compile("icmp_seq=\\d+ ttl=\\d+ time=(.*?) ms");
    /**
     * Windows的PING响应
     */
    public static final Pattern WINDOWS = compile("(bytes|字节)=\\d+ (time|时间)=(.*?)ms TTL=\\d+");

    // less than 1ms
    private static final Pattern WINDOWS_LESS = compile("(bytes|字节)=\\d+ (time|时间)<(.*?)ms TTL=\\d+");

    public static final List<String> DEAD_MSG = lines(new Resource.Definition[]{
            new Resource.Definition("META-INF/system_network_error", true),
            new Resource.Definition("system_network_error")
    }, true, true);

    /**
     * Ping IP
     *
     * @param ip    IP
     * @param count 次数
     * @return 平均响应时间
     * @throws IOException
     */
    public static double ping(final String ip, final int count) throws IOException {
        return ping(ip, count, -1, null);
    }

    /**
     * Ping IP,Linux普通用户时间间隔不能小于200毫秒
     *
     * @param ip       IP
     * @param count    次数
     * @param interval 间隔(毫秒)
     * @return 平均响应时间
     * @throws IOException
     */
    public static double ping(final String ip, final int count, final long interval) throws IOException {
        return ping(ip, count, interval, null);
    }

    /**
     * Ping IP,Linux普通用户时间间隔不能小于200毫秒
     *
     * @param ip       IP
     * @param count    次数
     * @param interval 间隔(毫秒)
     * @param output   输出信息
     * @return 平均响应时间
     * @throws IOException
     */
    public static double ping(final String ip, final int count, final long interval, final StringBuilder output) throws
            IOException {
        LineNumberReader input = null;
        double result = 0;
        int success = 0;
        try {
            // 根据操作系统构造ping命令
            String osName = System.getProperties().getProperty("os.name");
            String charset = System.getProperties().getProperty("sun.jnu.encoding");
            String pingCmd = null;
            boolean windows = osName.toUpperCase().startsWith("WINDOWS");
            if (windows) {
                pingCmd = "cmd /c ping -n {0} {1}";
                pingCmd = MessageFormat.format(pingCmd, count, ip);
            } else if (interval > 0) {
                pingCmd = "ping -c {0} -i {1} {2}";
                pingCmd = MessageFormat.format(pingCmd, count, interval * 1.0 / 1000, ip);
            } else {
                pingCmd = "ping -c {0} {1}";
                pingCmd = MessageFormat.format(pingCmd, count, ip);
            }
            // 执行ping命令
            Process process = Runtime.getRuntime().exec(pingCmd);
            // 读取输出
            input = new LineNumberReader(new InputStreamReader(process.getInputStream(), charset));
            String text;
            int lines = 0;
            // 循环读取输出
            while ((text = input.readLine()) != null) {
                // 输出
                if (output != null) {
                    if (lines++ > 0) {
                        output.append('\n');
                    }
                    output.append(text);
                }
                if (!text.isEmpty()) {
                    // 模式匹配
                    if (windows) {
                        Matcher matcher = WINDOWS.matcher(text);
                        if (matcher.find()) {
                            success++;
                            result += Double.valueOf(matcher.group(3));
                        } else {
                            matcher = WINDOWS_LESS.matcher(text);
                            if (matcher.find()) {
                                success++;
                                result += Double.valueOf(matcher.group(3));
                            }
                        }
                    } else {
                        Matcher matcher = LINUX.matcher(text);
                        if (matcher.find()) {
                            success++;
                            result += Double.valueOf(matcher.group(1));
                        }
                    }
                }
            }
            if (success > 0) {
                // 保留2位小数
                return Math.round(result * 100 / success) / 100.0;
                //return (double) Math.round(result * 100 / success) / 100; // or this
            }
            return -1;
        } finally {
            input.close();
        }
    }

    /**
     * 检查目标节点是否已经不存活了
     *
     * @param throwable 异常
     * @return 是否是死亡节点
     */
    public static boolean detectDead(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        Queue<Throwable> queue = new LinkedList<>();
        queue.add(throwable);
        Throwable t;
        while (!queue.isEmpty()) {
            t = queue.poll();
            t = t instanceof ConnectionException ? t.getCause() : t;
            if (t instanceof NoRouteToHostException) {
                //没有路由
                return true;
            } else if (t instanceof ConnectException) {
                //连接异常
                String msg = t.getMessage().toLowerCase();
                for (String deadMsg : DEAD_MSG) {
                    if (msg.contains(deadMsg)) {
                        return true;
                    }
                }
                return false;
            } else if (t.getCause() != null) {
                queue.add(t.getCause());
            }
        }
        return false;
    }
}
