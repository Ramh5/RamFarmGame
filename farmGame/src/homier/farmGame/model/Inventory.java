package homier.farmGame.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Inventory {
	private HashMap<String, ArrayList<Product>> data;
	
	public Inventory(){
		data=new HashMap<>();
	}
	
	
	/**
	 * Add a product to the inventory, can add a product with negative quantity to remove products
	 * @param product
	 */
	public void addProd(Product product){
		ArrayList<Product> currList = data.get(product.getName());
		
		if(currList==null){
			data.put(product.getName(), new ArrayList<Product>(Arrays.asList(product)));
			return;
		}
		
		for(int i=0; i<currList.size();i++){
			Product elem = currList.get(i);
			if(product.getFresh()==elem.getFresh()){
				elem.setQty(product.getQty()+elem.getQty());
				return;
			} 
		}
		currList.add(product);
	}


	public void update() {
		// TODO Auto-generated method stub
		
	}


	public int[] removeList(TreeMap<String, Double> ingredientList, int[] freshReqList) {
		// TODO Auto-generated method stub
		return new int[]{0};
	}
	
	public String toString(){
		String str="";
		
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			str += entry.getKey();
			for(Product prod : entry.getValue()){
				str += String.format("| %.1f f%d |", prod.getQty(), prod.getFresh());
			}
			str+= " kg\n";
		}
		
		return str;
	}


	public Product getProd() {
		
		return data.get("Wheat").get(0);
	}
	
	public HashMap<String, ArrayList<Product>> getData(){
		return data;
	}
}

