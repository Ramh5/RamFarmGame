package homier.farmGame.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import homier.farmGame.utils.ReadFile;
import javafx.util.Pair;

public class RecipeBook {
	
	private TreeMap<String,TreeMap<String,Recipe>> allRecipes = new TreeMap<String, TreeMap<String,Recipe>>();
	private static HashMap<String,Double> energyCostMap;
	private static HashMap<String,Double> timeCostMap;
	
	
	/**
	 * Creates a recipeBook using a path to a file containing all recipes for all workshops
	 * @param path
	 */
	public RecipeBook(String path){
		energyCostMap = new HashMap<>();
		timeCostMap = new HashMap<>();
		String fileString = ReadFile.getString(path);
			
		fileString=fileString.replaceAll("(?m)^COMMENTLINE.*(?:\r?\n)?", "").replace("ENERGYCOST ", "").replace("TIMECOST ", "");//remove all lines starting with COMMENTLINE 
		//get and then remove ENERGYCOST and TIMECOST lines which are now that 2 first lines of the file after removing COMMENTLINEs
			
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
		String[] firstLine = strList[0].split(";");
		
		for(int i=1;i<strList.length;i++){
			String wsName = strList[i].substring(0, strList[i].indexOf("\r\n"));
			String recStr = strList[i].substring(strList[i].indexOf("REC"));
			parsedStr.add(new Pair<String,String>(wsName, recStr));
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
		String[] strList = recipeListString.split("REC ");
		for(int i=1;i<strList.length;i++){
			String recName = strList[i].substring(0, strList[i].indexOf("ING ")-1);
			recipeList.put(recName, new Recipe(strList[i]));
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
	
}
