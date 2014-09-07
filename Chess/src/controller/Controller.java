package controller;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.Model;
import model.Move;
import model.Node;
import model.Piece;
import utils.Constants;
import utils.Log;
import utils.Utils;
import view.View;

/**
 * Controller class that runs the game and handles logic for user interaction
 * 
 * @author Matthew
 * 
 */
public class Controller {

	View view;
	Model model;
	BoardController boardController;
	MasterListener masterListener;
	RuleEngine ruleEngine;
	MoveGenerator moveGenerator;
	GameTreeController gameTreeController;
	AI AI;
	Log log;

	/**
	 * Runs the chess game
	 * 
	 * @param arg
	 */
	public static void main(String[] arg) {
		Controller controller = null;
		// try {
		controller = new Controller();

//		for (int depth = 0; depth < 9; depth++) {
//			long startTime = System.currentTimeMillis();
//			int nodes = controller.AI.perft(depth,true);
//			long endTime = System.currentTimeMillis();
//
//			System.out.println("Peft " + (depth + 1) + ": "
//					+nodes + ": " + (endTime - startTime) / 1000.0 + " seconds");
//		}
		// }

		// catch (Exception e) {
		// controller.getLog().error(e.toString());
		// controller.getLog().error(e.getStackTrace().toString());
		//
		// String moveListDump = "";
		// for (Move move : controller.getModel().getMoveList())
		// moveListDump += move.algebraicNotationPrint() + "\n";
		//
		// controller.getLog().error(moveListDump);
		// }

	}

	/**
	 * Constructor
	 */
	public Controller() {
		model = new Model();
		ruleEngine = new RuleEngine(this);
		boardController = new BoardController(model);
		moveGenerator = new MoveGenerator(boardController, ruleEngine, this);
		masterListener = new MasterListener(this);
		view = new View(boardController, masterListener,
				model.getCapturedPieces());
		gameTreeController = new GameTreeController(model.getGameTree(), this);
		AI = new AI(this);
		long startTime = System.currentTimeMillis();
		log = new Log();

	}

	/**
	 * Sets drag icon to that of the square where mouse was pressed, clears the
	 * square where mouse was pressed
	 * 
	 * @param e
	 */
	public void handleMousePress(MouseEvent e) {
		int row = computeRowFromMouseEvent(e);
		int col = computeColFromMouseEvent(e);
		view.updateDragLabelIcon(row, col);

		handleMouseDrag(e);
		view.clearSelectedPiece(row, col);
		if (boardController.getPieceByCoords(row, col) != null)
			highlightLegalMoves();
	}

	/**
	 * Puts a blue highlight on any square the currently selected piece can
	 * legally move to.
	 */
	private void highlightLegalMoves() {
		// TODO Auto-generated method stub
		MouseEvent e = masterListener.getPressEvent();

		int row = computeRowFromMouseEvent(e);
		int col = computeColFromMouseEvent(e);

		ArrayList<Move> legalMoves = moveGenerator.findMoves(row, col);

		for (Move move : legalMoves)
			view.highlightSquare(move.getEndRow(), move.getEndCol());

	}

	/**
	 * Calculates position of mouse on screen, puts dragIcon at that point
	 * 
	 * @param e
	 */
	public void handleMouseDrag(MouseEvent e) {
		view.moveDraggedPiece(e.getX() - 40, e.getY() - 40);
	}

