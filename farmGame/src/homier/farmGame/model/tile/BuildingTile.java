package homier.farmGame.model.tile;

import homier.farmGame.model.Weather;

public class BuildingTile extends Tile{

	
	private int state;//level of the house
	
	public BuildingTile() {
		super("BuildingTile");
		state=0;
		
	}

	
	@Override
	public void update(double dTime, Weather wx) {
		
	}
	
	
	public int getState(){
		return state;
	}
	
	public void setState(int state){
		this.state = state;
	}
	
	 
}
