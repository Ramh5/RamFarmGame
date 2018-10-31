package homier.farmGame.view;

import java.util.ArrayList;

import homier.farmGame.controller.App;
import homier.farmGame.controller.Engine;
import homier.farmGame.model.Employee;
import homier.farmGame.model.FarmTask;
import homier.farmGame.model.Inventory;
import homier.farmGame.model.MyData;
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
	
	

	private final static Image emptyTileImage = new Image("empty_tile.png", 100, 100, true, true);

	
	private int[] previousMap;
	public static boolean popupShown = false;
	
	public Renderer(ArrayList<Tile> tileList, GridPane grid) {
		new RenderingData();
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
		//ToggleButton pauseButton = engine.getPauseButton();
		VBox mouseOverPanel = engine.getMouseOverPanel();
		
		GameClock gameClock = engine.getGameClock();
		
		
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
			popupShown = false;
			engine.updatePauseButton();
		});
		popup.setOnShown(e -> {
			popupShown = true;
			engine.updatePauseButton();
		});

		for (int i = 0; i < App.gridColumns; i++) {
			for (int j = 0; j < App.gridRows; j++) {

				int index = App.gridRows * i + j;
				Tile tile = tileList.get(index);
				String tileType = tile.getType();
				int newIndexToRender;
								
				switch (tileType) {
				case "ForestTile":
					TileViewData forestTVD = RenderingData.getTVD("forest");
					newIndexToRender = forestTVD.getIndexToRender((gameClock.getMonth()+1)/3%4);
					setImageView(i, j, index, newIndexToRender, forestTVD, grid);
					break;
				case "BuildingTile":
					TileViewData houseTVD = RenderingData.getTVD("house");
					newIndexToRender = houseTVD.getIndexToRender(0);
					if (newIndexToRender != previousMap[index]) {
						ImageView newImageView = houseTVD.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;

						// set UI
						
						
						newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
							public void handle(MouseEvent event) {
								if (event.getButton() == MouseButton.PRIMARY) {
									
									b1.setText("Cuisine");
									b1.setOnAction(e -> {	
										engine.getOpenWSbutton().fire();
									});
									popup.show(newImageView, event.getScreenX(), event.getScreenY());
								}
							}
						});// eventhandler mouse clicked
					} // if
					break;
				case "FarmPlot":
					
					FarmPlot farmTile = (FarmPlot) tile;
					
					if(!farmTile.isPlowed()){
						TileViewData dirtTVD = RenderingData.getTVD("dirt");
						newIndexToRender = dirtTVD.getIndexToRender(0);
						if (newIndexToRender != previousMap[index]) {
							ImageView newImageView = dirtTVD.getImageToRender(newIndexToRender);
							GridPane.setConstraints(newImageView, i, j);
							grid.getChildren().set(index, newImageView);
							previousMap[index] = newIndexToRender;
							// set UI
							
							
							newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {	
								public void handle(MouseEvent event) {
									if (event.getButton() == MouseButton.PRIMARY) {
										Employee activeEmployee= engine.getActiveEmployee();
										//System.out.println(engine.getActiveEmployee());
										b1.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<100);
										b1.setText("Labourer");
										
										b1.setOnAction(e -> {
											FarmTask plow = new FarmTask("Labourer", 100, 30, gameClock.getTotalSeconds());
											activeEmployee.setTask(plow);
											plow.setPlow(true);
											plow.setNewTile(farmTile, index);
											plow.startTask(gameClock.getTotalSeconds(), activeEmployee);
											
										});//plant button setOnAction event handler
										popup.show(newImageView, event.getScreenX(), event.getScreenY());
									}//if primary mouse click (ie left mouse click)
								}//handle() for the ImageView mouse click event
							});// eventhandler mouse clicked
						} // if new image to render, ie if there was a change to be rendered
					}
					
					if(farmTile.isPlowed()&&!farmTile.isSown()){
						TileViewData farmTVD = RenderingData.getTVD("plowed");
						newIndexToRender = farmTVD.getIndexToRender(0);
						if (newIndexToRender != previousMap[index]) {
							ImageView newImageView = farmTVD.getImageToRender(newIndexToRender);
							GridPane.setConstraints(newImageView, i, j);
							grid.getChildren().set(index, newImageView);
							previousMap[index] = newIndexToRender;
							// set UI
							
							
							newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {	
								public void handle(MouseEvent event) {
									if (event.getButton() == MouseButton.PRIMARY) {
										Employee activeEmployee= engine.getActiveEmployee();
										//System.out.println(engine.getActiveEmployee());
										b1.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<100);
										b1.setText("Semer");
										
										b1.setOnAction(e -> {
											//TODO  pause while no task
											engine.getSeedPane().toFront();
											engine.getSeedPane().setUserData(index);
											engine.updateSeedPanel();
											
										});//plant button setOnAction event handler
										popup.show(newImageView, event.getScreenX(), event.getScreenY());
									}//if primary mouse click (ie left mouse click)
								}//handle() for the ImageView mouse click event
							});// eventhandler mouse clicked
						} // if new image to render, ie if there was a change to be rendered
					}
					
					if(farmTile.isSown()){
						DoubleProperty growthProperty = farmTile.growthProperty();
						DoubleProperty yieldProperty = farmTile.yieldProperty();
						TileViewData sownTVD = RenderingData.getTVD(farmTile.getSeed());
						newIndexToRender = sownTVD.getIndexToRender(growthProperty.get());
						if (newIndexToRender != previousMap[index]) {
							ImageView newImageView = sownTVD.getImageToRender(newIndexToRender);
							GridPane.setConstraints(newImageView, i, j);
							grid.getChildren().set(index, newImageView);
							previousMap[index] = newIndexToRender;

							// set UI
							newImageView.setOnMouseEntered(e -> {
								((Labeled) mouseOverPanel.getChildren().get(1)).textProperty()
								        .set(farmTile.getSeed().getName());
								((Labeled) mouseOverPanel.getChildren().get(2)).textProperty()
										.bind(growthProperty.asString("%.0f"));
								((Labeled) mouseOverPanel.getChildren().get(3)).textProperty()
										.bind(yieldProperty.asString("%.0f"));
								
							});
							
							newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
								public void handle(MouseEvent event) {
									if (event.getButton() == MouseButton.PRIMARY) {
										Employee activeEmployee= engine.getActiveEmployee();
										b1.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<100);
										b1.setText("Récolter");
										b1.setOnAction(e -> {
											
											//create the task, add it to the active employee and create a changelistener to finish the task
											
											FarmTask harvestWheat = new FarmTask("Récolter", 100, 30, gameClock.getTotalSeconds());
											activeEmployee.setTask(harvestWheat);
											
											harvestWheat.setResult(new Product(MyData.categoriesOf(farmTile.getSeed().getProdName()),
																	   		farmTile.getSeed().getProdName(),farmTile.getYield(),1,farmTile.getQual()));
											harvestWheat.setNewTile(new FarmPlot(), index);
											harvestWheat.startTask(gameClock.getTotalSeconds(), activeEmployee);
											
											//System.out.println("harvest task started " + previousMap + " index "+ index + " value " + previousMap[index]);
										});
										popup.show(newImageView, event.getScreenX(), event.getScreenY());
									}
								}
							});// eventhandler mouse clicked
						} // if
					}
					
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

	public int[] getPreviousMap() {
		return previousMap;
	}

}
