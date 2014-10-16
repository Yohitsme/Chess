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

package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import model.Move;
import model.Node;
import model.Piece;
import model.PieceArray;
import utils.Constants;
import utils.Log;

/**
 * This class holds the searching and evaluation parts of the chess engine.
 * 
 * You can create an instance of the AI class and call AI.move(boolean isWhite)
 *  and it will return the move it chooses.
 * 
 * All of the weights for bonuses, penalties, and piece values can be found
 * and/or tweaked in utils/Constants.java.
 * @author Matthew
 *
 */
public class AI {

	Controller controller;
	boolean debug = true;
	Log log = new Log();
	int nodesVisited = 0;
	int depth;
	long initTime = 0;
	Node bestNode;
	ArrayList<Node> localPV;
	ArrayList<Node> masterPV = new ArrayList<Node>();
	ArrayList<ArrayList<Move>> killerMoves = new ArrayList<ArrayList<Move>>(
			Constants.getDepth() + 1);
	Node[] PV;
	int nodesPerLevel[];
	boolean isNullMoveBranch = false;
    static NodeComparator nodeComparator;
    boolean isThinking = false;
    int branchCounter;
    int numBranches;
    
	/**
	 * Constructor
	 * @param controllerIn
	 */
	public AI(Controller controllerIn) {
		this.controller = controllerIn;

		for (int i = 0; i < Constants.getDepth() + 1; i++)
			killerMoves.add(new ArrayList<Move>());
		PV = new Node[10];
		this.nodeComparator = new NodeComparator();
		for (int i = 0; i < 10; i++)
			PV[i] = null;

		nodesPerLevel = new int[20];
	}

	/**
	 * This method calls the choose move method and returns the result.
	 * @param isWhiteTurn
	 * @return
	 */
	public Node move(boolean isWhiteTurn) {
		Node node = null;
		isThinking = true;
		chooseMove(isWhiteTurn);
		node = bestNode;
		log.info("AI.move: Move chosen: "
				+ node.getMove().algebraicNotationPrint());
		System.out.println("Nodes visited: " + nodesVisited);
		nodesVisited = 0;

//		 Branching factor calculations
//		 if (true)
//		 for (int i = 0; i< 15; i++){
//		 if (nodesPerLevel[i+1]!=0)
//		 System.out.println("Branching Factor " + i + ": " +
//		 (double)nodesPerLevel[i+1]/(double)nodesPerLevel[i]);
//		 }
		// for (ArrayList<Node> list : killerMoves)
		// System.out.println(killerMoves.indexOf(list) + ": "
		// + list.toString());
		isThinking = false;
		return node;
	}

	/**
	 * This method deepens iteratively and calls the pvSearch method, building
	 * up the masterPV array with the most probably variation, and then returns
	 * the first move in that sequence.
	 * @param isWhiteTurn
	 */
	public void chooseMove(boolean isWhiteTurn) {

		double alpha = -100000000000.0;
		double beta =   100000000000.0;

		// Reset Branch counter
		branchCounter = 0;
		Node parentNode = controller.gameTreeController.root;

		initializeKillerMoveArrays();

		for (int depth = 1; depth <= Constants.getDepth(); depth++) {
			this.depth = depth;

			if (depth == Constants.getDepth()){
				System.out.println();
				numBranches = parentNode.getChildren().size();
			}
			pvSearch(alpha, beta, depth, isWhiteTurn, parentNode);

			masterPV = new ArrayList<Node>();
			masterPV.addAll(this.localPV);
			// for (int i = 0; i < 10; i++)
			// if (PV[i] != null)
			// System.out.println(PV[i].getMove().algebraicNotationPrint());

		}
		for (Node node : this.masterPV) {
			System.out.println("PV: "
					+ node.getMove().coloredAlgebraicNotationPrint());
		}
		bestNode = this.masterPV.get(0);

	}

