package homier.farmGame.model;



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
	private StringProperty name;
	private DoubleProperty qty;
	private DoubleProperty spoilQty;
	private IntegerProperty fresh;
	private IntegerProperty qual;
	private DoubleProperty price;
	private BooleanProperty selected;
	private ChangeListener<Boolean> selListener;
	
	public Product(String name, double qty, int fresh, int qual) {

		this.name = new SimpleStringProperty(name);
		this.qty = new SimpleDoubleProperty(qty);
		this.spoilQty = new SimpleDoubleProperty(0);
		this.fresh = new SimpleIntegerProperty(fresh);
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
		this.name = new SimpleStringProperty(prod.getName());
		this.qty = new SimpleDoubleProperty(prod.getQty());
		this.spoilQty = new SimpleDoubleProperty(prod.getSpoilQty());
		this.fresh = new SimpleIntegerProperty(prod.getFresh());
		this.qual = new SimpleIntegerProperty(prod.getQual());
		this.price = new SimpleDoubleProperty(prod.getPrice());
		selected = new SimpleBooleanProperty(false);
		selListener = (a,b,c)->{};
	}

	
	
	public void setName(String name) {this.name.set(name);}
	public void setQty(double qty) {this.qty.set(qty);}
	public void setSpoilQty(double spoilQty) {this.spoilQty.set(spoilQty);}
	public void setFresh(int fresh) {this.fresh.set(fresh);}
	public void setQual(int qual) {this.qual.set(qual);}
	public void setPrice(double price) {this.price.set(price);}
	public void setSelected(boolean sel){selected.set(sel);}
	public void setSelListener(ChangeListener<Boolean> listener){
		selected.removeListener(selListener);
		selected.addListener(listener);
		selListener = listener;
		}
	public String getName() {return name.get();}
	public double getQty(){return qty.get();}
	public double getSpoilQty(){return spoilQty.get();}
	public int getFresh() {return fresh.get();}
	public int getQual() {return qual.get();}
	public double getPrice() {return price.get();}
	public StringProperty nameProperty(){return name;}	
	public DoubleProperty qtyProperty(){return qty;}
	public DoubleProperty spoilQtyProperty(){return spoilQty;}
	public IntegerProperty freshProperty(){return fresh;}
	public IntegerProperty qualProperty(){return qual;}	
	public DoubleProperty priceProperty(){return price;}
	public BooleanProperty selectedProperty(){return selected;}
	
	/**
	 * Updates the Price per kg of the product
	 * @param prod : the product for which we want to calculate the price
	 */
	public void updatePrice(Shop shop){
		
		double newPrice = shop.getBasePrice(name.get());
		newPrice *= Tools.interpolateMap(shop.getFreshMod(), fresh.get());
		newPrice *= Tools.interpolateMap(shop.getQualMod(), qual.get());
		price.set(newPrice);
	}
	
	public void updateSpoil(ProductData prodData, int day){
		setSpoilQty((getQty()*Tools.interpolateMap(prodData.getSpoilMap(getName()),getFresh()+day)));
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
		return getName() +" | "+ getQty() + " kg  F: " + getFresh() + "  Q: "+ getQual() 
			+ "  Spoil: " +  String.format("%.2f kg  Price: %.2f", getSpoilQty(), getPrice())+"$ |" ;
		
	}
}
