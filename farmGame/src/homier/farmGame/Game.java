package homier.farmGame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;



public class Game extends Application {

public static int tileSize = 100, gridColumns = 10, gridRows = 10;
public static int width = gridColumns*tileSize;
public static int height = gridRows*tileSize;


private BorderPane mainPanel;
private Label topLabel;
private Button leftButton;
private GridPane gridPane;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage window) {
		window.setTitle("JeuFerme");

		topLabel = new Label("Farm Game");
		topLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
		topLabel.setTextFill(Color.AQUAMARINE);
		leftButton = new Button("");
		leftButton.setRotate(-90);
		leftButton.setGraphic(new ImageView("empty_tile.png"));
		leftButton.setBorder(Border.EMPTY);
		leftButton.setPadding(Insets.EMPTY);
		gridPane = new GridPane();
		gridPane.add(new ImageView("empty_tile.png"), 0	, 0);
		
		Grid theGrid = new Grid(width, height);
		
		
		mainPanel = new BorderPane(gridPane,topLabel,null,null,leftButton);
		BorderPane.setAlignment(topLabel, Pos.CENTER);
		BorderPane.setAlignment(leftButton, Pos.CENTER);
		mainPanel.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));//TODO use CSS instead
		
		GraphicsContext gc = theGrid.getGraphicsContext2D();

		Font theFont = Font.font("Helvetica", FontWeight.BOLD, 24);
		gc.setFont(theFont);
		gc.setFill(Color.GREEN);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);

	
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				// calculate time since last update.
				
				// game logic
				
				// UI logic
				

				// render

				theGrid.render();
			}
		}.start();
		
		Scene myScene = new Scene(mainPanel);
		window.setScene(myScene);
		window.show();
	}
}
