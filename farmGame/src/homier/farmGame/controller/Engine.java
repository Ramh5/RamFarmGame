package homier.farmGame.controller;

import java.util.ArrayList;
import java.util.Map.Entry;

import homier.farmGame.model.Employee;
import homier.farmGame.model.Game;
import homier.farmGame.model.Inventory;
import homier.farmGame.model.Product;
import homier.farmGame.model.Shop;
import homier.farmGame.utils.FarmTimeUnits;
import homier.farmGame.utils.GameClock;
import homier.farmGame.view.Renderer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;

public class Engine {

	@FXML private GridPane gameGridPane;
	@FXML private ToggleButton pauseButton, openShopButton;
	@FXML private VBox mouseOverPanel;
	@FXML private TextArea leftTextArea;
	@FXML private ChoiceBox<Integer> gameSpeedChoice;
	@FXML private Label clockLabel, pauseLabel ,wxToday, wxTomorrow, energyLabel, taskName1, buyTotalLabel, sellTotalLabel;
	@FXML private ChoiceBox<Employee> employeeChoice;
	@FXML private ProgressIndicator taskProgress1;
	@FXML private AnchorPane shopPane;
	@FXML private TreeTableView<Product> tableInv, tableShop;
	@FXML private TreeTableColumn<Product, String> colNameInv, colNameShop,colQtyInv, colQtyShop, colPriceInv, colPriceShop;
	@FXML private TreeTableColumn<Product, Number> colFreshInv, colFreshShop,colQualInv, colQualShop;
	@FXML private TreeTableColumn<Product, Boolean> colActInv, colActShop;
	@FXML private TableView<Product>  tableSell, tableBuy;
	@FXML private TableColumn<Product, String> colNameSell, colNameBuy, colQtySell, colQtyBuy, colPriceSell, colPriceBuy;
	@FXML private TableColumn<Product, Number>  colFreshSell, colFreshBuy,  
												colQualSell, colQualBuy;				
	@FXML private TableColumn<Product, Boolean> colActSell, colActBuy;
	@FXML private Button closeShopButton, cancelTransactionButton, buyButton, sellButton;
	

	private Game game = new Game();
	private Renderer renderer;
	private GameClock gameClock;
	private boolean manPaused;
	
