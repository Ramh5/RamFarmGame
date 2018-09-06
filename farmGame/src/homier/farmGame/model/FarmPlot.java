package homier.farmGame.model;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FarmPlot extends Tile {
	private double growthRate;
	private DoubleProperty growth;
	private IntegerProperty yield ;
	private String product;
	private int quality;
	private ContextMenu popup;
	
	public FarmPlot(String ID, Image image, double growthRate, int yield) {
		super(ID, image);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(0);
		this.yield = new SimpleIntegerProperty(yield);
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = 0;
	
		
	}
	
	public FarmPlot(String ID, Image image, double growthRate, double growth, int yield, int quality) {
		super(ID, image);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(growth);
		this.yield = new SimpleIntegerProperty(yield);
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = quality;
	
		
	}
	public FarmPlot(String ID, Image[] images, int[] map, double growthRate, double growth, int yield, int quality) {
		super(ID, images, map);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(growth);
		this.yield = new SimpleIntegerProperty(yield);
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = quality;
	
		
	}
	public FarmPlot(String ID, Image[] images, int[] map, double growthRate, int yield) {
		super(ID, images, map);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(0);
		this.yield = new SimpleIntegerProperty(yield);
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = 0;
	
		
	}
	
	public double getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(double growthRate) {
		this.growthRate = growthRate;
	}

	public final double getGrowth() {return growth.get();}

	public final void setGrowth(double growth) {this.growth.set(growth);}

	public DoubleProperty growthProperty(){return growth;}
	
	public final int getYield() {return yield.get();}

	public final void setYield(int yield) {this.yield.set(yield);}
	
	public IntegerProperty yieldProperty(){ return yield;}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public ContextMenu getPopup() {
		return popup;
	}

	public void setPopup(ContextMenu popup) {
		this.popup = popup;
	}
	
	public ImageView[] getImageViews(){
		return super.getImageViews();
	}
	
	public int[] getMap(){
		return super.getMap();
	}
	
	public String toString(){
		return (super.toString() + String.format("\tGrowth Rate: %.0f", growthRate) + String.format("\t  Growth: %.0f", growth) + "\tyield: " + yield + "\tproduct: " + "\tquality: " + quality);
	}
	

}
