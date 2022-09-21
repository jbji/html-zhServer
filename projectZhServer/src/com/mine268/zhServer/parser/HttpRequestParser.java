package com.mine268.zhServer.parser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference:
 * <a href="https://www.w3.org/Protocols/HTTP/1.0/draft-ietf-http-v10-spec-01.html">
 *     Hypertext Transfer Protocol -- HTTP/1.0</a>
 */
public class HttpRequestParser {

    private int curr_ix = 0;

    private String headerContext;

    /**
     * 解析请求内容，返回请求的解析结果。
     * @param context 请求的完整内容
     * @return 解析的结果
     */
    public HttpRequest parse(String context) throws HttpRequestException {
        var ret = new HttpRequest();
        headerContext = context;
        String logic_line;
        String[] logic_line_sep;
        String uri_with_arg;

        // 设置请求到达的时间
        ret.hitTime = new Date();

        // 获取Request-Line或者Simple-Request
        logic_line = getNextLine()
                .replaceAll("\\s{2,}", " ");
        logic_line_sep = logic_line.split("\\s");
        // 判断HTTP版本并设置ret
        if (logic_line_sep.length == 2) { // Simple-Request
            if (!logic_line_sep[0].equals("GET")) {
                throw new HttpRequestException("HTTP/0.?不支持此方式：" + logic_line);
            }
            ret.requestType = HttpRequest.RequestType.GET;
            ret.httpMajorVersion = 0;
            ret.httpMinorVersion = -1;
        } else if (logic_line_sep.length == 3) { // Request-Line
            if (!(logic_line_sep[2].endsWith("1.0") || logic_line_sep[2].endsWith("1.1"))) { // 仅支持HTTP/1.0 1.1
                throw new HttpRequestException("不受支持的版本：" + logic_line_sep[2]);
            }
            ret.requestType = requestParse(logic_line_sep[0]);
            ret.httpMajorVersion = 1;
            ret.httpMajorVersion = 0;
        } else {
            throw new HttpRequestException("无法解析首行：" + logic_line);
        }
        // 检查URI是否正确
        if (uriCheck(logic_line_sep[1])) {
            var tmp_ix = logic_line_sep[1].indexOf("?");
            ret.requestURI = logic_line_sep[1].substring(0, tmp_ix == -1 ? logic_line_sep[1].length() : tmp_ix);
            uri_with_arg = logic_line_sep[1];
        } else {
            throw new HttpRequestException("不正确的uri：" + logic_line);
        }

        // 逐行解析剩下的General/Request/Entity-header
        boolean end_of_header = false;
        do {
            logic_line = getNextLine();
            end_of_header = logic_line.equals("");
            if (end_of_header) {
                break;
            }
            var colon_index = logic_line.indexOf(':');
            String[] k_v_array = { logic_line.substring(0, colon_index),
                    logic_line.substring(colon_index + 1).trim()};
            // http解码
            k_v_array[1] = httpDecode(k_v_array[1]);

            if (ret.header.containsKey(k_v_array[0])) {
                ret.header.put(k_v_array[0], "," + ret.header.get(k_v_array[0]) + k_v_array[1]);
            } else {
                ret.header.put(k_v_array[0], k_v_array[1]);
            }
        } while (true);

        // 处理Entity-Body
        ret.entityBody = headerContext.substring(curr_ix);

        // 处理表单参数
        if (ret.requestType == HttpRequest.RequestType.GET) {
            var tableStr = uri_with_arg.substring(uri_with_arg.indexOf("?") + 1);
            ret.tableValues = parseTableData(tableStr);
            ret.tableValuesStr = tableStr;
        } else if (ret.requestType == HttpRequest.RequestType.POST) {
            ret.tableValues = parseTableData(ret.entityBody);
            ret.tableValuesStr = ret.entityBody;
        }

        return ret;
    }

