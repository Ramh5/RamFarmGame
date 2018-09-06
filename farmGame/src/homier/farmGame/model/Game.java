package homier.farmGame.model;

import java.util.ArrayList;

import homier.farmGame.controller.App;

public class Game {

	private Inventory inventory = new Inventory();
	private ArrayList<Tile> tileList = new ArrayList<Tile>();

	public Game() {
		for (int i = 0; i < App.gridColumns*App.gridRows; i++) {

			tileList.add(new Tile("FREST_TILE", App.forestTileImage));
		}
		int[] indexList = { 30, 31, 32, 39, 41, 48, 49, 50 };
		for (int i : indexList) {
			tileList.set(i, new FarmPlot("DIRT_PLOT", App.dirtTileImage, 0, 0));
		}
		tileList.set(40, new Tile("HOUSE_TILE", App.houseImage));
	}
	
	public ArrayList<Tile> getTileList(){
		return tileList;
	}

}
