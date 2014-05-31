package controller;

import java.util.ArrayList;
import java.util.Collection;

import model.Move;
import model.Piece;

/**
 * This class is the efficient way for finding legal moves. It searches only
 * legal move styles for a given piece, and stops when the path along a file or
 * diagonal is blocked. It relies on the RuleEngine class for selfCheck checking
 * as well as king castling, but all other validation is done internally.
 * 
 * @author Matthew
 * 
 */
public class MoveGenerator {

	BoardController boardController;
	RuleEngine ruleEngine;
	Controller controller;
	
	/**
	 * Constructor
	 * 
	 * @param boardControllerIn
	 * @param ruleEngineIn
	 */
	public MoveGenerator(BoardController boardControllerIn,
			RuleEngine ruleEngineIn,Controller controllerIn) {
		this.boardController = boardControllerIn;
		this.ruleEngine = ruleEngineIn;
		this.controller = controllerIn;
	}

	/**
	 * Given a square designated by coordinates (row,col), returns an arrayList
	 * of legal moves for the piece on that square.
	 */
	public ArrayList<Move> findMoves(int row, int col) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		Piece piece = boardController.getPieceByCoords(row, col);

		if (piece != null) {
			if (piece.getType().equals("rook"))
				legalMoves.addAll(findRookMoves(row, col));
			else if (piece.getType().equals("bishop"))
				legalMoves.addAll(findBishopMoves(row, col));
			else if (piece.getType().equals("queen")) {
				legalMoves.addAll(findBishopMoves(row, col));
				legalMoves.addAll(findRookMoves(row, col));
			} else if (piece.getType().equals("knight"))
				legalMoves.addAll(findKnightMoves(row, col));
			else if (piece.getType().equals("king"))
				legalMoves.addAll(findKingMoves(row, col));
			else if (piece.getType().equals("pawn"))
				legalMoves.addAll(findPawnMoves(row, col));

		}

