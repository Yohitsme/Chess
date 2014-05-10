package controller;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

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

	/**
	 * Constructor
	 */
	public Controller() {
		model = new Model();
		boardController = new BoardController(model);
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
		Move move = new Move(piece, row, col, 0, 0);

		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				move.setEndCol(j);
				move.setEndRow(i);
				if (RuleEngine.validateMove(move, boardController))
					view.highlightSquare(i, j);
			}

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
		final int imgSideLength = 80;

		MouseEvent pressEvent = masterListener.getPressEvent();
		int startRow = computeRowFromMouseEvent(pressEvent);
		int startCol = computeColFromMouseEvent(pressEvent);
		int endRow = computeRowFromMouseEvent(e);
		int endCol = computeColFromMouseEvent(e);
		Piece piece = boardController.getPieceByCoords(startRow, startCol);

		if (piece != null) {
			Move move = new Move(piece, startRow, startCol, endRow, endCol);
			if (RuleEngine.validateMove(move, boardController)) {
				if (boardController.setPieceByCoords(endRow, endCol, piece)) {
					boardController.getPieceByCoords(move.getStartRow(),
							move.getStartCol()).setHasMoved(true);
					boardController.clearSquare(startRow,startCol);
					System.out
							.println("Controller.handleMouseRelease: Valid Move. Setting "
									+ piece.toString()
									+ " to row "
									+ endRow
									+ " and col " + endCol);
				} else
					System.out
							.println("Controller.handleMouseRelease: setPieceByCoords returned false, board not modified.");
			} else
				System.out
						.println("Controller.handleMouseRelease: Invalid move. Board not modified.");

		}
		view.removeHighlights();
		view.update();
	}

	public int computeColFromMouseEvent(MouseEvent e) {

		int result = e.getX() / 80;
		return result;
	}

	public int computeRowFromMouseEvent(MouseEvent e) {

		int result = (640 - e.getY()) / 80;
		return result;
	}

}
