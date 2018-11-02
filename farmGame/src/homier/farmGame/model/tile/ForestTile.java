package homier.farmGame.model.tile;

import homier.farmGame.model.Weather;

public class ForestTile extends Tile {
	private double price;

	private ForestTile() {
		super("ForestTile");
	}

	public ForestTile(double price){
		this();
		this.price=price;
	}
	
	@Override
	public void update(double dTime, Weather wx) {
		
	}
	
	public double getPrice(){
		return price;
	}
}
