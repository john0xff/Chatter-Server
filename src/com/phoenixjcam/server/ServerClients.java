package com.phoenixjcam.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ServerClients extends Thread
{
	private ServerGUI serverGUI;

	private String clientName;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	private Socket clientSocket;

	// each client = new Thread
	private final ServerClients[] serverClients;
	private int maxClientsCount;

	private boolean isOpen;

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
		String name = null;

		try
		{
			// setup streams for new client
			objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

			try
			{
				// wait for name
				while (true)
				{

					objectOutputStream.writeObject("Enter name");
					name = objectInputStream.readObject().toString();
					break;
				}

				// welcome new user
				objectOutputStream.writeObject("Welcome " + name + " in our chat room");

				Utils.printServerMsg("new user - " + name, serverGUI);

				// serverGUI.getTextArea().append("new user - " + name + Utils.NEWLINE); // update server frame

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
			}
			catch (SocketException e)
			{
				System.err.println("client in fase wait for name canceled connection");
			}

			// read msg from this client and broadcast to other clients
			while (true)
			{
				try
				{
					String clientMsg = objectInputStream.readObject().toString();

					// break this loop and end of life of this thread
					if (clientMsg.contains("END"))
						break;

					synchronized (this)
					{
						for (int i = 0; i < maxClientsCount; i++)
						{
							if (this.serverClients[i] != null && this.serverClients[i].clientName != null)
							{
								this.serverClients[i].objectOutputStream.writeObject("<" + name + "> " + clientMsg);
								Utils.printServerMsg(name + ": " + clientMsg, serverGUI);
								
								//serverGUI.getTextArea().append(name + ": " + clientMsg + Utils.NEWLINE); // update
																											// server
																											// frame
							}
						}
						Utils.printServerMsg("--------------------------------------------" , serverGUI);
						
						//serverGUI.getTextArea().append("--------------------------------------------" + Utils.NEWLINE);
					}
				}
				// in case if user close window without saying BYE || END
				catch (SocketException e)
				{
					isOpen = false;
					break;
				}

			}

			// if msg == end then close streams and socket on this thread
			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (this.serverClients[i] != null && this.serverClients[i] != this && this.serverClients[i].clientName != null)
					{
						String preparedMsg = "The user " + name + " is leaving the chat room";
						this.serverClients[i].objectOutputStream.writeObject(preparedMsg);
					}
				}
			}

			// if(this.objectOutputStream != null)
			// objectOutputStream.writeObject("BYE");

			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (this.serverClients[i] == this)
					{
						this.serverClients[i] = null;
					}
				}
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			shutdownStreams();
			closeSocket();
		}

	}
	
	private void broadcastMessage()
	{
		
	}

	private void shutdownStreams()
	{
		try
		{
			clientSocket.shutdownOutput();
			clientSocket.shutdownInput();
			System.out.println("shutdown Streams"); // for debug mode
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

	private void closeSocket()
	{
		try
		{
			clientSocket.close();

			System.out.println("close Socket"); // for debug mode
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

	public void updateServerGUI(ServerGUI serverGUI)
	{
		this.serverGUI = serverGUI;
	}
}