package homier.farmGame.utils;


import java.lang.reflect.Method;
import java.util.TreeMap;

import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;


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

	/**
	 * Workaround to get a ControlsFx clearable textfield since clearabletextfields can't be put directly in FXML
	 * @param customTextField
	 * @throws Exception
	 */
	public static void setupClearButtonField(CustomTextField customTextField) {
		try {
		Method m = TextFields.class.getDeclaredMethod("setupClearButtonField", TextField.class, ObjectProperty.class);
		m.setAccessible(true);
		m.invoke(null, customTextField, customTextField.rightProperty());
		}catch(Exception e){
			System.out.println("Error setting up a clear button field in a textField in " + Tools.class);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Copy a treeitem
	 * @param item
	 * @return
	 */
	public static <Item> TreeItem<Item> deepcopy(TreeItem<Item> item) {
	    TreeItem<Item> copy = new TreeItem<Item>(item.getValue());
	    for (TreeItem<Item> child : item.getChildren()) {
	        copy.getChildren().add(deepcopy(child));
	    }
	    return copy;
	}
	
}


