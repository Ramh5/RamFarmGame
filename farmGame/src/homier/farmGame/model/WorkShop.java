package homier.farmGame.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WorkShop extends Inventory {
	
	private HashMap<String,RecipeBook> workShopRecipeList = new HashMap<String, RecipeBook>();
	private final ObservableList<Product> selectedIngr = FXCollections.observableArrayList();
	
	public WorkShop() {
		super();
		workShopRecipeList.put("Kitchen", new RecipeBook());//TODO create recipe lists from a database
	}
	
	/**
	 * Calculate the result of the selected recipe given the selected ingredients
	 * @param recipe : the selected recipe for which we want to calculate the result
	 * @return the product resulting in the action (ex the cooked meal)
	 */
	public Product getResult(Recipe recipe) {
		HashMap<String, Double> availProdFactor = new HashMap<String, Double>(recipe.getIngredientList());
		for(Entry<String,Double> entry: availProdFactor.entrySet()) {
			entry.setValue(0.0);
		}
		//availProdFactor.forEach((k,v)->v=0.0);
		
		ArrayList<Product> selectedIngrCopy = new ArrayList<>(selectedIngr);
		
		ArrayList<Product> ingrToBeUsed = new ArrayList<Product>();
		
		Product result = new Product(recipe.getName(), 0, 0, 0);
		
		//make a map of ratios to determine the limfactor ingredient
		for(Product prod:selectedIngr) {
			String name = prod.getName();
			Double qtyNeeded = recipe.getIngredientList().get(name);
			if(qtyNeeded!=null) {
				availProdFactor.put(name, prod.getQty()/qtyNeeded+availProdFactor.get(name));
				System.out.println(qtyNeeded);
			}
		}
		
		//determine limfactor
		String limIngr="";
		double limFactor=0;
		
		for (Entry<String,Double> entry : availProdFactor.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
			if(limIngr=="") {
				System.out.println(limFactor);
				limIngr=entry.getKey();
				limFactor=entry.getValue();
				System.out.println(limFactor);
			}	
			if(entry.getValue()<limFactor) {
				limIngr=entry.getKey();
				limFactor=entry.getValue();
			}
		}
		
		System.out.println(limFactor);
		//use the limFactor to make the list of ingredients to be used with priority to 
		//less fresh ingredients
		if(limFactor==0) {
			return result;
		}
		
		for(Entry<String,Double> entry : recipe.getIngredientList().entrySet()) {
			String name = entry.getKey();
			Product ingrProd = new Product(entry.getKey(),0,0,0);
			double qtyNeeded = recipe.getIngredientList().get(name);
			
			while(ingrProd.getQty()/qtyNeeded<limFactor) {
				//System.out.println("in the loop" + ingrProd.getQty()/qtyNeeded);
				Product leastFreshProd = new Product(name,0,0,0);

				for(Product prod:selectedIngrCopy) {
					if(prod.getName().equals(entry.getKey())) {
						if(prod.getFresh()>leastFreshProd.getFresh()) {
							leastFreshProd = prod;
						}
					}
				}
				
				leastFreshProd.setQty(Math.min(limFactor*qtyNeeded-ingrProd.getQty(), leastFreshProd.getQty()));
				ingrProd.add(leastFreshProd);

				selectedIngrCopy.remove(leastFreshProd);
				
			}

			ingrToBeUsed.add(ingrProd);
		}

		
		//use the "list of ingredients to be used" to make the average fresh and qual result for the crafted product 
		double totIngrQty = 0;
		int totFresh = 0;
		int totQual = 0;
		for(Product prod:ingrToBeUsed) {
			totIngrQty += prod.getQty();
			totFresh += prod.getQty()*prod.getFresh();
			totQual += prod.getQty()*prod.getQual();
		}
		
		//TODO implement the crafting, update price, spoil, fix workshop inventory table getting deselected when closing the workshop		
		result.setQty(recipe.getQuantity()*limFactor);
		
		result.setFresh((int)(totFresh/totIngrQty));
		
		result.setQual((int)(totQual/totIngrQty));
		
		return result;
	}
	
	public ObservableList<Product> getSelectedIngr(){
		return selectedIngr;
	}
	
	public RecipeBook getRecipeBook(String workShopName) {
		return workShopRecipeList.get(workShopName);
	}
	
	public String[] getWSList(){
		return (String[]) workShopRecipeList.keySet().toArray();
	}

	/**
	 * Copies an inventory data to fill the WorkShop inventory data with copies of each products
	 * @param inventory : the inventory to be copied into the workshop data
	 */
	public void copyInventory(Inventory inventory) {
		getData().clear();
		for(Entry<String, ArrayList<Product>> entry : inventory.getData().entrySet()){
			for(Product prod : entry.getValue()){
				addProd(new Product(prod));
			}
		}		
	}
	
	
}
