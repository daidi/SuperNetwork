package com.isdaidi.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.isdaidi.ftpserver.FtpServer;
import com.isdaidi.webserver.WebServer;

public class ClientMain extends JFrame implements ActionListener {
	private JLabel lblUrl;
	private JTextField txtUrl;
	private JButton btnStartFtp;
	private JButton btnStartWeb;
	private JButton btnExit;

	public ClientMain() {
		super("超级客户端--并发测试");// 命名
		Container container = getContentPane();
		lblUrl = new JLabel("输入网址：");
		txtUrl = new JTextField("http://localhost:8080/",20);
		btnStartFtp = new JButton("打开FTP客户端");
		btnStartWeb = new JButton("打开WEB客户端");
		btnExit = new JButton("退出");
		btnStartFtp.addActionListener(this);
		btnStartWeb.addActionListener(this);
		btnExit.addActionListener(this);

		container.setLayout(null);
		lblUrl.setBounds(25, 25, 70, 40);
		txtUrl.setBounds(95, 25, 250, 40);
		btnStartWeb.setBounds(355, 25, 130, 40);
		btnStartFtp.setBounds(55, 95, 180, 40);
		btnExit.setBounds(270, 95, 180, 40);

		container.add(lblUrl);
		container.add(txtUrl);
		container.add(btnStartFtp);
		container.add(btnStartWeb);
		container.add(btnExit);
		setSize(530, 200);
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
			try {
				Runtime.getRuntime().exec("cmd.exe /c start iexplore "+txtUrl.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == btnStartFtp) {
			new FtpClientFrame();
		} else if (e.getSource() == btnExit) {
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		new ClientMain();
	}
}
