package homier.farmGame;


import java.util.ArrayList;


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
		this.tileList = new ArrayList<Tile>();
		
		//initialize the arrayList the required size with empty tiles 
		while(tileList.size() < Game.gridColumns*Game.gridRows) tileList.add(new Tile()); 
		/*
		for(int i=0;i<Game.gridColumns;i++){
			for(int j=0;j<Game.gridRows;j++){
				this.add(new Tile().getImageView(), i, j);
			}
		}
		*/
		//set mouse click action for a particular node
		/*
		 grid.getChildren().get(11).setOnMouseClicked(e->{
			//grid.setPadding(new Insets(10));
		});
		*/
		
	}
	 //hardcoded to set a dirt tile on the 
	/*
	public void setTile(int tileNum){
		FarmPlot dirtPlot = new FarmPlot("DIRT_PLOT", new Image("dirt_tile.png"), 0, 0);
		grid.getChildren().remove(tileNum); 
		grid.add(dirtPlot.getImageView(), 2, 5); //TODO hardcoded for the specifique case tileNum = 25, has to be implemented properly
	}
	*/
	
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
