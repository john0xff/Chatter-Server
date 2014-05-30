package com.phoenixjcam.application.server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatterServer extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final int width = 640;
	private final int height = 480;

	// make sure that port is available
	private int port;

	// sockets
	private ServerSocket serverSocket;
	private Socket connectionSocket;

	// streams
	private ObjectOutputStream output;
	private ObjectInputStream input;

	// components
	private JScrollPane scrollPane;
	private JTextArea chatArea;
	private JTextField userText;

	private String message;
	private final static String NEWLINE = "\n";

	public ChatterServer(int port)
	{
		super("Chatter Server");

		this.port = port;

		scrollPane = new JScrollPane(getChatArea());
		add(scrollPane, BorderLayout.CENTER);

		add(getUserText(), BorderLayout.NORTH);

		frameSettings();
	}

	public void frameSettings()
	{
		setSize(width, height);
		Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setLocation(100, (centerPoint.y) - (height / 2));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private JTextArea getChatArea()
	{
		chatArea = new JTextArea();
		chatArea.setFont(new Font("Arial", 0, 20));
		return chatArea;
	}

	/** send server text to client and append server text to server chat area */
	private JTextField getUserText()
	{
		userText = new JTextField();
		userText.setFont(new Font("Arial", 0, 20));

		userText.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent event)
			{
				String serverMessage = event.getActionCommand();

				try
				{
					// send server text to client
					output.writeObject(NEWLINE + "SERVER -  " + serverMessage);
					output.flush();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				// append server text to area
				chatArea.append(NEWLINE + "SERVER - " + serverMessage);

				userText.setText("");
			}
		});

		return userText;
	}

	public void runServer()
	{
		try
		{
			serverSocket = new ServerSocket(port);
			
			// waiting until client is connected
			clinetConnection();

			// when client is connected set streams
			output = new ObjectOutputStream(connectionSocket.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connectionSocket.getInputStream());

			do
			{
				// receive message form client side 
				message = (String) input.readObject();
				// append to server chat area
				chatArea.append(message);
			}
			// EXIT is command to end chat
			while (!message.equals("CLIENT - EXIT"));

			closeSocket();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private void clinetConnection()
	{
		chatArea.append("waiting for connection" + NEWLINE);

		try
		{
			// waiting until client is connected
			connectionSocket = serverSocket.accept();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		String hostName = connectionSocket.getInetAddress().getHostName();
		// InetAddress ip = connectionSocket.getInetAddress();
		chatArea.append("connected to " + hostName + NEWLINE);
	}

	private void closeSocket()
	{
		try
		{
			connectionSocket.close();
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
	}

	public static void main(String[] args)
	{

		int port = 9002;

		
		// create and show gui
		ChatterServer server = new ChatterServer(port);
		
		
		server.runServer();
	}
}
