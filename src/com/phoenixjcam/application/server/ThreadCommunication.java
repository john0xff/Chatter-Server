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

import com.sun.org.apache.regexp.internal.recompile;

public class ThreadCommunication implements Runnable
{

	private final static String NEWLINE = "\n";
	private final static String CLEAR = "";

	private ServerGUI serverGUI;
	private Socket clientSocket;
	private ThreadCommunication[] threadsArray;

	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	private Thread thread;
	private Runnable runnable;

	public ThreadCommunication(ServerGUI serverGUI, Socket clientSocket, ThreadCommunication[] threadsArray)
	{
		this.runnable = this;
		this.thread = new Thread(runnable);
		this.serverGUI = serverGUI; // same server gui for all clients - need to take care of concurrency
		this.clientSocket = clientSocket; // different socket for each new client
		this.threadsArray = threadsArray;

		// serverGUI.getUserText().addActionListener(new ActionListener()
		// {
		//
		// @Override
		// public void actionPerformed(ActionEvent e)
		// {
		//
		// String msg = e.getActionCommand();
		//
		// try
		// {
		// String serverMsg = serverGUI.currentTime() + " Server: " + msg + NEWLINE;// prepare full msg
		// // from
		// // server to display
		// // in
		// // both
		// objectOutputStream.writeObject(serverMsg);
		// serverGUI.getTextArea().append(serverMsg);
		// serverGUI.getUserText().setText(CLEAR);
		// }
		// catch (IOException e1)
		// {
		// e1.printStackTrace();
		// }
		//
		// }
		//
		// });

	}

	public Thread getThread()
	{
		return this.thread;
	}

	@Override
	public void run()
	{

		while (true)
		{
			synchronized (this)
			{

				for (int i = 0; i < threadsArray.length; i++)
				{
					try
					{
						objectInputStream = new ObjectInputStream(threadsArray[i].clientSocket.getInputStream());
						objectOutputStream = new ObjectOutputStream(threadsArray[i].clientSocket.getOutputStream());

						try
						{
							String msg = objectInputStream.readObject().toString();
							objectOutputStream.writeObject(Thread.currentThread().getId() + msg);
						}
						catch (ClassNotFoundException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// serverGUI.getUserText().setEnabled(true);

						// String msg = null;
						// do
						// {
						// try
						// {
						// msg = objectInputStream.readObject().toString();
						// serverGUI.getTextArea().append(msg);
						// }
						// catch (ClassNotFoundException e1)
						// {
						// e1.printStackTrace();
						// }
						// }
						// while (!msg.equals("END"));
						//
						// serverGUI.getTextArea().append("Client dissconected by typing END." + NEWLINE);
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

			System.out.println("closeSockets"); // for debug mode
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

}