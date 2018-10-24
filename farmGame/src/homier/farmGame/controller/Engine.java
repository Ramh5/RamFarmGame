package homier.farmGame.controller;

import java.util.ArrayList;
import java.util.Map.Entry;

import homier.farmGame.model.Employee;
import homier.farmGame.model.Game;
import homier.farmGame.model.Inventory;
import homier.farmGame.model.Product;
import homier.farmGame.model.Recipe;
import homier.farmGame.model.Shop;
import homier.farmGame.model.tile.BuildingTile;
import homier.farmGame.utils.FarmTimeUnits;
import homier.farmGame.utils.GameClock;
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
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionModel;
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
import javafx.util.Callback;

public class Engine {

	@FXML private GridPane gameGridPane;
	@FXML private Button actionWSbutton;//closeShopButton, cancelTransactionButton, buyButton, sellButton;
	@FXML private ToggleButton pauseButton, openShopButton, openWSbutton;
	@FXML private VBox mouseOverPanel;
	@FXML private TextArea leftTextArea;
	@FXML private ChoiceBox<Integer> gameSpeedChoice;
	@FXML private Label clockLabel, pauseLabel ,wxToday, wxTomorrow, energyLabel, taskName1, buyTotalLabel, sellTotalLabel, labelSelectedRecipe, labelResultWS;
	@FXML private ChoiceBox<Employee> employeeChoice;
	@FXML private ProgressIndicator taskProgress1;
	@FXML private AnchorPane shopPane, workShopPane;
	@FXML private TreeTableView<Product> tableInv, tableShop, tableInvWS;
	@FXML private TreeTableColumn<Product, String> colNameInv, colNameShop,colNameInvWS, colQtyInv, colQtyShop, colQtyInvWS, 
												   colPriceInv, colPriceShop, colPriceInvWS, colSpoilQtyInv, colSpoilQtyShop, colSpoilQtyInvWS;
	@FXML private TreeTableColumn<Product, Number> colFreshInv, colFreshShop, colFreshInvWS, colQualInv, colQualShop,colQualInvWS;
	@FXML private TreeTableColumn<Product, Boolean> colActInv, colActShop, colActInvWS;
	@FXML private TableView<Product>  tableSell, tableBuy, tableSelectedIngrWS;
	@FXML private TableColumn<Product, String> colNameSell, colNameBuy, colNameIngrWS, colQtySell, colQtyBuy, colQtyIngrWS, colPriceSell, colPriceBuy, colPriceIngrWS;
	@FXML private TableColumn<Product, Number>  colFreshSell, colFreshBuy, colFreshIngrWS,  
												colQualSell, colQualBuy, colQualIngrWS;				
	@FXML private TableColumn<Product, Boolean> colActSell, colActBuy, colActIngrWS;
	@FXML private ListView<String>  listViewRecipeDetails;
	@FXML private ListView<Recipe> listViewRecipe;

	private Game game = new Game();
	private Renderer renderer;
	private GameClock gameClock;
	private boolean manPaused;
	
	
	//TODO add button to skip days, months...
	
