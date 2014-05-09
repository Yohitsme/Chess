package controller;

import model.Move;
import model.Piece;

/**
 * Static class used to check basic chess rules
 * 
 * @author Matthew
 * 
 */
public class RuleEngine {

	/**
	 * All-in-one legal move checker. This method calls various checks to ensure
	 * <li>No pieces were skipped over illegally</li> <li>The move style fit the
	 * piece</li> <li>The player doesn't result in the moving player putting
	 * themselves in check</li> <li>The piece doesn't get moved to the same
	 * square it came from</li> <li>The piece doesn't capture a piece of it's
	 * own color</li>
	 * 
	 * <p>
	 * True is returned if all checks find the move to be valid, and false
	 * otherwise.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean validateMove(Move move,
			BoardController boardController) {

		boolean result = true;
		boolean debugPrint = true;

		if (!isNotSameSquare(move)) {
			result = false;
			if (debugPrint)
				System.out
						.println("RuleEngine.validateMove: Invalid move: You cannot move a piece to the same square it started on.");
		}
		if (!isNotSelfCapture(move, boardController)) {
			result = false;
			if (debugPrint)
				System.out
						.println("RuleEngine.validateMove: Invalid move: You cannot capture your own piece.");
		}
		if (!isLegalMoveStyle(move, boardController)) {
			result = false;
			if (debugPrint)
				System.out
						.println("RuleEngine.validateMove: Invalid move: You cannot move a "
								+ move.getPiece() + " like that.");
		}
		if (!isUnblocked(move, boardController)) {
			result = false;
			if (debugPrint)
				System.out
						.println("RuleEngine.validateMove: Invalid move: The path is blocked.");
		}
		if (!isNotSelfCheck(move, boardController)) {
			result = false;
			if (debugPrint)
				System.out
						.println("RuleEngine.validateMove: Invalid move: You cannot put yourself in check.");
		}

		return result;
	}

	/**
	 * Returns true if the start square is different than the end square, false
	 * otherwise
	 * 
	 * @param move
	 * @return
	 */
	public static boolean isNotSameSquare(Move move) {

		boolean result = false;
		result = !(move.getStartCol() == move.getEndCol() && move.getStartRow() == move
				.getEndRow());

		return result;
	}

