package controller;

import java.util.ArrayList;
import java.util.Random;

import utils.Constants;
import model.Move;
import model.Piece;

public class AI {

	Controller controller;

	public AI(Controller controllerIn) {
		this.controller = controllerIn;
	}

	public Move move(String color) {

		ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
				color);
		Move move = chooseMove(legalMoves);

		System.out.println("AI.move: Move chosen: "
				+ move.algebraicNotationPrint());

		return move;
	}

	public Move chooseMove(ArrayList<Move> legalMoves) {

		int score = 0;
		int highest = -1;
		Move bestMove = null;
		for (Move move : legalMoves) {
			score = evaluate(move);
			if (score > highest){
				System.out.println("AI.chooseMove: New highest: " + move.algebraicNotationPrint());
				bestMove = move;
				highest = score;
			}
		}
		return bestMove;
	}

	public Move chooseRandomMove(ArrayList<Move> legalMoves) {
		Random rand = new Random();
		Move move = null;
		int index = rand.nextInt(legalMoves.size());
		if (index >= 0) {
			move = legalMoves.get(index);

		}
		return move;
	}

	/**
	 * Calls all evaluation methods on a potential move and returns the score of
	 * the move.
	 * 
	 * @return
	 */
	public int evaluate(Move move) {

		int positionalScore = computePositionalScore(move);
		int materialScore = computeMaterialScore(move);
		int bonusScore = computeBonusScore(move);

		return positionalScore + materialScore + bonusScore;
	}

	/**
	 * Computes the integer value of the position, taking into account piece
	 * mobility, king safety, mobility of the enemy, passed pawns, doubled
	 * pawns, pawn structure, etc.
	 */
	public int computePositionalScore(Move move) {
		return 0;
	}

	/**
	 * Computes the material score of a move. Usually this just amounts to the
	 * value of the piece captured, if one was captured. Also accounts for pawn
	 * promotion.
	 * 
	 * @return
	 */
	public int computeMaterialScore(Move move) {
		int result = 0;
		Piece piece = controller.getBoardController().getPieceByCoords(
				move.getEndRow(), move.getEndCol());

		if (piece != null){
			result = Constants.getPieceWeight(piece);
		}
		// TODO account for passant
		// TODO pawn promotion

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

		boolean isWhite = move.getPiece().isWhite();
		
		
		
		return 0;
	}


}
