package com.phoenixjcam.application.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer extends Server
{
	private Object locker = new Object();
	private HashMap<String, ClientEquipment> clients = new HashMap<String, ClientEquipment>();
	private MessageReceivedEvent messageReceivedEvent = null;

	public void setMessageReceivedEvent(MessageReceivedEvent messageReceivedEvent)
	{
		this.messageReceivedEvent = messageReceivedEvent;
	}

	private boolean registerClient(ClientEquipment equipment) throws IOException
	{
		String username = equipment.getUsername();

		synchronized (this.locker)
		{
			if (this.clients.containsKey(username))
			{
				equipment.getOutputStream().writeObject("\"" + username + "\" already exists.");
				return false;
			}

			this.clients.put(username, equipment);
		}

		return true;
	}

	private boolean deregisterClient(ClientEquipment equipment) throws IOException
	{
		synchronized (this.locker)
		{
			ClientEquipment eqipment = this.clients.remove(equipment.getUsername());
			Utils.closeObject(equipment);

			return (eqipment != null);
		}
	}

	private void broadcastMessage(ClientEquipment equipment, String message) throws IOException
	{
		String senderName = equipment.getUsername();
		ObjectOutputStream senderStream = equipment.getOutputStream();

		synchronized (this.locker)
		{
			for (ClientEquipment el : this.clients.values())
			{
				try
				{
					ObjectOutputStream recipientStream = el.getOutputStream();
					recipientStream.writeObject(senderName + ": " + message);
				}
				catch (IOException e)
				{
					senderStream.writeObject("Send operation failed!");
				}
			}
		}
	}

	@Override
	protected void performClient(Socket clientSocket) throws Exception
	{
		ClientEquipment equipment = new ClientEquipment(clientSocket);
		ObjectInputStream inputStream = equipment.getInputStream();

		try
		{
			String username = (String) inputStream.readObject();
			equipment.setUsername(username);

			if (this.registerClient(equipment))
			{
				while (this.isLopped())
				{
					String message = (String) inputStream.readObject();

					if ("exit".equals(message))
						break;

					if (this.messageReceivedEvent != null)
						this.messageReceivedEvent.performEvent(message);

					this.broadcastMessage(equipment, message);
				}
			}
		}
		finally
		{
			this.deregisterClient(equipment);

		}
	}

	@Override
	public boolean stop()
	{
		if (this.isStarted())
		{
			synchronized (this.locker)
			{
				for (ClientEquipment el : this.clients.values())
					Utils.closeObject(el);
			}

			return super.stop();
		}

		return false;
	}

	private class ClientEquipment implements Closeable
	{
		private Socket socket;
		private String username;

		private ObjectInputStream inputStream = null;
		private ObjectOutputStream outputStream = null;

		public ClientEquipment(Socket socket) throws IOException
		{
			this.socket = socket;

			try
			{
				this.inputStream = new ObjectInputStream(socket.getInputStream());
				this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			}
			finally
			{
				if (this.inputStream != null)
					Utils.closeObject(this.inputStream);
			}
		}

		public String getUsername()
		{
			return this.username;
		}

		public void setUsername(String username)
		{
			this.username = username;
		}

		public ObjectInputStream getInputStream()
		{
			return inputStream;
		}

		public ObjectOutputStream getOutputStream()
		{
			return outputStream;
		}

		@Override
		public void close() throws IOException
		{
			Utils.closeObject(this.inputStream);
			Utils.closeObject(this.outputStream);

			Utils.closeObject(this.socket);
		}
	}
}
