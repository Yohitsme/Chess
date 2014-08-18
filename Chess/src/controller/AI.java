package controller;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.tree.DefaultMutableTreeNode;

import model.Move;
import model.Piece;
import utils.Constants;
import utils.Log;

public class AI {

	Controller controller;
	boolean debug = true;
	Log log = new Log();
	double alpha;
	double beta;

	public AI(Controller controllerIn) {
		this.controller = controllerIn;
	}

	public Move move(String color) {
		Move move = null;

		boolean isWhite = color.equals("white") ? true : false;

		ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
				isWhite);

		move = chooseMove(legalMoves, isWhite);

		log.info("AI.move: Move chosen: " + move.algebraicNotationPrint());

		return move;
	}

	public Move chooseMove(ArrayList<Move> legalMoves, boolean isWhite) {

//		System.out.println(Double.MAX_VALUE);
		
		double score = 0;
		double highest = -100000000;
		Move bestMove = null;
		Random rand = new Random();
		
		alpha = -1000000000.0;
		beta = 10000000000000.1;
		
		DefaultMutableTreeNode parentNode = controller.gameTreeController.root;
		for (Move move : legalMoves) {
		
			Piece capturedPiece = RuleEngine.processMove(move);
			ArrayList<Move> moveList = controller.getModel().getMoveList();

			DefaultMutableTreeNode node = new DefaultMutableTreeNode();
			node.setAllowsChildren(true);

//			score = -Negamax(Constants.getDepth() - 1,
//					moveList.get(moveList.size() - 1), node);

			score = alphaBetaMax(Constants.getDepth() -1, isWhite, node);
			node.setUserObject(move.algebraicNotationPrint() + ": " + score);

			parentNode.add(node);

			RuleEngine.undoChanges(capturedPiece, move);
			if (score > highest) {
				bestMove = move;
				highest = score;
			}

			// If the considered move is as good as the best so far, there's a
			// 10% chance the engine will pick it instead
//			else if (score == highest) {
//				if (rand.nextInt(10) == 0) {
//					bestMove = move;
//					highest = score;
//				}
//			}
		}
		return bestMove;
	}

	double alphaBetaMax(double depthleft, boolean isWhite,DefaultMutableTreeNode parentNode ) {
		double score = 0.0;  
		if ( depthleft == 0 ) 
			   return evaluate(isWhite);
		   
			ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
					!isWhite);
			int i = 0;
		   for ( Move move: legalMoves) {
			   boolean tmpHasMoved;
			   i++;
				Piece capturedPiece = RuleEngine.processMove(move);   // process
				DefaultMutableTreeNode node = new DefaultMutableTreeNode();
				node.setAllowsChildren(true);
				tmpHasMoved = move.getPiece().isHasMoved();
				move.getPiece().setHasMoved(true);
				score = alphaBetaMin(depthleft - 1, !isWhite,node);
		      
				node.setUserObject(i + ", " + move.algebraicNotationPrint() + ": "
						+ score);

				parentNode.add(node);

				RuleEngine.undoChanges(capturedPiece, move);
				move.getPiece().setHasMoved(tmpHasMoved);
				  // undo
		      if( score >= beta )
		         return beta;   // fail hard beta-cutoff
		      if( score > alpha )
		         alpha = score; // alpha acts like max in MiniMax
		   }
		   return alpha;
		}

	double alphaBetaMin(double depthleft, boolean isWhite,DefaultMutableTreeNode parentNode  ) {
		  
		double score = 0.0;
		if ( depthleft == 0 ) 
			   return -evaluate(isWhite);
		   ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
					!isWhite);
		   
		   int i = 0;
		   for ( Move move: legalMoves) {
			   boolean tmpHasMoved;
				i++;
			   Piece capturedPiece = RuleEngine.processMove(move);  // process
				DefaultMutableTreeNode node = new DefaultMutableTreeNode();
				node.setAllowsChildren(true);
				tmpHasMoved = move.getPiece().isHasMoved();
				move.getPiece().setHasMoved(true);
				score = alphaBetaMax(depthleft - 1, !isWhite,node );
				node.setUserObject(i + ", " + move.algebraicNotationPrint() + ": "
						+ score);

				parentNode.add(node);
				move.getPiece().setHasMoved(tmpHasMoved);
				RuleEngine.undoChanges(capturedPiece, move);       // undo
			   if( score <= alpha )
		         return alpha; // fail hard alpha-cutoff
		      if( score < beta )
		         beta = score; // beta acts like min in MiniMax
		   }
		   return beta;
		}
	
	public double Negamax(int depth, Move previousMove,
			DefaultMutableTreeNode parentNode) {
		if (depth == 0) {
			return evaluate(!previousMove.getPiece().isWhite());
		}
		double max = Integer.MIN_VALUE;
		double score = 0.0;

		boolean isWhite = !previousMove.getPiece().isWhite();
		ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
				isWhite);
		int i = 0;
		for (Move move : legalMoves) {
			i++;
			Piece capturedPiece = RuleEngine.processMove(move);

			DefaultMutableTreeNode node = new DefaultMutableTreeNode();
			node.setAllowsChildren(true);
			score = -Negamax(depth - 1, move, node);

			node.setUserObject(i + ", " + move.algebraicNotationPrint() + ": "
					+ score);

			parentNode.add(node);

			if (score > max)
				max = score;
			RuleEngine.undoChanges(capturedPiece, move);
		}
		return max;
	}



	/**
	 * Calls all evaluation methods on a potential move and returns the score of
	 * the move.
	 * 
	 * @return
	 */
	public double evaluate(boolean isWhitesTurn) {
		double result = 0.0;
		int positionalScore = computePositionalScore();
		int materialScore = computeMaterialScore();
		int bonusScore = computeBonusScore();

		// log.debug("-Positional Score: " + positionalScore);
		// log.debug("-Material Score: " + materialScore);

		double weightedPositionalScore = positionalScore
				* Constants.getPositionalScoreWeight();
		double weightedMaterialScore = materialScore
				* Constants.getMaterialScoreWeight();
		double weightedBonusScore = bonusScore
				* Constants.getBonusScoreWeight();

		log.info("AI.evaluate: weightdP: " + weightedPositionalScore
				+ " weighgtedM: " + weightedMaterialScore + " weightedB: "
				+ weightedBonusScore);

		double totalScore = weightedPositionalScore + weightedMaterialScore
				+ weightedBonusScore;
//		log.info("AI.evaluate: Considering " + move.algebraicNotationPrint()
//				+ ", score: " + totalScore);

		ArrayList<Move> moveList = controller.getModel().getMoveList();
		for (Move pastMove : moveList)
			log.info(pastMove.algebraicNotationPrint());
		// log.writeLine();

		 result = weightedPositionalScore + weightedMaterialScore
				+ weightedBonusScore;
		 
		 if (!isWhitesTurn)
			 result = result * -1.0;
		 
		 return result;
	}

	/**
	 * Computes the integer value of the position, taking into account piece
	 * mobility, king safety, mobility of the enemy, passed pawns, doubled
	 * pawns, pawn structure, etc.
	 * 
	 * Very computationally expensive, will need to optimize this in the future.
	 */
	public int computePositionalScore() {

		// TODO: Extra points for center control
		ArrayList<Move> whiteMoves = controller.getMoveGenerator().findMoves(
				true);

		ArrayList<Move> blackMoves = controller.getMoveGenerator().findMoves(
				false);

		int	difference = whiteMoves.size() - blackMoves.size();
		
		return difference;
	}

	/**
	 * Computes the material score of a move. This is the difference between the
	 * amount of material (in points) for black and white. Positive means white 
	 * is ahead in material.
	 * 
	 * @return
	 */
	public int computeMaterialScore() {
		int result = 0;
		int whiteScore = 0;
		int blackScore = 0;

		ArrayList<Piece> whitePieces = controller.getModel().getWhitePieces();
		ArrayList<Piece> blackPieces = controller.getModel().getBlackPieces();

		for (Piece piece : whitePieces) {
			whiteScore += Constants.getPieceWeight(piece);

			// If it's a pawn that will be promoting, let it have the extra
			// value it would have if it promotes to a queen (-1 because it
			// already has 1 for being pawn)
			if (piece.getType().equals("pawn") && piece.getRow() == 7) {
				whiteScore += Constants.getQueenweight() - 1;
				// System.out.println("was " + whiteScore);
				// System.out.println("now " + whiteScore);
			}
		}
		for (Piece piece : blackPieces) {
			blackScore += Constants.getPieceWeight(piece);

			if (piece.getType().equals("pawn") && piece.getRow() == 0) {
				blackScore += Constants.getQueenweight() - 1;

			}
		}
		// TODO account for passant

			result = whiteScore - blackScore;

		return result;
	}

	/**
	 * Computes the integer value of "bonuses". These can be for castling,
	 * moving the king and queen pawns early, having both bishops, having
	 * connected rooks, etc.
	 * 
	 * @return
	 */
	public int computeBonusScore() {

		// Preprocess information that several methods need to find anyway
		boolean black = false;
		boolean white = true;
		
		int result = computeOneSidedBonusScore(white) - computeOneSidedBonusScore(black);

		// TODO: Bonus for not moving the queen early on
		// TODO: Bonus for not moving the same piece twice in the opening
		// TODO: Bonus for not having a knight on the edge of the board
		// TODO: Bonus for connected pawns
		// TODO: Penalty for doubled pawns
		// TODO: Penalty for isolated pawns

		// log.debug("-bonusScore Score: " + result);
		/*
		 * System.out.println("---castling score: " + castlingBonus);
		 * System.out.println("---central pawns pushed score: " +
		 * centralPawnsPushedBonus); System.out.println("---bishop pair score: "
		 * + bishopPairBonus); System.out.println("---connected rooks score: " +
		 * connectedRooksBonus);
		 */

		return result;
	}
	
	public int computeOneSidedBonusScore(boolean isWhite){
		int result = 0;
		Piece king = findKing(isWhite);

		int castlingBonus = computeCastlingBonus(king);
		int centralPawnsPushedBonus = computeCentralPawnsPushedBonus(isWhite);
		int bishopPairBonus = computeBishopPairBonus(isWhite);
		int connectedRooksBonus = computeConnectedRooksBonus(isWhite);

		result = castlingBonus + centralPawnsPushedBonus + bishopPairBonus
				+ connectedRooksBonus;
		return result;
	}

	private int computeConnectedRooksBonus(boolean isWhite) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int computeBishopPairBonus(boolean isWhite) {

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
	private int computeCentralPawnsPushedBonus(boolean isWhite) {
		int result = 0;
		int pawnRow = -1;
		int col = -1;
		Piece piece = null;
		
		if (isWhite)
			pawnRow = Constants.getWhitePawnRow();
		else
			pawnRow = Constants.getBlackPawnRow();
		
		// Check King column pawn
		col = Constants.getKingColumn();
		piece = controller.getBoardController().getPieceByCoords(pawnRow, col);
		if ((piece == null) || (!piece.getType().equals("pawn")))
			result += Constants.getCentralPawnsPushedBonusWeight();
		
		// Check Queen column pawn
		col = Constants.getQueenColumn();
		piece = controller.getBoardController().getPieceByCoords(pawnRow, col);
		if ((piece == null) || (!piece.getType().equals("pawn")))
			result += Constants.getCentralPawnsPushedBonusWeight();
		
		
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
	public int computeCastlingBonus(Piece king) {
		int result = 0;
		if (controller.getModel().getMoveList().size() < 20) {
			boolean isWhite = king.isWhite();

			for (Move move : controller.getModel().getMoveList()) {
				if (move.getPiece() == king
						&& RuleEngine.calculateDeltaColUnsigned(move) == 2)
					result = Constants.getCastlingBonusWeight();
			}

		}

		return result;
	}

}
