package controller;

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
			text = "AI is thinking, " + ai.getBranchCounter() + "/" + ai.getNumBranches() + " branches considered.";
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