	/**
	 * Checks if the move was valid, if so, moves the piece to that spot, clears
	 * the old one. Otherwise, board is returned to it's state before the move
	 * was attempted
	 * 
	 * @param e
	 */
	public void handleMouseRelease(MouseEvent e) {
		view.clearDragLabelIcon();
		boolean gameOver = false;
		MouseEvent pressEvent = masterListener.getPressEvent();
		int startRow = computeRowFromMouseEvent(pressEvent);
		int startCol = computeColFromMouseEvent(pressEvent);
		int endRow = computeRowFromMouseEvent(e);
		int endCol = computeColFromMouseEvent(e);
		boolean iswithinBounds = isWithinBounds(endRow, endCol);
		Piece piece = boardController.getPieceByCoords(startRow, startCol);
		Move move = new Move(piece, startRow, startCol, endRow, endCol);

		if (isAcceptableInput(piece) && piece != null && iswithinBounds)
			processMoveAttempt(move);

		System.out.println("================================================");

		view.removeHighlights();
		view.update();

		gameOver = isGameOver();

		if (gameOver)
			JOptionPane.showMessageDialog(new JFrame(), "Game over!");

		if (isAIturn() && !gameOver) {
			processMove(AI.move(computeTurn()));
		}

		// view.updateAnalysisPanel(new JTree(gameTreeController.getRoot()));
		view.updateMoveListPanel(model.getMoveList());
		view.update();

		// gameTreeController.getRoot().removeAllChildren();
		if (isGameOver())
			JOptionPane.showMessageDialog(new JFrame(), "Game over!");

		if (model.getMoveList().size() != 0)
			view.highlightPreviousMove(model.getMoveList());
		/*
		 * long startTime = System.currentTimeMillis(); long endTime =
		 * System.currentTimeMillis(); System.out.println(
		 * "Controller.Controller(): Done printing. Time elapsed: " +
		 * (endTime-startTime)/1000.0 +" seconds");
		 */
		boolean printFlag = true;
		AI.evaluate(computeTurn().equals("white") ? true : false,
				gameTreeController.getRoot(), printFlag);

	}

	/**
	 * Checks to see if the attempted move was valid, and processes it if so.
	 * Invalid moves are ignored.
	 * 
	 * @param move
	 */
	public void processMoveAttempt(Move move) {
		boolean moveFound = false;
		if (RuleEngine.validateMove(move, boardController, true)) {
			Node root = gameTreeController.getRoot();
			if (root.getChildren().size() != 0)
				for (Node node : root.getChildren())
					if (node.getMove().equals(move)) {
						moveFound = true;
						processMove(node);
					}
			if (!moveFound) {
				processMove(new Node(move));
				System.out
						.println("Controller.processMoveAttempt: couldn't find it");
			}
		} else
			System.out
					.println("Controller.handleMouseRelease: Invalid move. Board not modified.");
	}

	/**
	 * Assumes that the move is valid and updates the move on the board
	 * 
	 * @param move
	 */
	public void processMove(Node node) {
		Move move = node.getMove();
		model.getMoveList().add(move);
		// Check for special cases, such as pawn promotes, en
		// passant captures
		handleSpecialCases(move);

		// Remove piece from it's list in model if a capture
		// occurred
		updatePieceLists(move);

		// Move the piece
		boardController.setPieceByCoords(move.getEndRow(), move.getEndCol(),
				move.getPiece());
		move.getPiece().setCol(move.getEndCol());
		move.getPiece().setRow(move.getEndRow());

		if (boardController.getPieceByCoords(move.getStartRow(),
				move.getStartCol()) == null) {
			System.out
					.println("Controller.processMove ERROR: Trying to move a null piece? A move probably wasn't selected from the search, so the same move that was picked last time is now being picked.");
			System.out.println("Controller.processMove: " + move.toString());
		}
		// Mark the piece has having moved
		boardController
				.getPieceByCoords(move.getStartRow(), move.getStartCol())
				.setHasMoved(true);

		boardController.clearSquare(move.getStartRow(), move.getStartCol());

		gameTreeController.setRoot(node);
		node.setParent(null);
	}

	/**
	 * Returns true if it is the human's turn and a piece was moved that is the
	 * color of the current player
	 * 
	 * @param piece
	 * @return
	 */
	public boolean isAcceptableInput(Piece piece) {
		String currentPlayerColor = computeTurn();
		boolean result = false;

		if (piece != null) {
			if (currentPlayerColor.equals("white") && piece.isWhite()
					&& !isWhiteAI()) {
				result = true;
			} else if (currentPlayerColor.equals("black") && !piece.isWhite()
					&& !isBlackAI())
				result = true;
		}
		return result;
	}

