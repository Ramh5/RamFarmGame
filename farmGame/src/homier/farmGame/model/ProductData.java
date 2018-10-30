package homier.farmGame.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import homier.farmGame.controller.App;
import homier.farmGame.utils.ReadFile;
import homier.farmGame.utils.Tools;



public class ProductData {
	//private String name;
	//private TreeMap<Double, Double> spoilMap;
	private HashMap<String, TreeMap<Double,Double>> spoilMapList;
	private static HashMap<String,SeedData> seedDataList;
	

	
	
	public ProductData(){
		spoilMapList = new HashMap<String, TreeMap<Double,Double>>();
		spoilMapList.put("default",Tools.buildTreeMap(cerealSpoilRate));
		spoilMapList.put("Blé",Tools.buildTreeMap(cerealSpoilRate)); //TODO Balance: specific spoil for each type of product, maybe by categories?
		spoilMapList.put("Oignons",Tools.buildTreeMap(cerealSpoilRate));
		spoilMapList.put("Carrotes",Tools.buildTreeMap(cerealSpoilRate));
		spoilMapList.put("Oeufs",Tools.buildTreeMap(cerealSpoilRate));
		spoilMapList.put("Omelette",Tools.buildTreeMap(cerealSpoilRate));
		spoilMapList.put("Semences de patates",Tools.buildTreeMap(seedSpoilMap));
		spoilMapList.put("Semences de blé",Tools.buildTreeMap(seedSpoilMap));

		seedDataList = new HashMap<String,SeedData>();
		String seedDataString = ReadFile.getString(App.SEED_DATA_PATH);
		for(String line:seedDataString.split("\r\n")){
			//System.out.println(line);
			if(line.contains("COMMENTLINE")) continue; //skips a commentline in the file
			String[] data = line.split(",");
			
			SeedData seedData = new SeedData(data[0], data[1], Double.valueOf(data[2]), Integer.valueOf(data[3]), 
					Tools.buildTreeMap(normYieldMap), new int[]{Integer.valueOf(data[5]),Integer.valueOf(data[6])});
			seedDataList.put(seedData.getName(), seedData);
		}
	}
	
	
	public TreeMap<Double,Double> getSpoilMap(String name){
		
		return spoilMapList.get(name)==null? spoilMapList.get("default"):spoilMapList.get(name);
	}
	
	public static SeedData getSeedData(String name){
		return seedDataList.get(name);
	}
	//spoil list databases and yieldmaps
	
	private double[][] cerealSpoilRate = {{1,.001},{10,.004},{20,.01},{30,.03},{40,.1},{50,.6}};
	private double[][] seedSpoilMap = {{1,.001},{20,.004},{40,.01},{60,.03},{80,.1},{100,.6}};
	private double[][] normYieldMap = {{0,0}, {50,0}, {80,30}, {95,98}, {100,100}, {110,98}, {115,95}, {130,70}, {150,0}, {160,0}};
}
