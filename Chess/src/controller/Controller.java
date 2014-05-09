package controller;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import utils.Utils;
import model.Model;
import model.Move;
import view.PiecePanel;
import view.View;

/**
 * Controller class that runs the game and handles logic for user interaction
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
	public Controller(){
		model = new Model();
		boardController = new BoardController(model);
		masterListener = new MasterListener(this);
		view = new View(boardController, masterListener);
	}
	
	/**
	 * Sets drag icon to that of the square where mouse was pressed, clears the square where mouse was pressed
	 * @param e
	 */
	public void handleMousePress(MouseEvent e){
		view.updateDragLabelIcon(((PiecePanel) e.getSource()).getRow(), ((PiecePanel) e.getSource()).getCol());
		handleMouseDrag(e);
		view.clearSelectedPiece(masterListener.getPiecePanelPressed().getRow(), masterListener.getPiecePanelPressed().getCol());
		highlightLegalMoves();
	}
	
	/**
	 * Puts a blue highlight on any square the currently selected piece can legally move to.
	 */
	private void highlightLegalMoves() {
		// TODO Auto-generated method stub
		PiecePanel panelSelected =masterListener.getPiecePanelPressed(); 
		Move move = new Move(panelSelected.getPieceRepresented(), panelSelected.getRow(),panelSelected.getCol(),0,0 );
		
		for (int row = 0; row < 8; row++)
			for (int col = 0; col< 8; col++){
				move.setEndCol(col);
				move.setEndRow(row);
				if (RuleEngine.validateMove(move, boardController))
					view.highlightSquare(row,col);
			}
	
	}

	/**
	 * Calculates position of mouse on screen, puts dragIcon at that point
	 * @param e
	 */
	public void handleMouseDrag(MouseEvent e){
		
	
		final int imgSideLength = ((PiecePanel) e.getSource()).getLabel().getIcon().getIconHeight();
		int col = ((PiecePanel) e.getSource()).getCol();
		int row = ((PiecePanel) e.getSource()).getRow();
		
		int x = col*imgSideLength + e.getX()-imgSideLength/2;
		int y = (7-row)*imgSideLength + e.getY()-imgSideLength/2;
		view.moveDraggedPiece(x,y);
	}
	
	/**
	 * Runs the chess game
	 * @param arg
	 */
	public static void main(String []arg){
		Controller controller = new Controller();
	}

	/**
	 * Checks if the move was valid, if so, moves the piece to that spot, clears the old one. 
	 * Otherwise, board is returned to it's state before the move was attempted
	 * @param e
	 */
	public void handleMouseRelease(MouseEvent e) {
		view.clearDragLabelIcon();
		final int imgSideLength = ((PiecePanel) e.getSource()).getLabel().getIcon().getIconHeight();
    
	    int topSpace = view.getFrame().getInsets().top;
	    int leftSpace = view.getFrame().getInsets().left;
	    
	    int x=(e.getXOnScreen()-leftSpace)/imgSideLength;
	    int y=7 -(e.getYOnScreen()-topSpace)/imgSideLength;
	        
	    Move move = new Move(masterListener.getPiecePanelPressed().getPieceRepresented(),
	    		((PiecePanel) e.getSource()).getRow(),
	    		((PiecePanel) e.getSource()).getCol(),
	    		y,x );
	    
		if (RuleEngine.validateMove(move, boardController)){
			if (boardController.setPieceByCoords(y, x, masterListener.getPiecePanelPressed().getPieceRepresented())){
				boardController.clearSquare(((PiecePanel) e.getSource()).getRow(), ((PiecePanel) e.getSource()).getCol());
				System.out.println("Controller.handleMouseRelease: Valid Move. Setting "+ masterListener.getPiecePanelPressed().getPieceRepresented()+ " to row "+  Utils.getRowFromMouseEvent(e)+ " and col " + Utils.getColFromMouseEvent(e));
			}else
				System.out.println("Controller.handleMouseRelease: setPieceByCoords returned false, board not modified.");
		}
			else
				System.out.println("Controller.handleMouseRelease: Invalid move. Board not modified.");
			System.out.println("======================================================================================");
		view.removeHighlights();
		view.update();	
	}
	
}
