package homier.farmGame.controller;






import homier.farmGame.model.MyData;
import homier.farmGame.view.Renderer;
import homier.farmGame.view.RenderingData;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application{
	
	public static float gameSpeed = 50;
	public static int gridColumns = 10, gridRows = 9;
	public static int width = gridColumns*RenderingData.tileSize;
	public static int height = gridRows*RenderingData.tileSize;
	public static String SHOP_LIST_PATH, SEED_DATA_PATH, TILE_IMAGES_PATH,
						 PROD_DATA_PATH, RECIPE_LIST_PATH;
	
	
	private Engine engine;
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println(javafx.scene.text.Font.getFamilies());
		PROD_DATA_PATH = getClass().getResource("/database/product_data.txt").getPath();
		SHOP_LIST_PATH = getClass().getResource("/database/shop_list.txt").getPath();
		SEED_DATA_PATH = getClass().getResource("/database/seed_data.txt").getPath();
		RECIPE_LIST_PATH = getClass().getResource("/database/recipe_list.txt").getPath();
		TILE_IMAGES_PATH = getClass().getResource("/tiles").getPath();
		new MyData();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/homier/farmGame/view/MainUI.fxml"));
		Parent root = loader.load();
		engine=loader.getController();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/homier/farmGame/view/FarmGameStyle.css").toExternalForm());
		primaryStage.setScene(scene);
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
