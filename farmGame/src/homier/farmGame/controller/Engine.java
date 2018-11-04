package homier.farmGame.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import org.hildan.fxgson.FxGson;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import homier.farmGame.model.Employee;
import homier.farmGame.model.FarmTask;
import homier.farmGame.model.Game;
import homier.farmGame.model.Inventory;
import homier.farmGame.model.MyData;
import homier.farmGame.model.Product;
import homier.farmGame.model.Recipe;
import homier.farmGame.model.Shop;
import homier.farmGame.model.WorkShop;
import homier.farmGame.model.tile.BuildingTile;
import homier.farmGame.model.tile.FarmPlot;
import homier.farmGame.model.tile.ForestTile;
import homier.farmGame.model.tile.Tile;
import homier.farmGame.utils.FarmTimeUnits;
import homier.farmGame.utils.GameClock;
import homier.farmGame.utils.RuntimeTypeAdapterFactory;
import homier.farmGame.view.Renderer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class Engine {

	@FXML private GridPane gameGridPane;
	@FXML private Button actionWSbutton, buyButton;//closeShopButton, cancelTransactionButton, sellButton;
	@FXML private ToggleButton pauseButton, openShopButton, openWSbutton;
	@FXML private VBox mouseOverPanel;
	@FXML private TextArea leftTextArea;
	@FXML private ChoiceBox<Integer> gameSpeedChoice;
	@FXML private Label clockLabel, pauseLabel ,wxToday, wxTomorrow, energyLabel, taskName1, buyTotalLabel, sellTotalLabel, labelSelectedRecipe, labelResultWS;
	@FXML private ChoiceBox<Employee> employeeChoice;
	@FXML private ChoiceBox<String> wsChoiceBox,seedCatChoiceBox ;
	@FXML private ProgressIndicator taskProgress1;
	@FXML private AnchorPane shopPane, workShopPane, seedPane;
	@FXML private TreeTableView<Product> tableInv, tableShop, tableInvWS, tableSeed;
	@FXML private TreeTableColumn<Product, String> colNameInv, colNameShop,colNameInvWS,colNameSeed, colQtyInv, colQtyShop, colQtyInvWS,colQtySeed, 
												   colPriceInv, colPriceShop, colPriceInvWS,colPriceSeed, colSpoilQtyInv, colSpoilQtyShop, colSpoilQtyInvWS,colSpoilQtySeed;
	@FXML private TreeTableColumn<Product, Number> colFreshInv, colFreshShop, colFreshInvWS,colFreshSeed, colQualInv, colQualShop,colQualInvWS,colQualSeed;
	@FXML private TreeTableColumn<Product, Boolean> colActInv, colActShop, colActInvWS,colActSeed;
	@FXML private TableView<Product>  tableSell, tableBuy, tableSelectedIngrWS;
	@FXML private TableColumn<Product, String> colNameSell, colNameBuy, colNameIngrWS, colQtySell, colQtyBuy, colQtyIngrWS, colPriceSell, colPriceBuy, colPriceIngrWS;
	@FXML private TableColumn<Product, Number>  colFreshSell, colFreshBuy, colFreshIngrWS,  
												colQualSell, colQualBuy, colQualIngrWS;				
	@FXML private TableColumn<Product, Boolean> colActSell, colActBuy, colActIngrWS;
	@FXML private ListView<String>  listViewRecipeDetails;
	@FXML private ListView<Recipe> listViewRecipe;
//	@FXML private MenuItem save,load;
	@FXML private TextFlow seedDetailTextFlow;
	@FXML private StackPane leftStackPane;
	@FXML private TextField shopFilterTextField,wsFilterTextField,seedFilterTextField;
	
	private Game game;
	private Renderer renderer;
	
	public static boolean manPaused = false;
	public static boolean otherPaused = false;
	
	FileChooser fileChooser = new FileChooser();
	
	//TODO add button to skip days, months...
	
	public void update(double dTime) {
		//Pause game when in the seed selection pane
		if(leftStackPane.getChildren().indexOf(seedPane)==leftStackPane.getChildren().size()-1){
			otherPaused=true;
			updatePauseButton();
			openShopButton.setDisable(true);
			openWSbutton.setDisable(true);
			pauseButton.setDisable(true);
			return;//not sure if this could introduce a bug since game might unpause for a tick after popup is hidden
		}
		
		game.getClock().update(dTime);
		clockLabel.setText(game.getClock().toString());
		
		//if it is a new day, update the forecast
		if(game.getClock().isNewDay()){
			gameSpeedChoice.getSelectionModel().select(new  Integer(1));//reset the gameSpeed
			game.getEmployees()[0].energyProperty().set(1000);
			game.getWxForcast().forcastNewDay();
			wxToday.setText("Today: "+game.getWxForcast().getToday().toString());
			wxTomorrow.setText("Tomorrow: "+game.getWxForcast().getTomorrow().toString());
			game.getClock().setIsNewDay(false);
			//update the inventory, especially for spoiling every day
			game.getInventory().update();
			game.getShop().update();
			updateShopPanel();
			updateWSPanel();
			
		}
		
		leftTextArea.setText(game.getInventory().toString());
		
		//update energy and task name when task is in progress
		taskProgress1.setProgress(getActiveEmployee().getTask().taskProgress(game.getClock().getTotalSeconds(), game.getInventory(),getActiveEmployee(), game.getTileList(), renderer.getPreviousMap()));
		taskName1.setText(getActiveEmployee().getTask().getName());
		
		//update each tile (only update the model, ie growth, yield)
		for (int i = 0; i < game.getTileList().size(); i++) {
			game.getTileList().get(i).update(dTime / game.getClock().getSecondsInADay(),game.getWxForcast().getToday());
		}
		
		

	}

	public void render() {
		renderer.render(this);

	}

	public void initialize() {
		
		if (game==null){
			game = new Game();//create a new game if not loaded save game
			fileChooser.setInitialDirectory(new File(getClass().getResource("/homier").getFile()));
			game.getClock().addTime(4*FarmTimeUnits.MONTH.seconds);
		}
		
		gameGridPane.getChildren().clear();
		renderer = new Renderer(game.getTileList(), gameGridPane);
		renderer.render(this);
		
		
		
		wxToday.setText("Today: "+game.getWxForcast().getToday().toString());
		wxTomorrow.setText("Tomorrow: "+game.getWxForcast().getTomorrow().toString());
		game.getClock().setIsNewDay(false);
		clockLabel.setText(game.getClock().toString());
		updatePauseButton();
		leftTextArea.setText(game.getInventory().toString());

		gameSpeedChoice.getItems().setAll(1,2,5,10,20,50,100,500,1000);
		gameSpeedChoice.getSelectionModel().select(3);
		App.gameSpeed=gameSpeedChoice.getSelectionModel().getSelectedItem();
		
		 //employee choice
		employeeChoice.setOnAction(e->{
			updateWSResultLabel();//to update the action button disable or not with the new employee
			energyLabel.textProperty().bind(getActiveEmployee().energyProperty().asString(" Energy: %.0f  "));
			taskProgress1.setProgress(getActiveEmployee().getTask().taskProgress(game.getClock().getTotalSeconds(), game.getInventory(),getActiveEmployee(), game.getTileList(), renderer.getPreviousMap()));
			taskName1.setText(getActiveEmployee().getTask().getName());
		});
		employeeChoice.getItems().setAll(game.getEmployees());
		employeeChoice.getSelectionModel().select(0);
		energyLabel.textProperty().bind(getActiveEmployee().energyProperty().asString(" Energy: %.0f  "));
		//task
		taskName1.setText(getActiveEmployee().getTask().getName());
		
		//shop
		setupShop();
		preventColumnReordering(tableInv);
		preventColumnReordering(tableShop);
		preventColumnReordering(tableSell);
		preventColumnReordering(tableBuy);
		
		//workShop
		setupAvailWS();
		wsChoiceBox.getSelectionModel().select(0);
		wsChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs,oldVal,newVal)->{
			//default selection to fix a bug when loading a new save game
			if(newVal==null)wsChoiceBox.getSelectionModel().select(0);
			
			updateWSPanel();
		});
		setupWS();
		
		//seedPane
		setupSeedPane();
		seedDetailTextFlow.getChildren().setAll(new Text("Choisissez une semence"));
		
	}
	/**
	 * populates the wsChoiceBox with the available workshops by looking through the tileList for buildings
	 * and updates the storage capacity of the inventory, and updates the level of the workshops
	 */
	public void setupAvailWS(){
		ArrayList<String> wsList = new ArrayList<>(Arrays.asList(MyData.getRecipeBook().getWSList()));
		ArrayList<String> availWS = new ArrayList<>();
		int[] wsMaxLevelList = new int[wsList.size()];
		Inventory inv = game.getInventory();
		inv.setSiloSize(0);
		inv.setStorageSize(0);
		for(Tile tile:game.getTileList()){
		
			if("BuildingTile".equals(tile.getType())){
				BuildingTile building = ((BuildingTile)tile);
				inv.setSiloSize(inv.getSiloSize()+building.getSiloSize());
				inv.setStorageSize(inv.getStorageSize()+building.getStorageSize());
				int i;
				switch(building.getWsType()){
				case "house":
					availWS.add("Kitchen");
					i = wsList.indexOf("Kitchen");
					wsMaxLevelList[i]=Math.max(wsMaxLevelList[i], building.getLevel());
					break;
				case "silo":
					availWS.add("Silo");
					i = wsList.indexOf("Silo");
					wsMaxLevelList[i]=Math.max(wsMaxLevelList[i], building.getLevel());
					break;
				case "mill":
					availWS.add("Wind mill");
					i = wsList.indexOf("Wind mill");
					wsMaxLevelList[i]=Math.max(wsMaxLevelList[i], building.getLevel());
				}
			}
		}
		game.getWorkShop().setWsLevelMap(MyData.getRecipeBook().getWSList(), wsMaxLevelList);
		wsChoiceBox.getItems().setAll(availWS);
	}

	//-------------- BUTTON HANDELERS---------------------
	@FXML
	private void gameSpeedChoiceAction(ActionEvent event){
		if(gameSpeedChoice.getSelectionModel().getSelectedItem()==null)gameSpeedChoice.getSelectionModel().select(3);
		App.gameSpeed=gameSpeedChoice.getSelectionModel().getSelectedItem();
	}
	
	@FXML 
	private void seedCancelButtonAction(ActionEvent event){
		seedPane.toBack();
		otherPaused=false;
		updatePauseButton();

		unselectTreeTable(tableSeed.getRoot());
		openShopButton.setDisable(false);
		openWSbutton.setDisable(false);
		pauseButton.setDisable(false);
	}
	
	@FXML 
	private void seedOKButtonAction(ActionEvent event){
		FarmTask plantWheat = new FarmTask("Sow", 100, 20);
		getActiveEmployee().setTask(plantWheat);
		plantWheat.setNewTile( new FarmPlot(), (int)seedPane.getUserData());//seedPane userData stores the tile index of the tile it was called from in Renderer.java
		Product selectedSeed = (Product)tableSeed.getUserData();//tableSeed userData stores the selected seed
		selectedSeed.setQty(selectedSeed.getQty()-0.5);//remove 0.5 kg when sowing TODO make it dependend on seed data
		plantWheat.setSow(selectedSeed.getName(),selectedSeed.getQual());
		plantWheat.startTask(game.getClock().getTotalSeconds(), getActiveEmployee());
		seedCancelButtonAction(new ActionEvent());
	}
	
	@FXML
	private void skipDayAction(){
		gameSpeedChoice.getSelectionModel().select(new Integer(1000));
	}
	
	
	@FXML 
	private void saveMenuAction(ActionEvent event){
		fileChooser.setTitle("Save game");
		File file = fileChooser.showSaveDialog(gameGridPane.getScene().getWindow());
		if(file!=null){
			fileChooser.setInitialDirectory(file.getParentFile());
			final RuntimeTypeAdapterFactory<Tile> typeFactory = RuntimeTypeAdapterFactory  
					.of(Tile.class,"type") 
					.registerSubtype(ForestTile.class) 
					.registerSubtype(FarmPlot.class)
					.registerSubtype(BuildingTile.class);

			// add the polymorphic specialization
			final Gson gson = FxGson.fullBuilder().registerTypeAdapterFactory(typeFactory).setPrettyPrinting().create();

			try {
				Writer writer = new FileWriter(file);
				gson.toJson(game,writer);
				writer.close();
				System.out.println("game saved");
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		}
	}

	@FXML 
	private void loadMenuAction(ActionEvent event){
		
		
		fileChooser.setTitle("Open save file");
		File file = fileChooser.showOpenDialog(gameGridPane.getScene().getWindow());
		
		if(file!=null){
			fileChooser.setInitialDirectory(file.getParentFile());
			final RuntimeTypeAdapterFactory<Tile> typeFactory = RuntimeTypeAdapterFactory  
					.of(Tile.class, "type") 
					.registerSubtype(ForestTile.class) 
					.registerSubtype(FarmPlot.class)
					.registerSubtype(BuildingTile.class);

			// add the polymorphic specialization
			final Gson gson = FxGson.fullBuilder().registerTypeAdapterFactory(typeFactory).create();

			try {

				game=gson.fromJson(new JsonReader(new FileReader(file)), Game.class);
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (JsonSyntaxException e) {	
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			game.getWxForcast().setGameClock(game.getClock()); // reinitialise the gameclock in wxforcast object after a json game load
			initialize();	
		}
	}

	@FXML
	public void pauseButtonAction() {
		
		if(pauseButton.isSelected()){
			manPaused=true;
		}else{
			manPaused=false;
		}
		updatePauseButton();
	}

	
	public void updatePauseButton(){
		
		if(manPaused||otherPaused||Renderer.popupShown){
			pauseButton.setSelected(true);
		}else{
			pauseButton.setSelected(false);
		}
		
		pauseButton.setBackground(Background.EMPTY);
		if (pauseButton.isSelected()) {
			pauseLabel.setText("Jeu en pause");
			pauseLabel.setTextFill(Color.RED);
			pauseLabel.setFont(new Font("Arial Bold", 12));
			pauseButton.setGraphic(new ImageView(new Image("Button-Play.png", 32, 32, true, true)));
		} else {
			pauseLabel.setText("");
			pauseButton.setGraphic(new ImageView(new Image("Button-Pause.png", 32, 32, true, true)));
		}
	}

	@FXML
	private void closeShopButtonAction(ActionEvent event) {
		if(!openWSbutton.isSelected()) {
			otherPaused=false;
			pauseButton.setDisable(false);
		}else {
			updateWSPanel();
		}
		shopPane.toBack();
		openShopButton.setSelected(false);
		updatePauseButton();

	}

	@FXML
	private void openShopButtonAction(ActionEvent event) {
		if(openShopButton.isSelected()){
			updateShopPanel();
			shopPane.toFront();
			otherPaused=true;
			pauseButton.setDisable(true);
		}else{
			if(!openWSbutton.isSelected()) {
				otherPaused=false;
				pauseButton.setDisable(false);
			}
			shopPane.toBack();
		}
		updatePauseButton();
	}

	@FXML
	private void closeWSbuttonAction(ActionEvent event) {
		if(!openShopButton.isSelected()) {
			otherPaused=false;
			pauseButton.setDisable(false);
		}else {
			updateShopPanel();
		}
		workShopPane.toBack();
		openWSbutton.setSelected(false);
		updatePauseButton();
	}
	
	@FXML 
	private void openWSbuttonAction(ActionEvent event) {
		if(openWSbutton.isSelected()){
			updateWSPanel();
			workShopPane.toFront();
			otherPaused=true;
			pauseButton.setDisable(true);
		}else{		
			if(!openShopButton.isSelected()) {
				otherPaused=false;
				pauseButton.setDisable(false);
			}
			workShopPane.toBack();
		}
		updatePauseButton();
	}
	@FXML
	private void buyButtonAction(ActionEvent event){
		game.getInventory().addMoney(-game.getShop().totalPrice(game.getShop().getDataBuying()));
		//game.getInventory().addMoney(-Double.valueOf(buyTotalLabel.getText()));
		for(Product prod:tableBuy.getItems() ){
			game.getInventory().addProd(new Product(prod));
			prod.setQty(-prod.getQty());
			game.getShop().addProd(new Product(prod));
		}
		tableBuy.getItems().clear();
		buyTotalLabel.setText(String.format("%.2f$", 0.00));
		
		updateShopPanel();
		updateWSPanel();
		unselectTreeTable(tableShop.getRoot());	
		leftTextArea.setText(game.getInventory().toString());
	}
	
	@FXML
	private void sellButtonAction(ActionEvent event){
		game.getInventory().addMoney(game.getShop().totalPrice(game.getShop().getDataSelling()));
		//game.getInventory().addMoney(Double.valueOf(sellTotalLabel.getText()));
		for(Product prod:tableSell.getItems() ){
			game.getShop().addProd(new Product(prod));
			prod.setQty(-prod.getQty());
			game.getInventory().addProd(new Product(prod));
		}
		tableSell.getItems().clear();
		sellTotalLabel.setText(String.format("%.2f$", 0.00));
		updateShopPanel();
		updateWSPanel();
		unselectTreeTable(tableInv.getRoot());
		leftTextArea.setText(game.getInventory().toString());
	}
	
	@FXML
	private void cancelTransactionButtonAction(ActionEvent event){
		tableBuy.getItems().clear();
		tableSell.getItems().clear();
		sellTotalLabel.setText(String.format("%.2f", 0.0));
		buyTotalLabel.setText(String.format("%.2f", 0.0));
		unselectTreeTable(tableInv.getRoot());
		unselectTreeTable(tableShop.getRoot());
	}

	

	/**
	 * creates the task for crafting a recipe in the workshop 
	 * 
	 * @param event
	 */
	@FXML
	private void actionWSbuttonAction(ActionEvent event) {
		game.getWorkShop().startTask(getActiveEmployee(),game.getInventory(),game.getClock().getTotalSeconds());
		taskProgress1.setProgress(getActiveEmployee().getTask().taskProgress(game.getClock().getTotalSeconds(), game.getInventory(),getActiveEmployee(), game.getTileList(), renderer.getPreviousMap()));
		taskName1.setText(getActiveEmployee().getTask().getName());
		updateShopPanel();
		updateWSPanel();
	}
	
	@FXML 
	private void cancelWSbuttonAction(ActionEvent event) {
		unselectTreeTable(tableInvWS.getRoot());
		updateWSResultLabel();
	}
	
	
	
	//SETTERS -- GETTERS
	public boolean getPaused() {
		return pauseButton.isSelected();
	}
	public GridPane getGameGridPane() {
		return gameGridPane;
	}
	public Label getClockLabel() {
		return clockLabel;
	}
	public ToggleButton getPauseButton() {
		return pauseButton;
	}
	public Label getPauseLabel() {
		return pauseLabel;
	}
	public VBox getMouseOverPanel() {
		return mouseOverPanel;
	}	
	public ChoiceBox<Integer> getGameSpeedChoice(){
		return gameSpeedChoice;
	}	
	public TextArea getLeftTextArea(){
		return leftTextArea;
	}
	public Game getGame() {
		return game;
	}
	public Renderer getRenderer() {
		return renderer;
	}
	public GameClock getGameClock() {
		return game.getClock();
	}
	/*
	public boolean getManPaused() {
		
		return manPaused;
	}
	*/
	public AnchorPane getSeedPane(){
		return seedPane;
	}
	public ToggleButton getOpenWSbutton(){
		return openWSbutton;
	}
	
	/**
	 * @return the active employee as selected on the employee choice box
	 */
	public Employee getActiveEmployee(){
		return employeeChoice.getSelectionModel().getSelectedItem();
	}
	
	
	/**
	 * setup the Shop panel for the first time
	 */
	private void setupShop(){
		
		shopFilterTextField.textProperty().addListener((obs, oldStr, newStr)->{
			updateShopPanel();
		});
		
		final PseudoClass topLevelTTVPseudoClass = PseudoClass.getPseudoClass("top-level-treetableview");
		
		//------------inventory table------------- 
		TreeItem<Product> rootInv = new TreeItem<>(new Product(null,"empty", 0, 0, 0));
		tableInv.setPlaceholder(new Text("Empty Inventory"));
		tableInv.setRoot(rootInv);
		tableInv.setShowRoot(false);
		tableInv.setEditable(true);
		colActInv.setEditable(true);
		
		//populate the inventory treetableview with the inventory data
		
		TreeItem<Product> rootShop = new TreeItem<>(new Product(null,"empty", 0, 0, 0));
		tableShop.setRoot(rootShop);
		updateShopPanel();
		
		//Setup the cell values
		colNameInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtyInv.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().qtyProperty()));
		colSpoilQtyInv.setCellValueFactory(cellData ->Bindings.format("-%.3f", cellData.getValue().getValue().spoilQtyProperty()));
		colSpoilQtyInv.setStyle("-fx-text-fill: red");
		colFreshInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colPriceInv.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().priceProperty()));
		colActInv.setCellFactory(column ->{ 
			return new CheckBoxTreeTableCell<Product, Boolean>(){
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					boolean isTopLevel = getTreeTableView().getRoot().getChildren().contains(getTreeTableRow().getTreeItem());
					if(item == null || empty){
						setText(null);
						setGraphic(null);
						getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, false);
					
					}else{
						getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, isTopLevel);
						setEditable(!isTopLevel);	
					}
				}
			};
		});
		colActInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));
			     
		//---------------sell table------------------
		tableSell.setItems(game.getShop().getDataSelling());
		tableSell.setEditable(true);
		colActSell.setEditable(true);
	
		
		colNameSell.setCellValueFactory(new PropertyValueFactory<>("name"));
		colQtySell.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().qtyProperty()));
		colQtySell.setCellFactory(TextFieldTableCell.forTableColumn());
		colQtySell.setOnEditCommit(transactionQtyEditCommit(game.getInventory()));
		colFreshSell.setCellValueFactory(new PropertyValueFactory<>("fresh"));
		colQualSell.setCellValueFactory(new PropertyValueFactory<>("qual"));
		colPriceSell.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().priceProperty()));
		colActSell.setCellFactory(CheckBoxTableCell.forTableColumn(colActSell));
		colActSell.setCellValueFactory(new PropertyValueFactory<>("selected"));
		
		//-------------shop table---------------
		
		tableShop.setPlaceholder(new Text("Empty Shop"));
		
		tableShop.setShowRoot(false);
		tableShop.setEditable(true);
		
		//Setup the cell values
		colNameShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtyShop.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().qtyProperty()));
		colSpoilQtyShop.setCellValueFactory(cellData ->Bindings.format("-%.3f", cellData.getValue().getValue().spoilQtyProperty()));
		colSpoilQtyShop.setStyle("-fx-text-fill: red");
		colFreshShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colPriceShop.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().priceProperty()));
		colActShop.setCellFactory(column ->{
			return new CheckBoxTreeTableCell<Product, Boolean>(){
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					boolean isTopLevel = getTreeTableView().getRoot().getChildren().contains(getTreeTableRow().getTreeItem());
					if(item == null || empty){
						setText(null);
						setGraphic(null);
						getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, false);
					}else{
					
						getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, isTopLevel);
						setEditable(!isTopLevel);	
					}
				}
			};
		});
		
		
		//setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(colActShop));
		colActShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));



		//-------------buy table-----------------
		tableBuy.setItems(game.getShop().getDataBuying());
		tableBuy.setEditable(true);
		colActBuy.setEditable(true);


		colNameBuy.setCellValueFactory(new PropertyValueFactory<>("name"));
		colQtyBuy.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().qtyProperty()));
		colQtyBuy.setCellFactory(TextFieldTableCell.forTableColumn());
		colQtyBuy.setOnEditCommit(transactionQtyEditCommit(game.getShop()));
		colFreshBuy.setCellValueFactory(new PropertyValueFactory<>("fresh"));
		colQualBuy.setCellValueFactory(new PropertyValueFactory<>("qual"));
		colPriceBuy.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().priceProperty()));
		colActBuy.setCellFactory(CheckBoxTableCell.forTableColumn(colActBuy));
		colActBuy.setCellValueFactory(new PropertyValueFactory<>("selected"));
		colQtyBuy.setEditable(true);

		
	}
	
	/**
	 * setup the workShop panel for the first time
	 */
	private void setupWS(){
		wsFilterTextField.textProperty().addListener((obs, oldStr, newStr)->{
			updateWSPanel();
		});
		
		final PseudoClass topLevelTTVPseudoClass = PseudoClass.getPseudoClass("top-level-treetableview");
		
		//------------ WS inventory table------------- //
		TreeItem<Product> rootInvWS = new TreeItem<>(new Product(null,"empty", 0, 0, 0));
		tableInvWS.setPlaceholder(new Text("Empty Inventory"));
		tableInvWS.setRoot(rootInvWS);
		tableInvWS.setShowRoot(false);
		tableInvWS.setEditable(true);
		colActInvWS.setEditable(true);
		
		//populate the inventory treetableview with the inventory data on the workShop Panel
		
		updateWSPanel();
		
		//Setup the cell values
		colNameInvWS.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtyInvWS.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().qtyProperty()));
		colFreshInvWS.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualInvWS.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colPriceInvWS.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().priceProperty()));
		colActInvWS.setCellFactory(column ->{ 
			return new CheckBoxTreeTableCell<Product, Boolean>(){
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					boolean isTopLevel = getTreeTableView().getRoot().getChildren().contains(getTreeTableRow().getTreeItem());
					if(item == null || empty){
						setText(null);
						setGraphic(null);
						getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, false);
					
					}else{
						getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, isTopLevel);
						setEditable(!isTopLevel);	
					}
				}
			};
		});
		colActInvWS.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));
			     
		//---------------selected Ingredient table------------------
		tableSelectedIngrWS.setItems(game.getWorkShop().getSelectedIngr());
		tableSelectedIngrWS.setEditable(true);
		colActIngrWS.setEditable(true);
	
		
		colNameIngrWS.setCellValueFactory(new PropertyValueFactory<>("name"));
		colQtyIngrWS.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().qtyProperty()));
		colQtyIngrWS.setCellFactory(TextFieldTableCell.forTableColumn());
		colQtyIngrWS.setOnEditCommit(transactionQtyEditCommit(game.getWorkShop()));
		colFreshIngrWS.setCellValueFactory(new PropertyValueFactory<>("fresh"));
		colQualIngrWS.setCellValueFactory(new PropertyValueFactory<>("qual"));
		colPriceIngrWS.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().priceProperty()));
		colActIngrWS.setCellFactory(CheckBoxTableCell.forTableColumn(colActIngrWS));
		colActIngrWS.setCellValueFactory(new PropertyValueFactory<>("selected"));
		
		game.getWorkShop().getSelectedIngr().addListener(new ListChangeListener<Product>() {

			@Override
			public void onChanged(Change<? extends Product> c) {			
					updateWSResultLabel();

			}
		});
		
		//-----------------setup recipe listview--------------------
		listViewRecipe.setCellFactory(new Callback<ListView<Recipe>, ListCell<Recipe>>() {
			@Override //overrides the Listcell to display the Recipe in a custom way
			public ListCell<Recipe> call(ListView<Recipe> param) {
				return new ListCell<Recipe>() {
					@Override
			        public void updateItem(Recipe item, boolean empty) {
			            super.updateItem(item, empty);
			            if (item == null) {
			            	setText(null);
			            }else {
			            	setText(item.getName()+" "+item.getIngredients().keySet().toString());
			            }
			        }
				};
			}
		});
		
		labelSelectedRecipe.setText(" Select a recipe from the list ");
		listViewRecipeDetails.getItems().setAll("Details...");
		listViewRecipe.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<Recipe>() {
					@Override // populate the recipe details label and table in the workshop panel with the selected item
					public void changed(ObservableValue<? extends Recipe> observable, Recipe oldValue, Recipe newValue) {
						
						if(newValue!=null) {
							Entry<String,Double> firstEntry = newValue.getResults().firstEntry();
							labelSelectedRecipe.setText(firstEntry.getKey() + ", " + firstEntry.getValue() + " kg");
							ObservableList<String> recipeDetailList = FXCollections.observableArrayList();
							for(Entry<String,Double> entry: newValue.getIngredients().entrySet()) {
								recipeDetailList.add(entry.getKey() + ", " + entry.getValue() + " kg");
							}
							listViewRecipeDetails.getItems().setAll(recipeDetailList);
							
							
						}else{
							labelSelectedRecipe.setText(" Select a recipe from the list ");
							listViewRecipeDetails.getItems().setAll("Details...");
						}
						updateWSResultLabel();
					}
				});


	}
	
	private void setupSeedPane(){
		seedCatChoiceBox.getItems().setAll("All seeds","Vegetable seed","Cereal seed");
		seedCatChoiceBox.getSelectionModel().select(0);
		
		seedFilterTextField.textProperty().addListener((obs, oldStr, newStr)->{
			updateSeedPanel();
		});
		
		seedCatChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldStr, newStr)->{
			updateSeedPanel();
			//System.out.println(newStr);
		});
		
		
		final PseudoClass topLevelTTVPseudoClass = PseudoClass.getPseudoClass("top-level-treetableview");
		
		//------------ Seed inventory table------------- //
		TreeItem<Product> rootSeed = new TreeItem<>(new Product(null,"empty", 0, 0, 0));
		tableSeed.setPlaceholder(new Text("Empty Inventory"));
		tableSeed.setRoot(rootSeed);
		tableSeed.setShowRoot(false);
		tableSeed.setEditable(true);
		colActSeed.setEditable(true);
		
		//populate the seed panel with the inventory data filtered for seeds
		
		updateSeedPanel();
		
		//Setup the cell values
		colNameSeed.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtySeed.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().qtyProperty()));
		colFreshSeed.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualSeed.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colPriceSeed.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().priceProperty()));
		colActSeed.setCellFactory(column ->{ 
			return new CheckBoxTreeTableCell<Product, Boolean>(){
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					boolean isTopLevel = getTreeTableView().getRoot().getChildren().contains(getTreeTableRow().getTreeItem());
					if(item == null || empty){
						setText(null);
						setGraphic(null);
						getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, false);
					
					}else{
						getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, isTopLevel);
						setEditable(!isTopLevel);	
					}
				}
			};
		});
		colActSeed.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));
		
	}
	
	/**
	 * updates the seed panel after it has been set up to show changed data
	 */
	public void updateSeedPanel(){
		ArrayList<String> catFilter = new ArrayList<>();
		catFilter.add(seedCatChoiceBox.getSelectionModel().getSelectedItem());
		catFilter.add("Only seeds");	
		updateTreeItemRoot(tableSeed.getRoot(), game.getInventory(), game.getShop(),seedFilterTextField.getText(),catFilter);
		listenForSelectionSeedPane(tableSeed.getRoot());
		
	}
	
	/**
	 * update the result label to reflect the selected recipe and selected ingredients
	 * @return the result (or anticipated result) of the craft
	 */
	public void updateWSResultLabel(){
		Product result = game.getWorkShop().getResult();
		Employee empl = getActiveEmployee();
		if(listViewRecipe.getSelectionModel().getSelectedItem()!=null) {
			game.getWorkShop().calculateResult(wsChoiceBox.getSelectionModel().getSelectedItem(), listViewRecipe.getSelectionModel().getSelectedItem());
			result=game.getWorkShop().getResult();
			result.updatePrice(game.getShop());			
		}
		if(result.getQty()==0 || empl.isWorking()|| empl.getEnergy()<empl.getTask().getEnergyCost()) {
			actionWSbutton.setDisable(true);
		}else {
			actionWSbutton.setDisable(false);
		}
		labelResultWS.setText(result.toString()); 
	}

	/**
	 * updates the workShop panel after it has been set up to show changed data
	 */
	private void updateWSPanel() {
		WorkShop ws = game.getWorkShop();
		ws.copyInventory(game.getInventory());
		ws.getSelectedIngr().clear();
		
		updateTreeItemRoot(tableInvWS.getRoot(), ws, game.getShop(),wsFilterTextField.getText(),null);
		listenForSelection(tableInvWS.getRoot(), ws.getSelectedIngr());
		
		
		listViewRecipe.getItems().setAll(ws.getRecipeList(wsChoiceBox.getSelectionModel().getSelectedItem()).values());
		
		updateWSResultLabel();
		
	}
	
	/**
	 * updates the Shop panel after it has been set up to show changed data
	 */
	private void updateShopPanel(){
		updateTreeItemRoot(tableInv.getRoot(), game.getInventory(), game.getShop(),shopFilterTextField.getText(),null);
		updateTreeItemRoot(tableShop.getRoot(), game.getShop(), game.getShop(),shopFilterTextField.getText(),null);
		listenForSelection(tableInv.getRoot(), game.getShop().getDataSelling());
		listenForSelection(tableShop.getRoot(), game.getShop().getDataBuying());
		
	}
	
	 /**
	  * A method to update a TreeTableView every time backing data changes in the model,
	  * without resetting the table by only adding and removing data when required.
	  * @param root : the root TreeItem of the table to update
	  * @param inventory : the inventory backing this table
	  * @param shop : the shop to calculate the price of products
	  */
	private void updateTreeItemRoot(TreeItem<Product> root, Inventory inventory, Shop shop, String filter, ArrayList<String> catFilter){
		inventory.clean();
		shop.clean();
		inventory.calculateAverageData();
		
		//retrieve the product list of category items of the previous TreeTableView structure before updating
		ArrayList<Product> oldProdCategoryList= new ArrayList<Product>();
		ArrayList<TreeItem<Product>> oldTreeItemCategoryList= new ArrayList<>(root.getChildren());
		for(TreeItem<Product> treeItem : oldTreeItemCategoryList){
			oldProdCategoryList.add(treeItem.getValue());
		}
		
		for(Entry<String, ArrayList<Product>> entry : inventory.getData().entrySet()){
			
			Product product = inventory.getAverageData(entry.getKey());
			
			//Filtering the TreeTableView 
			boolean filterOK = false;
			
			if(catFilter!=null){
				if(catFilter.contains("All")||catFilter.contains("All seeds")){
					filterOK = true;
					//System.out.println(catFilter +" "+ filterOK);
				}else{
					//else look for any matching categories
					for(String str:catFilter){
						if(product.getCategories().stream().anyMatch(s -> s.equals(str))){
							filterOK = true;
							break;
						}
					}
				}

				//turn filterOK to false if we want only seeds and the product is not a seed
				if(catFilter.contains("Only seeds")){
					if(!(product.getCategories().stream().anyMatch(s -> s.equals("Seed")))){
						filterOK = false;
						//System.out.println(" " +catFilter +" Product Name "+ product.getName()+" product.getCategories() "+product.getCategories() + filterOK);
					}
				}
			}else{
				filterOK = true;
			}
			
			if(filter.length()>0&&!product.getName().matches( "(?i:.*"+filter+".*)")){
				filterOK = false;
				//System.out.println(filterOK + " product " + product.getName() + " categories " + product.getCategories().get(0));
			}

			if(filterOK){

			
				product.updatePrice(shop);
				TreeItem<Product> averageProdDataItem; ;

				//if this product category is not yet present in the table add it.
				if(!oldProdCategoryList.contains(product)){
					averageProdDataItem = new TreeItem<Product>(product);
					averageProdDataItem.setExpanded(true);
					root.getChildren().add(averageProdDataItem);
				}else{
					//else if this product category is already in the table, retrieve the reference and remove it from the 
					//	"old" Lists to see if something needs to be removed from the table in the end
					averageProdDataItem = oldTreeItemCategoryList.get(oldProdCategoryList.indexOf(product));
					oldTreeItemCategoryList.remove(oldProdCategoryList.indexOf(product));
					oldProdCategoryList.remove(product);
				}

				//retrieve the product list for this category category of the previous TreeTableView structure before updating
				ArrayList<Product> oldProdList= new ArrayList<Product>();
				ArrayList<TreeItem<Product>> oldTreeItemList= new ArrayList<>(averageProdDataItem.getChildren());
				for(TreeItem<Product> treeItem : oldTreeItemList){
					oldProdList.add(treeItem.getValue());
				}

				for(Product prod : entry.getValue()){
					prod.updatePrice(shop);
					TreeItem<Product> treeItem;

					//if the table does not contain this prod, add it
					if(!oldProdList.contains(prod)){
						treeItem = new TreeItem<Product>(prod);
						averageProdDataItem.getChildren().add(treeItem);
					}else{

						oldTreeItemList.remove(oldProdList.indexOf(prod));

						oldProdList.remove(prod);
					}
				}
				averageProdDataItem.getChildren().removeAll(oldTreeItemList); //clean up empty products
			}// if filterOK
		}//for
		root.getChildren().removeAll(oldTreeItemCategoryList);// clean up empty categories


	}


	/**
	 * Adds the appropriate changelisteners to products in the inventory treetableview (only for the leafs)
	 * and populates the transaction table with copies of the selected products
	 * @param rootTreeItem : the inventory root(top level) TreeItem
	 * @param list : the transaction table list
	 */
	private void listenForSelection(TreeItem<Product> rootTreeItem, ObservableList<Product> list) {
		Product copy = new Product(rootTreeItem.getValue());
		copy.setSelListener((obs, oldVal, newVal) ->{
			if(!newVal){
				rootTreeItem.getValue().setSelected(false);
			}
		});
		if(rootTreeItem.isLeaf()){
			rootTreeItem.getValue().setSelListener((obs, oldVal, newVal) -> {
				if(newVal){
					copy.setSelected(true);
					list.add(copy);
				}else{
					list.remove(copy);
					copy.setQty(rootTreeItem.getValue().getQty());
				}
				updateBuySellLabels();
			});
		}
		rootTreeItem.getChildren().forEach(item -> listenForSelection(item,list));
	}
	/**
	 * Adds the appropriate changelisteners to Seeds in the tableSeed treetableview (only for the leafs)
	 * and adds the details to the seedDetailTextFlow
	 * @param rootTreeItem : the tableSeed root(top level) TreeItem
	 */
	private void listenForSelectionSeedPane(TreeItem<Product> rootTreeItem) {
		if(rootTreeItem.isLeaf()){
			rootTreeItem.getValue().setSelListener((obs, oldVal, newVal) -> {
				if(newVal){
					seedDetailTextFlow.getChildren().setAll(new Text(rootTreeItem.getValue().toString()));
					tableSeed.setUserData(rootTreeItem.getValue());//store the selected product in the treetableview userdata
					unselectTreeTableButOne(tableSeed.getRoot(),rootTreeItem);
				}else{
					seedDetailTextFlow.getChildren().setAll(new Text("Choisissez une semence"));
				}
				
			});
		}
		rootTreeItem.getChildren().forEach(item -> listenForSelectionSeedPane(item));
	}
	
	/**
	 * 
	 * @param rootTreeItem : the root node of the treeItem we want to unselect all products
	 * for example when we clear the transaction tables
	 */
	private void unselectTreeTable(TreeItem<Product> rootTreeItem){
		rootTreeItem.getValue().setSelected(false);
		rootTreeItem.getChildren().forEach(item->unselectTreeTable(item));
	}
	
	/**
	 * Unselect all but one of a treeTableView, usefull when we want to force just one selected
	 * @param rootTreeItem : the root node of the treeItem we want to unselect all products but one
	 * @param one : the one treeItem that is selected on purpose
	 */
	private void unselectTreeTableButOne(TreeItem<Product> rootTreeItem, TreeItem<Product> one){
		if(!rootTreeItem.equals(one)){
			rootTreeItem.getValue().setSelected(false);
		}
		rootTreeItem.getChildren().forEach(item->unselectTreeTableButOne(item,one));
		seedDetailTextFlow.getChildren().setAll(new Text(one.getValue().toString()));
	}
	
	/**
	 * EventHandler to handle and validate editing of quantity the Qty column in a table
	 * @param inventory : the inventory to check against for max values during edit of the cell
	 * @return the eventHandler
	 */
	private EventHandler<TableColumn.CellEditEvent<Product,String>> transactionQtyEditCommit(Inventory inventory){
		return event->{
			int row = event.getTablePosition().getRow();
			Product prod = event.getTableView().getItems().get(row);
			double newValue = Double.valueOf(event.getNewValue());
			if(newValue<0){
				prod.setQty(0);
			}else if(newValue >= inventory.getMaxQty(prod)){
				prod.setQty(inventory.getMaxQty(prod));
			}else{	
				prod.setQty(newValue);
			}
			event.getTableView().refresh();
			
			updateBuySellLabels();
			
			updateWSResultLabel();
		};
	}
	/**
	 * update the transaction labels and buy button disable if too expensive
	 */
	private void updateBuySellLabels(){
		double buyPrice = game.getShop().totalPrice(game.getShop().getDataBuying());
		sellTotalLabel.setText(String.format("%.2f$", game.getShop().totalPrice(game.getShop().getDataSelling())));
		buyTotalLabel.setText(String.format("%.2f$", buyPrice));
		
		//check if enough storage to enable harvest
		
		boolean enoughSilo = game.getInventory().enoughStorageFor(game.getShop().getBuyingStorageRequired(true), true);
		boolean enoughOtherStorage = game.getInventory().enoughStorageFor(game.getShop().getBuyingStorageRequired(false), false);
		buyButton.setDisable(buyPrice>game.getInventory().getMoney()||!enoughSilo||!enoughOtherStorage);
	}
	
	public static <T> void preventColumnReordering(TableView<T> tableView) {
	    Platform.runLater(() -> {
	        for (Node header : tableView.lookupAll(".column-header")) {
	            header.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
	        }
	    });
	}
	
	public static <T> void preventColumnReordering(TreeTableView<T> tableView) {
	    Platform.runLater(() -> {
	        for (Node header : tableView.lookupAll(".column-header")) {
	            header.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
	        }
	    });
	}
}

   
    