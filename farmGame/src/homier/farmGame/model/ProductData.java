package homier.farmGame.model;

import java.util.ArrayList;
import java.util.TreeMap;


public class ProductData {
	private String name;
	private ArrayList<String> categories;
	private double basePrice;
	private double freshDecay;
	
	
	
	public ProductData(String name, ArrayList<String> categories, double basePrice, double freshDecay) {
		this.name = name;
		this.categories = categories;
		this.basePrice = basePrice;
		this.freshDecay=freshDecay;
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
	public double getFreshDecay(){
		return freshDecay;
	}
	
	
	
}
