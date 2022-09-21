package com.mine268.zhServer;

import com.mine268.zhServer.gui.GuiSingleton;
import com.mine268.zhServer.gui.runner.MainWindow;
import com.mine268.zhServer.logger.Logger;
import com.mine268.zhServer.webServer.WebServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        /*
        Logger logger = new Logger(System.out);

        logger.log("localhost", "chrome",
                "11332", new Date().toString(),
                "GET", "/",
                200, 39943, "/index.html");
        */

//        GuiSingleton.GetInstance();
//
//        try {
//            ClassLoader cl = new URLClassLoader(new URL[] { new URL("file:D:\\study\\7-2022-2023-1\\7-Computer-Network-Course-Design\\html-zhServer\\projectZhServer\\webroot\\cgi-bin\\test.jar") });
//            Class<?> clazz = cl.loadClass("webserver.cgi.CGIClass");
//            Method method = clazz.getMethod("run", String.class);
//            String ret = (String) method.invoke(clazz.getDeclaredConstructor().newInstance(), "2333");
//            System.out.println(ret);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        WebServer webServer = new WebServer(6789,10,12,
                60, TimeUnit.SECONDS,10);
        webServer.listen();
    }
}
