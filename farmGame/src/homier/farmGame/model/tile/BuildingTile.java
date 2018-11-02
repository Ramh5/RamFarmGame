package homier.farmGame.model.tile;

import homier.farmGame.model.Weather;

public class BuildingTile extends Tile{

	private String wsType;
	private int state;//level of the house/workshop
	private double storageSize;//other than grains storage
	private double siloSize;//grains storage
	
	


	private BuildingTile() {
		super("BuildingTile");
		state=0;
		
	}
	
	public BuildingTile(String wsType, int state, double storageSize, double siloSize) {
		this();
		this.wsType = wsType;
		this.state = state;
		this.storageSize = storageSize;
		this.siloSize = siloSize;
	}



	@Override
	public void update(double dTime, Weather wx) {
		
	}
	
	public String getWsType() {
		return wsType;
	}

	public void setWsType(String wsType) {
		this.wsType = wsType;
	}

	public double getStorageSize() {
		return storageSize;
	}

	public void setStorageSize(double storageSize) {
		this.storageSize = storageSize;
	}

	public double getSiloSize() {
		return siloSize;
	}

	public void setSiloSize(double siloSize) {
		this.siloSize = siloSize;
	}
	public int getState(){
		return state;
	}
	
	public void setState(int state){
		this.state = state;
	}
	
	 
}
