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
	private ArrayList<Product> ingrToBeUsed = new ArrayList<Product>();
	private Product result = new Product("EMPTY",0,0,0);
	private FarmTask task = new FarmTask();
	
	
	
	public WorkShop() {
		super();
		workShopRecipeList.put("Kitchen", new RecipeBook());//TODO create recipe lists from a database
	}
	
	/**
	 * Calculate and sets the result of the selected recipe given the selected ingredients
	 * @param recipe : the selected recipe for which we want to calculate the result
	 * 
	 * 
	 */
	public void calculateResult(Recipe recipe) {
		HashMap<String, Double> availProdFactor = new HashMap<String, Double>(recipe.getIngredientList());
		for(Entry<String,Double> entry: availProdFactor.entrySet()) {
			entry.setValue(0.0);
		}
		
		ArrayList<Product> selectedIngrCopy = new ArrayList<>(selectedIngr);
		
		result=new Product(recipe.getName(),0,0,0);
		
		//make a map of ratios to determine the limfactor ingredient
		for(Product prod:selectedIngr) {
			String name = prod.getName();
			Double qtyNeeded = recipe.getIngredientList().get(name);
			if(qtyNeeded!=null) {
				availProdFactor.put(name, prod.getQty()/qtyNeeded+availProdFactor.get(name));
			}
		}
		
		//determine limfactor
		String limIngr="";
		double limFactor=0;
		
		for (Entry<String,Double> entry : availProdFactor.entrySet()) {
			
			if(limIngr=="") {
			
				limIngr=entry.getKey();
				limFactor=entry.getValue();
				
			}	
			if(entry.getValue()<limFactor) {
				limIngr=entry.getKey();
				limFactor=entry.getValue();
			}
		}
		
		//use the limFactor to make the list of ingredients to be used with priority to 
		//less fresh ingredients
		if(limFactor==0) {
			return;
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
				selectedIngrCopy.remove(leastFreshProd);
				
				leastFreshProd = new Product(leastFreshProd); // copy to not alter the displayed list
				leastFreshProd.setQty(Math.min(limFactor*qtyNeeded-ingrProd.getQty(), leastFreshProd.getQty()));
				ingrProd.add(leastFreshProd);

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
		
		result.setQty(recipe.getQuantity()*limFactor);
		result.setFresh((int)(totFresh/totIngrQty));
		result.setQual((int)(totQual/totIngrQty));
		result.updateSpoil(getProdData(), 0);
		
		//set the energy and time cost for this task
		task.setEnergyCost(20*limFactor);
		task.setTimeCost((int)(10*limFactor));
	}
	
	public void startTask(Employee employee,Inventory gameInv, double startedAt) {
		employee.setTask(task); // TODO make the task do something after it completes, update UI as soon as employe tasked
		task.startTask(startedAt);
		
		for(Product prod:ingrToBeUsed) {
			System.out.println(prod);
			prod.setQty(-prod.getQty());
			gameInv.addProd(prod);
		}
		ingrToBeUsed.clear();
		
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
	
	public Product getResult() {
		return result;
	}
	
	public FarmTask getTask() {
		return task;
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
