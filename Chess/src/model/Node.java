package model;

import java.util.ArrayList;

public class Node extends Move{

	Node parent;
	ArrayList<Node> children;
	int score;
	int depth;
	
	public Node(Move move) {
		super(move);
		score = 0;
		depth = 0;
		children = new ArrayList<Node>();
		
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

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public void setDepth(int currentDepth) {
		this.depth = currentDepth;
		
	}

	public int getDepth() {
		// TODO Auto-generated method stub
		return this.depth;
	}

	
}
