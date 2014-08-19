package utils;

import model.Piece;

public class Constants {

	private final static int pawnWeight = 1;
	private final static int kingWeight = 200;
	private final static int knightWeight = 3;
	private final static int rookWeight = 5;
	private final static int queenWeight = 9;
	private final static int bishopWeight = 3;
	
	private final static int blackPieceRow = 7;
	private final static int blackPawnRow = 6;
	private final static int whitePieceRow = 0;
	private final static int whitePawnRow = 1;
	
	private final static int queenColumn = 3;
	private final static int kingColumn = 4;
	
	private final static int castlingBonusWeight = 10;
	private final static int connectedRooksBonusWeight = 5;
	private final static int bishopPairBonusWeight = 3;
	private final static int centralPawnsPushedBonusWeight = 5;
	
	private final static double positionalScoreWeight = .05;
	private final static double materialScoreWeight = .75;
	private final static double bonusScoreWeight = .20;
	
	private final static int depth = 4;
	
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


	public static double getPositionalScoreWeight() {
		return positionalScoreWeight;
	}


	public static double getMaterialScoreWeight() {
		return materialScoreWeight;
	}


	public static double getBonusScoreWeight() {
		return bonusScoreWeight;
	}


	public static int getDepth() {
		return depth;
	}


	public static int getCentralPawnsPushedBonusWeight() {
		return centralPawnsPushedBonusWeight;
	}


	public static int getBlackPieceRow() {
		return blackPieceRow;
	}


	public static int getBlackPawnRow() {
		return blackPawnRow;
	}


	public static int getWhitePieceRow() {
		return whitePieceRow;
	}


	public static int getWhitePawnRow() {
		return whitePawnRow;
	}


	public static int getQueenColumn() {
		return queenColumn;
	}


	public static int getKingColumn() {
		return kingColumn;
	}
}
