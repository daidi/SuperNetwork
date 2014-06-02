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
			txtLog.append("WEB��������������...\n");
			server = new ServerSocket(webPort);
		} catch (IOException e) {
			txtLog.append("WEB����������ʧ�ܣ�������־��\n" + e);
			e.printStackTrace();
		}
		txtLog.append("WEB�����������ɹ������ڼ���"+webPort+"�˿ڡ�\n");
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
