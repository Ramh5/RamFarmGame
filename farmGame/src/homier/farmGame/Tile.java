package homier.farmGame;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class Tile {
	private String ID;
	private ImageView[] imageViews;
	private int[] map;
	public Tile(){
		ID = "EMPTY_TILE";
		imageViews= new ImageView[1];
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
		imageViews = new ImageView[1];
		map = new int[imageViews.length];
		imageViews[0]= new ImageView(image);
		
	}
	
	public Tile(String ID, Image[] images, int[] map){
		this.ID=ID;
		imageViews = new ImageView[images.length];
		this.map = map;
		
		for(int i=0;i<images.length;i++){
			this.imageViews[i]= new ImageView(images[i]);
		}
		
	}
	
	//methode to set a popup menu controlled by the different tiles, but needing to update the grid
	public void setMouse(Grid theGrid, int i){
		
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
	
	public ImageView[] getImageViews(){
		return imageViews;
	}
	
	public int[] getMap(){
		return map;
	}
	
	public ImageView getImageToRender(){
		return imageViews[0];
	}
	
	public void update(double dTime){
		
	}
	
	public String toString(){
		return ("ID: " + ID +"\tStages: " + imageViews.length);
	}
	
}
