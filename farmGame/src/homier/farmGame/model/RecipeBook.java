package homier.farmGame.model;


import java.util.TreeMap;

public class RecipeBook {
	private TreeMap<String,Recipe> recipeList;
	
	public RecipeBook(){
		TreeMap<String,Double> omelette = new TreeMap<String,Double>();
		omelette.put("Onions", 1.0);
		omelette.put("Eggs", 2.0);
		omelette.put("Carrots", 1.0);
		recipeList=new TreeMap<String,Recipe>();
		recipeList.put("Omelette", new Recipe("Omelette", omelette, 3.0));	
		
		TreeMap<String,Double> soupe = new TreeMap<String,Double>();
		soupe.put("Onions", 1.5);
		soupe.put("Potatoes", 2.2);
		soupe.put("Carrots", 0.5);
		soupe.put("Beef", 1.5);
		recipeList.put("Soupe", new Recipe("Soupe", soupe, 4.5));	
		
	}
	
	public TreeMap<String,Recipe> getRecipeList(){
		return recipeList;
	}
}
