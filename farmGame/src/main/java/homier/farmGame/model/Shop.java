package homier.farmGame.model;

import java.util.List;
import java.util.TreeMap;

import homier.farmGame.controller.App;
import homier.farmGame.utils.Tools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Shop extends Inventory {
	
	private double buyingPricePenalty;//price factor for buying
	private double sellingPricePenalty;//price factor for selling
	private double dailyBuyLimit;//Daily buy transaction limit
	private double dailyBuyCount;//Daily buy transaction done so far
	private double dailySellLimit;//Daily sell transaction limit
	private double dailySellCount;//Daily sell transactiom counter

	private TreeMap<Double, Double> freshMod;//freshness price modifier
	private TreeMap<Double, Double> qualMod;//quality price modifier
	
	private final ObservableList<Product> dataSelling = FXCollections.observableArrayList();
	private final ObservableList<Product> dataBuying = FXCollections.observableArrayList();

	
	public Shop(){
		super(App.SHOP_LIST_PATH);
		buyingPricePenalty = 1.2;
		sellingPricePenalty = .8;
		dailyBuyLimit=500;
		dailyBuyCount=0;
		dailySellLimit=1000;
		dailySellCount=0;
		freshMod = Tools.buildTreeMap(new double[][]{{100,1},{50,.75},{0,.2}});
		qualMod = Tools.buildTreeMap(new double[][]{{0,0},{1,.5},{50,.75},{100,1}});
	}

	
	@Override
	public void update(){
		super.update();//this update is not needed if we reload from the file every day anyway
		load(App.SHOP_LIST_PATH);//clears the shop inventory and reloads from the file
		dailyBuyCount=0;
		dailySellCount=0;
		
		//System.out.println(getClass()+" transaction counters reset");
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
	 * check if we have enough buying transactions left today
	 * @return true of false if we have enough buying transactions left today
	 */
	public boolean enoughBuyTransaction(){
		return totalQty(dataBuying)<=dailyBuyLimit-dailyBuyCount;
	}
	
	/**
	 * check if we have enough selling transactions left today
	 * @return true of false if we have enough selling transactions left today
	 */
	public boolean enoughSellTransaction(){
		return totalQty(dataSelling)<=dailySellLimit-dailySellCount;
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
	
	/**
	 * Helper method to calculate the total Quantity of a list of product
	 * @param list
	 * @return the total qty all products in a list
	 */
	private double totalQty(List<Product> list){
		double totalQty = 0;
		for(Product prod:list){
			totalQty += prod.getQty();
		}
		return totalQty;
	}
 	
	public double getBuyingPricePenalty() {
		return buyingPricePenalty;
	}

	public void setBuyingPricePenalty(double buyingPricePenalty) {
		this.buyingPricePenalty = buyingPricePenalty;
	}

	public double getSellingPricePenalty() {
		return sellingPricePenalty;
	}

	public void setSellingPricePenalty(double sellingPricePenalty) {
		this.sellingPricePenalty = sellingPricePenalty;
	}

	public double getDailyBuyLimit() {
		return dailyBuyLimit;
	}

	public void setDailyBuyLimit(double dailyBuyLimit) {
		this.dailyBuyLimit = dailyBuyLimit;
	}

	public double getDailyBuyCount() {
		return dailyBuyCount;
	}

	public void addToDailyBuyCount(double qty) {
		dailyBuyCount+=qty;
	}

	public double getDailySellLimit() {
		return dailySellLimit;
	}

	public void setDailySellLimit(double dailySellLimit) {
		this.dailySellLimit = dailySellLimit;
	}

	public double getDailySellCount() {
		return dailySellCount;
	}

	public void addToDailySellCount(double qty) {
		dailySellCount+=qty;
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
	 * @return the formatted string to display the daily transactions limit
	 */
	public String transactionsToString() {
		return String.format("-----Daily transaction limits in kg-----\n"
							+ "Selling: %.2f/%.2f\n"
							+ "Buying: %.2f/%.2f",
				dailySellCount,dailySellLimit,dailyBuyCount,dailyBuyLimit);
	}
}
