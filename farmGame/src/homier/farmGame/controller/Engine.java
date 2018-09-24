package homier.farmGame.controller;

import java.util.ArrayList;

import homier.farmGame.model.Employee;
import homier.farmGame.model.Game;
import homier.farmGame.model.Product;
import homier.farmGame.utils.GameClock;
import homier.farmGame.utils.*;
import homier.farmGame.view.Renderer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeCell;
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
import javafx.util.Callback;

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
	@FXML private TreeTableView<Product> tableInv, tableShop, tableSell, tableBuy;
	@FXML private TreeTableColumn<Product, String> colNameInv, colNameShop, colNameSell, colNameBuy;
	@FXML private TreeTableColumn<Product, Number> colQtyInv, colQtyShop, colQtySell, colQtyBuy,
												   colPriceInv, colPriceShop, colPriceSell, colPriceBuy;
	@FXML private TreeTableColumn<Product, Number> colFreshInv, colFreshShop, colFreshSell, colFreshBuy,
													colQualInv, colQualShop, colQualSell,colQualBuy;
	@FXML private TreeTableColumn<Product, Boolean> colActInv, colActShop, colActSell, colActBuy;
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
		TreeItem<Product> rootSell = new TreeItem<>();
		ObservableList<TreeItem<Product>> sellList = FXCollections.observableList(new ArrayList<TreeItem<Product>>());
		
		//inventory table
		TreeItem<Product> rootInv = new TreeItem<>();
		tableInv.setRoot(rootInv);
		tableInv.setShowRoot(false);
		tableInv.setEditable(true);
		colActInv.setEditable(true);
		rootInv.getChildren().add(new TreeItem<Product>(game.getInventory().getProd()));
		
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
		colActInv.setCellValueFactory(cellData -> {
			BooleanProperty property = cellData.getValue().getValue().selectedProperty();
            property.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					TreeItem<Product> treeItem = new TreeItem<Product>(cellData.getValue().getValue());
					if(newValue){
						sellList.add(treeItem);//TODO why does not add the treeitem
						System.out.println(newValue);//why prints 4 times?
					}else{
						sellList.remove(treeItem);
					}
						
				}
            	
			});
			return property;
        });
        
		//sell table
		rootSell.getChildren().addAll(sellList);
		tableSell.setRoot(rootSell);
		tableSell.setShowRoot(false);
		tableSell.setEditable(true);
		colActSell.setEditable(true);
		//sellList.add(new TreeItem<Product>(new Product("Wheat", 1, 1, 1)));
		rootSell.getChildren().add(new TreeItem<Product>(new Product("Wheat", 1, 1, 1)));
		colNameSell.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		colQtySell.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
		colFreshSell.setCellValueFactory(new TreeItemPropertyValueFactory<>("fresh"));
		colQualSell.setCellValueFactory(new TreeItemPropertyValueFactory<>("qual"));
		colPriceSell.setCellValueFactory( cellData-> {
				ObservableValue<Number> price ;
				price =new ReadOnlyDoubleWrapper(game.getShop().price(cellData.getValue().getValue())); 
				return price;
		});
		colActSell.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(colActSell));
		colActSell.setCellValueFactory(cellData -> {
             return cellData.getValue().getValue().selectedProperty();
        });
		
		
	}
	/*
	private TreeItem<Product> updateTransactionTable(){
		TreeItem<Product> rootSell = new TreeItem<>();
		rootSell.getChildren().add(new TreeItem<Product>(game.getInventory().getProd()));
	}
	*/
}

   
    