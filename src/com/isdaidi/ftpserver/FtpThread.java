package com.isdaidi.ftpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JTextArea;

import com.isdaidi.superserver.ServerMain;

public class FtpThread implements Runnable {
	private InputStream input = null;
	private OutputStream output = null;
	private JTextArea txtLog;
	private Socket socket = null;
	private String rootDir = ServerMain.rootDir; // default ftp directory
	private String username = "anonymous";
	private String password = "test";

	public FtpThread(Socket socket, JTextArea txtLog) {
		this.socket=socket;
		this.txtLog=txtLog;
	}

	@Override
	public void run() {
		txtLog.append("Ftp服务器：用户 "+socket.getInetAddress()+":"+socket.getPort()+" 连接到FTP服务器上。\n");
		try {
			input = socket.getInputStream();
			output = socket.getOutputStream();
			Request request = new Request(input, txtLog);
			Response response = new Response(output, txtLog);
			response.setRequest(request);
			response.setWorkingDir(rootDir);
			response.sendEventMsg(Response.WELCOME);
			request.getMsg(); // get user name
			boolean flag=true;
			if (request.getRequestType() != Request.LOGIN_USER) {
				response.sendEventMsg(Response.ERROR_CMD);
				socket.close();		
				flag=false;
			}
			response.sendEventMsg(Response.GET_PSW); // get user password
			request.getMsg();
			if (request.getRequestType() != Request.LOGIN_PASS) {
				response.sendEventMsg(Response.ERROR_CMD);
				socket.close();	
				flag=false;
			}
			if (!request.getUser().equals(username)
					|| !request.getPsw().equals(password)) { // user
																// password
																// uncorrect
				response.sendEventMsg(Response.PSW_ERROR);
				socket.close();	
				flag=false;
			}
			response.sendEventMsg(Response.LOGIN_SUCC); // login
			while (request.getRequestType() != Request.LOGOUT && flag) {
				System.out.println(request.getRequestType());
				request.getMsg();
				response.sendEventMsg(request.getRequestType());
			}
			// Close the socket
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
