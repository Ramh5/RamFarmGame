package homier.farmGame.model.tile;




import homier.farmGame.model.MyData;
import homier.farmGame.model.ProductData;
import homier.farmGame.model.SeedData;
import homier.farmGame.model.Weather;
import homier.farmGame.utils.Tools;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;



public class FarmPlot extends Tile {

	private SeedData seed;
	private boolean plowed;
	private boolean sown;
	private DoubleProperty growth;
	private DoubleProperty yield;
	private DoubleProperty quality;
	

	public FarmPlot(){
		super("FarmPlot");
		this.growth = new SimpleDoubleProperty(0);
		this.yield= new SimpleDoubleProperty(0);
		this.quality = new SimpleDoubleProperty(1);
	}
	
	@Override
	public void update(double dTime,Weather wx){
		if(!plowed||!sown){
			return;
		}
		double wxFactor = wx.getFactor(seed.getTempRange());
		if(growth.get()<150)
			growth.set(growth.get()+seed.getGrowthRate()*(float)dTime*wxFactor);
			
		yield.set(calculateYield());
		quality.set(Math.min(100, quality.get()+wxFactor*dTime));
		//System.out.println(quality + " " + wxFactor*dTime );
		
	}
	
	public void plow(){
		this.plowed=true;
	}
	
	public void sow(String seedName){
		this.sown = true;
		this.seed = MyData.seedDataOf(seedName);
	}
	
	public SeedData getSeed(){
		return seed;
	}
	
	public boolean isPlowed(){
		return plowed;
	}
	
	public boolean isSown(){
		return sown;
	}
	

	public final double getGrowth() {return growth.get();}

	public final void setGrowth(float growth) {this.growth.set(growth);}

	public DoubleProperty growthProperty(){return growth;}
	
	public final double getYield() {return yield.get();}

	public final void setYield(int yield) {this.yield.set(yield);}
	
	public DoubleProperty yieldProperty(){ return yield;}

	public final void setQuality(double quality){this.quality.set(quality);}

	public int getQual() {
		return (int)quality.get();
	}



	
	public String toString(){
		return (super.toString() + String.format("\tGrowth Rate: %.0f", seed.getGrowthRate()) + 
				String.format("\t  Growth: %.0f", growth.get()) + "\tyield: " + yield.get() + "\tproduct: " + "\tquality: " + quality.get());
	}
	/*
	//private helper methode to create the YieldMap
	private TreeMap<Double, Double> buildYieldMap() {
		TreeMap<Double, Double> treeMap = new TreeMap<Double,Double>();
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
*/
	//private helper methode to calculate the yield
	private double calculateYield(){
		return Tools.interpolateMap(seed.getYieldMap(), growth.get())/100*seed.getMaxYield();
		/*
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
		*/
	}
	
}
