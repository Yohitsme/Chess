package model;

import utils.Utils;

/**
 * The Move class holds the int values of the starting and ending position of a
 * piece. The piece is also stored.
 * 
 * @author Matthew
 * 
 */
public class Move {
	int startRow;
	int startCol;
	int endRow;
	int endCol;
	Piece piece;
	int score = 0;
	char promotePiece;

	/**
	 * Constructor
	 * 
	 * @param pieceIn
	 * @param startRowIn
	 * @param startColIn
	 * @param endRowIn
	 * @param endColIn
	 */
	public Move(Piece pieceIn, int startRowIn, int startColIn, int endRowIn,
			int endColIn) {
		this.piece = pieceIn;
		this.startRow = startRowIn;
		this.startCol = startColIn;
		this.endRow = endRowIn;
		this.endCol = endColIn;
		
	}

	public Move(Move move) {
		this.piece = move.getPiece();
		this.startRow = move.getStartRow();
		this.startCol = move.getStartCol();
		this.endRow = move.getEndRow();
		this.endCol = move.getEndCol();
		this.promotePiece = move.getPromotePiece();
	}

	/**
	 * Returns the human readable version of a move.
	 * @return
	 */
	public String algebraicNotationPrint() {

		char startCol = Utils.getAlgebraicCharacterFromCol(this.startCol);
		char endCol = Utils.getAlgebraicCharacterFromCol(this.endCol);
		char pieceAbbreviation = this.piece.getType();

		String result = "" + pieceAbbreviation + startCol + (this.startRow + 1)
				+ " " + pieceAbbreviation + endCol + (this.endRow + 1);

		return result;
	}
	
	/**
	 * Returns true if two moves have the same origin, destination and piece.
	 * @param moveIn
	 * @return
	 */
	public boolean equals(Move moveIn){
		boolean result = true;
		
		if (this.startCol != moveIn.getStartCol())
			result = false;
		if (this.endCol != moveIn.getEndCol())
			result = false;
		if (this.startRow != moveIn.getStartRow())
			result = false;
		if (this.endRow != moveIn.getEndRow())
			result = false;
		if (this.piece.type != moveIn.getPiece().getType())
			result = false;
		
		return result;
		
	}
	
	@Override
	public String toString() {
		return "Move [startRow=" + startRow + ", startCol=" + startCol
				+ ", endRow=" + endRow + ", endCol=" + endCol + ", piece="
				+ piece + "]";
	}

	/**
	 * Prints the algebraic notation with a prefix for the color
	 * @return
	 */
	public String coloredAlgebraicNotationPrint() {

		char color = piece.isWhite() ? 'w' : 'b';
		String result = "[" + color + "]" + algebraicNotationPrint();
		return result;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public char getPromotePiece() {
		return this.promotePiece;
	}

	public void setPromotePiece(char promotePiece) {
		this.promotePiece= promotePiece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getStartCol() {
		return startCol;
	}

	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getEndCol() {
		return endCol;
	}

	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}
}
