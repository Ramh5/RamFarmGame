package homier.farmGame.controller;






import java.io.File;
import java.util.Optional;

import homier.farmGame.model.MyData;
import homier.farmGame.view.Renderer;
import homier.farmGame.view.RenderingData;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class App extends Application{
	
	public static float gameSpeed = 50;
	public static int gridColumns = 10, gridRows = 9;
	public static int width = gridColumns*RenderingData.tileSize;
	public static int height = gridRows*RenderingData.tileSize;
	public static String SHOP_LIST_PATH, SEED_DATA_PATH, TILE_IMAGES_PATH,
						 PROD_DATA_PATH, RECIPE_LIST_PATH;
	
	public static boolean confirmOnClose = true;
	private Engine engine;
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setOnCloseRequest(e->{
			e.consume();
			stop();
		});
		//System.out.println(javafx.scene.text.Font.getFamilies());
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
                
                engine.updateUI();
                
                if (engine.getPaused()) return;
                
                timer.value += dTime;
                
                engine.update(dTime*gameSpeed);
                
                engine.render();   
			}//handle() methode
		}.start();
	}//start() method
	
	@Override
	public void stop(){
		if(confirmOnClose){
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirm");
			//alert.setGraphic(new ImageView(iconURL + "/stop.png"));
			alert.setHeaderText("You are about to exit the game without saving");
			alert.setContentText("Are you sure you want to exit before saving?");
			ButtonType exitButtonType = new ButtonType("Exit");
			//ButtonType saveButtonType = new ButtonType("Save");
			alert.getButtonTypes().clear();
			alert.getButtonTypes().addAll(ButtonType.CANCEL, /*saveButtonType,*/ exitButtonType);
			// set background color of the save button and the exit button
			//((Button) alert.getDialogPane().lookupButton(saveButtonType)).setStyle("-fx-border-color: #0000ff;");
			((Button) alert.getDialogPane().lookupButton(exitButtonType)).setStyle("-fx-base: #ff8c1a;");
			Optional<ButtonType> result = alert.showAndWait();
			if ((result.isPresent()) && (result.get() == exitButtonType)){
				Platform.exit();
				System.exit(0);
			}/* else if((result.isPresent()) && (result.get() == saveButtonType )){
				FileChooser fc = new FileChooser();
				File file = fc.showSaveDialog(mainPanel.getScene().getWindow());
				if(file!=null){
					data.saveToFile(file.getPath());
					Platform.exit();
					System.exit(0);
				}
			}*/
		}else {
			Platform.exit();
			System.exit(0);
		}
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
