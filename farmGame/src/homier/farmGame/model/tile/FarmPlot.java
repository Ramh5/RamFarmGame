package homier.farmGame.model.tile;




import java.util.TreeMap;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;



public class FarmPlot extends Tile {

	private float growthRate;
	private FloatProperty growth;
	private FloatProperty yield;
	private int maxYield;
	private TreeMap<Float, Float> yieldMap;
	private String product;
	private int quality;
	
	
	public FarmPlot(String ID, float growthRate, int maxYield) {
		super(ID);
		this.growthRate = growthRate;
		this.growth = new SimpleFloatProperty(0);
		this.yield= new SimpleFloatProperty(0);
		this.maxYield = maxYield;
		yieldMap = buildYieldMap();
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = 0;
	
		
	}
	


	public void update(double dTime){
		if(growth.get()<150)
			growth.set(growth.get()+growthRate*(float)dTime);
			
		yield.set(calculateYield());
	}
	
	public double getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(float growthRate) {
		this.growthRate = growthRate;
	}

	public final double getGrowth() {return growth.get();}

	public final void setGrowth(float growth) {this.growth.set(growth);}

	public FloatProperty growthProperty(){return growth;}
	
	public final float getYield() {return yield.get();}

	public final void setYield(int yield) {this.yield.set(yield);}
	
	public FloatProperty yieldProperty(){ return yield;}

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
	private TreeMap<Float, Float> buildYieldMap() {
		TreeMap<Float, Float> treeMap = new TreeMap<Float,Float>();
		treeMap.put(-10f,0f);
		treeMap.put(0f,0f);
		treeMap.put(50f,0f);
		treeMap.put(80f,30f);
		treeMap.put(95f,98f);
		treeMap.put(100f,100f);
		treeMap.put(110f,98f);
		treeMap.put(115f,95f);
		treeMap.put(130f,70f);
		treeMap.put(150f,0f);
		treeMap.put(160f,0f);
		return treeMap;
	}

	//private helper methode to calculate the yield
	private float calculateYield(){
		float yield=0;
		float yieldFactor;
		float x1 =  yieldMap.floorKey(growth.get());
		float x2 =  yieldMap.ceilingKey(growth.get());
		float y1 =  yieldMap.get(x1).floatValue();
		float y2 =  yieldMap.get(x2).floatValue();
		//to prevent devision by zero
		if(x2-x1==0){
			yieldFactor = y1;
		}else{
			yieldFactor =  ((y2-y1)/((x2-x1)))*(growth.get()-x1)+y1;
		}
		
			yield = (yieldFactor/100*maxYield);
			
		return yield;
	}
	
}
