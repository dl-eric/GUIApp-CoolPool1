package edu.andover.coolpool.model;

import java.util.ArrayList;

import edu.andover.coolpool.GameManager;
import edu.andover.coolpool.controller.CueBallController;
import edu.andover.coolpool.controller.CueStickController;
import edu.andover.coolpool.controller.PoolScreenController;
import javafx.animation.AnimationTimer;

public class PoolGame {
	//TODO: Add Comments
	
	// Create a reference to game manager here
	GameManager gameManager;
	
	PoolBoard poolBoard;
	CueStick cueStick;
	Player[] players = new Player[2];
	int currPlayerInd = 0;
	boolean gameHasEnded = false;
	boolean sidesAreSet = false;
	
	AnimationTimer timer;
	private CueStickController cueStickController;
	private PoolScreenController poolScreenController;
	private CueBallController cueBallController;
	boolean streak = false;
	
	public PoolGame(PoolScreenController poolScreenController){
		gameManager = GameManager.getInstance();
		
		poolBoard = new PoolBoard();
		setUpCueStick();
		
		players[0] = new Player();
		players[1] = new Player();
		
		cueBallController = new CueBallController();
		cueBallController.addMouseHoverEventHandler(poolBoard.getView(), poolBoard.getBalls()[15]);
		
		
		timer = new AnimationTimer() {
			@Override
			public void handle(long timestamp) {
				poolBoard.update();
				if (poolBoard.stable()) { 
					this.stop();
					updatePoints(poolBoard.pocketedBalls());
					poolBoard.resetPocketedBalls();
					cueStick.setCanMove(true);
					cueStick.setCanReset(true);
				}
			}
		};
		
		this.poolScreenController = poolScreenController;
	}
	
	public void turn(){
		timer.start();
	}
	
	public void setSides(int ballId){
		players[currPlayerInd].setBallType(ballId);
		players[(currPlayerInd+1)%2].setBallType((ballId + 1)%2);
		poolScreenController.setBallColorText(currPlayerInd, ballId);
		sidesAreSet = true;
	}
	
	private boolean pocketedOther(ArrayList<Ball> pocketedBalls){
		int playerBallType = players[currPlayerInd].getBallType();
		for (Ball b: pocketedBalls){
			if (playerBallType != -1 && playerBallType != b.getId()
					&& (b.getId() == 0 || b.getId() == 1)){
				return true;
			}
		}
		return false;
	}
	
	public void switchPlayer(){
		currPlayerInd = (currPlayerInd + 1)%2;
		streak = false;
		poolScreenController.setPlayerTurnText(currPlayerInd, streak);
	}
	
	public boolean pocketedCueBall(ArrayList<Ball> pocketedBalls){
		for (Ball b: pocketedBalls){
			if ( b.getId() == 2) return true;
		}
		return false;
	}
	
	public boolean pocketedEightBall(ArrayList<Ball> pocketedBalls){
		for (Ball b: pocketedBalls){
			if ( b.getId() == 3) return true;
		}
		return false;
	}
	
	public void continuePlayer(){
		streak = true;
		poolScreenController.setPlayerTurnText(currPlayerInd, streak);
	}

	public void updatePoints(ArrayList<Ball> pocketedBalls){
		int size = pocketedBalls.size();
		if (size == 0){
			poolScreenController.setStatusPlayerFailed(currPlayerInd);
			switchPlayer();
		}
		else{
			for (int i = 0; i < size; i ++){
				int ballId = pocketedBalls.get(i).getId();
				if (ballId == 0 || ballId == 1){
					if (!sidesAreSet){
						setSides(ballId);
					}
					if (players[currPlayerInd].getBallType() == ballId){
						players[currPlayerInd].addPoint();
					}
					else{
						players[(currPlayerInd+1)%2].addPoint();
					}
				}
			}
			
			poolScreenController.setPointsText(players[0].getPoints(), 
					players[1].getPoints());
			
			if (pocketedEightBall(pocketedBalls)){
				gameHasEnded = true;
				gameManager.initEndScreen();
			}
			
			else if (pocketedCueBall(pocketedBalls)){
				poolScreenController.setStatusPocketedCueBall(currPlayerInd);
				poolBoard.resetCueBall();
				cueStick.setCueBall(poolBoard.getBalls()[15]);
				switchPlayer();
			}		
			else if (pocketedOther(pocketedBalls)){
				poolScreenController.setStatusPocketedOther(currPlayerInd);
				switchPlayer();
			}
			else{
				poolScreenController.setStatusPlayerSucceeded(currPlayerInd);
				continuePlayer();
			}
		}
	}

	public PoolBoard getPoolBoard(){
		return poolBoard;
	}
	
	private void setUpCueStick() {
		cueStickController = new CueStickController();
		cueStick = new CueStick(poolBoard.getBalls()[15], this);
		cueStickController.addMouseHoverEH(poolBoard.getView(), cueStick);
		cueStickController.addMousePressedEH(poolBoard.getView(), cueStick);
		cueStickController.addMouseReleasedEH(poolBoard.getView(), cueStick);
		cueStickController.addMouseDraggedEH(poolBoard.getView(), cueStick);
		poolBoard.getView().getPane().getChildren().add(cueStick.getView());
	}
	
	public Player[] getPlayers(){ return players; }
}