package homier.farmGame.controller;

import javafx.scene.text.Font;
import homier.farmGame.model.Game;
import homier.farmGame.utils.Tools;
import homier.farmGame.view.Renderer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class Engine {
	
	
	@FXML private GridPane gameGridPane;
	@FXML private Label clockLabel;
	@FXML private ToggleButton pauseButton;
	@FXML private Label pauseLabel;
	@FXML private VBox mouseOverPanel;
	
	private Game game = new Game();
	private Renderer renderer;
	private double gameTime = 0;
	
	
	public void update(double dTime) {
		
		gameTime+=dTime;
		clockLabel.setText(Tools.elapsedSecondsFormatter(gameTime));
		for(int i=0;i<game.getTileList().size();i++){
			game.getTileList().get(i).update(dTime/App.secondsInADay);
		}
		
	}
	
	public void render() {
		renderer.render(game.getTileList(), gameGridPane, pauseButton, mouseOverPanel);
		
	}
	
	public void initialize(){
		renderer = new Renderer(game.getTileList(), gameGridPane);
		clockLabel.setText(Tools.elapsedSecondsFormatter(gameTime));
		pauseButton.setGraphic(new ImageView(new Image("Button-Pause.png", 32,32,true,true)));
		pauseButton.setBackground(Background.EMPTY);
	}
	
	@FXML
	private void pauseButtonAction(ActionEvent event){
		if(pauseButton.isSelected()){	
			pauseLabel.setText("Jeu en pause");
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
