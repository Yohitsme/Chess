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

	@Override
	public String toString() {
		return "Move [startRow=" + startRow + ", startCol=" + startCol
				+ ", endRow=" + endRow + ", endCol=" + endCol + ", piece="
				+ piece + "]";
	}

	int startRow;
	int startCol;
	int endRow;
	int endCol;
	Piece piece;
	int score = 0;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

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
	}

	public String algebraicNotationPrint() {

		char startCol = Utils.getAlgebraicCharacterFromCol(this.startCol);
		char endCol = Utils.getAlgebraicCharacterFromCol(this.endCol);
		char pieceAbbreviation = Utils
				.getAlgebraicCharacterFromPieceType(this.piece.getType());

		String result = "" + pieceAbbreviation + startCol + (this.startRow + 1)
				+ " " + pieceAbbreviation + endCol + (this.endRow + 1);

		return result;
	}
	
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
		if (!this.piece.type.equals( moveIn.getPiece().getType()))
			result = false;
		
		return result;
		
	}

	public String coloredAlgebraicNotationPrint() {

		char color = piece.isWhite() ? 'w' : 'b';
		String result = "[" + color + "]" + algebraicNotationPrint();
		return result;
	}

	public Piece getPiece() {
		return piece;
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
