package com.mine268.zhServer.parser;

public class HttpRequest {

    /**
     * Request类型，EXT为extension-method
     */
    public enum RequestType {
        GET, HEAD, PUT, POST, DELETE, LINK, UNLINK, EXT
    }

    // 请求的类型
    public RequestType requestType;

    // 请求的URI
    public String requestURI;

    // 请求使用的http的版本
    public int httpMajorVersion, httpMinorVersion;

}
