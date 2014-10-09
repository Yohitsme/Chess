package controller;

import java.util.ArrayList;

import model.Move;
import model.Piece;
import model.PieceArray;
import utils.Constants;
import utils.Log;

/**
 * Static class used to check basic chess rules
 * 
 * @author Matthew
 * 
 */
public class RuleEngine {
	private static boolean printFlag;
	private static Controller controller;
	static Log log = new Log();

	public RuleEngine(Controller controllerIn) {
		RuleEngine.controller = controllerIn;
	}

	/**
	 * All-in-one legal move checker. This method calls various checks to ensure
	 * <li>No pieces were skipped over illegally</li> <li>The move style fit the
	 * piece</li> <li>The move doesn't result in the moving player putting
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
			BoardController boardController, boolean printFlag) {
		RuleEngine.printFlag = printFlag;

		boolean result = true;

		if (move.getEndCol() == 2 && move.getEndRow() == 0)
			printFlag = true;

		// All subsequent methods are only called if result is still true. If we
		// already found a reason the move is illegal, we can quit looking and
		// return false.
		if (!isNotSameSquare(move)) {
			result = false;
			if (printFlag)
				System.out
						.println("RuleEngine.validateMove: Invalid move: You cannot move a piece to the same square it started on.");
		}
		if (result) {
			if (!isNotSelfCapture(move, boardController)) {
				result = false;
				if (printFlag)
					System.out
							.println("RuleEngine.validateMove: Invalid move: You cannot capture your own piece.");
			}
		}
		if (result) {
			if (!isLegalMoveStyle(move, boardController)) {
				result = false;
				if (printFlag)
					System.out
							.println("RuleEngine.validateMove: Invalid move: You cannot move a "
									+ move.getPiece() + " like that.");
			}
		}
		if (result) {
			if (!isUnblocked(move, boardController)) {
				result = false;
				if (printFlag)
					System.out
							.println("RuleEngine.validateMove: Invalid move: The path is blocked.");
			}
		}
		if (result) {
			if (!isNotSelfCheck(move, boardController)) {
				result = false;
				if (printFlag)
					System.out
							.println("RuleEngine.validateMove: Invalid move: You cannot put yourself in check.");
			}
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
		if (move.getPiece().getType() == Constants.getRookChar())
			result = isLegalRookMove(move, boardController);
		else if (move.getPiece().getType() == Constants.getQueenChar())
			result = isLegalQueenMove(move, boardController);
		else if (move.getPiece().getType() == Constants.getKnightChar())
			result = isLegalKnightMove(move, boardController);
		else if (move.getPiece().getType() == Constants.getPawnChar())
			result = isLegalPawnMove(move, boardController);
		else if (move.getPiece().getType() == Constants.getKingChar())
			result = isLegalKingMove(move);
		else if (move.getPiece().getType() == Constants.getBishopChar())
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
		else if (calculateDeltaRowUnsigned(move) == calculateDeltaColUnsigned(move)) {
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
		// If it's a not capture move
		if (move.getStartCol() == move.getEndCol()
				&& boardController.getPieceByCoords(move.getEndRow(),
						move.getEndCol()) == null) {
			int deltaRow = move.getEndRow() - move.getStartRow();

			if (move.getEndCol() == move.getStartCol()) {
				// White pawns
				if (deltaRow == 1 && move.getPiece().isWhite())
					result = true;
				else if (deltaRow == 2 && !move.getPiece().isHasMoved()
						&& move.getPiece().isWhite())
					result = true;
				// Black pawns
				else if (deltaRow == -1 && !move.getPiece().isWhite())
					result = true;
				else if (deltaRow == -2 && !move.getPiece().isHasMoved()
						&& !move.getPiece().isWhite())
					result = true;
			}
		} else
		// If normal capture
		if (boardController
				.getPieceByCoords(move.getEndRow(), move.getEndCol()) != null) {

			int deltaCol = calculateDeltaColUnsigned(move);
			int deltaRow = move.getEndRow() - move.getStartRow();

			// Legal purchase
			if (deltaCol == 1) {

				// If white pawn
				if (move.getPiece().isWhite() && deltaRow == 1)
					result = true;
				// If black pawn
				else if (!move.getPiece().isWhite() && deltaRow == -1)
					result = true;

			}
		}
		// If en passant
		else if (!controller.getModel().getMoveList().isEmpty()) {
			Move previousMove = controller.getModel().getMoveList()
					.get(controller.getModel().getMoveList().size() - 1);
			boolean previousPieceIsPawn = previousMove.getPiece().getType() == Constants
					.getPawnChar();
			boolean previousPieceIsDifferentColor = previousMove.getPiece()
					.isWhite() != move.getPiece().isWhite();
			boolean correctCol = move.getEndCol() == previousMove.getStartCol()
					&& calculateDeltaColUnsigned(move) == 1;

			if (previousPieceIsPawn && previousPieceIsDifferentColor
					&& correctCol) {
				// If white pawn
				if (move.getPiece().isWhite()) {
					if (calculateDeltaRowSigned(previousMove) == -2
							&& move.getEndRow() == (previousMove.getEndRow() + 1)
							&& move.getStartRow() == (previousMove.getEndRow()))
						result = true;
				}
				// If black pawn
				else {
					if (calculateDeltaRowSigned(previousMove) == 2
							&& move.getEndRow() == (previousMove.getEndRow() - 1)
							&& move.getStartRow() == (previousMove.getEndRow()))
						result = true;

				}
			}
		}
		// If it's not a capture move
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

		int deltaRow = calculateDeltaRowUnsigned(move);
		int deltaCol = calculateDeltaColUnsigned(move);

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

		int deltaRow = calculateDeltaRowUnsigned(move);
		int deltaCol = calculateDeltaColUnsigned(move);

		if ((deltaRow == 1 && deltaCol == 2)
				|| (deltaRow == 2 && deltaCol == 1))
			result = true;

		return result;
	}

	/**
	 * Returns true if the piece moves less than 2 squares in any direction or
	 * castled legally.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isLegalKingMove(Move move) {
		boolean result = false;
		BoardController boardController = controller.getBoardController();
		int deltaRow = calculateDeltaRowUnsigned(move);
		int deltaCol = calculateDeltaColUnsigned(move);

		// Normal moving
		if (deltaRow < 2 && deltaCol < 2)
			result = true;

		// Castling
		else if (deltaCol == 2 && deltaRow == 0
				&& (move.getStartRow() == 0 || move.getStartRow() == 7)) {

			// Conditions for castling
			boolean kingHasMoved = false;
			boolean rookHasMoved = false;
			boolean isInCheck = false;
			boolean isCastlingThroughCheck = false;
			boolean isCastlingIntoCheck = false;
			boolean piecesBetweenRookAndKing = false;
			boolean isRookDeadOrMissing = false;

			// Get signed deltaCol
			deltaCol = calculateDeltaColSigned(move);

			String color = move.getPiece().isWhite() ? "black" : "white";
			kingHasMoved = move.getPiece().isHasMoved();

			// Only continue if king hasn't moved
			if (!kingHasMoved) {
				isInCheck = isAttackedSquare(move.getPiece().getRow(), move
						.getPiece().getCol(), color);
				// If castling kingside and rook is alive
				if (deltaCol > 0) {
					if (boardController.getPieceByCoords(move.getStartRow(), 7) == null)
						isRookDeadOrMissing = true;
					else {

						rookHasMoved = boardController.getPieceByCoords(
								move.getStartRow(), 7).isHasMoved();

						for (int i = 1; i < 3; i++) {
							if (boardController.getPieceByCoords(
									move.getStartRow(), move.getStartCol() + i) != null) {
								piecesBetweenRookAndKing = true;
								if (move.getStartCol() + i > 7)
									System.out.print("Out of bounds");
							}
						}
						int row = move.getPiece().getRow();
						int col = move.getPiece().getCol() + 1;
						isCastlingThroughCheck = isKingVulnerableOnThisSquare(
								boardController, color, move, row, col);

						col = col + 1;
						isCastlingIntoCheck = isKingVulnerableOnThisSquare(
								boardController, color, move, row, col);

					}
				}
				// If castling queenside and rook is alive
				else if (boardController
						.getPieceByCoords(move.getStartRow(), 0) == null)
					isRookDeadOrMissing = true;
				else {
					rookHasMoved = boardController.getPieceByCoords(
							move.getStartRow(), 0).isHasMoved();
					for (int i = 1; i < 4; i++) {
						if (boardController.getPieceByCoords(
								move.getStartRow(), move.getStartCol() - i) != null) {
							piecesBetweenRookAndKing = true;
							if (move.getStartCol() - i < 0)
								System.out.print("Out of bounds");
						}
					}

					int row = move.getPiece().getRow();
					int col = move.getPiece().getCol() - 1;
					isCastlingThroughCheck = isKingVulnerableOnThisSquare(
							boardController, color, move, row, col);

					col = col - 1;
					isCastlingIntoCheck = isKingVulnerableOnThisSquare(
							boardController, color, move, row, col);

				}
			}

			result = !kingHasMoved && !rookHasMoved && !isInCheck
					&& !isCastlingThroughCheck && !isCastlingIntoCheck
					&& !piecesBetweenRookAndKing && !isRookDeadOrMissing;
		}

		return result;
	}

	/**
	 * Helper method for king castling logic. Simulates the king moving to that
	 * square so that the square can be checked for any piece attacking it (the
	 * king has to be on the square in question, since a pawn wouldn't be able
	 * to move diagonally and attack an empty square). Returns true if king is
	 * vulnerable on the square designated by parameters row and col.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public static boolean isKingVulnerableOnThisSquare(
			BoardController boardController, String color, Move move,
			int newRow, int newCol) {
		Piece piece = move.getPiece();
		Piece prevPiece = null;

		boolean isVulerable = false;

		// Simulate the move so pawns know if they can capture diagonally
		prevPiece = boardController.getPieceByCoords(newRow, newCol);
		boardController.setPieceByCoords(newRow, newCol, piece);
		piece.setCol(newCol);

		isVulerable = isAttackedSquare(newRow, newCol, color);

		if (prevPiece != null)
			boardController.setPieceByCoords(newRow, newCol, prevPiece);
		else
			boardController.clearSquare(newRow, newCol);

		// Put piece back
		boardController.setPieceByCoords(move.getStartRow(),
				move.getStartCol(), piece);
		piece.setCol(move.getStartCol());

		return isVulerable;
	}

	/**
	 * Checks to see if any piece of team color can attack the square designated
	 * by parameters row and col.
	 * 
	 * @return
	 */
	public static boolean isAttackedSquare(int row, int col, String color) {
		boolean result = false;
		PieceArray pieces;
		if (color.equals("white"))
			pieces = controller.getModel().getWhitePieces();
		else
			pieces = controller.getModel().getBlackPieces();
		for (int i = 0; i < PieceArray.numPieces; i++) {
			Piece piece = pieces.getPiece(i);
			if (piece != null){
			
			boolean seemsValid = false;
			int unsignedRowDifference = Math.abs(row - piece.getRow());
			int unsignedColDifference = Math.abs(col - piece.getCol());

			// If it's not the move style of the piece to attack the king's square,
			// don't bother with the elaborate, expensive validateCheck method
			if ((piece.getType() == Constants.getRookChar() && (piece.getRow() == row || piece
					.getCol() == col)))
				seemsValid = true;
			 if ((piece.getType() == Constants.getPawnChar()
					&& (unsignedColDifference == 1) && (unsignedRowDifference == 1)))
				seemsValid = true;
			else if ((piece.getType() == Constants.getBishopChar() && unsignedColDifference == unsignedRowDifference))
				seemsValid = true;
			else if ((piece.getType() == Constants.getKnightChar()
					&& (unsignedColDifference == 1 && unsignedRowDifference == 2) || (unsignedColDifference == 2 && unsignedRowDifference == 1)))
				seemsValid = true;
			else if ((piece.getType() == Constants.getQueenChar() && (unsignedRowDifference == unsignedColDifference)||(row == piece.getRow() || col == piece.getCol())))
				seemsValid = true;
			else if (piece.getType() == Constants.getKingChar() && unsignedRowDifference < 2 && unsignedColDifference < 2)
				seemsValid = true;
			if (seemsValid)
			{
			if (validateCheck(
						new Move(piece, piece.getRow(), piece.getCol(), row,
								col), controller.getBoardController(), false))
					result = true;
			}}
		}

		return result;
	}

