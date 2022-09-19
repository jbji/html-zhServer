package com.mine268.zhServer.webServer;

import com.mine268.zhServer.logger.Logger;
import com.mine268.zhServer.parser.HttpRequest;
import com.mine268.zhServer.parser.HttpRequestParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class RequestHandler implements Runnable {

    // 处理的请求的socket
    Socket socket;

    // 日志对象
    Logger logger;

    /**
     * 创立用于处理请求的Handler
     * @param _socket 处理的请求的socket
     */
    public RequestHandler(Socket _socket, Logger log) {
        socket = _socket;
        logger = log;
    }

    @Override
    public void run() {
        HttpRequest request_ctx;
        // todo: 读取
        try {
            request_ctx = new HttpRequestParser().parse(
                    Arrays.toString(readStream(socket.getInputStream())));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // todo: 执行
        // todo: 返回
    }

    /**
     * 从流读取接受的内容
     * @param input_stream 输入流
     * @return 返回读取的字节数组
     * @throws IOException 读取异常（超时）
     */
    private static byte[] readStream(InputStream input_stream) throws IOException, InterruptedException {
        var bao = new ByteArrayOutputStream();
        readStreamRecursion(bao, input_stream);
        bao.close();
        return bao.toByteArray();
    }

    private static void readStreamRecursion(ByteArrayOutputStream output_stream, InputStream input_stream)
            throws IOException, InterruptedException {
        var start_time = System.currentTimeMillis();
        while (input_stream.available() == 0) {
            if (System.currentTimeMillis() - start_time >= WebServerConfig.socket_timeout) {
                throw new SocketTimeoutException("Socket reading time-outs.");
            }
        }

        var buffer = new byte[2048];
        var read_count = input_stream.read(buffer);
        output_stream.write(buffer, 0, read_count);
        Thread.sleep(100);
        if (input_stream.available() != 0) {
            readStreamRecursion(output_stream, input_stream);
        }
    }
}
