package com.isdaidi.superserver;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import javax.swing.*;

import com.isdaidi.ftpserver.FtpServer;
import com.isdaidi.webserver.WebServer;

public class ServerMain extends JFrame implements ActionListener {
	private Thread ftpThread;
	private Thread webThread;
	private JTextField txtUri;
	private JButton btnStartFtp;
	private JButton btnStartWeb;
	private JButton btnExit;
	private JButton btnChoose;
	private JPanel contain;
	private JPanel contain1;
	private JTextArea txtLog;
	private JFileChooser fileChooser;

	public static final int ftpPort = 21;
	public static final int webPort = 8080;
	public static String rootDir = System.getProperty("user.dir")
			+ File.separator + "root";

	Hashtable userList = new Hashtable();
	ServerSocket serverSocket = null, rServerSocket = null;
	Socket socket = null, rSocket = null;

	public ServerMain() {
		super("超级服务器");// 命名
		Container container = getContentPane();
		txtLog = new JTextArea();
		txtLog.setLineWrap(true);
		txtUri = new JTextField(20);
		fileChooser = new JFileChooser();
		btnStartFtp = new JButton("打开FTP服务器");
		btnStartWeb = new JButton("打开WEB服务器");
		btnExit = new JButton("退出");
		btnChoose = new JButton("选择工作目录");
		btnStartFtp.addActionListener(this);
		btnStartWeb.addActionListener(this);
		btnExit.addActionListener(this);
		btnChoose.addActionListener(this);
		contain = new JPanel();
		contain1 = new JPanel();
		container.setLayout(new BorderLayout());
		container.add(contain1, BorderLayout.NORTH);
		contain1.add(txtUri);
		contain1.add(btnChoose);
		container.add(contain, BorderLayout.SOUTH);
		contain.add(btnStartFtp);
		contain.add(btnStartWeb);
		contain.add(btnExit);

		container.add(new JScrollPane(txtLog), BorderLayout.CENTER);
		setSize(500, 300);
		setVisible(true);

		this.validate();
		setLocationRelativeTo(null);// 窗体居中
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStartWeb) {
			if (webThread == null) {
				rootDir = txtUri.getText();
				txtLog.append("尝试设定工作目录：" + rootDir + "\n");
				if (rootDir == null || "".equals(rootDir)) {
					txtLog.append("设定失败，请检查是否设置工作目录。\n");
				} else {

					WebServer webRunner = new WebServer(txtLog);
					txtLog.append("设定成功，尝试打开WEB服务…\n");
					webThread = new Thread(webRunner);
					webThread.start();
				}
			} else {
				txtLog.append("WEB服务已经启动，无需再次打开！\n");
			}
		} else if (e.getSource() == btnStartFtp) {
			if (ftpThread == null) {
				rootDir = txtUri.getText();
				txtLog.append("尝试设定工作目录：" + rootDir + "\n");
				if (rootDir == null || "".equals(rootDir)) {
					txtLog.append("设定失败，请检查是否设置工作目录。\n");
				} else {
					FtpServer ftpRunner = new FtpServer(txtLog);
					txtLog.append("设定成功，尝试打开FTP服务…\n");
					ftpThread = new Thread(ftpRunner);
					ftpThread.start();
				}
			} else {
				txtLog.append("FTP服务已经启动，无需再次打开！\n");
			}
		} else if (e.getSource() == btnExit) {
			System.exit(0);
		} else if (e.getSource() == btnChoose) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int intRetVal = fileChooser.showOpenDialog(this);
			if (intRetVal == JFileChooser.APPROVE_OPTION) {
				txtUri.setText(fileChooser.getSelectedFile().getPath());
			}
		}
	}

	public static void main(String[] args) {
		ServerMain sm = new ServerMain();
	}

}
