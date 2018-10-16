package homier.farmGame.model;

import java.util.HashMap;
import java.util.TreeMap;

import homier.farmGame.utils.Tools;



public class ProductData {
	//private String name;
	//private TreeMap<Double, Double> spoilMap;
	private HashMap<String, TreeMap<Double,Double>> spoilMapList;
	
	

	/* TODO add parameters to ProductData
	private double growthRate;
	private int maxYield;
	private TreeMap<Double, Double> yieldMap;
	private int[] tempRange;
    */
	
	public ProductData(){
		spoilMapList = new HashMap<String, TreeMap<Double,Double>>();
		spoilMapList.put("default",Tools.buildTreeMap(cerealSpoilRate));
		spoilMapList.put("Wheat",Tools.buildTreeMap(cerealSpoilRate)); //TODO Balance: specific spoil for each type of product
		spoilMapList.put("Onions",Tools.buildTreeMap(cerealSpoilRate));
		spoilMapList.put("Carrots",Tools.buildTreeMap(cerealSpoilRate));
		spoilMapList.put("Eggs",Tools.buildTreeMap(cerealSpoilRate));
		spoilMapList.put("Omelette",Tools.buildTreeMap(cerealSpoilRate));
	}
	
	
	public TreeMap<Double,Double> getSpoilMap(String name){
		
		return spoilMapList.get(name)==null? spoilMapList.get("default"):spoilMapList.get(name);
	}
	
	//spoil list databases
	
	private double[][] cerealSpoilRate = {{1,.001},{10,.004},{20,.01},{30,.03},{40,.1},{50,.6}};
}