	/**
	 * Returns true if it is the AI's turn
	 * 
	 * @return
	 */
	public boolean isAIturn() {
		boolean result = false;
		String currentPlayerColor = computeTurn();
		if (currentPlayerColor.equals("white") && isWhiteAI()) {
			result = true;
		} else if (currentPlayerColor.equals("black") && isBlackAI())
			result = true;

		return result;

	}

	/**
	 * Returns true if white or black is in checkmate or stalemate.
	 * 
	 * @return
	 */
	public boolean isGameOver() {

		boolean result = false;

		boolean isWhite = true;

		if (isBlackCheckmated() || isWhiteCheckmated()
				|| moveGenerator.isStalemated(isWhite)
				|| moveGenerator.isStalemated(!isWhite))
			result = true;

		return result;
	}

	/**
	 * Returns true if white is in check and has no legal moves
	 * 
	 * @return
	 */
	public boolean isWhiteCheckmated() {
		ArrayList<Piece> white = model.getWhitePieces();
		Piece king = null;
		boolean inCheck = false;
		boolean result = false;

		for (Piece piece : white) {
			if (piece.getType().equals("king"))
				king = piece;
		}

		if (king == null)
			result = true;
		else{
		
		if (RuleEngine.isAttackedSquare(king.getRow(), king.getCol(), "black"))
			inCheck = true;

		ArrayList<Move> list = new ArrayList<Move>();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++)
				if (boardController.getPieceByCoords(row, col) != null
						&& boardController.getPieceByCoords(row, col).isWhite()) {
					list.addAll(moveGenerator.findMoves(row, col));

				}
		}

