package com.isdaidi.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;

import com.isdaidi.superserver.ServerMain;

public class WebServer implements Runnable {
	private int webPort = ServerMain.webPort;
	private JTextArea txtLog;

	public WebServer(JTextArea txtLog) {
		this.txtLog = txtLog;
	}

	@Override
	public void run() {
		ServerSocket server = null;
		Socket client = null;
		try {
			txtLog.append("WEB服务器正在启动...\n");
			server = new ServerSocket(webPort);
		} catch (IOException e) {
			txtLog.append("WEB服务器启动失败！错误日志：\n" + e);
			e.printStackTrace();
		}
		txtLog.append("WEB服务器启动成功！正在监听"+webPort+"端口。\n");
		while (true) {
			try {
				client = server.accept();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			new ConnectionThread(client, txtLog).start();

		}
	}
}
