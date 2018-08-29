package homier.farmGame;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class Tile {
	private static String ID;
	private int size;
	//private Image image;
	private ImageView imageView;
	
	public Tile(){
		imageView = new ImageView(Game.emptyTileImage);
		
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				if (event.getButton()==MouseButton.PRIMARY){
					imageView.setImage(new Image("dirt_tile.png")); 
				}
			}
		});//eventhandler mouse clicked
	}// empty constructor
	
	public Tile(int size,Image image){
		this.size=size;
		this.imageView= new ImageView(image);
	}

	public int getSize() {
		return size;
	}


	public ImageView getImageView() {
		return imageView;
	}

	
	
	
}