	/**
	 * Returns true if Piece piece can check a king on the square designated by
	 * row and col. Different from validateMove because it doesn't matter if the
	 * move would put the checking player into check. Avoids infinite mutual
	 * recursion
	 * 
	 * @return
	 */
	public static boolean validateCheck(Move move,
			BoardController boardController, boolean printFlag) {
		RuleEngine.printFlag = printFlag;

		boolean result = true;

		if (!isNotSameSquare(move)) {
			result = false;
			if (printFlag)
				System.out
						.println("RuleEngine.validateCheck: Invalid move: You cannot move a piece to the same square it started on.");
		}

		// Commented out below 8.9.2014 - This method is called to see if a
		// piece can check the enemy king
		// so there shouldn't ever be a time when the the move is attacking the
		// color piece.
		// if (!isNotSelfCapture(move, boardController)) {
		// result = false;
		// if (printFlag)
		// System.out
		// .println("RuleEngine.validateCheck: Invalid move: You cannot capture your own piece.");
		// }
		if (result) {
			if (!isLegalMoveStyle(move, boardController)) {
				result = false;
				if (printFlag)
					System.out
							.println("RuleEngine.validateCheck: Invalid move: You cannot move a "
									+ move.getPiece() + " like that.");
			}
		}
		if (result) {
			if (!isUnblocked(move, boardController)) {
				result = false;
				if (printFlag)
					System.out
							.println("RuleEngine.validateCheck: Invalid move: The path is blocked.");
			}
		}
		return result;
	}

