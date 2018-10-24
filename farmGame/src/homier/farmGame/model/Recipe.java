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
	
/**
 * creates a Recipe with a String of the form "Soupe ING Patates,2,Carottes,2 RES Soupe,3.5",
 * does not allow multiple results recipe yet
 * 
 * @param string
 */
	public Recipe(String recStr) {
		this.name=recStr.substring(0, recStr.indexOf("ING ")-1);
		
		this.ingredientList = new TreeMap<>();
		String[] ingrStr = recStr.substring(recStr.indexOf("ING ")+4, recStr.indexOf("RES ")-1).split(",");
		for(int i=0;i<ingrStr.length;i=i+2){
			ingredientList.put(ingrStr[i], Double.valueOf(ingrStr[i+1]));
		}
		this.quantity = Double.valueOf(recStr.substring(recStr.lastIndexOf(",")+1));
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
