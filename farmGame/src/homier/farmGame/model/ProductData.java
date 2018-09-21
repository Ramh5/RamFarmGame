package homier.farmGame.model;

import java.util.TreeMap;

import homier.farmGame.utils.Tools;

enum Freshness{
	Fresh
}

public class ProductData {
	private String name;
	private TreeMap<Double, Double> spoilMap;
	
	

	/* TODO add parameters to ProductData
	private double growthRate;
	private int maxYield;
	private TreeMap<Double, Double> yieldMap;
	private int[] tempRange;
    */
	
	public ProductData(String name){
		this.name=name;
		spoilMap = Tools.buildTreeMap(cerealSpoilRate); //TODO specific spoil for each type of product
	}
	
	
	
	
	//spoil list databases
	
	private double[][] cerealSpoilRate = {{1,.001},{10,.004},{20,.01},{30,.03},{40,.1},{50,.6}};
}
