package com.mine268.zhServer.webServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WebServer {

    // 线程池
    private ThreadPoolExecutor threadPool;

    // 监听的端口
    int port;

    // socket
    ServerSocket serverSocket;

    /**
     * 创建多线程服务器
     * @param core_thread 核心线程数
     * @param max_thread 最大线程数
     * @param keep_alive_time 超出的闲置线程存活时间
     * @param time_unit 存活时间的时间单位
     * @param queue_length 任务等待队列
     */
    public WebServer(int _port, int core_thread, int max_thread, int keep_alive_time,
                     TimeUnit time_unit, int queue_length) {
        assert(_port > 1023);
        assert(core_thread > 0);
        assert(max_thread > 0);
        assert(max_thread >= core_thread);
        assert(keep_alive_time >= 0);

        port = _port;

        threadPool = new ThreadPoolExecutor(
                core_thread,
                max_thread,
                keep_alive_time,
                time_unit,
                new LinkedBlockingQueue<>(queue_length),
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
    }

    /**
     * 开始监听端口
     * @throws IOException 监听过程中可能的异常
     */
    public void listen() throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            var socket = serverSocket.accept();
            threadPool.execute(new RequestHandler(socket));
        }
    }
}
