package controller;

import model.Model;
import model.Move;
import model.Piece;

/**
 * This class is the controller for the board. All advanced operations on the board are handled in this class
 * @author Matthew
 *
 */
public class BoardController {

	Model model;
	
	/**
	 * Constructor
	 * @param model
	 */
	public BoardController(Model model){
		this.model = model;
		
	}
	
	/**
	 * Gets the piece from the model at position (row,col). Prints error message if illegal arguments
	 * are passed.
	 * @param row
	 * @param col
	 * @return
	 */
	public Piece getPieceByCoords(int row, int col){
		Piece piece = null;
		
//		//piece.isWhite();
//		if (row >=8 || row <0){
//			System.out.println("BoardController.getPieceByCoords: Row value of " + row + " outside range [0,7]. Null value returned.");
//			
//		}else if (col >=8 || col <0){
//			System.out.println("BoardController.getPieceByCoords: Column value of " + col + " outside range [0,7]. Null value returned.");
//			
//		}else
			piece = model.getBoard()[row][col];

		
		return piece;
	}
	
	/**
	 * Sets a position on the board to Piece piece if row and col are in range [0,7].
	 * Returns true if piece was set successfully, false otherwise
	 * @param row
	 * @param col
	 * @param piece
	 * @return
	 */
	public boolean setPieceByCoords(int row, int col, Piece piece){
		boolean result = false;
		
		if (row >=8 || row <0){
			System.out.println("BoardController.setPieceByCoords: Row value of " + row + " outside range [0,7]. No action taken. Piece: " + piece.toString());
		}else if (col >=8 || col <0){
			System.out.println("BoardController.setPieceByCoords: Column value of " + col + " outside range [0,7]. No action taken. Piece: " + piece.toString());
	}else if (piece == null)
			System.out.println("BoardController.setPieceByCoords: Error; attempt to move null piece ignored.");
		else{
			model.getBoard()[row][col] = piece;
			result = true;
			
	}
		return result;
	}
	
	
	/**
	 * Sets the square (row, col) to null if it is within the bounds of the board. Returns true if
	 * operation was successful (false if bad parameters)
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean clearSquare(int row, int col){
		boolean result = false;
		
		if (row >=8 || row <0)
			System.out.println("Model.clearSquare: Row value of " + row + " outside range [0,7]. No action taken.");
		else if (col >=8 || col <0)
			System.out.println("Model.clearSquare: Column value of " + row + " outside range [0,7]. No action taken.");
		else{
			model.getBoard()[row][col] = null;
			result = true;
			
	}
		return result;
	}
}
