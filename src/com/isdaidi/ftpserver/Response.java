package com.isdaidi.ftpserver;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import javax.swing.JTextArea;

import com.isdaidi.superserver.ServerMain;

public class Response {
	OutputStream output;
	Request request;
	File workingDir;
	public static final int WELCOME = 101; // send welcome message
	public static final int GET_PSW = 102; // get psw
	public static final int LOGIN_SUCC = 103; // login succ
	public static final int PSW_ERROR = 104; // psw error , login fail
	public static final int ERROR_CMD = 105; // error command

	public static final int ERROR = -1;
	private JTextArea txtLog;

	public Response(OutputStream output, JTextArea txtLog) {
		this.txtLog = txtLog;
		this.output = output;
	}

	public void setRequest(Request rq) {
		request = rq;
	}

	public void setWorkingDir(String wd) {
		workingDir = new File(wd);
	}

	public boolean sendEventMsg(int evt) throws IOException {
		try {
			switch (evt) {
			case WELCOME:
				sendMsg("220 My FTP Server ready ...");
				break;
			case GET_PSW:
				sendMsg("331 Password required for " + request.getUser());
				break;
			case LOGIN_SUCC:
				sendMsg("230 User " + request.getUser() + " logged in.");
				break;
			case PSW_ERROR:
				sendMsg("530 Login or Password incorrect.");
				break;
			case ERROR_CMD:
				sendMsg("221 command error. disconnect");
				break;
			case Request.SYSTEM:
				sendMsg("215 UNIX Type: L8");
				break;
			case Request.RETRIEVE:
				sendMsg("150 Opening data connection for "
						+ request.getFilename());
				sendFile();
				break;
			case Request.STORE:
				sendMsg("150 Opening data connection for "
						+ request.getFilename());
				receiveFile();
				break;
			case Request.PRINT_WORKING_DIRECTORY:
				sendMsg("257 \"" + getDir() + "\" is current directory.");
				break;
			case Request.LIST:
				sendMsg("150 Opening data connection for directory list.");
				sendDirMsg();
				sendMsg("226 File sent ok.");
				break;
			case Request.NOOP:
				sendMsg("200 NOOP command successful.");
				break;
			case Request.LOGOUT:
				// sendMsg("221 Bye Bye ...");
				break;
			case Request.CURRENT_WORKING_DIRECTORY:
				changeDir();
				sendMsg("250 CWD command successful. \"" + getDir()
						+ "\" is current directory");
				break;
			case Request.DATA_PORT:
				sendMsg("200 Port command successful..");
				break;
			case Request.CHANGE_DIR_UP:
				changeUpDir();
				sendMsg("250 CWD command successful. " + getDir()
						+ " is current directory.");
				break;
			case Request.REPRESENTATION_TYPE:
				sendMsg("215 TYPE command successful. " );
				break;
			default:
				sendMsg("500 '" + request.getRequestCmd()
						+ "': command not understood.");
				break;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			txtLog.append("FTP服务器：Response.sendEventMsg()返回消息出错！错误日志：\n" + e
					+ "\n");
			return false;
		}
	}

	// send response message
	public void sendMsg(String sendMsg) throws IOException {
		byte[] buf = new byte[2];
		buf[0] = 13;
		buf[1] = 10;
		try {
			output.write(sendMsg.getBytes(), 0, sendMsg.length());
			output.write(buf, 0, 2);
		} catch (Exception e) {
			e.printStackTrace();
			txtLog.append("FTP服务器：Response发送消息 “" + sendMsg + "”时出现异常！错误日志： "
					+ e+ "\n");
		}
	}

	// change working directory
	private void changeDir() {
		String curDir = request.getFilename();
		if (curDir.charAt(0) == '/') {
			curDir = curDir.replace('/', '\\');
			if (curDir.charAt(curDir.length() - 1) == '\\'
					&& curDir.length() > 1)
				curDir = curDir.substring(0, curDir.length() - 1);
			workingDir = new File(ServerMain.rootDir + curDir);
		} else {
			workingDir = new File(workingDir.getPath() + File.separator
					+ curDir);
		}
	}

	// change the upper directory
	private void changeUpDir() {
		String curDir = workingDir.getPath();
		if (curDir.charAt(curDir.length() - 1) == '\\' && curDir.length() > 1)
			curDir = curDir.substring(0, curDir.length() - 1);
		int index = curDir.length();
		int tmpIndex = 0;
		while ((tmpIndex = curDir.indexOf('\\', tmpIndex + 1)) > 0)
			index = tmpIndex;
		curDir = curDir.substring(0, index);
		workingDir = new File(curDir);
	}

	// get the current working directory
	private String getDir() {
		String curDir = workingDir.getPath();
		String relativeDir = curDir.substring(ServerMain.rootDir.length(),
				curDir.length());
		relativeDir = relativeDir + File.separator;
		relativeDir = relativeDir.replace('\\', '/');
		return relativeDir;
	}

	// list files in the working directory
	private void sendDirMsg() throws IOException {
		try {
			Socket socket;
			socket = new Socket(request.getIP(), request.getPort());

			OutputStream output = socket.getOutputStream();
			String dirMsg = new String();

			File[] fileList = workingDir.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				String msg = "";
				if (fileList[i].isDirectory())
					msg = "drwxr-xr-x   1 ftp      ftp            0";
				else {
					msg = "-rw-r--r--   1 ftp      ftp";
					String fileSize = String.valueOf(fileList[i].length());
					for (int j = 0; j < 22 - fileSize.length(); j++)
						fileSize = " " + fileSize;
					msg += fileSize;
				}
				msg += " Nov 04 18:15 ";
				msg += fileList[i].getName();
				msg += "\r\n";
				dirMsg += msg;
			}
			if (fileList.length == 0)
				dirMsg = "\r\n";
			output.write(dirMsg.getBytes(), 0, dirMsg.length());
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			txtLog.append("FTP服务器：Response.sendDirMsg()出现异常！错误日志： " + e+ "\n");
		}
	}