	/**
	 * Delegates to the appropriate handler to verify the that path of the piece
	 * in the move parameter is not blocked. Returns true if path is
	 * unobstructed, false otherwise.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isUnblocked(Move move, BoardController boardController) {
		boolean result = true;

		if (move.getPiece().getType() == Constants.getRookChar())
			result = isUnblockedRookPath(move, boardController);
		else if (move.getPiece().getType() == Constants.getQueenChar())
			result = isUnblockedQueenPath(move, boardController);
		else if (move.getPiece().getType() == Constants.getKnightChar())
			result = isUnblockedKnightPath(move, boardController);
		else if (move.getPiece().getType() == Constants.getPawnChar())
			result = isUnblockedPawnPath(move, boardController);
		else if (move.getPiece().getType() == Constants.getKingChar())
			result = isUnblockedKingPath(move, boardController);
		else if (move.getPiece().getType() == Constants.getBishopChar())
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

	/**
	 * Returns true if there are no pieces on the diagonal from the start square
	 * in argument move to the end square in the argument move.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isUnblockedBishopPath(Move move,
			BoardController boardController) {
		boolean result = true;

		int rowDirection = 0;
		int colDirection = 0;

		colDirection = calculateColDirection(move);
		rowDirection = calculateRowDirection(move);

		int distance = calculateDeltaRowUnsigned(move);

		for (int i = 1; i < distance; i++) {
			int newRow = move.getStartRow() + i * rowDirection;
			int newCol = move.getStartCol() + i * colDirection;
			if (boardController.getPieceByCoords(newRow, newCol) != null)
				result = false;
		}
		return result;
	}

	/**
	 * Returns true if there are no pieces on squares between the start and end
	 * squares of the move parameter, assuming movement along a column or row.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isUnblockedRookPath(Move move,
			BoardController boardController) {
		boolean result = true;
		int distance = 0;
		int direction = 0;

		// If moving across columns
		if (move.getStartCol() != move.getEndCol()) {
			distance = Math.abs(move.getStartCol() - move.getEndCol());

			direction = calculateColDirection(move);

			for (int i = 1; i < distance; i++) {

				int newCol = move.getStartCol() + (direction * i);

				if (boardController
						.getPieceByCoords(move.getStartRow(), newCol) != null) {
					result = false;
				}
			}
		}

		// If moving across rows
		else {
			distance = Math.abs(move.getStartRow() - move.getEndRow());

			direction = calculateRowDirection(move);

			for (int i = 1; i < distance; i++) {

				int newRow = move.getStartRow() + (direction * i);

				if (boardController
						.getPieceByCoords(newRow, move.getStartCol()) != null) {
					result = false;
				}
			}

		}

		return result;
	}

	/**
	 * Returns true if the initial movement of a pawn is two squares and is
	 * blocked. Returns false otherwise
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isUnblockedPawnPath(Move move,
			BoardController boardController) {
		boolean result = true;

		int distance = move.getEndRow() - move.getStartRow();

		// White pawn
		if (distance == 2 && move.getPiece().isWhite()) {
			if (boardController.getPieceByCoords(move.getStartRow() + 1,
					move.getStartCol()) != null)
				result = false;
		}
		// Black pawn
		else if (distance == -2 && !move.getPiece().isWhite()) {
			if (boardController.getPieceByCoords(move.getStartRow() - 1,
					move.getStartCol()) != null)

				result = false;
		}
		return result;
	}

	/**
	 * Returns true if there is an unobstructed path from the queen to her
	 * destination square, false otherwise.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isUnblockedQueenPath(Move move,
			BoardController boardController) {
		boolean result = false;

		// If queen is moving like a rook
		if (move.getStartCol() == move.getEndCol()
				|| move.getStartRow() == move.getEndRow())
			result = isUnblockedRookPath(move, boardController);

		// If queen is moving like a bishop
		else
			result = isUnblockedBishopPath(move, boardController);

		return result;
	}

	/**
	 * Returns true. The one square movement of a king cannot be blocked, and
	 * the error checking for castling handles blocking of that type of
	 * movement.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isUnblockedKingPath(Move move,
			BoardController boardController) {
		return true;
	}

	/**
	 * Simulates the move in question, and checks to see if the moving player
	 * would be in check afterwards. If so, false is returned, true otherwise.
	 * The board is reverted to it's position before the simulation afterwards.
	 * 
	 * @param move
	 * @param boardController
	 * @return
	 */
	public static boolean isNotSelfCheck(Move move,
			BoardController boardController) {
		boolean result = true;
		String color = move.getPiece().isWhite() ? "white" : "black";
		String opponentColor = color.equals("white") ? "black" : "white";
		Piece king = null;
		int kingRow = 0;
		int kingCol = 0;

		Piece capturedPiece = processMove(move);

		PieceArray pieces = null;
		if (color.equals("white"))
			pieces = controller.getModel().getWhitePieces();
		else
			pieces = controller.getModel().getBlackPieces();
		// try{
		king = pieces.getKing();
				
		// }
		// catch (NullPointerException nullPointerException){
		// controller.log.error(nullPointerException.toString());
		// }
		if (king == null) {
			System.out
					.println("RuleEngine.isNotSelfCheck: King not in piece list");
			for (Move m : controller.getModel().getMoveList())
				System.out.println(m.coloredAlgebraicNotationPrint());

			if (capturedPiece != null)
				System.out.println("Captured piece: "
						+ capturedPiece.toString());
		}
		// If it is the king that's moving, it is his new position which will
		// need to be checked if it is being attacked. Otherwise, use his
		// current location
		if (move.getPiece().getType() == Constants.getKingChar()) {
			kingRow = move.getEndRow();
			kingCol = move.getEndCol();
		} else {
			kingRow = king.getRow();
			kingCol = king.getCol();
		}

		result = !isAttackedSquare(kingRow, kingCol, opponentColor);

		// Revert the board to it's previous state
		undoChanges(capturedPiece, move);

		// If the king wasnt located in the above logic, all bets are off.
		if (king == null) {
			System.out
					.println("RuleEngine.isNotSelfCheck: King was not found on the board, something has gone wrong. King got captured? Returning false.");
			System.out.println("Move: " + move.algebraicNotationPrint());
			result = false;
		}

		return result;
	}