	/**
	 * Returns true if the color of the piece on the end square differs from
	 * that of the start square. Returns true if piece on end square is null.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isNotSelfCapture(Move move,
			BoardController boardController) {
		boolean result = false;

		Piece startPiece = boardController.getPieceByCoords(move.getStartRow(),
				move.getStartCol());
		Piece endPiece = boardController.getPieceByCoords(move.getEndRow(),
				move.getEndCol());

		// If the destination square is empty, it can't be a self capture
		if (endPiece == null)
			result = true;

		// If the color of the pieces on the starting and ending squares are the
		// same, it's a self capture
		else if (startPiece.isWhite() == endPiece.isWhite())
			result = false;
		else
			result = true;

		return result;
	}

	/**
	 * Delegates to the appropriate method to check if the style of move matches
	 * the piece being moved.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isLegalMoveStyle(Move move,
			BoardController boardController) {
		boolean result = false;
		if (move.getPiece().getType().equals("rook"))
			result = isLegalRookMove(move, boardController);
		else if (move.getPiece().getType().equals("queen"))
			result = isLegalQueenMove(move, boardController);
		else if (move.getPiece().getType().equals("knight"))
			result = isLegalKnightMove(move, boardController);
		else if (move.getPiece().getType().equals("pawn"))
			result = isLegalPawnMove(move, boardController);
		else if (move.getPiece().getType().equals("king"))
			result = isLegalKingMove(move, boardController);
		else if (move.getPiece().getType().equals("bishop"))
			result = isLegalBishopMove(move, boardController);

		else
			System.out.println("RuleEngine.isLegalMoveStyle: Piece type \'"
					+ move.getPiece().getType()
					+ "\' not recognized. Returning false.");

		return result;
	}

	/**
	 * Returns true if move was along a column or along a row.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isLegalRookMove(Move move,
			BoardController boardController) {
		boolean result = false;

		if (move.getEndCol() == move.getStartCol()
				|| move.getStartRow() == move.getEndRow())
			result = true;

		return result;
	}

	/**
	 * Returns true if piece moved along a column or row, or if the change in
	 * row number is equal to the change in column number
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isLegalQueenMove(Move move,
			BoardController boardController) {
		boolean result = false;

		// If moving like a rook
		if (move.getEndCol() == move.getStartCol()
				|| move.getStartRow() == move.getEndRow())
			result = true;

		// If moving like a bishop
		else if (calculateDeltaRow(move) == calculateDeltaCol(move)) {
			result = true;
		}

		return result;
	}

	/**
	 * Returns true if piece moved one or two squares forward, captured one
	 * square diagonally, or captured via en passent
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isLegalPawnMove(Move move,
			BoardController boardController) {
		boolean result = false;

		// TODO En passant
		// If it's a capture move
		if (boardController
				.getPieceByCoords(move.getEndRow(), move.getEndCol()) != null)
			System.out
					.println("RuleEngine.isLegalPawnMove: Pawn capture logic not yet coded."
							+ boardController.getPieceByCoords(
									move.getEndRow(), move.getEndCol()));

		// If it's not a capture move
		else {
			int deltaRow = move.getEndRow() - move.getStartRow();

			if (move.getEndCol() == move.getStartCol()
					&& (deltaRow == 1 || deltaRow == 2)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Returns true if the number of columns moved is equal to the number of
	 * rows moved
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isLegalBishopMove(Move move,
			BoardController boardController) {
		boolean result = false;

		int deltaRow = calculateDeltaRow(move);
		int deltaCol = calculateDeltaCol(move);

		if (deltaRow == deltaCol)
			result = true;

		return result;
	}

	/**
	 * Returns true if move was in an "L"
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isLegalKnightMove(Move move,
			BoardController boardController) {
		boolean result = false;

		int deltaRow = calculateDeltaRow(move);
		int deltaCol = calculateDeltaCol(move);

		if ((deltaRow == 1 && deltaCol == 2)
				|| (deltaRow == 2 && deltaCol == 1))
			result = true;

		return result;
	}

	/**
	 * Returns true if the piece moves less than 2 squares in any direction
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isLegalKingMove(Move move,
			BoardController boardController) {
		boolean result = false;

		int deltaRow = calculateDeltaRow(move);
		int deltaCol = calculateDeltaCol(move);

		if (deltaRow < 2 && deltaCol < 2)
			result = true;

		return result;
	}

	public static boolean isUnblocked(Move move, BoardController boardController) {
		boolean result = true;

		if (move.getPiece().getType().equals("rook"))
			result = isUnblockedRookPath(move, boardController);
		else if (move.getPiece().getType().equals("queen"))
			result = isUnblockedQueenPath(move, boardController);
		else if (move.getPiece().getType().equals("knight"))
			result = isUnblockedKnightPath(move, boardController);
		else if (move.getPiece().getType().equals("pawn"))
			result = isUnblockedPawnPath(move, boardController);
		else if (move.getPiece().getType().equals("king"))
			result = isUnblockedKingPath(move, boardController);
		else if (move.getPiece().getType().equals("bishop"))
			result = isUnblockedBishopPath(move, boardController);
		else
			System.out.println("RuleEngine.isUnblocked: Piece type \'"
					+ move.getPiece().getType()
					+ "\' not recognized. Returning false.");

		return result;
	}

	/**
	 * Returns true. Knight paths are never blocked
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isUnblockedKnightPath(Move move,
			BoardController boardController) {
		return true;
	}

	public static boolean isUnblockedBishopPath(Move move,
			BoardController boardController) {
		boolean result = false;

		return result;
	}

	public static boolean isUnblockedRookPath(Move move,
			BoardController boardController) {
		boolean result = true;
		int distance = 0;
		int direction = 0;

		// If moving across columns
		if (move.getStartCol() != move.getEndCol()) {
			distance = Math.abs(move.getStartCol() - move.getEndCol());

			if (move.getStartCol() > move.getEndCol())
				direction = -1;
			else
				direction = 1;

			for (int i = 1; i < distance; i++) {

				int newCol = move.getStartCol() + (direction * i);

				if (boardController
						.getPieceByCoords(move.getStartRow(), newCol) != null){
					result = false;
					System.out.println("RuleEngine.isUnblockedRookPath: Path blocked. " + boardController
							.getPieceByCoords(move.getStartRow(), newCol).getType() + " found on row " + move.getStartRow() + ", col " + newCol);
				}
			}
		}
		// If moving across rows
		else {
			distance = Math.abs(move.getStartRow() - move.getEndRow());

			if (move.getStartRow() > move.getEndRow())
				direction = -1;
			else
				direction = 1;

			for (int i = 1; i < distance; i++) {

				int newRow = move.getStartRow() + (direction * i);

				if (boardController
						.getPieceByCoords(newRow, move.getStartCol()) != null){
					result = false;
//					System.out.println("RuleEngine.isUnblockedRookPath: Path blocked. " + boardController
//							.getPieceByCoords(move.getStartRow(), newCol).getType() + " found on row " + move.getStartRow() + ", col " + newCol);
				}
			}

		}

		return result;
	}

	public static boolean isUnblockedPawnPath(Move move,
			BoardController boardController) {
		boolean result = true;

		return result;
	}

	public static boolean isUnblockedQueenPath(Move move,
			BoardController boardController) {
		boolean result = false;

		return result;
	}

	public static boolean isUnblockedKingPath(Move move,
			BoardController boardController) {
		return true;
	}

	public static boolean isNotSelfCheck(Move move,
			BoardController boardController) {
		boolean result = true;
		return result;
	}

	public static int calculateDeltaRow(Move move) {
		return Math.abs(move.getStartRow() - move.getEndRow());
	}

	public static int calculateDeltaCol(Move move) {
		return Math.abs(move.getStartCol() - move.getEndCol());
	}

}
