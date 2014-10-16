package controller;

import view.View;

public class AI_Thread implements Runnable{

	boolean isWhiteTurn;
	Controller controller;
	AI ai;
	
	public AI_Thread(Controller controllerIn, AI aiIn, boolean whiteTurnIn) {
		isWhiteTurn = whiteTurnIn;
		controller = controllerIn;
		ai = aiIn;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		controller.processMove(ai.move(isWhiteTurn));
		
		View view = controller.getView();
		view.update();
	}

}
