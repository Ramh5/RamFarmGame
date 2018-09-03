package homier.farmGame;



import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class FarmPlot extends Tile{

	private double growthRate;
	private double growth;
	private int yield;
	private String product;
	private int quality;
	private ContextMenu popup;
	
	public FarmPlot(String ID, Image image, double growthRate, int yield) {
		super(ID, image);
		this.growthRate = growthRate;
		this.growth = 0;
		this.yield = yield;
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = 0;
	
		
	}
	
	public FarmPlot(String ID, Image image, double growthRate, double growth, int yield, int quality) {
		super(ID, image);
		this.growthRate = growthRate;
		this.growth = growth;
		this.yield = yield;
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = quality;
	
		
	}
	public FarmPlot(String ID, Image[] images, int[] map, double growthRate, double growth, int yield, int quality) {
		super(ID, images, map);
		this.growthRate = growthRate;
		this.growth = growth;
		this.yield = yield;
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = quality;
	
		
	}
	public FarmPlot(String ID, Image[] images, int[] map, double growthRate, int yield) {
		super(ID, images, map);
		this.growthRate = growthRate;
		this.growth = 0;
		this.yield = yield;
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = 0;
	
		
	}
	
	public void update(double dTime){
		if(growth<100)
		growth += growthRate*dTime/Game.secondsInADay;
	}
		
	// only works for 3 stages for now, need a loop to make it more robust
		public ImageView getImageToRender(){
			if(getMap().length==1)
				return getImageViews()[0];
			
			if(growth<getMap()[1])
				return getImageViews()[0];
			
			if(growth<getMap()[2])
				return getImageViews()[1];
			
				return getImageViews()[2];
		}
	
	
	//methode to set a popup menu controlled by the different tiles, but needing to update the grid
	public void setUI(Grid theGrid, int i){
		ImageView imageView = this.getImageToRender();
		MenuItem menuItem = new MenuItem("Plant wheat");
		/*
		MenuItem menu2 = new MenuItem("menu2");
		Menu menu3 = new Menu("item3");
		MenuItem menu4 = new MenuItem("menu4");
		menu3.getItems().add(menu4);
		*/
		popup = new ContextMenu();
		popup.getItems().addAll(menuItem);
		
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				
				if (event.getButton()==MouseButton.PRIMARY){
					Game.pause=true;
					
					menuItem.setOnAction(e->{ 
						Image[] images = {Game.dirtTileImage, Game.wheat1Image, Game.wheat2Image};
						Tile newTile = new FarmPlot("WHEAT_PLOT", images,new int[]{0,25,90} , 15, 400);
						theGrid.getTileList().set(i,newTile);
						
					});

					popup.show(imageView, event.getScreenX(), event.getScreenY());
					
					
				}
			}
		});//eventhandler mouse clicked
	}

	
	
	public double getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(double growthRate) {
		this.growthRate = growthRate;
	}

	public double getGrowth() {
		return growth;
	}

	public void setGrowth(double growth) {
		this.growth = growth;
	}

	public int getYield() {
		return yield;
	}

	public void setYield(int yield) {
		this.yield = yield;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public ContextMenu getPopup() {
		return popup;
	}

	public void setPopup(ContextMenu popup) {
		this.popup = popup;
	}
	
	public ImageView[] getImageViews(){
		return super.getImageViews();
	}
	
	public int[] getMap(){
		return super.getMap();
	}
	
	public String toString(){
		return (super.toString() + String.format("\tGrowth Rate: %.0f", growthRate) + String.format("\t  Growth: %.0f", growth) + "\tyield: " + yield + "\tproduct: " + "\tquality: " + quality);
	}
	
}
