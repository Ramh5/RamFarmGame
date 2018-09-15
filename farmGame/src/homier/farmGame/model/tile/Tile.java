package homier.farmGame.model.tile;

import homier.farmGame.model.Weather;

public abstract class Tile {

	private String ID;

	public Tile(String ID){
		this.ID=ID;	
	}
	
	public abstract void update(double dTime, Weather wx);
	
	public String getID(){
		return ID;
	}
	
	public String toString(){
		return ("ID: " + ID);
	}
	

}
