package controller;

import java.util.ArrayList;
import java.util.Random;

import model.Move;
import model.Piece;
import utils.Constants;
import utils.Log;

public class AI {

	Controller controller;
	boolean debug = true;
	Log log = new Log();

	public AI(Controller controllerIn) {
		this.controller = controllerIn;
	}

	public Move move(String color) {
		Move move = null;

		boolean isWhite = color.equals("white") ? true : false;

		ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
				isWhite);

		move = chooseMove(legalMoves);

		log.info("AI.move: Move chosen: " + move.algebraicNotationPrint());

		return move;
	}

	public Move chooseMove(ArrayList<Move> legalMoves) {

		double score = 0;
		double highest = -100000000;
		Move bestMove = null;
		Random rand = new Random();
		for (Move move : legalMoves) {
			Piece capturedPiece = RuleEngine.processMove(move);
			ArrayList<Move> moveList = controller.getModel().getMoveList();
			score = -maxi(Constants.getDepth(),
					moveList.get(moveList.size() - 1));
			log.writeLine();
			log.error("AI.chooseMove: score: " + score + ", " + move.algebraicNotationPrint());
			RuleEngine.undoChanges(capturedPiece, move);
			if (score > highest) {
				log.error("AI.chooseMove: New highest: "
						+ move.algebraicNotationPrint());
				bestMove = move;
				highest = score;
			}

			// If the considered move is as good as the best so far, there's a
			// 10% chance the engine will pick it instead
			else if (score == highest) {
				if (rand.nextInt(10) == 0) {
					bestMove = move;
					highest = score;
				}
			}
		}
		return bestMove;
	}

	public double maxi(int depth, Move previousMove) {
//		System.out.println(depth);
		if (depth == 0) {
			//log.debug("AI.maxi: evaluating white? "
		//				+ previousMove.getPiece().isWhite());
			return evaluate(previousMove);
		}
		double max = Integer.MIN_VALUE;
		double score = 0.0;

		boolean isWhite = !previousMove.getPiece().isWhite();
		ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
				isWhite);

		for (Move move : legalMoves) {
			Piece capturedPiece = RuleEngine.processMove(move);
			score = -maxi(depth - 1, move);
			if (score > max)
				max = score;
			RuleEngine.undoChanges(capturedPiece, move);
		}
		return max;
	}

	public Move mini(int depth, ArrayList<Move> legalMoves) {

		return null;

	}

	/**
	 * Calls all evaluation methods on a potential move and returns the score of
	 * the move.
	 * 
	 * @return
	 */
	public double evaluate(Move move) {

		int positionalScore = computePositionalScore(move);
		//int positionalScore = 0;
		int materialScore = computeMaterialScore(move);
		int bonusScore = computeBonusScore(move);

		//log.debug("-Positional Score: " + positionalScore);
		//log.debug("-Material Score: " + materialScore);
		

		double weightedPositionalScore = positionalScore * 0.1;
		double weightedMaterialScore = materialScore * 0.75;
		double weightedBonusScore = bonusScore * 0.15;

		log.info("AI.evaluate: weightdP: "
					+ weightedPositionalScore + " weighgtedM: "
					+ weightedMaterialScore + " weightedB: "
					+ weightedBonusScore);

		double totalScore = weightedPositionalScore + weightedMaterialScore
				+ weightedBonusScore;
		log.info("AI.evaluate: Considering "
					+ move.algebraicNotationPrint() + ", score: " + totalScore);
		
		ArrayList<Move> moveList = controller.getModel().getMoveList();
		for (Move pastMove: moveList)
			log.error(pastMove.algebraicNotationPrint());
	//	log.writeLine();
		
		return weightedPositionalScore + weightedMaterialScore
				+ weightedBonusScore;
	}

	/**
	 * Computes the integer value of the position, taking into account piece
	 * mobility, king safety, mobility of the enemy, passed pawns, doubled
	 * pawns, pawn structure, etc.
	 * 
	 * Very computationally expensive, will need to optimize this in the future.
	 */
	public int computePositionalScore(Move move) {

		// Piece capturedPiece = RuleEngine.processMove(move);

		ArrayList<Move> whiteMoves = controller.getMoveGenerator().findMoves(
				true);

		ArrayList<Move> blackMoves = controller.getMoveGenerator().findMoves(
				false);

		int difference = 0;
		if (move.getPiece().isWhite())
			difference = whiteMoves.size() - blackMoves.size();
		else
			difference = blackMoves.size() - whiteMoves.size();

		// RuleEngine.undoChanges(capturedPiece, move);
		return difference;
	}

	/**
	 * Computes the material score of a move. This is the difference between the
	 * amount of material (in points) for black and white. The sign of the
	 * result is dependent on who moved (material defecit is always negative,
	 * having more material than the opponent is positive).
	 * 
	 * @return
	 */
	public int computeMaterialScore(Move move) {
		int result = 0;
		int whiteScore = 0;
		int blackScore = 0;

		// Piece capturedPiece = RuleEngine.processMove(move);

		ArrayList<Piece> whitePieces = controller.getModel().getWhitePieces();
		ArrayList<Piece> blackPieces = controller.getModel().getBlackPieces();

		for (Piece piece : whitePieces)
			whiteScore += Constants.getPieceWeight(piece);

		for (Piece piece : blackPieces)
			blackScore += Constants.getPieceWeight(piece);

		// RuleEngine.undoChanges(capturedPiece, move);
		// TODO account for passant
		// TODO pawn promotion

		if (move.getPiece().isWhite())
			result = whiteScore - blackScore;
		else
			result = blackScore - whiteScore;

		return result;
	}

	/**
	 * Computes the integer value of "bonuses". These can be for castling,
	 * moving the king and queen pawns early, having both bishops, having
	 * connected rooks, etc.
	 * 
	 * @return
	 */
	public int computeBonusScore(Move move) {

		// Preprocess information that several methods need to find anyway
		int result = 0;
		boolean isWhite = move.getPiece().isWhite();
		Piece king = findKing(isWhite);

		int castlingBonus = computeCastlingBonus(move, king);
		int centralPawnsPushedBonus = computeCentralPawnsPushedBonus(move,
				isWhite);
		int bishopPairBonus = computeBishopPairBonus(move, isWhite);
		int connectedRooksBonus = computeConnectedRooksBonus(move);

		result = castlingBonus + centralPawnsPushedBonus + bishopPairBonus
				+ connectedRooksBonus;

		//log.debug("-bonusScore Score: " + result);
			/*
			 * System.out.println("---castling score: " + castlingBonus);
			 * System.out.println("---central pawns pushed score: " +
			 * centralPawnsPushedBonus);
			 * System.out.println("---bishop pair score: " + bishopPairBonus);
			 * System.out.println("---connected rooks score: " +
			 * connectedRooksBonus);
			 */
		

		return result;
	}

	private int computeConnectedRooksBonus(Move move) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int computeBishopPairBonus(Move move, boolean isWhite) {

		ArrayList<Piece> pieceList = findPieceList(isWhite);
		int numBishops = 0;
		int result = 0;

		for (Piece piece : pieceList) {
			if (piece.getType().equals("bishop"))
				numBishops++;
		}

		if (numBishops == 2)
			result = Constants.getBishopPairBonusWeight();

		return result;
	}

	/**
	 * If we are not yet 10 moves in and the move considered is a central pawn
	 * push, award bonus points
	 * 
	 * @param move
	 * @return
	 */
	private int computeCentralPawnsPushedBonus(Move move, boolean isWhite) {
		int result = 0;
		Piece piece = move.getPiece();

		if (piece.getType().equals("pawn")) {
			int col = piece.getCol();

			// If it was a central pawn
			if (col == 3 || col == 4) {

				if ((move.getStartRow() == 1 && isWhite)
						|| (move.getStartRow() == 6 && !isWhite))
					result = Constants.getCentralPawnsPushedBonusWeight();
			}
		}

		return result;
	}

	/**
	 * Returns a reference to the king on the team specified by isWhite.
	 * 
	 * @param isWhite
	 * @return
	 */
	public Piece findKing(boolean isWhite) {
		ArrayList<Piece> pieceList = findPieceList(isWhite);
		Piece king = null;

		for (Piece piece : pieceList) {
			if (piece.getType().equals("king"))
				king = piece;
		}

		return king;
	}

	public ArrayList<Piece> findPieceList(boolean isWhite) {
		ArrayList<Piece> pieceList = null;
		if (isWhite)
			pieceList = controller.getModel().getWhitePieces();
		else
			pieceList = controller.getModel().getBlackPieces();
		return pieceList;
	}

	/**
	 * Returns a bonus if there have been less than 10 moves and the move being
	 * considered is a castling move
	 * 
	 * @param move
	 * @return
	 */
	public int computeCastlingBonus(Move move, Piece king) {
		int result = 0;
		if (controller.getModel().getMoveList().size() < 20) {
			int unsignedDeltaCol = RuleEngine.calculateDeltaColUnsigned(move);

			// If the potential move is a castle, assign it weight
			if (move.getPiece().getType().equals("king")
					&& unsignedDeltaCol == 2)
				result = Constants.getCastlingBonusWeight();

		}

		return result;
	}

}