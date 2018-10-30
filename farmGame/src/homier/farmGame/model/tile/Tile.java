package homier.farmGame.model.tile;

import homier.farmGame.model.Weather;

public abstract class Tile {

	
	transient private String type;// flag for Gson parsing
	
	
	public Tile(String type){
		
		this.type = type;
	}
	
	public abstract void update(double dTime, Weather wx);
	
	
	public String toString(){
		return ("Tile type: " + type);
	}

	public String getType() {
		return type;
	}
	

}
