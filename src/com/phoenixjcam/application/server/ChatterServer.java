package com.phoenixjcam.application.server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatterServer extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int width = 640;
    private final int height = 480;

    private ServerSocket serverSocket;
    private Socket connectionSocket;

    // make sure that port is available
    private int port;

    private JScrollPane scPane;
    private JTextArea txtArea;

    public ChatterServer(int port) {
	super("Chatter Server");

	this.port = port;

	scPane = new JScrollPane(getTxtArea());
	add(scPane, BorderLayout.CENTER);

	frameSettings();
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
    }

    public void frameSettings() {
	setSize(width, height);
	Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment()
		.getCenterPoint();
	setLocation(100, (centerPoint.y) - (height / 2));
    }

    private JTextArea getTxtArea() {
	txtArea = new JTextArea();
	txtArea.setFont(new Font("Arial", 0, 20));
	return txtArea;
    }

    public void runServer() {
	try {
	    serverSocket = new ServerSocket(port);

	    // InetAddress ip = Inet4Address.getLocalHost();

	    connectionWait();

	    txtArea.append("sending test" + "\n");

	    byte[] b = new byte[10];
	    byte ascii = 98;
	    // ascii code
	    for (int i = 0; i < b.length; i++) {
		b[i] = ascii;
		ascii++;
	    }

	    for (int i = 0; i < b.length; i++) {
		connectionSocket.getOutputStream().write(b[i]);
	    }

	    closeSocket();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void connectionWait() {
	txtArea.append("waiting for connection" + "\n");
	try {
	    connectionSocket = serverSocket.accept();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	String hostName = connectionSocket.getInetAddress().getHostName();
	// InetAddress ip = connectionSocket.getInetAddress();
	txtArea.append("connected to " + hostName + "\n");
    }

    private void closeSocket() {
	try {
	    connectionSocket.close();
	} catch (IOException ioException) {
	    ioException.printStackTrace();
	}
    }

    public static void main(String[] args) {

	int port = 9002;

	new ChatterServer(port).runServer();
    }
}
