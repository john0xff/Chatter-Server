package com.phoenixjcam.application.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainListener
{
	private ServerGUI serverGUI;

	private static Socket clientSocket;
	private static ServerClients[] serverClients;

	private static ServerSocket serverSocket;
	private static final int maxClients = 5;
	
	private String serverMsg;

	public MainListener()
	{
		serverGUI = new ServerGUI();
		int port = 9002;

		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		serverClients = new ServerClients[maxClients];

		serverMsg = Utils.getCurrentTime() + " before accepting";
		System.out.println(serverMsg);
		serverGUI.getTextArea().append(serverMsg + Utils.NEWLINE);

		
		
		while (true)
		{
			try
			{
				clientSocket = serverSocket.accept();

				serverMsg = Utils.getCurrentTime() + " accepted";
				System.out.println(serverMsg);
				serverGUI.getTextArea().append(serverMsg + Utils.NEWLINE);

				for (int i = 0; i < maxClients; i++)
				{
					if (serverClients[i] == null)
					{
						(serverClients[i] = new ServerClients(clientSocket, serverClients)).start();
						serverClients[i].updateServerGUI(serverGUI);
						
						 serverMsg = Utils.getCurrentTime() + " new client nr - " + i;
						System.out.println(serverMsg);
						serverGUI.getTextArea().append(serverMsg +  Utils.NEWLINE);
						
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

	public static void main(String[] args)
	{
		new MainListener();
	}
}