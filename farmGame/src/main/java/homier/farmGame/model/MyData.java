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
import javafx.util.Pair;

public class MyData {
	
		
		private static RecipeBook recipeBook;
		private static HashMap<String,ProductData> prodDataList;
		private static HashMap<String,SeedData> seedDataList;
		private static HashMap<String,Double> basePriceMap;
		private static HashMap<String,Double> freshDecayMap;
		private static HashMap<String,TreeMap<Double,Double>> yieldMaps;
		public final static TreeMap<Double,Double> spoilMap = Tools.buildTreeMap(new double[][]{{100,0},{80,.0005},{60,.002},{40,.015},{20,.05},{0,.4}});
		
		
		public MyData(){
			new WaterData();// do not need to create a variable, will be accessed with WaterData's static methods
			recipeBook = new RecipeBook(App.RECIPE_LIST_PATH);
			prodDataList = new HashMap<>();
			basePriceMap = new HashMap<>();
			freshDecayMap = new HashMap<>();
			String prodDataString = ReadFile.getStringOfResFile(App.PROD_DATA_PATH);
			prodDataString=prodDataString.replaceAll("(?m)^COMMENTLINE.*(?:\r?\n)?", "").replace("BASEPRICE ", "").replace("FRESHDECAY ", "");//remove all lines starting with COMMENTLINE 
			//get and then remove BASEPRICE and FRESHDECAY lines which are now that 2 first lines of the file after removing COMMENTLINEs
			
				String basePriceStr = prodDataString.substring(0, prodDataString.indexOf("\r\n")+2);//get and then remove 
				prodDataString = prodDataString.replaceFirst(basePriceStr, "");
				String freshDecayStr = prodDataString.substring(0, prodDataString.indexOf("\r\n")+2);
				prodDataString = prodDataString.replaceFirst(freshDecayStr, "");
				
				String[] basePriceStrList = basePriceStr.split(";");
				String[] freshDecayStrList = freshDecayStr.split(";");
					
				//populate the maps
				for(int i=0;i<basePriceStrList.length;i=i+2){
					basePriceMap.put(basePriceStrList[i], Double.parseDouble(basePriceStrList[i+1]));
					System.out.println(basePriceStrList[i] + " "+ basePriceStrList[i+1]);
				}
				for(int i=0;i<freshDecayStrList.length;i=i+2){
					//System.out.println(freshDecayStrList[i] + " " + freshDecayStrList[i+1]);
					freshDecayMap.put(freshDecayStrList[i], Double.parseDouble(freshDecayStrList[i+1]));
				}
			

			for(String line:prodDataString.split("\r\n")){
				String[] prodStrList = line.split(";");
				
				ArrayList<String> categories = new ArrayList<>(Arrays.asList(prodStrList[1].split(",")));
				
				// get the base price, can be numeric in the file or from the recipe book
				// or a string and get it from the basePriceMap
				double basePrice = 0;
				if(NumberUtils.isParsable(prodStrList[2])){
					basePrice = Double.parseDouble(prodStrList[2]);
				}else if("rec".equals(prodStrList[2])){
					TreeMap<String,TreeMap<String,Recipe>> allRecipes = recipeBook.getAllRecipes();
					//Only works if the recipe name is exactly the same as its main result name for now
					for(TreeMap<String,Recipe> recMap:allRecipes.values()){
						if(recMap.containsKey(prodStrList[0])){
							basePrice = recMap.get(prodStrList[0]).calcBasePrice();
						}
					}
					
				}else{
					//System.out.println(name);
					basePrice = basePriceMap.get(prodStrList[2]);
				}
				
				double freshDecay = 0;
				if(NumberUtils.isParsable(prodStrList[3])){
					basePrice = Double.parseDouble(prodStrList[3]);
				}else{
					freshDecay = freshDecayMap.get(prodStrList[3]);
				}
				prodDataList.put(prodStrList[0], new ProductData(prodStrList[0], categories, basePrice, freshDecay));
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



			
			yieldMaps= new HashMap<String,TreeMap<Double,Double>>();
			yieldMaps.put("norm", Tools.buildTreeMap(normYieldList));
			yieldMaps.put("seed", Tools.buildTreeMap(seedYieldList));
			yieldMaps.put("hay", Tools.buildTreeMap(hayYieldList));
			seedDataList = new HashMap<>();
			String seedDataString = ReadFile.getStringOfResFile(App.SEED_DATA_PATH);
			for(String line:seedDataString.split("\r\n")){
				//System.out.println(line);
				if(line.contains("COMMENTLINE")) continue; //skips a commentline in the file
				String[] data = line.split(";");
				TreeMap<String,SeedByProd> byProds = new TreeMap<>();
				String[] list = data[1].split(",");
				for(int i=0;i<list.length;i=i+3) {
					System.out.println(list[i]);
					byProds.put(list[i], new SeedByProd(list[i], Integer.parseInt(list[i+1]), yieldMaps.get(list[i+2])));
				}
				
				
				SeedData seedData = new SeedData(data[0], byProds, Double.parseDouble(data[2]), 
												new int[]{Integer.valueOf(data[3]),Integer.parseInt(data[4])});
//				SeedData seedData = new SeedData(data[0], data[1], Double.valueOf(data[2]), Integer.valueOf(data[3]), 
//						Tools.buildTreeMap(normYieldList), new int[]{Integer.valueOf(data[5]),Integer.valueOf(data[6])});
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
		private double[][] seedYieldList = {{0,0}, {95,0}, {100,10}, {150,100}, {160,100}};
		private double[][] hayYieldList = {{0,0}, {30,0}, {100,100}, {160,100}};
}
