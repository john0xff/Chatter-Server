package multithread.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils
{
	public static String getCurrentTime()
	{
		return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
	}
}
