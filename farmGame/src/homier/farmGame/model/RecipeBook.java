package homier.farmGame.model;

import java.util.List;
import java.util.TreeMap;

public class RecipeBook {
	private TreeMap<String,Recipe> recipeList;
	
	public RecipeBook(){
		TreeMap<String,Double> omelette = new TreeMap<String,Double>();
		omelette.put("Onions", 1.0);
		omelette.put("Eggs", 2.0);
		omelette.put("Carrots", 1.0);
		recipeList=new TreeMap<String,Recipe>();
		recipeList.put("Omelette", new Recipe(omelette, 3.0));	
	}
	
	public TreeMap<String,Recipe> getRecipeList(){
		return recipeList;
	}
}
