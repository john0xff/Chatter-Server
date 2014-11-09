package com.phoenixjcam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class SocketListener
{
	private ServerGUI serverGUI;

	private static Socket clientSocket;
	private static ServerClients[] serverClients;

	private static ServerSocket serverSocket;
	private static final int maxClients = 5;

	private String serverMsg;

	public SocketListener()
	{
		serverGUI = new ServerGUI();
		
		
//		String givenPort = JOptionPane.showInputDialog(serverGUI.getFrame(), "Type port (9002) or similar", 9002);
//
//		int port = Integer.valueOf(givenPort);
		
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

		Utils.printServerMsg(Utils.getCurrentTime() + " before accepting" + " server port = " + port, serverGUI);

		while (true)
		{
			try
			{
				// block until new client connection
				clientSocket = serverSocket.accept();
				
				Utils.printServerMsg(Utils.getCurrentTime() + " accepted", serverGUI);

				for (int i = 0; i < maxClients; i++)
				{
					if (serverClients[i] == null)
					{
						(serverClients[i] = new ServerClients(clientSocket, serverClients)).start();
						serverClients[i].updateServerGUI(serverGUI);

						Utils.printServerMsg(Utils.getCurrentTime() + " new client nr - " + i, serverGUI);

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
