package homier.farmGame.model.tile;

import homier.farmGame.model.Inventory;
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

	public void cook(String recipe, Inventory inventory){
		
		boolean success=inventory.removeList(recipeBook.getRecipeList().get(recipe).getIngredientList());
		if(success)
		inventory.addProd(recipe, recipeBook.getRecipeList().get(recipe).getQuantity());
		System.out.println("cooking success?"+success);
	
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
