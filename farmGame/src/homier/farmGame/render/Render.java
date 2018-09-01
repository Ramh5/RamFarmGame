package homier.farmGame.render;

import homier.farmGame.Game;
import homier.farmGame.Grid;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Render {

	public static void render(Grid theGrid){	
		for(int i=0; i<Game.gridColumns; i++){
			for(int j=0; j<Game.gridRows; j++){
				ImageView imageView = theGrid.getTileList().get(Game.gridRows*i+j).getImageView(0);
				GridPane.setConstraints(imageView, i, j);
				theGrid.getChildren().set(Game.gridRows*i+j, imageView);
			}	
		}
	}//render method
}//Render class
