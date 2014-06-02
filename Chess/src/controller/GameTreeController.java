package controller;


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
				
				Piece capturedPiece = RuleEngine.processMove(move);
				
//				System.out.print("\nGameTreeController.generateSubtree:");
//				for (int i = 0; i < currentDepth; i++)
//					System.out.print("   ");
//				System.out.print(node.algebraicNotationPrint());
				
				generateSubtree(maxDepth, currentDepth,node);
				RuleEngine.undoChanges(capturedPiece, move);
				
			}
			
			
			
		}
		
	}
	
	public void print(Node currentNode, int indentCounter){
		
		if (currentNode != null){
			//if (currentNode.getChildren().size() == 0){
		
			for (int i = 0; i < indentCounter; i++){
				System.out.print("+--");
			}
			System.out.print(currentNode.coloredAlgebraicNotationPrint() + ", counter" + (counter++ +1) +"\n");
//			System.out.print(currentNode.coloredAlgebraicNotationPrint() + "\n");
			//}
			for (Node node: currentNode.getChildren())
				print(node,indentCounter+1);
			}
		
		
	}

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}
	
	
}
