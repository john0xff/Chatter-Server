package com.phoenixjcam.application.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server
{
	private Object locker = new Object();
	
	private boolean started = false;
	private boolean looped = false;
	
	private int connectionsCount = 0;

	private ServerSocket serverSocket;
	private Thread listeningThread;

	protected boolean isLopped()
	{
		return this.looped;
	}

	public boolean isStarted()
	{
		return this.started;
	}

	protected abstract void performClient(Socket clientSocket) throws Exception;

	public void start(int port) throws IOException
	{
		if (this.started)
			throw new IOException("Server already started.");

		this.serverSocket = new ServerSocket(port);

		this.listeningThread = new ServerThread();
		this.listeningThread.start();
		
		this.started = true;
	}

	public boolean stop()
	{
		this.looped = false;

		return Utils.joinThread(this.listeningThread);
	}

	private class ClientThread extends Thread
	{
		private Socket socket;

		public ClientThread(Socket socket)
		{
			super("Client thread");
			this.socket = socket;
		}

		@Override
		public void run()
		{
			try
			{
				Server.this.performClient(this.socket);
			}
			catch (Exception e)
			{
				// assert true : e.getMessage();
			}
			finally
			{
				Utils.closeObject(this.socket);
				
				synchronized (Server.this.locker)
				{
					Server.this.connectionsCount -= 1;
				}
			}
		}
	}

	private class ServerThread extends Thread
	{
		public ServerThread()
		{
			super("Listening thread");
		}

		@Override
		public void run()
		{
			Server.this.looped = true;
			
			while (Server.this.looped)
			{
				try
				{
					Socket clientSocket = Server.this.serverSocket.accept();

					synchronized (Server.this.locker)
					{
						Server.this.connectionsCount += 1;
					}
					
					ClientThread clientThread = new ClientThread(clientSocket);
					clientThread.start();
				}
				catch (IOException e)
				{
					break;
				}
			}
			
			while(true)
			{
				synchronized (Server.this.locker)
				{
					if(Server.this.connectionsCount == 0)
						break;
				}
				
				Utils.sleepThread(100);
			}

			Utils.closeObject(Server.this.serverSocket);
			Server.this.started = false;
		}
	}
}
