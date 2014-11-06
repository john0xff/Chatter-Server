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

public class MainListener
{

	private final static String NEWLINE = "\n";
	private final static String CLEAR = "";

	private ServerGUI serverGUI;

	private static Socket clientSocket;
	private static ServerClients[] serverClients;

	private static ServerSocket serverSocket;
	private static final int maxClients = 5;

	public static void main(String[] args)
	{
		int port = 9002;

		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true)
		{
			try
			{
				clientSocket = serverSocket.accept();

				for (int i = 0; i < maxClients; i++)
				{
					if (serverClients[i] == null)
					{
						(serverClients[i] = new ServerClients(clientSocket, serverClients)).start();
						break;
					}
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	

}