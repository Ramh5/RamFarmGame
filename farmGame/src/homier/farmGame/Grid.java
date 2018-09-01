package homier.farmGame;


import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;


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
	
	//initialize start game
	private void init(){
		for (int i=0;i<tileList.size();i++){
			tileList.set(i, new FarmPlot("DIRT_TILE", Game.dirtTileImage, 0, 0));
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
