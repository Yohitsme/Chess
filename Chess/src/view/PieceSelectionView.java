package view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class for a small window where the user can select the piece they want to put
 * on the board.
 * 
 * @author Matthew
 * 
 */
public class PieceSelectionView extends JFrame {

	JPanel iconPanel;
	
	public PieceSelectionView() {
		super("Select a piece");
		
		iconPanel = new JPanel();
		JLabel piecesGoHere = new JLabel("piecesGoHere");
		iconPanel.add(piecesGoHere);
		this.add(iconPanel);
		this.setVisible(true);
		this.pack();
		
	}

}
