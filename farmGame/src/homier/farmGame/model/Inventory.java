package homier.farmGame.model;


import java.util.Map.Entry;
import java.util.TreeMap;

public class Inventory {

	private TreeMap<String, Double> inventory;  
	
	public Inventory(){
		inventory = new TreeMap<>();
		
	}
	
	
	public void addProd(String product, double quantity){
		Double oldValue=inventory.get(product);
		inventory.put(product, oldValue==null ?quantity:oldValue+quantity);
	}
	
	public boolean removeList(TreeMap<String, Double> ingredients ){
		
		for(String ingredient:ingredients.keySet()){
			Double oldVal = inventory.get(ingredient);
			double ingredientVal = ingredients.get(ingredient);		
			if(oldVal==null||oldVal<ingredientVal){
				 return false;
			}
		}
		
		for(String ingredient:ingredients.keySet()){	
			inventory.put(ingredient, inventory.get(ingredient)-ingredients.get(ingredient));
		}	
		
		return true;
	}
	
	public TreeMap<String, Double> getInventory(){
		return inventory;
	}
	
	public String toString(){
		String str="";
		for(Entry<String,Double> entry : inventory.entrySet()){
			str += String.format("%s: %.1f kg\n", entry.getKey(), entry.getValue());//entry.getKey() +": "+ entry.getValue()+ " kg\n";
		}
		return str;
	}
}
