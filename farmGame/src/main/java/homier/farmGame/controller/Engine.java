package homier.farmGame.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import homier.farmGame.utils.TextFlowManager;
import homier.farmGame.utils.Tools;
import homier.farmGame.view.Renderer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
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
	@FXML private Button actionWSbutton, buyButton,sellButton, seedOKButton;//closeShopButton, cancelTransactionButton, ;
	@FXML private ToggleButton pauseButton, openShopButton, openWSbutton; //FIXME shop, workshop, game grid showing mecanic
	@FXML private VBox mouseOverPanel;
	@FXML private TextArea leftTextArea,mouseOverSeedDetails;
	@FXML private ChoiceBox<Integer> gameSpeedChoice;
	@FXML private Label clockLabel, pauseLabel ,wxToday, wxTomorrow, energyLabel, taskName1, buyTotalLabel, sellTotalLabel, labelSelectedRecipe, labelResultWS,
						craftEnergyLabel,craftTimeLabel;
	@FXML private ChoiceBox<Employee> employeeChoice;
	@FXML private ChoiceBox<String> wsChoiceBox,seedCatChoiceBox ;
	@FXML private ProgressIndicator taskProgress1;
	@FXML private AnchorPane shopPane, workShopPane, seedPane, mouseBlockingPane;
	@FXML private TreeTableView<Product> tableInv, tableShop, tableInvWS, tableSeed;
	@FXML private TreeTableColumn<Product, String> colNameInv, colNameShop,colNameInvWS,colNameSeed, colQtyInv, colQtyShop, colQtyInvWS,colQtySeed, 
												   colPriceInv, colPriceShop, colPriceInvWS,colPriceSeed, colSpoilQtyInv, colSpoilQtyInvWS,colSpoilQtySeed;
	@FXML private TreeTableColumn<Product, Number> colMaturityInv, colFreshInv, colFreshShop, colFreshInvWS,colFreshSeed, colQualInv, colQualShop,colQualInvWS,colQualSeed;
	@FXML private TreeTableColumn<Product, Boolean> colActInv, colActShop, colActInvWS,colActSeed;
	@FXML private TableView<Product>  tableSell, tableBuy, tableSelectedIngrWS;
	@FXML private TableColumn<Product, String> colNameSell, colNameBuy, colNameIngrWS, colQtySell, colQtyBuy, colQtyIngrWS, colPriceSell, colPriceBuy, colPriceIngrWS;
	@FXML private TableColumn<Product, Number>  colFreshSell, colFreshBuy, colFreshIngrWS,  
												colQualSell, colQualBuy, colQualIngrWS;				
	@FXML private TableColumn<Product, Boolean> colActSell, colActBuy, colActIngrWS;
	@FXML private ListView<String>  listViewRecipeDetails;
	@FXML private ListView<Recipe> listViewRecipe;
