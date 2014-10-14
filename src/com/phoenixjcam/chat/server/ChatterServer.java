package com.phoenixjcam.chat.server;

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
 * Server implementation of multiclient communicator Each client runs in new Thread.
 * 
 * @author Bart Bien
 *
 */
public class ChatterServer extends JFrame implements Runnable
{
	private static final long serialVersionUID = -7609660477991900485L;

	private final int width = 640;
	private final int height = 480;

	// make sure that port is available - cmd: netstat -a
	private int port;
	private ServerSocket serverSocket;
	private Socket connectionSocket;
	
	private static int clientCounter = 0;

	// all stuff related to communication between 1 instance of server and several clients
	private Communication communication;

	public ChatterServer()
	{
		super("Chatter Server");
		//this.port = port;

		communication = new Communication();
		add(communication.getJScrollPane(), BorderLayout.CENTER);
		frameSettings();
		//runServer();
	}

	@Override
	public void run()
	{
		
	}
	
	public Runnable newClientConnection(Socket socket)
	{
		connectionSocket = socket;
		clientCounter++;
		
		return this;
	}

	public void frameSettings()
	{
		setSize(width, height);
		Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setLocation(100, (centerPoint.y) - (height / 2));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void runServer()
	{
		try
		{
			serverSocket = new ServerSocket(port);

			// waiting until client is connected
			clinetConnection();

			// performCommunication();
			communication.communicate();
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			communication.closeStreams();
			closeSockets();
		}
	}

	private void clinetConnection() throws IOException
	{
		communication.getChatArea().append("waiting for connection" + communication.NEWLINE);

		// waiting until client is connected
		connectionSocket = serverSocket.accept();

		InetAddress hostName = connectionSocket.getInetAddress();
		// InetAddress ip = connectionSocket.getInetAddress();

		communication.getChatArea().append("connected to " + hostName + communication.NEWLINE);

		// when client is connected set streams
		communication.setupOutputStream(new ObjectOutputStream(connectionSocket.getOutputStream()));
		communication.setupInputStream(new ObjectInputStream(connectionSocket.getInputStream()));

	}

	private void closeSockets()
	{
		ServerUtils.closeObject(connectionSocket);
		ServerUtils.closeObject(serverSocket);
	}
}