package homier.farmGame;



import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class FarmPlot extends Tile{

	private double growthRate;
	private DoubleProperty growth;
	private IntegerProperty yield ;
	private String product;
	private int quality;
	private ContextMenu popup;
	
	public FarmPlot(String ID, Image image, double growthRate, int yield) {
		super(ID, image);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(0);
		this.yield = new SimpleIntegerProperty(yield);
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = 0;
	
		
	}
	
	public FarmPlot(String ID, Image image, double growthRate, double growth, int yield, int quality) {
		super(ID, image);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(growth);
		this.yield = new SimpleIntegerProperty(yield);
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = quality;
	
		
	}
	public FarmPlot(String ID, Image[] images, int[] map, double growthRate, double growth, int yield, int quality) {
		super(ID, images, map);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(growth);
		this.yield = new SimpleIntegerProperty(yield);
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = quality;
	
		
	}
	public FarmPlot(String ID, Image[] images, int[] map, double growthRate, int yield) {
		super(ID, images, map);
		this.growthRate = growthRate;
		this.growth = new SimpleDoubleProperty(0);
		this.yield = new SimpleIntegerProperty(yield);
		this.product = ID.substring(0, ID.indexOf("_"));
		this.quality = 0;
	
		
	}
	
	public void update(double dTime){
		if(growth.get()<100)
		growth.set(growth.get()+growthRate*dTime/Game.secondsInADay);
	}
		
	// only works for 3 stages for now, need a loop to make it more robust
		public ImageView getImageToRender(){
			
			if(getMap().length==1)
				return getImageViews()[0];
			
			for(int i=0;i<(getMap().length-1);i++){
				if(growth.get()<getMap()[i+1])
					return getImageViews()[i];
			}
			
				return getImageViews()[getMap().length-1];
		}
	
	
	//methode to set a popup menu controlled by the different tiles, that popup menu needs to be able to modify the grid
	public void setUI(Grid theGrid, int i){
		ImageView imageView = this.getImageToRender();
		MenuItem menuItem = new MenuItem();
		Label label = new Label("  growth : xx");
		SeparatorMenuItem separator = new SeparatorMenuItem();
		separator.setContent(label);
		popup = new ContextMenu();
		popup.setOnHidden(e->{Game.pause = false;});
		popup.getItems().addAll(menuItem,separator);
		VBox mouseOverInfo = (VBox) theGrid.getParent().getParent().getChildrenUnmodifiable().get(2);
		//System.out.println(theGrid.getParent().getParent().getChildrenUnmodifiable().get(2));
		
		imageView.setOnMouseEntered(e->{
				Label growthLabel = (Label) ((VBox) mouseOverInfo.getChildren().get(1)).getChildren().get(0);
				Label yieldLabel = (Label) ((VBox) mouseOverInfo.getChildren().get(1)).getChildren().get(1);
				
				
				growthLabel.textProperty().bind(growth.asString("%.0f"));
				
				yieldLabel.textProperty().bind(growth.multiply(yield.get()).multiply(.01).asString("%.0f"));
		});
		
		if(getID().equals("DIRT_PLOT")){
			
			imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
				public void handle(MouseEvent event) {
					if (event.getButton()==MouseButton.PRIMARY){
						Game.pause=true;
						menuItem.setText("Plant Wheat");
						menuItem.setOnAction(e->{ 
							Image[] images = {Game.dirtTileImage, Game.sown1Image, Game.sown2Image, Game.wheat1Image, Game.wheat2Image};
							Tile newTile = new FarmPlot("WHEAT_PLOT", images,new int[]{0,10,30,60,90} , 15, 400);
							theGrid.getTileList().set(i,newTile);
						});
						popup.show(imageView, event.getScreenX(), event.getScreenY());	
					}
				}
			});//eventhandler mouse clicked
		}
		
		if(getID().equals("WHEAT_PLOT")){
			
			imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
				public void handle(MouseEvent event) {
					if (event.getButton()==MouseButton.PRIMARY){
						Game.pause=true;
						menuItem.setText("Harvest "+ (int)(growth.get()/100.0*yield.get())+ " kg of wheat");
						menuItem.setOnAction(e->{ 
							Image[] images = {Game.dirtTileImage};
							Tile newTile = new FarmPlot("DIRT_PLOT", images,new int[]{0} , 0, 0);
							theGrid.getTileList().set(i,newTile);
						});
						popup.show(imageView, event.getScreenX(), event.getScreenY());
					}
				}
			});//eventhandler mouse clicked
		}
		
		
		/*
		MenuItem menu2 = new MenuItem("menu2");
		Menu menu3 = new Menu("item3");
		MenuItem menu4 = new MenuItem("menu4");
		menu3.getItems().add(menu4);
		*/
		//label as content of a separator to create a label in the context menu
		
		
		
		
	
	}

	
	
	public double getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(double growthRate) {
		this.growthRate = growthRate;
	}

	public final double getGrowth() {return growth.get();}

	public final void setGrowth(double growth) {this.growth.set(growth);}

	public DoubleProperty growthProperty(){return growth;}
	
	public final int getYield() {return yield.get();}

	public final void setYield(int yield) {this.yield.set(yield);}
	
	public IntegerProperty yieldProperty(){ return yield;}

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
