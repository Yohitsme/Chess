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
	private final static int earlyQueenPenaltyWeight = 10;
	
	private final static int kingsideCastleRookCol = 5;
	private final static int queensideCastleRookCol = 3;
	private final static int kingsideCastleKingCol = 6;
	private final static int queensideCastleKingCol = 2;
	
	private final static int queenRookCol = 0;
	private final static int queenKnightCol = 1;
	private final static int queenBishopCol = 2;
	private final static int queenCol = 3;
	private final static int kingCol = 4;
	private final static int kingBishopCol = 5;
	private final static int kingKnightCol = 6;
	private final static int kingRookCol = 7;
	
	
	private final static int nullMoveReduction = 2;
	private final static int checkMateScore = 10000000;
	private final static int drawScore = 0;
	private final static double killerMoveScore = -0.1;
	
	private  static double positionalScoreWeight = .09;
	private  static double materialScoreWeight = .9;
	private  static double bonusScoreWeight = .01;
	
	private static int depth = 3;
	
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

	public static int getNullMoveReduction (){
		return nullMoveReduction;
	}

	public static int getEarlyQueenPenaltyWeight(){
		return earlyQueenPenaltyWeight;
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

	public static int getCastlingbonusweight() {
		return castlingBonusWeight;
	}


	public static int getConnectedrooksbonusweight() {
		return connectedRooksBonusWeight;
	}


	public static int getBishoppairbonusweight() {
		return bishopPairBonusWeight;
	}


	public static int getCentralpawnspushedbonusweight() {
		return centralPawnsPushedBonusWeight;
	}


	public static int getKingsideCastleRookCol() {
		return kingsideCastleRookCol;
	}


	public static int getQueensideCastleRookCol() {
		return queensideCastleRookCol;
	}


	public static int getKingsideCastleKingCol() {
		return kingsideCastleKingCol;
	}


	public static int getQueensideCastleKingCol() {
		return queensideCastleKingCol;
	}


	public static int getQueenRookCol() {
		return queenRookCol;
	}


	public static int getQueenKnightCol() {
		return queenKnightCol;
	}


	public static int getQueenBishopCol() {
		return queenBishopCol;
	}


	public static int getQueenCol() {
		return queenCol;
	}


	public static int getKingCol() {
		return kingCol;
	}


	public static int getKingBishopCol() {
		return kingBishopCol;
	}


	public static int getKingKnightCol() {
		return kingKnightCol;
	}


	public static int getKingRookCol() {
		return kingRookCol;
	}



	public static double getMaterialscoreweight() {
		return materialScoreWeight;
	}


	public static double getBonusscoreweight() {
		return bonusScoreWeight;
	}
	
	public static void setBonusScoreWeight(double newBonusWeight){
		bonusScoreWeight = newBonusWeight;
	}

	public static void setMaterialScoreWeight(double newMaterialWeight){
		materialScoreWeight = newMaterialWeight;
	}

	public static void setPositionalScoreWeight(double newPositionalScoreWeight){
		positionalScoreWeight = newPositionalScoreWeight;
	}
	
	public static int getWhitePawnRow() {
		return whitePawnRow;
	}


	public static int getQueenColumn() {
		return queenColumn;
	}


	public static double getKillerMoveScore() {
		return killerMoveScore;
	}


	public static int getKingColumn() {
		return kingColumn;
	}


	public static void setDepth(int i) {
		depth = i;
		
	}


	public static double getCheckMateScore() {
		// TODO Auto-generated method stub
		return checkMateScore;
	}


	public static double getDrawScore() {
		// TODO Auto-generated method stub
		return drawScore;
	}


}
