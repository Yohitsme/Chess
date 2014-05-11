package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import controller.BoardController;
import controller.MasterListener;
import model.Model;
import model.Piece;

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
	JLayeredPane layeredPane;
	BoardController boardController;
	MasterListener masterListener;
	PiecePanel[][] piecePanelArray;
	JLabel [][] highlightArray;
	JLabel dragPiece;

	HashMap<String, ImageIcon> imgMap;

	/**
	 * View constructor. Takes boardController and sets each position to the
	 * image of the piece of the corresponding position in the board of the
	 * model
	 * 
	 * @param boardControllerIn
	 */
	public View(BoardController boardControllerIn,
			MasterListener masterListenerIn) {

		this.boardController = boardControllerIn;
		this.masterListener = masterListenerIn;
		// Populate imageMap
		loadImages();

		// Configure Panels
		configureFrame();
		configureBoardPanel();
		configurePiecePanel();
		configureHighlightPanel();
		configureDragPanel();
		configureLayeredPane();
		

		frame.add(layeredPane, BorderLayout.CENTER);
		frame.pack();

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
		for (int row = 7; row >= 0; row--) {
			for (int col = 0; col < 8; col++) {
				piecePanelArray[row][col] = new PiecePanel(row, col,
						generateJLabel(row, col),
						boardController.getPieceByCoords(row, col));
				piecePanel.add(piecePanelArray[row][col]);

			}
		}
		piecePanel.repaint();
		piecePanel.revalidate();

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
		update();

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
		else if (piece.getType().equals("pawn")) {
			if (piece.isWhite())
				image = imgMap.get("WP");
			else
				image = imgMap.get("BP");
		} else if (piece.getType().equals("rook")) {
			if (piece.isWhite())
				image = imgMap.get("WR");
			else
				image = imgMap.get("BR");
		} else if (piece.getType().equals("bishop")) {
			if (piece.isWhite())
				image = imgMap.get("WB");
			else
				image = imgMap.get("BB");
		} else if (piece.getType().equals("knight")) {
			if (piece.isWhite())
				image = imgMap.get("WN");
			else
				image = imgMap.get("BN");
		} else if (piece.getType().equals("queen")) {
			if (piece.isWhite())
				image = imgMap.get("WQ");
			else
				image = imgMap.get("BQ");
		} else if (piece.getType().equals("king")) {
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

		imgMap.put("whiteSquare", buildImage("whiteSquare"));
		imgMap.put("blackSquare", buildImage("blackSquare"));

		imgMap.put("WB", buildImage("WB"));
		imgMap.put("BB", buildImage("BB"));

		imgMap.put("WR", buildImage("WR"));
		imgMap.put("BR", buildImage("BR"));

		imgMap.put("WK", buildImage("WK"));
		imgMap.put("BK", buildImage("BK"));

		imgMap.put("WN", buildImage("WN"));
		imgMap.put("BN", buildImage("BN"));

		imgMap.put("WQ", buildImage("WQ"));
		imgMap.put("BQ", buildImage("BQ"));

		imgMap.put("WP", buildImage("WP"));
		imgMap.put("BP", buildImage("BP"));

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

	public void highlightSquare(int row, int col) {
		highlightArray[row][col].setIcon(imgMap.get("highlight"));
		
	}

}
