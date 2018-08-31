package homier.farmGame.logic;

import java.util.ArrayList;

import homier.farmGame.FarmPlot;
import homier.farmGame.Grid;
import homier.farmGame.Tile;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Logic {
	
	public static void update(Grid theGrid, double dTime){
		ArrayList<Tile> tileList = theGrid.getTileList();
		ObservableList<Node> children = theGrid.getChildren();
		int size = children.size();
		
		for(Tile tile : tileList){
			tile.update(dTime);
		}
		
		
		
		//((ImageView) children.get((int)(Math.random()*(size-1)))).setImage(new Image("dirt_tile.png"));
		
		//loops tiles to update them, "if" to see what  kind of tile they are... a little messy
		/*
		for(Tile tile:tileList){
			if(tile.getID().substring(tile.getID().indexOf("_")).equals("PLOT")){
				
			}
		}
		*/
		
		//loops tiles to update them using .getclass to get the type BAD OOP practice to use if instanceof and casting
		/*
		for(Tile tile:tileList){
			if(tile instanceof FarmPlot){
				double growth = ( (FarmPlot) tile).getGrowth()+((FarmPlot) tile).getGrowthRate()*dTime;
				((FarmPlot) tile).setGrowth(growth);
				System.out.println(growth);
				
			}
		}
		*/
		
	}
	
}
