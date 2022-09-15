package com.mine268.zhServer.webServer;

import java.net.Socket;

public class RequestHandler implements Runnable {

    // 处理的请求的socket
    Socket socket;

    /**
     * 创立用于处理请求的Handler
     * @param _socket 处理的请求的socket
     */
    public RequestHandler(Socket _socket) {
        socket = _socket;
    }

    @Override
    public void run() {
        ;
    }
}
