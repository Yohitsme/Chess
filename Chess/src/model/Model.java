package model;

import java.util.ArrayList;

/**
 * The array of pieces that represents the board is stored here.
 * 
 * @author Matthew
 * 
 */
public class Model {

	Piece[][] board;
	ArrayList<Piece> whitePieces;
	ArrayList<Piece> blackPieces;
	ArrayList<Piece> capturedPieces;
	

	ArrayList<Move> moveList;
	String gameMode; // "pVc,pVp,cVp,cVc"
	GameTree gameTree;

	/**
	 * Constructor. Initializes board to classic chess start position
	 */
	public Model() {

		whitePieces = new ArrayList<Piece>();
		blackPieces = new ArrayList<Piece>();
		capturedPieces = new ArrayList<Piece>();
		moveList = new ArrayList<Move>();
		gameTree = new GameTree();
		gameMode = "pVc";

		board = new Piece[8][8];
//		initializeBoard();
		initializeSimpleBoard();
		populateLists();
	}

	/**
	 * Copy constructor. Don't need the same references to pieces to be the same
	 * across moveList and board and piece lists? I don't think so
	 * 
	 * @param modelIn
	 */
	public Model(Model modelIn) {
		this.board = new Piece[8][8];

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				this.board[i][j] = new Piece(modelIn.getBoard()[i][j]);
			}
		}

		this.whitePieces = new ArrayList<Piece>(modelIn.getWhitePieces());
		this.blackPieces = new ArrayList<Piece>(modelIn.getBlackPieces());
		this.moveList = new ArrayList<Move>(modelIn.getMoveList());

	}

	/**
	 * Resets all data to initial game state
	 */
	public void resetModel() {
		whitePieces.removeAll(whitePieces);
		blackPieces.removeAll(blackPieces);
		moveList.removeAll(moveList);

		initializeBoard();
		populateLists();
	}

	/**
	 * Initializes board to classic chess start position
	 */
	public void initializeBoard() {

		/*
		 * (row,col) (7,0) ... ... ... (7,7) ... ... ... ... ... ... (2,0) ...
		 * (1,0) ... (0,0) ... ... ... (0,7)
		 */

		boolean hasMoved = false;
		boolean white = true;
		boolean black = false;

		// Set white piece row
		board[0][0] = new Piece("rook", white, hasMoved, 0, 0);
		board[0][1] = new Piece("knight", white, hasMoved, 0, 1);
		board[0][2] = new Piece("bishop", white, hasMoved, 0, 2);
		board[0][3] = new Piece("queen", white, hasMoved, 0, 3);
		board[0][4] = new Piece("king", white, hasMoved, 0, 4);
		board[0][5] = new Piece("bishop", white, hasMoved, 0, 5);
		board[0][6] = new Piece("knight", white, hasMoved, 0, 6);
		board[0][7] = new Piece("rook", white, hasMoved, 0, 7);

		// Set white pawns
		for (int i = 0; i < 8; i++) {
			board[1][i] = new Piece("pawn", white, hasMoved, 1, i);
		}

		// Set empty rows
		for (int row = 2; row < 6; row++)
			for (int col = 0; col < 8; col++)
				board[row][col] = null;

		// Set black pawns
		for (int i = 0; i < 8; i++) {
			board[6][i] = new Piece("pawn", black, hasMoved, 6, i);
		}

		// Set black piece row
		board[7][0] = new Piece("rook", black, hasMoved, 7, 0);
		board[7][1] = new Piece("knight", black, hasMoved, 7, 1);
		board[7][2] = new Piece("bishop", black, hasMoved, 7, 2);
		board[7][3] = new Piece("queen", black, hasMoved, 7, 3);
		board[7][4] = new Piece("king", black, hasMoved, 7, 4);
		board[7][5] = new Piece("bishop", black, hasMoved, 7, 5);
		board[7][6] = new Piece("knight", black, hasMoved, 7, 6);
		board[7][7] = new Piece("rook", black, hasMoved, 7, 7);
	}

	/**
	 * Initialize a very simple version of the board for gameTree testing
	 */
	public void initializeSimpleBoard() {
		boolean hasMoved = true;
		boolean white = true;
		boolean black = false;

		// Set white piece row

		// Set white pawns

		// Set empty rows
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
				board[row][col] = null;

		board[0][0] = new Piece("king", white, hasMoved, 0, 0);
//		board[1][4] = new Piece("rook", white, hasMoved, 1, 4);
		board[2][5] = new Piece("rook", white, hasMoved, 2, 5);
		

		board[7][3] = new Piece("king", black, hasMoved, 7, 3);
	
//		board[6][6] = new Piece("pawn", black, hasMoved, 6, 6);
//		board[6][7] = new Piece("pawn", black, hasMoved, 6, 7);
//		board[6][4] = new Piece("pawn", black, hasMoved, 6, 4);
//			
//		
//		board[4][4] = new Piece("pawn", black, hasMoved, 4,4);
//		board[3][5] = new Piece("rook", white,hasMoved,3,5);
//		board[1][7] = new Piece("pawn",black,hasMoved,1,7);

		// Set black piece row
		
	}

	/**
	 * Adds all of each team's pieces to their respective list
	 * 
	 * @return
	 */
	public void populateLists() {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++)
				if (board[row][col] != null && board[row][col].isWhite())
					whitePieces.add(board[row][col]);
		}
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++){
				if (board[row][col] != null && !board[row][col].isWhite())
					blackPieces.add(board[row][col]);}
			
		}
	}
	
	public void printMoveList(){
		String body = "";
		for (int i = 0; i < moveList.size(); i++) {
			if (i % 2 == 0)
				body += "\n" + Integer.toString(i / 2 + 1) + " ";

			body += moveList.get(i).algebraicNotationPrint() + " ";
		}
		System.out.println(body);
	}

	public Piece[][] getBoard() {
		return board;
	}

	public void setBoard(Piece[][] board) {
		this.board = board;
	}

	public ArrayList<Move> getMoveList() {
		return moveList;
	}

	public void setMoveList(ArrayList<Move> moveList) {
		this.moveList = moveList;
	}

	public ArrayList<Piece> getWhitePieces() {
		return whitePieces;
	}

	public void setWhitePieces(ArrayList<Piece> whitePieces) {
		this.whitePieces = whitePieces;
	}

	public ArrayList<Piece> getBlackPieces() {
		return blackPieces;
	}

	public void setBlackPieces(ArrayList<Piece> blackPieces) {
		this.blackPieces = blackPieces;
	}

	public String getGameMode() {
		return gameMode;
	}
	
	public ArrayList<Piece> getCapturedPieces() {
		return capturedPieces;
	}

	public void setCapturedPieces(ArrayList<Piece> capturedPieces) {
		this.capturedPieces = capturedPieces;
	}

	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}

	public GameTree getGameTree() {
		return gameTree;
	}

	public void setGameTree(GameTree gameTree) {
		this.gameTree = gameTree;
	}
}