	/**
	 * Empty the arraylist of arraylists of killer moves
	 */
	private void initializeKillerMoveArrays() {

		for (int i = 0; i <= Constants.getDepth(); i++)
			killerMoves.get(i).removeAll(killerMoves.get(i));

	}

	/**
	 * Updates the arrays that hold killer moves.  This gets called whenever
	 * the user changes the depth the AI is searching to.
	 */
	public void resizeKillerMoveArrays() {
		killerMoves = new ArrayList<ArrayList<Move>>(Constants.getDepth() + 1);
		for (int i = 0; i < Constants.getDepth() + 1; i++)
			killerMoves.add(new ArrayList<Move>());

	}

	double pvSearch(double alpha, double beta, int depthleft, boolean isWhiteTurn,
			Node parentNode) {
		Node pv = null;
		double score = 0.0;
		long startTime = 0;
		int i = 0;
		boolean tmpHasMoved;
		boolean bSearchPv = true;
		boolean exploringPV = false;

		// Validate board and pieces agree on all piece locations for debugging
//		for (int r = 0; r < 8; r++){
//			for (int c = 0; c < 8; c++)
//				if (controller.getBoardController().getPieceByCoords(r, c)!=null)
//				if (controller.getBoardController().getPieceByCoords(r, c).getRow()!= r ||controller.getBoardController().getPieceByCoords(r, c).getCol()!= c )
//					System.out.println("AI.pvSearch: ERROR: Board out of sync, make/unmake process unstable");
//		}
		
		// If Check mate/draw/king got captured
		if (controller.isWhiteCheckmated()){this.localPV = new ArrayList<Node>();
		this.localPV.add(parentNode);
			score = -Constants.getCheckMateScore(); // This one should be negative
		}else if (controller.isBlackCheckmated()){this.localPV = new ArrayList<Node>();
		this.localPV.add(parentNode);
			score = Constants.getCheckMateScore();
		}else if (controller.isDrawByThreefoldRepitition()){
			this.localPV = new ArrayList<Node>();
			this.localPV.add(parentNode);
			return Constants.getDrawScore();}
		if (score != 0)
			return score;
		
		// Termination condition
		if (depthleft == 0) {
			this.localPV = new ArrayList<Node>();
			this.localPV.add(parentNode);
			return quiesce(alpha, beta, isWhiteTurn, parentNode, depthleft);
		}

		ArrayList<Node> localPV = new ArrayList<Node>();

		// Branch timing housekeeping
		if (depthleft == Constants.getDepth()) {
			startTime = System.currentTimeMillis();
			initTime = startTime;
		}

		if (parentNode.getChildren().size() == 0)
			populateChildren(parentNode, isWhiteTurn, depthleft);

		// We should always start with PV if on first ply of search, if it
		// isn't null

		// If the established PV is longer than the chain we're looking at,
		// then there's at least one move we might consider
		boolean isDeepEnough = masterPV.size() > this.depth + 1- depthleft;

		if (isDeepEnough && validatePV(parentNode, depthleft)){
			prioritizeNode(parentNode,
					this.masterPV.get(this.depth +1- depthleft));
			exploringPV = true;
		}
		for (int j = 0; j < parentNode.getChildren().size(); j++) {

			// Branch timing housekeeping
			if (depthleft == Constants.getDepth()) {
				branchCounter++;
				printTimeStats(i++, parentNode, startTime);
				startTime = System.currentTimeMillis();
			}

			Node node = parentNode.getChildren().get(j);
			Move move = node.getMove();

			Piece capturedPiece = RuleEngine.processMove(move);
			tmpHasMoved = move.getPiece().isHasMoved();
			move.getPiece().setHasMoved(true);


			// Null move addition BEGIN
//			if ((!(exploringPV && j == 0))&& (depthleft-1-Constants.getNullMoveReduction()>0) ){ // If we're looking at PV and on the first child move, we're continuing PV
//				if (!inCheck(isWhite)){
//					isNullMoveBranch = true;
////					System.out.println(depthleft-1-Constants.getNullMoveReduction());
//				
//					ArrayList <Node> realChildren = new ArrayList<Node>();
//					realChildren.addAll(parentNode.getChildren());
//					
//					parentNode.getChildren().removeAll(parentNode.getChildren());
//					populateChildren(parentNode, !isWhite,depthleft-1-Constants.getNullMoveReduction());
//				
//					score = -pvSearch(-beta,-alpha, depthleft-1-Constants.getNullMoveReduction(), isWhite,parentNode);
//					
//					parentNode.getChildren().removeAll(parentNode.getChildren());
//					parentNode.getChildren().addAll(realChildren);
//					
//					isNullMoveBranch = false;
// 
//		    if(score >= beta){
//				RuleEngine.undoChanges(capturedPiece, move);
//				move.getPiece().setHasMoved(tmpHasMoved);
//		    	return score; // Cutoff
//		    }
//				}
//				
//			}
			// Null move addition END

			// PV backend
			if (bSearchPv) {
				score = -pvSearch(-beta, -alpha, depthleft - 1, !isWhiteTurn, node);
			} else {
				score = -pvSearch(-alpha - 0.00000001, -alpha, depthleft - 1,
						!isWhiteTurn, node);
				if (score > alpha){
					score = -pvSearch(-beta, -alpha, depthleft - 1, !isWhiteTurn,
							node); // re-search
				}}

			RuleEngine.undoChanges(capturedPiece, move);
			move.getPiece().setHasMoved(tmpHasMoved);

			// Fail hard beta-cutoff
			if (score >= beta) {

				// If the current move isn't already a killer move, make it one
				boolean nodeFound = false;
				for (Move killerNode : killerMoves.get(depthleft))
					if (killerNode.equals(node.getMove()))
						nodeFound = true;
				if (!nodeFound){
					killerMoves.get(depthleft).add(node.getMove());
					
				}
				return beta;
			}

			// Tighten the alpha bound
			if (score > alpha) {
				alpha = score;
				pv = node;
				localPV = this.localPV;

				// If we are in the first recursive call, save the best move
				// so we can use it later
				if (depthleft == Constants.getDepth()) {
					bestNode = node;
				}
			}

			bSearchPv = false;
		}

		ArrayList<Node> tmp = new ArrayList<Node>();
		if (depthleft != Constants.getDepth())
			tmp.add(parentNode);
		tmp.addAll(localPV);
		this.localPV = tmp;
		return alpha;
	}

	
	/**
	 * Returns true if the king of color <code>isWhite</code> is in check.
	 * @param isWhite
	 * @return
	 */
	private boolean inCheck(boolean isWhite) {
		boolean result = false;
		Piece king = findKing(isWhite);
		if(RuleEngine.isAttackedSquare(king.getRow(),king.getCol(), king.isWhite()==true?"white":"black"))
		result = true;
		
		return result;
	}

