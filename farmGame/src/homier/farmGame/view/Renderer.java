package homier.farmGame.view;

import java.util.ArrayList;

import homier.farmGame.controller.App;
import homier.farmGame.model.Tile;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Renderer {

	public static int tileSize = 100;

	private final Image emptyTileImage = new Image("empty_tile.png", tileSize, tileSize, true, true);
	private final Image farmPlotImage = new Image("dirt_plot.png", tileSize, tileSize, true, true);
	private final Image sown1Image = new Image("sown1_plot.png", tileSize, tileSize, true, true);
	private final Image sown2Image = new Image("sown2_plot.png", tileSize, tileSize, true, true);
	private final Image wheat1Image = new Image("wheat1_plot.png", tileSize, tileSize, true, true);
	private final Image wheat2Image = new Image("wheat2_plot.png", tileSize, tileSize, true, true);
	private final Image houseImage = new Image("farmhouse.png", tileSize, tileSize, true, true);
	private final Image forest1Image = new Image("summer_tile.png", tileSize, tileSize, true, true);

	private final TileViewData wheatViewData = new TileViewData(new Image[] {sown1Image,sown2Image,wheat1Image,wheat2Image}, new int[] {0,25,50,90});
	private final TileViewData houseViewData = new TileViewData(new Image[] { houseImage }, new int[] { 0 });
	private final TileViewData forestViewData = new TileViewData(new Image[] { forest1Image }, new int[] { 0 });
	private final TileViewData farmPlotViewData = new TileViewData(new Image[] { farmPlotImage }, new int[] { 0 });

	private int[] previousMap;

	
	public Renderer(ArrayList<Tile> tileList, GridPane grid){
		init(tileList,  grid);
	}
	
	public void init(ArrayList<Tile> tileList, GridPane grid){
		previousMap = new int[tileList.size()];
	
		for (int i = 0; i < App.gridColumns; i++) {
			for (int j = 0; j < App.gridRows; j++) {
				int index = App.gridRows * i + j;
				previousMap[index] = -1;

				
				grid.getChildren().add(new ImageView(emptyTileImage));
			}//for j
		}//for i
	}
	
	public void render(ArrayList<Tile> tileList, GridPane grid) {

		for (int i = 0; i < App.gridColumns; i++) {
			for (int j = 0; j < App.gridRows; j++) {
				int index = App.gridRows * i + j;
				Tile tile = tileList.get(index);
				String tileID = tile.getID();
				int newIndexToRender;
				
				switch (tileID) {
				case "FOREST_TILE":
					newIndexToRender = forestViewData.getIndexToRender(0);
					if(newIndexToRender!=previousMap[index]){
						ImageView newImageView = forestViewData.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;
					}
					break;
				case "HOUSE_TILE":
					newIndexToRender = houseViewData.getIndexToRender(0);
					if(newIndexToRender!=previousMap[index]){
						ImageView newImageView = houseViewData.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;
					}
					break;
				case "FARM_PLOT":
					newIndexToRender = farmPlotViewData.getIndexToRender(0);
					if(newIndexToRender!=previousMap[index]){
						ImageView newImageView = farmPlotViewData.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;
					}
					break;
				case "WHEAT_PLOT":
					newIndexToRender = wheatViewData.getIndexToRender(0);
					if(newIndexToRender!=previousMap[index]){
						ImageView newImageView = wheatViewData.getImageToRender(newIndexToRender);
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(index, newImageView);
						previousMap[index] = newIndexToRender;
					
					}
				
					break;
				default:
					break;
				}//switch
			}//for j
		}//for i
		/*
		 * for (int i = 0; i < App.gridColumns; i++) { for (int j = 0; j <
		 * App.gridRows; j++) {
		 * 
		 * 
		 * 
		 * Node currentImageView = new ImageView(); try { ImageView newImageView
		 * = tile.getImageToRender(); currentImageView =
		 * grid.getChildren().get(App.gridRows * i + j); if
		 * (!newImageView.equals(currentImageView)) {
		 * GridPane.setConstraints(newImageView, i, j);
		 * grid.getChildren().set(App.gridRows * i + j, newImageView);
		 * 
		 * } } catch (IndexOutOfBoundsException e) {
		 * grid.getChildren().add(App.gridRows * i + j, currentImageView); }
		 * 
		 * } }
		 */
	}// render method
	

}
