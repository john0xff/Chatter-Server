package server.closable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerTest
{
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private int port;

	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	private JFrame frame;
	private JTextField userText;
	private JTextArea textArea;

	public ServerTest(int port)
	{
		frame = new JFrame("server");
		userText = new JTextField();
		userText.setEnabled(false);

		userText.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				String msg = e.getActionCommand();

				try
				{
					objectOutputStream.writeObject(msg);
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				textArea.append("Server: " + msg + "\n");
			}
		});

		frame.add(userText, BorderLayout.NORTH);

		textArea = new JTextArea();
		textArea.setEnabled(false);
		frame.add(textArea, BorderLayout.CENTER);

		frame.setSize(600, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		while (true)
		{
			try
			{
				serverSocket = new ServerSocket(port);
				clientSocket = serverSocket.accept();

				objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
				userText.setEnabled(true);

				String msg = null;

				try
				{
					do
					{
						// if(objectInputStream.)
						msg = objectInputStream.readObject().toString();

						textArea.append("Client: " + msg + "\n");
					}
					while (msg != "END");
				}
				catch (ClassNotFoundException | IOException e1)
				{
					e1.printStackTrace();
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				closeStreams();
				closeSockets();
			}
		}
	}

	private void closeStreams()
	{
		try
		{
			clientSocket.shutdownOutput();
			clientSocket.shutdownInput();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void closeSockets()
	{
		try
		{
			clientSocket.close();
			serverSocket.close();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		new ServerTest(9001);
	}

}
