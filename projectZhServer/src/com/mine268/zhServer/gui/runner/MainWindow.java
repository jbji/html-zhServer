package com.mine268.zhServer.gui.runner;

import com.mine268.zhServer.gui.frame.FrameWindow;

import javax.swing.*;

public class MainWindow implements Runnable{
    public void run(){
        SwingUtilities.invokeLater(()-> FrameWindow.GetInstance().setVisible(true));
    }
}
