package homier.farmGame.model;

import java.util.TreeMap;
/**
 * Class to store the data for each product of a seed: 
 * prodName, maxYield, yieldMap
 * @author Ram
 *
 */
public class SeedByProd {
	private String prodName;
	private int maxYield;
	private TreeMap<Double, Double> yieldMap;
	
	public SeedByProd(String prodName, int maxYield, TreeMap<Double, Double> yieldMap) {
		this.prodName = prodName;
		this.maxYield = maxYield;
		this.yieldMap = yieldMap;
	}
	
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public int getMaxYield() {
		return maxYield;
	}
	public void setMaxYield(int maxYield) {
		this.maxYield = maxYield;
	}
	public TreeMap<Double, Double> getYieldMap() {
		return yieldMap;
	}
	public void setYieldMap(TreeMap<Double, Double> yieldMap) {
		this.yieldMap = yieldMap;
	}
	
	
	
}
