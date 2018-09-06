package homier.farmGame.controller;

import javafx.scene.text.Font;
import homier.farmGame.utils.Tools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;


public class MainController {
	
	
	@FXML private GridPane gameGridPane;
	@FXML private Label clockLabel;
	@FXML private ToggleButton pauseButton;
	@FXML private Label pauseLabel;
	
	
	
	private double gameTime=0;
	
	
	
	
	public void update(double dTime) {
		gameTime+=dTime;
		clockLabel.setText(Tools.elapsedSecondsFormatter(gameTime));
		
	}
	
	public void render() {
		// TODO Auto-generated method stub
		
	}
	
	public void initialize(){
		clockLabel.setText(Tools.elapsedSecondsFormatter(gameTime));
		pauseButton.setGraphic(new ImageView(new Image("Button-Pause.png", 32,32,true,true)));
		pauseButton.setBackground(Background.EMPTY);
	}
	
	@FXML
	private void pauseButtonAction(ActionEvent event){
		if(pauseButton.isSelected()){			
			pauseLabel.setText("Game Paused");
			pauseLabel.setTextFill(Color.RED);
			pauseLabel.setFont(new Font("Arial Bold",12));
			pauseButton.setGraphic(new ImageView(new Image("Button-Play.png", 32,32,true,true)));
		}else{
			pauseLabel.setText("");
			pauseButton.setGraphic(new ImageView(new Image("Button-Pause.png", 32,32,true,true)));
		}
	}
	
	public boolean getPaused(){
		return pauseButton.isSelected();
	}
}