	/**
	 * If parameter capturedPiece is a real piece, it is returned to the board
	 * and the piece list in the model. The piece that was moved via parameter
	 * move is returned to it's initial square also.
	 * 
	 * @param capturedPiece
	 * @param move
	 */
	public static void undoChanges(Piece capturedPiece, Move move) {
		if (capturedPiece != null) {
			PieceArray pieces;
			if (capturedPiece.isWhite())
				pieces = controller.getModel().getWhitePieces();
			else
				pieces = controller.getModel().getBlackPieces();

			pieces.add(capturedPiece);

//			if (capturedPiece.getType() == Constants.getPawnChar())
//				log.info("Adding back piece: " + capturedPiece.toString());
			controller.getModel().getCapturedPieces().remove(capturedPiece);

			controller.getBoardController().setPieceByCoords(move.getEndRow(),
					move.getEndCol(), capturedPiece);

		}

		// Remove move from move list
		ArrayList<Move> moveList = controller.getModel().getMoveList();
		moveList.remove(move);

		// Clear the end spot of the tested move
		controller.getBoardController().clearSquare(move.getEndRow(),
				move.getEndCol());

		// Set the captured piece back where it went
		if (capturedPiece != null)
			controller.getBoardController().setPieceByCoords(
					capturedPiece.getRow(), capturedPiece.getCol(),
					capturedPiece);

		undoCastleMove(move);

		// Set the tested move piece back where it was
		controller.getBoardController().setPieceByCoords(move.getStartRow(),
				move.getStartCol(), move.getPiece());

		move.getPiece().setRow(move.getStartRow());
		move.getPiece().setCol(move.getStartCol());

		undoPawnPromote(move);
	}

