package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.Node;

/**
 * Log class. Logs to a file if the logLevel of the logging type called is
 * greater than or equal to the value of variable logLevel. I.E. logLevel 1 =
 * log everything. logLevel 3 = only log errors.
 * 
 * @author Matthew
 * 
 */
public class Log {

	private String fileName = "C:/Users/Matthew/Desktop/ChessLog.txt";
	private final int debugLogLevel = 1;
	private final int infoLogLevel = 2;
	private final int errorLogLevel = 3;
	private int logLevel = 3;

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
		String output = "\n" + getTime() + " " + msg;

		File file = new File(fileName);

		// if file doesnt exists, then create it
		
			try {
				//if (!file.exists()) 
				file.createNewFile();

				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);

				bw.append(output);

				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	

	public void writeLine() {
		write(" -----------------------------------------------");

	}

	public static String getTime() {
		long yourmilliseconds = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");

		Date resultdate = new Date(yourmilliseconds);
		return sdf.format(resultdate);
	}
}
