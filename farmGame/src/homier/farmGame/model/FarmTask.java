package homier.farmGame.model;

import java.util.ArrayList;

import homier.farmGame.model.tile.FarmPlot;
import homier.farmGame.model.tile.Tile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class FarmTask {
	private String name;
	private double energyCost;
	private int timeCost;
	private double startedAt;
	private boolean isComplete;
	
	//optional properties related to the result when task completes
	//depending on the type of task
	private Product result;
	private Tile newTile;
	private int newTileIndex;
	private boolean plow=false;
	private boolean sow=false;
	private String seedName;
	
	
	public FarmTask() {
		name="-----";
		energyCost = 0;
		timeCost = 0;
		startedAt = 0;
		isComplete=true;
	}
	
	public FarmTask(String name, double energyCost, int timeCost, double startedAt){
		this.name=name;
		this.energyCost=energyCost;
		this.timeCost=timeCost;
		this.startedAt=startedAt;
		isComplete=true;
	}

	public void startTask(double startedAt, Employee employee) {
		employee.spendEnergy(energyCost);
		employee.setIsWorking(true);
		isComplete=false;
		
		this.startedAt=startedAt;
	}
	

	public String getName() {
		return name;
	}

	

	public double getEnergyCost() {
		return energyCost;
	}

	

	public int getTimeCost() {
		return timeCost;
	}

	

	public double getStartedAt() {
		return startedAt;
	}

	/**
	 * This method must be called in order for the task to complete(isComplete to turn true) when time is up.
	 * @param currentTime : game clock time in seconds
	 * @return the progress of the task from 0 to 1 and sets 
	 * the isComplete booleanProperty of the task to true when progress reaches 1
	 */
	public double taskProgress(double currentTime, Inventory inventory, Employee employee, ArrayList<Tile> tileList, int[] previousMap){
		double taskProgress = (currentTime-startedAt)/timeCost;
		if(taskProgress>=1&&!isComplete){
			if(result!=null){
				inventory.addProd(result);
			}
			if(newTile!=null){
				tileList.set(newTileIndex, newTile);
				previousMap[newTileIndex]=-1;
				if(plow){
					((FarmPlot) newTile).plow();
					plow=false;
				}
				if(sow){
					((FarmPlot) newTile).sow(seedName);
					sow=false;
				}
				//System.out.println( name+ " task completed " + previousMap + " index "+ newTileIndex + " value " + previousMap[newTileIndex]);
			}
			
			isComplete=true;
			employee.setIsWorking(false);
		}
		return taskProgress;
	}
	
	public boolean isComplete(){
		return isComplete;
	}

	public void setName(String name) {
		this.name=name;
	}
	
	public void setEnergyCost(double energyCost) {
		this.energyCost=energyCost;
		
	}

	public void setTimeCost(int timeCost) {
		this.timeCost=timeCost;
	}
	
	public void setResult(Product prod){
		this.result=new Product(prod);
	}

	public void setNewTile(Tile newTile, int newTileIndex) {
		this.newTile=newTile;
		this.newTileIndex = newTileIndex;
	}
	
	public void setPlow(boolean plow){
		this.plow=plow;
	}
	
	public void setSow(boolean sow, String seedName){
		this.seedName=seedName;
		this.sow=sow;
	}
}
