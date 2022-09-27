package com.mine268.zhServer.gui.frame;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FrameWindow extends JFrame{

    private static FrameWindow object;
    public static FrameWindow GetInstance(){
        if(object == null) object = new FrameWindow();
        return object;
    }

    private JMenu fileMenu;
    private JMenuItem menuClose;


    private JMenu helpMenu;
    private JMenuItem menuAbout;

    private JTextArea textArea;
    private JLabel stateBar;

    public synchronized void setStatus(String status_str) {
        stateBar.setText("状态: "+status_str);
    }
    public synchronized void addLog(String log_str) {
        textArea.append(">>" + log_str + "\n");
    }

    private FrameWindow() {
        initComponents();
        initEventListeners();
        this.setLocationRelativeTo(null);
    }
    private void initComponents() {
        setTitle("projectZhServer - A Http 1.0 Server");
        setSize(500,350);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        initMenuBar();
        initTextArea();
        initStateBar();
    }
    private void initEventListeners() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initMenuBarListeners();
    }

    private void initMenuBar() {
        fileMenu = new JMenu("文件");
        menuClose = new JMenuItem("退出程序");
        fileMenu.add(menuClose);

        helpMenu = new JMenu("帮助");
        menuAbout = new JMenuItem("关于");
        helpMenu.add(menuAbout);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
    private void initTextArea() {
        JLabel logLabel = new JLabel(" ----- 服务器日志 Logs ----- ");
        getContentPane().add(logLabel,BorderLayout.NORTH);
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        JScrollPane panel = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panel, BorderLayout.CENTER);
        addLog("欢迎使用projectZhServer!");
    }
    private void initStateBar() {
        stateBar = new JLabel("状态: 就绪");
        stateBar.setHorizontalAlignment(SwingConstants.LEFT);
        getContentPane().add(stateBar, BorderLayout.SOUTH);
    }

    private void initMenuBarListeners() {
        menuClose.addActionListener((event)->{
            System.exit(0);
        });
        menuAbout.addActionListener((event)->
                JOptionPane.showOptionDialog(null,
                        "projectZhServer",
                        "关于",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,null,null));
    }
}
