package utils;

import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import view.PiecePanel;

/**
 * This class holds static methods that might be useful in numerous places
 * 
 * @author Matthew
 * 
 */
public class Utils {

	/**
	 * This casts and parses the row of a piece panel that is the source of a
	 * mouse event
	 * 
	 * @param e
	 * @return
	 */
	public static int getRowFromMouseEvent(MouseEvent e) {
		return ((PiecePanel) e.getSource()).getRow();
	}

	/**
	 * This casts and parses the column of a piece panel that is the source of a
	 * mouse event
	 * 
	 * @param e
	 * @return
	 */
	public static int getColFromMouseEvent(MouseEvent e) {
		return ((PiecePanel) e.getSource()).getCol();
	}

	/**
	 * Returns the proper letter designation for a column. Ex: a for 1, b for 2,
	 * ...h for 8
	 */
	public static char getAlgebraicCharacterFromCol(int col) {
		char result;

		if (col == 0)
			result = 'a';
		else if (col == 1)
			result = 'b';
		else if (col == 2)
			result = 'c';
		else if (col == 3)
			result = 'd';
		else if (col == 4)
			result = 'e';
		else if (col == 5)
			result = 'f';
		else if (col == 6)
			result = 'g';
		else if (col == 7)
			result = 'h';
		else
			result = '?';

		return result;
	}

	/**
	 * Returns the letter designation of the piece type passed as a parameter.
	 * Knight: n, King: k, Queen q, etc
	 * 
	 * @param type
	 * @return
	 */
	public static char getAlgebraicCharacterFromPieceType(String type) {

		char result;
		if (type.equals("knight"))
			result = 'n';
		else
			result = type.charAt(0);

		return result;
	}

	/**
	 * Writes a string to a file designated by <code>filename</code>
	 * @param fileName
	 * @param msg
	 */
	public static void writeToFile(String fileName, String msg) {

		String output = msg;

		File file = new File(fileName);

		// if file doesnt exists, then create it
		try {
			 if (!file.exists())
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
	
	
	/**
	 * Takes the memory preservative char representation of a piece and returns
	 * the string name
	 * @param piece
	 * @return
	 */
	public String getFullPieceName(char piece){
		String result = "";
		
		if (piece == 'p')
			result = "pawn";
		else if (piece == 'q')
			result = "queen";
		else if (piece == 'b')
			result = "bishop";
		else if (piece == 'r')
			result = "rook";
		else if (piece == 'n')
			result = "knight";
		else if (piece == 'k')
			result = "king";		
		else
			result = "?";
		
		return result;
	}

	/**
	 * Returns the current date and time in human readable format (MMM dd, yyyy HH:mm:ss)
	 * @return
	 */
	public static String getTime() {
		long yourmilliseconds = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

		Date resultdate = new Date(yourmilliseconds);
		return sdf.format(resultdate);
	}
	
	/**
	 * Returns the same thing as getTime, but with dashes and underscores instead of spaces
	 * so it can be used in a file path. Format: MM-dd-yyyy_HH-mm-ss
	 * @return
	 */
	public static String getTimeNoSpaces() {
		long yourmilliseconds = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");

		Date resultdate = new Date(yourmilliseconds);
		return sdf.format(resultdate);
	}
}
