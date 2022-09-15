package com.mine268.zhServer.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Logger {

    private final OutputStream log_stream;

    /**
     * 创建logger，log信息输出到 {@code log_path} 指定的文件
     * @param log_path log文件地址
     * @exception FileNotFoundException 关于log文件的异常
     */
    public Logger(String log_path) throws FileNotFoundException {
        log_stream = new FileOutputStream(log_path);
    }

    /**
     * 创建logger，log信息输出到 {@code output_stream}
     * @param output_stream log信息输出到的流
     */
    public Logger(OutputStream output_stream) {
        log_stream = output_stream;
    }

    /**
     * 写入一行log信息
     * @param remote_ip 发出请求的ip地址
     * @param visitor_ua 用户身份
     * @param login_id 用户登录id
     * @param hit_time 请求到达的时间
     * @param request_method 请求方法
     * @param request_uri 请求的页面
     * @param status_code 状态码
     * @param file_size 请求的文件的大小，单位为B
     * @param referer_page 包含访问链接的文件地址
     */
    public void log(String remote_ip,
                    String visitor_ua,
                    String login_id,
                    String hit_time,
                    String request_method,
                    String request_uri,
                    int    status_code,
                    int    file_size,
                    String referer_page) throws IOException {
        String log_line = String.format("[%s]ua %s, login_id %s, hit_time %s, " +
                "method %s, uri %s, status %d, size %d, referer_page %s\n",
                remote_ip,
                visitor_ua,
                login_id,
                hit_time,
                request_method,
                request_uri,
                status_code,
                file_size,
                referer_page);
        logText(log_line);
    }

    /**
     * 向文件写入log文本
     * @param text 写入的log文本
     */
    private synchronized void logText(String text) throws IOException {
        // 考虑多线程同一个logger对象的调用
        log_stream.write(text.getBytes());
    }

}
