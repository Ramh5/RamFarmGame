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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Engine {

	@FXML private GridPane gameGridPane;
	@FXML private ToggleButton pauseButton, openShopButton, openWSbutton;
	@FXML private VBox mouseOverPanel;
	@FXML private TextArea leftTextArea;
	@FXML private ChoiceBox<Integer> gameSpeedChoice;
	@FXML private Label clockLabel, pauseLabel ,wxToday, wxTomorrow, energyLabel, taskName1, buyTotalLabel, sellTotalLabel;
	@FXML private ChoiceBox<Employee> employeeChoice;
	@FXML private ProgressIndicator taskProgress1;
	@FXML private AnchorPane shopPane, workShopPane;
	@FXML private TreeTableView<Product> tableInv, tableShop;
	@FXML private TreeTableColumn<Product, String> colNameInv, colNameShop, colQtyInv, colQtyShop, 
												   colPriceInv, colPriceShop, colSpoilQtyInv, colSpoilQtyShop;
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
			game.getShop().update();
			updateShopPanel();
			
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
		shopPane.toBack();
		setupShop();
		preventColumnReordering(tableInv);
		preventColumnReordering(tableShop);
		preventColumnReordering(tableSell);
		preventColumnReordering(tableBuy);
		
		//workShop
		workShopPane.toBack();
		
		
	}

	//-------------- BUTTON HANDELERS---------------------
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
		if(!openWSbutton.isSelected()) {
			if (!manPaused) {
				pauseButton.setSelected(false);
			}
			pauseButton.setDisable(false);
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
			pauseButton.setSelected(true);
			pauseButton.setDisable(true);
		}else{
			if(!openWSbutton.isSelected()) {
				if (!manPaused) {
					pauseButton.setSelected(false);
				}
				pauseButton.setDisable(false);
			}
			shopPane.toBack();
		}
		updatePauseButton();
	}

	@FXML
	private void buyButtonAction(ActionEvent event){
		game.getInventory().addMoney(-Double.valueOf(buyTotalLabel.getText()));
		for(Product prod:tableBuy.getItems() ){
			game.getInventory().addProd(new Product(prod));
			prod.setQty(-prod.getQty());
			game.getShop().addProd(new Product(prod));
		}
		tableBuy.getItems().clear();
		buyTotalLabel.setText(String.format("%.2f", 0.0));
		
		updateShopPanel();
		unselectTreeTable(tableShop.getRoot());	
	}
	
	@FXML
	private void sellButtonAction(ActionEvent event){
		game.getInventory().addMoney(Double.valueOf(sellTotalLabel.getText()));
		for(Product prod:tableSell.getItems() ){
			game.getShop().addProd(new Product(prod));
			prod.setQty(-prod.getQty());
			game.getInventory().addProd(new Product(prod));
		}
		tableSell.getItems().clear();
		sellTotalLabel.setText(String.format("%.2f", 0.0));
		updateShopPanel();
		unselectTreeTable(tableInv.getRoot());
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
	
	@FXML
	private void actionWSbuttonAction(ActionEvent event) {
		//TODO handle action workshop button
	}
	
	@FXML 
	private void cancelWSbuttonAction(ActionEvent event) {
		//TODO handle cancel workshop button
	}
	
	@FXML
	private void closeWSbuttonAction(ActionEvent event) {
		if(!openShopButton.isSelected()) {
			if (!manPaused) {
				pauseButton.setSelected(false);
			}
			pauseButton.setDisable(false);
		}
		workShopPane.toBack();
		openWSbutton.setSelected(false);
		updatePauseButton();
	}
	
	@FXML private void openWSbuttonAction(ActionEvent event) {
		if(openWSbutton.isSelected()){
			//updateShopPanel(); //TODO update workshoppane function
			workShopPane.toFront();
			pauseButton.setSelected(true);
			pauseButton.setDisable(true);
		}else{		
			if(!openShopButton.isSelected()) {
				if (!manPaused) {
					pauseButton.setSelected(false);
				}
				pauseButton.setDisable(false);
			}
			workShopPane.toBack();
		}
		updatePauseButton();
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
		
		//------------inventory table------------- //TODO setup spoil colum (negative and red)
		TreeItem<Product> rootInv = new TreeItem<>(new Product("empty", 0, 0, 0));
		tableInv.setPlaceholder(new Text("Empty Inventory"));
		tableInv.setRoot(rootInv);
		tableInv.setShowRoot(false);
		tableInv.setEditable(true);
		colActInv.setEditable(true);
		
		//populate the inventory treetableview with the inventory data
		
		TreeItem<Product> rootShop = new TreeItem<>(new Product("empty", 0, 0, 0));
		tableShop.setRoot(rootShop);
		updateShopPanel();
		
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
	
	
	
	private void updateShopPanel(){
		updateTreeItemRoot(tableInv.getRoot(), game.getInventory(), game.getShop());
		updateTreeItemRoot(tableShop.getRoot(), game.getShop(), game.getShop());
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
	private void updateTreeItemRoot(TreeItem<Product> root, Inventory inventory, Shop shop){
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
		}
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
	
	private EventHandler<TableColumn.CellEditEvent<Product,String>> transactionQtyEditCommit(Inventory inventory){
		return event->{
			int row = event.getTablePosition().getRow();
			Product prod = event.getTableView().getItems().get(row);
			double newValue = Double.valueOf(event.getNewValue());
			if(newValue<0){
				prod.setQty(0);
			}else if(newValue > prod.getQty()){
				prod.setQty(inventory.getMaxQty(prod));
			}else{	
				prod.setQty(newValue);
			}
			event.getTableView().refresh();
			//update the transaction labels
			Shop shop = game.getShop();
			buyTotalLabel.setText(String.format("%.2f", shop.totalPrice(shop.getDataBuying())));
			sellTotalLabel.setText(String.format("%.2f", shop.totalPrice(shop.getDataSelling())));
		};
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

   
    