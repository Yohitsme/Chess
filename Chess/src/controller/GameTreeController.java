package controller;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import model.GameTree;
import model.Move;
import model.Node;
import model.Piece;

/**
 * This has been replaced for the most part by a game tree internal to the AI
 * to avoid unneccesary references to old moves that were cluttering up memory.
 * @author Matthew
 *
 */
public class GameTreeController {

	GameTree gameTree;
	Controller controller;
	Node rootNode;			// Dummy node, doesn't actually represent a move
	int counter = 0;
	Piece debugPiece;
	int debugNum = 0;
	Node root = new Node(null);
	
	
	public GameTreeController(GameTree gameTreeIn, Controller controllerIn){
		this.gameTree = gameTreeIn;
		this.controller = controllerIn;
		rootNode = new Node(new Move(new Piece('z',false,false,0,0),0,0,0,0));
		
	}
	
//	public void generateSubtree(int maxDepth, int currentDepth, Node currentNodeIn, Node node1){
//		Node currentNode = currentNodeIn;
//		boolean tmpHasMoved = false;
//		
//		if (currentNode == null)
//			currentNode = rootNode;
//		
//		// Terminating condition is currentDepth == maxDepth, so recursion ends when execution
//		// fails this if statement.
//		if (currentDepth < maxDepth){
//			
//			// Find moves that are of the opposite color as the current node
//			ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(!currentNode.getPiece().isWhite());
//			
//			// We've moved one step deeper into the tree
//			currentDepth++;
//			
//			for (Move move:legalMoves){
//				Node node = new Node(move);
//				node.setDepth(currentDepth);
//				currentNode.getChildren().add(node);
//				tmpHasMoved = move.getPiece().isHasMoved();
//				move.getPiece().setHasMoved(true);
//				Piece capturedPiece = RuleEngine.processMove(move);
//				if (currentDepth == maxDepth)
//					counter++;
//				
////				DefaultMutableTreeNode guiNode = new DefaultMutableTreeNode(move.algebraicNotationPrint());
////				if (currentNode == rootNode)
////					root.add(guiNode);
////				else
////					parentNode.add(guiNode);
////				
////				System.out.print("\nGameTreeController.generateSubtree:");
////				for (int i = 0; i < currentDepth; i++)
////					System.out.print("   ");
////				System.out.print(node.algebraicNotationPrint());
//				
//				generateSubtree(maxDepth, currentDepth,node,node);
//				RuleEngine.undoChanges(capturedPiece, move);
//				move.getPiece().setHasMoved(tmpHasMoved);
//				
//			}
//			
//			
//			
//		}
//		
//	}
	
//	public void print(Node currentNode, int indentCounter){
//		try {
//			 
//			if (currentNode.getDepth() == 1)
//				debugPiece = currentNode.getPiece();
//			else if (currentNode.getDepth() == 3){
//				
//				String type1 = debugPiece.getType();
//				String type2 = currentNode.getPiece().getType();
////				if ((type1.equals("pawn") && !type2.equals("pawn"))
////					|| (type2.equals("pawn")&& !type1.equals("pawn")))
////				if (debugPiece == currentNode.getPiece() && debugPiece.getType().equals("knight")
////						&& currentNode.getEndRow()!=0)	
//				if ((type1.equals("rook")&&type2.equals("knight"))||(type1.equals("knight")&&type2.equals("rook")) )
//				
//				debugNum++;
//			}
//			
//			File file = new File("C:/Users/Matthew/Desktop/ChessStuff.txt");
// 
//			// if file doesnt exists, then create it
//			if (!file.exists()) {
//				file.createNewFile();
//			}
//			else{
//				if(counter == 1 && file.delete())
//					System.out.print("File not deleted");
//				file.createNewFile();
//			}
//			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//			BufferedWriter bw = new BufferedWriter(fw);
//
//			bw.write("");
// 
//			
// 
//		
//		if (currentNode != null){
//		
//			for (int i = 0; i < indentCounter; i++){
//				bw.append("+--");
//				System.out.print("+--");
//			}
//			
//			if (currentNode.getChildren().size() == 0)
//				counter++;
//			
//			String text = currentNode.coloredAlgebraicNotationPrint() + ", " + (counter);
//			if (currentNode.getChildren().size()!= 0)
//				text += "{";
//			
//			bw.append(text +"\n");
//			System.out.print(text +"\n");
//
//			for (Node node: currentNode.getChildren())
//				print(node,indentCounter+1);
//			}
//		if (currentNode.getChildren().size()!= 0){
//			bw.append("}");
//			System.out.print("}");
//		}
//			
//		
//			bw.flush();
//		bw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public Node getRootNode() {
		return rootNode;
	}



	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getDebugNum() {
		return debugNum;
	}

	public void setDebugNum(int debugNum) {
		this.debugNum = debugNum;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node rootIn) {
		boolean isUpdated = false;
		for (Node node: root.getChildren()){
			if (node.getMove().equals(rootIn.getMove())){
				this.root = node;
				isUpdated = true;
			}
		}
		
		if (!isUpdated){
			
			System.out.println("GameTreeController.setRoot: Could not find move " + rootIn.getMove().algebraicNotationPrint() + " in children of current root. Replaced anyway. Error?");
			
			this.root=rootIn;
		
		}
	}
}
