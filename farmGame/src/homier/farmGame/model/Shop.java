package homier.farmGame.model;

import java.util.HashMap;
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
		addProd(new Product("Wheat", 5, 1, 15));
		addProd(new Product("Carrots",10,1,20));
		basePrice = new HashMap<String, Double>();
		basePrice.put("Wheat", 1.99);
		basePrice.put("Carrots", 2.50);
		freshMod = Tools.buildTreeMap(new double[][]{{1,1},{10,.75},{20,.5}});
		qualMod = Tools.buildTreeMap(new double[][]{{1,.1},{10,.5},{20,.8}});
	}

	public HashMap<String, Double> getBasePrice() {
		return basePrice;
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
	
}
