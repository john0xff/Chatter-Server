package com.phoenixjcam.application.server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * test commit
 *
 * @author Bart Bien
 *
 */
public class ChatterServer extends JFrame
{
	private static final long serialVersionUID = -7609660477991900485L;
	private final int width = 640;
	private final int height = 480;
	// make sure that port is available
	private int port;
	// sockets
	private ServerSocket serverSocket;
	private Socket connectionSocket;
	// streams
	private ObjectOutputStream output;
	private ObjectInputStream input;
	// components
	private JScrollPane scrollPane;
	private JTextArea chatArea;
	private JTextField userText;
	private String message;
	private final static String NEWLINE = "\n";
	private final static String EXITCMD = "\nCLIENT - EXIT";
	// time
	private Calendar calendar;
	private SimpleDateFormat dateFormat;
	private String currentTime;

	public ChatterServer(int port)
	{
		super("Chatter Server");
		this.port = port;
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		scrollPane = new JScrollPane(getChatArea());
		add(scrollPane, BorderLayout.CENTER);
		add(createUserText(), BorderLayout.NORTH);
		frameSettings();
		runServer();
	}

	public void frameSettings()
	{
		setSize(width, height);
		Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setLocation(100, (centerPoint.y) - (height / 2));
		// ImageIO.read(input)
		ImageIcon img = new ImageIcon(ChatterServer.class.getResource("res/icoB.png"));
		setIconImage(img.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private JTextArea getChatArea()
	{
		chatArea = new JTextArea();
		chatArea.setFont(new Font("Arial", 0, 20));
		return chatArea;
	}

	/** send server text to client and append server text to server chat area */
	private JTextField createUserText()
	{
		userText = new JTextField();
		userText.setFont(new Font("Arial", 0, 20));
		userText.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				String serverMessage = event.getActionCommand();
				try
				{
					calendar = Calendar.getInstance();
					currentTime = dateFormat.format(calendar.getTime());
					// send server text to client
					output.writeObject(NEWLINE + currentTime + " SERVER - " + serverMessage);
					output.flush();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				// append server text to area
				chatArea.append(NEWLINE + currentTime + " SERVER - " + serverMessage);
				userText.setText("");
			}
		});
		return userText;
	}

	public void runServer()
	{
		try
		{
			serverSocket = new ServerSocket(port);
			// waiting until client is connected
			clinetConnection();
			performCommunication();
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeStreams();
			closeSockets();
		}
	}

	private void clinetConnection() throws IOException
	{
		chatArea.append("waiting for connection" + NEWLINE);
		// waiting until client is connected
		connectionSocket = serverSocket.accept();
		InetAddress hostName = connectionSocket.getInetAddress();
		// InetAddress ip = connectionSocket.getInetAddress();
		chatArea.append("connected to " + hostName + NEWLINE);
		// when client is connected set streams
		output = new ObjectOutputStream(connectionSocket.getOutputStream());
		// output.flush();
		input = new ObjectInputStream(connectionSocket.getInputStream());
	}

	private void performCommunication() throws ClassNotFoundException, IOException
	{
		do
		{
			// receive message form client side
			message = (String) input.readObject();
			// append to server chat area
			chatArea.append(message);
		}
		while (!message.equals(EXITCMD));
	}

	private void closeObject(Closeable object)
	{
		try
		{
			object.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeStreams()
	{
		closeObject(input);
		closeObject(output);
	}

	private void closeSockets()
	{
		closeObject(connectionSocket);
		closeObject(serverSocket);
	}

	public static void main(String[] args)
	{
		int port = 9002;
		// create and show gui
		new ChatterServer(port);
	}
}