package com.phoenixjcam.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main
{
	private static int i = 0;
	
	public static void main(String[] args)
	{
		int port = 9005;
		// create and show gui
		
		ChatterServer server = new ChatterServer();
		
		try
		{
			ServerSocket serverSocket = new ServerSocket(port);
			
			while (true)
			{
				Socket clientSocket = serverSocket.accept();
				Runnable runnable = server.newClientConnection(clientSocket); 
				i++;
				Thread thread = new Thread(runnable, "client thread nr - " + i);
				thread.start();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
	}
}
