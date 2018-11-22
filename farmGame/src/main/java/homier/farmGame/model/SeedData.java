package homier.farmGame.model;

import java.util.TreeMap;

public class SeedData {
	

	private String name;
	private TreeMap<String,SeedByProd> byProds;
	private double growthRate;
	private int[] tempRange;
	
	
	public SeedData(String name){
		this.name = name;
	}
	
	public SeedData(String name, TreeMap<String,SeedByProd> byProds, double growthRate, int[] tempRange) {
		this(name);
		this.byProds=byProds;
		this.growthRate = growthRate;
		this.tempRange = tempRange;
		
	}
	

	public String getName() {
		return name;
	}

	public TreeMap<String,SeedByProd> getByProds(){
		return byProds;
	}
	
	public double getGrowthRate() {
		return growthRate;
	}

	public int[] getTempRange() {
		return tempRange;
	}

	@Override
	public String toString(){
		String byProdsStr = "";
		for(SeedByProd byProd:byProds.values()) {
			byProdsStr += byProd.getProdName()+", ";
		}
		String str = "Seed: " + name + "\nProducts: \n" +byProdsStr + "\n";
		str+= String.format("Growth rate: %.0f%%\n", growthRate);
		str+= String.format("Temperature range: %d to %d \u00b0C", tempRange[0], tempRange[1]);
		return str;
	}
	
}
