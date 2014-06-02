package model;

import java.util.ArrayList;

public class Node extends Move{

	Node parent;
	ArrayList<Node> children;
	int score;
	
	public Node(Move move) {
		super(move);
		score = 0;
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

	
}
