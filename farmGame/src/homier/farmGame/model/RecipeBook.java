package homier.farmGame.model;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import homier.farmGame.utils.ReadFile;
import javafx.util.Pair;

public class RecipeBook {
	
	private TreeMap<String,TreeMap<String,Recipe>> allRecipes = new TreeMap<String, TreeMap<String,Recipe>>();
	
	public RecipeBook(){
		TreeMap<String, Recipe> recipeList = new TreeMap<String,Recipe>();
		
		TreeMap<String,Double> omelette = new TreeMap<String,Double>();
		omelette.put("Onions", 1.0);
		omelette.put("Eggs", 2.0);
		omelette.put("Carrots", 1.0);
		recipeList.put("Omelette", new Recipe("Omelette", omelette, 3.0));	
		
		
		
		TreeMap<String,Double> soupe = new TreeMap<String,Double>();
		soupe.put("Onions", 1.5);
		soupe.put("Potatoes", 2.2);
		soupe.put("Carrots", 0.5);
		soupe.put("Beef", 1.5);
		recipeList.put("Soupe", new Recipe("Soupe", soupe, 4.5));	
		
		allRecipes.put("Kitchen", recipeList);
		
	}
	
	/**
	 * Creates a recipeBook using a path to a file containing all recipes for all workshops
	 * @param path
	 */
	public RecipeBook(String path){
		String fileString = fileToString(path);
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
	 * @param path : loads the file at the specified path
	 * @return the string in the file
	 */
	private String fileToString(String path){
		String fileString = "";
		try {
			fileString = ReadFile.getString(path);
		} catch (IOException e) {
			System.out.println("error in RecipeBook reading the workshop file database at " + path);
			e.printStackTrace();
		}
		return fileString;
	}
	
	/**
	 * 
	 * @param workShopName
	 * @return the recipeList of the specified workshop
	 */
	public TreeMap<String,Recipe> getRecipeList(String workShopName){
		
		return allRecipes.get(workShopName)!=null?allRecipes.get(workShopName):new TreeMap<String,Recipe>();
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
