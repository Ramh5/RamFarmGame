package homier.farmGame.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileViewData {
	private Image[] images;
	private int[] map;
	
	
	
	public TileViewData(Image[] images, int[] map){
		this.images = images;
		this.map = map;	
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
	
	public ImageView getImageToRender(int index){
		
		return new ImageView(images[index]);
		
	}//getImageToRender method 
	
	public Image[] getImages() {
		return images;
	}

	public int[] getMap() {
		return map;
	}
}
