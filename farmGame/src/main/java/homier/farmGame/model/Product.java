package homier.farmGame.model;



import java.util.ArrayList;
import java.util.Arrays;

import homier.farmGame.utils.Tools;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class Product{
	private ArrayList<String> categories;
	private StringProperty name;
	private DoubleProperty qty;
	private DoubleProperty spoilQty;
	private DoubleProperty maturity; //0 to 100 %
	private int maturation;//maturation time in days
	private DoubleProperty fresh;
	private IntegerProperty qual;
	private DoubleProperty price;
	private BooleanProperty selected;
	transient private ChangeListener<Boolean> selListener;
	
	/**
	 * 
	 * @param categories
	 * @param name
	 * @param qty
	 * @param maturity
	 * @param fresh
	 * @param qual
	 */
	public Product(ArrayList<String> categories, String name, double qty, double maturity, double fresh, int qual) {
		this.categories = categories!=null?categories:new ArrayList<>(Arrays.asList("default"));
		this.name = new SimpleStringProperty(name);
		this.qty = new SimpleDoubleProperty(qty);
		this.spoilQty = new SimpleDoubleProperty(0);
		this.maturity = new SimpleDoubleProperty(maturity);
		this.maturation = 0;
		this.fresh = new SimpleDoubleProperty(fresh);
		this.qual = new SimpleIntegerProperty(qual);
		this.price = new SimpleDoubleProperty(0);
		selected = new SimpleBooleanProperty(false);
		selListener = (a,b,c)->{};
		
	}
	/**
	 * Copy constructor
	 * @param prod : the product to be copied
	 */
	
	public Product(Product prod){
		this.categories = new ArrayList<String>(prod.getCategories());
		this.name = new SimpleStringProperty(prod.getName());
		this.qty = new SimpleDoubleProperty(prod.getQty());
		this.spoilQty = new SimpleDoubleProperty(prod.getSpoilQty());
		this.maturity = new SimpleDoubleProperty(prod.getMaturity());
		this.maturation = prod.getMaturation();
		this.fresh = new SimpleDoubleProperty(prod.getFresh());
		this.qual = new SimpleIntegerProperty(prod.getQual());
		this.price = new SimpleDoubleProperty(prod.getPrice());
		selected = new SimpleBooleanProperty(false);
		selListener = (a,b,c)->{};
	}

	
	
	
	public void setName(String name) {this.name.set(name);}
	public void setQty(double qty) {this.qty.set(qty);}
	public void setSpoilQty(double spoilQty) {this.spoilQty.set(spoilQty);}
	public void setMaturity(double maturity) {this.maturity.set(maturity);}
	public void setMaturation(int maturation) {this.maturation=maturation;}
	public void setFresh(double fresh) {this.fresh.set(fresh);}
	public void setQual(int qual) {this.qual.set(qual);}
	public void setPrice(double price) {this.price.set(price);}
	public void setSelected(boolean sel){selected.set(sel);}
	public void setSelListener(ChangeListener<Boolean> listener){
		selected.removeListener(selListener!=null?selListener:(a,b,c)->{});
		selected.addListener(listener);
		selListener = listener;
		}
	public String getName() {return name.get();}
	public double getQty(){return qty.get();}
	public double getSpoilQty(){return spoilQty.get();}
	public double getMaturity() {return maturity.get();}
	public int getMaturation() {return maturation;}
	public double getFresh() {return fresh.get();}
	public int getQual() {return qual.get();}
	public double getPrice() {return price.get();}
	public boolean isSelected() {return selected.get();}
	public StringProperty nameProperty(){return name;}	
	public DoubleProperty qtyProperty(){return qty;}
	public DoubleProperty spoilQtyProperty(){return spoilQty;}
	public DoubleProperty maturityProperty(){return maturity;}
	public DoubleProperty freshProperty(){return fresh;}
	public IntegerProperty qualProperty(){return qual;}	
	public DoubleProperty priceProperty(){return price;}
	public BooleanProperty selectedProperty(){return selected;}
	
	/**
	 *update the maturity every day
	 */
	public void mature() {
		if(maturation>0&&maturity.get()<100) {
			maturity.set(Math.min(100, maturity.get()+100/maturation));
		}
	}
	
	/**
	 * Updates the Price per kg of the product
	 * @param prod : the product for which we want to calculate the price
	 */
	public void updatePrice(Shop shop){
		
		//double newPrice = shop.getBasePrice(name.get());
		double newPrice = MyData.basePriceOf(name.get());
		newPrice *= Tools.interpolateMap(shop.getFreshMod(), fresh.get());
		newPrice *= Tools.interpolateMap(shop.getQualMod(), qual.get());
		newPrice *= maturity.get()<100?0:1; //price zero if not mature
		price.set(newPrice);
	}
	
	public void updateSpoil(){
		setSpoilQty(MyData.freshDecayOf(getName())*(getQty()*Tools.interpolateMap(MyData.spoilMap,getFresh())));
	}
	
	/**
	 * adds a product to a product and averages fresh and qual (does not update spoil, price)
	 * @param prod
	 */
	public void add(Product prod) {
		double newQty = getQty()+prod.getQty();
		setFresh((int)((getFresh()*getQty()+prod.getFresh()*prod.getQty())/newQty));
		setQual((int)((getQual()*getQty()+prod.getQual()*prod.getQty())/newQty));
		setQty(newQty);
	}
	
	
	@Override
	public String toString() {
		return getName() + String.format(" | %.2f kg  F: %.2f  Q: ", getQty(), getFresh()) + getQual() 
		+  String.format(" Price: %.2f",getPrice())+"$ |";
		
		//+ "  Spoil: " +  String.format("%.2f kg  Price: %.2f", getSpoilQty(), getPrice())+"$ |" ;
		
	}
	public ArrayList<String> getCategories() {
		return categories;
	}
}
