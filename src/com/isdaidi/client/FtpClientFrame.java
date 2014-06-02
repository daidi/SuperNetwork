package com.isdaidi.client;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import sun.net.ftp.*;
import sun.net.*;

import java.io.*;
import java.util.Properties;

public class FtpClientFrame extends JFrame {
	JPanel contentPane;
	Label labelPrompt = new Label(); // 状态提示
	Label labelcopyright = new Label(); // 状态提示
	Label labelHost = new Label();
	TextField textFieldHost = new TextField("localhost"); // 主机地址
	Label labelUser = new Label();
	TextField textFieldUser = new TextField(); // 用户名
	Label labelPassword = new Label();
	TextField textFieldPassword = new TextField(); // 密码
	Button buttonLink = new Button(); // 连接按钮
	Button buttonDisconnect = new Button(); // 断开按钮
	Label labelFileShow = new Label();
	TextArea textAreaContent = new TextArea(); // 显示文件和目录的文本域
	Label labelFile = new Label();
	JFileChooser fileChooser = new JFileChooser();
	Button buttonDownload = new Button(); // 下载按钮
	Label labelChgDir = new Label();
	Button buttonCDChg = new Button(); // 改变目录按钮
	Button buttonCDUP = new Button(); // 返回上级目录按钮

	JComboBox cbxFile = new JComboBox();
	JComboBox cbxFolder = new JComboBox();

	FtpClient myFtp = null; // FtpClient对象
	TelnetInputStream inStream = null; // 输入流?

	public FtpClientFrame() {
		try {
			jbInit(); // 界面初始化
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 界面初始化并显示
	private void jbInit() throws Exception {
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(null);
		labelcopyright.setText("网络实践作业  姓名：戴頔   学号：13S003116   哈尔滨工业大学计算机学院");

		labelcopyright.setBounds(new Rectangle(25, 450, 420, 22));
		labelPrompt.setText("输入用户名、密码登录ftp，若不输入，则默认使用匿名登录。");
		labelPrompt.setBounds(new Rectangle(25, 5, 420, 22));
		labelHost.setText("主机名:");
		labelHost.setBounds(new Rectangle(25, 35, 50, 22));
		textFieldHost.setBounds(new Rectangle(78, 35, 280, 22));

		labelUser.setText("用户名:");
		labelUser.setBounds(new Rectangle(25, 70, 50, 22));
		textFieldUser.setBounds(new Rectangle(78, 70, 114, 22));
		labelPassword.setText("密码:");
		labelPassword.setBounds(new Rectangle(205, 70, 35, 22));
		textFieldPassword.setBounds(new Rectangle(244, 70, 114, 22));
		textFieldPassword.setEchoChar('*');

		buttonLink.setLabel("连接");
		buttonLink.setBounds(new Rectangle(375, 35, 70, 22));
		buttonLink.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonLink_actionPerformed(e);
			}
		});
		buttonLink.setEnabled(true);

