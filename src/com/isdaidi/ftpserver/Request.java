package com.isdaidi.ftpserver;

import java.io.InputStream;
import java.io.IOException;

import javax.swing.JTextArea;

public class Request {
	public static final int LOGIN_USER = 0; // USER
	public static final int LOGIN_PASS = 1; // PASS
	public static final int SYSTEM = 2; // SYST
	public static final int RESTART = 3; // REST
	public static final int RETRIEVE = 4; // RETR
	public static final int STORE = 5; // STOR
	public static final int RENAME_FROM = 6; // RNFR
	public static final int RENAME_TO = 7; // RNTO
	public static final int ABORT = 8; // ABOR
	public static final int DELETE = 9; // DELE
	public static final int MAKE_DIRECTORY = 10;// MKD
	public static final int PRINT_WORKING_DIRECTORY = 11; // PWD
	public static final int LIST = 12; // LIST
	public static final int NOOP = 13; // NOOP
	public static final int REPRESENTATION_TYPE = 14; // TYPE
	public static final int LOGOUT = 15; // QUIT
	public static final int DATA_PORT = 16; // PORT
	public static final int PASSIVE = 17; // PASV
	public static final int CURRENT_WORKING_DIRECTORY = 18; // CWD
	public static final int CHANGE_DIR_UP = 19; // CDUP
	public static final int UNKNOWN_CMD = 99; // unknow cmd

	public static final int PORT = 0;
	public static final int PASV = 1;

	public static final int ERROR = -1;

	private InputStream input;
	private JTextArea txtLog;
	private String request;
	private int requestType;
	private String cmd;
	private String user;
	private String psw;
	private String fileName;
	private int transferType;
	private int port;
	private String ip;
	private String representationType;

	public String getRequest() {
		return request;
	}

	public String getRequestCmd() {
		return cmd;
	}

	public int getRequestType() {
		return requestType;
	}

	public String getUser() {
		return user;
	}

	public String getPsw() {
		return psw;
	}

	public String getFilename() {
		return fileName;
	}

	public int getTransferType() {
		return transferType;
	}

	public int getPort() {
		return port;
	}

	public String getIP() {
		return ip;
	}

	public String getRepresentationType() {
		return representationType;
	}

	public Request() {
		requestType = ERROR;
		transferType = ERROR;
		port = 0;
	}

	public Request(InputStream input, JTextArea txtLog) {
		this.input = input;
		this.txtLog = txtLog;
	}

	private void parse() {
		int index = request.indexOf(' ');
		if (index < 0)
			cmd = request;
		else
			cmd = request.substring(0, index);
		if (cmd.equals("USER")) {
			requestType = LOGIN_USER;
			user = request.substring(index + 1, request.length());
			return;
		}
		if (cmd.equals("PASS")) {
			requestType = LOGIN_PASS;
			psw = request.substring(index + 1, request.length());
			return;
		}
		if (cmd.equals("SYST")) {
			requestType = SYSTEM;
			return;
		}
		if (cmd.equals("REST")) {
			requestType = RESTART;
			return;
		}
		if (cmd.equals("RETR")) {
			requestType = RETRIEVE;
			fileName = request.substring(index + 1, request.length());
			return;
		}
		if (cmd.equals("STOR")) {
			requestType = STORE;
			fileName = request.substring(index + 1, request.length());
			return;
		}
		if (cmd.equals("RNFR")) {
			requestType = RENAME_FROM;
			fileName = request.substring(index + 1, request.length());
			return;
		}
		if (cmd.equals("RNTO")) {
			requestType = RENAME_TO;
			fileName = request.substring(index + 1, request.length());
			return;
		}
		if (cmd.equals("ABOR")) {
			requestType = ABORT;
			return;
		}
		if (cmd.equals("DELE")) {
			requestType = DELETE;
			fileName = request.substring(index + 1, request.length());
			return;
		}
		if (cmd.equals("PWD")) {
			requestType = PRINT_WORKING_DIRECTORY;
			return;
		}
		if (cmd.equals("LIST")) {
			requestType = LIST;
			return;
		}
		if (cmd.equals("NOOP")) {
			requestType = NOOP;
			return;
		}
		if (cmd.equals("TYPE")) {
			requestType = REPRESENTATION_TYPE;
			representationType = request.substring(index + 1, request.length());
			return;
		}
		if (cmd.equals("QUIT")) {
			requestType = LOGOUT;
			return;
		}
		if (cmd.equals("PORT")) {
			requestType = DATA_PORT;
			transferType = PORT;
			port = parsePort(request.substring(index + 1, request.length()));
			ip = parseIP(request.substring(index + 1, request.length()));
			return;
		}
		if (cmd.equals("PASV")) {
			requestType = PASSIVE;
			transferType = PASV;
			return;
		}
		if (cmd.equals("CWD")) {
			requestType = CURRENT_WORKING_DIRECTORY;
			fileName = request.substring(index + 1, request.length());
			return;
		}
		if (cmd.equals("CDUP")) {
			requestType = CHANGE_DIR_UP;
			return;
		}
		System.out.println(cmd);
		requestType = UNKNOWN_CMD;
	}

	private String parseIP(String portCmd) {
		int index = -1;
		for (int i = 0; i < 4; i++)
			index = portCmd.indexOf(',', index + 1);
		String ip = portCmd.substring(0, index);
		return ip.replace(',', '.').trim();
	}

	private int parsePort(String portCmd) {
		int index = -1;
		for (int i = 0; i < 4; i++)
			index = portCmd.indexOf(',', index + 1);
		String portStr = portCmd.substring(index + 1, portCmd.length()).trim();
		index = portStr.indexOf(',');
		int high = Integer.parseInt(portStr.substring(0, index));
		int low = Integer.parseInt(portStr.substring(index + 1,
				portStr.length()));
		return high * 0x100 + low;
	}

	public String getMsg() {
		StringBuffer requestBuf = new StringBuffer(2048);
		int i;
		byte[] buffer = new byte[2048];
		try {
			i = input.read(buffer);
		} catch (IOException e) {
			txtLog.append("FTP服务器：Request.getMsg()出现问题！错误日志：\n" + e + "\n");
			e.printStackTrace();
			i = -1;
		}
		for (int j = 0; j < i; j++) {
			requestBuf.append((char) buffer[j]);
		}
		request = requestBuf.toString().trim();
		parse();
		return request;
	}

}