package homier.farmGame.view;

import java.util.ArrayList;

import homier.farmGame.controller.App;
import homier.farmGame.controller.Engine;
import homier.farmGame.model.Employee;
import homier.farmGame.model.FarmTask;
import homier.farmGame.model.Inventory;
import homier.farmGame.model.Product;
import homier.farmGame.model.tile.BuildingTile;
import homier.farmGame.model.tile.FarmPlot;
import homier.farmGame.model.tile.Tile;
import homier.farmGame.utils.GameClock;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class Renderer {

	public static int tileSize = 100;

	private final Image emptyTileImage = new Image("empty_tile.png", tileSize, tileSize, true, true);
	private final Image farmPlotImage = new Image("dirt_plot.png", tileSize, tileSize, true, true);
	private final Image sown1Image = new Image("sown1_plot.png", tileSize, tileSize, true, true);
	private final Image sown2Image = new Image("sown2_plot.png", tileSize, tileSize, true, true);
	private final Image wheat1Image = new Image("wheat1_plot.png", tileSize, tileSize, true, true);
	private final Image wheat2Image = new Image("wheat2_plot.png", tileSize, tileSize, true, true);
	private final Image houseImage = new Image("farmhouse.png", tileSize, tileSize, true, true);
	private final Image forest1Image = new Image("spring_tile.png", tileSize, tileSize, true, true);
	private final Image forest2Image = new Image("summer_tile.png", tileSize, tileSize, true, true);
	private final Image forest3Image = new Image("automn_tile.png", tileSize, tileSize, true, true);
	private final Image forest4Image = new Image("winter_tile.png", tileSize, tileSize, true, true);

	private final TileViewData wheatViewData = new TileViewData(
			new Image[] { sown1Image, sown2Image, wheat1Image, wheat2Image }, new int[] { 0, 25, 50, 90 });
	private final TileViewData houseViewData = new TileViewData(new Image[] { houseImage }, new int[] { 0 });
	private final TileViewData forestViewData = new TileViewData(new Image[] { forest4Image,forest1Image,forest2Image,forest3Image }, new int[] { 0,1,2,3});
	private final TileViewData farmPlotViewData = new TileViewData(new Image[] { farmPlotImage }, new int[] { 0 });

	private int[] previousMap;

	public Renderer(ArrayList<Tile> tileList, GridPane grid) {
		init(tileList, grid);
	}

	public void init(ArrayList<Tile> tileList, GridPane grid) {
		previousMap = new int[tileList.size()];

		for (int i = 0; i < App.gridColumns; i++) {
			for (int j = 0; j < App.gridRows; j++) {
				int index = App.gridRows * i + j;
				previousMap[index] = -1;
				grid.getChildren().add(new ImageView(emptyTileImage));
			} // for j
		} // for i
	}

	public void render(Engine engine) {

		ArrayList<Tile> tileList = engine.getGame().getTileList();
		GridPane grid = engine.getGameGridPane();
		ToggleButton pauseButton = engine.getPauseButton();
		VBox mouseOverPanel = engine.getMouseOverPanel();
		Inventory inventory = engine.getGame().getInventory();
		GameClock gameClock = engine.getGameClock();
		Employee activeEmployee= engine.getActiveEmployee();
		
		// create popup menu for the tiles and set to pause when shown and
		// unpause when hidden only if the game was not manualy paused prior
		Button b1 = new Button("");
		Button b2 =	new Button("");
		Button b3 =	new Button("");
		Button b4 =	new Button("");
		b1.getStyleClass().add("button-popup");
		b2.getStyleClass().add("button-popup");
		b3.getStyleClass().add("button-popup");
		b4.getStyleClass().add("button-popup");
		GridPane popupGrid = new GridPane();
		popupGrid.add(b1, 0, 0);
		popupGrid.add(b2, 1, 0);
		popupGrid.add(b3, 0, 1);
		popupGrid.add(b4, 1, 1);
		CustomMenuItem customMI = new CustomMenuItem(popupGrid, true);
		ContextMenu popup = new ContextMenu();
		popup.getItems().setAll(customMI);
		popup.setOnHidden(e -> {
			if (!engine.getManPaused()) {
				pauseButton.setSelected(false);
				engine.updatePauseButton();
			}
		});
		popup.setOnShown(e -> {
			pauseButton.setSelected(true);
			engine.updatePauseButton();
		});
		
		for (int i = 0; i < App.gridColumns; i++) {
			for (int j = 0; j < App.gridRows; j++) {

				int index = App.gridRows * i + j;
				Tile tile = tileList.get(index);
				String tileID = tile.getID();
				int newIndexToRender;
								
				switch (tileID) {
				case "FOREST_TILE":
					newIndexToRender = forestViewData.getIndexToRender((gameClock.getMonth()+1)/3%4);
					setImageView(i, j, index, newIndexToRender, forestViewData, grid);
					break;
				case "HOUSE_TILE":
					newIndexToRender = houseViewData.getIndexToRender(0);
					if (newIndexToRender != previousMap[index]) {
						ImageView newImageView = houseViewData.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;

						// set UI
						
						
						newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
							public void handle(MouseEvent event) {
								if (event.getButton() == MouseButton.PRIMARY) {
									b1.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<100);
									b1.setText("Omelette");
									b1.setOnAction(e -> {	
										//TODO prevent generating cooking task if not enough items
										FarmTask cookOmelette = new FarmTask("Cook", 100, 40, gameClock.getTotalSeconds());
										activeEmployee.setTask(cookOmelette);
										activeEmployee.spendEnergy(cookOmelette.getEnergyCost());
										//System.out.println("inside b1 OnAction of house_tile   tile: "+tile);
										cookOmelette.isCompleteProperty().addListener(new ChangeListener<Boolean>() {
										
								            @Override
								            public void changed(ObservableValue<? extends Boolean> obs, Boolean old, Boolean val) {
								              if(val){
								          
								            	  ((BuildingTile) tile).cook("Omelette", inventory );
								            	  engine.getLeftTextArea().setText(engine.getGame().getInventory().toString());
								            	  activeEmployee.setIsWorking(false);
								            	  //System.out.println("inside task changelistener cooking omelette tile: "+tile);
								            	
								              }
								            }
								        });//changelistener on the task isComplete boolean property
										
									});
									popup.show(newImageView, event.getScreenX(), event.getScreenY());
								}
							}
						});// eventhandler mouse clicked
					} // if
					break;
				case "FARM_PLOT":
					newIndexToRender = farmPlotViewData.getIndexToRender(0);
					if (newIndexToRender != previousMap[index]) {
						ImageView newImageView = farmPlotViewData.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;
						// set UI
						
						
						newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {	
							public void handle(MouseEvent event) {
								if (event.getButton() == MouseButton.PRIMARY) {
									b1.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<100);
									b1.setText("Plant Wheat");
									
									b1.setOnAction(e -> {
										//TODO  pause while no task
										//create the task, add it to the active employee and create a changelistener to finish the task
										FarmTask plantWheat = new FarmTask("Plant", 100, 20, gameClock.getTotalSeconds());
										activeEmployee.setTask(plantWheat);
										activeEmployee.spendEnergy(plantWheat.getEnergyCost());
										plantWheat.isCompleteProperty().addListener(new ChangeListener<Boolean>() {

								            @Override
								            public void changed(ObservableValue<? extends Boolean> obs, Boolean old, Boolean val) {
								              if(val){
								            	  Tile newTile = new FarmPlot("WHEAT_PLOT", 15, 400,new int[]{15,25});
								            	  tileList.set(index, newTile);
								            	  previousMap[index] = -1; 
								            	  activeEmployee.setIsWorking(false);
								            	 
								            	  
								              }
								            }
								        });//changelistener on the task isComplete boolean property
									});//plant button setOnAction event handler
									popup.show(newImageView, event.getScreenX(), event.getScreenY());
								}//if primary mouse click (ie left mouse click)
							}//handle() for the ImageView mouse click event
						});// eventhandler mouse clicked
					} // if new image to render, ie if there was a change to be rendered
					break;
				case "WHEAT_PLOT":
					DoubleProperty growthProperty = ((FarmPlot) tile).growthProperty();
					DoubleProperty yieldProperty = ((FarmPlot) tile).yieldProperty();
					newIndexToRender = wheatViewData.getIndexToRender(growthProperty.get());
					if (newIndexToRender != previousMap[index]) {
						ImageView newImageView = wheatViewData.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;

						// set UI
						newImageView.setOnMouseEntered(e -> {
							((Labeled) mouseOverPanel.getChildren().get(0)).textProperty()
									.bind(growthProperty.asString("%.0f"));
							((Labeled) mouseOverPanel.getChildren().get(1)).textProperty()
									.bind(yieldProperty.asString("%.0f"));
						});
						
						newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
							public void handle(MouseEvent event) {
								if (event.getButton() == MouseButton.PRIMARY) {
									
									b1.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<100);
									b1.setText("Harvest Wheat");
									b1.setOnAction(e -> {
										
										//create the task, add it to the active employee and create a changelistener to finish the task
										FarmTask harvestWheat = new FarmTask("Harvest", 100, 30, gameClock.getTotalSeconds());
										activeEmployee.setTask(harvestWheat);
										activeEmployee.spendEnergy(harvestWheat.getEnergyCost());
										harvestWheat.isCompleteProperty().addListener(new ChangeListener<Boolean>() {

								            @Override
								            public void changed(ObservableValue<? extends Boolean> obs, Boolean old, Boolean val) {
								              if(val){
								            	  inventory.addProd(new Product(  "Wheat",((FarmPlot) tile).getYield(),1,1));
												  engine.getLeftTextArea().setText(engine.getGame().getInventory().toString());
								            	  Tile newTile = new FarmPlot("FARM_PLOT", 0, 0);
								            	  tileList.set(index, newTile);
								            	  previousMap[index] = -1; 
								            	  activeEmployee.setIsWorking(false);
								              }
								            }
								        });//changelistener on the task isComplete boolean property
									});
									popup.show(newImageView, event.getScreenX(), event.getScreenY());
								}
							}
						});// eventhandler mouse clicked
					} // if

					break;
				default:
					break;
				}// switch

			} // for j
		} // for i
	}// render method

	// method to set the imageView on the grid for a tile
	private void setImageView(int i, int j, int index, int newIndexToRender, TileViewData tileViewData, GridPane grid) {
		if (newIndexToRender != previousMap[index]) {
			ImageView newImageView = tileViewData.getImageToRender(newIndexToRender);
			GridPane.setConstraints(newImageView, i, j);
			grid.getChildren().set(index, newImageView);
			previousMap[index] = newIndexToRender;

		}
	}

}
