package com.mine268.zhServer.parser;

public class TestMain {
    public static void main(String[] args) {
//        decodeTest();
        parseTest();
    }

    public static void decodeTest() {
        String[] decode = {
                "%E6%88%91%20%E8%8D%89%20%E6%B3%A5%20%E9%A9%AC",
                "%3d",
                "Tom%20is%2b%3D%3d",
                "%3f%3F",
                "%2F%25",
        };

        for (var code : decode) {
            try {
                System.out.println(HttpRequestParser.httpDecode(code));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void parseTest() {
        String[] heads = {
                """
                GET https://www.baidu.com?fname=2333&uuid=9900 HTTP/1.0
                Date: Tue,\040
                 15 Nov 1994 08:12:31 GMT
                Expires: Thu, 01 Dec 1994 16:00:00 GMT
                From: webmaster@w3.org
                If-Modified-Since: Sat, 29 Oct 1994 19:43:31 GMT
                Link: <mailto:timbl@w3.org>; rev="Made"; title="Tim Berners-Lee"
                
                <h1>2333</h1>
                """,
                "POST https://www.baidu.com HTTP/1.0\n\nuuid=23874789324",
                "GET /indexes/index.html?fname=0&rname=2 HTTP/1.0",
                "POST / HTTP/1.2",
                "GET /pages/kill.html HTTP/1.2",
                "ab\n cd\nef\n gh",
                "DELETE /vault/stu010 HTTP/1.0"
        };
        for (var head : heads) {
            try {
                var res = new HttpRequestParser().parse(head);
                System.out.printf("成功解析：%s\n", head);
            } catch (Exception ex) {
                System.out.printf("无法解析[%s]：%s\n", ex.getMessage(), head);
            }
        }
    }

}
