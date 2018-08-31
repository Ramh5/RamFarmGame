package homier.farmGame;



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
	
		setMouse();
	}
	
	public FarmPlot(String ID, Image image, double growthRate, double growth, int yield, int quality) {
		super(ID, image);
		this.growthRate = growthRate;
		this.growth = growth;
		this.yield = yield;
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = quality;
	
		setMouse();
	}
	
	
	public void update(double dTime){
		growth += growthRate*dTime;
		System.out.println(dTime + " " + growth);
	}
	
	
//set click to plant wheat, but only changes the imageview, does not realy change the Tile with all its property
	private void setMouse(){
		System.out.println("setMouse");
		ImageView imageView = this.getImageView();
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				System.out.println("test");
				if (event.getButton()==MouseButton.PRIMARY){
					MenuItem menuItem = new MenuItem("Plant wheat");
					menuItem.setOnAction(e->{ imageView.setImage(new Image("wheat1_plot.png")); });
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
