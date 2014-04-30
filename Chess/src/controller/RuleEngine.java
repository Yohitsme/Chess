package controller;

import model.Move;
import model.Piece;


/**
 * Static class used to check basic chess rules
 * @author Matthew
 *
 */
public class RuleEngine {

	/**
	 * All-in-one legal move checker.  This method calls various checks to ensure
	 * <li>No pieces were skipped over illegally</li>
	 * <li>The move style fit the piece</li>
	 * <li>The player doesn't result in the moving player putting themselves in check</li>
	 * <li>The piece doesn't get moved to the same square it came from</li>
	 * <li>The piece doesn't capture a piece of it's own color</li>
	 * 
	 * <p>True is returned if all checks find the move to be valid, and false otherwise.
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean validateMove(Move move, BoardController boardController) {

		boolean result = false;
		
		result = isNotSameSquare(move) && isNotSelfCapture(move, boardController);
		// Check legal move style
		// Check puts self in check

		return result;
	}

	/**
	 * Returns true if the start square is different than the end square, false otherwise
	 * @param move
	 * @return
	 */
	public static boolean isNotSameSquare(Move move){
		
		boolean result = false;
		result =  !(move.getStartCol() == move.getEndCol() && move.getStartRow() == move.getEndRow());
	
		if (!result){
			System.out.println("RuleEngine.isNotSameSquare: Returning false.");
		}
		
		return result;
	}
	
	/**
	 * Returns true if the color of the piece on the end square differs from that of the start square.
	 * Returns true if piece on end square is null.
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isNotSelfCapture(Move move, BoardController boardController){
		boolean result = false;
		
		Piece startPiece= boardController.getPieceByCoords(move.getStartRow(), move.getStartCol());
		Piece endPiece =boardController.getPieceByCoords(move.getEndRow(), move.getEndCol());
		
		// If the destination square is empty, it can't be a self capture
		if (endPiece== null)
			result = true;
		
		// If the color of the pieces on the starting and ending squares are the same, it's a self capture
		else if (startPiece.isWhite() == endPiece.isWhite() )
			result = false;
		else
			result = true;
		
		if (!result){
			System.out.println("RuleEngine.isNotSelfCapture: Returning false.");
		
		}
		
		return result;
	}
	
}
