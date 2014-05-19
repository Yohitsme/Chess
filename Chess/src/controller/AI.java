package controller;

import java.util.ArrayList;
import java.util.Random;

import model.Move;

public class AI {

	Controller controller;
	
	public AI(Controller controllerIn){
		this.controller = controllerIn;
	}
	
	
	
	public Move move(String color){
		
		
		ArrayList <Move> legalMoves = controller.getMoveGenerator().findMoves(color);
		Move move = null;
		Random rand = new Random(); 
		int index = rand.nextInt(legalMoves.size()); 
		if (index >=0){
		move = legalMoves.get(index); 
		System.out.println("AI.move: Move chosen: " + move.algebraicNotationPrint());
		}return move;
	}
	
	
	
}
