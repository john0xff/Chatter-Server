package console.multichat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable
{
	private static Socket clientSocket;
	private static ObjectInputStream objectInputStream;
	private static ObjectOutputStream objectOutputStream;

	private static BufferedReader bufferedReader;
	private static boolean closed = false;

	public static void main(String[] args)
	{
		// The default port.
		int portNumber = 2222;
		// The default host.
		String host = "localhost";
		
		try
		{
			clientSocket = new Socket(host, portNumber);
			bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			
			objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (clientSocket != null && objectOutputStream != null && objectInputStream != null)
		{
			new Thread(new Client()).start();
			
			while (!closed)
			{
				
				try
				{
					objectOutputStream.writeObject(bufferedReader.readLine().toString());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						objectOutputStream.close();
						objectInputStream.close();
						clientSocket.close();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
	}

	public Client()
	{

	}

	@Override
	public void run()
	{
		String response;
		
		try
		{
			while ((response = objectInputStream.readLine()) != null)
			{
				System.out.println(response);
			}
			
			closed = true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