	// send file
	private void sendFile() throws IOException {
		int BUFFER_SIZE = 1024;
		try {
			Socket socket;
			socket = new Socket(request.getIP(), request.getPort());

			OutputStream output = socket.getOutputStream();

			byte[] bytes = new byte[BUFFER_SIZE];
			FileInputStream fis = null;
			try {
				File file = new File(workingDir.getPath(),
						request.getFilename());
				if (file.exists()) {
					fis = new FileInputStream(file);
					int ch = fis.read(bytes, 0, BUFFER_SIZE);
					while (ch != -1) {
						output.write(bytes, 0, ch);
						ch = fis.read(bytes, 0, BUFFER_SIZE);
					}
					sendMsg("226 File sent ok.");
				} else {
					// file not found
					sendMsg("550 cannot open File: " + request.getFilename()
							+ " : No Such File.");
				}
			} catch (Exception e) {
				// thrown if cannot instantiate a File object
				e.printStackTrace();
				txtLog.append("FTP服务器：Response.sendFile()出现异常！错误日志： " + e+ "\n");
			} finally {
				if (fis != null)
					fis.close();
			}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			txtLog.append("FTP服务器：Response.sendFile()出现异常！错误日志： " + e+ "\n");
		}
	}

	// receive file to current working directory
	private void receiveFile() throws IOException {
		int BUFFER_SIZE = 1024;
		try {
			Socket socket;
			socket = new Socket(request.getIP(), request.getPort());

			InputStream input = socket.getInputStream();

			byte[] bytes = new byte[BUFFER_SIZE];
			FileOutputStream fos = null;
			try {
				File file = new File(workingDir.getPath(),
						request.getFilename());
				if (file.createNewFile()) {
					fos = new FileOutputStream(file);

					int ch = input.read(bytes, 0, BUFFER_SIZE);
					while (ch != -1) {
						fos.write(bytes, 0, ch);
						ch = input.read(bytes, 0, BUFFER_SIZE);
					}
					sendMsg("226 File received ok.");
				} else {
					// file can't write
					sendMsg("550 cannot upload File:" + request.getFilename());
				}
			} catch (Exception e) {
				// thrown if cannot instantiate a File object
				e.printStackTrace();
				txtLog.append("FTP服务器：Response.receiveFile()出现异常！错误日志： " + e+ "\n");
			} finally {
				if (fos != null)
					fos.close();
			}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			txtLog.append("FTP服务器：Response.receiveFile()出现异常！错误日志： " + e + "\n");
		}
	}
}