	/**
	 * Sets a piece's pieceType back to pawn if it had been promoted
	 * 
	 * @param move
	 */
	public static void undoPawnPromote(Move move) {

		// TODO: test this to see if it works
		if (move.getPromotePiece() != Character.UNASSIGNED) {
			move.getPiece().setType(Constants.getPawnChar());
		}
	}

	/**
	 * Returns a rook and king to their original columns if castling had taken
	 * place.
	 * 
	 * @param move
	 */
	private static void undoCastleMove(Move move) {
		// TODO Auto-generated method stub
		if (move.getPiece().getType() == Constants.getKingChar()
				&& RuleEngine.calculateDeltaColUnsigned(move) == 2) {
			if (RuleEngine.calculateDeltaColSigned(move) == 2) {
				Piece rook = controller.boardController.getPieceByCoords(
						move.getStartRow(),
						Constants.getKingsideCastleRookCol());
				controller.boardController.setPieceByCoords(move.getStartRow(),
						Constants.getKingRookCol(), rook);
				rook.setCol(Constants.getKingRookCol());
				controller.boardController.clearSquare(move.getStartRow(),
						Constants.getKingsideCastleRookCol());
			} else {
				Piece rook = controller.boardController.getPieceByCoords(
						move.getStartRow(),
						Constants.getQueensideCastleRookCol());
				controller.boardController.setPieceByCoords(move.getStartRow(),
						Constants.getQueenRookCol(), rook);
				rook.setCol(Constants.getQueenRookCol());
				controller.boardController.clearSquare(move.getStartRow(),
						Constants.getQueensideCastleRookCol());
			}

		}
	}

