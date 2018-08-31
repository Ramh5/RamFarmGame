package homier.farmGame.render;

import homier.farmGame.Game;
import homier.farmGame.Grid;

public class Render {

	public static void render(Grid theGrid){	
		theGrid.getChildren().clear();
		//System.out.println(theGrid.getTileList().size());
		for(int i=0; i<Game.gridColumns; i++){
			for(int j=0; j<Game.gridRows; j++){
				//System.out.println(i + " "+ j);
				//System.out.println(Game.gridRows*i+j);
				theGrid.add(theGrid.getTileList().get(Game.gridRows*i+j).getImageView(), i, j);
			}	
		}
	}//render method
}//Render class
