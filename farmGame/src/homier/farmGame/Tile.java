package homier.farmGame;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class Tile {
	private String ID;
	private ImageView[] imageViews = new ImageView[5];
	
	public Tile(){
		ID = "EMPTY_TILE";
		imageViews[0] = new ImageView(Game.emptyTileImage);
		
		/*
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				if (event.getButton()==MouseButton.PRIMARY){
					//imageView.setImage(new Image("dirt_tile.png")); 
				}
			}
		});//eventhandler mouse clicked
		*/
	}// empty constructor
	
	public Tile(String ID, Image image){
		this.ID=ID;
		this.imageViews[0]= new ImageView(image);
	}

	public String getID(){
		return ID;
	}
	
	public void setImageView(ImageView imageView){
		this.imageViews[0] = imageView;
	}
	
	public ImageView getImageView(int i) {
		return imageViews[i];
	}
	
	public void update(double dTime){
		
	}
	
}
