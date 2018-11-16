package homier.farmGame.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import homier.farmGame.controller.App;

import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WorkShop extends Inventory {
	
	
	private final ObservableList<Product> selectedIngr = FXCollections.observableArrayList();
	private ArrayList<Product> ingrToBeUsed = new ArrayList<Product>();
	private Product result = new Product(null,"EMPTY",0,0,0);
	private FarmTask task = new FarmTask();
	private HashMap<String, Integer> wsLevelMap = new HashMap<>();//this map is set in Engine.java by the method setupAvailWS()
	
	
	public WorkShop() {
		super();
		
	}
	
	/**
	 * Calculate and sets the result of the selected recipe given the selected ingredients
	 * @param wsName : the name of the selected workshop for energy and time calculation
	 * @param recipe : the selected recipe for which we want to calculate the result
	 */
	public void calculateResult(String wsName,Recipe recipe) {
		HashMap<String, Double> availProdFactor = new HashMap<String, Double>(recipe.getIngredients());
		for(Entry<String,Double> entry: availProdFactor.entrySet()) {
			entry.setValue(0.0);
		}
		
		ArrayList<Product> selectedIngrCopy = new ArrayList<>(selectedIngr);
		ingrToBeUsed.clear();
		
		String resultName = recipe.getResults().firstKey();
		
		result=new Product(MyData.categoriesOf(resultName),resultName,0,0,0);
		
		//make a map of ratios to determine the limfactor ingredient
		for(Product prod:selectedIngr) {
			String name = prod.getName();
			Double qtyNeeded = recipe.getIngredients().get(name);
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
		
		for(Entry<String,Double> entry : recipe.getIngredients().entrySet()) {
			String name = entry.getKey();
			//Product ingrProd = new Product(entry.getKey(),0,0,0);
			double qtyNeeded = recipe.getIngredients().get(name);
			double qtyAvail = 0;
			while(qtyAvail/qtyNeeded<limFactor) {
				
				Product leastFreshProd = new Product(null,name,0,0,0);

				for(Product prod:selectedIngrCopy) {
					if(prod.getName().equals(entry.getKey())) {
						if(prod.getFresh()>leastFreshProd.getFresh()) {
							leastFreshProd = prod;
						}
					}
				}
				selectedIngrCopy.remove(leastFreshProd);
				
				leastFreshProd = new Product(leastFreshProd); // copy to not alter the displayed list
				leastFreshProd.setQty(Math.min(limFactor*qtyNeeded-qtyAvail, leastFreshProd.getQty()));
				qtyAvail+=leastFreshProd.getQty();
				ingrToBeUsed.add(leastFreshProd);
			}
		}

		//use the "list of ingredients to be used" to make the average fresh and qual result for the crafted product 
		double totIngrQty = 0;
		double totFresh = 0;
		int totQual = 0;
		for(Product prod:ingrToBeUsed) {
			totIngrQty += prod.getQty();
			totFresh += prod.getQty()*prod.getFresh();
			totQual += prod.getQty()*prod.getQual();
		}
		//TODO change the implementation to allow multi result recipes
		result.setQty(recipe.getResults().firstEntry().getValue()*limFactor);
		result.setFresh(totFresh/totIngrQty);
		result.setQual((int)(Math.round(totQual/totIngrQty)));
		result.updateSpoil();
		
		//set the energy and time cost for this task
		task.setEnergyCost(limFactor*recipe.getBaseEnergyCost()*RecipeBook.energyFactorOf(wsName, wsLevelMap.get(wsName)));
		task.setTimeCost((int)(limFactor*recipe.getBaseTimeCost()*RecipeBook.timeFactorOf(wsName, wsLevelMap.get(wsName))));
		System.out.println(task.getTimeCost());
		task.setName("Crafting " +recipe.getName() + " in the " + wsName);
	}
	
	/**
	 * Starts a task, removes ingredients from inventory
	 * @param employee  employee to be tasked
	 * @param gameInv  inventory to be used for ingredients and result
	 * @param startedAt  the time in game seconds the tasks starts
	 */
	public void startTask(Employee employee,Inventory gameInv, double startedAt) {
		employee.setTask(task); 
		task.startTask(startedAt, employee);
		
		//remove used ingredients from the inventory
		for(Product prod:ingrToBeUsed) {
			prod.setQty(-prod.getQty());
			gameInv.addProd(prod);
		}
		ingrToBeUsed.clear();
		//add the listener to to isComplete of the task to add the result of the crafting
		//to the inventory when completed
		employee.getTask().setResult(result);	
	}
	
	public ObservableList<Product> getSelectedIngr(){
		return selectedIngr;
	}
	
	/**
	 * 
	 * @param workShopName
	 * @return the recipes TreeMap of a particular workshop
	 */
	public TreeMap<String, Recipe> getRecipeList(String workShopName) {
		return MyData.getRecipeBook().getRecipeList(workShopName);
	}
	
	/**
	 * 
	 * @return the String array of all workshop names
	 */
	public String[] getWSList(){
		return MyData.getRecipeBook().getWSList();
	}
	
	public Product getResult() {
		return result;
	}
	
	public FarmTask getTask() {
		return task;
	}
	
	/**
	 * Sets the workshop levels map 
	 * @param wsNames
	 * @param levels
	 */
	public void setWsLevelMap(String[] wsNames, int[] levels){
		for(int i=0;i<wsNames.length;i++){
			wsLevelMap.put(wsNames[i], levels[i]);
		}
	}
	
	
//	public int wsLevelOf(String wsName) {
//		return wsLevelMap.get(wsName);
//	}
	
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
