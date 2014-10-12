package model;

/**
 * This is a specialized array where each piece has a known location based
 * off of the square it starts on.  This allows for constant time add and
 * deletes because the piece also saves it's id (where it is stored in the
 * array).  Trading space for time here, but the ArrayList.remove(Piece p)
 * implementation was very expensive since it had to loop over all the pieces
 * for all calls to the remove method.
 * @author Matthew
 *
 */
public class PieceArray {

	private Piece[] array;
	public static int numPieces = 16;
	
	public final static int A_rookId = 0;
	public final static int H_rookId = 7;
	public final static int B_knightId = 1;
	public final static int G_knightId = 6;
	public final static int C_bishopId = 2;
	public final static int F_bishopId = 5;
	public final static int D_queenId = 3;
	public final static int E_kingId = 4;
	
	public final static int A_pawnId = 8;
	public final static int B_pawnId = 9;
	public final static int C_pawnId = 10;
	public final static int D_pawnId = 11;
	public final static int E_pawnId = 12;
	public final static int F_pawnId = 13;
	public final static int G_pawnId = 14;
	public final static int H_pawnId = 15;
	
	
	public PieceArray(){
		array = new Piece[numPieces];
	}
	
	public PieceArray(PieceArray otherPieceArray){
		array = new Piece[numPieces];
		for (int i = 0; i < numPieces; i++)
			array[i]=otherPieceArray.getPiece(i);
	}
	
	public void add(Piece piece){
		array[piece.getID()] = piece;
	}
	
	public Piece getPiece(int id){
		return array[id];
	}
	
	public void remove(Piece piece){
		array[piece.getID()] = null;
	}
	
	public Piece getQueen(){
		return array[D_queenId];
	}
	
	public void removeAll(){
		for (int i = 0; i < numPieces;i++)
			array[i]=null;
	}
	
	public Piece getKing(){
		return array[E_kingId];
	}
	
}
