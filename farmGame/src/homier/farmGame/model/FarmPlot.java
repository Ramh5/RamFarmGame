package homier.farmGame.model;




import java.util.TreeMap;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;



public class FarmPlot extends Tile {
	private double growthRate;
	private DoubleProperty growth;
	private IntegerProperty yield;
	private int maxYield;
	private TreeMap<Integer, Integer> yieldMap;
	private String product;
	private int quality;
	
	
	public FarmPlot(String ID, double growthRate, int maxYield) {
		super(ID);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(0);
		this.yield= new SimpleIntegerProperty(0);
		this.maxYield = maxYield;
		yieldMap = buildYieldMap();
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = 0;
	
		
	}
	
	public FarmPlot(String ID, double growthRate, double growth, int maxYield, int quality) {
		super(ID);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(growth);
		this.yield= new SimpleIntegerProperty(0);
		this.maxYield = maxYield;
		
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = quality;
	
		
	}
	


	public void update(double dTime){
		if(growth.get()<150)
		growth.set(growth.get()+growthRate*dTime);
		yield.set(calculateYield());
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


	
	
	
	public String toString(){
		return (super.toString() + String.format("\tGrowth Rate: %.0f", growthRate) + String.format("\t  Growth: %.0f", growth) + "\tyield: " + yield + "\tproduct: " + "\tquality: " + quality);
	}
	
	//private helper methode to create the YieldMap
	private TreeMap<Integer, Integer> buildYieldMap() {
		TreeMap<Integer, Integer> treeMap = new TreeMap<Integer,Integer>();
		treeMap.put(0,0);
		treeMap.put(50,0);
		treeMap.put(80,30);
		treeMap.put(95,98);
		treeMap.put(100,100);
		treeMap.put(110,98);
		treeMap.put(115,95);
		treeMap.put(130,70);
		treeMap.put(150,0);
		return treeMap;
	}

	//private helper methode to calculate the yield
	private int calculateYield(){
		int growthInt =(int) growth.get();
		double yield=0;
		float yieldFactor;

		int x1 =  yieldMap.floorKey(growthInt);
		int x2 =  yieldMap.ceilingKey(growthInt);
		float y1 =  yieldMap.get(x1).floatValue();
		float y2 =  yieldMap.get(x2).floatValue();
		//to prevent devision by zero
		if(x2-x1==0){
			yieldFactor = y1;
		}else{
			yieldFactor =  ((y2-y1)/((float)(x2-x1)))*(growthInt-x1)+y1;
		}
		
			yield = (yieldFactor/100*maxYield);
			
		return (int)yield;
	}
	
}
