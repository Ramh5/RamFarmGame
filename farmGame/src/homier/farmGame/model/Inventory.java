package homier.farmGame.model;

import java.util.ArrayList;

public class Inventory {

	private ArrayList<String> productList;
	private ArrayList<Double> quantityList;
	
	public Inventory(){
		productList = new ArrayList<String>();
		quantityList = new ArrayList<Double>();
	}
	
	//TODO + fonctionality to add quantity to some already existing product in the list and append only if a new item is added.
	public void add(String product, double quantity){
		productList.add(product);
		quantityList.add(quantity);
	}
	
	
	public String toString(){
		String str="";
		for(int i=0;i<productList.size();i++){
			str += productList.get(i) +": "+ quantityList.get(i) + "kg\n";
		}
		
		return str;
	}
}
