package homier.farmGame.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Inventory {
 
	private TreeMap<String, ArrayList<double[]>> inventory;  
	private HashMap<String, ProductData> prodDataMap;
	
	
	public Inventory(){
		inventory = new TreeMap<>();
		prodDataMap = new HashMap<String,ProductData>();
		prodDataMap.put("Wheat", new ProductData("Wheat"));
	}
	
	public TreeMap<String, Double> update(){
		TreeMap<String, Double> lastDaySpoil = new TreeMap<String, Double>();
		
		//TODO implement spoiling every day
		
		return lastDaySpoil;
	}
	
	/** Add a product to the inventory.
	 * 
	 * @param product : String for the product Name
	 * @param quantity : in kg
	 * @param freshness : 1 being freshest
	 */
	public void addProd(String product, double quantity, double freshness ){
		ArrayList<double[]> currList = inventory.get(product);
		double[] addList = new double[]{quantity,freshness};
		
		if(currList==null){
			inventory.put(product, new ArrayList<double[]>(Arrays.asList(addList)));
			return;
		}
		
		for(int i=0; i<currList.size();i++){
			double[] elem = currList.get(i);
			if(freshness==elem[1]){
				//System.out.println( product +" i ="+i+ " freshness =" + freshness +"elem[1]= " + elem[1] );
				addList[0] += elem[0];
				currList.set(i, addList);
				inventory.put(product, currList);
				return;
			}
			
			if(freshness<elem[1]){
				currList.add(i, addList);
				inventory.put(product, currList);
				return;
			} 
		}
		
		currList.add(addList);
		inventory.put(product, currList);
	}
	
	
	/**
	 * @param ingredients: the ingredient list of product to be removed from the inventory
	 * @param freshReqList : the requested freshness for each ingredient, 1 being fresh, 
	 * request the desired freshness floor and the function will give the worst items above the requested freshness
	 * @return int[], the average freshness of each item or {0} if not enough items in the inventory
	 */
	public int[] removeList(TreeMap<String, Double> ingredientList, int[] freshReqList ){
		int[] freshResult = new int[ingredientList.size()];
		int i = 0;
		//check if enough of every product in the inventory
		for(String prodName:ingredientList.keySet()){
			ArrayList<double[]> prodFreshList = getProductWithFresh(prodName, ingredientList.get(prodName), freshReqList[i] );
			//System.out.println(freshReqList[i] + " "+prodName);
			//if not enough item of the required freshness in the inventory
			if(prodFreshList.get(0)[0]==0){
				//System.out.println("not enough item: missing"+ prodName+ listToString(inventory.get(prodName)));
				return new int[]{0};
				
			}
			i++;
		}
		//since there is enough product in the inventory we can now remove the items
		i=0;
		for(String prodName:ingredientList.keySet()){
			ArrayList<double[]> prodFreshList = getProductWithFresh(prodName, ingredientList.get(prodName), freshReqList[i] );
			freshResult[i]= (int)totalProductAverageFresh(prodFreshList)[1];
			//remove from inventory
			for(double[] elem:prodFreshList){
				this.addProd(prodName, -elem[0], elem[1]);
			}
			
			i++;
		}
		return freshResult;
	}
	
	
	/**
	 * @param name of the requested product
	 * @param freshReq : floor freshness requested
	 * @param quantity required of the product
	 * @return the ArrayList<double[]> of product at least as fresh ad freshReq. if their is not enough 
	 * product in the inventory, returns ArrayList<{0,0}>
	 */
	private ArrayList<double[]> getProductWithFresh(String name, double quantity, int freshReq){
		
		ArrayList<double[]> result = new ArrayList<double[]>();
		ArrayList<double[]> product = inventory.get(name);
		double rest = quantity;
		//System.out.println("Inventory getProductWithFresh " + name + " quantity: "+ quantity + " freshReq: " + freshReq );
		if(product == null){
			//System.out.println("Inventory getProductWithFresh null");
			result.add(new double[]{0,0});
			return result;
		}
		
		for(int i=product.size()-1;i>=0;i--){
			//System.out.println("Inventory getProductWithFresh in the for loop  i: " + i);
			double[] elem = product.get(i);
			if(elem[1]<=freshReq){
				//System.out.println("Inventory getProductWithFresh fresh enough");
				if(elem[0]>=rest){
					result.add(0,new double[]{rest,elem[1]});
					return result;
				}
				result.add(0,elem);
				rest-=elem[0];
			}
		}
		if(rest>0){
			
			//System.out.println("Inventory getProductWithFresh : not enough " + name +" rest "+ rest);
			result.clear();
			result.add(new double[]{0,0});
			return result;
		}
		return result;
	}
	
	
	/**
	 * @param prodList the list of product with freshness to be totaled with freshness averaged
	 * @return double[] total product with average freshness
	 */
	private double[] totalProductAverageFresh(ArrayList<double[]> prodList){
		double[] result = new double[2];
	
		for(double[] elem:prodList){
				result[0]+=elem[0];
				result[1]+=elem[1]*elem[0]; //average the freshness
		}
		result[1]= result[1]/result[0]; //average the freshness
		return result;
	}
	
	
	public TreeMap<String, ArrayList<double[]>> getInventory(){
		return inventory;
	}
	
	
	
	public String toString(){
		String str="";
		for(Entry<String, ArrayList<double[]>> entry : inventory.entrySet()){
			str += String.format("%s: %s kg\n", entry.getKey(), listToString(entry.getValue()));
		}
		return str;
	}
	
	private String listToString(ArrayList<double[]> list){
		String str = "";
		for(double[] elem:list){
			str += String.format("%.1f f%.0f  ", elem[0], elem[1]);
		}
		return str;
	}

}
