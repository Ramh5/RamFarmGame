package homier.farmGame.model;

public class Tile {

	private String ID;

	
	public Tile(){
		ID = "EMPTY_TILE";
	}// empty constructor
	
	public Tile(String ID){
		this.ID=ID;
		
	}
	
	
	public void update(double dTime){
		
	}
	
	public String getID(){
		return ID;
	}
	
	
	
	public String toString(){
		return ("ID: " + ID);
	}
	

}
