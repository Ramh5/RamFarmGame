package homier.farmGame.model;

import java.util.Map.Entry;
import java.util.TreeMap;

public class Recipe {
	
	private String name;
	private TreeMap<String,Double> ingredients;
	private TreeMap<String,Double> results;
	
	 
	
	public Recipe(String name,TreeMap<String,Double> ingredientList, TreeMap<String,Double> results){
		this.name=name;
		this.ingredients=ingredientList;
		this.results=results;
	}
	
/**
 * creates a Recipe with a String of the form "Soupe ING Patates,2,Carottes,2 RES Soupe,3.5",
 * @param string
 */
	public Recipe(String recStr) {
		this.name=recStr.substring(0, recStr.indexOf(" ING "));
		
		this.ingredients = new TreeMap<>();
		String[] ingrStr = recStr.substring(recStr.indexOf("ING ")+4, recStr.indexOf(" RES ")).split(",");
		for(int i=0;i<ingrStr.length;i=i+2){
			ingredients.put(ingrStr[i], Double.parseDouble(ingrStr[i+1]));
		}
		this.results = new TreeMap<>();
		String[] resultStr = recStr.substring(recStr.indexOf("RES ")+4).split(",");
		for(int i=0;i<resultStr.length;i=i+2){
			results.put(resultStr[i], Double.parseDouble(resultStr[i+1]));
		}
		
	}

	public String getName() {
		return name;
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
		bp *=1.2; // recipe valuation factor
		bp /= results.firstEntry().getValue();
		return bp;
	}
	
}
