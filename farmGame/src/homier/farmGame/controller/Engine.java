package homier.farmGame.controller;

import java.util.ArrayList;
import java.util.Map.Entry;

import homier.farmGame.model.Employee;
import homier.farmGame.model.Game;
import homier.farmGame.model.Inventory;
import homier.farmGame.model.Product;
import homier.farmGame.utils.FarmTimeUnits;
import homier.farmGame.utils.GameClock;
import homier.farmGame.view.Renderer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Engine {

	@FXML private GridPane gameGridPane;
	@FXML private Label clockLabel;
	@FXML private ToggleButton pauseButton;
	@FXML private Label pauseLabel;
	@FXML private VBox mouseOverPanel;
	@FXML private TextArea leftTextArea;
	@FXML private ChoiceBox<Integer> gameSpeedChoice;
	@FXML private Label wxToday;
	@FXML private Label wxTomorrow;
	@FXML private Label energyLabel;
	@FXML private ChoiceBox<Employee> employeeChoice;
	@FXML private Label taskName1;
	@FXML private ProgressIndicator taskProgress1;
	@FXML private AnchorPane shopPane;
	@FXML private TreeTableView<Product> tableInv, tableShop;
	@FXML private TreeTableColumn<Product, String> colNameInv, colNameShop;
	@FXML private TreeTableColumn<Product, Number> colQtyInv, colQtyShop, colFreshInv, colFreshShop,
												   colQualInv, colQualShop, colPriceInv, colPriceShop;
	@FXML private TreeTableColumn<Product, Boolean> colActInv, colActShop;
	@FXML private TableView<Product>  tableSell, tableBuy;
	@FXML private TableColumn<Product, String> colNameSell, colNameBuy;
	@FXML private TableColumn<Product, Number>  colQtySell, colQtyBuy, colFreshSell, colFreshBuy,  
												colQualSell, colQualBuy, colPriceSell, colPriceBuy;				
	@FXML private TableColumn<Product, Boolean> colActSell, colActBuy;
	@FXML private Button closeShopButton;

	private Game game = new Game();
	private Renderer renderer;
	private GameClock gameClock;
	private boolean manPaused;
	
	
	
	//TODO add button to skip days, months...
	
	public void update(double dTime) {

		gameClock.update(dTime);
		clockLabel.setText(gameClock.toString());
		
		//if it is a new day, update the forcast
		if(gameClock.isNewDay()){
			game.getWxForcast().forcastNewDay(gameClock);
			wxToday.setText("Today: "+game.getWxForcast().getToday().toString());
			wxTomorrow.setText("Tomorrow: "+game.getWxForcast().getTomorrow().toString());
			gameClock.setIsNewDay(false);
			//update the inventory, especially for spoiling every day
			game.getInventory().update();
			leftTextArea.setText(game.getInventory().toString());
		}
		
		//update energy and task name when task is in progress
		
		taskProgress1.setProgress(getActiveEmployee().getTask().getTaskProgress(gameClock.getTotalSeconds()));
		taskName1.setText(getActiveEmployee().getTask().getName());
		
		//update each tile (only update the model, ie growth, yield)
		for (int i = 0; i < game.getTileList().size(); i++) {
			game.getTileList().get(i).update(dTime / gameClock.getSecondsInADay(),game.getWxForcast().getToday());
		}
		
		

	}

	public void render() {
		renderer.render(this);

	}

	public void initialize() {

		renderer = new Renderer(game.getTileList(), gameGridPane);
		gameClock = new GameClock(300, 0);
		gameClock.addTime(4*FarmTimeUnits.MONTH.seconds);
		
		game.getWxForcast().forcastNewDay(gameClock);
		wxToday.setText("Today: "+game.getWxForcast().getToday().toString());
		wxTomorrow.setText("Tomorrow: "+game.getWxForcast().getTomorrow().toString());
		gameClock.setIsNewDay(false);
		clockLabel.setText(gameClock.toString());
		pauseButton.setGraphic(new ImageView(new Image("Button-Pause.png", 32, 32, true, true)));
		pauseButton.setBackground(Background.EMPTY);
		leftTextArea.setText(game.getInventory().toString());
		
		
		
		gameSpeedChoice.getItems().addAll(1,2,5,10,50,500);
		gameSpeedChoice.getSelectionModel().select(3);
		App.gameSpeed=gameSpeedChoice.getSelectionModel().getSelectedItem();
		gameSpeedChoice.setOnAction(e->{//TODO could add a call to a methode in the FXML instead of here
			App.gameSpeed=gameSpeedChoice.getSelectionModel().getSelectedItem();
		});
		
		 //employee choice
		employeeChoice.setOnAction(e->{
			energyLabel.textProperty().bind(getActiveEmployee().energyProperty().asString(" Energy: %.0f  "));
		});
		employeeChoice.getItems().addAll(game.getEmployees());
		employeeChoice.getSelectionModel().select(0);
		energyLabel.textProperty().bind(getActiveEmployee().energyProperty().asString(" Energy: %.0f  "));
		//task
		taskName1.setText("TASK");
		
		//shop
		setupShop();
		
		
	}

	@FXML
	private void pauseButtonAction(ActionEvent event) {
		updatePauseButton();
		if(pauseButton.isSelected()){
			manPaused=true;
		}else{
			manPaused=false;
		}
		
	}

	public void updatePauseButton(){
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
		shopPane.toBack();
	}

	@FXML
	private void openShopButtonAction(ActionEvent event) {
		shopPane.toFront();
	}
	
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
		return gameClock;
	}

	public boolean getManPaused() {
		
		return manPaused;
	}
	
	/**
	 * @return the active employee as selected on the employee choice box
	 */
	public Employee getActiveEmployee(){
		return employeeChoice.getSelectionModel().getSelectedItem();
	}
	
	
	
	private void setupShop(){
		
	
		
		
		
		//inventory table
		TreeItem<Product> rootInv = new TreeItem<>(new Product("empty", 0, 0, 0));
		tableInv.setPlaceholder(new Text("Empty Inventory"));
		tableInv.setRoot(rootInv);
		tableInv.setShowRoot(false);
		tableInv.setEditable(true);
		colActInv.setEditable(true);
		//ObservableList<TreeItem<Product>> invList = FXCollections.observableList(new ArrayList<TreeItem<Product>>());
		Inventory inventory = game.getInventory();
		inventory.calculateAverageData();
		for(Entry<String, ArrayList<Product>> entry : inventory.getData().entrySet()){
			
			TreeItem<Product> averageProdDataItem = new TreeItem<Product>(inventory.getAverageData(entry.getKey())); //TODO prevent this product from being selectable
			rootInv.getChildren().add(averageProdDataItem);
			for(Product prod : entry.getValue()){
				TreeItem<Product> treeItem = new TreeItem<Product>(prod);
				averageProdDataItem.getChildren().add(treeItem);
			}
			
		}
		
		listenForSelection(rootInv, game.getShop().getDataSelling());
		
		colNameInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtyInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
		colFreshInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colPriceInv.setCellValueFactory( cellData-> {
				ObservableValue<Number> price ;
				price =new ReadOnlyDoubleWrapper(game.getShop().price(cellData.getValue().getValue())); 
				return price;
		});
		colActInv.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(colActInv));
		colActInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));
			
  
        
		//sell table
		tableSell.setItems(game.getShop().getDataSelling());
		//tableSell.setRoot(rootSell);
		tableSell.setEditable(true);
		colActSell.setEditable(true);
		
		//rootSell.getChildren().add(new TreeItem<Product>(new Product("Wheat", 1, 1, 1)));
		colNameSell.setCellValueFactory(new PropertyValueFactory<>("name"));
		colQtySell.setCellValueFactory(new PropertyValueFactory<>("qty"));
		colFreshSell.setCellValueFactory(new PropertyValueFactory<>("fresh"));
		colQualSell.setCellValueFactory(new PropertyValueFactory<>("qual"));
		colPriceSell.setCellValueFactory( cellData-> {
				ObservableValue<Number> price ;
				price =new ReadOnlyDoubleWrapper(game.getShop().price(cellData.getValue())); 
				return price;
		});
		colActSell.setCellFactory(CheckBoxTableCell.forTableColumn(colActSell));
		colActSell.setCellValueFactory(new PropertyValueFactory<>("selected"));
				
		
	}
	
	
	
	/*
	private void updateTransactionTable(){
		rootSell.getChildren().setAll(sellList);
		//tableSell.setRoot(rootSell);
		
	}
	*/
	
	private void addToTransactionTable(ObservableList<Product> list, Product prod){
		prod.setSelected(true);
		prod.selectedProperty().addListener((obs, oldVal, newVal) -> {
			if(!newVal){
				list.remove(prod);
			}
		});
		list.add(prod);
	}
	
	private void transactionTableListener(Product prod,ChangeListener<Boolean> listener){
		
	}
	
	private void listenForSelection(TreeItem<Product> treeItem, ObservableList<Product> list) {
		BooleanProperty selected = treeItem.getValue().selectedProperty();
		selected.addListener((obs, oldVal, newVal) -> {
		if(newVal){
			list.add(treeItem.getValue());//TODO make a clone method in Produdct to allow editable??
		}else{
			list.remove(treeItem.getValue());
		}
		});
		treeItem.getChildren().forEach(item -> listenForSelection(item,list));
	}
	
}

   
    