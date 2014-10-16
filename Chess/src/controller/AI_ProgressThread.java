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

/**
 * AI_ProgressThread is a background thread that loops continuously and keeps
 * a JLabel under the board updated with either the status of the AI in its
 * search, or letting the user know it is their turn.
 * @author Matthew
 *
 */
public class AI_ProgressThread implements Runnable{
	Controller controller;
	AI ai;
	public AI_ProgressThread(Controller controllerIn){
		controller = controllerIn;
		ai = controller.getAI();
	}
	@Override
	public void run() {
		
		while (true){
		String text;
		if (ai.isThinking()){
			text = "Quiet Intrigue is thinking..." + ai.getBranchCounter() + "/" + ai.getNumBranches() + " moves considered.";
		}
		else{
			text = "It is your turn to move";
		}
		
		controller.getView().updateMessageLabel(text);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	}

}
