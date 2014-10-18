package com.phoenixjcam.application.server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerGUI
{
	// gui components
	private JFrame frame;
	private JTextField userText;
	private JScrollPane scrollPane;
	private JTextArea textArea;

	// time
	private Calendar calendar;
	private SimpleDateFormat dateFormat;
	private String currentTime;

	public ServerGUI()
	{
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		frame = new JFrame("Server");

		userText = new JTextField();
		userText.setFont(new Font("Arial", 0, 20));
		userText.setEnabled(false);
		frame.add(userText, BorderLayout.NORTH);

		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", 0, 20));
		//textArea.setEnabled(false);
		scrollPane = new JScrollPane(textArea);
		frame.add(scrollPane, BorderLayout.CENTER);

		frame.setSize(600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public String currentTime()
	{
		calendar = Calendar.getInstance();
		currentTime = dateFormat.format(calendar.getTime());

		return currentTime;
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public void setFrame(JFrame frame)
	{
		this.frame = frame;
	}

	public JTextField getUserText()
	{
		return userText;
	}

	public void setUserText(JTextField userText)
	{
		this.userText = userText;
	}

	public JTextArea getTextArea()
	{
		return textArea;
	}

	public void setTextArea(JTextArea textArea)
	{
		this.textArea = textArea;
	}

}
