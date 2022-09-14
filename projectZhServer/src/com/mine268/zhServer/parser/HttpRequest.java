package com.mine268.zhServer.parser;

import java.util.HashMap;
import java.util.Map;

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

    // header键值对
    // TODO: 按照HTTP/1.0标准改进键值对的存储模型
    public Map<String, String> header = new HashMap<>();

    // EntityBody
    public String entityBody;

    // 表单数据
    public Map<String, String> tableValues = new HashMap<>();

}
