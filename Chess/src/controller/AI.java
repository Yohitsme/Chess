package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import javax.swing.tree.DefaultMutableTreeNode;

import model.Move;
import model.Node;
import model.Piece;
import utils.Constants;
import utils.Log;

public class AI {

	Controller controller;
	boolean debug = true;
	Log log = new Log();
	int nodesVisited = 0;
	int depth;
	long initTime = 0;
	Node bestNode;
	ArrayList<ArrayList<Node>> killerMoves = new ArrayList<ArrayList<Node>>(
			Constants.getDepth()+1);

	public AI(Controller controllerIn) {
		this.controller = controllerIn;
		
		for (int i = 0; i < Constants.getDepth()+1; i++)
			killerMoves.add(new ArrayList<Node>());
	}

	public Move move(String color) {
		Node node = null;

		boolean isWhite = color.equals("white") ? true : false;
		chooseMove(isWhite);
		node = bestNode;
		log.info("AI.move: Move chosen: "
				+ node.getMove().algebraicNotationPrint());
		System.out.println("Nodes visited: " + nodesVisited);
		nodesVisited = 0;

//		for (ArrayList<Node> list : killerMoves)
//			System.out.println(killerMoves.indexOf(list) + ": "
//					+ list.toString());
		return node.getMove();
	}

	public void chooseMove(boolean isWhite) {

		double alpha = -1000000000.0;
		double beta = 1000000000.0;

		Node parentNode = controller.gameTreeController.root;
		initializeKillerMoveArrays();

		for (int depth = 1; depth <= Constants.getDepth(); depth++) {
			this.depth = (int) depth;
			alphaBeta(alpha, beta, depth, isWhite, parentNode);
		}

	}

	/**
	 * Empty the arraylist of arraylists of killer moves
	 */
	private void initializeKillerMoveArrays() {

		for (int i = 0; i <= Constants.getDepth(); i++)
			killerMoves.get(i).removeAll(killerMoves.get(i));

	}

	double alphaBeta(double alpha, double beta, int depthleft, boolean isWhite,
			Node parentNode) {
		Node pv = null;
		double score = 0.0;
		long startTime = 0;
		int i = 0;
		boolean tmpHasMoved;

		// Termination condition
		if (depthleft == 0) {
			return evaluate(isWhite, parentNode);
		}

		// Branch timing housekeeping
		if (depthleft == Constants.getDepth()) {
			startTime = System.currentTimeMillis();
			initTime = startTime;
		}

		if (parentNode.getChildren().size() == 0)
			populateChildren(parentNode, isWhite, depthleft);

		for (int j = 0; j < parentNode.getChildren().size(); j++) {

			// Branch timing housekeeping
			if (depthleft == Constants.getDepth()) {
				printTimeStats(i, parentNode, startTime);
				startTime = System.currentTimeMillis();
			}

			Node node = parentNode.getChildren().get(j);
			Move move = node.getMove();

			Piece capturedPiece = RuleEngine.processMove(move);
			tmpHasMoved = move.getPiece().isHasMoved();
			move.getPiece().setHasMoved(true);

			score = -alphaBeta(-beta, -alpha, depthleft - 1, !isWhite, node);

			node.setUserObject(++i + ", " + move.algebraicNotationPrint()
					+ ": " + score);

			RuleEngine.undoChanges(capturedPiece, move);
			move.getPiece().setHasMoved(tmpHasMoved);

			// Fail hard beta-cutoff
			if (score >= beta) {

				// If we're quitting this recursion early because of a
				// beta cut off, save the PV we've found so far
				if (pv != null)
					prioritizeNode(parentNode, pv);

				// If the current move isn't already a killer move, make it one
				boolean nodeFound = false;
				for (Node killerNode : killerMoves.get(depthleft))
					if (killerNode.getMove().equals(node.getMove()))
						nodeFound = true;
				if (!nodeFound)
					killerMoves.get(depthleft).add(node);

				return beta;
			}

			// Tighten the alpha bound
			if (score > alpha || bestNode == null) {
				alpha = score;
				pv = node;

				// If we are in the first recursive call, save the best move
				// so we can use it later
				if (depthleft == Constants.getDepth()) {
					bestNode = node;
				}
			}
		}

		if (parentNode.getChildren().size() == 0) {
			// If I have no moves assume I was checkmated and return low alpha
			// value
			alpha = -10000000000000.0;
		}
		if (pv != null)
			prioritizeNode(parentNode, pv);

		return alpha;
	}

	/**
	 * Takes the Node child and puts it in the front of the arraylist of children
	 * of the parent Node.  This makes that child the first one analyzed when
	 * looping through the parents' children.
	 * @param parent
	 * @param child
	 */
	public void prioritizeNode(Node parent, Node child) {
		parent.getChildren().remove(child);
		parent.getChildren().add(0, child);
	}

	/**
	 * Build the subtree of legal moves for the Node parent node passed as a parameter.
	 * This also populates the DefaultMutableTree object tree, so the GUI reflects the
	 * real game tree
	 * @param parentNode
	 * @param isWhite
	 * @param depthleft
	 */
	public void populateChildren(Node parentNode, boolean isWhite, int depthleft) {
		ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
				isWhite);

		for (Move move : legalMoves) {

			Node node = new Node(move);
			node.setUserObject(move.algebraicNotationPrint());

			parentNode.add(node);
			parentNode.getChildren().add(node);
		}

