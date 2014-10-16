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

package controller;

import view.View;

/**
 * AI_Thread is a backend thread that the GUI event can kick off so that
 * the program is responsive and the GUI doesn't have to wait until search
 * and evaluation is done to update the board.
 * @author Matthew
 *
 */
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
		
		synchronized(controller.getModel()){
		controller.processMove(ai.move(isWhiteTurn));
		
		View view = controller.getView();
		view.update();
	}
	}
}
