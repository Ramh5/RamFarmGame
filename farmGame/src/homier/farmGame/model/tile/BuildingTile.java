package homier.farmGame.model.tile;

import java.util.Arrays;

import java.util.Map.Entry;

import homier.farmGame.model.Inventory;
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

	public void cook(String recipeName, Inventory inventory){
		Recipe recipe = recipeBook.getRecipeList().get(recipeName);
		//generate default freshness request
		int[] freshReqList = new int[recipe.getIngredientList().size()];
		Arrays.fill(freshReqList, 10);
		
		int[] result = inventory.removeList(recipe.getIngredientList(), freshReqList);
		if(result[0]==0) {
			return;
		}
		//average the freshness of every ingredient
		double averageFresh = 0;
		double total = 0;
		int i=0;
		for(Entry<String, Double> entry:recipe.getIngredientList().entrySet()){
			averageFresh += entry.getValue()*result[i];
			total += entry.getValue();	
			i++;
		}
		averageFresh/=total;
		inventory.addProd(recipeName, recipe.getQuantity(), (int)averageFresh );
		
	
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
