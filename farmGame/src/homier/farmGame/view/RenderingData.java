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



	public RenderingData(){

		images = new HashMap<>();
		tvdList = new HashMap<>();
		//load up all the images in the image folder
		File folder = new File(App.TILE_IMAGES_PATH);
		String[] fileNames = folder.list();
		for(String name:fileNames){
			images.put(name.replace(".png", ""), new Image("/tiles/"+name, tileSize, tileSize, true, true));
			//System.out.println(name.replace(".png", ""));
		}
		
		
		tvdList.put("dirt", new TileViewData(new Image[]{images.get("dirt")}, new int[]{0}));
		tvdList.put("plowed", new TileViewData(new Image[]{images.get("plowed")}, new int[]{0}));
		tvdList.put("house", new TileViewData(new Image[]{images.get("house")}, new int[]{0}));
		tvdList.put("cereal", new TileViewData(new Image[]{images.get("sown1"),images.get("sown2"),
						images.get("cereal1"),images.get("cereal2")}, new int[]{ 0, 25, 50, 90}));
		tvdList.put("Patates", new TileViewData(new Image[]{images.get("sown1"),images.get("sown2"),
						images.get("potato1"),images.get("potato2")}, new int[]{ 0, 25, 50, 90}));
		tvdList.put("forest", new TileViewData(new Image[]{images.get("forest_winter"),images.get("forest_spring"),
				   		images.get("forest_summer"),images.get("forest_automn")}, new int[]{ 0, 1, 2, 3}));
		
		
	}
	
	public static TileViewData getTVD(String key){
		TileViewData tvd = tvdList.get(key);
		if(tvd==null)System.out.println("no TileViewData available for : "+ key);
		return tvd;
	}
	
	//TODO implement categories on SeedData, using a categories database?
	public static TileViewData getTVD(SeedData seed){
		TileViewData tvd = tvdList.get(seed.getProdName());
		if(tvd==null){
			System.out.println("RenderingData.java : no TileViewData available for : "+ seed.getProdName());
			
			ArrayList<String> catList = MyData.categoriesOf(seed.getProdName());
			if(catList.contains("Céréale")){
				tvd = tvdList.get("cereal");
			}else if(catList.contains("Légume")){
				tvd = tvdList.get("Patates");
			}else{
				tvd = tvdList.get("dirt");
				System.out.println("RenderingData.java : no TileViewData available for : "+ seed.getProdName() + " : used dirt as default");
			}
			
		}
		return tvd;
	}
	
}
