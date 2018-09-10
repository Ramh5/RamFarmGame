package homier.farmGame.model;


import homier.farmGame.controller.App;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tile {

	private String ID;

	
	public Tile(){
		ID = "EMPTY_TILE";
	}// empty constructor
	
	public Tile(String ID){
		this.ID=ID;
		
	}
	
	
	
	public String getID(){
		return ID;
	}
	
	
	
	public String toString(){
		return ("ID: " + ID);
	}
	

}
