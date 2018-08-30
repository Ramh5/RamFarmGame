package homier.farmGame;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class Tile {
	private String ID;
	private ImageView imageView;
	
	public Tile(){
		ID = "EMPTY_TILE";
		imageView = new ImageView(Game.emptyTileImage);
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				if (event.getButton()==MouseButton.PRIMARY){
					//imageView.setImage(new Image("dirt_tile.png")); 
				}
			}
		});//eventhandler mouse clicked
	}// empty constructor
	
	public Tile(String ID, Image image){
		this.ID=ID;
		this.imageView= new ImageView(image);
	}

	public String getID(){
		return ID;
	}
	
	public ImageView getImageView() {
		return imageView;
	}
}
