package controller;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import model.GameTree;
import model.Move;
import model.Node;
import model.Piece;

public class GameTreeController {

	GameTree gameTree;
	Controller controller;
	Node rootNode;			// Dummy node, doesn't actually represent a move
	int counter = 0;
	
	public GameTreeController(GameTree gameTreeIn, Controller controllerIn){
		this.gameTree = gameTreeIn;
		this.controller = controllerIn;
		rootNode = new Node(new Move(new Piece("z",false,false,0,0),0,0,0,0));
	}
	
	public void generateSubtree(int maxDepth, int currentDepth, Node currentNodeIn){
		Node currentNode = currentNodeIn;
		boolean tmpHasMoved = false;
		
		if (currentNode == null)
			currentNode = rootNode;
		
		// Terminating condition is currentDepth == maxDepth, so recursion ends when execution
		// fails this if statement.
		if (currentDepth < maxDepth){
			
			// Find moves that are of the opposite color as the current node
			ArrayList<Move> legalMoves = controller.getMoveGenerator().findMoves(!currentNode.getPiece().isWhite());
			
			// We've moved one step deeper into the tree
			currentDepth++;
			
			for (Move move:legalMoves){
				Node node = new Node(move);
				currentNode.getChildren().add(node);
				tmpHasMoved = move.getPiece().isHasMoved();
				move.getPiece().setHasMoved(true);
				
				Piece capturedPiece = RuleEngine.processMove(move);
				
//				System.out.print("\nGameTreeController.generateSubtree:");
//				for (int i = 0; i < currentDepth; i++)
//					System.out.print("   ");
//				System.out.print(node.algebraicNotationPrint());
				
				generateSubtree(maxDepth, currentDepth,node);
				RuleEngine.undoChanges(capturedPiece, move);
				move.getPiece().setHasMoved(tmpHasMoved);
				
			}
			
			
			
		}
		
	}
	
	public void print(Node currentNode, int indentCounter){
//		try {
			 
 
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
 
			
 
		
		if (currentNode != null){
			if (currentNode.getChildren().size() == 0){
		
			for (int i = 0; i < indentCounter; i++){
//				bw.append("+--");
				System.out.print("+--");
			}
			counter++;
//			bw.append(currentNode.coloredAlgebraicNotationPrint() + ", counter" + (counter) +"\n");
//			bw.flush();
//			bw.close();
			System.out.print(currentNode.coloredAlgebraicNotationPrint() + ", counter" + (counter) +"\n");
//			System.out.print(currentNode.coloredAlgebraicNotationPrint() + "\n");
			}
			for (Node node: currentNode.getChildren())
				print(node,indentCounter+1);
			}
		
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	
}
