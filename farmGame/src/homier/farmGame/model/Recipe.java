package homier.farmGame.model;

import java.util.TreeMap;

public class Recipe {
	
	private TreeMap<String,Double> ingredientList;
	private double quantity;
	
	public Recipe(TreeMap<String,Double> ingredientList, double quantity){
		this.ingredientList=ingredientList;
		this.quantity=quantity;
	}

	public TreeMap<String, Double> getIngredientList() {
		return ingredientList;
	}

	public double getQuantity() {
		return quantity;
	}
	
	
}
