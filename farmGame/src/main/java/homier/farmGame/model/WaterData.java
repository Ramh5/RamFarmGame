package homier.farmGame.model;

import java.util.TreeMap;

import homier.farmGame.utils.Tools;

public class WaterData {
	private final static TreeMap<Double,Double> growthFactorMap = Tools.buildTreeMap(new double[][]{{0,0},{50,.7},{80,.9},{100,1},{120,1}});
	private final static TreeMap<Double,Double> yieldPenaltyFactorMap = Tools.buildTreeMap(new double[][]{{0,1},{20,.5},{50,0},{120,0}});
	private final static TreeMap<Double,Double> tempDryingFactorMap = Tools.buildTreeMap(new double[][]{{-10,0},{0,0},{30,1.4}});
	
	
	/**
	 * get the drying factor given the weather wind, sky and temperature
	 * @param wx
	 * @return
	 */
	public static double dryingFactor(Weather wx){
		
		double factor;
		factor = Tools.interpolateMap(tempDryingFactorMap, wx.getTemp());
		
		factor += wx.getSky().getDryingFactor();
		
		//if raining only account for the rain
		if(wx.getSky().getDryingFactor()<0){
			factor = wx.getSky().getDryingFactor();
		}
		//only take wind into account if not raining and temp not below zero
		if(factor>0){
			factor += wx.getWind().getDryingFactor();
		}
		return factor;
	}
	
	/**
	 * get the yield penalty factor for drying crops when water level is too low
	 * @param waterLevel
	 * @return the yield penalty factor
	 */
	public static double yieldPenaltyFactor(double waterLevel){
		return Tools.interpolateMap(yieldPenaltyFactorMap, waterLevel);
	}
	/**
	 * Get the water level growth factor
	 * @param waterLevel
	 * @return the waterFactor to apply to growth 
	 */
	public static double growthFactor(double waterLevel){
		return Tools.interpolateMap(growthFactorMap, waterLevel);
	}
	
}
