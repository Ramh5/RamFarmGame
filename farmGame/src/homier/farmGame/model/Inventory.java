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
	private ProductData prodData;
	
	public Inventory(){
		money = 1000;
		data=new HashMap<>();
		averageData=new HashMap<>();
		prodData = new ProductData();//TODO productData.getinstane to have just one instance of it accessible everywhere
	}
	
	/**
	 * calculates the total quantity and average fresh and quality of each product in the inventory 
	 * and stores it in HashMap<String, Product> averageData
	 */
	public void calculateAverageData(){

		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			String prodName = entry.getKey();
			Product averageProd = averageData.get(prodName);
			double totQty=0;
			double totSpoilQty=0;
			double avFresh=0;
			double avQual=0;

			for(Product prod : entry.getValue()){
				totQty += prod.getQty();
				totSpoilQty += prod.getSpoilQty();
				avFresh += prod.getQty()*prod.getFresh();
				avQual += prod.getQty()*prod.getQual();
			}
			if(totQty!=0){
				avFresh /= totQty;
				avQual /= totQty;
			}
			if(averageProd==null){
				averageProd = new Product(prodName, totQty, (int)avFresh, (int)avQual);
				averageProd.setSpoilQty(totSpoilQty);
				averageData.put(prodName, averageProd);
			}else{
				averageProd.setQty(totQty);
				averageProd.setSpoilQty(totSpoilQty);
				averageProd.setFresh((int)avFresh);
				averageProd.setQual((int)avQual);
			}

		}
		averageData.values().removeIf(v->v.getQty()==0);
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
			if(product.getFresh()==elem.getFresh()){//TODO need to check for Qual also for adding products
				elem.setQty(product.getQty()+elem.getQty());
				return;
			} 
		}
		currList.add(product);
	}

	/**
	 *  updates the data of this inventory by applying the spoil, increasing freshness
	 *  NOTE: meant to be called once at the start of every day.
	 */
	public void update() {

		spoilAndAge();
		calculateSpoil(0);
		clean();

	}

	/**
	 * calculate and updates the spoilQty property of every product in the list
	 * @param day, 0 if we want to calculate today spoil, 1 to calculate tomorrow spoil
	 */
	private void calculateSpoil(int day){
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			for(Product prod : entry.getValue()){
				prod.updateSpoil(prodData, day);
			}
		}
	}
	
	/**
	 * apply the spoil for each item using the previously calculated spoil quantities,
	 * and adds one day to the freshness
	 */
	private void spoilAndAge(){
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			for(Product prod : entry.getValue()){
				prod.setQty(prod.getQty()-prod.getSpoilQty());
				prod.setFresh(prod.getFresh()+1);
			}
		}
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
	
	/**
	 * 
	 * @param prod : the product for which we want to know the maximum quantity available in the inventory
	 * @return the maximum quantity available in the inventory (matching fresh and qual)
	 */
	public double getMaxQty(Product prod){
		ArrayList<Product> list = data.get(prod.getName());
		if(list==null){
			return 0;
		}
		for(Product invProd : list ){
			if(prod.getFresh()==invProd.getFresh()&&prod.getQual()==invProd.getQual()){
				return invProd.getQty();
			}
		}
		return 0;
	}
	
/**
 * removes products when quantity < 0.1 as well as empty categories
 */
	public void clean(){
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			entry.getValue().removeIf(v->v.getQty()<0.1);
		}
		data.values().removeIf(v->v.size()==0);
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
	
	public ProductData getProdData() {
		return prodData;
	}
	
}

