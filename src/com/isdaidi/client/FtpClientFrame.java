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
	Label labelPrompt = new Label(); // ״̬��ʾ
	Label labelcopyright = new Label(); // ״̬��ʾ
	Label labelHost = new Label();
	TextField textFieldHost = new TextField("localhost"); // ������ַ
	Label labelUser = new Label();
	TextField textFieldUser = new TextField(); // �û���
	Label labelPassword = new Label();
	TextField textFieldPassword = new TextField(); // ����
	Button buttonLink = new Button(); // ���Ӱ�ť
	Button buttonDisconnect = new Button(); // �Ͽ���ť
	Label labelFileShow = new Label();
	TextArea textAreaContent = new TextArea(); // ��ʾ�ļ���Ŀ¼���ı���
	Label labelFile = new Label();
	JFileChooser fileChooser = new JFileChooser();
	Button buttonDownload = new Button(); // ���ذ�ť
	Label labelChgDir = new Label();
	Button buttonCDChg = new Button(); // �ı�Ŀ¼��ť
	Button buttonCDUP = new Button(); // �����ϼ�Ŀ¼��ť

	JComboBox cbxFile = new JComboBox();
	JComboBox cbxFolder = new JComboBox();

	FtpClient myFtp = null; // FtpClient����
	TelnetInputStream inStream = null; // ������?

	public FtpClientFrame() {
		try {
			jbInit(); // �����ʼ��
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// �����ʼ������ʾ
	private void jbInit() throws Exception {
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(null);
		labelcopyright.setText("����ʵ����ҵ  ���������E   ѧ�ţ�13S003116   ��������ҵ��ѧ�����ѧԺ");

		labelcopyright.setBounds(new Rectangle(25, 450, 420, 22));
		labelPrompt.setText("�����û����������¼ftp���������룬��Ĭ��ʹ��������¼��");
		labelPrompt.setBounds(new Rectangle(25, 5, 420, 22));
		labelHost.setText("������:");
		labelHost.setBounds(new Rectangle(25, 35, 50, 22));
		textFieldHost.setBounds(new Rectangle(78, 35, 280, 22));

		labelUser.setText("�û���:");
		labelUser.setBounds(new Rectangle(25, 70, 50, 22));
		textFieldUser.setBounds(new Rectangle(78, 70, 114, 22));
		labelPassword.setText("����:");
		labelPassword.setBounds(new Rectangle(205, 70, 35, 22));
		textFieldPassword.setBounds(new Rectangle(244, 70, 114, 22));
		textFieldPassword.setEchoChar('*');

		buttonLink.setLabel("����");
		buttonLink.setBounds(new Rectangle(375, 35, 70, 22));
		buttonLink.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonLink_actionPerformed(e);
			}
		});
		buttonLink.setEnabled(true);

		buttonDisconnect.setLabel("�Ͽ�");
		buttonDisconnect.setBounds(new Rectangle(375, 70, 70, 22));
		buttonDisconnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonDisconnect_actionPerformed(e);
			}
		});
		buttonDisconnect.setEnabled(false);

		labelFileShow.setText("Ŀ¼�б�");
		labelFileShow.setBounds(new Rectangle(25, 105, 140, 22));

		textAreaContent.setBounds(new Rectangle(25, 135, 420, 235));
		textAreaContent.setEditable(false);

		labelChgDir.setText("���ļ���:");
		labelChgDir.setBounds(new Rectangle(25, 380, 100, 22));

		cbxFolder.setBounds(new Rectangle(128, 380, 130, 22));
		cbxFolder.setEnabled(false);

		buttonCDChg.setLabel("ת��Ŀ¼");
		buttonCDChg.setBounds(new Rectangle(275, 380, 70, 22));
		buttonCDChg.setEnabled(false);
		buttonCDChg.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonCDChg_actionPerformed(e);
			}
		});
		buttonCDUP.setLabel("�����ϼ�Ŀ¼");
		buttonCDUP.setBounds(new Rectangle(365, 380, 80, 22));
		buttonCDUP.setEnabled(false);
		buttonCDUP.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonCDUP_actionPerformed(e);
			}
		});

		labelFile.setText("�����ļ�:");
		labelFile.setBounds(new Rectangle(25, 410, 100, 22));
		cbxFile.setBounds(new Rectangle(128, 410, 230, 22));
		cbxFile.setEnabled(false);

		buttonDownload.setLabel("����");
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
		this.setTitle("ftp�ͻ���");
		this.setVisible(true);
		setLocationRelativeTo(null);// �������

		Properties props = System.getProperties();
		String version = props.getProperty("java.version");
		int index = version.indexOf(".");
		String temp = version.substring(index + 1, index + 2);
		if (Integer.parseInt(temp) > 6)
			JOptionPane
					.showMessageDialog(
							this,
							"��⵽��ʹ�õ�jre�汾��"
									+ version
									+ "���ð汾�뱾���򲻼��ݡ���Ϊjre��1.7��ʼȡ���˶�sun.net.*�ɰ汾�ļ����ԡ�\n���ǵ�1.6�Ĺ㷺ʹ�ã��Ҳ�û��ʹ��1.7+�汾���п�����Ϊ������ʹ�øó��򣬽�������jre��Ϊ1.6����͡�");

	}

	// ��Ӧ�����ӡ���ť�ĵ����Ϣ�����ӵ���������
	void buttonLink_actionPerformed(ActionEvent e) {
		String hostname = textFieldHost.getText();
		labelPrompt.setText("�������ӣ���ȴ�.....");
		try {

			String user = textFieldUser.getText();
			String psw = textFieldPassword.getText();
			if ("".equals(user)) {
				user = "anonymous";
				psw = "test";
				textFieldUser.setText(user);
				textFieldPassword.setText(psw);
			}
			myFtp = new FtpClient(hostname, 21); // ����һ������
			myFtp.login(user, psw); // �Ը����û����������¼
			// myFtp.binary(); // ��ʾ�ļ��Զ�����ģʽ����
			showFileContents(); // �г��������˵�ǰĿ¼�µ�Ŀ¼���ļ���

			labelPrompt.setText("��������:" + textFieldHost.getText() + "�ɹ�!");

			buttonDisconnect.setEnabled(true);
			buttonDownload.setEnabled(true);
			buttonCDChg.setEnabled(true);
			buttonCDUP.setEnabled(true);
			buttonLink.setEnabled(false);
			cbxFile.setEnabled(true);
			cbxFolder.setEnabled(true);
		} catch (FtpLoginException e1) {
			e1.printStackTrace();
			String strPrompt = "�û����������";
			labelPrompt.setText(strPrompt);
		} catch (IOException e1) {
			e1.printStackTrace();
			String strPrompt = "��������:" + hostname + "ʧ��!";
			labelPrompt.setText(strPrompt);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			String strPrompt = "��Ȩ��������:" + hostname + "����!";
			labelPrompt.setText(strPrompt);
		}
	}

	// ��Ӧ���Ͽ�����ť�ĵ����Ϣ�����ӵ���������
	void buttonDisconnect_actionPerformed(ActionEvent e) {
		try {
			myFtp.closeServer(); // �ر���������˵�����
			textAreaContent.setText("");
			labelPrompt.setText("�������Ͽ�����");
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

	// ת��Ŀ¼
	void buttonCDChg_actionPerformed(ActionEvent e) {
		try {
			myFtp.cd(cbxFolder.getSelectedItem().toString().trim());
			cbxFile.removeAllItems();
			cbxFolder.removeAllItems();
			showFileContents(); // �г��������˵�ǰĿ¼�µ�Ŀ¼���ļ���
		} catch (IOException e1) {
			System.out.println("Error: " + e1);
			e1.printStackTrace();
		}
	}

	// �����ϼ�Ŀ¼
	void buttonCDUP_actionPerformed(ActionEvent e) {
		try {
			myFtp.cdUp();
			cbxFile.removeAllItems();
			cbxFolder.removeAllItems();
			showFileContents(); // �г��������˵�ǰĿ¼�µ�Ŀ¼���ļ���
		} catch (IOException e1) {
			System.out.println("Error: " + e1);
			e1.printStackTrace();
		}
	}

	// �г��������˵�ǰĿ¼�µ�Ŀ¼���ļ���
	public void showFileContents() {
		int ch;
		StringBuffer buf = new StringBuffer();
		try {
			inStream = myFtp.list(); // �õ������˵�ǰĿ¼�������ļ���Ŀ¼������������
			while ((ch = inStream.read()) >= 0) { // ���������ж�ȡ����
				buf.append((char) ch); // �������ݵ�������
			}
			textAreaContent.setText("");
			textAreaContent.append(buf.toString()); // ��Ŀ¼���ļ�����ʾ���ı�����
			String[] file = buf.toString().split("\n");
			for (String k : file) {
				if (k.startsWith("d")) {
					cbxFolder.addItem(k.substring(k.lastIndexOf(" ")));
				} else
					cbxFile.addItem(k.substring(k.lastIndexOf(" ")));
			}
			inStream.close(); // �ر�������
		} catch (Exception e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}

	// ��Ӧ�����ء���ť�ĵ����Ϣ
	void buttonDownload_actionPerformed(ActionEvent e) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG); // ����
		fileChooser.setApproveButtonText("����");
		int intRetVal = fileChooser.showOpenDialog(this);
		String path = fileChooser.getSelectedFile().getPath();
		if (intRetVal == JFileChooser.APPROVE_OPTION) {
			int ch;
			StringBuffer buf = new StringBuffer();
			buf.setLength(0);
			try {
				File dir = new File(path); // ����Ŀ¼
				File f = new File(dir, cbxFile.getSelectedItem().toString()); // ͨ���ļ�
				RandomAccessFile file = new RandomAccessFile(f, "rw"); // ����һ����������ļ�
				inStream = myFtp.get(cbxFile.getSelectedItem().toString()
						.trim()); // �õ��ļ���������
				while ((ch = inStream.read()) >= 0) { // ��ȡ������
					buf.append((char) ch); // ����ȡ�����ݴ��ڻ�������
				}
				file.writeBytes(buf.toString()); // ���������е��������ַ�����ʽд���ļ�
				file.close(); // �ر��ļ�
				JOptionPane msg = new JOptionPane(); // ��ʾ�Ի���
				JOptionPane.showMessageDialog(FtpClientFrame.this, "���سɹ�",
						"���سɹ���", 1);
			} catch (Exception e1) {
				System.out.println("Error: " + e1);
				e1.printStackTrace();
			}
		} else {

		}

	}

}