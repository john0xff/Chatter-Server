package com.phoenixjcam.application.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMulticonnection
{
	private ServerGUI serverGUI;
	private final static String NEWLINE = "\n";
	private final static String CLEAR = "";
	
	private int port;
	private ServerSocket serverSocket;
	private Socket clientSocket;

	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	public ServerMulticonnection(int port)
	{
		this.port = port;

		serverGUI = new ServerGUI();
		
		serverGUI.getUserText().addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String msg = e.getActionCommand();

				try
				{
					String serverMsg = serverGUI.currentTime() + " Server: " + msg + NEWLINE;// prepare full msg from server to display in both  
					objectOutputStream.writeObject(serverMsg);
					serverGUI.getTextArea().append(serverMsg);
					serverGUI.getUserText().setText(CLEAR);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		
		while (true)
		{
			try
			{
				serverSocket = new ServerSocket(port);
				clientSocket = serverSocket.accept();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			try
			{
				objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
				serverGUI.getUserText().setEnabled(true);

				String msg = null;
				do
				{
					try
					{
						msg = objectInputStream.readObject().toString();
						serverGUI.getTextArea().append(msg);
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();
					}
				}
				while (!msg.equals("END"));
				
				serverGUI.getTextArea().append("Client dissconected by typing END." + NEWLINE);
			}
			catch (IOException e)
			{
				serverGUI.getTextArea().append("Client dissconected by pressing EXIT." + NEWLINE);
				System.out.println("Client dissconected."); // for debug mode
				e.printStackTrace();
			}
			finally
			{
				shutdownStreams();
				closeSockets();
			}
		}
	}

	private void shutdownStreams()
	{
		try
		{
			clientSocket.shutdownOutput();
			clientSocket.shutdownInput();
			System.out.println("shutdownStreams"); // for debug mode
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

	private void closeSockets()
	{
		try
		{
			clientSocket.close();
			serverSocket.close();
			System.out.println("closeSockets"); // for debug mode
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		new ServerMulticonnection(9000);
	}
}