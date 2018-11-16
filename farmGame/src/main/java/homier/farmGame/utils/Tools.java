package homier.farmGame.utils;


import java.util.TreeMap;


public class Tools {

	
	
	public static TreeMap<Double,Double> buildTreeMap(double[][] listToMap){
		TreeMap<Double,Double> map = new TreeMap<Double,Double>();
		for(double[] elem:listToMap){
			map.put(elem[0], elem[1]);
		}
		
		return map;
	}
	/**
	 * interpolates a TreeMap of double values given a double as an input 
	 * @param map: a TreeMap of double
	 * @param x: input
	 * @return the y value that matches x
	 */
	public static double interpolateMap(TreeMap<Double,Double> map, double x){
		
		double y;
		double x1;
		double x2;
		double y1;
		double y2;
		
		if(map.containsKey(x)){//if x is an exact key in the map
			return map.get(x);	
		}
		
		if(x<map.firstKey()){// if x is lower than the map, extrapolate
			x1=map.firstKey();
			x2=map.higherKey(map.firstKey());
		}else if(x>map.lastKey()){//if x is bigger than the map, extrapolate
			x1=map.lowerKey(map.lastKey());
			x2=map.lastKey();
		}else{
			x1 =  map.floorKey(x);
			x2 =  map.ceilingKey(x);
		}
		
		y1 =  map.get(x1).doubleValue();
		y2 =  map.get(x2).doubleValue();
		y =  ((y2-y1)/((x2-x1)))*(x-x1)+y1;

		return y;
	}
	
	
}


