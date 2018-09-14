package homier.farmGame.model.tile;

public abstract class Tile {

	private String ID;

	public Tile(String ID){
		this.ID=ID;	
	}
	
	public abstract void update(double dTime);
	
	public String getID(){
		return ID;
	}
	
	public String toString(){
		return ("ID: " + ID);
	}
	

}
