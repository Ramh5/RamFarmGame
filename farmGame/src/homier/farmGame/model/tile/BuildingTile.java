package homier.farmGame.model.tile;

import java.util.Arrays;

import java.util.Map.Entry;

import homier.farmGame.model.Inventory;
import homier.farmGame.model.Product;
import homier.farmGame.model.Recipe;
import homier.farmGame.model.RecipeBook;
import homier.farmGame.model.Weather;

public class BuildingTile extends Tile{

	private RecipeBook recipeBook;
	private int state;//level of the house
	
	public BuildingTile(String ID) {
		super(ID);
		state=0;
		recipeBook=new RecipeBook();
	}

	
	@Override
	public void update(double dTime, Weather wx) {
		
	}
	
	
	public int getState(){
		return state;
	}
	
	public void setState(int state){
		this.state = state;
	}
	
	public RecipeBook getRecipeBook(){
		return recipeBook;
	}
	 
}
