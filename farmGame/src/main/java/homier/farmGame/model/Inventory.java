package homier.farmGame.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import homier.farmGame.utils.ReadFile;
import javafx.scene.control.TreeItem;

public class Inventory {
	private double money;
	private HashMap<String, ArrayList<Product>> data;
	private HashMap<String, Product> averageData;
	private double siloSize;//the silo size of all the buildings combined
	private double storageSize;//the storage size of all the buildings combined
	private HashMap<String, Boolean> expandedMap; // stores the treeItem expanded state of each averageData
	
	public Inventory(){
		money = 0;
		data=new HashMap<>();
		averageData=new HashMap<>();
		siloSize=0;
		storageSize=0;
		expandedMap = new HashMap<>();
	}
	/**
	 * Creates a Inventory using a path to a file containing Products
	 * @param path
	 */
	public Inventory(String path){
		this();
		load(path);
	}
	
	/**
	 * Reload an inventory data by using a file specified by the path
	 * @param path
	 */
	public void load(String path) {
		data.clear();
		String fileString = ReadFile.getStringOfResFile(path);
		String[] lines = fileString.split("\r\n");
		for(String line:lines){
			
			if(line.contains("COMMENTLINE")) continue; //skips a commentline in the file
			
			String[] strList = line.split(",");
			String name = strList[0];
			System.out.println(name);
			Product prod = new Product(MyData.categoriesOf(name), name, Double.parseDouble(strList[1]),Double.parseDouble(strList[2]),
										Integer.parseInt(strList[3]), Integer.parseInt(strList[4]));
			addProd(prod);
		}
	}
	
	/**
	 * Check if enough storage space for more products
	 * 
	 * @param qty of items we want to add to the storage
	 * @param forSilo true if items to be put in silo, false for other storage
	 * @return whether or not we have enough free storage space for more products
	 */
	public boolean enoughStorageFor(double qty, boolean forSilo){
		boolean enoughStorage;
		if(forSilo){
			enoughStorage = qty+getTotalSiloQty()<getSiloSize();
			if(!enoughStorage){
				System.out.println("Not enough silo capacity");
			}
		}else{
			enoughStorage = qty+getTotalOtherQty()<getStorageSize();
			if(!enoughStorage){
				System.out.println("Not enough wharehouse storage capacity");
			}
		}
		
		return enoughStorage;
	}
	

	
	/**
	 * 
	 * @return the total quantity of products that goes in the silo
	 */
	public double getTotalSiloQty(){
		double totQty=0;
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			for(Product prod : entry.getValue()){
				if(prod.getCategories().contains("Silo")){
					totQty+=prod.getQty();
				}
			}
		}
		return totQty;
	}
	
	/**
	 * 
	 * @return the total quantity of all product that goes in wharehouse
	 */
	public double getTotalOtherQty(){
		double totQty=0;
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			for(Product prod : entry.getValue()){
				if(!prod.getCategories().contains("Silo")){
					totQty+=prod.getQty();
				}
			}
		}
		return totQty;
	}

	/**
	 * calculates the total quantity and average fresh and quality of each product in the inventory 
	 * and stores it in HashMap<String, Product> averageData
	 */
	public void calculateAverageData(){

		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			
			String prodName = entry.getKey();
			ArrayList<String> categories = entry.getValue().get(0).getCategories();
			Product averageProd = averageData.get(prodName);
			double totQty=0;
			double totSpoilQty=0;
			double avMaturity=0;
			double avFresh=0;
			double avQual=0;

			//System.out.println(getClass() + " product name " +prodName);
			
			for(Product prod : entry.getValue()){
				double qty = prod.getQty();
				totQty += qty;
				totSpoilQty += prod.getSpoilQty();
				avMaturity += qty*prod.getMaturity();
				avFresh += qty*prod.getFresh();
				avQual += qty*prod.getQual();
			}
			if(totQty!=0){
				avMaturity /= totQty;
				avFresh /= totQty;
				avQual /= totQty;
			}
			if(averageProd==null){
				averageProd = new Product(categories, prodName, totQty, avMaturity, avFresh, (int)Math.round(avQual));
				averageProd.setSpoilQty(totSpoilQty);
				averageData.put(prodName, averageProd);
			}else{
				averageProd.setQty(totQty);
				averageProd.setSpoilQty(totSpoilQty);
				averageProd.setMaturity(avMaturity);
				averageProd.setFresh(avFresh);
				averageProd.setQual((int)Math.round(avQual));
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
			//if an exact product exist in the inventory add(or substract) to it
			if(product.getFresh()==elem.getFresh()&&product.getQual()==elem.getQual()
					&&product.getMaturity()==elem.getMaturity()){
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
		calculateSpoil();
		clean();

	}

	/**
	 * calculate and updates the spoilQty property of every product in the list
	 * 
	 */
	private void calculateSpoil(){
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			for(Product prod : entry.getValue()){
				prod.updateSpoil();
			}
		}
	}
	
	/**
	 * apply the spoil for each item using the previously calculated spoil quantities,
	 * and decays the freshness for one day
	 */
	private void spoilAndAge(){
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			for(Product prod : entry.getValue()){
				if(prod.getMaturity()==100) {
					prod.setQty(prod.getQty()-prod.getSpoilQty());
					prod.setFresh(Math.max(0, prod.getFresh()-MyData.freshDecayOf(prod.getName())));
				}
				prod.mature();
			}
		}
	}
	

	public String toString(){
		String str= String.format("Fonds : %.2f$\n", money);
		str+= String.format("Silos: %.1f/", getTotalSiloQty()) + siloSize + "\n";
		str+= String.format("Entrepôts: %.1f/", getTotalOtherQty()) + storageSize + "\n\n";
		for(Entry<String, ArrayList<Product>> entry : data.entrySet()){
			str += entry.getKey();
			for(Product prod : entry.getValue()){
				str += String.format("| %.1f f%.1f |", prod.getQty(), prod.getFresh());
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
	
	public double getSiloSize() {
		return siloSize;
	}
	public void setSiloSize(double siloSize) {
		this.siloSize = siloSize;
	}
	public double getStorageSize() {
		return storageSize;
	}
	public void setStorageSize(double storageSize) {
		this.storageSize = storageSize;
	}
	
	public HashMap<String,Boolean> getExpandedMap(){
		return expandedMap;
	}
	public void setExpandedMap(TreeItem<Product> root) {
		for(TreeItem<Product> prodTreeItem:root.getChildren()) {
			expandedMap.put(prodTreeItem.getValue().getName(),prodTreeItem.isExpanded());
		}
	}
	
	
}

