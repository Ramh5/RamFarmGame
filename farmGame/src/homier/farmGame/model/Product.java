package homier.farmGame.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
	private StringProperty name;
	private DoubleProperty qty;
	private IntegerProperty fresh;
	private IntegerProperty qual;
	private BooleanProperty selected;
	
	public Product(String name, double qty, int fresh, int qual) {

		this.name = new SimpleStringProperty(name);
		this.qty = new SimpleDoubleProperty(qty);
		this.fresh = new SimpleIntegerProperty(fresh);
		this.qual = new SimpleIntegerProperty(qual);
		selected = new SimpleBooleanProperty(false);
	}

	
	public void setName(String name) {this.name.set(name);}
	public void setQty(double qty) {this.qty.set(qty);}
	public void setFresh(int fresh) {this.fresh.set(fresh);}
	public void setQual(int qual) {this.qual.set(qual);}
	public void setSelected(boolean sel){selected.set(sel);}	
	public String getName() {return name.get();}
	public double getQty(){return qty.get();}	
	public int getFresh() {return fresh.get();}
	public int getQual() {return qual.get();}	
	public StringProperty nameProperty(){return name;}	
	public DoubleProperty qtyProperty(){return qty;}	
	public IntegerProperty freshProperty(){return fresh;}
	public IntegerProperty qualProperty(){return qual;}	
	public BooleanProperty selectedProperty(){return selected;}
}
