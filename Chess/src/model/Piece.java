/*
Quiet Intrigue is a chess playing engine with GUI written in Java.
Copyright (C) <2014>  Matthew Voss

Quiet Intrigue is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Quiet Intrigue is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Quiet Intrigue.  If not, see <http://www.gnu.org/licenses/>.
*/

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
