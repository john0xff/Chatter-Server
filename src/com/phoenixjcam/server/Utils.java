package com.phoenixjcam.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;

public class Utils
{
	public final static String NEWLINE = "\n";
	public final static String CLEAR = "";

	public static String getCurrentTime()
	{
		return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
	}

	/**
	 * Print msg in console and in gui for debug mode. Main Frame need to be initialized.
	 * 
	 * @param serverMsg
	 */
	public static void printServerMsg(String serverMsg, ServerGUI serverGUI)
	{
		System.out.println(serverMsg);
		serverGUI.getTextArea().append(serverMsg + Utils.NEWLINE);
	}


}
