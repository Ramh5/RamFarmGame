package homier.farmGame.model;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import homier.farmGame.utils.Tools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Shop extends Inventory {
	
	private HashMap<String, Double> basePrice;
	private TreeMap<Double, Double> freshMod;//freshness price modifier
	private TreeMap<Double, Double> qualMod;//quality price modifier
	
	private final ObservableList<Product> dataSelling = FXCollections.observableArrayList();
	private final ObservableList<Product> dataBuying = FXCollections.observableArrayList();

	
	public Shop(){
		super();
		addProd(new Product("Wheat",20,1,80));
		addProd(new Product("Wheat",20,10,15));
		addProd(new Product("Carrots",10,1,80));
		addProd(new Product("Carrots",10,10,15));
		addProd(new Product("Eggs",5,1,80));
		addProd(new Product("Eggs",5,10,15));
		addProd(new Product("Onions",5,10,50));
		addProd(new Product("Onions",5,12,15));
		basePrice = new HashMap<String, Double>();
		basePrice.put("Wheat", 1.99);
		basePrice.put("Carrots", 2.50);
		basePrice.put("Eggs", 3.2);
		basePrice.put("Onions", 1.2);
		basePrice.put("Soupe", 4.5);
		basePrice.put("Omelette", 4.5);
		freshMod = Tools.buildTreeMap(new double[][]{{1,1},{20,.75},{50,.2}});
		qualMod = Tools.buildTreeMap(new double[][]{{0,0},{50,.5},{100,1}});
	}

	public HashMap<String, Double> getBasePrice() {
		return basePrice;
	}
	 /**
	  * gets the base price of a product returning 0 if not in the database
	  * @param name : the name of the product
	  * @return 0 if name is not in the database
	  */
	public double getBasePrice(String name) {
		return basePrice.get(name)==null?0:basePrice.get(name);
	}

	public TreeMap<Double, Double> getFreshMod() {
		return freshMod;
	}

	public TreeMap<Double, Double> getQualMod() {
		return qualMod;
	}

	public ObservableList<Product> getDataSelling(){
		return dataSelling;
	}
	
	public ObservableList<Product> getDataBuying(){
		return dataBuying;
	}
	
	/**
	 * 
	 * @param list of product
	 * @return the total price of a list of product
	 */
	public double totalPrice(List<Product> list){
		double totalPrice = 0;
		for(Product prod:list){
			totalPrice += prod.getPrice()*prod.getQty();
		}
		return totalPrice;
	}
}
