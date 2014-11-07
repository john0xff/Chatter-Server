package multithread.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class ClientSocket extends Thread
{

	private String clientName = null;
	private DataInputStream is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	private final ClientSocket[] threads;
	private int maxClientsCount;

	public ClientSocket(Socket clientSocket, ClientSocket[] threads)
	{
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run()
	{
		int maxClientsCount = this.maxClientsCount;
		ClientSocket[] threads = this.threads;

		try
		{
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			String name;
			while (true)
			{
				os.println("Enter your name.");
				name = is.readLine().trim();
				if (name.indexOf('@') == -1)
				{
					break;
				}
				else
				{
					os.println("The name should not contain '@' character.");
				}
			}

			os.println("Welcome " + name + " to our chat room.\nTo leave enter /quit in a new line.");
			System.out.println(Utils.getCurrentTime() + " "+ "new user (" + name + ")");
			
			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null && threads[i] == this)
					{
						clientName = "@" + name;
						break;
					}
				}
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null && threads[i] != this)
					{
						threads[i].os.println("*** A new user " + name + " entered the chat room !!! ***");
					}
				}
			}

			while (true)
			{
				String line = is.readLine();
				if (line.startsWith("/quit"))
				{
					break;
				}

				if (line.startsWith("@")) // If the message is private sent it to the given client.
				{
					String[] words = line.split("\\s", 2);
					if (words.length > 1 && words[1] != null)
					{
						words[1] = words[1].trim();
						if (!words[1].isEmpty())
						{
							synchronized (this)
							{
								for (int i = 0; i < maxClientsCount; i++)
								{
									if (threads[i] != null && threads[i] != this && threads[i].clientName != null && threads[i].clientName.equals(words[0]))
									{
										threads[i].os.println("<" + name + "> " + words[1]);

										this.os.println(">" + name + "> " + words[1]); // Echo this message to let the
																						// client know the private
																						// message was sent.
										break;
									}
								}
							}
						}
					}
				}
				else // The message is public, broadcast it to all other clients.
				{
					synchronized (this)
					{
						for (int i = 0; i < maxClientsCount; i++)
						{
							if (threads[i] != null && threads[i].clientName != null)
							{
								threads[i].os.println("<" + name + "> " + line);
								System.out.println(Utils.getCurrentTime() + " ("+ name + ") msg = " + line);
							}
						}
						System.out.println("-----------------------------------");
					}
				}
			}
			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null && threads[i] != this && threads[i].clientName != null)
					{
						threads[i].os.println("*** The user " + name + " is leaving the chat room !!! ***");
					}
				}
			}
			os.println("*** Bye " + name + " ***");

			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] == this)
					{
						threads[i] = null;
					}
				}
			}
			
			is.close();
			os.close();
			clientSocket.close();
		}
		catch (IOException e)
		{
		}
	}
}
