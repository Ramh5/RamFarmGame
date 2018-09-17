package homier.farmGame.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class FarmTask {
	private String name;
	private double energyCost;
	private int timeCost;
	private double startedAt;
	private BooleanProperty isComplete = new SimpleBooleanProperty();
	
	public FarmTask() {
		name="-----";
		energyCost = 0;
		timeCost = 0;
		startedAt = 0;
		isComplete.set(false);
	}
	
	public FarmTask(String name, double energyCost, int timeCost, double startedAt){
		this.name=name;
		this.energyCost=energyCost;
		this.timeCost=timeCost;
		this.startedAt=startedAt;
		isComplete.set(false);
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
	 * 
	 * @param currentTime : game clock time in seconds
	 * @return the progress of the task from 0 to 1 and sets 
	 * the isComplete booleanProperty of the task to true when progress reaches 1
	 */
	public double getTaskProgress(double currentTime){
		double taskProgress = (currentTime-startedAt)/timeCost;
		if(taskProgress>=1) isComplete.set(true);
		return taskProgress;
	}
	
	public BooleanProperty isCompleteProperty(){
		return isComplete;
	}
	
}