//	@FXML private MenuItem save,load;
	@FXML private TextFlow seedDetailTextFlow, gameInfoTextFlow;
	@FXML private StackPane leftStackPane;
	@FXML private TextField shopFilterTextField,wsFilterTextField,seedFilterTextField;
	
	private Game game;
	private Renderer renderer;
	
	public static boolean manPaused = false;
	public static boolean manUnpaused = false; //used to unpause when we want to play while employee is not working
	public static boolean noTaskPaused = false; //used to pause when no task
	public static boolean otherPaused = false;
	public static BooleanProperty paused = new SimpleBooleanProperty(false);
	
	FileChooser fileChooser = new FileChooser();
	
	
	/**
	 * TESTING
	 */
	
	double lastFilterRun = System.currentTimeMillis()+4000;
	boolean filterSet = false;
	int counter = 0;
	int updateItemCounter=0;
	String[] filterList = new String[] {"q","w","e","r","t","y","u","i","o","p","a","s","d","f","g",
			"h","j","k","l","z","x","c","v","b","n","m","q","w","e","r","t","y","u","i","o","p","a","s","d"};
	String[] filterList2 = new String[] {"a","a","a","r","r","r","r","i","o","p","a","s","d","f","g",
			"h","j","k","l","z","x","c","v","b","n","m","q","w","e","r","t","y","u","i","o","p","a","s","d"};
	//--------------------
	
	/**
	 * Update the UI, especially the mouseover info even when the game is paused
	 */
	public void updateUI(){
		
		/**
		 * TESTING
		 */
		if(System.currentTimeMillis()-lastFilterRun>250&&!filterSet) {
			filterSet=true;
			//shopFilterTextField.setText(filterList[counter]);
			//shopFilterTextField.setText("r");
			counter++;
			//System.out.println(counter + " " + shopFilterTextField.getText());
			
		}
		if(System.currentTimeMillis()-lastFilterRun>500) {
			lastFilterRun=System.currentTimeMillis();
			filterSet=false;
			//shopFilterTextField.clear();
			
		}
		//--------------------------------------------------------------
		
		
		
		//Pause game when in the seed selection pane
		if(leftStackPane.getChildren().indexOf(seedPane)==leftStackPane.getChildren().size()-1){
			seedPaneUIinteraction(true);
		}

		TextFlowManager.update();
		//Pause if employee is not working TODO make it work for multiple employee
		if(!getActiveEmployee().isWorking()) {
			noTaskPaused=!manUnpaused;
		}else{
			noTaskPaused = false;
		}
		updatePause();
	}

	/** 
	 * @param dTime: elapsed time since last update in seconds (multiply dTime from the main game loop to increase gameSpeed)
	 */
	public void update(double dTime) {
		
		game.getClock().update(dTime);
		clockLabel.setText(game.getClock().toString());
		
		//if it is a new day, update the forecast
		if(game.getClock().isNewDay()){
			if(game.getClock().getDay()==1){//first day of the month popup alert
				otherPaused=true;
				game.getInventory().addMoney(-100);
				Alert newMonthAlert = new Alert(AlertType.INFORMATION);
				newMonthAlert.setTitle("New month!");
				newMonthAlert.setHeaderText("This is the first day of the month and some bills have been payed\nThis is a summary:");
				newMonthAlert.setContentText("Electricity charges:" + 100 + "$");
				newMonthAlert.setOnHidden(evt -> {otherPaused=false;updatePause();});
				newMonthAlert.show();
			}
			
			manUnpaused=false;//reset the pause on no task behavior every new day
			gameSpeedChoice.getSelectionModel().select(new  Integer(20));//reset the gameSpeed
			game.getEmployees()[0].energyProperty().set(1000);
			game.getWxForcast().forcastNewDay();
			wxToday.setText("Today: "+game.getWxForcast().getToday().toString());
			wxTomorrow.setText("Tomorrow: "+game.getWxForcast().getTomorrow().toString());
			game.getClock().setIsNewDay(false);
			//update the inventory, especially for spoiling every day
			game.getInventory().update();
			game.getShop().update();
			updateShopPanel(true,true);
			updateWSPanel();
			
		}
		
		
		
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
		leftTextArea.setText(game.getInventory().toString()+"\n\n"+game.getShop().transactionsToString());
	}

	public void initialize() {
		//TESTING
		
		shopPane.toFront();
		
		
		//--------------------------
		
		
		if (game==null){
			game = new Game();//create a new game if not loaded save game
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Documents/FarmGame/saves"));
			//fileChooser.setInitialDirectory(new File("C:/Users/Ram/Documents/FarmGame/saves"));
			game.getClock().addTime(4*FarmTimeUnits.MONTH.seconds);
		}
		
		gameGridPane.getChildren().clear();
		renderer = new Renderer(game.getTileList(), gameGridPane);
		render();
		
		
		
		wxToday.setText("Today: "+game.getWxForcast().getToday().toString());
		wxTomorrow.setText("Tomorrow: "+game.getWxForcast().getTomorrow().toString());
		game.getClock().setIsNewDay(false);
		clockLabel.setText(game.getClock().toString());
		
		leftTextArea.setText(game.getInventory().toString()+"\n\n"+game.getShop().transactionsToString());

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
		
		new TextFlowManager();//instanciate the TextFlowManager, it will be called statically from various places
		
		//initialize the pause status
		paused.addListener(x->updatePauseButton());
		
		//setup clear button textField
//		Tools.setupClearButtonField(seedFilterTextField);
//		Tools.setupClearButtonField(shopFilterTextField);
//		Tools.setupClearButtonField(wsFilterTextField);
		
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
					break;
				case "bakery":
					availWS.add("Bakery");
					i = wsList.indexOf("Bakery");
					wsMaxLevelList[i]=Math.max(wsMaxLevelList[i], building.getLevel());
					break;
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
		getActiveEmployee().setTask(new FarmTask());
		seedPaneUIinteraction(false);
	}
	/**
	 * helper method to manage the interactivity of the UI while seedPane is up
	 * @param seedPaneUp true or false
	 */
	private void seedPaneUIinteraction(boolean seedPaneUp){
		if(seedPaneUp){
			mouseBlockingPane.toFront();
		}else{
			seedPane.toBack();
			mouseBlockingPane.toBack();
			unselectTreeTable(tableSeed.getRoot());
		}
		
		otherPaused=seedPaneUp;
		openShopButton.setDisable(seedPaneUp);
		openWSbutton.setDisable(seedPaneUp);
		pauseButton.setDisable(seedPaneUp);
		employeeChoice.setDisable(seedPaneUp);
		
	}
	@FXML 
	private void seedOKButtonAction(ActionEvent event){
		FarmTask plantSeed = getActiveEmployee().getTask();
		Product selectedSeed = (Product)tableSeed.getUserData();//tableSeed userData stores the selected seed
		selectedSeed.setQty(selectedSeed.getQty()-(selectedSeed.getName().equals("Potatoes")?1000:3));//remove 3 when planting or 1000 if potatoes
		plantSeed.setSow(selectedSeed.getName(),selectedSeed.getQual());
		plantSeed.startTask(game.getClock().getTotalSeconds(), getActiveEmployee());
		
		seedPaneUIinteraction(false);
		
	}
	
	@FXML
	private void skipDayAction(){
		gameSpeedChoice.getSelectionModel().select(new Integer(1000));
		manUnpaused=true;
	}
	
	@FXML
	private void addMoneyMenuAction(){
		game.getInventory().addMoney(1000);
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
			if(noTaskPaused) {
				manUnpaused=true;
			}
		}
	}

	public void updatePause() {
		if(manPaused||otherPaused||Renderer.popupShown||noTaskPaused){
			paused.set(true);
			pauseButton.setSelected(true);
		}else{
			paused.set(false);
			pauseButton.setSelected(false);
		}
	}
	
	public void updatePauseButton(){
		pauseButton.setBackground(Background.EMPTY);
		if (paused.get()) {
			pauseLabel.setText("-------Game paused-------");
			pauseLabel.setTextFill(Color.RED);
			pauseLabel.setFont(new Font("Arial Bold", 12));
			pauseButton.setGraphic(new ImageView(new Image("/icons/Button-Play.png", 32, 32, true, true)));
		} else {
			pauseLabel.setText("");
			pauseButton.setGraphic(new ImageView(new Image("/icons/Button-Pause.png", 32, 32, true, true)));
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
	}

	@FXML
	private void openShopButtonAction(ActionEvent event) {
		if(openShopButton.isSelected()){
			updateShopPanel(false,false);
			shopPane.toFront();
			otherPaused=true;
			pauseButton.setDisable(true);
		}else{
			if(openWSbutton.isSelected()) {
				updateWSPanel();
			}else {
				otherPaused=false;
				pauseButton.setDisable(false);
			}
			shopPane.toBack();
		}
	}

	@FXML
	private void closeWSbuttonAction(ActionEvent event) {
		if(!openShopButton.isSelected()) {
			otherPaused=false;
			pauseButton.setDisable(false);
		}else {
			updateShopPanel(false,false);
		}
		workShopPane.toBack();
		openWSbutton.setSelected(false);
	}
	
	@FXML 
	private void openWSbuttonAction(ActionEvent event) {
		if(openWSbutton.isSelected()){
			updateWSPanel();
			workShopPane.toFront();
			otherPaused=true;
			pauseButton.setDisable(true);
		}else{		
			if(openShopButton.isSelected()) {
				updateShopPanel(false, false);
			}else {
				otherPaused=false;
				pauseButton.setDisable(false);
			}
			workShopPane.toBack();
		}
	}
	
	@FXML
	private void buyButtonAction(ActionEvent event){
		Shop shop = game.getShop();
		game.getInventory().addMoney(-shop.totalPrice(shop.getDataBuying())*shop.getBuyingPricePenalty());
		for(Product prod:tableBuy.getItems() ){
			game.getInventory().addProd(new Product(prod));
			shop.addToDailyBuyCount(prod.getQty());
			prod.setQty(-prod.getQty());
			shop.addProd(new Product(prod));
		}
		tableBuy.getItems().clear();
		buyTotalLabel.setText(String.format("%.2f$", 0.00));
		updateShopPanel(false,true);
		updateWSPanel();
		leftTextArea.setText(game.getInventory().toString()+"\n\n"+game.getShop().transactionsToString());
	}
	
	@FXML
	private void sellButtonAction(ActionEvent event){
		Shop shop = game.getShop();
		game.getInventory().addMoney(shop.totalPrice(shop.getDataSelling())*shop.getSellingPricePenalty());
		
		for(Product prod:tableSell.getItems() ){
			shop.addProd(new Product(prod));
			shop.addToDailySellCount(prod.getQty());
			prod.setQty(-prod.getQty());
			game.getInventory().addProd(new Product(prod));
		}
		tableSell.getItems().clear();
		sellTotalLabel.setText(String.format("%.2f$", 0.00));
		updateShopPanel(true,false);
		updateWSPanel();
		leftTextArea.setText(game.getInventory().toString()+"\n\n"+game.getShop().transactionsToString());
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
		
		updateShopPanel(true,false);
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
	

	public TextArea getMouseOverSeedDetails() {
		return mouseOverSeedDetails;
	}
	
	public TextFlow getGameInfoTextFlow(){
		return gameInfoTextFlow;
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
			filterShopPanel();
		});
		
		
		//------------inventory table------------- 
		TreeItem<Product> rootInv = new TreeItem<>(new Product(null,"empty",0,0,0,0));
		tableInv.setPlaceholder(new Text("Empty Inventory"));
		tableInv.setRoot(rootInv);
		tableInv.setShowRoot(false);
		tableInv.setEditable(true);
		tableInv.getSortOrder().add(colNameInv);
		tableInv.setRowFactory(table-> {
			return new TreeTableRow<Product>(){
				@Override
				public void updateItem(Product pers, boolean empty) {
					super.updateItem(pers, empty);		
					boolean isTopLevel = table.getRoot().getChildren().contains(treeItemProperty().get());
					if(pers==null||empty) {
						setText(null);
						setGraphic(null);
						getStyleClass().remove("topLevelRow");
					}else {
						if(isTopLevel) {
							if(!getStyleClass().contains("topLevelRow")) {
								getStyleClass().add("topLevelRow");
							}
						}else {
							getStyleClass().remove("topLevelRow");
						}
					}
				}
			};
		});
		colActInv.setEditable(true);
		
		//populate the inventory treetableview with the inventory data
		
		TreeItem<Product> rootShop = new TreeItem<>(new Product(null,"empty",0,0,0,0));
		tableShop.setRoot(rootShop);
		
		
		//Setup the cell values
		colNameInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtyInv.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().qtyProperty()));
		colSpoilQtyInv.setCellValueFactory(cellData ->Bindings.format("-%.3f", cellData.getValue().getValue().spoilQtyProperty()));
		colSpoilQtyInv.setStyle("-fx-text-fill: darkred");
		colMaturityInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("maturity"));
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
					}else{
						if(isTopLevel){
							setGraphic(null);
						}
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
		tableShop.getSortOrder().add(colNameShop);
		tableShop.setShowRoot(false);
		tableShop.setEditable(true);
		tableShop.setRowFactory(table-> {
			return new TreeTableRow<Product>(){
				@Override
				public void updateItem(Product pers, boolean empty) {
					super.updateItem(pers, empty);
					
					boolean isTopLevel = table.getRoot().getChildren().contains(treeItemProperty().get());
					if(pers==null||empty) {
						setText(null);
						setGraphic(null);
						
						//System.out.println(getStyleClass());
	
						getStyleClass().remove("topLevelRow");
					}else {
						if(isTopLevel) {
							if(!getStyleClass().contains("topLevelRow")) {
								getStyleClass().add("topLevelRow");
							}
						}else {
							getStyleClass().remove("topLevelRow");
							
						}
					}


				}
			};
		});
		//Setup the cell values
		colNameShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtyShop.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().qtyProperty()));
		colFreshShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colPriceShop.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().priceProperty()));
		colActShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));
		colActShop.setCellFactory(column ->{
			return new CheckBoxTreeTableCell<Product, Boolean>(){
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					boolean isTopLevel = getTreeTableView().getRoot().getChildren().contains(getTreeTableRow().getTreeItem());
					if(item == null || empty){
						setText(null);
						setGraphic(null);
					}else{
						if(isTopLevel){
							setGraphic(null);
						}
						setEditable(!isTopLevel);	
					}
				}
			};
		});
				
		updateShopPanel(false,false);

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
			filterWSPanel(game.getWorkShop());
		});
		
		
		
		//------------ WS inventory table------------- //
		TreeItem<Product> rootInvWS = new TreeItem<>(new Product(null,"empty",0,0,0,0));
		tableInvWS.setPlaceholder(new Text("Empty Inventory"));
		tableInvWS.setRoot(rootInvWS);
		tableInvWS.setShowRoot(false);
		tableInvWS.setEditable(true);
		tableInvWS.setRowFactory(table-> {
			return new TreeTableRow<Product>(){
				@Override
				public void updateItem(Product pers, boolean empty) {
					super.updateItem(pers, empty);
					boolean isTopLevel = table.getRoot().getChildren().contains(treeItemProperty().get());
					if(pers==null||empty) {
						setText(null);
						setGraphic(null);
						getStyleClass().remove("topLevelRow");
					}else {
						if(isTopLevel) {
							if(!getStyleClass().contains("topLevelRow")) {
								getStyleClass().add("topLevelRow");
							}
						}else {
							getStyleClass().remove("topLevelRow");
						}
					}
				}
			};
		});
		colActInvWS.setEditable(true);
		tableInvWS.getSortOrder().add(colNameInvWS);
		
		//populate the inventory treetableview with the inventory data on the workShop Panel
		
		updateWSPanel();
		
		//Setup the cell values
		colNameInvWS.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtyInvWS.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().qtyProperty()));
		colFreshInvWS.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualInvWS.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colSpoilQtyInvWS.setCellValueFactory(cellData ->Bindings.format("-%.3f", cellData.getValue().getValue().spoilQtyProperty()));
		colSpoilQtyInvWS.setStyle("-fx-text-fill: darkred");
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
					}else{
						if(isTopLevel){
							setGraphic(null);
						}
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
							wsFilterTextField.clear();
							for(Entry<String,Double> entry: newValue.getIngredients().entrySet()) {
								recipeDetailList.add(entry.getKey() + ", " + entry.getValue() + " kg");
								wsFilterTextField.appendText(entry.getKey()+",");
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
		seedOKButton.setDisable(true);
		seedCatChoiceBox.getItems().setAll("All seeds","Vegetable seed","Cereal seed");
		seedCatChoiceBox.getSelectionModel().select(0);
		
		seedFilterTextField.textProperty().addListener((obs, oldStr, newStr)->{
			updateSeedPanel();
		});
		
		seedCatChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldStr, newStr)->{
			updateSeedPanel();
			//System.out.println(newStr);
		});
		
		
		
		//------------ Seed inventory table------------- //
		TreeItem<Product> rootSeed = new TreeItem<>(new Product(null,"empty",0,0,0,0));
		tableSeed.setPlaceholder(new Text("Empty Inventory"));
		tableSeed.setRoot(rootSeed);
		tableSeed.setShowRoot(false);
		tableSeed.setEditable(true);
		tableSeed.getSortOrder().add(colNameSeed);
		tableSeed.setRowFactory(table-> {
			return new TreeTableRow<Product>(){
				@Override
				public void updateItem(Product pers, boolean empty) {
					super.updateItem(pers, empty);
					
					boolean isTopLevel = table.getRoot().getChildren().contains(treeItemProperty().get());
					if(pers==null||empty) {
						setText(null);
						setGraphic(null);
						
						//System.out.println(getStyleClass());
	
						getStyleClass().remove("topLevelRow");
					}else {
						if(isTopLevel) {
							if(!getStyleClass().contains("topLevelRow")) {
								getStyleClass().add("topLevelRow");
							}
						}else {
							getStyleClass().remove("topLevelRow");
							
						}
					}


				}
			};
		});
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
					}else{
						if(isTopLevel){
							setGraphic(null);
						}
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
		tableSeed.sort();
		
	}
	
	/**
	 * update the result label and the energy and time labels to reflect the selected recipe and selected ingredients
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
		if(result.getQty()==0 || empl.isWorking()|| empl.getEnergy()<game.getWorkShop().getTask().getEnergyCost()) {
			actionWSbutton.setDisable(true);
		}else {
			actionWSbutton.setDisable(false);
		}
		labelResultWS.setText(result.toString());
		if(result.getQty()==0){
			craftEnergyLabel.setText(String.format("Energy cost: %.0f",0.0));
			craftTimeLabel.setText(String.format("Time cost: %d",0));
		}else{
			craftEnergyLabel.setText(String.format("Energy cost: %.0f",game.getWorkShop().getTask().getEnergyCost()));
			craftTimeLabel.setText(String.format("Time cost: %d",game.getWorkShop().getTask().getTimeCost()));
		}
		
	}

	
	/**
	 * updates the workShop panel after it has been set up to show changed data
	 */
	private void updateWSPanel() {
System.out.println("updateWS");
		WorkShop ws = game.getWorkShop();
		ws.copyInventory(game.getInventory());
		ws.getSelectedIngr().clear();
		filterWSPanel(ws);
		listenForSelection(tableInvWS.getRoot(), ws.getSelectedIngr());
		listViewRecipe.getItems().setAll(ws.getRecipeList(wsChoiceBox.getSelectionModel().getSelectedItem()).values());
		updateWSResultLabel();
		
	}
	/**
	 * filter and updates the treetableview of the workshop
	 * @param ws
	 */
	private void filterWSPanel(WorkShop ws) {
			ArrayList<String> catFilter = new ArrayList<>();
			//catFilter.add(wsCatChoiceBox.getSelectionModel().getSelectedItem());
			catFilter.add("All");
			catFilter.add("Only mature");
			updateTreeItemRoot(tableInvWS.getRoot(), ws, game.getShop(),wsFilterTextField.getText(),catFilter);
			tableInvWS.getSelectionModel().clearSelection();
			tableInvWS.sort();
	}

	/**
	 * updates the Shop panel after it has been set up to show changed data
	 * @param clearInvSelection : to clear the selection of the inventory table, after using products in the workshop for example
	 * @param clearShopSelection : to clear shop and inventory selection on a new day for example
	 */
	private void updateShopPanel(boolean clearInvSelection, boolean clearShopSelection){
		if(clearInvSelection)unselectTreeTable(tableInv.getRoot());
		if(clearShopSelection)unselectTreeTable(tableShop.getRoot());
		filterShopPanel();
		listenForSelection(tableInv.getRoot(), game.getShop().getDataSelling());
		listenForSelection(tableShop.getRoot(), game.getShop().getDataBuying());
		
	}
	
	/**
	 * filter and updates the treetableview of the shopPanel
	 */
	private void filterShopPanel() {
		updateTreeItemRoot(tableInv.getRoot(), game.getInventory(), game.getShop(),shopFilterTextField.getText(),null);
		updateTreeItemRoot(tableShop.getRoot(), game.getShop(), game.getShop(),shopFilterTextField.getText(),null);
		tableInv.getSelectionModel().clearSelection();//to prevent a bug when sorting if something is selected
		tableShop.getSelectionModel().clearSelection();
		tableInv.sort();
		tableShop.sort();
	}
	/**
	 * Update the root of a TreeTableView with the data of an inventory and applies filters
	 * @param root
	 * @param inventory
	 * @param shop
	 * @param filter
	 * @param catFilter
	 */
	private void updateTreeItemRoot(TreeItem<Product> root, Inventory inventory, Shop shop, String filter, ArrayList<String> catFilter){
		inventory.clean();
		shop.clean();
		inventory.calculateAverageData();
		TreeItem<Product> newRoot = new TreeItem<>();
		//get the expandedMap to restore the treetableview to the correct expanded state after updating
		inventory.setExpandedMap(root);
		HashMap<String,Boolean> expandedMap = inventory.getExpandedMap();
		
		for(Entry<String, ArrayList<Product>> entry : inventory.getData().entrySet()){
			Product product = inventory.getAverageData(entry.getKey());

			if(Tools.filterOK(product, filter, catFilter)){
				product.updatePrice(shop);
				TreeItem<Product> averageProdDataItem = new TreeItem<Product>(product);
				averageProdDataItem.setExpanded(expandedMap.get(product.getName())!=null?expandedMap.get(product.getName()):true);
				newRoot.getChildren().add(averageProdDataItem);

				for(Product prod : entry.getValue()){
					prod.updatePrice(shop);
					TreeItem<Product> treeItem = new TreeItem<Product>(prod);
					averageProdDataItem.getChildren().add(treeItem);
				}
				
			}// if filterOK
		}//for
		root.getChildren().setAll(newRoot.getChildren());
	}
	
	//FIXME bug when filtered out items are not getting their changelistener properly
	/**
	 * Adds the appropriate changelisteners to products in the inventory treetableview (only for the leafs)
	 * and populates the transaction table with copies of the selected products
	 * @param rootTreeItem : the inventory root(top level) TreeItem
	 * @param list : the transaction table list
	 */
	private void listenForSelection(TreeItem<Product> rootTreeItem, ObservableList<Product> list) {
		
		Product copy = new Product(rootTreeItem.getValue());
		
		copy.setSelListener((obs, oldVal, newVal) ->{
		//	System.out.println("change listener on copy " + copy.getName());
			if(!newVal){
				rootTreeItem.getValue().setSelected(false);
			}
		});
		if(rootTreeItem.isLeaf()&&!rootTreeItem.getValue().isSelected()){
			rootTreeItem.getValue().setSelListener((obs, oldVal, newVal) -> {
			//	System.out.println("change Listener on item " + rootTreeItem.getValue().getName());
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
					//Disable the seedOk button if not enough seeds
					if(rootTreeItem.getValue().getQty()>=(rootTreeItem.getValue().getName().equals("Potatoes")?1000:3)){
						seedOKButton.setDisable(false);
					}
				}else{
					seedDetailTextFlow.getChildren().setAll(new Text("Choisissez une semence"));
					seedOKButton.setDisable(true);
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
		Shop shop = game.getShop();
		Inventory inv = game.getInventory();
		double buyPrice = shop.totalPrice(shop.getDataBuying())*shop.getBuyingPricePenalty();
		sellTotalLabel.setText(String.format("%.2f$", shop.totalPrice(shop.getDataSelling())*shop.getSellingPricePenalty()));
		buyTotalLabel.setText(String.format("%.2f$", buyPrice));
		//check if enough storage to enable harvest
		boolean enoughSilo = inv.enoughStorageFor(shop.getBuyingStorageRequired(true), true);
		boolean enoughOtherStorage = inv.enoughStorageFor(shop.getBuyingStorageRequired(false), false);
		buyButton.setDisable(buyPrice>inv.getMoney()||!enoughSilo||!enoughOtherStorage||!shop.enoughBuyTransaction());
		sellButton.setDisable(!shop.enoughSellTransaction());
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

   
    