package homier.farmGame.model;

import java.util.TreeMap;

public class Recipe {
	
	private String name;
	private TreeMap<String,Double> ingredientList;
	private double quantity;
	
	public Recipe(String name,TreeMap<String,Double> ingredientList, double quantity){
		this.name=name;
		this.ingredientList=ingredientList;
		this.quantity=quantity;
	}

	public String getName() {
		return name;
	}
	public TreeMap<String, Double> getIngredientList() {
		return ingredientList;
	}

	public double getQuantity() {
		return quantity;
	}
	
	
}
