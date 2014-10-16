package view;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import model.Move;
import model.Piece;
import utils.Constants;
import controller.BoardController;
import controller.Controller;
import controller.MasterListener;

/**
 * The entire view. 
 * @author Matthew
 *
 */
// Test commit
public class View {

	JFrame frame;
	JPanel boardPanel;
	JPanel piecePanel;
	JPanel dragPanel;
	JPanel highlightPanel;
	JPanel analysisPanel;
	JPanel moveListPanel;
	JPanel capturedPiecePanel;
	JPanel sidePanel;
	JLayeredPane layeredPane;
	BoardController boardController;
	ArrayList<Piece>capturedPieces;
	MasterListener masterListener;
	PiecePanel[][] piecePanelArray;
	JLabel [][] highlightArray;
	JLabel dragPiece;
	String boardOrientation;
	Controller controller;
	JLabel messageLabel;

	HashMap<String, ImageIcon> imgMap;

	/**
	 * View constructor. Takes boardController and sets each position to the
	 * image of the piece of the corresponding position in the board of the
	 * model
	 * 
	 * @param boardControllerIn
	 */
	public View(Controller controllerIn, BoardController boardControllerIn,
			MasterListener masterListenerIn, ArrayList<Piece> capturedPiecesIn) {

		this.boardController = boardControllerIn;
		this.masterListener = masterListenerIn;
		this.capturedPieces = capturedPiecesIn;
		this.controller = controllerIn;
		this.boardOrientation = "normal";
		// Populate imageMap
		loadImages();

		// Configure Panels
		configureCapturedPiecePanel();
		configureFrame();
		configureMenuBar();
		configureBoardPanel();
		configurePiecePanel();
		configureHighlightPanel();
		configureDragPanel();
		configureLayeredPane();
		configureAnalysisPanel();
		configureMoveListPanel();
		configureSidePanel();
		configureMessageLabel();
		
		frame.add(layeredPane, BorderLayout.CENTER);
		frame.add(sidePanel,BorderLayout.EAST);
		frame.add(messageLabel,BorderLayout.SOUTH);
		frame.pack();
		update();

	}

	private void configureMessageLabel() {
		messageLabel = new JLabel(Constants.getOpeningGameText());
	}

	private void configureCapturedPiecePanel() {
		capturedPiecePanel = new JPanel();
		
	}

	/**
	 * Sets up the menu options available to the user
	 */
	public void configureMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu viewMenu = new JMenu("View");
		JMenu settingsMenu = new JMenu("Settings");
		
		JMenuItem newGame = new JMenuItem("New Game");
		newGame.setActionCommand("newGame");
		newGame.addActionListener(this.masterListener);
		
		JMenuItem exportMoveList = new JMenuItem("Export Move List");
		exportMoveList.setActionCommand("exportMoveList");
		exportMoveList.addActionListener(this.masterListener);	
		
		JMenuItem changeGameMode = new JMenuItem("Change Game Mode");
		changeGameMode.setActionCommand("changeGameMode");
		changeGameMode.addActionListener(this.masterListener);
		
		JMenuItem flipBoard = new JMenuItem("Flip Board");
		flipBoard.setActionCommand("flipBoard");
		flipBoard.addActionListener(masterListener);
		
		JMenuItem adjustDepth = new JMenuItem("Adjust Depth");
		adjustDepth.setActionCommand("adjustDepth");
		adjustDepth.addActionListener(masterListener);
		
		JMenuItem tuneEngine = new JMenuItem("Tune Engine");
		tuneEngine.setActionCommand("tuneEngine");
		tuneEngine.addActionListener(masterListener);
		
