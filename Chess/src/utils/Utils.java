package utils;

import java.awt.event.MouseEvent;

import view.PiecePanel;

/**
 * This class holds static methods that might be useful in numerous places
 * @author Matthew
 *
 */
public class Utils {

	/**
	 * This casts and parses the row of a piece panel that is the source of a mouse event
	 * @param e
	 * @return
	 */
	public static int getRowFromMouseEvent(MouseEvent e){
		return ((PiecePanel) e.getSource()).getRow();
	}
	
	
	/**
	 * This casts and parses the column of a piece panel that is the source of a mouse event
	 * @param e
	 * @return
	 */
	public static int getColFromMouseEvent(MouseEvent e){
		return ((PiecePanel) e.getSource()).getCol();
	}
	
	/**
	 * Returns the proper letter designation for a column. Ex: a for 1, b for 2, ...h for 8
	 */
	public static char getAlgebraicCharacterFromCol(int col){
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
	 * Returns the letter designation of the piece type passed as a parameter. Knight: n, King: k, Queen q, etc
	 * @param type
	 * @return
	 */
	public static char getAlgebraicCharacterFromPieceType(String type){
		
		char result;
		if (type.equals("knight"))
				result = 'n';
		else
			result = type.charAt(0);
		
		return result;
	}
	
	
}
