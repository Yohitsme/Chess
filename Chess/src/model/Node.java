/*
Quiet Intrigue is a chess playing engine with GUI written in Java.
Copyright (C) <2014>  Matthew Voss

Quiet Intrigue is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Quiet Intrigue is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Quiet Intrigue.  If not, see <http://www.gnu.org/licenses/>.
*/

package model;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

public class Node {//extends DefaultMutableTreeNode{

	Node parent;
	ArrayList<Node> children;
	double score;
	int depth;
	Node principalVariation;
	Move move;
	


	public Node(Move move) {
		super();
		score = 1000;
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

	public double getScore() {
		return score;
	}

	public void setScore(double d) {
		this.score = d;
	}

	public int getDepth() {
		// TODO Auto-generated method stub
		return this.depth;
	}

	
}
