package homier.farmGame.model;

import java.util.List;
import java.util.TreeMap;

import homier.farmGame.controller.App;
import homier.farmGame.utils.Tools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Shop extends Inventory {
	
	
	private TreeMap<Double, Double> freshMod;//freshness price modifier
	private TreeMap<Double, Double> qualMod;//quality price modifier
	
	private final ObservableList<Product> dataSelling = FXCollections.observableArrayList();
	private final ObservableList<Product> dataBuying = FXCollections.observableArrayList();

	
	public Shop(){
		
		super(App.SHOP_LIST_PATH);
		freshMod = Tools.buildTreeMap(new double[][]{{100,1},{50,.75},{0,.2}});
		qualMod = Tools.buildTreeMap(new double[][]{{0,0},{1,.5},{50,.75},{100,1}});
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
	 * @param forSilo true if we want the silo storage required or false if other storage
	 * @return the quantity of storage required by products in the dataBuying list
	 */
	public double getBuyingStorageRequired(boolean forSilo){
		double qty=0;
		for(Product prod:dataBuying){
			if(forSilo){
				if(prod.getCategories().contains("Céréale")){
					qty+=prod.getQty();
				}
			}else{
				if(!prod.getCategories().contains("Céréale")){
					qty+=prod.getQty();
				}
			}
		}
		return qty;
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
