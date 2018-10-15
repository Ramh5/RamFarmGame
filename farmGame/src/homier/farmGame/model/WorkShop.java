package homier.farmGame.model;


import java.util.ArrayList;
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
