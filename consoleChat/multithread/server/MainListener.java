package multithread.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainListener
{
	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	private static final int maxClientsCount = 10;
	private static final ClientSocket[] CLIENT_SOCKETS = new ClientSocket[maxClientsCount];

	public MainListener()
	{
		int portNumber = 9005;

		try
		{
			serverSocket = new ServerSocket(portNumber);
			System.out.println(Utils.getCurrentTime() + " " +  "Listening on port - " + portNumber);
		}
		catch (IOException e)
		{
			System.out.println(e);
		}

		while (true)
		{
			try
			{
				clientSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxClientsCount; i++)
				{
					if (CLIENT_SOCKETS[i] == null)
					{ 
						// for each new client new thread for broadcasting concurrently
						(CLIENT_SOCKETS[i] = new ClientSocket(clientSocket, CLIENT_SOCKETS)).start();

						System.out.println(Utils.getCurrentTime() + " " + "start - next client nr - " + i);

						break;
					}
				}
				if (i == maxClientsCount)
				{
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	}

	public static void main(String args[])
	{
		new MainListener();
	}
}