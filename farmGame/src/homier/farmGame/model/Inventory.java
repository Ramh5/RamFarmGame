package homier.farmGame.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Inventory {
	private double money;
	private HashMap<String, ArrayList<Product>> data;
	private HashMap<String, Product> averageData;
	
	public Inventory(){
		money = 1000;
		data=new HashMap<>();
		averageData=new HashMap<>();
	}
	
	/**TODO find a way to calculate and display total Price
	 * calculates the total quantity and average fresh and quality of each product in the inventory 
	 * and stores it in HashMap<String, Product> averageData
	 */
	public void calculateAverageData(){
		averageData.clear();
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			String prodName = entry.getKey();
			
			double totQty=0;
			double avFresh=0;
			double avQual=0;
			
			for(Product prod : entry.getValue()){
				totQty += prod.getQty();
				avFresh += prod.getQty()*prod.getFresh();
				avQual += prod.getQty()*prod.getQual();
			}
			Product averageProd = new Product(prodName, totQty, (int)(avFresh/totQty), (int)(avQual/totQty));
			averageData.put(prodName, averageProd);
		}
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
		String str="Cash : " + money + "$\n\n";
		
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			str += entry.getKey();
			for(Product prod : entry.getValue()){
				str += String.format("| %.1f f%d |", prod.getQty(), prod.getFresh());
			}
			str+= " kg\n";
		}
		
		return str;
	}


	public double getMoney(){
		return money;
	}
	
	public void addMoney(double transactionValue){
		money+=transactionValue;
	}
	
	
	public Product getAverageData(String name) {
		return averageData.get(name);
	}

	public HashMap<String, ArrayList<Product>> getData(){
		return data;
	}
	
}

