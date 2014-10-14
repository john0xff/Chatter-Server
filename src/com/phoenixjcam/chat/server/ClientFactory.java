package com.phoenixjcam.chat.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientFactory implements Runnable
{
	private Thread thread;
	private String threadName;
	
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	
	public ClientFactory()
	{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
}
