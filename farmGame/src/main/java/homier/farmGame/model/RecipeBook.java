package homier.farmGame.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.lang3.math.NumberUtils;

import homier.farmGame.utils.ReadFile;
import javafx.util.Pair;

public class RecipeBook {
	
	private static TreeMap<String,TreeMap<String,Recipe>> allRecipes = new TreeMap<String, TreeMap<String,Recipe>>();
	private static HashMap<String,Double> energyCostMap;//String keys to energy cost for use in recipes
	private static HashMap<String,Double> timeCostMap;//String keys to time cost for use in recipes
	private static HashMap<String,ArrayList<Double>> wsEnergyFactorMap;//list of energy factor for each tier for each workshop 
	private static HashMap<String,ArrayList<Double>> wsTimeFactorMap;//list of time factor for each tier for each workshop 
	
	
	/**
	 * Creates a recipeBook using a path to a file containing all recipes for all workshops
	 * @param path
	 */
	public RecipeBook(String path){
		energyCostMap = new HashMap<>();
		timeCostMap = new HashMap<>();
		wsEnergyFactorMap = new HashMap<>();
		wsTimeFactorMap = new HashMap<>();
		String fileString = ReadFile.getStringOfResFile(path);
			
		fileString=fileString.replaceAll("(?m)^COMMENTLINE.*(?:\r?\n)?", "").replace("BASEENERGYCOST ", "").replace("BASETIMECOST ", "");//remove all lines starting with COMMENTLINE 
		//get and then remove BASEENERGYCOST and BASETIMECOST lines which are now that 2 first lines of the file after removing COMMENTLINEs
			
		String energyCostStr = fileString.substring(0, fileString.indexOf("\r\n")+2);//get and then remove 
		
		fileString = fileString.replaceFirst(energyCostStr, "");

		String timeCostStr = fileString.substring(0, fileString.indexOf("\r\n")+2);
		fileString = fileString.replaceFirst(timeCostStr, "");
		String[] energyCostStrList = energyCostStr.split(";");
		String[] timeCostStrList = timeCostStr.split(";");
			
		//populate the maps
		for(int i=0;i<energyCostStrList.length;i=i+2){
			energyCostMap.put(energyCostStrList[i], Double.parseDouble(energyCostStrList[i+1]));
		}
		for(int i=0;i<timeCostStrList.length;i=i+2){
			timeCostMap.put(timeCostStrList[i], Double.parseDouble(timeCostStrList[i+1]));
		}
		
		ArrayList<Pair<String,String>> parseStr = parseWSstr(fileString);
		for(Pair<String, String> pair:parseStr){
			allRecipes.put(pair.getKey(), parseRECstr(pair.getValue()));
		}
		
	}
	
	
	/**
	 * Split the fileString into substring for each workshop
	 * @param fileString
	 * @return a pair<workShopName,recipeListString>
	 */
	private ArrayList<Pair<String,String>> parseWSstr(String fileString){
		ArrayList<Pair<String,String>> parsedStr = new ArrayList<>();
		String[] strList = fileString.split("WORKSHOP ");
		//System.out.println(strList[1]);
		
		//the first line (index 0) is empty cause of the way split works so start at i=1
		for(int i=1;i<strList.length;i++){
			//System.out.println(strList[i]);
			String firstLine = strList[i].substring(0, strList[i].indexOf("\r\n")+2);//get the first line and then remove 
			//System.out.println("FirstLine : \n" +firstLine);
			strList[i] = strList[i].replaceFirst(firstLine, "");
			
			String[] firstLineList = firstLine.split(";");
			String wsName = firstLineList[0];
			
			//populate the wsEnergyFactorMap and wsTimeFactorMap from the first line of the workShop string
			ArrayList<Double> energyFactorList = new ArrayList<>();
			//System.out.println("FirstLine : \n" +firstLine);
			//System.out.println(firstLineList[0]);
			for(String val:firstLineList[1].split(",")){
				energyFactorList.add(Double.parseDouble(val));
			}
			wsEnergyFactorMap.put(firstLineList[0], energyFactorList);
			ArrayList<Double> timeFactorList = new ArrayList<>();
			for(String val:firstLineList[2].split(",")){
				timeFactorList.add(Double.parseDouble(val));
			}
			wsTimeFactorMap.put(firstLineList[0], timeFactorList);
			
			//
			parsedStr.add(new Pair<String,String>(wsName, strList[i]));
		}
		return parsedStr;
	}
	
