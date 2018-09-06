package homier.farmGame.controller;


import homier.farmGame.logic.Logic;
import homier.farmGame.render.Render;
import homier.farmGame.utils.Tools;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application{
	
	public static float gameSpeed = 50;
	public static int secondsInADay = 300;
	public static int tileSize = 100, gridColumns = 10, gridRows = 9;
	public static int width = gridColumns*tileSize;
	public static int height = gridRows*tileSize;
	public static Image emptyTileImage = new Image("empty_tile.png",tileSize,tileSize,true, true);
	public static Image dirtTileImage = new Image("dirt_plot.png",tileSize,tileSize,true,true);
	public static Image sown1Image = new Image("sown1_plot.png",tileSize,tileSize,true,true);
	public static Image sown2Image = new Image("sown2_plot.png",tileSize,tileSize,true,true);
	public static Image wheat1Image = new Image("wheat1_plot.png",tileSize,tileSize,true,true);
	public static Image wheat2Image = new Image("wheat2_plot.png",tileSize,tileSize,true,true);
	public static Image houseImage = new Image("farmhouse.png",tileSize,tileSize,true,true);
	public static Image forestTileImage = new Image("summer_tile.png",tileSize,tileSize,true,true);
	
	private Engine engine;
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/homier/farmGame/view/MainUI.fxml"));
		Parent root = loader.load();
		engine=loader.getController();
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
		
		LongValue lastNanoTime = new LongValue( System.nanoTime() );
		DoubleValue timer = new DoubleValue(0);
		
		
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				
				// calculate time since last update.
				double dTime = (currentNanoTime - lastNanoTime.value) / 1e9;
                lastNanoTime.value = currentNanoTime;
                
                if (engine.getPaused()) return;
                
                timer.value += dTime;
                
                engine.update(dTime*gameSpeed);
                
                engine.render();   
			}//handle() methode
		}.start();
	}//start() method

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