	/**
	 * Handles the movement of parameter move on the real board, and returns a
	 * reference to any piece that got captured
	 * 
	 * @param move
	 * @return
	 */
	public static Piece processMove(Move move) {
		Piece capturedPiece = controller.getBoardController().getPieceByCoords(
				move.getEndRow(), move.getEndCol());

		if (controller.getBoardController().getPieceByCoords(move.getStartRow(), move.getStartCol())== null)
			System.out.println("RuleEngine.process move: Moving null piece ERROR");
		
		// Add move to move list
		ArrayList<Move> moveList = controller.getModel().getMoveList();
		moveList.add(move);

		// If the move was a successful capture, remove the captured piece from
		// the list
		if (capturedPiece != null) {
			controller.removePieceFromList(move);
//			if (capturedPiece.getType() == Constants.getPawnChar())
//				log.info("Removing piece: " + capturedPiece.toString());
		}

		// Handle En Passant. If a non-null value is returned, then there was a
		// en passant capture and we save a reference to the captured
		// pawn
		Piece tmpPiece = controller.handleEnPassantCaptures(move);
		controller.handleCastling(move);
		if (tmpPiece != null)
			capturedPiece = tmpPiece;

		// Clear move the piece to the new square, clear the old square
		controller.getBoardController().setPieceByCoords(move.getEndRow(),
				move.getEndCol(), move.getPiece());
		controller.getBoardController().clearSquare(move.getStartRow(),
				move.getStartCol());

		move.getPiece().setRow(move.getEndRow());
		move.getPiece().setCol(move.getEndCol());

		handlePawnPromotes(move);

		return capturedPiece;
	}

	/**
	 * Changes a piece's type if the move in question is a pawn promote.
	 * 
	 * @param move
	 */
	public static void handlePawnPromotes(Move move) {

		// TODO does this work?
		if (move.getPromotePiece() != Character.UNASSIGNED) {
			move.getPiece().setType(move.getPromotePiece());
		}

	}

	public static int calculateDeltaRowUnsigned(Move move) {
		return Math.abs(move.getStartRow() - move.getEndRow());
	}

	public static int calculateDeltaColUnsigned(Move move) {
		return Math.abs(move.getStartCol() - move.getEndCol());
	}

	public static int calculateDeltaColSigned(Move move) {
		return move.getEndCol() - move.getStartCol();
	}

	public static int calculateDeltaRowSigned(Move move) {
		return move.getEndRow() - move.getStartRow();
	}

	public static int calculateColDirection(Move move) {
		int colDirection;

		if (move.getStartCol() > move.getEndCol())
			colDirection = -1;
		else
			colDirection = 1;
		return colDirection;
	}

	public static int calculateRowDirection(Move move) {
		int rowDirection;

		if (move.getStartRow() > move.getEndRow())
			rowDirection = -1;
		else
			rowDirection = 1;
		return rowDirection;
	}

}
