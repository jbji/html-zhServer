package com.mine268.zhServer;

import com.mine268.zhServer.gui.GuiSingleton;
import com.mine268.zhServer.gui.runner.MainWindow;
import com.mine268.zhServer.logger.Logger;
import com.mine268.zhServer.webServer.WebServer;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException {
        /*
        Logger logger = new Logger(System.out);

        logger.log("localhost", "chrome",
                "11332", new Date().toString(),
                "GET", "/",
                200, 39943, "/index.html");
        */

//        GuiSingleton.GetInstance();

        WebServer webServer = new WebServer(6789,10,12,
                60, TimeUnit.SECONDS,10);
        webServer.listen();
    }
}
