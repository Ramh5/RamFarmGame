package homier.farmGame;

import javafx.scene.image.Image;

public class Tile {
	private int size;
	private Image image;
	
	public Tile(int size,Image image){
		this.size=size;
		this.image=image;
	}

	public int getSize() {
		return size;
	}


	public Image getImage() {
		return image;
	}

	
	
	
}
