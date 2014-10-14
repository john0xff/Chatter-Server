package com.phoenixjcam.chat.server;

import java.awt.Font;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Communication
{
	// streams
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	// components
	private JScrollPane scrollPane;
	private JTextArea chatArea;

	// private JTextField userText;
	private String message;
	public final static String NEWLINE = "\n";
	public final static String EXITCMD = "\nCLIENT - EXIT";
	// time
	private Calendar calendar;
	private SimpleDateFormat dateFormat;
	private String currentTime;

	public Communication()
	{
		dateFormat = new SimpleDateFormat("HH:mm:ss");

		chatArea = new JTextArea();
		chatArea.setFont(new Font("Arial", 0, 20));
		scrollPane = new JScrollPane(chatArea);
	}

	public JScrollPane getJScrollPane()
	{
		if (scrollPane == null)
			throw new NullPointerException("JScrollPane not initialized");

		return scrollPane;
	}

	public void setupOutputStream(ObjectOutputStream objectOutputStream)
	{
		this.objectOutputStream = objectOutputStream;
	}

	public void setupInputStream(ObjectInputStream objectInputStream)
	{
		this.objectInputStream = objectInputStream;
	}

	public JTextArea getChatArea()
	{
		return chatArea;
	}

	public void communicate() throws ClassNotFoundException, IOException
	{
		do
		{
			// receive message form client side
			message = (String) objectInputStream.readObject();
			// append to server chat area
			chatArea.append(message);
		}
		while (!message.equals(EXITCMD));
	}

	public void closeStreams()
	{
		ServerUtils.closeObject(objectInputStream);
		ServerUtils.closeObject(objectOutputStream);

	}

}
