package com.mine268.zhServer.parser;

public class TestMain {
    public static void main(String[] args) {
        String[] heads = {
                "GET https://www.baidu.com",
                "GET /indexes/index.html?fname=0&rname=2 HTTP/1.0",
                "POST / HTTP/1.2",
                "DELETE /vault/stu010 HTTP/1.0"
        };

        for (var head : heads) {
            try {
                var res = HttpRequestParser.parse(head);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
