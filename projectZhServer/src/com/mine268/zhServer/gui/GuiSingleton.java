package com.mine268.zhServer.gui;

import com.mine268.zhServer.gui.frame.FrameWindow;
import com.mine268.zhServer.gui.runner.MainWindow;

public class GuiSingleton {
    private static GuiSingleton object;

    public static GuiSingleton GetInstance(){
        if(object == null) object = new GuiSingleton();
        return object;
    }
    public void addLog(String str){
        FrameWindow.GetInstance().addLog(str);
    }
    public void addLog(byte[] str){
        FrameWindow.GetInstance().addLog(new String(str));
    }
    public void setStatus(String str) {
        FrameWindow.GetInstance().setStatus(str);
    }
    private GuiSingleton(){
        Thread window = new Thread(new MainWindow());
        window.start();
    }
}
