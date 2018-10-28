package homier.farmGame.model.tile;




import java.util.TreeMap;



import homier.farmGame.model.Weather;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;



public class FarmPlot extends Tile {

	private double growthRate;
	private DoubleProperty growth;
	private DoubleProperty yield;
	private int maxYield;
	private TreeMap<Double, Double> yieldMap;
	private String product;
	private int quality;
	private int[] tempRange;

	
	
	public FarmPlot(String prodName, float growthRate, int maxYield) {
		super(prodName.toUpperCase()+"_PLOT", "FarmPlot");
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(0);
		this.yield= new SimpleDoubleProperty(0);
		this.maxYield = maxYield;
		yieldMap = buildYieldMap();
		this.product = prodName;
		this.quality = 0;
		this.tempRange = new int[]{0,0};
		
	}
	
	public FarmPlot(String prodName, float growthRate, int maxYield, int[] tempRange) {
		this(prodName,growthRate,maxYield);
		this.tempRange = tempRange;
	}
	

	@Override
	public void update(double dTime,Weather wx){
		double wxFactor = wx.getFactor(tempRange);
		if(growth.get()<150)
			growth.set(growth.get()+growthRate*(float)dTime*wxFactor);
			
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

	public DoubleProperty growthProperty(){return growth;}
	
	public final double getYield() {return yield.get();}

	public final void setYield(int yield) {this.yield.set(yield);}
	
	public DoubleProperty yieldProperty(){ return yield;}

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
		return (super.toString() + String.format("\tGrowth Rate: %.0f", growthRate) + String.format("\t  Growth: %.0f", growth.get()) + "\tyield: " + yield.get() + "\tproduct: " + "\tquality: " + quality);
	}
	
	//private helper methode to create the YieldMap
	private TreeMap<Double, Double> buildYieldMap() {
		TreeMap<Double, Double> treeMap = new TreeMap<Double,Double>();
		treeMap.put(-10.0,0.0);
		treeMap.put(0.0,0.0);
		treeMap.put(50.0,0.0);
		treeMap.put(80.0,30.0);
		treeMap.put(95.0,98.0);
		treeMap.put(100.0,100.0);
		treeMap.put(110.0,98.0);
		treeMap.put(115.0,95.0);
		treeMap.put(130.0,70.0);
		treeMap.put(150.0,0.0);
		treeMap.put(160.0,0.0);
		return treeMap;
	}

	//private helper methode to calculate the yield
	private double calculateYield(){
		double yield=0;
		double yieldFactor;
		double x1 =  yieldMap.floorKey(growth.get());
		double x2 =  yieldMap.ceilingKey(growth.get());
		double y1 =  yieldMap.get(x1).doubleValue();
		double y2 =  yieldMap.get(x2).doubleValue();
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