		orderMoves(parentNode.getChildren(), depthleft);
	}

	
	/**
	 * Takes a list of legal moves and attempts to sort them with the following weight:
	 * 
	 * 1. MVV-LVA (pxn before nxp
	 * 2. Killer Heuristic
	 * 
	 * alphaBeta search prioritizes PV nodes as it finds them, and that always
	 * happens after this method is called, so PV nodes end up in the front of
	 * the arraylist.
	 * @param nodes
	 * @param depthleft
	 */
	@SuppressWarnings("unchecked")
	public void orderMoves(ArrayList<Node> nodes, int depthleft) {

		for (int i = 0; i < nodes.size(); i++) {

			Node node = nodes.get(i);
			Piece otherPiece = controller.boardController.getPieceByCoords(node
					.getMove().getEndRow(), node.getMove().getEndCol());

			// If it is a capture, get the material difference and save it
			// so we can sort (achieves MVV-LVA)
			if (otherPiece != null) {
				int materialDifference = Constants.getPieceWeight(node
						.getMove().getPiece())
						- Constants.getPieceWeight(otherPiece);
				node.setScore(materialDifference);

			}

			// If it's not a capture, check to see if it is in the killer moves
			// array. If so, set the score to -0.1 so it is before the
			// noncaptures
			else {
				boolean nodeFound = false;
				for (Node killerNode : killerMoves.get(depthleft))
					if (killerNode.getMove().equals(node.getMove()))
						nodeFound = true;
				if (nodeFound) {
					node.setScore(Constants.getKillerMoveScore());
				}
			}
		}
		Collections.sort(nodes, new NodeComparator());
		// System.out.println("---------");
		// for (Node node: nodes){
		// System.out.println(controller.boardController.getPieceByCoords(node
		// .getMove().getStartRow(),
		// node.getMove().getStartCol())+" "+controller.boardController.getPieceByCoords(node
		// .getMove().getEndRow(), node.getMove().getEndCol()) +
		// node.getScore());
		// }

	}

	/**
	 * Calls all evaluation methods on a potential move and returns the score of
	 * the move.
	 * 
	 * @return
	 */
	public double evaluate(boolean isWhitesTurn, Node node) {
		if (this.depth == Constants.getDepth())
			nodesVisited++;
		double result = 0.0;
		int positionalScore = computePositionalScore(isWhitesTurn, node);
		int materialScore = computeMaterialScore();
		int bonusScore = computeBonusScore();

		double weightedPositionalScore = positionalScore
				* Constants.getPositionalScoreWeight();
		double weightedMaterialScore = materialScore
				* Constants.getMaterialScoreWeight();
		double weightedBonusScore = bonusScore
				* Constants.getBonusScoreWeight();

		/*log.info("AI.evaluate: weightdP: " + weightedPositionalScore
				+ " weighgtedM: " + weightedMaterialScore + " weightedB: "
				+ weightedBonusScore);*/

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
	public int computePositionalScore(boolean isWhite, Node node) {

		/*
		 * Note: isWhite is the OPPOSITE color of the piece in node. We have a
		 * reference to that so we can add legal children nodes to node if they
		 * don't exist already
		 */
		// TODO: Extra points for center control

		int whiteMoves = 0;
		int blackMoves = 0;

		// This method is only called when evaluate is called, which is only
		// when depth = 0
		int depth = 0;

		if (isWhite) {
			if (node.getChildren().size() == 0)
				populateChildren(node, isWhite, depth);

			whiteMoves = node.getChildren().size();

			
			blackMoves = controller.getMoveGenerator().findMoves(false).size();
		}

		else {

			if (node.getChildren().size() == 0)
				populateChildren(node, isWhite, depth);
			blackMoves = node.getChildren().size();

			whiteMoves = controller.getMoveGenerator().findMoves(true).size();

		}

		int difference = whiteMoves - blackMoves;

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

		int result = computeOneSidedBonusScore(white)
				- computeOneSidedBonusScore(black);

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

	public int computeOneSidedBonusScore(boolean isWhite) {
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

	/**
	 * Old version of Negamax, replaced by method AlphaBeta.  
	 * @param depth
	 * @param previousMove
	 * @param parentNode
	 * @return
	 */
	public double Negamax(int depth, Move previousMove,
			DefaultMutableTreeNode parentNode) {
		if (depth == 0) {
			// previousMove.timesEvaluated++;
			// return evaluate(!previousMove.getPiece().isWhite(),
			// previousMove));
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
	 * Calculate how long the previous branch of the tree took to search
	 * and print to the console.
	 * @param i
	 * @param parentNode
	 * @param startTime
	 */
	public void printTimeStats(int i, Node parentNode, long startTime) {
		long endTime = System.currentTimeMillis();
		System.out.println("AI.ChooseMove: " + ++i
				+ " of "
				+ parentNode.getChildren().size()
				// * Constants.getDepth()
				+ " branches searched. Time elapsed: " + (endTime - initTime)
				/ 1000.0 + " seconds, last branch took "
				+ (endTime - startTime) / 1000.0 + " seconds");

		startTime = System.currentTimeMillis();
	}
}


/**
 * Simple comparator implementation so I can use the sort method in the
 * Collection class. This helps move ordering to get tight alpha/beta
 * bounds quickly
 * @author Matthew
 *
 */
class NodeComparator implements Comparator {

	@Override
	public int compare(Object node1, Object node2) {
		// TODO Auto-generated method stub neg is less than

		double scoreDifference = (((Node) node1).getScore() - ((Node) node2)
				.getScore());

		if (scoreDifference > 0)
			return 1;
		else if (scoreDifference == 0)
			return 0;
		else
			return -1;
	}
}
