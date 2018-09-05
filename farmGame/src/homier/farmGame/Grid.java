package homier.farmGame;


import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


/**
 * Grid extends Canvas
 * @author Ram
 * 
 */
public class Grid extends GridPane{
	

	//private GridPane grid;
	private ArrayList<Tile> tileList;
	
	public Grid() {
		this.setMaxSize(Game.tileSize*Game.gridColumns, Game.tileSize*Game.gridRows);
		this.tileList = new ArrayList<Tile>();
		
		//initialize the arrayList the required size with empty tiles 
		while(tileList.size() < Game.gridColumns*Game.gridRows){
			Tile tile = new Tile();
			tileList.add(tile);
			this.getChildren().add(tile.getImageView(0));
		}
		
		init();
	}
	
	//methode to set a popup menu controlled by the different tiles, but needing to update the grid
	public void setUI(){
		for(int i=0;i<tileList.size();i++){
			tileList.get(i).setUI(this,i);
		}
	}
	
	//initialize start game
	private void init(){
		for (int i=0;i<tileList.size();i++){
			
			tileList.set(i, new Tile("FREST_TILE", Game.forestTileImage));
		}
		int[] indexList = {30,31,32,39,41,48,49,50};
		for (int i: indexList){
			tileList.set(i, new FarmPlot("DIRT_PLOT", Game.dirtTileImage, 0, 0));
		}
		tileList.set(40, new Tile("HOUSE_TILE", Game.houseImage));
	}
	
	//sets the tileList of the Grid object
	public void setTileList(ArrayList<Tile> tileList){
		this.tileList = tileList;
	}
	
	//sets a specific tile of the Grid object
	public void setTile(Tile tile, int x, int y){
		int i = Game.gridRows*x+y;  //converts (x,y) into a 1D array indice
		tileList.set(i, tile);
	}
	
	
	public ArrayList<Tile> getTileList(){
		return tileList;
	}
	

}
