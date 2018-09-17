package homier.farmGame.model;

public class FarmTask {
	private String name;
	private double energyCost;
	private int timeCost;
	private double startedAt;
	
	public FarmTask(String name, double energyCost, int timeCost){
		this.name=name;
		this.energyCost=energyCost;
		this.timeCost=timeCost;
		startedAt=0;
	}
	
	
	
}
