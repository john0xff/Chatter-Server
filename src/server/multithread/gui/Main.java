package server.multithread.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main
{
	private static Socket clientSocket;
	private static ServerSocket serverSocket;

	private static final int maxClientsCount = 10;
	private static final ClientThread[] threads = new ClientThread[maxClientsCount];

	private JFrame frame;
	private JTextField userText;
	private JTextArea textArea;

	public static void main(String[] args)
	{
		new Main().frame();

		final int port = 2222;

		try
		{
			serverSocket = new ServerSocket(port);
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
					if (threads[i] == null)
					{
						(threads[i] = new ClientThread(clientSocket, threads)).start();
						break;
					}
				}
				if (i == maxClientsCount)
				{
					// PrintStream os = new PrintStream(clientSocket.getOutputStream());
					// os.println("Server too busy. Try later.");
					// os.close();
					clientSocket.close();
				}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	}

	private void frame()
	{

		frame = new JFrame("MultithreadServer");
		frame.setSize(600, 400);

		textArea = new JTextArea("Add all msg here" + "\n");
		textArea.setFont(new Font("Arial", 0, 20));
		textArea.setEnabled(false);
		frame.add(textArea, BorderLayout.CENTER);

		userText = new JTextField("Type here");
		userText.setEnabled(false);
		userText.setFont(new Font("Arial", 0, 20));

		userText.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String msg = e.getActionCommand();

				// try
				// {
				// objectOutputStream.writeObject(msg);
				// }
				// catch (IOException e1)
				// {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }

				textArea.append("Server: " + msg + "\n");
			}
		});

		frame.add(userText, BorderLayout.NORTH);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
