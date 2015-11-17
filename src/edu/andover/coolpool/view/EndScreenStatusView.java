package edu.andover.coolpool.view;

import java.util.Observable;
import java.util.Observer;

import edu.andover.coolpool.model.EndScreenStatus;
import edu.andover.coolpool.model.PoolGame;
import edu.andover.coolpool.model.PoolGameStatus;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class EndScreenStatusView implements Observer{

	@FXML
	private Text gameOverText;

	private EndScreenStatus endScreenStatus;
	    
    public void setObservable(EndScreenStatus endScreenStatus){
    	this.endScreenStatus = endScreenStatus;
    	update(endScreenStatus, "initial update");
    }

	@FXML
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		if (endScreenStatus == o) {
			gameOverText.setText(endScreenStatus.getGameOverMessage());
		}
	}
}