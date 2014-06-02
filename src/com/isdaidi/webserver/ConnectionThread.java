package com.isdaidi.webserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.JTextArea;

import com.isdaidi.superserver.ServerMain;

class ConnectionThread extends Thread {
	Socket socket; // 连接Web浏览器的socket字
	int counter; // 计数器
	private JTextArea txtLog;

	public String rootDir = ServerMain.rootDir; // default web directory

	public ConnectionThread(Socket cl, JTextArea txtLog) {
		socket = cl;
		this.txtLog = txtLog;
	}

	public void run() // 线程体
	{
		try {
			String destIP = socket.getInetAddress().toString(); // 客户机IP地址
			int destport = socket.getPort(); // 客户机端口号
			txtLog.append("WEB服务器：用户 " + socket.getInetAddress() + ":"
					+ socket.getPort() + " 连接到WEB服务器上。\n");
			PrintStream outstream = new PrintStream(socket.getOutputStream());

			BufferedReader d = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String inline = d.readLine(); // 读取Web浏览器提交的请求信息
			txtLog.append("WEB服务器：接收到用户发来请求："+inline);
			if (getrequest(inline)) { // 如果是GET请求
				String filename = getfilename(inline);
				File file = new File(rootDir+File.separator+filename);
				if (file.exists()) { // 若文件存在，则将文件送给Web浏览器
					System.out.println(filename + " requested.");
					outstream.println("HTTP/1.0 200 OK");
					outstream.println("MIME_version:1.0");
					outstream.println("Content_Type:text/html");
					int len = (int) file.length();
					outstream.println("Content_Length:" + len);
					outstream.println("");
					sendfile(outstream, file); // 发送文件
					outstream.flush();
				} else { // 文件不存在时
					String notfound = "<html><head><title>Not Found</title></head><body><h1>Error 404-file not found</h1></body></html>";
					outstream.println("HTTP/1.0 404 no found");
					outstream.println("Content_Type:text/html");
					outstream
							.println("Content_Length:" + notfound.length() + 2);
					outstream.println("");
					outstream.println(notfound);
					outstream.flush();
				}
			}
			long m1 = 1;
			while (m1 < 11100000) {
				m1++;
			} // 延时
			socket.close();
		} catch (IOException e) {
			txtLog.append("WEB服务器：产生异常："+e);
		}
	}

	/* 获取请求类型是否为“GET” */
	boolean getrequest(String s) {
		System.out.println(s);
		if (s.length() > 0) {
			if (s.substring(0, 3).equalsIgnoreCase("GET"))
				return true;
		}
		return false;
	}

	/* 获取要访问的文件名 */
	String getfilename(String s) {
		String f = s.substring(s.indexOf(' ') + 1);
		f = f.substring(0, f.indexOf(' '));
		try {
			if (f.charAt(0) == '/')
				f = f.substring(1);
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println("Exception:" + e);
		}
		if (f.equals(""))
			f = "index.html";
		return f;
	}

	/* 把指定文件发送给Web浏览器 */
	void sendfile(PrintStream outs, File file) {
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int len = (int) file.length();
			byte buf[] = new byte[len];
			in.readFully(buf);
			outs.write(buf, 0, len);
			outs.flush();
			in.close();
		} catch (Exception e) {
			System.out.println("Error retrieving file.");
			System.exit(1);
		}
	}
}
