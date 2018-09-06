package homier.farmGame.model;


import homier.farmGame.controller.App;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tile {

	private String ID;
	private ImageView[] imageViews;
	private int[] map;
	
	public Tile(){
		ID = "EMPTY_TILE";
		imageViews= new ImageView[1];
		imageViews[0] = new ImageView(App.emptyTileImage);
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
