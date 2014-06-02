package com.isdaidi.ftpserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.awt.event.*;
import java.net.*;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.isdaidi.superserver.ServerMain;

public class FtpServer implements Runnable {
	public String rootDir = ServerMain.rootDir; // default ftp directory
	private int ftpPort = ServerMain.ftpPort;

	private JTextArea txtLog;
	Socket socket = null;

	public FtpServer(JTextArea txtLog) {
		this.txtLog = txtLog;
	}

	public void run() {
		txtLog.append("Ftp��������������...\n");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(ftpPort);
		} catch (IOException e) {
			txtLog.append("Ftp����������ʧ�ܣ�������־��\n" + e);
			e.printStackTrace();
		}
		txtLog.append("Ftp�����������ɹ������ڼ���"+ftpPort+"�˿ڡ�\n");
		while (true) {

			try {
				socket = serverSocket.accept();
				new Thread(new FtpThread(socket, txtLog)).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
