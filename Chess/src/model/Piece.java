package model;

/**
 * The Piece class holds information about a chess piece
 * @author Matthew
 *
 */
public class Piece {

	String type;
	boolean isWhite;
	boolean hasMoved;
	
	/**
	 * Constructor
	 * @param typeIn
	 * @param isWhiteIn
	 * @param hasMovedIn
	 */
	public Piece(String typeIn, boolean isWhiteIn, boolean hasMovedIn){
		this.type = typeIn;
		this.isWhite = isWhiteIn;
		this.hasMoved = hasMovedIn;
		
	}

	@Override
	public String toString() {
		return "Piece [type=" + type + ", isWhite=" + isWhite + ", hasMoved="
				+ hasMoved + "]";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
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
	
}
