package homier.farmGame.model;

import java.util.ArrayList;
import java.util.TreeMap;

public class ProductData {
	private String name;
	private ArrayList<String> categories;
	private double basePrice;
	private TreeMap<Double,Double> spoilMap;
	
	
	
	public ProductData(String name, ArrayList<String> categories, double basePrice, TreeMap<Double, Double> spoilMap) {
		this.name = name;
		this.categories = categories;
		this.basePrice = basePrice;
		this.spoilMap = spoilMap;
	}
	
	public String getName() {
		return name;
	}
	public ArrayList<String> getCategories() {
		return categories;
	}
	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}
	public double getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}
	public TreeMap<Double, Double> getSpoilMap() {
		return spoilMap;
	}
	public void setSpoilMap(TreeMap<Double, Double> spoilMap) {
		this.spoilMap = spoilMap;
	}
	
	
	
}
