package homier.farmGame.model.tile;

import homier.farmGame.model.Weather;

public abstract class Tile {

	private String ID;
	@SuppressWarnings("unused")
	transient private String type;// flag for Gson parsing
	
	
	public Tile(String ID, String type){
		this.ID=ID;
		this.type = type;
	}
	
	public abstract void update(double dTime, Weather wx);
	
	public String getID(){
		return ID;
	}
	
	public String toString(){
		return ("ID: " + ID);
	}
	

}
