package homier.farmGame.controller;

import homier.farmGame.model.Employee;
import homier.farmGame.model.Game;
import homier.farmGame.utils.GameClock;
import homier.farmGame.view.Renderer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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
	@FXML private Label wxToday;
	@FXML private Label wxTomorrow;
	@FXML private Label energyLabel;
	@FXML private ChoiceBox<Employee> employeeChoice;
	@FXML private Label taskName1;
	@FXML private ProgressIndicator taskProgress1;
	private Game game = new Game();
	private Renderer renderer;
	private GameClock gameClock;
	private boolean manPaused;
	
	
	
	public void update(double dTime) {

		gameClock.update(dTime);
		clockLabel.setText(gameClock.toString());
		
		//if it is a new day, update the forcast
		if(gameClock.isNewDay()){
			game.getWxForcast().forcastNewDay(gameClock);
			wxToday.setText("Today: "+game.getWxForcast().getToday().toString());
			wxTomorrow.setText("Tomorrow: "+game.getWxForcast().getTomorrow().toString());
			gameClock.setIsNewDay(false);
		}
		
		//update energy and task name when task is in progress
		
		taskProgress1.setProgress(getActiveEmployee().getTask().getTaskProgress(gameClock.getTotalSeconds()));
		taskName1.setText(getActiveEmployee().getTask().getName());
		
		//update each tile (only update the model, ie growth, yield)
		for (int i = 0; i < game.getTileList().size(); i++) {
			game.getTileList().get(i).update(dTime / gameClock.getSecondsInADay(),game.getWxForcast().getToday());
		}
		
		

	}

	public void render() {
		renderer.render(this);

	}

	public void initialize() {

		renderer = new Renderer(game.getTileList(), gameGridPane);
		gameClock = new GameClock(300, 0);
		game.getWxForcast().forcastNewDay(gameClock);
		wxToday.setText("Today: "+game.getWxForcast().getToday().toString());
		wxTomorrow.setText("Tomorrow: "+game.getWxForcast().getTomorrow().toString());
		gameClock.setIsNewDay(false);
		clockLabel.setText(gameClock.toString());
		pauseButton.setGraphic(new ImageView(new Image("Button-Pause.png", 32, 32, true, true)));
		pauseButton.setBackground(Background.EMPTY);
		leftTextArea.setText(game.getInventory().toString());
		
		gameSpeedChoice.getItems().addAll(1,2,5,10,50,500);
		gameSpeedChoice.getSelectionModel().select(3);
		App.gameSpeed=gameSpeedChoice.getSelectionModel().getSelectedItem();
		gameSpeedChoice.setOnAction(e->{//TODO could add a call to a methode in the FXML instead of here
			App.gameSpeed=gameSpeedChoice.getSelectionModel().getSelectedItem();
		});
		
		 //employee choice
		employeeChoice.setOnAction(e->{
			energyLabel.textProperty().bind(getActiveEmployee().energyProperty().asString(" Energy: %.0f  "));
		});
		employeeChoice.getItems().addAll(game.getEmployees());
		employeeChoice.getSelectionModel().select(0);
		energyLabel.textProperty().bind(getActiveEmployee().energyProperty().asString(" Energy: %.0f  "));
		//task
		taskName1.setText("TASK");
		
		
		
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
	
	/**
	 * @return the active employee as selected on the employee choice box
	 */
	public Employee getActiveEmployee(){
		return employeeChoice.getSelectionModel().getSelectedItem();
	}
}
