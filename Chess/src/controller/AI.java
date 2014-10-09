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

	public AI(Controller controllerIn) {
		this.controller = controllerIn;

		for (int i = 0; i < Constants.getDepth() + 1; i++)
			killerMoves.add(new ArrayList<Move>());
		PV = new Node[10];

		for (int i = 0; i < 10; i++)
			PV[i] = null;

		nodesPerLevel = new int[20];
	}

	// Color is the side to play
	public Node move(String color) {
		Node node = null;

		boolean isWhiteTurn = color.equals("white") ? true : false;
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
		return node;
	}

	public void chooseMove(boolean isWhiteTurn) {

		double alpha = -1000000001.0;
		double beta = 1000000001.0;

		Node parentNode = controller.gameTreeController.root;
		initializeKillerMoveArrays();

		for (int depth = 1; depth <= Constants.getDepth(); depth++) {
			this.depth = depth;

			if (depth == Constants.getDepth())
				System.out.println();
			pvSearch(alpha, beta, depth, isWhiteTurn, parentNode);

			masterPV = new ArrayList<Node>();
			masterPV.addAll(this.localPV);
			// for (int i = 0; i < 10; i++)
			// if (PV[i] != null)
			// System.out.println(PV[i].getMove().algebraicNotationPrint());

		}
		// this.depth = Constants.getDepth();
		// alphaBeta(alpha, beta, depth, isWhite, parentNode);
		//
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

		
		for (int r = 0; r < 8; r++){
			for (int c = 0; c < 8; c++)
				if (controller.getBoardController().getPieceByCoords(r, c)!=null)
				if (controller.getBoardController().getPieceByCoords(r, c).getRow()!= r ||controller.getBoardController().getPieceByCoords(r, c).getCol()!= c )
					System.out.println("AI.pvSearch: BOARD OUT OF SYNC ERROR");
		}
		
		// If Check mate/draw/king got captured
		
		if (controller.isWhiteCheckmated()){
			score = -Constants.getCheckMateScore() ;
		}else if (controller.isBlackCheckmated()){
			score = Constants.getCheckMateScore();
		}else if (controller.isDrawByThreefoldRepitition())
			return Constants.getDrawScore();
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
				printTimeStats(i++, parentNode, startTime);
				startTime = System.currentTimeMillis();
			}

			Node node = parentNode.getChildren().get(j);
			Move move = node.getMove();

			if (move.getEndRow() == 5 && move.getEndCol() == 5 && move.getPiece().getType() == 'q' && depthleft==2)
				System.out.println("PV Search queen move 55");
			
			if (move.getEndRow() == 2 && move.getEndCol() == 2 && move.getStartCol() == 5 && move.getPiece().getType() == 'q' && depthleft == 2)
				System.out.println("PVSearch checking queen move");
				
			Piece capturedPiece = RuleEngine.processMove(move);
			if (capturedPiece != null && capturedPiece.getType()== 'k')
				System.out.println("PVSearch removing king");
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
//					if (depthleft == Constants.getDepth()) {
//						System.out.println("AI.pvSearch: Researching....expensive!");
//					}
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
				}
			}

			bSearchPv = false;
		}

		if (parentNode.getChildren().size() == 0) {
			// If I have no moves assume I was checkmated and return low alpha
			// value

			alpha = -1000000000.0;
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
		parent.getChildren().remove(child);
		parent.getChildren().add(0, child);
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
		
//		if (isWhiteTurn == parentNode.getMove().getPiece().isWhite())
//			System.out.println("Houston we have a problems");
		
		
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
		Collections.sort(nodes, new NodeComparator());

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

		// TODO: Bonus for not moving the queen early on
		// TODO: Bonus for not moving the same piece twice in the opening
		// TODO: Bonus for not having a knight on the edge of the board
		// TODO: Bonus for connected pawns
		// TODO: Penalty for doubled pawns
		// TODO: Penalty for isolated pawns

		int castlingBonus = computeCastlingBonus(king);
		int centralPawnsPushedBonus = computeCentralPawnsPushedBonus(isWhite);
		int bishopPairBonus = computeBishopPairBonus(isWhite);
		int connectedRooksBonus = computeConnectedRooksBonus(isWhite);
		int earlyQueenPenalty = computeEarlyQueenPenalty(isWhite);
		result = castlingBonus + centralPawnsPushedBonus + bishopPairBonus
				+ connectedRooksBonus + earlyQueenPenalty;
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
		if (controller.getModel().getMoveList().size() < 6) {
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
	 * Checks to see if both rooks are alive and if they are connected, returning the relevant bonus if so.
	 * @param isWhite
	 * @return
	 */
	private int computeConnectedRooksBonus(boolean isWhite) {
		// TODO Implement this.
		return 0;
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
	
	/**
	 * Getter to let outside classes know if we are on a NullMoveBranch,
	 * since there will be a null move if that's the case.
	 * @return
	 */
	public boolean isNullMoveBranch(){
		return isNullMoveBranch;
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