/*
Quiet Intrigue is a chess playing engine with GUI written in Java.
Copyright (C) <2014>  Matthew Voss

Quiet Intrigue is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Quiet Intrigue is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Quiet Intrigue.  If not, see <http://www.gnu.org/licenses/>.
*/

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
	private final int DEBUG = 1;
	private final int INFO = 2;
	private final int ERROR = 3;
	private int logLevel = ERROR;

	public void info(String msg) {
		if (logLevel <= INFO) {
			write("[INFO] "+msg);
		}
	}

	public void debug(String msg) {
		if (logLevel <= DEBUG) {
			write("[DEBUG] "+msg);
		}
	}

	public void error(String msg) {
		if (logLevel <= ERROR) {
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
