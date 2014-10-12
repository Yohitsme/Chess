package model;

import utils.Utils;

/**
 * The Piece class holds information about a chess piece
 * @author Matthew
 *
 */
public class Piece {

	char type;
	boolean isWhite;
	boolean hasMoved;
	int row;
	int col;
	int id;
	
	/**
	 * Constructor
	 * @param typeIn
	 * @param isWhiteIn
	 * @param hasMovedIn
	 */
	public Piece(char typeIn, boolean isWhiteIn, boolean hasMovedIn, int rowIn, int colIn, int idIn){
		this.type = typeIn;
		this.isWhite = isWhiteIn;
		this.hasMoved = hasMovedIn;
		this.row = rowIn;
		this.col = colIn;
		this.id = idIn;
	}
	/**
	 * Copy constructor
	 * @param otherPiece
	 */
	public Piece(Piece otherPiece){
		this.type = otherPiece.getType();
		this.isWhite = otherPiece.isWhite();
		this.hasMoved=otherPiece.isHasMoved();
		this.row=otherPiece.getRow();
		this.col=otherPiece.getCol();
		this.id = otherPiece.getID();
	}

	@Override
	public String toString() {
		String str = ""+Utils.getAlgebraicCharacterFromCol(getCol())+getRow();
		if (isWhite)
			str += " white ";
		else
			str += " black ";
		
		return str += type;
	}

	public int getID(){
		return this.id;
	}
	
	public void setID(int idIn){
		this.id=idIn;
	}
	
	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public boolean isWhite() {
		return isWhite;
	}

	public void setWhite(boolean isWhite) {
		this.isWhite = isWhite;
	}

	public boolean isHasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
}
