package com.phoenixjcam.application.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerClients extends Thread
{
	private String clientName;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	private Socket clientSocket;
	
	// each client = new Thread
	private final ServerClients[] serverClients;
	private int maxClientsCount;
	
	public ServerClients(Socket clientSocket, ServerClients[] clients)
	{
		this.clientSocket = clientSocket;
		this.serverClients = clients;
		this.maxClientsCount = clients.length;
	}
	
	@Override
	public void run()
	{
		int maxClientsCount = this.maxClientsCount;
		ServerClients[] clients = this.serverClients;
		String name;
		
		try
		{
			// setup streams for new client
			objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			
			// wait for name
			while(true)
			{
				objectOutputStream.writeObject("Enter name");
				name = objectInputStream.readObject().toString();
				break;
			}
			
			// welcome new user
			objectOutputStream.writeObject("Welcome " + name + " in our chat room");
			
			
			synchronized (this)
			{
				// update client name
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (this.serverClients[i] != null && this.serverClients[i] == this)
					{
						clientName = "@" + name;
						break;
					}
				}
				
				// send to each of client update about new user
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (this.serverClients[i] != null && this.serverClients[i] != this)
					{
						this.serverClients[i].objectOutputStream.writeObject("A new user " + name + " entered the chat room");
					}
				}
			}
			
			// read msg from this client and broadcast to other clients 
			while (true)
			{
				String clientMsg = objectInputStream.readObject().toString();
				
				// break this loop and end of life of this thread
				if(clientMsg.contains("end"))
					break;
				
				synchronized (this)
				{
					for (int i = 0; i < maxClientsCount; i++)
					{
						if (this.serverClients[i] != null && this.serverClients[i].clientName != null)
						{
							this.serverClients[i].objectOutputStream.writeObject("<" + name + "> " + clientMsg);
						}
					}
				}
				
			}
			
			// if msg == end then close streams and socket on this thread
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

//	private void shutdownStreams()
//	{
//		try
//		{
//			clientSocket.shutdownOutput();
//			clientSocket.shutdownInput();
//			System.out.println("shutdownStreams"); // for debug mode
//		}
//		catch (IOException e1)
//		{
//			e1.printStackTrace();
//		}
//	}
//
//	private void closeSockets()
//	{
//		try
//		{
//			clientSocket.close();
//
//			System.out.println("closeSockets"); // for debug mode
//		}
//		catch (IOException e1)
//		{
//			e1.printStackTrace();
//		}
//	}
}