		fileMenu.add(newGame);
		fileMenu.add(changeGameMode);
		fileMenu.add(exportMoveList);
		viewMenu.add(flipBoard);
		settingsMenu.add(tuneEngine);
		settingsMenu.add(adjustDepth);
		
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(settingsMenu);
		frame.setJMenuBar(menuBar);
	}
	
	public void configureAnalysisPanel(){
		analysisPanel = new JPanel();
	}
	
	public void configureMoveListPanel(){
		moveListPanel = new JPanel(new BorderLayout());
	}
	
	public void configureSidePanel(){
		sidePanel = new JPanel();
		sidePanel.setLayout(new GridLayout(2,1));
		sidePanel.add(moveListPanel);
//		sidePanel.add(analysisPanel);
		sidePanel.add(capturedPiecePanel);
	}
	/**
	 * Boilerplate highlight panel configuration.  The highlight panel is between the board panel and
	 * the piece panel.  When a player presses the mouse on a piece, all squares that are legal to move 
	 * to are highlighted on this panel.  
	 */
	private void configureHighlightPanel() {
		highlightPanel = new JPanel(new GridLayout(8, 8, 0, 0));
		highlightPanel.setOpaque(false);
		highlightPanel.setPreferredSize(new Dimension(640, 640));
		highlightPanel.setBounds(0, 0, 640, 640);
		
		highlightArray = new JLabel[8][8];
		
		
		for (int row = 7; row >= 0; row--) {
			for (int col = 0; col < 8; col++) {
				JLabel label = new JLabel(imgMap.get("blank"));
				highlightArray[row][col] = label;
				highlightPanel.add(label);
			}
		}
		
		
	}

	/**
	 * Boilerplate configuration.  The drag panel is a panel on top of everything
	 * that a piece gets moved on when a player clicks and drags a piece.
	 */
	public void configureDragPanel() {
		dragPanel = new JPanel();
		dragPanel.setOpaque(false);
		dragPanel.setPreferredSize(new Dimension(640, 640));
		dragPanel.setBounds(0, 0, 640, 640);
		dragPanel.addMouseListener(masterListener);
		dragPanel.addMouseMotionListener(masterListener);

		dragPiece = new JLabel();
		dragPanel.add(dragPiece);
	}

	/**
	 * Moves the dragIcon to pixel location (x,y)
	 * @param x
	 * @param y
	 */
	public void moveDraggedPiece(int x, int y) {
		dragPiece.setLocation(x, y);
	}

	/**
	 * Sets the drag image to that of the piece at location (row,col) on the board
	 * @param row
	 * @param col
	 */
	public void updateDragLabelIcon(int row, int col) {
		dragPiece.setIcon(generateImageIcon(row, col));
	}

	/**
	 * Clear the board, iterate over the board and set the images to the
	 * corresponding piece images in the model.
	 */
	public void update() {

		piecePanel.removeAll();
		
		if (boardOrientation.equals("normal")){
		for (int row = 7; row >= 0; row--) {
			for (int col = 0; col < 8; col++) {
				piecePanelArray[row][col] = new PiecePanel(row, col,
						generateJLabel(row, col),
						boardController.getPieceByCoords(row, col));
				piecePanel.add(piecePanelArray[row][col]);

			}
		}}
		else if (boardOrientation.equals("flipped")){
			
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					piecePanelArray[row][col] = new PiecePanel(row, col,
							generateJLabel(row, col),
							boardController.getPieceByCoords(row, col));
					piecePanel.add(piecePanelArray[row][col]);

				}
			}
			
			
		}
		updateCapturedPiecePanel();
		updateMoveListPanel(controller.getModel().getMoveList());
		removeHighlights();
		highlightPreviousMove(controller.getModel().getMoveList());
		piecePanel.repaint();
		piecePanel.revalidate();
		frame.pack();

	}

	@SuppressWarnings("unchecked")
	private void updateCapturedPiecePanel() {
		// TODO Auto-generated method stub
		//JLabel deadPieces = new JLabel();
		JPanel whitePanel1 = new JPanel();
		whitePanel1.setLayout(new BoxLayout(whitePanel1, BoxLayout.Y_AXIS));
		JPanel blackPanel1 = new JPanel(); 
		blackPanel1.setLayout(new BoxLayout(blackPanel1, BoxLayout.Y_AXIS));
		JPanel whitePanel2 = new JPanel();
		whitePanel2.setLayout(new BoxLayout(whitePanel2, BoxLayout.Y_AXIS));
		JPanel blackPanel2 = new JPanel(); 
		blackPanel2.setLayout(new BoxLayout(blackPanel2, BoxLayout.Y_AXIS));		
		
		
		ArrayList<Piece>whiteList = new ArrayList<Piece>();
		ArrayList<Piece>blackList = new ArrayList<Piece>();
		
		
		for (Piece piece: capturedPieces){
			if (piece.isWhite())
				whiteList.add(piece);
			else
				blackList.add(piece);
		}
		Collections.sort(whiteList, new PieceSorter());
		Collections.sort(blackList, new PieceSorter());
		
		
		int i = 0;
		for (Piece piece: whiteList){
			if (i < 8)
				whitePanel1.add(new JLabel(imgMap.get(getPieceAbbreviation(piece)+"_small")));
			else
				whitePanel2.add(new JLabel(imgMap.get(getPieceAbbreviation(piece)+"_small")));
				i++;
		}
		i = 0;
		for (Piece piece: blackList){
			if (i < 8)
				blackPanel1.add(new JLabel(imgMap.get(getPieceAbbreviation(piece)+"_small")));
			else
				blackPanel2.add(new JLabel(imgMap.get(getPieceAbbreviation(piece)+"_small")));
				i++;
		}
		
		
		
		
		capturedPiecePanel.removeAll();
		capturedPiecePanel.add(whitePanel1);
		capturedPiecePanel.add(whitePanel2);
		capturedPiecePanel.add(blackPanel1);
		capturedPiecePanel.add(blackPanel2);
		
		capturedPiecePanel.repaint();
		capturedPiecePanel.revalidate();
	}

	/**
	 * Boilerplate configuration. Order of panes: (Bottom) board panel, piece
	 * panel, highlight panel, drag panel (top)
	 */
	private void configureLayeredPane() {
		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(640, 640));

		layeredPane.setVisible(true);

		layeredPane.add(boardPanel, new Integer(0));
		layeredPane.add(piecePanel, new Integer(1));
		layeredPane.add(highlightPanel, new Integer(2));
		layeredPane.add(dragPanel, new Integer(3));

	}

	/**
	 * Boilerplate configuration. Assign a 8x8 gridlayout manager, size,
	 * dimensions, and then call update to refresh with the current board status
	 */
	private void configurePiecePanel() {
		piecePanel = new JPanel(new GridLayout(8, 8, 0, 0));
		piecePanel.setOpaque(false);
		piecePanel.setPreferredSize(new Dimension(640, 640));
		piecePanel.setBounds(0, 0, 640, 640);
		piecePanelArray = new PiecePanel[8][8];
//		update();

	}
	
	/**
	 * 
	 * @param jTree
	 */
	public void updateAnalysisPanel(JTree jTree){
		analysisPanel.removeAll();
		
		JScrollPane pane = new JScrollPane(jTree);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(pane,BorderLayout.CENTER);
		pane.setPreferredSize(new Dimension(200,310));
		analysisPanel.add(panel);
		analysisPanel.repaint();
		analysisPanel.revalidate();
	}
	
	public void updateMoveListPanel(ArrayList<Move>moveList){
		moveListPanel.removeAll();
		
		int size = moveList.size()/2 + 1;
		String [] columnNames = {"Move","White","Black"};
		Object [][] data = new String[size][3];
		
		for(int i = 0; i < moveList.size(); i++){
			data[i/2][0] = Integer.toString(i/2 +1);
			data[i/2][i%2 + 1] = moveList.get(i).algebraicNotationPrint();
		}
		JTable table = new JTable(data, columnNames);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < 3; i++)
		table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		
		table.setEnabled(false);
	
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(200,310));
		moveListPanel.add(new JScrollPane(scrollPane));

		moveListPanel.repaint();
		moveListPanel.revalidate();
	}

	/**
	 * Given coordinates row and col, get the piece at location board[row][col]
	 * and build a JLabel with the corresponding image. Return image
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public ImageIcon generateImageIcon(int row, int col) {

		Piece piece = boardController.getPieceByCoords(row, col);
		ImageIcon image = null;

		if (piece == null)
			image = imgMap.get("blank");
		else if (piece.getType()==Constants.getPawnChar()) {
			if (piece.isWhite())
				image = imgMap.get("WP");
			else
				image = imgMap.get("BP");
		} else if (piece.getType()==Constants.getRookChar()) {
			if (piece.isWhite())
				image = imgMap.get("WR");
			else
				image = imgMap.get("BR");
		} else if (piece.getType()==Constants.getBishopChar()) {
			if (piece.isWhite())
				image = imgMap.get("WB");
			else
				image = imgMap.get("BB");
		} else if (piece.getType()==Constants.getKnightChar()) {
			if (piece.isWhite())
				image = imgMap.get("WN");
			else
				image = imgMap.get("BN");
		} else if (piece.getType()==Constants.getQueenChar()) {
			if (piece.isWhite())
				image = imgMap.get("WQ");
			else
				image = imgMap.get("BQ");
		} else if (piece.getType()==Constants.getKingChar()) {
			if (piece.isWhite())
				image = imgMap.get("WK");
			else
				image = imgMap.get("BK");
		} else
			System.out
					.println("View.generateJLabel: Piece type or color not recognized: "
							+ piece.toString());

		return image;
	}

	/**
	 * Calls generateImageIcona and builds a JLabel with the result
	 * @param row
	 * @param col
	 * @return
	 */
	public JLabel generateJLabel(int row, int col) {
		return new JLabel(generateImageIcon(row, col));
	}

	/**
	 * Assign each square to a black or white square image
	 */
	private void configureBoardPanel() {
		boardPanel = new JPanel(new GridLayout(8, 8, 0, 0));
		boardPanel.setPreferredSize(new Dimension(640, 640));
		boardPanel.setOpaque(false);
		boardPanel.setBounds(0, 0, 640, 640);

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				JPanel tmpPanel = new JPanel(new BorderLayout());

				JLabel whiteLabel = new JLabel(imgMap.get("whiteSquare"));
				JLabel blackLabel = new JLabel(imgMap.get("blackSquare"));

				if ((row % 2) == (col % 2))
					tmpPanel.add(whiteLabel, BorderLayout.CENTER);
				else
					tmpPanel.add(blackLabel, BorderLayout.CENTER);

				boardPanel.add(tmpPanel);
			}
		}
	}

	/**
	 * Put an image for each piece into the HashMap imgMap. All pieces are
	 * designated by two letters, color and then piece name. "WR" for
	 * "white rook", "BN" for black knight, "WK" for white king, etc. Other
	 * images are the "blank" image, highlight border, and white/black squares.
	 */
	public void loadImages() {
		imgMap = new HashMap<String, ImageIcon>();

		imgMap.put("blank", buildImage("Blank"));
		imgMap.put("highlight", buildImage("highlight"));
		imgMap.put("squareHighlight", buildImage("squareHighlight"));

		imgMap.put("whiteSquare", buildImage("whiteSquare"));
		imgMap.put("blackSquare", buildImage("blackSquare"));

		imgMap.put("WB", buildImage("WB"));
		imgMap.put("WB_small", buildImage("WB_small"));
		imgMap.put("BB", buildImage("BB"));
		imgMap.put("BB_small", buildImage("BB_small"));

		imgMap.put("WR", buildImage("WR"));
		imgMap.put("WR_small", buildImage("WR_small"));
		imgMap.put("BR", buildImage("BR"));
		imgMap.put("BR_small", buildImage("BR_small"));

		imgMap.put("WK", buildImage("WK"));
		imgMap.put("WK_small", buildImage("WK_small"));
		imgMap.put("BK", buildImage("BK"));
		imgMap.put("BK_small", buildImage("BK_small"));

		imgMap.put("WN", buildImage("WN"));
		imgMap.put("WN_small", buildImage("WN_small"));
		imgMap.put("BN", buildImage("BN"));
		imgMap.put("BN_small", buildImage("BN_small"));

		imgMap.put("WQ", buildImage("WQ"));
		imgMap.put("WQ_small", buildImage("WQ_small"));
		imgMap.put("BQ", buildImage("BQ"));
		imgMap.put("BQ_small", buildImage("BQ_small"));

		imgMap.put("WP", buildImage("WP"));
		imgMap.put("WP_small", buildImage("WP_small"));
		imgMap.put("BP", buildImage("BP"));
		imgMap.put("BP_small", buildImage("BP_small"));

	}

	/**
	 * Given a string filename (without file extension!), the image is loaded as
	 * an ImageIcon and returned
	 * 
	 * @param fileName
	 * @return
	 */
	public ImageIcon buildImage(String fileName) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass()
					.getResource("/" + fileName + ".png"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ImageIcon imageIcon = new ImageIcon(image);
		return imageIcon;
	}

	/**
	 * Boilerplate frame configuration.
	 */
	public void configureFrame() {
		frame = new JFrame("Quiet Intrigue");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
	}

	/**
	 * Sets the drag icon to the blank, transparent square png
	 */
	public void clearDragLabelIcon() {
		// TODO Auto-generated method stub
		dragPiece.setIcon(imgMap.get("blank"));
	}

	/**
	 * Sets the square designated by (row,col) to the blank square png
	 * @param row
	 * @param col
	 */
	public void clearSelectedPiece(int row, int col) {
		piecePanelArray[row][col].getLabel().setIcon(imgMap.get("blank"));

	}

	public JFrame getFrame() {
		// TODO Auto-generated method stub
		return frame;
	}

	public void removeHighlights() {
		// TODO Auto-generated method stub
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
				highlightArray[row][col].setIcon(imgMap.get("blank"));
	}

	public String getBoardOrientation() {
		return boardOrientation;
	}
	
	/** 
	 * Flips the board
	 */
	public void flipBoard(){
		if (boardOrientation.equals("flipped")){
			boardOrientation = "normal";
			update();
		}
		else if (boardOrientation.equals("normal")){
			boardOrientation = "flipped";
			update();			
		}
		else
			System.out.println("View.flipBoard: boardOrientation of /'" + boardOrientation + "/' not recognized.");
	}

	public void setBoardOrientation(String boardOrientation) {
		this.boardOrientation = boardOrientation;
	}

	public void highlightSquare(int row, int col) {
		if (boardOrientation.equals("flipped"))
			row = 7-row;
		
		
		highlightArray[row][col].setIcon(imgMap.get("highlight"));
		
	}
	
	public String getPieceAbbreviation(Piece piece){
		String result = "";
		
		if (piece.isWhite())
			result+="W";
		else
			result+="B";
		result +=Character.toUpperCase(piece.getType());
		
		return result;
	}
	
	public void updateMessageLabel(String text){
		messageLabel.setText(text);
	}

	public void highlightPreviousMove(ArrayList<Move> moveList) {
		if (moveList.size() != 0){
		int row,col;
		int size = moveList.size();
		Move move = moveList.get(size-1);
		
		row = move.getStartRow();
		col = move.getStartCol();
		highlightArray[row][col].setIcon(imgMap.get("squareHighlight"));

		row = move.getEndRow();
		col = move.getEndCol();
		highlightArray[row][col].setIcon(imgMap.get("squareHighlight"));
	}}

}

class PieceSorter implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		Piece p1 = (Piece)o1;
		Piece p2 = (Piece)o2;
		int weight1 = Constants.getPieceWeight(p1);
		int weight2 = Constants.getPieceWeight(p2);
		
		
		if ( weight1< weight2)
			return 1;
		else if (weight1 == weight2)
			return 0;
		else
		return -1;
	}
	
}