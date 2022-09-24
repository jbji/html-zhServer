package com.mine268.zhServer.webServer;

import com.mine268.zhServer.logger.Logger;
import com.mine268.zhServer.parser.HttpRequest;
import com.mine268.zhServer.parser.HttpRequestParser;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.charset.StandardCharsets;

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
        WebServerConfig.StatusCode status_code; //结果状态
        String request_file = null;
        InputStream request_file_stream = null;

        // -- 读取请求 --
        try {
            request_ctx = new HttpRequestParser().parse(
                    new String(readStream(socket.getInputStream())));

            if(request_ctx.requestURI.equals("/"))
                request_file = WebServerConfig.default_page_path;
            else
                request_file = request_ctx.requestURI;
        } catch (Exception ex) {
            logger.logError(ex.getMessage());
        }

        // -- 读取网页文件 --
        // 读取网页数据
        String page_path;
        assert request_file != null;
        // 根据访问的页面存在性设置状态码和访问的地址
        if (request_file.matches("^/cgi-bin/.+")) {
            if (new File(WebServerConfig.root_path + request_file + ".jar").exists()) {
                page_path = WebServerConfig.root_path + request_file + ".jar";
                status_code = WebServerConfig.StatusCode.OK;
            } else {
                page_path = WebServerConfig.root_path + WebServerConfig.not_found_page;
                status_code = WebServerConfig.StatusCode.NOT_FOUND;
            }
        } else {
            if (new File(WebServerConfig.root_path + request_file).exists()) {
                page_path = WebServerConfig.root_path + request_file;
                status_code = WebServerConfig.StatusCode.OK;
            } else {
                page_path = WebServerConfig.root_path + WebServerConfig.not_found_page;
                status_code = WebServerConfig.StatusCode.NOT_FOUND;
            }
        }

        if (request_file.matches("^/cgi-bin/.+")) {
            // 处理请求CGI
            try (var cl = new URLClassLoader(new URL[] { new URL("file:" + page_path) })) {
                var clazz = cl.loadClass(WebServerConfig.cgi_bin_class);
                var method = clazz.getMethod(WebServerConfig.cgi_bin_method, String.class);
                var cgi_ret = (String) method.invoke(clazz.getDeclaredConstructor().newInstance(), request_ctx.tableValuesStr);
                request_file_stream = new ByteArrayInputStream(cgi_ret.getBytes(StandardCharsets.UTF_8));
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException | IOException | InstantiationException e) {
                // 若CGI执行失败，则产生500错误
                logger.logError(String.format("CGI程序执行错误：%s", page_path));
                try {
                    request_file_stream = new FileInputStream(
                            WebServerConfig.root_path + WebServerConfig.internal_err_page);
                } catch (FileNotFoundException ex) {
                    // 若500错误页面无法找到，则log报错
                    logger.logError("无法读取500.html文件");
                }
                status_code = WebServerConfig.StatusCode.INTERNAL_SERVER_ERROR;
            }
        } else {
            // 处理静态页面
            try {
                request_file_stream = switch (request_ctx.requestType) {
                    case GET, POST -> new FileInputStream(page_path);
                    default -> new ByteArrayInputStream(new byte[] {}); // 返回空流
                };
            } catch (Exception e) {
                // 静态页面处理错误，则报错
                logger.logError(String.format("无法读取文件: %s", page_path));
            }
        }

        // -- 返回 --
        returnPage(socket, status_code, request_file_stream, request_ctx);
        try {
            socket.close();
        } catch (Exception e){
            logger.logError("无法关闭socket");
        }

    }

    /**
     * 返回页面到socket上
     * @param socket 返回到的主机的socket
     * @param code 页面状态码
     * @param input_stream 页面的输入流
     */
    private void returnPage(Socket socket, WebServerConfig.StatusCode code,
                            InputStream input_stream, HttpRequest req_ctx) {
        try {
            var output_stream = socket.getOutputStream();
            output_stream.write(WebServerConfig.getHtmlHeader(code).getBytes());
            output_stream.write(input_stream.readAllBytes());
            output_stream.close();
            logger.logRequest(socket.getInetAddress().getHostAddress(),
                    req_ctx.header.get("User-Agent"),
                    "",
                    req_ctx.hitTime.toString(),
                    req_ctx.requestType.name(),
                    req_ctx.requestURI,
                    code.code,
                    input_stream.available(),
                    "");
        } catch(Exception e){
            logger.logError(String.format("传输数据时遇到异常: %s", socket.getInetAddress().getHostAddress()));
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
