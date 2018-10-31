package homier.farmGame.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.lang3.math.NumberUtils;

import homier.farmGame.controller.App;
import homier.farmGame.model.SeedData;
import homier.farmGame.utils.ReadFile;
import homier.farmGame.utils.Tools;

public class MyData {
	
		//private static HashMap<String, ArrayList<String>> categoriesData;
		
		private static HashMap<String,ProductData> prodDataList;
		private static HashMap<String,SeedData> seedDataList;
		private static HashMap<String, TreeMap<Double,Double>> spoilMapList;
		private static HashMap<String,Double> basePriceMap;

		
		
		public MyData(){
			
			spoilMapList = new HashMap<String, TreeMap<Double,Double>>();
			spoilMapList.put("default",Tools.buildTreeMap(defaultSpoilList));
			spoilMapList.put("cereal",Tools.buildTreeMap(cerealSpoilList)); 
			spoilMapList.put("seed",Tools.buildTreeMap(seedSpoilList));
			spoilMapList.put("vegy",Tools.buildTreeMap(vegySpoilList));
			spoilMapList.put("animal",Tools.buildTreeMap(animalSpoilList));
			
			prodDataList = new HashMap<>();
			basePriceMap = new HashMap<>();
			String prodDataString = ReadFile.getString(App.PROD_DATA_PATH);
			for(String line:prodDataString.split("\r\n")){
				//System.out.println(line);
				if(line.contains("COMMENTLINE")) continue; //skips a commentline in the file
				if(line.contains("BASEPRICE")){
					String[] bPList = line.substring(10).split(",");
					for(int i=0;i<bPList.length;i=i+2){
						basePriceMap.put(bPList[i], Double.parseDouble(bPList[i+1]));
					}
					continue;
				}
				String name = line.substring(0,line.indexOf("CAT")-1);
				ArrayList<String> categories = new ArrayList<>(Arrays.asList(line.substring(line.indexOf("CAT")+4, line.indexOf(" DETAILS")).split(",")));
				String[] strList = line.substring(line.indexOf("DETAILS")+8).split(",");
				
				// get the base price, can be numeric in the file or from the recipe book
				// or a string and get it from the basePriceMap
				double basePrice;
				if(NumberUtils.isParsable(strList[0])){
					basePrice = Double.parseDouble(strList[0]);
				}else if("rec".equals(strList[0])){
				
					basePrice = 2;//TODO get price from recipe book
				}else{
					
					basePrice = basePriceMap.get(strList[0]);
				}
				
				TreeMap<Double,Double> spoilMap = spoilMapList.get(strList[1]);
	
				prodDataList.put(name, new ProductData(name, categories, basePrice, spoilMap));
			}
		

			

			seedDataList = new HashMap<>();
			String seedDataString = ReadFile.getString(App.SEED_DATA_PATH);
			for(String line:seedDataString.split("\r\n")){
				//System.out.println(line);
				if(line.contains("COMMENTLINE")) continue; //skips a commentline in the file
				String[] data = line.split(",");
				
				SeedData seedData = new SeedData(data[0], data[1], Double.valueOf(data[2]), Integer.valueOf(data[3]), 
						Tools.buildTreeMap(normYieldList), new int[]{Integer.valueOf(data[5]),Integer.valueOf(data[6])});
				seedDataList.put(seedData.getName(), seedData);
			}
		}
		
		
		public static TreeMap<Double,Double> spoilMapOf(String name){
			return prodDataList.get(name).getSpoilMap();
			//return spoilMapList.get(name)==null? spoilMapList.get("default"):spoilMapList.get(name);
		}
		
		/**
		 * @param name of the seed for which we want to get the data
		 * @return the SeedData object for the seed
		 */
		public static SeedData seedDataOf(String name){
			System.out.println(seedDataList);
			return seedDataList.get(name);
		}
		
		/**
		 * @param name of the product for which  we want to get the categories
		 * @return the list of categories for the product
		 */
		
		public static ArrayList<String> categoriesOf(String name){
			return prodDataList.get(name).getCategories();
		}
		
		public static double basePriceOf(String name){
			System.out.println(name);
			return prodDataList.get(name).getBasePrice();
		}
		//spoil list databases and yieldmaps
		private double[][] defaultSpoilList = {{1,.001},{10,.004},{20,.01},{30,.03},{40,.1},{50,.6}};
		private double[][] vegySpoilList = {{1,.001},{10,.004},{20,.01},{30,.03},{40,.1},{50,.6}};
		private double[][] cerealSpoilList = {{1,.001},{15,.004},{30,.01},{45,.03},{60,.1},{75,.6}};
		private double[][] seedSpoilList = {{1,.001},{40,.004},{80,.01},{120,.03},{160,.1},{200,.6}};
		private double[][] animalSpoilList = {{1,.001},{5,.004},{10,.01},{15,.03},{20,.1},{25,.6}};
		private double[][] normYieldList = {{0,0}, {50,0}, {80,30}, {95,98}, {100,100}, {110,98}, {115,95}, {130,70}, {150,0}, {160,0}};
}
