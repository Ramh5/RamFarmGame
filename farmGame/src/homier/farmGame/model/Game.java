package homier.farmGame.model;

import java.util.ArrayList;

import homier.farmGame.controller.App;
import homier.farmGame.model.tile.BuildingTile;
import homier.farmGame.model.tile.FarmPlot;
import homier.farmGame.model.tile.ForestTile;
import homier.farmGame.model.tile.Tile;

public class Game {

	private Inventory inventory = new Inventory();
	private ArrayList<Tile> tileList = new ArrayList<Tile>();
	private WxForcast wxForcast = new WxForcast();
	private Employee[] employees;
	
	
	
	public Game() {
		for (int i = 0; i < App.gridColumns*App.gridRows; i++) {

			tileList.add(new ForestTile("FOREST_TILE"));
		}
		int[] indexList = { 30, 31, 32, 39, 41, 48, 49, 50 };
		for (int i : indexList) {
			tileList.set(i, new FarmPlot("FARM_PLOT", 0, 0));
		}
		tileList.set(40, new BuildingTile("HOUSE_TILE"));
		employees = new Employee[]{new Employee("Ram",0),new Employee("EMPTY",200)};
		inventory.addProd("Carrots", 52);
		inventory.addProd("Onions", 12);
		inventory.addProd("Eggs", 2);
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
}
