package com.mine268.zhServer.parser;

public class HttpRequestParser {

    /**
     * 解析请求内容，返回请求的解析结果。
     * @param context 请求的完整内容
     * @return 解析的结果
     */
    public static HttpRequest parse(String context) throws HttpRequestException {
        var ret = new HttpRequest();
        var curr_ix = 0;
        String logic_line;
        String[] logic_line_sep;
        NoParamRet<String> get_next_line = () -> {
            int tmp_ix = curr_ix;
            while (tmp_ix < context.length() && context.charAt(tmp_ix) != '\n') {
                ++tmp_ix;
            }
            return context.substring(curr_ix, tmp_ix).trim();
        }; // 获取下一个逻辑行

        // 获取Request-Line或者Simple-Request
        logic_line = get_next_line.impl()
                .replaceAll("\\s{2,}", " ");
        logic_line_sep = logic_line.split("\\s");
        if (logic_line_sep.length == 2) { // Simple-Request
            if (!logic_line_sep[0].equals("GET")) {
                throw new HttpRequestException("HTTP/0.?不支持此方式：" + logic_line);
            }
            ret.requestType = HttpRequest.RequestType.GET;
            ret.httpMajorVersion = 0;
            ret.httpMinorVersion = -1;
        } else if (logic_line_sep.length == 3) { // Request-Line
            if (!logic_line_sep[2].endsWith("1.0")) { // 仅支持HTTP/1.0
                throw new HttpRequestException("不受支持的版本：" + logic_line_sep[2]);
            }
            ret.requestType = requestParse(logic_line_sep[0]);
            ret.httpMajorVersion = 1;
            ret.httpMajorVersion = 0;
        } else {
            throw new HttpRequestException("无法解析首行：" + logic_line);
        }
        if (uriCheck(logic_line_sep[1])) {
            ret.requestURI = logic_line_sep[1];
        } else {
            throw new HttpRequestException("不正确的uri：" + logic_line);
        }

        // 逐行解析剩下的General/Request/Entity-header

        // 处理Entity-Body

        return ret;
    }

    /**
     * 检查URI是否合法
     * @param uri uri
     * @return 合法则为 {@code true}，反之为 {@code false}
     */
    private static boolean uriCheck(String uri) {
        return uri.matches(
                "(([a-zA-Z][0-9a-zA-Z+\\\\\\\\-\\\\\\\\.]*:)?" +
                "/{0,2}[0-9a-zA-Z;/?:@&=+$\\\\\\\\.\\\\\\\\-_!~*'()%]+)?" +
                "(#[0-9a-zA-Z;/?:@&=+$\\\\\\\\.\\\\\\\\-_!~*'()%]+)?");
    }

    /**
     * 检测并返回请求类型，若为未知则返回 {@code RequestType.POST}
     * @param req 请求类型的文本
     * @return 枚举类型的结果
     */
    private static HttpRequest.RequestType requestParse(String req) {
        return switch (req) {
            case "GET" -> HttpRequest.RequestType.GET;
            case "POST" -> HttpRequest.RequestType.POST;
            case "PUT" -> HttpRequest.RequestType.PUT;
            case "DELETE" -> HttpRequest.RequestType.DELETE;
            case "LINK" -> HttpRequest.RequestType.LINK;
            case "UNLINK" -> HttpRequest.RequestType.UNLINK;
            default -> HttpRequest.RequestType.EXT;
        };
    }

    /**
     * 函数式接口，无参数，有返回值
     * @param <T> 返回值的类型
     */
    private interface NoParamRet<T> {
        T impl();
    }
}
