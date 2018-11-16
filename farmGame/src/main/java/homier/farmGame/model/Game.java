package homier.farmGame.model;

import java.util.ArrayList;

import homier.farmGame.controller.App;
import homier.farmGame.model.tile.BuildingTile;
import homier.farmGame.model.tile.FarmPlot;
import homier.farmGame.model.tile.ForestTile;
import homier.farmGame.model.tile.Tile;
import homier.farmGame.utils.GameClock;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Game {



	
	private Inventory inventory;
	private Shop shop;
	private WorkShop workShop;
	private ArrayList<Tile> tileList;
	private WxForcast wxForcast;
	private Employee[] employees;
	private GameClock gameClock;
	
	
	
	
	public Game() {

		inventory = new Inventory();
		inventory.addMoney(1000);
		shop = new Shop();
		workShop = new WorkShop();
		tileList = new ArrayList<Tile>();
		gameClock = new GameClock(300, 0);
		wxForcast = new WxForcast(gameClock);
		
		//puts foresttiles on all the grid and sets the price in relation to the distance from (4,4) where we put the house
		for (int i = 0; i < App.gridColumns; i++) {
			for (int j = 0; j < App.gridRows; j++) {
				double distance = Math.sqrt((i-4)*(i-4)+(j-4)*(j-4));
				tileList.add(new ForestTile(1000*Math.round(distance)));
				
			}
		}

				
		
		int[] indexList = { 30, 31, 32, 39, 41, 48, 49, 50 };
		for (int i : indexList) {
			tileList.set(i, new FarmPlot());
		}
		tileList.set(40, new BuildingTile("house", 0, 500, 500));
		employees = new Employee[]{new Employee("Ram",0,1000)};
		/*
		inventory.addProd(new Product(MyData.categoriesOf("Blé"),"Blé",12.0220,100,1));
		inventory.addProd(new Product(MyData.categoriesOf("Carottes"),"Carottes",100,100,80));
		inventory.addProd(new Product(MyData.categoriesOf("Carottes"),"Carottes",80,80,50));
		inventory.addProd(new Product(MyData.categoriesOf("Oeufs"),"Oeufs",100,100,50));
		inventory.addProd(new Product(MyData.categoriesOf("Oignons"),"Oignons",30,70,50));
	*/
		
	}
	
	public ArrayList<Tile> getTileList(){
		return tileList;
	}
	
	public Inventory getInventory(){
		return inventory;
	}

	public WxForcast getWxForcast() {
		return wxForcast;
	}
	
	public Employee[] getEmployees(){
		return employees;
	}
	
	public Shop getShop(){
		return shop;
	}
	
	public WorkShop getWorkShop() {
		return workShop;
	}
	
	public GameClock getClock(){
		return gameClock;
	}
}
