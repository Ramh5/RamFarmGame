package homier.farmGame.controller;

import homier.farmGame.model.Game;
import homier.farmGame.utils.GameClock;
import homier.farmGame.view.Renderer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Engine {

	@FXML private GridPane gameGridPane;
	@FXML private Label clockLabel;
	@FXML private ToggleButton pauseButton;
	@FXML private Label pauseLabel;
	@FXML private VBox mouseOverPanel;
	@FXML private TextArea leftTextArea;
	@FXML private ChoiceBox<Integer> gameSpeedChoice;
	private Game game = new Game();
	private Renderer renderer;
	private GameClock gameClock;;
	private boolean manPaused;
	
	public void update(double dTime) {

		gameClock.update(dTime);
		clockLabel.setText(gameClock.toString());
		for (int i = 0; i < game.getTileList().size(); i++) {
			game.getTileList().get(i).update(dTime / gameClock.getSecondsInADay(),game.getWxEngine());
		}

	}

	public void render() {
		renderer.render(this);

	}

	public void initialize() {
		renderer = new Renderer(game.getTileList(), gameGridPane);
		gameClock = new GameClock(300, 0);
		clockLabel.setText(gameClock.toString());
		pauseButton.setGraphic(new ImageView(new Image("Button-Pause.png", 32, 32, true, true)));
		pauseButton.setBackground(Background.EMPTY);
		gameSpeedChoice.getItems().addAll(1,2,5,10,50,500);
		gameSpeedChoice.getSelectionModel().selectFirst();
		leftTextArea.setText(game.getInventory().toString());
		gameSpeedChoice.setOnAction(e->{//TODO could add a call to a methode in the FXML instead of here
			App.gameSpeed=gameSpeedChoice.getSelectionModel().getSelectedItem();
		});
	}

	@FXML
	private void pauseButtonAction(ActionEvent event) {
		updatePauseButton();
		if(pauseButton.isSelected()){
			manPaused=true;
		}else{
			manPaused=false;
		}
		
	}

	public void updatePauseButton(){
		if (pauseButton.isSelected()) {
			pauseLabel.setText("Jeu en pause");
			pauseLabel.setTextFill(Color.RED);
			pauseLabel.setFont(new Font("Arial Bold", 12));
			pauseButton.setGraphic(new ImageView(new Image("Button-Play.png", 32, 32, true, true)));
		} else {
			pauseLabel.setText("");
			pauseButton.setGraphic(new ImageView(new Image("Button-Pause.png", 32, 32, true, true)));
		}
	}
	
	public boolean getPaused() {
		return pauseButton.isSelected();
	}

	public GridPane getGameGridPane() {
		return gameGridPane;
	}

	public Label getClockLabel() {
		return clockLabel;
	}

	public ToggleButton getPauseButton() {
		return pauseButton;
	}

	public Label getPauseLabel() {
		return pauseLabel;
	}

	public VBox getMouseOverPanel() {
		return mouseOverPanel;
	}
	
	public ChoiceBox<Integer> getGameSpeedChoice(){
		return gameSpeedChoice;
	}
	
	public TextArea getLeftTextArea(){
		return leftTextArea;
	}

	public Game getGame() {
		return game;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public GameClock getGameClock() {
		return gameClock;
	}

	public boolean getManPaused() {
		
		return manPaused;
	}
}