    /**
     * 解析表单提交的数据
     * @param context 表单数据字符串
     * @return 解析结果
     */
    private static Map<String, String> parseTableData(String context) {
        var kvs = context.split("&");
        var ret = new HashMap<String, String>();
        for (var kv : kvs) {
            var equal_ix = kv.indexOf("=");
            if (equal_ix != -1) {
                ret.put(
                        kv.substring(0, equal_ix),
                        kv.substring(equal_ix + 1)
                );
            }
        }
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
                "/{0,2}[\\-0-9a-zA-Z;/?:@&=+$\\\\\\\\.\\\\\\\\-_!~*'()%]+)?" +
                "(#[\\-0-9a-zA-Z;/?:@&=+$\\\\\\\\.\\\\\\\\-_!~*'()%]+)?");
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
     * 进行http解码
     * @param str 待解码的字符串
     * @return 解码完成后的字符
     */
    public static String httpDecode(String str) throws HttpRequestException {
        var strb = new StringBuilder();
        int start = 0, end = 0;
        while (end < str.length()) {
            start = end;
            while (end < str.length() && str.charAt(end) != '%' && str.charAt(end) != '+') {
                ++end;
            }
            strb.append(str.substring(start, end));
            if (end < str.length()) {
                switch (str.charAt(end)) {
                    case '+' -> {
                        strb.append(' ');
                        start = ++end;
                    }
                    case '%' -> {
                        int code_buffer, trailing_character = 0;
                        if (end + 2 < str.length()) {
                            code_buffer = Integer.parseInt(str.substring(end + 1, end + 3), 16);
                            if ((code_buffer & 0x80) == 0x00) { // 0XXX,XXXX
//                                trailing_character = 0;
                                code_buffer &= 0x7f;
                            } else if ((code_buffer & 0xe0) == 0xc0) { // 110X,XXXX
                                trailing_character = 1;
                                code_buffer &= 0x1f;
                            } else if ((code_buffer & 0xf0) == 0xe0) { // 1110,XXXX
                                trailing_character = 2;
                                code_buffer &= 0x0f;
                            } else if ((code_buffer & 0xf8) == 0xf0) { // 1111,0XXX
                                trailing_character = 3;
                                code_buffer &= 0x07;
                            }
                            while (trailing_character-- != 0) {
                                end += 3;
                                code_buffer <<= 6;
                                if (str.charAt(end) == '%' && end + 2 < str.length()) {
                                    var tmp_code_buffer = Integer.parseInt(str.substring(end + 1, end + 3), 16);
                                    if ((tmp_code_buffer & 0x80) != 0x80) {
                                        throw new HttpRequestException("不正确的UTF8编码：" + str);
                                    }
                                    code_buffer |= tmp_code_buffer & 0x3f;
                                } else {
                                    throw new HttpRequestException("不正确的UTF8编码：" + str);
                                }
                            }
                            strb.append((char) code_buffer);
                            end += 3;
                            start = end;
                        } else {
                            throw new HttpRequestException("不能转义的值：" + str);
                        }
                    }
                    default -> {
                    }
                }
            }
        }

        return strb.toString();
    }

    /**
     * 获取下一个逻辑行
     * @return 获取到的逻辑行
     */
    private String getNextLine() {
        int start = curr_ix, end = curr_ix;
        boolean end_of_logic_line = curr_ix >= headerContext.length() - 1;
        while(!end_of_logic_line) {
            end_of_logic_line = (end >= headerContext.length() - 1) ||
                    (headerContext.charAt(end) == '\n' && !isSpace(headerContext.charAt(end + 1)));
            ++end;
        }
        curr_ix = end;
        return headerContext.substring(start, end).replaceAll("\\n\\s+", "").trim();
    }

    /**
     * 判断是否是十六进制字符
     * @param c 字符
     * @return 是否是十六进制字符
     */
    private static boolean isHexDigit(char c) {
        return (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F') || (c >= '0' && c <= '9');
    }

    /**
     * 判断某个字符是不是空字符
     * @param c 字符
     * @return 判断结果
     */
    private static boolean isSpace(char c) {
        return c == ' ' || c == '\t';
    }
}
