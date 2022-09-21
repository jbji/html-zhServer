package com.mine268.zhServer.webServer;

import com.mine268.zhServer.logger.Logger;
import com.mine268.zhServer.parser.HttpRequest;
import com.mine268.zhServer.parser.HttpRequestParser;

import java.io.*;
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
        HttpRequest request_ctx = null;
        WebServerConfig.StatusCode status_code = WebServerConfig.StatusCode.OK; //结果状态
        String file_path = null;

        // -------------------------------------------读取-------------------------------------------
        try {
            request_ctx = new HttpRequestParser().parse(
                    new String(readStream(socket.getInputStream())));

            if(request_ctx.requestURI.length() == 1)
                file_path = WebServerConfig.default_page_path;
            else
                file_path = request_ctx.requestURI;
        } catch (Exception ex) {
            ex.printStackTrace();
        } // 张天 软件工程组 分析 理解 程序应用支持 系统支持

        // -------------------------------------------执行-------------------------------------------
        if(request_ctx.requestType == HttpRequest.RequestType.GET){

        }
        else if(request_ctx.requestType == HttpRequest.RequestType.POST){

        }

        status_code = WebServerConfig.StatusCode.OK;

        //读取网页数据
        InputStream input_stream = null;
        try {
            input_stream = new FileInputStream(WebServerConfig.root_path + file_path);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // -------------------------------------------返回-------------------------------------------
        returnPage(socket,status_code,input_stream);
        try{
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 返回页面到socket上
     * @param socket 返回到的主机的socket
     * @param code 页面状态码
     * @param input_stream 页面的输入流
     */
    private void returnPage(Socket socket, WebServerConfig.StatusCode code, InputStream input_stream){
        try {
            var output_stream = socket.getOutputStream();
            output_stream.write(WebServerConfig.getHtmlHeader(code).getBytes());
            output_stream.write(input_stream.readAllBytes());
            output_stream.close();
        } catch(Exception e){
            e.printStackTrace();
        }
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
