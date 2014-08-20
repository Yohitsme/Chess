package model;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

public class Node extends DefaultMutableTreeNode{

	Node parent;
	ArrayList<Node> children;
	int score;
	int depth;
	Node principalVariation;
	Move move;
	


	public Node(Move move) {
		super();
		score = 0;
		depth = 0;
		children = new ArrayList<Node>();
		this.move = move;
		// TODO Auto-generated constructor stub
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}
	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public void setDepth(int currentDepth) {
		this.depth = currentDepth;
		
	}

	public Node getPrincipalVariation() {
		return principalVariation;
	}

	public void setPrincipalVariation(Node principalVariation) {
		this.principalVariation = principalVariation;
	}

	public int getDepth() {
		// TODO Auto-generated method stub
		return this.depth;
	}

	
}
