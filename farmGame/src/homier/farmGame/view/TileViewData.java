package homier.farmGame.view;

import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileViewData {
	private Image[] images;
	private int[] map;
	private Image icon;
	
	
	
	public TileViewData(Image[] images, int[] map, Image icon){
		this.images = images;
		this.map = map;	
		this.icon = icon;
	}

	//method to return the index corresponding to a certain value when compared against map[]	
	public int getIndexToRender(double value){
	
		if(map.length==1)
			return 0;
		
		for(int i=0;i<(map.length-1);i++){
			if(value<map[i+1])
				return i;
		}
		
			return  map.length-1;
	}//getIndexToRender method 
	
	public Group getImageToRender(int index){
		ImageView tileIV = new ImageView(images[index]);
		ImageView iconIV = new ImageView(icon);
		iconIV.setBlendMode(BlendMode.SRC_OVER);
		
		return new Group(tileIV,iconIV);
		
	}//getImageToRender method 
	
	public Image[] getImages() {
		return images;
	}

	public int[] getMap() {
		return map;
	}
}
