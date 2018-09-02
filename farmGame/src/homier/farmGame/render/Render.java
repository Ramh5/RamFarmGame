package homier.farmGame.render;

import homier.farmGame.Game;
import homier.farmGame.Grid;
import homier.farmGame.Tile;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Render {

	public static void render(Grid theGrid){	
		for(int i=0; i<Game.gridColumns; i++){
			for(int j=0; j<Game.gridRows; j++){
				Tile tile = theGrid.getTileList().get(Game.gridRows*i+j);
				GridPane.setConstraints(tile.getImageToRender(), i, j);
				theGrid.getChildren().set(Game.gridRows*i+j, tile.getImageToRender());
				//System.out.println(theGrid.getTileList().get(Game.gridRows*i+j).toString());
			}	
		}
	}//render method
}//Render class