	ChangeListener<TreeItem<Product>> rowListener;
	ChangeListener<TreeItem<Product>> prevRowListener = (a,b,c)->{};
	
	
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
			
		}
		leftTextArea.setText(game.getInventory().toString());
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
		openShopButton.setSelected(false);
	}

	@FXML
	private void openShopButtonAction(ActionEvent event) {
		if(openShopButton.isSelected()){
			shopPane.toFront();
		}else{
			shopPane.toBack();
		}
		 
	}
	
	@FXML
	private void buyButtonAction(ActionEvent event){
		game.getInventory().addMoney(-Double.valueOf(buyTotalLabel.getText()));
		for(Product prod:tableBuy.getItems() ){
			game.getInventory().addProd(prod);
		}
		tableBuy.getItems().clear();
		unselectTreeTable(tableShop.getRoot());	
	}
	
	@FXML
	private void sellButtonAction(ActionEvent event){
		game.getInventory().addMoney(Double.valueOf(sellTotalLabel.getText()));
		for(Product prod:tableBuy.getItems() ){
			prod.setQty(-prod.getQty());
			game.getInventory().addProd(prod);
		}
		tableSell.getItems().clear();
		unselectTreeTable(tableInv.getRoot());
		
	}
	
	@FXML
	private void cancelTransactionButtonAction(ActionEvent event){
		tableBuy.getItems().clear();
		tableSell.getItems().clear();
		unselectTreeTable(tableInv.getRoot());
		unselectTreeTable(tableShop.getRoot());
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
		final PseudoClass topLevelTTVPseudoClass = PseudoClass.getPseudoClass("top-level-treetableview");
		
		//------------inventory table-------------
		TreeItem<Product> rootInv = new TreeItem<>(new Product("empty", 0, 0, 0));
		tableInv.setPlaceholder(new Text("Empty Inventory"));
		tableInv.setRoot(rootInv);
		tableInv.setShowRoot(false);
		tableInv.setEditable(true);
		colActInv.setEditable(true);
		
		
		
		//populate the inventory treetableview with the inventory data
		Inventory inventory = game.getInventory();
		Shop shop = game.getShop();
		
		inventory.calculateAverageData();
		for(Entry<String, ArrayList<Product>> entry : inventory.getData().entrySet()){
			//TODO prevent this product from being selectable (already removed the listener so the selection does noting but still visual bug)
			Product product = inventory.getAverageData(entry.getKey());
			TreeItem<Product> averageProdDataItem = new TreeItem<Product>(product); 
			product.updatePrice(shop);
			averageProdDataItem.setExpanded(true);
			rootInv.getChildren().add(averageProdDataItem);
			for(Product prod : entry.getValue()){
				prod.updatePrice(shop);
				TreeItem<Product> treeItem = new TreeItem<Product>(prod);
				averageProdDataItem.getChildren().add(treeItem);
			}
		}
		
		//link the inventory table with the selling table
		listenForSelection(rootInv, game.getShop().getDataSelling());
		
		//Setup the cell values
		colNameInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtyInv.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().qtyProperty()));
		colFreshInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualInv.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colPriceInv.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().priceProperty()));
		colActInv.setCellFactory(column ->{ //TODO show the state of the checkbox depending on the children selected
			return new CheckBoxTreeTableCell<Product, Boolean>(){
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					boolean isTopLevel = getTreeTableView().getRoot().getChildren().contains(getTreeTableRow().getTreeItem());
					getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, isTopLevel);
					setEditable(!isTopLevel);
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
		colFreshSell.setCellValueFactory(new PropertyValueFactory<>("fresh"));
		colQualSell.setCellValueFactory(new PropertyValueFactory<>("qual"));
		colPriceSell.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().priceProperty()));
		colActSell.setCellFactory(CheckBoxTableCell.forTableColumn(colActSell));
		colActSell.setCellValueFactory(new PropertyValueFactory<>("selected"));
		
		//-------------shop table---------------
		TreeItem<Product> rootShop = new TreeItem<>(new Product("empty", 0, 0, 0));
		tableShop.setPlaceholder(new Text("Empty Shop"));
		tableShop.setRoot(rootShop);
		tableShop.setShowRoot(false);
		tableShop.setEditable(true);
		
	

		//populate the shop treetableview with the shop data
		
		shop.calculateAverageData();
		for(Entry<String, ArrayList<Product>> entry : shop.getData().entrySet()){
			Product product = shop.getAverageData(entry.getKey());
			TreeItem<Product> averageProdDataItem = new TreeItem<Product>(product); 
			product.updatePrice(shop);
			averageProdDataItem.setExpanded(true);
			rootShop.getChildren().add(averageProdDataItem);
			for(Product prod : entry.getValue()){
				prod.updatePrice(shop);
				TreeItem<Product> treeItem = new TreeItem<Product>(prod);
				averageProdDataItem.getChildren().add(treeItem);
			}

		}
		
		//link the shop table with the buying table
		listenForSelection(rootShop, game.getShop().getDataBuying());

		//Setup the cell values
		colNameShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtyShop.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().qtyProperty()));
		colFreshShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualShop.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colPriceShop.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().getValue().priceProperty()));
		colActShop.setCellFactory(column ->{
			return new CheckBoxTreeTableCell<Product, Boolean>(){
				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					boolean isTopLevel = getTreeTableView().getRoot().getChildren().contains(getTreeTableRow().getTreeItem());
					setEditable(!isTopLevel);
					getTreeTableRow().pseudoClassStateChanged(topLevelTTVPseudoClass, isTopLevel);
					
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
		colFreshBuy.setCellValueFactory(new PropertyValueFactory<>("fresh"));
		colQualBuy.setCellValueFactory(new PropertyValueFactory<>("qual"));
		colPriceBuy.setCellValueFactory(cellData ->Bindings.format("%.2f", cellData.getValue().priceProperty()));
		colActBuy.setCellFactory(CheckBoxTableCell.forTableColumn(colActBuy));
		colActBuy.setCellValueFactory(new PropertyValueFactory<>("selected"));
		colQtyBuy.setEditable(true);

		//tableBuy.getColumns().get(3).getCellData(12)
	}
	
	
	
	/**
	 * Adds the appropriate changelisteners to products in the inventory treetableview (only for the leafs)
	 * and populates the transaction table with copies of the selected products
	 * @param rootTreeItem : the inventory root(top level) TreeItem
	 * @param list : the transaction table list
	 */
	private void listenForSelection(TreeItem<Product> rootTreeItem, ObservableList<Product> list) {
		//TODO figure out how to manage new products added in the inventory, maybe adding a listener property to the Product class
		BooleanProperty selected = rootTreeItem.getValue().selectedProperty();
		Product copy = new Product(rootTreeItem.getValue());
		copy.setSelected(true);
		copy.selectedProperty().addListener((obs, oldVal, newVal)->{
			if(newVal){
				
			}else{
				selected.set(false);
			}
		});
		if(rootTreeItem.isLeaf()){
			selected.addListener((obs, oldVal, newVal) -> {
				if(newVal){
					list.add(copy);
					
				}else{
					list.remove(copy);
					copy.setSelected(true);
				}
				
				sellTotalLabel.setText(String.format("%.2f", game.getShop().totalPrice(game.getShop().getDataSelling())));
				buyTotalLabel.setText(String.format("%.2f", game.getShop().totalPrice(game.getShop().getDataBuying())));
			});
		}
		rootTreeItem.getChildren().forEach(item -> listenForSelection(item,list));
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
}

   
    