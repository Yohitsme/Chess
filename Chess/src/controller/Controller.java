package controller;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import utils.Utils;
import model.Model;
import model.Move;
import model.Piece;
import view.PiecePanel;
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

	/**
	 * Constructor
	 */
	public Controller() {
		model = new Model();
		ruleEngine = new RuleEngine(this);
		boardController = new BoardController(model);
		moveGenerator = new MoveGenerator(boardController, ruleEngine);
		masterListener = new MasterListener(this);
		view = new View(boardController, masterListener);
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

		Piece piece = boardController.getPieceByCoords(row, col);
		//Move move = new Move(piece, row, col, 0, 0);

		ArrayList<Move> legalMoves = moveGenerator.findMoves(row,col);
		
		for (Move move: legalMoves)
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
	 * Runs the chess game
	 * 
	 * @param arg
	 */
	public static void main(String[] arg) {
		Controller controller = new Controller();
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

		MouseEvent pressEvent = masterListener.getPressEvent();
		int startRow = computeRowFromMouseEvent(pressEvent);
		int startCol = computeColFromMouseEvent(pressEvent);
		int endRow = computeRowFromMouseEvent(e);
		int endCol = computeColFromMouseEvent(e);
		boolean withinBounds = isWithinBounds(endRow, endCol);
		Piece piece = boardController.getPieceByCoords(startRow, startCol);
		
		if (piece != null && withinBounds) {
			Move move = new Move(piece, startRow, startCol, endRow, endCol);
			if (RuleEngine.validateMove(move, boardController, true)) {
				
				// Check for special cases, such as pawn promotes, en passant captures
				handleSpecialCases(move);
				
				// Remove piece from it's list in model if a capture occurred
				updatePieceLists(move);

				// Move the piece
				boardController.setPieceByCoords(endRow, endCol, piece);
				piece.setCol(endCol);
				piece.setRow(endRow);
				
				// Mark the piece has having moved
				boardController.getPieceByCoords(move.getStartRow(),
						move.getStartCol()).setHasMoved(true);

				boardController.clearSquare(startRow, startCol);
				model.getMoveList().add(move);

				System.out
						.println("Controller.handleMouseRelease: Valid Move: " + move.algebraicNotationPrint());
		

			} else
				System.out
						.println("Controller.handleMouseRelease: Invalid move. Board not modified.");

		}
		System.out.println("========================================================");
	
		view.removeHighlights();
		view.update();
		
//		printLegalMoves();
//		printTeams();
//		int m = 1;
//		ArrayList<Move> list = new ArrayList<Move>();
//		for (int i = 0; i < 8; i++){
//			for (int j = 0; j < 8; j++)
//				if (boardController.getPieceByCoords(i,j) != null &&boardController.getPieceByCoords(i,j).isWhite()){
//			list.addAll(moveGenerator.findMoves(i,j));
//		
//		}}
//			for (Move move: list)
//				System.out.println("Legal move" + "[" + m++ +"]: " + move.algebraicNotationPrint());
//		
	}

	private boolean isWithinBounds(int endRow, int endCol) {
		return (endRow >=0 && endRow <=7 && endCol >= 0 && endCol <=7);
		}

	/**
	 * Calls methods to check and handle edge case moves
	 * <li> En Passant captures
	 * <li> Pawn promotions
	 * <li> Castling
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
	public void printLegalMoves(){
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		
		
		for (int row = 0; row < 8; row++){
			for (int col = 0; col < 8; col++){
				legalMoves.addAll(moveGenerator.findMoves(row,col));
			}
		}
		
		for (Move move: legalMoves)
			System.out.println(move.toString());
	}
	
	/**
	 * If parameter move was a kingside or queenside castle, this method moves the rook to the proper square
	 * @param move
	 */
	private void handleCastling(Move move) {
		if (move.getPiece().getType().equals("king") && RuleEngine.calculateDeltaColUnsigned(move) ==2){
			if (RuleEngine.calculateDeltaColSigned(move) == 2){
				Piece rook = boardController.getPieceByCoords(move.getStartRow(), 7);
				boardController.setPieceByCoords(move.getStartRow(), 5, rook);
				rook.setCol(5);
				boardController.clearSquare(move.getStartRow(), 7);
			}else{
				Piece rook = boardController.getPieceByCoords(move.getStartRow(), 0);
				boardController.setPieceByCoords(move.getStartRow(), 3, rook);
				rook.setCol(3);
				boardController.clearSquare(move.getStartRow(), 0);
			}
				
		}
		
		
	}

	/**
	 * If parameter move was a pawn being moved to the first or last rank, this method prompts the user
	 * for a piece type and turns the pawn into the type chosen by the user
	 * @param move
	 */
	private void handlePawnPromote(Move move) {
		if (move.getPiece().getType().equals("pawn") && (move.getEndRow() == 7 || move.getEndRow() == 0)){
			String choice = getPawnPromoteChoice();
			move.getPiece().setType(choice);
		}
		
	}

	/**
	 * Prompts the user for the type of piece they want to promote their pawn to, and returns a string of the name
	 * of that type.  
	 * @return
	 */
	private String getPawnPromoteChoice() {
		// TODO Auto-generated method stub
		Object[] options = {"queen", "rook","knight","bishop"};
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
		for (Piece piece: model.getWhitePieces())
			System.out.println("-" + piece.toString());
		

		System.out.println("Controller.printTeams: Black pieces remaining: ");
		for (Piece piece: model.getBlackPieces())
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
			Move previousMove = model.getMoveList().get(size - 1);

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
	 * @param move
	 */
	public void removePieceFromList(Move move) {

		Piece piece = boardController.getPieceByCoords(move.getEndRow(),
				move.getEndCol());

		if (piece.isWhite()) {
			model.getWhitePieces().remove(piece);
		} else
			model.getBlackPieces().remove(piece);

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
			result = 7-result;
		
		
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

	/**
	 * Processes action events
	 * @param e
	 */
	public void handleActionEvent(ActionEvent e) {
		if (e.getActionCommand().equals("newGame")){
			System.out.println("Controller.handleActionEvent: ResettingGame");
			model.resetModel();
			view.update();
		}
		else if(e.getActionCommand().equals("flipBoard")){
			view.flipBoard();
		}
		else
			System.out.println("Controller.handleActionEvent: Action command /'" + e.getActionCommand() + "/' not recognized");
			
		
	}

}
