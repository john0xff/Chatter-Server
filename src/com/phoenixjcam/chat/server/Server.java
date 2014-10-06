package com.phoenixjcam.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;

public class Server
{
	private ServerSocket serverSocket;
	private Socket connectionSocket;
	private int port;

	public Server(int port)
	{
		this.port = port;
		connectionListener();
	}

	private void connectionListener()
	{
		try
		{
			serverSocket = new ServerSocket(port);
			connectionSocket = serverSocket.accept();
			System.out.println("server: client connected");

			communication();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void communication()
	{

		for (int i = 0; i < 100; i++)
		{
			try
			{
				if (connectionSocket.isClosed())
				{
					System.out.println("server loop nr: " + i + " client disconnected");
				}
				else
				{
					System.out.println("server loop nr: " + i + " client connected");
				}
				
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args)
	{
		int port = 9005;
		Server server = new Server(port);

	}
}
