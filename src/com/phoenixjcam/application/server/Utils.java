package com.phoenixjcam.application.server;

import java.io.Closeable;
import java.io.IOException;

public class Utils
{
	public static boolean closeObject(Closeable object)
	{
		try
		{
			object.close();
			
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	
	public static boolean joinThread(Thread thread)
	{
		try
		{
			thread.join();
			
			return true;
		}
		catch (InterruptedException e)
		{
			return false;
		}
	}

	public static boolean sleepThread(int time)
	{
		try
		{
			Thread.sleep(time);
			
			return true;
		}
		catch (InterruptedException e)
		{
			return false;
		}
	}
}
