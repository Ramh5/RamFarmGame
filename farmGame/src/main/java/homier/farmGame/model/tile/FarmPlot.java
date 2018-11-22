package homier.farmGame.model.tile;




import java.util.ArrayList;
import java.util.HashMap;

import homier.farmGame.model.Inventory;
import homier.farmGame.model.MyData;
import homier.farmGame.model.SeedByProd;
import homier.farmGame.model.SeedData;
import homier.farmGame.model.WaterData;
import homier.farmGame.model.Weather;
import homier.farmGame.utils.Tools;




public class FarmPlot extends Tile {

	private SeedData seed;
	private boolean plowed;
	private boolean sown;
	private double waterLevel;
	private double yieldPenalty;//cumulative yieldPenalty factor
	private double growth;
	private HashMap<String,Double> yield;
	private double quality;
	private double growthFactor;//one day of growth

	public FarmPlot(){
		super("FarmPlot");
		this.seed=new SeedData("empty");
		this.waterLevel=0;
		this.yieldPenalty=0;
		this.growth = 0;
		this.yield = new HashMap<>();
		this.quality = 0;
		this.growthFactor = 0;
	}
	
	@Override
	public void update(double dTime,Weather wx){
		if(plowed&&sown){
			double wxFactor = wx.getGrowthFactor(seed.getTempRange());
			growthFactor=seed.getGrowthRate()*wxFactor*WaterData.growthFactor(waterLevel);
			
			if(growth<150){
				growth=growth+growthFactor*dTime;
			}
			yieldPenalty+=WaterData.yieldPenaltyFactor(waterLevel)*dTime;
			
			yield.clear();
			for(SeedByProd byProd:seed.getByProds().values()) {
				yield.put(byProd.getProdName(), calculateYield(byProd));
			}
			
			quality=Math.min(100, quality+wxFactor*WaterData.growthFactor(waterLevel)*dTime);

			//update the waterlevel
			waterLevel = Math.min(120, Math.max(0, waterLevel-WaterData.dryingFactor(wx)*dTime*25));//lose 25 waterLevel per day if dryingFactor of 1
			//System.out.println(quality + " " + wxFactor*dTime );
			//System.out.println(String.format("WaterLevel: %.2f yieldPenalty: %.2f ",waterLevel,yieldPenalty )  );
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
	

	public final double getGrowth() {return growth;}

	public final void setGrowth(float growth) {this.growth=growth;}

	
	public final double yieldOf(String prodName) {return yield.get(prodName);}
	
	public final HashMap<String, Double> getYield() {return yield;}

	public final void setQuality(double quality){this.quality=quality;}

	
	public double getGrowthFactor(){return growthFactor;}
	
	public int getQual() {
		return (int)quality;
	}

	public double getWaterLevel(){
		return waterLevel;
	}
	public double getYieldPenalty() {
		return yieldPenalty;
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
				String.format("\t  Growth: %.0f", growth)  + "\tquality: " + quality);
	}
	
	/**
	 * calculate the yield using the seedData yield vs growth map and the current yieldPenalty
	 * @return the current yield of the plot
	 */
	private double calculateYield(SeedByProd byProd){
		return Math.max(0,(Tools.interpolateMap(byProd.getYieldMap(), growth)-yieldPenalty)/100*byProd.getMaxYield());
	}

	/**
	 * Calculates if the inventory has enough storage to harvest this farmTile
	 * @param inventory
	 * @return
	 */
	public boolean enoughStorageToHarvest(Inventory inventory) {
		double qtyForSilo=0;
		double qtyForOther=0;
		for(SeedByProd byProd:seed.getByProds().values()) {
			String prodName = byProd.getProdName();
			if(MyData.categoriesOf(prodName).contains("Silo")) {
				qtyForSilo+=yield.get(prodName);
			}else {
				qtyForOther+=yield.get(prodName);
			}
		}
		return inventory.enoughStorageFor(qtyForSilo, true)&&inventory.enoughStorageFor(qtyForOther, false);
	}
	
	
}
