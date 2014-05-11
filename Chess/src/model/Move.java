package model;

/**
 * The Move class holds the int values of the starting and ending position of a piece. The piece is also stored.
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
	
	/**
	 * Constructor
	 * @param pieceIn
	 * @param startRowIn
	 * @param startColIn
	 * @param endRowIn
	 * @param endColIn
	 */
	public Move(Piece pieceIn, int startRowIn, int startColIn, int endRowIn, int endColIn){
		this.piece = pieceIn;
		this.startRow = startRowIn;
		this.startCol = startColIn;
		this.endRow = endRowIn;
		this.endCol = endColIn;
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
