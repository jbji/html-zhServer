package com.mine268.zhServer.webServer;

public class WebServerConfig {

    public static final String root_path = "./webroot";

    public static final String log_path = "./webroot/log";

    public static final String cgi_bin_path = "/cgi-bin";

    public static final String cgi_bin_class = "webserver.cgi.CGIClass";

    public static final String cgi_bin_method = "run";

    public static final String default_page_path = "/index.html";

    public static final String not_found_page = "/404.html";

    public static final String internal_err_page = "/500.html";

    public static final int socket_timeout = 60 * 1000;


    public enum StatusCode {
        OK(200, "OK"),
        BadRequest(400,"Bad Request"),  //语法格式错误
        Forbidden(403,"Forbidden"),    //资源访问被拒绝
        NOT_FOUND(404, "Not found"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        UNSUPPORTED_VERSION(505, "HTTP Version not supported");

        public final Integer code;
        public final String description;

        StatusCode(Integer code, String description) {
            this.code = code;
            this.description = description;
        }
    }

    public static String getHtmlHeader(StatusCode statusCode) {
        return String.format("""
            HTTP/1.0 %d %s
            Server: zhServer
            
            """,
                statusCode.code, statusCode.description);
    }
}
