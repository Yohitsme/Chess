package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log class. Logs to a file if the logLevel of the logging type called is
 * greater than or equal to the value of variable logLevel. I.E. logLevel 1 =
 * log everything. logLevel 3 = only log errors.
 * 
 * @author Matthew
 * 
 */
public class Log {

	private String fileName = "ChessLog.txt";
	private final int debugLogLevel = 1;
	private final int infoLogLevel = 2;
	private final int errorLogLevel = 3;
	private int logLevel = 5;

	public void info(String msg) {
		if (logLevel <= infoLogLevel) {
			write("[INFO] "+msg);
		}
	}

	public void debug(String msg) {
		if (logLevel <= debugLogLevel) {
			write("[DEBUG] "+msg);
		}
	}

	public void error(String msg) {
		if (logLevel <= errorLogLevel) {
			write("[ERROR] "+msg);
		}
	}

	public void write(String msg) {
		String output = "\n" + Utils.getTime() + " " + msg;
		
		Utils.writeToFile(fileName,output);

		}
	

	public void writeLine() {
		write(" -----------------------------------------------");

	}

}
