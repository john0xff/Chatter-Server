package com.phoenixjcam.application.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils
{
	public final static String NEWLINE = "\n";
	public final static String CLEAR = "";
	
	public static String getCurrentTime()
	{
		return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
	}
}
