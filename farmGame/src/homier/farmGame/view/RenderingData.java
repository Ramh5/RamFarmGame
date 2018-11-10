package homier.farmGame.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import homier.farmGame.controller.App;
import homier.farmGame.model.MyData;
import homier.farmGame.model.SeedData;
import homier.farmGame.utils.ReadFile;
import homier.farmGame.utils.Tools;
import javafx.scene.image.Image;

public class RenderingData {
	public static int tileSize = 100;
	private static HashMap<String,TileViewData> tvdList; // map String tileName with their tvd (example dirt, plowed, seedName, forest, house, silo)
	private static HashMap<String,Image> images; // map of images and their name as defined in the image folder
	private static HashMap<String,Image> icons;// map of icons for each products


	public RenderingData(){

		images = new HashMap<>();
		tvdList = new HashMap<>();
		icons = new HashMap<>();
		//load up all the images in the image folder
		File folder = new File(App.TILE_IMAGES_PATH);
		String[] fileNames = folder.list();
		for(String name:fileNames){
			images.put(name.replace(".png", ""), new Image("/tiles/"+name, tileSize, tileSize, true, true));
			//System.out.println(name.replace(".png", ""));
		}
		
		File iconsfolder = new File(App.PROD_ICON_PATH);
		String[] iconsfileNames = iconsfolder.list();
		
		for(String name:iconsfileNames){
			icons.put(name.replace(".png", ""), new Image("/products_icons/"+name, 40, 40, true, true));
			
		}
		
		
		tvdList.put("dirt", new TileViewData(new Image[]{images.get("dirt")}, new int[]{0},icons.get("empty")));
		tvdList.put("plowed", new TileViewData(new Image[]{images.get("plowed")}, new int[]{0},icons.get("empty")));
		tvdList.put("house", new TileViewData(new Image[]{images.get("house")}, new int[]{0},icons.get("empty")));
		tvdList.put("silo", new TileViewData(new Image[]{images.get("silo1")}, new int[]{0},icons.get("empty")));
		tvdList.put("wharehouse", new TileViewData(new Image[]{images.get("wharehouse1")}, new int[]{0},icons.get("empty")));
		tvdList.put("mill", new TileViewData(new Image[]{images.get("mill1")}, new int[]{0},icons.get("empty")));
		tvdList.put("bakery", new TileViewData(new Image[]{images.get("bakery1")}, new int[]{0},icons.get("empty")));
		
		tvdList.put("Sunflower seeds", new TileViewData(new Image[]{images.get("planted1"),images.get("planted2"),
		   		images.get("sunflowers1"),images.get("sunflowers2")}, new int[]{ 0, 25, 50, 90},icons.get("sunflower")));
		
		tvdList.put("Wheat grain", new TileViewData(new Image[]{images.get("planted1"),images.get("planted2"),
		   		images.get("cereal1"),images.get("cereal2")}, new int[]{ 0, 25, 50, 90},icons.get("wheat")));
		
		tvdList.put("Barley grain", new TileViewData(new Image[]{images.get("planted1"),images.get("planted2"),
		   		images.get("cereal1"),images.get("cereal2")}, new int[]{ 0, 25, 50, 90},icons.get("barley")));
		
		tvdList.put("Soybeans", new TileViewData(new Image[]{images.get("planted1"),images.get("planted2"),
		   		images.get("cereal1"),images.get("cereal2")}, new int[]{ 0, 25, 50, 90},icons.get("soy")));
		
		tvdList.put("Onions", new TileViewData(new Image[]{images.get("planted1"),images.get("planted2"),
		   		images.get("potato1"),images.get("potato2")}, new int[]{ 0, 25, 50, 90},icons.get("onion")));
		
		tvdList.put("Carrots", new TileViewData(new Image[]{images.get("planted1"),images.get("planted2"),
		   		images.get("potato1"),images.get("potato2")}, new int[]{ 0, 25, 50, 90},icons.get("carrot")));
		
		tvdList.put("Potatoes", new TileViewData(new Image[]{images.get("planted1"),images.get("planted2"),
						images.get("potato1"),images.get("potato2")}, new int[]{ 0, 25, 50, 90},icons.get("potato")));
		
		tvdList.put("forest", new TileViewData(new Image[]{images.get("forest_winter"),images.get("forest_spring"),
				   		images.get("forest_summer"),images.get("forest_automn")}, new int[]{ 0, 1, 2, 3},icons.get("empty")));
		
		
	}
	
	public static TileViewData getTVD(String key){
		TileViewData tvd = tvdList.get(key);
		if(tvd==null)System.out.println("no TileViewData available for : "+ key);
		return tvd;
	}
	
	
	public static TileViewData getTVD(SeedData seed){
		TileViewData tvd = tvdList.get(seed.getProdName());
		if(tvd==null){
			//System.out.println("RenderingData.java : no TileViewData available for : "+ seed.getProdName());
			
			ArrayList<String> catList = MyData.categoriesOf(seed.getProdName());
			if(catList.contains("Cereal")){
				tvd = tvdList.get("Wheat grain");
			}else if(catList.contains("Vegetable")){
				tvd = tvdList.get("Potatoes");
			}else{
				tvd = tvdList.get("Wheat grain");
				//System.out.println("RenderingData.java : no TileViewData available for : "+ seed.getProdName() + " : used dirt as default");
			}
			
		}
		return tvd;
	}
	
}
