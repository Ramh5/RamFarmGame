package homier.farmGame.render;

import homier.farmGame.Game;
import homier.farmGame.Grid;
import homier.farmGame.Tile;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Render {

	public static void render(Grid theGrid){	
		for(int i=0; i<Game.gridColumns; i++){
			for(int j=0; j<Game.gridRows; j++){
				
				Tile tile = theGrid.getTileList().get(Game.gridRows*i+j);
				
				Node currentImageView = theGrid.getChildren().get(Game.gridRows*i+j);
				ImageView newImageView = tile.getImageToRender();
				
				if(!newImageView.equals(currentImageView)){
					GridPane.setConstraints(newImageView, i, j);
					theGrid.getChildren().set(Game.gridRows*i+j, newImageView);
					tile.setUI(theGrid, Game.gridRows*i+j);
				}
				
				//System.out.println(theGrid.getTileList().get(Game.gridRows*i+j).toString());
			}	
		}
	}//render method
}//Render class
