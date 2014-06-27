package utils;

import model.Piece;

public class Constants {

	private final static int pawnWeight = 1;
	private final static int kingWeight = 200;
	private final static int knightWeight = 3;
	private final static int rookWeight = 5;
	private final static int queenWeight = 9;
	private final static int bishopWeight = 3;
	
	private final static int castlingBonusWeight = 10;
	private final static int connectedRooksBonusWeight = 5;
	private final static int bishopPairBonusWeight = 3;
	private final static int centralPawnsPushedBonusWeight = 5;
	private final static int depth = 5;
	
	public static int getPieceWeight(Piece piece){
		int result = 0;
		if (piece.getType().equals("pawn"))
			result = pawnWeight;
		else if (piece.getType().equals("knight"))
			result = knightWeight;
		else if (piece.getType().equals("bishop"))
			result = bishopWeight;
		else if (piece.getType().equals("king"))
			result = kingWeight;
		else if (piece.getType().equals("rook"))
			result = rookWeight;
		else if (piece.getType().equals("queen"))
			result = queenWeight;
		else {
			System.out.println("Constants.getPieceWeight: Piece type '"+piece.getType() + "' not recognized. Returning 0.");
		}
		return result;
	}


	public static int getPawnweight() {
		return pawnWeight;
	}


	public static int getKingweight() {
		return kingWeight;
	}


	public static int getKnightweight() {
		return knightWeight;
	}


	public static int getRookweight() {
		return rookWeight;
	}


	public static int getQueenweight() {
		return queenWeight;
	}


	public static int getBishopweight() {
		return bishopWeight;
	}


	public static int getCastlingBonusWeight() {
		return castlingBonusWeight;
	}


	public static int getConnectedRooksBonusWeight() {
		return connectedRooksBonusWeight;
	}


	public static int getBishopPairBonusWeight() {
		return bishopPairBonusWeight;
	}


	public static int getDepth() {
		return depth;
	}


	public static int getCentralPawnsPushedBonusWeight() {
		return centralPawnsPushedBonusWeight;
	}
}
