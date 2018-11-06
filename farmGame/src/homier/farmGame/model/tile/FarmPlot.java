package homier.farmGame.model.tile;




import homier.farmGame.model.MyData;
import homier.farmGame.model.SeedData;
import homier.farmGame.model.WaterData;
import homier.farmGame.model.Weather;
import homier.farmGame.utils.Tools;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;



public class FarmPlot extends Tile {

	private SeedData seed;
	private boolean plowed;
	private boolean sown;
	private double waterLevel;
	private double yieldPenalty;
	private DoubleProperty growth;
	private DoubleProperty yield;
	private DoubleProperty quality;
	private DoubleProperty growthFactor;//one day of growth

	public FarmPlot(){
		super("FarmPlot");
		this.waterLevel=0;
		this.yieldPenalty=0;
		this.growth = new SimpleDoubleProperty(0);
		this.yield= new SimpleDoubleProperty(0);
		this.quality = new SimpleDoubleProperty(1);
		this.growthFactor = new SimpleDoubleProperty(0);
	}
	
	@Override
	public void update(double dTime,Weather wx){
		if(plowed&&sown){
			double wxFactor = wx.getGrowthFactor(seed.getTempRange());
			growthFactor.set(seed.getGrowthRate()*wxFactor*WaterData.growthFactor(waterLevel));
			
			if(growth.get()<150){
				growth.set(growth.get()+growthFactor.get()*dTime);
			}
			yieldPenalty+=WaterData.yieldPenaltyFactor(waterLevel)*dTime*50;//50 would be the yieldPenalty Rate
			setYield(calculateYield());
			quality.set(Math.min(100, quality.get()+wxFactor*WaterData.growthFactor(waterLevel)*dTime));

			//update the waterlevel
			waterLevel = Math.min(120, Math.max(0, waterLevel-WaterData.dryingFactor(wx)*dTime*50));//lose 50 waterLevel per day if dryingFactor of 1
			//System.out.println(quality + " " + wxFactor*dTime );
			System.out.println(String.format("WaterLevel: %.2f yieldPenalty: %.2f ",waterLevel,yieldPenalty )  );
		}
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

	public final void setYield(double yield) {
		this.yield.set(Math.max(0, yield));
	}
	
	public DoubleProperty yieldProperty(){ return yield;}

	public final void setQuality(double quality){this.quality.set(quality);}

	public DoubleProperty qualityProperty(){return quality;}
	
	public DoubleProperty growthFactorProperty(){return growthFactor;}
	
	public int getQual() {
		return (int)quality.get();
	}

	public double getWaterLevel(){
		return waterLevel;
	}
	
	/**
	 * sets the water level of the tile and restricts it to min 0 max 120
	 * @param waterLevel
	 */
	public void setWaterLevel(double waterLevel){
		this.waterLevel=waterLevel = Math.min(120, Math.max(0, waterLevel));
	}
	
	public String toString(){
		return (super.toString() + String.format("\tGrowth Rate: %.0f", seed.getGrowthRate()) + 
				String.format("\t  Growth: %.0f", growth.get()) + "\tyield: " + yield.get() + "\tproduct: " + "\tquality: " + quality.get());
	}
	
	/**
	 * calculate the yield using the seedData yield vs growth map and the current yieldPenalty
	 * @return the current yield of the plot
	 */
	private double calculateYield(){
		return Tools.interpolateMap(seed.getYieldMap(), growth.get())/100*seed.getMaxYield()-yieldPenalty;
	}
	
}
