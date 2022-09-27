package com.mine268.zhServer.logger;

import com.mine268.zhServer.gui.GuiSingleton;
import com.mine268.zhServer.webServer.WebServerConfig;

import java.io.*;
import java.util.Date;

public class Logger {

    private OutputStream log_stream;

    /**
     * 创建logger，log信息输出到 {@code log_path} 指定的文件
     */
    public Logger() {
        try{
            String filePath = WebServerConfig.log_path + '/' + new Date().toString().replace(' ','_').replace(':','_') + ".log";
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            log_stream = new FileOutputStream(file);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[Error] Failed to create log file. 日志文件创建失败.");
        }
    }

    /**
     * 创建logger，log信息输出到 {@code output_stream}
     * @param output_stream log信息输出到的流
     */
    public Logger(OutputStream output_stream) {
        log_stream = output_stream;
    }

    /**
     * 打印错误信息
     * @param message 错误信息描述
     */
    public void logError(String message) {
        logText(String.format("[ERROR] %s %s\n", new Date(), message));
    }

    /**
     * 写入一行request log信息
     * @param remote_ip 发出请求的ip地址
     * @param visitor_ua 用户身份
     * @param login_id 用户登录id
     * @param hit_time 请求到达的时间
     * @param request_method 请求方法
     * @param request_url 请求的页面
     * @param status_code 状态码
     * @param file_size 请求的文件的大小，单位为B
     * @param referer_page 包含访问链接的文件地址
     */
    public void logRequest(String remote_ip,
                           String visitor_ua,
                           String login_id,
                           String hit_time,
                           String request_method,
                           String request_url,
                           int    status_code,
                           int    file_size,
                           String referer_page) {
        String log_line = String.format("%s - [%s] \"%s %s\" %d %d, \"%s\", %s, %s\n",
                remote_ip,
                hit_time,
                request_method,
                request_url,
                status_code,
                file_size,
                referer_page,
                visitor_ua,
                login_id
                );
        logText(log_line);
    }

    /**
     * 向文件写入log文本
     * @param text 写入的log文本
     */
    private synchronized void logText(String text) {
        // 考虑多线程同一个logger对象的调用
        try {
            log_stream.write(text.getBytes()); //输出到输出流
            GuiSingleton.GetInstance().addLog(text.getBytes()); //打印到日志窗口
        } catch (IOException ex) {
            System.err.printf("[%s]日志写入失败.\n", new Date());
        }
    }

}