	/**
	 * Returns the number of legal moves at depth <code>depth</code>
	 * 
	 * @param depth
	 * @return
	 */
	public int perft(int depth, boolean isWhite) {

		ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
				isWhite);

		int numMoves = 0;
		if (depth == 0)
			return legalMoves.size();
		else {
			for (Move move : legalMoves) {
				boolean tmpHasMoved = move.getPiece().isHasMoved();
				move.getPiece().setHasMoved(true);
				Piece capturedPiece = RuleEngine.processMove(move);
				
				numMoves += perft(depth - 1, !isWhite);
				
				RuleEngine.undoChanges(capturedPiece, move);
				move.getPiece().setHasMoved(tmpHasMoved);
			}

		}

		return numMoves;
	}

	
	/**
	 * Alpha Beta Search.  This was replaced by pvSearch.  Searches in a very similar
	 * way but pvSearch has optimizations that help performance significantly.
	 * @param alpha
	 * @param beta
	 * @param depthleft
	 * @param isWhite
	 * @param parentNode
	 * @return
	 */
	@Deprecated
	double alphaBeta(double alpha, double beta, int depthleft, boolean isWhite,
			Node parentNode) {
		Node pv = null;
		double score = 0.0;
		long startTime = 0;
		int i = 0;
		boolean tmpHasMoved;

		// Termination condition
		if (depthleft == 0) {
			this.localPV = new ArrayList<Node>();
			this.localPV.add(parentNode);
			return quiesce(alpha, beta, isWhite, parentNode, depthleft);
		}

		ArrayList<Node> localPV = new ArrayList<Node>();

		// Branch timing housekeeping
		if (depthleft == Constants.getDepth()) {
			startTime = System.currentTimeMillis();
			initTime = startTime;
		}

		if (parentNode.getChildren().size() == 0)
			populateChildren(parentNode, isWhite, depthleft);

		// We should always start with PV if on first ply of search, if it
		// isn't null

		// If the established PV is longer than the chain we're looking at,
		// then there's at least one move we might consider
		boolean isDeepEnough = masterPV.size() > this.depth - depthleft + 1;

		if (isDeepEnough && validatePV(parentNode, depthleft))
			prioritizeNode(parentNode,
					this.masterPV.get(this.depth - depthleft + 1));

		for (int j = 0; j < parentNode.getChildren().size(); j++) {

			// Branch timing housekeeping
			if (depthleft == Constants.getDepth()) {
				printTimeStats(++i, parentNode, startTime);
				startTime = System.currentTimeMillis();
			}

			Node node = parentNode.getChildren().get(j);
			Move move = node.getMove();

			Piece capturedPiece = RuleEngine.processMove(move);
			tmpHasMoved = move.getPiece().isHasMoved();
			move.getPiece().setHasMoved(true);

			score = -alphaBeta(-beta, -alpha, depthleft - 1, !isWhite, node);

			// node.setUserObject(++i + ", " + move.algebraicNotationPrint()
			// + ": " + score);

			RuleEngine.undoChanges(capturedPiece, move);
			move.getPiece().setHasMoved(tmpHasMoved);

			// Fail hard beta-cutoff
			if (score >= beta) {

				// If the current move isn't already a killer move, make it one
				boolean nodeFound = false;
				for (Move killerNode : killerMoves.get(depthleft))
					if (killerNode.equals(node.getMove()))
						nodeFound = true;
				if (!nodeFound)
					killerMoves.get(depthleft).add(node.getMove());

				return beta;
			}

			// Tighten the alpha bound
			if (score > alpha) {
				alpha = score;
				pv = node;
				localPV = this.localPV;

				// If we are in the first recursive call, save the best move
				// so we can use it later
				if (depthleft == Constants.getDepth()) {
					bestNode = node;
					System.out.println("AI.AB Choosing "
							+ node.getMove().algebraicNotationPrint());
				}
			}
		}

		if (parentNode.getChildren().size() == 0) {
			// If I have no moves assume I was checkmated and return low alpha
			// value
			alpha = -10000000000000.0;
		}

		ArrayList<Node> tmp = new ArrayList<Node>();
		if (depthleft != Constants.getDepth())
			tmp.add(parentNode);
		tmp.addAll(localPV);
		this.localPV = tmp;
		return alpha;
	}

	/**
	 * This continues the end of the search until it finds a "quiet" position, or
	 * one in which no captures can be made.  This helps avoid the horizon effect,
	 * and is called at the end of pvSearch.  It returns the score of the position 
	 * found by the eval function.
	 * @param alpha
	 * @param beta
	 * @param isWhite
	 * @param parentNode
	 * @param depthleft
	 * @return
	 */
	public double quiesce(double alpha, double beta, boolean isWhiteTurn,
			Node parentNode, int depthleft) {
		nodesPerLevel[this.depth]++;
		boolean printFlag = false;
		
		
		double stand_pat = evaluate(isWhiteTurn, parentNode, printFlag);
		double score;
		boolean tmpHasMoved = true;
		if (stand_pat >= beta)
			return beta;
		if (alpha < stand_pat)
			alpha = stand_pat;
		if (parentNode.getChildren().size() == 0)
			populateChildren(parentNode, isWhiteTurn, depthleft);

		for (Node node : parentNode.getChildren()) {
			Move move = node.getMove();
			int col = move.getEndCol();
			int row = move.getEndRow();
			Piece otherPiece = controller.boardController.getPieceByCoords(row,
					col);
			if (otherPiece != null) {

				Piece capturedPiece = RuleEngine.processMove(move);
				tmpHasMoved = move.getPiece().isHasMoved();
				move.getPiece().setHasMoved(true);

				score = -quiesce(-beta, -alpha, !isWhiteTurn, node, depthleft - 1);

				RuleEngine.undoChanges(capturedPiece, move);
				move.getPiece().setHasMoved(tmpHasMoved);

				if (score >= beta)
					return beta;
				if (score > alpha)
					alpha = score;
			}
		}
		return alpha;
	}

	/**
	 * Verifies that we have explored a Princpal variation branch up to the node
	 * parentNode.  This allows us to continue checking the PV.  If we are no longer
	 * on the PV branch, then we shouldn't assume that the PV node at the current
	 * depth is a good move.
	 * @param parentNode
	 * @param depthleft
	 * @return
	 */
	private boolean validatePV(Node parentNode, int depthleft) {

		int i = this.depth - depthleft; // how deep we are
		boolean result = true;

		while (i >= 0 && result == true) {
			if (parentNode == masterPV.get(i)) {

				parentNode = parentNode.getParent();
				i--;
			} else
				result = false;

		}

		return result;
	}

	
	/**
	 * Takes the Node child and puts it in the front of the arraylist of
	 * children of the parent Node. This makes that child the first one analyzed
	 * when looping through the parents' children.
	 * 
	 * @param parent
	 * @param child
	 */
	public void prioritizeNode(Node parent, Node child) {
		boolean successfulRemove = parent.getChildren().remove(child);
		if (successfulRemove)
		parent.getChildren().add(0, child);
		else
			System.out.println("AI.PrioritizeNode: ERROR, non-child node was attempted to prioritize");
		// child.setScore((-500));
	}

	/**
	 * Build the subtree of legal moves for the Node parent node passed as a
	 * parameter. This also populates the DefaultMutableTree object tree, so the
	 * GUI reflects the real game tree
	 * 
	 * @param parentNode
	 * @param isWhite
	 * @param depthleft
	 */
	public void populateChildren(Node parentNode, boolean isWhiteTurn, int depthleft) {
	
		ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(
				isWhiteTurn);
		for (Move move : legalMoves) {

			Node node = new Node(move);
			// node.setUserObject(move.algebraicNotationPrint());

			// parentNode.add(node);
			parentNode.getChildren().add(node);
			node.setParent(parentNode);
		}

		orderMoves(parentNode.getChildren(), depthleft);
	}

	/**
	 * Takes a list of legal moves and attempts to sort them with the following
	 * weight:
	 * 
	 * <li>1. MVV-LVA (pxn before nxp)
	 * <li>2. Killer Heuristic
	 * 
	 * pvSearch prioritizes PV nodes as it finds them, and that always
	 * happens after this method is called, so PV nodes end up in the front of
	 * the arraylist.
	 * 
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
				for (Move killerNode : killerMoves.get(depthleft))
					if (killerNode.equals(node.getMove()))
						nodeFound = true;
				if (nodeFound) {
					node.setScore(Constants.getKillerMoveScore());
				}
			}
		}
		Collections.sort(nodes, this.nodeComparator);

	}

	/**
	 * Calls all evaluation methods on a potential move and returns the score of
	 * the move.
	 * 
	 * @return
	 */
	public double evaluate(boolean isWhitesTurn, Node node, boolean printFlag) {
		if (this.depth == Constants.getDepth())
			nodesVisited++;
		double result = 0.0;

		// If the current position is a checkmate, we can give it the end-game
		// score without evaluating all the other factors.
		if (controller.isWhiteCheckmated()||controller.isBlackCheckmated())
			result = Constants.getCheckMateScore();
		else if (controller.isDrawByThreefoldRepitition())
			result = Constants.getDrawScore();
		else {
			int positionalScore = computePositionalScore(isWhitesTurn, node);
			int materialScore = computeMaterialScore();
			int bonusScore = computeBonusScore();

			double weightedPositionalScore = positionalScore
					* Constants.getPositionalScoreWeight();
			double weightedMaterialScore = materialScore
					* Constants.getMaterialScoreWeight();
			double weightedBonusScore = bonusScore
					* Constants.getBonusScoreWeight();
			
			/*
			 * log.info("AI.evaluate: weightdP: " + weightedPositionalScore +
			 * " weighgtedM: " + weightedMaterialScore + " weightedB: " +
			 * weightedBonusScore);
			 */

			result = weightedPositionalScore + weightedMaterialScore
					+ weightedBonusScore;
			
			if (printFlag){
				System.out.println("Evaluation of current position: " + result);
				System.out.println("-Material score:" + weightedMaterialScore);
				System.out.println("-Positional score:" + weightedPositionalScore);
				System.out.println("-Bonus score:" + weightedBonusScore);			
			}

		}
		
		// Positive scores mean white is winning, so we negate the calculated score here
		// if it is black's turn
		if (!isWhitesTurn)
			result = result * -1.0;

		// If no legal moves, it's a stalemate
		if (node.getChildren().size() == 0)
			result = Constants.getDrawScore();

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

		PieceArray whitePieces = controller.getModel().getWhitePieces();
		PieceArray blackPieces = controller.getModel().getBlackPieces();

		for (int i = 0; i < PieceArray.numPieces; i++){
			Piece piece = whitePieces.getPiece(i);
			if (piece != null){
			whiteScore += Constants.getPieceWeight(piece);

			// If it's a pawn that will be promoting, let it have the extra
			// value it would have if it promotes to a queen (-1 because it
			// already has 1 for being pawn)
			if (piece.getType() == Constants.getPawnChar() && piece.getRow() == 7) {
				whiteScore += Constants.getQueenweight() - 1;
			}
			}
		}
		for (int i = 0; i < PieceArray.numPieces; i++){
			Piece piece = blackPieces.getPiece(i);
			if (piece != null){
			blackScore += Constants.getPieceWeight(piece);

			if (piece.getType() == Constants.getPawnChar() && piece.getRow() == 0) {
				blackScore += Constants.getQueenweight() - 1;
			}
			}
		}
		// TODO account for passant

		result = whiteScore - blackScore;

		return result;
	}

	/**
	 * Calls the bonus calculation methods for each side and returns the difference.
	 * 
	 * @return
	 */
	public int computeBonusScore() {

		// TODO: Break up the bonuses instead of lumping them all together
		//       so it is easier to apply different weights.
		
		boolean black = false;
		boolean white = true;

		int result = computeOneSidedBonusScore(white)
				- computeOneSidedBonusScore(black);

		return result;
	}

	/**
	 * Computes the integer value of "bonuses". These can be for castling,
	 * moving the king and queen pawns early, having both bishops, having
	 * connected rooks, etc.
	 * @param isWhite
	 * @return
	 */
	public int computeOneSidedBonusScore(boolean isWhite) {
		int result = 0;
		Piece king = findKing(isWhite);

		// TODO: Bonus for not moving the same piece twice in the opening
		// TODO: Bonus for not having a knight on the edge of the board
		// TODO: Bonus for connected pawns
		// TODO: Penalty for doubled pawns
		// TODO: Penalty for isolated pawns

		int castlingBonus = computeCastlingBonus(king);
		int multiMoveOpeningPiecePenalty = computeMultiMoveOpeningPiecePenalty(isWhite);
		int centralPawnsPushedBonus = computeCentralPawnsPushedBonus(isWhite);
		int bishopPairBonus = computeBishopPairBonus(isWhite);
		int connectedRooksBonus = computeConnectedRooksBonus(isWhite);
		int earlyQueenPenalty = computeEarlyQueenPenalty(isWhite);
		result = castlingBonus + centralPawnsPushedBonus + bishopPairBonus
				+ connectedRooksBonus + earlyQueenPenalty + multiMoveOpeningPiecePenalty;
		return result;
	}

	/**
	 * Returns the penalty weight of moving a queen from her home square early if
	 * she is not on her homesquare and we're less than 3 moves in.
	 * @param isWhite
	 * @return
	 */
	private int computeEarlyQueenPenalty(boolean isWhite) {
		
		// TODO: The AI still loves to move the queen out early...not sure this is working 100% right
		
		int result = 0;
		if (controller.getModel().getMoveList().size() < 16) {
			PieceArray pieces = findPieceList(isWhite);
			Piece queen = pieces.getQueen();
			if (queen != null) {
				int row = queen.getRow();
				int col = queen.getCol();

				
				int homeRow;
				if (isWhite)
					homeRow = Constants.getWhitePieceRow();
				else
					homeRow = Constants.getBlackPieceRow();
				
				if (!(col == Constants.getQueenCol() && row == homeRow))
					result = -Constants.getEarlyQueenPenaltyWeight();
//				if (result <0)
//					System.out.println("Queen penalty, " + queen.toString());
			}
		} else
			result = 0;
		
		
		return result;
	}

	/**
	 * This checks to see if any pawns are on the same column, and returns 
	 * the penalty cost if so.
	 * @param isWhite
	 * @return
	 */
	private int computeDoubledPawnsPenalty(boolean isWhite){
		
		// TODO: Implement this
		return 0;
	}
	
	/**
	 * If we are less than 30 ply into the game (or the first 15 moves), then
	 * this method will count how many times a piece has moved more than once,
	 * and multiply that times the penalty.  After 15 moves the opening is 
	 * considered over and this method will return 0.
	 * 
	 * Making tons of ArrayLists is too slow, the current implementation will
	 * need to be redone.  One option would be to add a boolean to the piece
	 * class (or an int) to reflect if it has moved more than once, or how many 
	 * times it has moved.
	 * 
	 * @param isWhite
	 * @return
	 */
	private int computeMultiMoveOpeningPiecePenalty(boolean isWhite){
		int result = 0;
		
		// TODO: Find a faster implementation
	//	ArrayList<Move>moveList = controller.getModel().getMoveList();
		
		// If we're not in the opening anymore, this isn't relevant anymore
//		if (moveList.size() > 30)
//			result = 0;
//		else{
//		int start = 0;
//		if(isWhite)
//			start = 0;
//		else
//			start = 1;
//		ArrayList<Piece>movedPieces = new ArrayList<Piece>();
//		for (int i = 0; (2*i+start) < moveList.size(); i++){
//			int index = 2*i + start;
//			
//			Piece piece = moveList.get(index).getPiece();
//			if (!movedPieces.contains(piece))
//			movedPieces.add(piece);
//			else
//				result++;
//		}
//		}
//	
		return -result * Constants.getMultiMoveOpeningPiecePenalty();
	}
	
	/**
	 * Checks to see if both rooks are alive and if they are connected, returning the relevant bonus if so.
	 * @param isWhite
	 * @return
	 */
	private int computeConnectedRooksBonus(boolean isWhite) {
		// TODO Implement this.
		int result = 0;
		Piece A_rook = findPieceList(isWhite).getA_rook();
		Piece H_rook = findPieceList(isWhite).getH_rook();
		
		// If either or both rooks are dead, no bonus
		if (A_rook == null || H_rook == null)
			result = 0;
		else{
			int A_rookCol = A_rook.getCol();
			int A_rookRow = A_rook.getRow();
			int H_rookCol = H_rook.getCol();
			int H_rookRow = H_rook.getRow();
			
			// If on same col
			if (A_rookCol == H_rookCol){
				int startRow = Math.min(A_rookRow, H_rookRow);
				boolean pathBlocked = false;
				for (int i = 1; i < Math.abs(A_rookRow - H_rookRow); i++)
					if (controller.getBoardController().getPieceByCoords(startRow + i, A_rookCol) != null)
						pathBlocked = true;
				if (!pathBlocked)
					result = Constants.getConnectedRooksBonusWeight();
			}
			// Else if on same row
			else if (A_rookRow == H_rookRow){

				int startCol = Math.min(A_rookCol, H_rookCol);
				boolean pathBlocked = false;
				for (int i = 1; i < Math.abs(A_rookCol - H_rookCol); i++)
					if (controller.getBoardController().getPieceByCoords(A_rookRow, startCol+i) != null)
						pathBlocked = true;
				if (!pathBlocked)
					result = Constants.getConnectedRooksBonusWeight();
			}
			// If not on same row or col, they are not connected
			else
				result = 0;
		}
		return result;
	}

	/**
	 * Returns a bonus for having both bishops, if they are both alive.
	 * @param isWhite
	 * @return
	 */
	private int computeBishopPairBonus(boolean isWhite) {

		PieceArray pieces = findPieceList(isWhite);
		int numBishops = 0;
		int result = 0;

		if (pieces.getPiece(PieceArray.C_bishopId)!= null)
			numBishops++;
		if (pieces.getPiece(PieceArray.F_bishopId)!= null)
			numBishops++;

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
		if ((piece == null) || (piece.getType()!=Constants.getPawnChar()))
			result += Constants.getCentralPawnsPushedBonusWeight();

		// Check Queen column pawn
		col = Constants.getQueenColumn();
		piece = controller.getBoardController().getPieceByCoords(pawnRow, col);
		if ((piece == null) || (piece.getType() != Constants.getPawnChar()))
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
		PieceArray pieceArray = findPieceList(isWhite);
		Piece king = null;

		king = pieceArray.getKing();

		return king;
	}

	
	/**
	 * Given a color <code>isWhite</code>, returns the arrayList of pieces associated
	 * with that color.
	 * @param isWhite
	 * @return
	 */
	public PieceArray findPieceList(boolean isWhite) {
		PieceArray pieceList = null;
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
	 * 
	 * @param depth
	 * @param previousMove
	 * @param parentNode
	 * @return
	 */
	@Deprecated
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
	 * Calculate how long the previous branch of the tree took to search and
	 * print to the console.
	 * 
	 * @param i
	 * @param parentNode
	 * @param startTime
	 */
	public void printTimeStats(int i, Node parentNode, long startTime) {
		long endTime = System.currentTimeMillis();
		System.out.println("AI.ChooseMove: " + ++i + " of "
				+ parentNode.getChildren().size()

				+ " branches searched. Time elapsed: " + (endTime - initTime)
				/ 1000.0 + " seconds, last branch took "
				+ (endTime - startTime) / 1000.0 + " seconds");

		startTime = System.currentTimeMillis();
	}
	
	public int getBranchCounter(){
		return branchCounter;
	}
	
	public int getNumBranches(){
		return numBranches;
	}
	
	/**
	 * Getter to let outside classes know if we are on a NullMoveBranch,
	 * since there will be a null move if that's the case.
	 * @return
	 */
	public boolean isNullMoveBranch(){
		return isNullMoveBranch;
	}
	
	public boolean isThinking(){
		return isThinking;
	}
}

/**
 * Simple comparator implementation so I can use the sort method in the
 * Collection class. This helps move ordering to get tight alpha/beta bounds
 * quickly
 * 
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