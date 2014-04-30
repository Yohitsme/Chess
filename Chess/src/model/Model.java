package model;

/**
 * The array of pieces that represents the board is stored here.
 * @author Matthew
 *
 */
public class Model {

	Piece[][] board;
	

	/**
	 * Constructor. Initializes board to classic chess start position
	 */
	public Model(){
		
		board = new Piece[8][8];
		initializeBoard();
	}
	
	/**
	 * Initializes board to classic chess start position
	 */
	public void initializeBoard(){
		
		/*
		 * (row,col)
		 * (7,0) ... ... ... (7,7)
		 * ...           ...
		 * ...           ...
		 * ...       ...
		 * (2,0)     ...
		 * (1,0) ...
		 * (0,0) ... ... ... (0,7)
		 */
		
		boolean hasMoved = false;
		boolean white = true;
		boolean black = false;
		
		// Set white piece row
		board [0][0] = new Piece("rook",white,hasMoved);
		board [0][1] = new Piece("knight",white,hasMoved);
		board [0][2] = new Piece("bishop",white,hasMoved);
		board [0][3] = new Piece("queen",white,hasMoved);
		board [0][4] = new Piece("king",white,hasMoved);
		board [0][5] = new Piece("bishop",white,hasMoved);
		board [0][6] = new Piece("knight",white,hasMoved);
		board [0][7] = new Piece("rook",white,hasMoved);
		
		// Set white pawns
		for (int i = 0; i < 8; i++){
			board[1][i] = new Piece("pawn", white, hasMoved);
		}
		
		// Set empty rows
		for (int row = 2; row < 6; row++)
			for (int col = 0; col < 8; col++)
				board[row][col] = null;
		
		// Set black pawns
		for (int i = 0; i < 8; i++){
			board[6][i] = new Piece("pawn", black, hasMoved);
		}
		
		// Set black piece row
		board [7][0] = new Piece("rook",black,hasMoved);
		board [7][1] = new Piece("knight",black,hasMoved);
		board [7][2] = new Piece("bishop",black,hasMoved);
		board [7][3] = new Piece("queen",black,hasMoved);
		board [7][4] = new Piece("king",black,hasMoved);
		board [7][5] = new Piece("bishop",black,hasMoved);
		board [7][6] = new Piece("knight",black,hasMoved);
		board [7][7] = new Piece("rook",black,hasMoved);
	}
	
	public Piece[][] getBoard() {
		return board;
	}
	
	public void setBoard(Piece[][] board) {
		this.board = board;
	}
}
