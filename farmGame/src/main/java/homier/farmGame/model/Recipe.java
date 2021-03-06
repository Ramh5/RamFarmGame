package homier.farmGame.model;

import java.util.Map.Entry;
import java.util.TreeMap;

public class Recipe {
	
	private String name;
	private double baseEnergyCost;
	private double baseTimeCost;
	private int maturation;
	private TreeMap<String,Double> ingredients;
	private TreeMap<String,Double> results;
	
	
	 
	
	public Recipe(String name, double baseEnergyCost, double baseTimeCost, int maturation, 
			      TreeMap<String,Double> ingredientList, TreeMap<String,Double> results){
		this.name=name;
		this.baseEnergyCost=baseEnergyCost;
		this.baseTimeCost=baseTimeCost;
		this.maturation = maturation;
		this.ingredients=ingredientList;
		this.results=results;
		
	}
	
/**
 * creates a Recipe with a String of the form 
 * "Recipe name; energyCost; timeCost; ingredient,quantity in kg...; result,quantity in kg... (starting with the primary result if multiple results)"
 * @param string : the line for a recipe from the recipe_list.txt file
 */
	public Recipe(String recStr) {
		String[] recStrList = recStr.split(";");
		name=recStrList[0];
		baseEnergyCost=RecipeBook.energyCostOf(recStrList[1]);
		baseTimeCost=RecipeBook.timeCostOf(recStrList[2]);
		maturation=Integer.parseInt(recStrList[3]);
		this.ingredients = new TreeMap<>();
		String[] ingrStr = recStrList[4].split(",");
		for(int i=0;i<ingrStr.length;i=i+2){
			ingredients.put(ingrStr[i], Double.parseDouble(ingrStr[i+1]));
		}
		this.results = new TreeMap<>();
		String[] resultStr = recStrList[5].split(",");
		for(int i=0;i<resultStr.length;i=i+2){
			results.put(resultStr[i], Double.parseDouble(resultStr[i+1]));
		}
		
	}

	public String getName() {
		return name;
	}
	
	public double getBaseEnergyCost() {
		return baseEnergyCost;
	}

	public double getBaseTimeCost() {
		return baseTimeCost;
	}
	
	public int getMaturation() {
		return maturation;
	}
	
	public TreeMap<String, Double> getIngredients() {
		return ingredients;
	}

	public TreeMap<String, Double> getResults() {
		return results;
	}
	
	

	/**
	 * 
	 * @return the basePrice of the main product result of this recipe 
	 * given a certain bonus factor (does not take into acount a secondary product yet)
	 * returns 0 if base price is not set yet for one of the ingredient
	 */
	public double calcBasePrice(){
		double bp = 0;
		for(Entry<String,Double> entry:ingredients.entrySet()){
			if(MyData.basePriceOf(entry.getKey())==0){
				return 0;
			}
			bp += MyData.basePriceOf(entry.getKey())*entry.getValue();
		}
		bp *=(1+baseEnergyCost/3); // recipe valuation factor
		bp /= results.firstEntry().getValue();
		return bp;
	}
	
}
