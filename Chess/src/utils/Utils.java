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
	
}
