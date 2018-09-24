package homier.farmGame.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import homier.farmGame.utils.Tools;

public class Shop extends Inventory {
	
	private HashMap<String, Double> basePrice;
	private TreeMap<Double, Double> freshMod;
	private TreeMap<Double, Double> qualMod;
	
	
	public Shop(){
		super();
		addProd(new Product("Wheat", 5, 1, 32));
		basePrice = new HashMap();
		basePrice.put("Wheat", 1.99);
		freshMod = Tools.buildTreeMap(new double[][]{{1,1},{10,.75},{20,.5}});
		qualMod = Tools.buildTreeMap(new double[][]{{1,.1},{10,.5},{20,.8}});
	}
	
	public double price(Product prod){
		double price = basePrice.get(prod.getName());
		price *= Tools.interpolateMap(freshMod, prod.getFresh());
		price *= Tools.interpolateMap(qualMod, prod.getQual());
		return price;
	}
	
}