	/**
	 * Parse a sub string of the recipe_list.txt file to produce a treemap of recipes for
	 * a workshop in particular
	 * @param recipeListString
	 * @return treemap of recipes
	 */
	private TreeMap<String, Recipe> parseRECstr(String recipeListString){
		TreeMap<String, Recipe> recipeList = new TreeMap<String,Recipe>();
		String[] strList = recipeListString.split("\r\n");
		for(int i=0;i<strList.length;i++){
			Recipe recipe = new Recipe(strList[i]);
			recipeList.put(recipe.getName(), recipe);
		}
		return recipeList;
	}
	
	
	
	/**
	 * 
	 * @param workShopName
	 * @return the recipeList of the specified workshop
	 */
	public TreeMap<String,Recipe> getRecipeList(String workShopName){
		
		return allRecipes.get(workShopName)!=null?allRecipes.get(workShopName):new TreeMap<String,Recipe>();
	}
	
	
	public  TreeMap<String,TreeMap<String,Recipe>> getAllRecipes(){
		return allRecipes;
	}
	
	/**
	 * 
	 * @return the workshop names as an array of string
	 */
	public String[] getWSList(){
		
		Object[] objArray = allRecipes.keySet().toArray();
		return Arrays.copyOf(objArray, objArray.length, String[].class);
	}
	
	/**
	 * get the energyCost for a recipe using a key
	 * @param key
	 * @return the energyCost for the key, or 0 if no such key
	 */
	public static double energyCostOf(String key){
		if(NumberUtils.isParsable(key)){//if the key is a number use it directly
			return Double.parseDouble(key);
		}
		Double energyCost = energyCostMap.get(key);
		if(energyCost==null){
			energyCost=0.0;
			System.out.println("no energyCost for key:" + key + "     in RecipeBook.java" );
		}
		return energyCost;
	}
	
	/**
	 * get the timeCost for a recipe using a key
	 * @param key
	 * @return the timeCost for the key, or 0 if no such key
	 */
	public static double timeCostOf(String key){
		if(NumberUtils.isParsable(key)){//if the key is a number use it directly
			return Double.parseDouble(key);
		}
		Double timeCost = timeCostMap.get(key);
		if(timeCost==null){
			timeCost=0.0;
			System.out.println("no timeCost for key:" + key + "     in RecipeBook.java" );
		}
		return timeCost;
	}
	
	/**
	 * get the energy cost Factor of the workshop given a name and a level
	 * @param wsName
	 * @param wsLevel
	 * @return
	 */
	public static double energyFactorOf(String wsName, int wsLevel){
		Double energyFactor = wsEnergyFactorMap.get(wsName).get(wsLevel);
		if(energyFactor==null){
			energyFactor=0.0;
			System.out.println("no energyFactor for key:" + wsName + "     in RecipeBook.java" );
		}
		return energyFactor;
	}
	
	/**
	 * get the time cost Factor of the workshop given a name and a level
	 * @param wsName
	 * @param wsLevel
	 * @return
	 */
	public static double timeFactorOf(String wsName, int wsLevel){
		Double timeFactor = wsTimeFactorMap.get(wsName).get(wsLevel);
		if(timeFactor==null){
			timeFactor=0.0;
			System.out.println("no timeFactor for key:" + wsName + "     in RecipeBook.java" );
		}
		return timeFactor;
	}
	
}