		result = inCheck && (list.isEmpty());
		}
		return result;
	}

	/**
	 * Returns true if black is in check and has no legal moves
	 * 
	 * @return
	 */
	public boolean isBlackCheckmated() {
		ArrayList<Piece> black = model.getBlackPieces();
		Piece king = null;
		boolean inCheck = false;
		boolean result = false;

		for (Piece piece : black) {
			if (piece.getType().equals("king"))
				king = piece;
		}
		if (king == null)
			result = true;
		else{
		if (RuleEngine.isAttackedSquare(king.getRow(), king.getCol(), "white"))
			inCheck = true;

		ArrayList<Move> list = new ArrayList<Move>();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++)
				if (boardController.getPieceByCoords(row, col) != null
						&& !boardController.getPieceByCoords(row, col)
								.isWhite()) {
					list.addAll(moveGenerator.findMoves(row, col));

				}
		}

		result = inCheck && (list.isEmpty());
		}
		return result;
	}

	private boolean isWithinBounds(int endRow, int endCol) {
		return (endRow >= 0 && endRow <= 7 && endCol >= 0 && endCol <= 7);
	}

	/**
	 * Calls methods to check and handle edge case moves <li>En Passant captures
	 * <li>Pawn promotions <li>Castling
	 * 
	 * @param move
	 */
	private void handleSpecialCases(Move move) {
		handleEnPassantCaptures(move);
		handlePawnPromote(move);
		handleCastling(move);
	}

	/**
	 * Calls move generator and prints all the legal moves that it finds
	 */
	public void printLegalMoves() {
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				legalMoves.addAll(moveGenerator.findMoves(row, col));
			}
		}

		for (Move move : legalMoves)
			System.out.println(move.toString());
	}

	/**
	 * If parameter move was a kingside or queenside castle, this method moves
	 * the rook to the proper square
	 * 
	 * @param move
	 */
	public void handleCastling(Move move) {
		if (move.getPiece().getType().equals("king")
				&& RuleEngine.calculateDeltaColUnsigned(move) == 2) {
			if (RuleEngine.calculateDeltaColSigned(move) == 2) {
				Piece rook = boardController.getPieceByCoords(
						move.getStartRow(), 7);
				boardController.setPieceByCoords(move.getStartRow(), 5, rook);
				rook.setCol(5);
				boardController.clearSquare(move.getStartRow(), 7);
			} else {
				Piece rook = boardController.getPieceByCoords(
						move.getStartRow(), 0);
				boardController.setPieceByCoords(move.getStartRow(), 3, rook);
				rook.setCol(3);
				boardController.clearSquare(move.getStartRow(), 0);
			}

		}

	}

	/**
	 * If parameter move was a pawn being moved to the first or last rank, this
	 * method prompts the user for a piece type and turns the pawn into the type
	 * chosen by the user. If it's the AI's move, default to a queen.
	 * 
	 * @param move
	 */
	private void handlePawnPromote(Move move) {

		// If it's a user move, the piece type will be a pawn
		if (move.getPiece().getType().equals("pawn")
				&& (move.getEndRow() == 7 || move.getEndRow() == 0)) {
			String choice = "";

			if (isAIturn())
				choice = move.getPromotePiece();
			else
				choice = getPawnPromoteChoice();
			move.getPiece().setType(choice);
		}

	}

	/**
	 * Prompts the user for the type of piece they want to promote their pawn
	 * to, and returns a string of the name of that type.
	 * 
	 * @return
	 */
	private String getPawnPromoteChoice() {
		// TODO Auto-generated method stub
		Object[] options = { "queen", "rook", "knight", "bishop" };
		int selection = -1;

		while (selection == -1)
			selection = JOptionPane.showOptionDialog(new JFrame(),
					"Select what type of piece to promote you pawn to",
					"Pawn Promotion", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		return (String) options[selection];
	}

	/**
	 * Prints all pieces on each team that remain
	 */
	private void printTeams() {
		System.out.println("Controller.printTeams: White pieces remaining: ");
		for (Piece piece : model.getWhitePieces())
			System.out.println("-" + piece.toString());

		System.out.println("Controller.printTeams: Black pieces remaining: ");
		for (Piece piece : model.getBlackPieces())
			System.out.println("-" + piece.toString());
	}

	/**
	 * If the move was a capture, remove the captured piece from it's list
	 * 
	 * @param move
	 */
	private void updatePieceLists(Move move) {
		if (boardController
				.getPieceByCoords(move.getEndRow(), move.getEndCol()) != null)
			removePieceFromList(move);

	}

	/**
	 * If execution gets to this method, then Move parameter move is valid.
	 * Check if it was an en passant capture. If it is a valid en passant
	 * capture, then we can clear the square that the previous move ended on,
	 * because it had to have been where the captured pawn ended up.
	 * 
	 * Returns a reference to the pawn that got captured, if one exists
	 * 
	 * @param move
	 */
	public Piece handleEnPassantCaptures(Move move) {
		Piece pawnCaptured = null;

		if (move.getPiece().getType().equals("pawn")
				&& move.getStartCol() != move.getEndCol()
				&& boardController.getPieceByCoords(move.getEndRow(),
						move.getEndCol()) == null) {
			int size = model.getMoveList().size();

			// This method only gets called after the passant move has been
			// added to the move list,
			// so we need to look 2 moves back to fine the move where the enemy
			// pawn moved 2 squares.
			Move previousMove = null;
			if (AI.isNullMoveBranch())
			previousMove = model.getMoveList().get(size - 3);
			else
			previousMove = model.getMoveList().get(size - 2);

			if (null == boardController.getPieceByCoords(previousMove.getEndRow(),
					previousMove.getEndCol()))
				System.out.println("Error");
				removePieceFromList(previousMove);

			pawnCaptured = previousMove.getPiece();

			boardController.clearSquare(previousMove.getEndRow(),
					previousMove.getEndCol());
		}
		return pawnCaptured;

	}

	/**
	 * Removes the piece on the destination square of Move parameter move from
	 * it's black/white piece list in the model.
	 * 
	 * Note: This method gets called eventually when checking to see how many
	 * moves a player has form a given position. If the player can check their
	 * opponent, then capturing the enemy king is a legal move from that
	 * position (even though it is not that player's turn). The end result is
	 * that the king might get "captured" by this method, but it's not because
	 * it's possible, the engine is just seeing if that is a legal move from the
	 * position (assuming it were able to move).
	 * 
	 * @param move
	 */
	public void removePieceFromList(Move move) {

		Piece piece = boardController.getPieceByCoords(move.getEndRow(),
				move.getEndCol());

		if (piece == null)
			log.error("Controller.removePieceFromList: Removing null piece?");

		if (piece.isWhite()) {
			model.getWhitePieces().remove(piece);
		} else
			model.getBlackPieces().remove(piece);

		model.getCapturedPieces().add(piece);

	}

	public int computeColFromMouseEvent(MouseEvent e) {

		int result = e.getX() / 80;
		return result;
	}

	public int computeRowFromMouseEvent(MouseEvent e) {

		boolean isFlipped;

		if (view.getBoardOrientation().equals("normal"))
			isFlipped = false;
		else
			isFlipped = true;

		int result = (640 - e.getY()) / 80;

		if (isFlipped)
			result = 7 - result;

		return result;
	}

	public Model getModel() {
		return model;
	}

	public BoardController getBoardController() {
		return boardController;
	}

	public void setBoardController(BoardController boardController) {
		this.boardController = boardController;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	/**
	 * Processes action events
	 * 
	 * @param e
	 */
	public void handleActionEvent(ActionEvent e) {
		if (e.getActionCommand().equals("newGame")) {
			System.out.println("Controller.handleActionEvent: ResettingGame");
			model.resetModel();
			view.update();
		} else if (e.getActionCommand().equals("flipBoard")) {
			view.flipBoard();
		} else if (e.getActionCommand().equals("changeGameMode")) {
			String choice = promptForGameMode();
			if (choice != null) {
				model.setGameMode(choice);
				// model.resetModel();
				view.update();
			}
		} else if (e.getActionCommand().equals("exportMoveList")) {
			exportMoveList();
		} else if (e.getActionCommand().equals("tuneEngine")) {
			promptUserForNewWeights();
		} else if (e.getActionCommand().equals("adjustDepth")) {
			promptUserForNewDepth();
		} else
			System.out
					.println("Controller.handleActionEvent: Action command /'"
							+ e.getActionCommand() + "/' not recognized");

	}

	private void promptUserForNewDepth() {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame();
		Object[] possibilities = { "1", "2", "3", "4", "5" };
		String s = (String) JOptionPane
				.showInputDialog(
						frame,
						"Note: Clicking OK to confirm changes will restart the game"
								+ "\n\nChoose the depth to which the engine will search",
						"Tune Engine", JOptionPane.PLAIN_MESSAGE, null,
						possibilities, "4");

		if (s != null && s.length() > 0) {
			int i = new Integer(s);
			Constants.setDepth(i);
			model.resetModel();
			view.update();
			AI.resizeKillerMoveArrays();
		}
	}

	private void promptUserForNewWeights() {
		JPanel panel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(boxLayout);
		JTextField materialWeightInput = new JTextField(3);
		JTextField positionalWeightInput = new JTextField(3);
		JTextField bonusWeightInput = new JTextField(3);

		String info = "-Use decimal values to represent percents: 0.8 instead of 80%"
				+ "\n-Default values: Material 0.8, Positional and Bonus 0.1"
				+ "\n-Make sure all values add up to 1.0 (aka 100%)"
				+ "\n-Bonus Weight includes castling, bishop pair, and central pawn pushes\n";
		JTextArea textArea = new JTextArea(info);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		panel.add(textArea);

		JPanel materialPanel = new JPanel((LayoutManager) new FlowLayout(
				FlowLayout.LEFT));
		materialPanel.add(new JLabel("Material Weight: "));
		materialPanel.add(materialWeightInput);
		// materialPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(materialPanel);

		JPanel positionalPanel = new JPanel((LayoutManager) new FlowLayout(
				FlowLayout.LEFT));
		positionalPanel.add(new JLabel("Positional Weight: "));
		positionalPanel.add(positionalWeightInput);
		// positionalPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.add(positionalPanel);

		JPanel bonusPanel = new JPanel((LayoutManager) new FlowLayout(
				FlowLayout.LEFT));
		bonusPanel.add(new JLabel("Bonus Weight: "));
		bonusPanel.add(bonusWeightInput);
		// bonusPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.add(bonusPanel);
		panel.revalidate();
		panel.repaint();

		JOptionPane.showConfirmDialog(null, panel, "Evaluation Weight Tuning",
				JOptionPane.OK_CANCEL_OPTION);

		try {
			double bonusWeight = new Double(bonusWeightInput.getText());
			double materialWeight = new Double(materialWeightInput.getText());
			double positionalWeight = new Double(
					positionalWeightInput.getText());

			Constants.setBonusScoreWeight(bonusWeight);
			Constants.setMaterialScoreWeight(materialWeight);
			Constants.setPositionalScoreWeight(positionalWeight);
		} catch (NumberFormatException numberFormatException) {
			JOptionPane.showMessageDialog(new JFrame(),
					"Invalid input. Weights not modified.");
		}

	}

	private void exportMoveList() {

		ArrayList<Move> moveList = model.getMoveList();
		String fileName = "Chess Game Move List Export - "
				+ Utils.getTimeNoSpaces() + ".txt";

		String body = "Game played on " + Utils.getTime();
		body += "\n\nEngine stats:\n";
		body += "\nDepth: " + Constants.getDepth();
		body += "\nPositional Scoring weight: "
				+ Constants.getPositionalScoreWeight();
		body += "\nMaterial Scoring weight: "
				+ Constants.getMaterialScoreWeight();
		body += "\nBonus Scoring weight: " + Constants.getBonusScoreWeight();
		body += "\n";

		for (int i = 0; i < moveList.size(); i++) {
			if (i % 2 == 0)
				body += "\n" + Integer.toString(i / 2 + 1) + " ";

			body += moveList.get(i).algebraicNotationPrint() + " ";
		}

		Utils.writeToFile(fileName, body);

	}

	public String promptForGameMode() {

		// TODO Auto-generated method stub
		String[] choices = { "pVp", "pVc", "cVp", "cVc" };
		String result = null;
		Object[] options = { "Player Vs Player", "Player Vs Computer",
				"Computer Vs Player", "Computer Vs Computer" };

		int selection = JOptionPane.showOptionDialog(new JFrame(),
				"Select a game mode (this will restart the game)", "Game Mode",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);

		if (selection != -1)
			result = choices[selection];
		return result;

	}

	/**
	 * Returns "white" if it is white's turn, and "black" if it is black's turn
	 * 
	 * @return
	 */
	public String computeTurn() {
		String result;
		int turnNumber = model.getMoveList().size();

		if ((turnNumber % 2) == 1)
			result = "black";
		else
			result = "white";
		return result;
	}

	/**
	 * Returns true if white is played by the computer
	 */
	public boolean isWhiteAI() {
		boolean result = false;
		if (model.getGameMode().equals("cVp")
				|| model.getGameMode().equals("cVc"))
			result = true;

		return result;
	}

	/**
	 * Returns true if black is played by the computer
	 * 
	 * @return
	 */
	public boolean isBlackAI() {
		boolean result = false;
		if (model.getGameMode().equals("cVc")
				|| model.getGameMode().equals("pVc"))
			result = true;

		return result;

	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public MasterListener getMasterListener() {
		return masterListener;
	}

	public void setMasterListener(MasterListener masterListener) {
		this.masterListener = masterListener;
	}

	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}

	public void setRuleEngine(RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

	public MoveGenerator getMoveGenerator() {
		return moveGenerator;
	}

	public void setMoveGenerator(MoveGenerator moveGenerator) {
		this.moveGenerator = moveGenerator;
	}

	public AI getAI() {
		return AI;
	}

	public void setAI(AI aI) {
		AI = aI;
	}

	public boolean isDrawByThreefoldRepitition() {
		boolean result = true;

		if (model.getMoveList().size() < 11)
			result = false;
		else {
			int size = model.getMoveList().size();
			ArrayList<Move> moveList = model.getMoveList();
			for (int i = 0; i < 5; i++) {
				if (!moveList.get(size - 1 - i).equals(
						moveList.get(size - 4 - 1 - i)))
					result = false;

			}

		}

		return result;
	}
}
