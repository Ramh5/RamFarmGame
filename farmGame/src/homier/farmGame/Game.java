package homier.farmGame;

import homier.farmGame.logic.Logic;
import homier.farmGame.render.Render;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;




public class Game extends Application {

public static int tileSize = 100, gridColumns = 10, gridRows = 9;
public static int width = gridColumns*tileSize;
public static int height = gridRows*tileSize;


public static Image emptyTileImage = new Image("empty_tile.png",tileSize,tileSize,true, true);
public static Image dirtTileImage = new Image("dirt_tile.png",tileSize,tileSize,true,true);
public static Image wheat1Image = new Image("wheat1_plot.png",tileSize,tileSize,true,true);
public static Image wheat2Image = new Image("wheat_field2.png",tileSize,tileSize,true,true);
public static Image houseImage = new Image("farmhouse.png",tileSize,tileSize,true,true);
public static Image forestTileImage = new Image("summer_tile.png",tileSize,tileSize,true,true);

private BorderPane mainPanel;
private StackPane root;
private Canvas effect;
private Label topLabel;
private Button leftButton;
private Grid theGrid;
private Scene myScene ;


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
		leftButton.setOnAction(e->{
			topLabel.setText(""+System.currentTimeMillis());
		});
		
		// build a top layer effect
		
		effect = new Canvas(tileSize*gridColumns, tileSize*gridRows);
		effect.setMouseTransparent(true);
		//GraphicsContext gc = effect.getGraphicsContext2D();
		//gc.setFill(new Color(0, 0, 1.0,.2));
		//gc.fillRect(0, 0, tileSize*gridColumns, tileSize*gridRows);
		
		
		
		theGrid = new Grid();
		theGrid.setMouse();
		//theGrid.setTile(new FarmPlot("DIRT_TILE", dirtTileImage, 15, 400), 0,0);
		//theGrid.setTile(new FarmPlot("DIRT_TILE", dirtTileImage, 15, 400), 1,1);
		//theGrid.setTile(new FarmPlot("DIRT_TILE", dirtTileImage, 15, 400), 2,2);
		Render.render(theGrid);
		
		root = new StackPane();
		root.getChildren().addAll(theGrid,effect);
		mainPanel = new BorderPane(root,topLabel,null,null,leftButton);
		BorderPane.setAlignment(topLabel, Pos.CENTER);
		BorderPane.setAlignment(leftButton, Pos.CENTER);
		mainPanel.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));//TODO use CSS instead
		
		

		LongValue lastNanoTime = new LongValue( System.nanoTime() );
		DoubleValue timer = new DoubleValue(0);
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				
			
				// calculate time since last update.
				double dTime = (currentNanoTime - lastNanoTime.value) / 1e9;
                lastNanoTime.value = currentNanoTime;
                timer.value += dTime;
                
                //System.out.println(theGrid.getPadding());
               // System.out.println(theGrid.getHeight());
               //System.out.println(effect.getHeight());
                //System.out.println("timer: "+timer.value);
               // System.out.println("elapsedTime: "+elapsedTime);
				// game logic
				
                if(timer.value>0.001){
                	Logic.update(theGrid,dTime);
                	timer.value=0;
                	
                }
				// UI logic
				

				// render
                Render.render(theGrid);
				
			}
		}.start();
		
		myScene = new Scene(mainPanel,1100, 1000); 
		window.setScene(myScene);
		window.show();
	}
}

class DoubleValue{
	public double value;
	public DoubleValue(double d){
		value = d;
	}
}
class LongValue{
    public long value;  
    public LongValue(long i){
        value = i;
    }
}