	public void update(double dTime) {

		gameClock.update(dTime);
		clockLabel.setText(gameClock.toString());
		
		//if it is a new day, update the forecast
		if(gameClock.isNewDay()){
			game.getWxForcast().forcastNewDay(gameClock);
			wxToday.setText("Today: "+game.getWxForcast().getToday().toString());
			wxTomorrow.setText("Tomorrow: "+game.getWxForcast().getTomorrow().toString());
			gameClock.setIsNewDay(false);
			//update the inventory, especially for spoiling every day
			game.getInventory().update();
			game.getShop().update();
			updateShopPanel();
			updateWSPanel();
			
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
		gameSpeedChoice.setOnAction(e->{//TODO could add a call to a method in the FXML instead of here
			App.gameSpeed=gameSpeedChoice.getSelectionModel().getSelectedItem();
		});
		
		 //employee choice
		employeeChoice.setOnAction(e->{
			updateWSResultLabel();//to update the action button disable or not with the new employee
			energyLabel.textProperty().bind(getActiveEmployee().energyProperty().asString(" Energy: %.0f  "));
			taskProgress1.setProgress(getActiveEmployee().getTask().getTaskProgress(gameClock.getTotalSeconds()));
			taskName1.setText(getActiveEmployee().getTask().getName());
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
		setupWS();
		
		
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
	private void closeWSbuttonAction(ActionEvent event) {
		if(!openShopButton.isSelected()) {
			if (!manPaused) {
				pauseButton.setSelected(false);
			}
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
		updateWSPanel();
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
		updateWSPanel();
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
	/**
	 * creates the task for crafting a recipe in the workshop 
	 * 
	 * @param event
	 */
	@FXML
	private void actionWSbuttonAction(ActionEvent event) {
		game.getWorkShop().startTask(getActiveEmployee(),game.getInventory(),gameClock.getTotalSeconds());
		taskProgress1.setProgress(getActiveEmployee().getTask().getTaskProgress(gameClock.getTotalSeconds()));
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
	
	
	/**
	 * setup the Shop panel for the first time
	 */
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
	
	/**
	 * setup the workShop panel for the first time
	 */
	private void setupWS(){
		final PseudoClass topLevelTTVPseudoClass = PseudoClass.getPseudoClass("top-level-treetableview");
		
		//------------ WS inventory table------------- //TODO setup spoil colum (negative and red)
		TreeItem<Product> rootInv = new TreeItem<>(new Product("empty", 0, 0, 0));
		tableInvWS.setPlaceholder(new Text("Empty Inventory"));
		tableInvWS.setRoot(rootInv);
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
		colActIngrWS.setCellFactory(CheckBoxTableCell.forTableColumn(colActSell));
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
			            	setText(item.getName()+" "+item.getIngredientList().keySet().toString());
			            }
			        }
				};
			}
		});

		listViewRecipe.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<Recipe>() {
					@Override // populate the recipe details label and table in the workshop panel with the selected item
					public void changed(ObservableValue<? extends Recipe> observable, Recipe oldValue, Recipe newValue) {
						if(newValue!=null) {
							labelSelectedRecipe.setText(newValue.getName() + ", " + newValue.getQuantity() + " kg");
							ObservableList<String> recipeDetailList = FXCollections.observableArrayList();
							for(Entry<String,Double> entry: newValue.getIngredientList().entrySet()) {
								recipeDetailList.add(entry.getKey() + ", " + entry.getValue() + " kg");
							}
							listViewRecipeDetails.getItems().setAll(recipeDetailList);
							
							
						}
						updateWSResultLabel();
					}
				});


	}
	
	/**
	 * update the result label to reflect the selected recipe and selected ingredients
	 * @return the result (or anticipated result) of the craft
	 */
	public void updateWSResultLabel(){
		Product result = game.getWorkShop().getResult();
		Employee empl = getActiveEmployee();
		if(listViewRecipe.getSelectionModel().getSelectedItem()!=null) {
			game.getWorkShop().calculateResult(listViewRecipe.getSelectionModel().getSelectedItem());
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
		
		game.getWorkShop().copyInventory(game.getInventory());
		game.getWorkShop().getSelectedIngr().clear();
		
		updateTreeItemRoot(tableInvWS.getRoot(), game.getWorkShop(), game.getShop());
		listenForSelection(tableInvWS.getRoot(), game.getWorkShop().getSelectedIngr());
		
		Recipe selectedRecipe = listViewRecipe.getSelectionModel().getSelectedItem();
		listViewRecipe.getItems().setAll(game.getWorkShop().getRecipeList("Cuisine").values());
		listViewRecipe.getSelectionModel().select(selectedRecipe);
		updateWSResultLabel();
		//listViewRecipe.setItems(.toArray());
	}
	
	/**
	 * updates the Shop panel after it has been set up to show changed data
	 */
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
			//update the transaction labels
			Shop shop = game.getShop();
			buyTotalLabel.setText(String.format("%.2f", shop.totalPrice(shop.getDataBuying())));
			sellTotalLabel.setText(String.format("%.2f", shop.totalPrice(shop.getDataSelling())));
			
			updateWSResultLabel();
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

   
    