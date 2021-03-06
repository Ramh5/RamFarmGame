package homier.farmGame.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

import homier.farmGame.controller.App;
import homier.farmGame.controller.Engine;
import homier.farmGame.model.Employee;
import homier.farmGame.model.FarmTask;
import homier.farmGame.model.Inventory;
import homier.farmGame.model.MyData;
import homier.farmGame.model.Product;
import homier.farmGame.model.tile.BuildingTile;
import homier.farmGame.model.tile.FarmPlot;
import homier.farmGame.model.tile.ForestTile;
import homier.farmGame.model.tile.Tile;
import homier.farmGame.utils.GameClock;
import homier.farmGame.utils.TextFlowDataSet;
import homier.farmGame.utils.TextFlowManager;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

public class Renderer {
	
	private Button b1 = new Button("");
	private Button b2 = new Button("");
	private Button b3 = new Button("");
	private Button b4 = new Button("");
	private GridPane popupGrid = new GridPane();
	private CustomMenuItem customMI;
	private ContextMenu popup = new ContextMenu();
	private final static Image emptyTileImage = new Image("/icons/empty_tile.png", 100, 100, true, true);

	
	private int[] previousMap;
	public static boolean popupShown = false;
	
	public Renderer(ArrayList<Tile> tileList, GridPane grid) {
		new RenderingData();
		init(tileList, grid);
		b1.getStyleClass().add("button-popup");
		b2.getStyleClass().add("button-popup");
		b3.getStyleClass().add("button-popup");
		b4.getStyleClass().add("button-popup");
		b1.setWrapText(true);
		b2.setWrapText(true);
		b3.setWrapText(true);
		b4.setWrapText(true);
		b1.setTextAlignment(TextAlignment.CENTER);
		b2.setTextAlignment(TextAlignment.CENTER);
		b3.setTextAlignment(TextAlignment.CENTER);
		b4.setTextAlignment(TextAlignment.CENTER);
		popupGrid.add(b1, 0, 0);
		popupGrid.add(b2, 1, 0);
		popupGrid.add(b3, 0, 1);
		popupGrid.add(b4, 1, 1);
		customMI = new CustomMenuItem(popupGrid, true);
		popup.getItems().setAll(customMI);
		
		
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
		//VBox mouseOverPanel = engine.getMouseOverPanel();
		Inventory inventory = engine.getGame().getInventory();
		GameClock gameClock = engine.getGameClock();
		
		
		// create popup menu for the tiles and set to pause when shown and
		// unpause when hidden only if the game was not manualy paused prior
		b1.setText("");
		b2.setText("");
		b3.setText("");
		b4.setText("");
		b1.setDisable(true);
		b2.setDisable(true);
		b3.setDisable(true);
		b4.setDisable(true);
		
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
					//setImageView(i, j, index, newIndexToRender, forestTVD, grid);
					if (newIndexToRender != previousMap[index]) {
						Group newImageView = forestTVD.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;
						// set UI

						newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
							public void handle(MouseEvent event) {
								if (event.getButton() == MouseButton.PRIMARY) {
									Employee activeEmployee= engine.getActiveEmployee();
									
									b1.setDisable(activeEmployee.isWorking()||activeEmployee.getEnergy()<120);
									b1.setText("Collect mushrooms");
									b1.setOnAction(e -> {	
									FarmTask collectMushrooms = new FarmTask("Collecting mushrooms", 120, 20);
										activeEmployee.setTask(collectMushrooms);
										collectMushrooms.setResult(new Product(MyData.categoriesOf("Mushrooms"), "Mushrooms", 4.0,100, 
												100, Math.min(100,Math.max(0,(int)(50+new Random().nextGaussian()*10)))));
										collectMushrooms.startTask(gameClock.getTotalSeconds(), activeEmployee);
										
									});
									double price = ((ForestTile) tile).getPrice();
									b2.setDisable(price>engine.getGame().getInventory().getMoney());
									b2.setText("Buy land: " + String.format("%.0f$", price));
									b2.setOnAction(e -> {	
										engine.getGame().getInventory().addMoney(-price);
										tileList.set(index, new FarmPlot());
										
									});
									
									popup.show(newImageView, event.getScreenX(), event.getScreenY());
								}
							}
						});// eventhandler mouse clicked

					}
					break;
				case "BuildingTile":
					TileViewData buildingTVD = RenderingData.getTVD(((BuildingTile) tile).getWsType());
					newIndexToRender = buildingTVD.getIndexToRender(0);
					if (newIndexToRender != previousMap[index]) {
						Group newImageView = buildingTVD.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;
						engine.setupAvailWS();//update the workshops and storage available
						// set UI
						
						
						newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
							public void handle(MouseEvent event) {
								if (event.getButton() == MouseButton.PRIMARY) {
									//System.out.println(b1.getText() + " " + b2.getText());
								
									b1.setDisable(false);
									b1.setText("Workshops");
									
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
							Group newImageView = dirtTVD.getImageToRender(newIndexToRender);
							GridPane.setConstraints(newImageView, i, j);
							grid.getChildren().set(index, newImageView);
							previousMap[index] = newIndexToRender;
							// set UI
							
							
							newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {	
								public void handle(MouseEvent event) {
									if (event.getButton() == MouseButton.PRIMARY) {
										Employee activeEmployee= engine.getActiveEmployee();
										//System.out.println(engine.getActiveEmployee());
										double money = engine.getGame().getInventory().getMoney();
										b1.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<200);
										b2.setDisable(money<1000|| activeEmployee.getEnergy()<300);
										
										b1.setText("Plow");
										b2.setText("Add a building");
										
									
										b1.setOnAction(e -> {
											FarmTask plow = new FarmTask("Plow", 200, 30);
											activeEmployee.setTask(plow);
											plow.setPlow(true);
											plow.setNewTile(farmTile, index);
											plow.startTask(gameClock.getTotalSeconds(), activeEmployee);
											
										});//plant button setOnAction event handler
										b2.setOnAction(e -> {	
											buildDialogHandling(engine,index);
										});
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
							Group newImageView = farmTVD.getImageToRender(newIndexToRender);
							GridPane.setConstraints(newImageView, i, j);
							grid.getChildren().set(index, newImageView);
							previousMap[index] = newIndexToRender;
							// set UI
							newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {	
								public void handle(MouseEvent event) {
									if (event.getButton() == MouseButton.PRIMARY) {
										Employee activeEmployee= engine.getActiveEmployee();
										double waterEnergy = waterEnergy(farmTile);
										//System.out.println(engine.getActiveEmployee());
										b1.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<100+waterEnergy);
										b1.setText("Water and sow");
										b1.setOnAction(e -> {
											
											FarmTask plantSeed = new FarmTask("Water and sow", 100+waterEnergy, waterTime(farmTile)+20);
											activeEmployee.setTask(plantSeed);
											FarmPlot newFarmPlot = new FarmPlot();
											plantSeed.setWater(120, newFarmPlot);//Water at the same time as planting the seed
											plantSeed.setNewTile(newFarmPlot , index);
											engine.getSeedPane().toFront();
											engine.updateSeedPanel();		
										});//plant button setOnAction event handler
										b2.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<100);
										b2.setText("\"Un-plow\"");
										b2.setOnAction(e->{
											FarmTask unplow = new FarmTask("Un-plow", 100, 20);
											activeEmployee.setTask(unplow);
											unplow.setNewTile(new FarmPlot(), index);
											unplow.startTask(gameClock.getTotalSeconds(), activeEmployee);
										});
										popup.show(newImageView, event.getScreenX(), event.getScreenY());
									}//if primary mouse click (ie left mouse click)
								}//handle() for the ImageView mouse click event
							});// eventhandler mouse clicked
						} // if new image to render, ie if there was a change to be rendered
					}
					
					if(farmTile.isSown()){
						TileViewData sownTVD = RenderingData.getTVD(farmTile.getSeed());
						newIndexToRender = sownTVD.getIndexToRender(farmTile.getGrowth());
						if (newIndexToRender != previousMap[index]) {
							Group newImageView = sownTVD.getImageToRender(newIndexToRender);
							GridPane.setConstraints(newImageView, i, j);
							grid.getChildren().set(index, newImageView);
							previousMap[index] = newIndexToRender;

							// setup mouseOver for sown fields
							//TextFlowManager.farmTileTFDS=new TextFlowDataSet(farmTile, true, engine.getGameInfoTextFlow());
							newImageView.setOnMouseEntered(e -> {
								TextFlowManager.farmTileTFDS=new TextFlowDataSet(farmTile, true, engine.getGameInfoTextFlow());
							});
							newImageView.setOnMouseExited(e->{
								TextFlowManager.farmTileTFDS.off();				
							});
							
							newImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
								public void handle(MouseEvent event) {
									if (event.getButton() == MouseButton.PRIMARY) {
										Employee activeEmployee= engine.getActiveEmployee();
										
										//check if enough storage to enable harvest
										
										
										boolean enoughStorage = farmTile.enoughStorageToHarvest(inventory);
										b2.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<200||!enoughStorage);
										b2.setText("Harvest");
										b2.setOnAction(e -> {

											//create the task, add it to the active employee and create a changelistener to finish the task
											
											FarmTask harvestWheat = new FarmTask("Harvest", 200, 30);
											activeEmployee.setTask(harvestWheat);
											harvestWheat.setHarvestResults(farmTile);
											harvestWheat.setNewTile(new FarmPlot(), index);
											harvestWheat.startTask(gameClock.getTotalSeconds(), activeEmployee);
											
											//System.out.println("harvest task started " + previousMap + " index "+ index + " value " + previousMap[index]);
										});
										double waterEnergy = waterEnergy(farmTile);
										b1.setDisable(activeEmployee.isWorking() || activeEmployee.getEnergy()<waterEnergy);
										b1.setText("Water");
										b1.setOnAction(e -> {

											//create the task, add it to the active employee and create a changelistener to finish the task
										
											int waterTime = waterTime(farmTile);
											FarmTask waterTile = new FarmTask("Water", waterEnergy, waterTime);
											
											activeEmployee.setTask(waterTile);
											
											waterTile.setWater(120-farmTile.getWaterLevel(),farmTile);
											waterTile.startTask(gameClock.getTotalSeconds(), activeEmployee);
											
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

	private void buildDialogHandling(Engine engine, int index){
		Employee activeEmployee= engine.getActiveEmployee();
		GameClock gameClock = engine.getGameClock();
		HashMap<String,String> choices = new HashMap<>();
		choices.put("Silo", "silo");
		choices.put("Wharehouse", "wharehouse");
		choices.put("Wind mill", "mill");
		choices.put("Bakery", "bakery");
		ChoiceDialog<String> dialog = new ChoiceDialog<>("Silo", choices.keySet());
		dialog.setTitle("Select a building");
		dialog.setHeaderText("");
		ButtonType buildButtonType = new ButtonType("Build", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().setAll(buildButtonType, ButtonType.CANCEL);


		Optional<String> result = dialog.showAndWait();
		result.ifPresent(building -> {
			engine.getGame().getInventory().addMoney(-1000);
			FarmTask buildTask = new FarmTask("Building", 300, 100);
			activeEmployee.setTask(buildTask);
			BuildingTile newBuilding = new BuildingTile(choices.get(building), 0, 0, 0);
			if("Silo".equals(building)){
				newBuilding.setSiloSize(2000);
			}else if("Wharehouse".equals(building)){
				newBuilding.setStorageSize(10000);
			}else if("Wind mill".equals(building)){
				newBuilding.setSiloSize(500);
			}else if("Bakery".equals(building)){
				
			}
			buildTask.setNewTile(newBuilding, index);
			buildTask.startTask(gameClock.getTotalSeconds(), activeEmployee);
		});
	}
	/**
	 * calculates the energy required to water a FarmPlot as a function
	 * of missing water plus 30 flat energy
	 * @param farmPlot
	 * @return the energy required to water a FarmPlot
	 */
	private double waterEnergy(FarmPlot farmPlot){
		return (120-farmPlot.getWaterLevel())*1.5+50;
	}
	/**
	 * calculate the time required to water a FarmPlot as a function
	 * of missing water plus a 10 seconds flat time
	 * @param farmPlot
	 * @return time required to water a farmplot
	 */
	private int waterTime(FarmPlot farmPlot){
		return (int)(0.20*(120-farmPlot.getWaterLevel())+20);
	}
	public int[] getPreviousMap() {
		return previousMap;
	}

}
