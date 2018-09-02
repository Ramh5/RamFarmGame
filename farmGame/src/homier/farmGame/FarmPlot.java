package homier.farmGame;



import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
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
	
	
	public void update(double dTime){
		if(growth<100){
		growth += growthRate/2*dTime;
		//System.out.println(dTime + " " + growth);
		}else{
			this.setImageView(new ImageView(Game.wheat2Image));
		}
	}
	
	
	//methode to set a popup menu controlled by the different tiles, but needing to update the grid
	public void setMouse(Grid theGrid, int i){
		ImageView imageView = this.getImageView(0);
		Tile tile = this;
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				System.out.println(event.getTarget());
				if (event.getButton()==MouseButton.PRIMARY){
					MenuItem menuItem = new MenuItem("Plant wheat");
					menuItem.setOnAction(e->{ 
						theGrid.getTileList().set(i,new FarmPlot("WHEAT_PLOT", Game.dirtTileImage, 15, 400));
						System.out.println(e.getTarget());
					});
					popup = new ContextMenu(menuItem);
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
	
	
	
}