		buttonDisconnect.setLabel("断开");
		buttonDisconnect.setBounds(new Rectangle(375, 70, 70, 22));
		buttonDisconnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonDisconnect_actionPerformed(e);
			}
		});
		buttonDisconnect.setEnabled(false);

		labelFileShow.setText("目录列表");
		labelFileShow.setBounds(new Rectangle(25, 105, 140, 22));

		textAreaContent.setBounds(new Rectangle(25, 135, 420, 235));
		textAreaContent.setEditable(false);

		labelChgDir.setText("打开文件夹:");
		labelChgDir.setBounds(new Rectangle(25, 380, 100, 22));

		cbxFolder.setBounds(new Rectangle(128, 380, 130, 22));
		cbxFolder.setEnabled(false);

		buttonCDChg.setLabel("转到目录");
		buttonCDChg.setBounds(new Rectangle(275, 380, 70, 22));
		buttonCDChg.setEnabled(false);
		buttonCDChg.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonCDChg_actionPerformed(e);
			}
		});
		buttonCDUP.setLabel("返回上级目录");
		buttonCDUP.setBounds(new Rectangle(365, 380, 80, 22));
		buttonCDUP.setEnabled(false);
		buttonCDUP.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonCDUP_actionPerformed(e);
			}
		});

		labelFile.setText("下载文件:");
		labelFile.setBounds(new Rectangle(25, 410, 100, 22));
		cbxFile.setBounds(new Rectangle(128, 410, 230, 22));
		cbxFile.setEnabled(false);

		buttonDownload.setLabel("下载");
		buttonDownload.setBounds(new Rectangle(375, 410, 70, 22));
		buttonDownload.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonDownload_actionPerformed(e);
			}
		});
		buttonDownload.setEnabled(false);

		contentPane.add(labelPrompt, null);
		contentPane.add(labelHost, null);
		contentPane.add(textFieldHost, null);
		contentPane.add(labelUser, null);
		contentPane.add(textFieldUser, null);
		contentPane.add(labelPassword, null);
		contentPane.add(textFieldPassword, null);
		contentPane.add(buttonLink, null);
		contentPane.add(buttonDisconnect, null);

		contentPane.add(labelFileShow, null);
		contentPane.add(textAreaContent, null);
		contentPane.add(labelFile, null);
		contentPane.add(buttonDownload, null);

		contentPane.add(labelChgDir, null);
		contentPane.add(buttonCDChg, null);
		contentPane.add(buttonCDUP, null);
		contentPane.add(cbxFile, null);
		contentPane.add(cbxFolder, null);
		contentPane.add(labelcopyright, null);

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		this.setSize(new Dimension(480, 515));
		this.setResizable(false);
		this.setTitle("ftp客户端");
		this.setVisible(true);
		setLocationRelativeTo(null);// 窗体居中

		Properties props = System.getProperties();
		String version = props.getProperty("java.version");
		int index = version.indexOf(".");
		String temp = version.substring(index + 1, index + 2);
		if (Integer.parseInt(temp) > 6)
			JOptionPane
					.showMessageDialog(
							this,
							"检测到您使用的jre版本是"
									+ version
									+ "，该版本与本程序不兼容。因为jre从1.7开始取消了对sun.net.*旧版本的兼容性。\n考虑到1.6的广泛使用，我并没有使用1.7+版本进行开发。为了正常使用该程序，建议您将jre换为1.6或更低。");

	}

	// 响应“连接”按钮的点击消息，连接到服务器端
	void buttonLink_actionPerformed(ActionEvent e) {
		String hostname = textFieldHost.getText();
		labelPrompt.setText("正在连接，请等待.....");
		try {

			String user = textFieldUser.getText();
			String psw = textFieldPassword.getText();
			if ("".equals(user)) {
				user = "anonymous";
				psw = "test";
				textFieldUser.setText(user);
				textFieldPassword.setText(psw);
			}
			myFtp = new FtpClient(hostname, 21); // 构造一个对象
			myFtp.login(user, psw); // 以给定用户名和密码登录
			// myFtp.binary(); // 表示文件以二进制模式传输
			showFileContents(); // 列出服务器端当前目录下的目录和文件名

			labelPrompt.setText("连接主机:" + textFieldHost.getText() + "成功!");

			buttonDisconnect.setEnabled(true);
			buttonDownload.setEnabled(true);
			buttonCDChg.setEnabled(true);
			buttonCDUP.setEnabled(true);
			buttonLink.setEnabled(false);
			cbxFile.setEnabled(true);
			cbxFolder.setEnabled(true);
		} catch (FtpLoginException e1) {
			e1.printStackTrace();
			String strPrompt = "用户名密码错误";
			labelPrompt.setText(strPrompt);
		} catch (IOException e1) {
			e1.printStackTrace();
			String strPrompt = "连接主机:" + hostname + "失败!";
			labelPrompt.setText(strPrompt);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			String strPrompt = "无权限与主机:" + hostname + "连接!";
			labelPrompt.setText(strPrompt);
		}
	}

	// 响应“断开”按钮的点击消息，连接到服务器端
	void buttonDisconnect_actionPerformed(ActionEvent e) {
		try {
			myFtp.closeServer(); // 关闭与服务器端的连接
			textAreaContent.setText("");
			labelPrompt.setText("与主机断开连接");
		} catch (IOException e1) {
			System.out.println("Error: " + e1);
			e1.printStackTrace();
		}
		buttonLink.setEnabled(true);
		buttonDownload.setEnabled(false);
		buttonDisconnect.setEnabled(false);
		buttonCDChg.setEnabled(false);
		buttonCDUP.setEnabled(false);
	}

	// 转换目录
	void buttonCDChg_actionPerformed(ActionEvent e) {
		try {
			myFtp.cd(cbxFolder.getSelectedItem().toString().trim());
			cbxFile.removeAllItems();
			cbxFolder.removeAllItems();
			showFileContents(); // 列出服务器端当前目录下的目录和文件名
		} catch (IOException e1) {
			System.out.println("Error: " + e1);
			e1.printStackTrace();
		}
	}

	// 返回上级目录
	void buttonCDUP_actionPerformed(ActionEvent e) {
		try {
			myFtp.cdUp();
			cbxFile.removeAllItems();
			cbxFolder.removeAllItems();
			showFileContents(); // 列出服务器端当前目录下的目录和文件名
		} catch (IOException e1) {
			System.out.println("Error: " + e1);
			e1.printStackTrace();
		}
	}

	// 列出服务器端当前目录下的目录和文件名
	public void showFileContents() {
		int ch;
		StringBuffer buf = new StringBuffer();
		try {
			inStream = myFtp.list(); // 得到主机端当前目录下所有文件和目录的输入数据流
			while ((ch = inStream.read()) >= 0) { // 从输入流中读取数据
				buf.append((char) ch); // 保存数据到缓冲区
			}
			textAreaContent.setText("");
			textAreaContent.append(buf.toString()); // 将目录和文件名显示在文本框中
			String[] file = buf.toString().split("\n");
			for (String k : file) {
				if (k.startsWith("d")) {
					cbxFolder.addItem(k.substring(k.lastIndexOf(" ")));
				} else
					cbxFile.addItem(k.substring(k.lastIndexOf(" ")));
			}
			inStream.close(); // 关闭输入流
		} catch (Exception e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}

	// 响应“下载”按钮的点击消息
	void buttonDownload_actionPerformed(ActionEvent e) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG); // 保存
		fileChooser.setApproveButtonText("保存");
		int intRetVal = fileChooser.showOpenDialog(this);
		String path = fileChooser.getSelectedFile().getPath();
		if (intRetVal == JFileChooser.APPROVE_OPTION) {
			int ch;
			StringBuffer buf = new StringBuffer();
			buf.setLength(0);
			try {
				File dir = new File(path); // 构造目录
				File f = new File(dir, cbxFile.getSelectedItem().toString()); // 通过文件
				RandomAccessFile file = new RandomAccessFile(f, "rw"); // 构造一个随机访问文件
				inStream = myFtp.get(cbxFile.getSelectedItem().toString()
						.trim()); // 得到文件的输入流
				while ((ch = inStream.read()) >= 0) { // 读取数据流
					buf.append((char) ch); // 将读取的数据存在缓冲区中
				}
				file.writeBytes(buf.toString()); // 将缓冲区中的数据以字符串形式写入文件
				file.close(); // 关闭文件
				JOptionPane msg = new JOptionPane(); // 提示对话框
				JOptionPane.showMessageDialog(FtpClientFrame.this, "下载成功",
						"下载成功！", 1);
			} catch (Exception e1) {
				System.out.println("Error: " + e1);
				e1.printStackTrace();
			}
		} else {

		}

	}

}