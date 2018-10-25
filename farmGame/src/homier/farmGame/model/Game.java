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
		shop = new Shop();
		workShop = new WorkShop();
		tileList = new ArrayList<Tile>();
		gameClock = new GameClock(300, 0);
		wxForcast = new WxForcast(gameClock);
		
		for (int i = 0; i < App.gridColumns*App.gridRows; i++) {

			tileList.add(new ForestTile("FOREST_TILE"));
		}
		int[] indexList = { 30, 31, 32, 39, 41, 48, 49, 50 };
		for (int i : indexList) {
			tileList.set(i, new FarmPlot("FARM_PLOT", 0, 0));
		}
		tileList.set(40, new BuildingTile("HOUSE_TILE"));
		employees = new Employee[]{new Employee("Ram",0,1000),new Employee("EMPTY",200,900)};
		inventory.addProd(new Product("Blé",12.0220,5,1));
		inventory.addProd(new Product("Blé",12,6,1));
		inventory.addProd(new Product("Blé",12,1,1));
		inventory.addProd(new Product("Blé",-2,5,1));
		inventory.addProd(new Product("Blé",1,6,1));
		inventory.addProd(new Product("Carottes",15,2,80));
		inventory.addProd(new Product("Carottes",10,6,50));
		inventory.addProd(new Product("Oeufs",15,6,50));
		inventory.addProd(new Product("Oeufs",14,14,80));
		inventory.addProd(new Product("Oeufs",25,24,80));
		inventory.addProd(new Product("Oignons",30,2,50));
	
		
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
