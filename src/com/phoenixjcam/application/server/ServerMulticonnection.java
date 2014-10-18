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
	private int port;
	private ServerSocket serverSocket;
	private Socket clientSocket;

	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	private JFrame frame;
	private JTextField userText;
	private JTextArea textArea;

	public ServerMulticonnection(int port)
	{
		this.port = port;

		frame = new JFrame("Server");

		userText = new JTextField();
		userText.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				String msg = e.getActionCommand();

				try
				{
					objectOutputStream.writeObject(msg);
					textArea.append("Server: " + msg + "\n");
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		userText.setEnabled(false);
		frame.add(userText, BorderLayout.NORTH);

		textArea = new JTextArea();
		textArea.setEnabled(false);
		frame.add(textArea, BorderLayout.CENTER);

		frame.setSize(600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

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
				userText.setEnabled(true);

				String msg = null;
				do
				{
					try
					{
						msg = objectInputStream.readObject().toString();
						textArea.append("Client: " + msg + "\n");
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();
					}
				}
				while (!msg.equals("END"));
			}
			catch (IOException e)
			{
				textArea.append("Client dissconected.");
				System.out.println("Client dissconected.");
				e.printStackTrace();
			}
			finally
			{
				try
				{
					clientSocket.shutdownOutput();
					clientSocket.shutdownInput();
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try
				{
					clientSocket.close();
					serverSocket.close();
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args)
	{
		new ServerMulticonnection(9000);
	}
}