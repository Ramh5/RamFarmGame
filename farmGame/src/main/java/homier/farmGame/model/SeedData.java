package homier.farmGame.model;

import java.util.TreeMap;

public class SeedData {
	

	private String name;
	private String prodName;
	private double growthRate;
	private int maxYield;
	private TreeMap<Double, Double> yieldMap;
	private int[] tempRange;
	
	public SeedData(String name){
		this.name = name;
	}
	public SeedData(String name, String prodName, double growthRate, int maxYield, TreeMap<Double, Double> yieldMap, int[] tempRange) {
		this(name);
		this.prodName = prodName;
		this.growthRate = growthRate;
		this.maxYield = maxYield;
		this.yieldMap = yieldMap;
		this.tempRange = tempRange;
	}
	

	public String getName() {
		return name;
	}

	public String getProdName() {
		return prodName;
	}

	public double getGrowthRate() {
		return growthRate;
	}

	public int getMaxYield() {
		return maxYield;
	}

	public TreeMap<Double, Double> getYieldMap() {
		return yieldMap;
	}

	public int[] getTempRange() {
		return tempRange;
	}

	@Override
	public String toString(){
		String str = "Seed: " + name + "\nProduct: " + prodName + "\n";
		str+= String.format("Growth rate: %.0f%%\n", growthRate);
		str+= String.format("Temperature range: %d to %d \u00b0C", tempRange[0], tempRange[1]);
		return str;
	}
	
}
