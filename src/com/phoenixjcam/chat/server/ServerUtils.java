package com.phoenixjcam.chat.server;

import java.io.Closeable;
import java.io.IOException;

public class ServerUtils
{
	public static void closeObject(Closeable object)
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
}
