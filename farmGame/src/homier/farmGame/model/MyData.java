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
	
		
		private static RecipeBook recipeBook;
		private static HashMap<String,ArrayList<Double>> wsEnergyFactorMap;//list of energy factor for each tier for each workshop 
		private static HashMap<String,ArrayList<Double>> wsTimeFactorMap;//list of time factor for each tier for each workshop 
		private static HashMap<String,ProductData> prodDataList;
		private static HashMap<String,SeedData> seedDataList;
		private static HashMap<String,Double> basePriceMap;
		private static HashMap<String,Double> freshDecayMap;
		public final static TreeMap<Double,Double> spoilMap = Tools.buildTreeMap(new double[][]{{100,.0001},{80,.001},{60,.001},{40,.015},{20,.05},{0,.4}});
		//public final static TreeMap<Double,Double> spoilMap = Tools.buildTreeMap(new double[][]{{100,.001},{80,.004},{60,.01},{40,.03},{20,.1},{0,.6}});
		public MyData(){
			
			recipeBook = new RecipeBook(App.RECIPE_LIST_PATH);
			
			prodDataList = new HashMap<>();
			basePriceMap = new HashMap<>();
			freshDecayMap = new HashMap<>();
			String prodDataString = ReadFile.getString(App.PROD_DATA_PATH);
			for(String line:prodDataString.split("\r\n")){
				//System.out.println(line);
				if(line.contains("COMMENTLINE")) continue; //skips a commentline in the file
				if(line.contains("BASEPRICE")){
					String[] split = line.replace("BASEPRICE ", "").replace(" FRESHDECAY ", "").split(";");
					String[] basePriceStrList = split[0].split(",");
					String[] freshDecayStrList = split[1].split(",");
					
					for(int i=0;i<basePriceStrList.length;i=i+2){
						basePriceMap.put(basePriceStrList[i], Double.parseDouble(basePriceStrList[i+1]));
					}
					for(int i=0;i<freshDecayStrList.length;i=i+2){
						//System.out.println(freshDecayStrList[i] + " " + freshDecayStrList[i+1]);
						freshDecayMap.put(freshDecayStrList[i], Double.parseDouble(freshDecayStrList[i+1]));
					}
					//System.out.println("continued");
					continue;
				}
				String name = line.substring(0,line.indexOf("CAT")-1);
				ArrayList<String> categories = new ArrayList<>(Arrays.asList(line.substring(line.indexOf("CAT")+4, line.indexOf(" DETAILS")).split(",")));
				String[] strList = line.substring(line.indexOf("DETAILS")+8).split(",");
				
				// get the base price, can be numeric in the file or from the recipe book
				// or a string and get it from the basePriceMap
				double basePrice = 0;
				if(NumberUtils.isParsable(strList[0])){
					basePrice = Double.parseDouble(strList[0]);
				}else if("rec".equals(strList[0])){
					TreeMap<String,TreeMap<String,Recipe>> allRecipes = recipeBook.getAllRecipes();
					//Only works if the recipe name is exactly the same as its main result name for now
					for(TreeMap<String,Recipe> recMap:allRecipes.values()){
						if(recMap.containsKey(name)){
							basePrice = recMap.get(name).calcBasePrice();
						}
					}
					
				}else{
					//System.out.println(name);
					basePrice = basePriceMap.get(strList[0]);
				}
				
				double freshDecay = freshDecayMap.get(strList[1]);
	
				prodDataList.put(name, new ProductData(name, categories, basePrice, freshDecay));
			}

			for(int i=0;i<100;i++){//iterate a large number of times to try sort out any missing basePrice
				boolean allSet=true;
				for(ProductData prodData:prodDataList.values()){
					String name = prodData.getName();
					if(prodData.getBasePrice()==0){
						allSet=false;
						System.out.println("basePrice not set for " + name + "   i = " + i);
						TreeMap<String,TreeMap<String,Recipe>> allRecipes = recipeBook.getAllRecipes();
						//Only works if the recipe name is exactly the same as its main result name for now
						for(TreeMap<String,Recipe> recMap:allRecipes.values()){
							if(recMap.containsKey(name)){
								prodData.setBasePrice(recMap.get(name).calcBasePrice());
							}
						}
					}
				}
				if(allSet){
					i=100;//skip the loop if no values are missing
				}
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
		
		public static RecipeBook getRecipeBook(){
			return recipeBook;
		}
		
		public static double freshDecayOf(String name){
			return prodDataList.get(name).getFreshDecay();	
		}
		
		/**
		 * @param name of the seed for which we want to get the data
		 * @return the SeedData object for the seed
		 */
		public static SeedData seedDataOf(String name){
			
			return seedDataList.get(name);
		}
		
		/**
		 * @param name of the product for which  we want to get the categories
		 * @return the list of categories for the product
		 */
		
		public static ArrayList<String> categoriesOf(String name){
			//System.out.println(prodDataList.get(name).getName());
			return prodDataList.get(name).getCategories();
		}
		
		public static double basePriceOf(String name){
			if(prodDataList.get(name)==null){
				System.out.println("basePrice not set for " + name);
				return 0;
			}
			return prodDataList.get(name).getBasePrice();
		}
		
		// yieldmaps
		
		
		private double[][] normYieldList = {{0,0}, {50,0}, {80,30}, {95,98}, {100,100}, {110,98}, {115,95}, {130,70}, {150,0}, {160,0}};
}