		return legalMoves;
	}

	/**
	 * Finds all moves for the color of the given parameter <code>color</code>
	 * 
	 * @return
	 */
	public ArrayList<Move> findMoves(boolean isWhite) {
		Piece piece = null;
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				piece = boardController.getPieceByCoords(row, col);
				if (piece != null) {
					if (isWhite == piece.isWhite())

						legalMoves.addAll(findMoves(row, col));

				}
			}
		}
		return legalMoves;
	}

	/**
	 * Returns true i
	 */
	public boolean isStalemated(boolean isWhite) {
		Piece piece = null;
		int row = 0;
		int col = 0;
		int numMoves = 0;
		boolean result = false;
		Piece king = null;
		
		while (row < 8 && numMoves == 0) {
			col = 0;
			while (col < 8 && numMoves == 0) {
				piece = boardController.getPieceByCoords(row, col);
				if (piece != null) {
					if ((isWhite == piece.isWhite())){
						numMoves += findMoves(row, col).size();
					}
				}
			col++;
			}
			row++;
		}
		
		
		
		
		if (numMoves == 0){
			king = controller.getAI().findKing(isWhite);
			String color = isWhite?"white":"black";
			
			// If the king's square is attacked, it's checkmate, not stalemate.
			if (!RuleEngine.isAttackedSquare(king.getRow(),king.getCol(),color));
			result = true;
		}
		
		
		
		if (result)
			System.out.println("MoveGenerator.isStaleMated: StalemateDetected");
		return result;
	}

	/**
	 * Checks one square in each direction around the king and calls
	 * RuleEngine's logic for validating castling moves.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public ArrayList<Move> findKingMoves(int row, int col) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		Piece piece = boardController.getPieceByCoords(row, col);
		Move move = null;
		int newRow = 0;
		int newCol = 0;

		// Check normal one-square moves
		for (int deltaRow = -1; deltaRow < 2; deltaRow++) {
			for (int deltaCol = -1; deltaCol < 2; deltaCol++) {

				// You can't move to the same square you are on
				if (!(deltaRow == 0 && deltaCol == 0)) {
					newRow = row + deltaRow;
					newCol = col + deltaCol;

					// If the new square is still on the board
					if ((newRow < 8 && newRow >= 0)
							&& (newCol < 8 && newCol >= 0)) {

						// If our own piece is on the destination square, don't
						// validate it
						if (!isEmptySquare(newRow, newCol)
								&& !isEnemyPiece(piece, newRow, newCol))
							move = null;

						// Otherwise it's empty or a capture, so validate
						else
							move = new Move(piece, row, col, newRow, newCol);

						// Add it if it is valid
						if (move != null
								&& RuleEngine.isNotSelfCheck(move,
										boardController))
							legalMoves.add(move);
					}
				}
			}
		}

		// Check kingside castling
		move = new Move(piece, piece.getRow(), piece.getCol(), piece.getRow(),
				piece.getCol() + 2);
		if (RuleEngine.isLegalKingMove(move))
			legalMoves.add(move);

		// Check queenside castling
		move = new Move(piece, piece.getRow(), piece.getCol(), piece.getRow(),
				piece.getCol() - 2);
		if (RuleEngine.isLegalKingMove(move))
			legalMoves.add(move);

		return legalMoves;
	}

	/**
	 * Moves along all 4 diagonals until the path becomes blocks, and returns an
	 * arraylist of all legal moves to unoccupied squares or square occupied by
	 * the enemy.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public ArrayList<Move> findBishopMoves(int row, int col) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		// Call the findBishopMovesAlongDiagonal method with each of the 4
		// directions
		legalMoves.addAll(findBishopMovesAlongDiagonal(row, col, 1, 1));
		legalMoves.addAll(findBishopMovesAlongDiagonal(row, col, 1, -1));
		legalMoves.addAll(findBishopMovesAlongDiagonal(row, col, -1, 1));
		legalMoves.addAll(findBishopMovesAlongDiagonal(row, col, -1, -1));

		return legalMoves;
	}

	/**
	 * Checks all 8 squares a knight can move to and returns a list of the ones
	 * that it can go to.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public ArrayList<Move> findKnightMoves(int row, int col) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		// Check all moves that are two rows over and one column over
		legalMoves.addAll(findKnightMoves_2Row1Col(row, col, 1, 1));
		legalMoves.addAll(findKnightMoves_2Row1Col(row, col, -1, 1));
		legalMoves.addAll(findKnightMoves_2Row1Col(row, col, 1, -1));
		legalMoves.addAll(findKnightMoves_2Row1Col(row, col, -1, -1));

		// Check all moves that are one row over and two columns over
		legalMoves.addAll(findKnightMoves_1Row2Col(row, col, 1, 1));
		legalMoves.addAll(findKnightMoves_1Row2Col(row, col, -1, 1));
		legalMoves.addAll(findKnightMoves_1Row2Col(row, col, 1, -1));
		legalMoves.addAll(findKnightMoves_1Row2Col(row, col, -1, -1));

		return legalMoves;
	}

	/**
	 * Returns knight moves that are two rows over and one column over in the
	 * direction designated by rowDirection and colDirection.
	 * 
	 * @param row
	 * @param col
	 * @param rowDirection
	 * @param colDirection
	 * @return
	 */
	public ArrayList<Move> findKnightMoves_2Row1Col(int row, int col,
			int rowDirection, int colDirection) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		Move move = null;

		Piece piece = boardController.getPieceByCoords(row, col);

		int newRow = row + (2 * rowDirection);
		int newCol = col + (1 * colDirection);
		if ((newRow < 8 && newRow >= 0) && (newCol < 8 && newCol >= 0)) {
			if (isEmptySquare(newRow, newCol))
				move = new Move(piece, row, col, newRow, newCol);
			else if (isEnemyPiece(piece, newRow, newCol))
				move = new Move(piece, row, col, newRow, newCol);

			if (move != null
					&& RuleEngine.isNotSelfCheck(move, boardController))
				legalMoves.add(move);
		}
		return legalMoves;

	}

	/**
	 * Returns knight moves that are one row over and two columns over in the
	 * direction designated by rowDirection and colDirection.
	 * 
	 * @param row
	 * @param col
	 * @param rowDirection
	 * @param colDirection
	 * @return
	 */
	public ArrayList<Move> findKnightMoves_1Row2Col(int row, int col,
			int rowDirection, int colDirection) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		Move move = null;

		Piece piece = boardController.getPieceByCoords(row, col);

		int newRow = row + (1 * rowDirection);
		int newCol = col + (2 * colDirection);

		if ((newRow < 8 && newRow >= 0) && (newCol < 8 && newCol >= 0)) {
			if (isEmptySquare(newRow, newCol))
				move = new Move(piece, row, col, newRow, newCol);
			else if (isEnemyPiece(piece, newRow, newCol))
				move = new Move(piece, row, col, newRow, newCol);

			if (move != null
					&& RuleEngine.isNotSelfCheck(move, boardController))
				legalMoves.add(move);

		}

		return legalMoves;

	}

	/**
	 * Moves along the diagonal designated by rowDirection and colDirection and
	 * checks the validity of moving to each square until the path is blocked.
	 * 
	 * @param row
	 * @param col
	 * @param rowDirection
	 * @param colDirection
	 * @return
	 */
	public ArrayList<Move> findBishopMovesAlongDiagonal(int row, int col,
			int rowDirection, int colDirection) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		Piece piece = boardController.getPieceByCoords(row, col);
		boolean isBlocked = false;
		Move move;
		int i = 1;
		int newRow = row + (rowDirection * i);
		int newCol = col + (colDirection * i);

		while (!isBlocked && (newRow < 8 && newRow >= 0)
				&& (newCol < 8 && newCol >= 0)) {
			move = null;
			if (isEmptySquare(newRow, newCol)) {
				move = new Move(piece, row, col, newRow, newCol);
			} else if (isEnemyPiece(piece, newRow, newCol)) {
				move = new Move(piece, row, col, newRow, newCol);
				isBlocked = true;
			}
			// If the square is not empty and it does not hold an enemy piece,
			// it must hold our piece. Our own piece will block us from moving
			// any farther in this direction
			else
				isBlocked = true;

			if (move != null
					&& RuleEngine.isNotSelfCheck(move, boardController)) {
				legalMoves.add(move);
			}
			i++;
			newRow = row + i * rowDirection;
			newCol = col + i * colDirection;
		}

		return legalMoves;
	}

	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public ArrayList<Move> findPawnMoves(int row, int col) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		Piece piece = boardController.getPieceByCoords(row, col);
		Move move;
		int rowDirection;
		if (piece.isWhite())
			rowDirection = 1;
		else
			rowDirection = -1;

		// Moving one step forward
		move = new Move(piece, row, col, row + 1 * rowDirection, col);
		if (RuleEngine.isLegalPawnMove(move, boardController)
				&& RuleEngine.isNotSelfCheck(move, boardController))
			legalMoves.add(move);

		// Moving two steps forward
		if (boardController.getPieceByCoords(row + 1 * rowDirection, col) == null) {
			move = new Move(piece, row, col, row + 2 * rowDirection, col);
			if (RuleEngine.isLegalPawnMove(move, boardController)
					&& RuleEngine.isNotSelfCheck(move, boardController))
				legalMoves.add(move);
		}

		// Capturing to the right
		move = new Move(piece, row, col, row + 1 * rowDirection, col + 1);
		if (RuleEngine.isLegalPawnMove(move, boardController)
				&& RuleEngine.isNotSelfCheck(move, boardController)
				&& isEnemyPieceOrEmpty(piece, row + 1 * rowDirection, col + 1))
			legalMoves.add(move);

		// Capturing to the left
		move = new Move(piece, row, col, row + 1 * rowDirection, col - 1);
		if (RuleEngine.isLegalPawnMove(move, boardController)
				&& RuleEngine.isNotSelfCheck(move, boardController)
				&& isEnemyPieceOrEmpty(piece, row + 1 * rowDirection, col - 1))
			legalMoves.add(move);
		return legalMoves;
	}

	/**
	 * All rule checking is enforced by the way that potential moves are
	 * explored. I.e. no need to call isValidMoveStyle because this method will
	 * only look at squares in the same row or column as the start square.
	 * isNotSelfCheck is called within to ensure total move legality.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public ArrayList<Move> findRookMoves(int row, int col) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		int i = 1;
		int direction = 1;

		// Check up direction
		legalMoves.addAll(findRookMovesAlongCol(row, col, direction));

		// Check down direction
		legalMoves.addAll(findRookMovesAlongCol(row, col, direction * -1));

		// Check right direction
		legalMoves.addAll(findRookMovesAlongRow(row, col, direction));

		// Check left direction
		legalMoves.addAll(findRookMovesAlongRow(row, col, direction * -1));

		return legalMoves;
	}

	public ArrayList<Move> findRookMovesAlongCol(int row, int col, int direction) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		Piece piece = boardController.getPieceByCoords(row, col);
		boolean isBlocked = false;
		Move move;
		int i = 1;
		int newRow = row + (direction * i);

		while (!isBlocked && (newRow < 8 && newRow >= 0)) {
			move = null;
			if (isEmptySquare(newRow, col)) {
				move = new Move(piece, row, col, newRow, col);
			} else if (isEnemyPiece(piece, newRow, col)) {
				move = new Move(piece, row, col, newRow, col);
				isBlocked = true;
			} else
				isBlocked = true;

			if (move != null
					&& RuleEngine.isNotSelfCheck(move, boardController)) {
				legalMoves.add(move);
			}
			i++;
			newRow = row + i * direction;
		}
		return legalMoves;
	}

	public ArrayList<Move> findRookMovesAlongRow(int row, int col, int direction) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		Piece piece = boardController.getPieceByCoords(row, col);
		boolean isBlocked = false;
		Move move;
		int i = 1;
		int newCol = col + (direction * i);

		while (!isBlocked && (newCol < 8 && newCol >= 0)) {
			move = null;
			if (isEmptySquare(row, newCol)) {
				move = new Move(piece, row, col, row, newCol);
			} else if (isEnemyPiece(piece, row, newCol)) {
				move = new Move(piece, row, col, row, newCol);
				isBlocked = true;
			} else
				isBlocked = true;

			if (move != null
					&& RuleEngine.isNotSelfCheck(move, boardController)) {
				legalMoves.add(move);
			}
			i++;
			newCol = col + i * direction;
		}
		return legalMoves;
	}

	/**
	 * Returns true if the square designated by (row,col) is empty
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isEmptySquare(int row, int col) {
		boolean result = false;
		Piece otherPiece = boardController.getPieceByCoords(row, col);

		if (otherPiece == null)
			result = true;
		return result;
	}

	/**
	 * Returns true if the piece on square (row,col) is not the same color as
	 * parameter <code>piece</code>.
	 * 
	 * @param piece
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isEnemyPiece(Piece piece, int row, int col) {
		boolean result = false;
		Piece otherPiece = boardController.getPieceByCoords(row, col);

		if (otherPiece.isWhite() != piece.isWhite())
			result = true;
		return result;
	}

	/**
	 * Returns true if the piece on square (row,col) is not the same color as
	 * parameter <code>piece</code>, or if it is null.
	 * 
	 * @param piece
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isEnemyPieceOrEmpty(Piece piece, int row, int col) {
		boolean result = false;
		Piece otherPiece = boardController.getPieceByCoords(row, col);

		if (otherPiece == null || (otherPiece.isWhite() != piece.isWhite()))
			result = true;
		return result;
	}

	public BoardController getBoardController() {
		return boardController;
	}

	public void setBoardController(BoardController boardController) {
		this.boardController = boardController;
	}

}
