package homier.farmGame.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Employee {

	private String name;
	private double salary;
	private DoubleProperty energy;
	private boolean isWorking;
	private FarmTask currentTask;
	
	
	public Employee(String name, double salary) {
		this.name=name;
		energy=new SimpleDoubleProperty(1000);
		isWorking=false;
		
		currentTask = new FarmTask();
	}

	public String getName(){
		return name;
	}
	
	public void setTask(FarmTask task){
		
		currentTask=task;
	}
	
	public FarmTask getTask(){
		return currentTask;
	}
	
	public void spendEnergy(double energySpent){
		energy.set(energy.get()-energySpent);
	}
	
	public double getEnergy(){
		return energy.get();
	}

	public DoubleProperty energyProperty(){
		return energy;
	}
	
	public double getSalary(){
		return salary;
	}
	
	public String toString(){
		return name;	
	}

	
}
