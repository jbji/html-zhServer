package com.mine268.zhServer;

import com.mine268.zhServer.Logger.Logger;

import java.io.IOException;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {
        Logger logger = new Logger(System.out);

        logger.log("localhost", "chrome",
                "11332", new Date().toString(),
                "GET", "/",
                200, 39943, "/index.html");
    }
}
