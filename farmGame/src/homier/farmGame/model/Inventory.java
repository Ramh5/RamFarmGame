package homier.farmGame.model;


import java.util.Map.Entry;
import java.util.TreeMap;

public class Inventory {

	private TreeMap<String, Double> treeMap;  
	
	public Inventory(){
		treeMap = new TreeMap<>();
		
	}
	
	
	public void add(String product, double quantity){
		Double oldValue=treeMap.get(product);
		treeMap.put(product, oldValue==null ?quantity:oldValue+quantity);
	}
	
	
	public TreeMap<String, Double> getTreeMap(){
		return treeMap;
	}
	
	public String toString(){
		String str="";
		for(Entry<String,Double> entry : treeMap.entrySet()){
			str += entry.getKey() +": "+ entry.getValue()+ " kg\n";
		}
		return str;
	}
}
