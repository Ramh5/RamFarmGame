package homier.farmGame;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;


/**
 * Grid extends Canvas
 * @author Ram
 * 
 */
public class Grid extends Canvas{
	

	private GridPane grid;
	
	public Grid(int width, int height) {
		super(width,height);
		grid = new GridPane();//[Game.gridRows][Game.gridColumns];
		for(int i=0;i<Game.gridColumns;i++){
			for(int j=0;j<Game.gridRows;j++){
				grid.add(new Tile().getImageView(), i, j);
			}
		}
		//set mouse click action for a particular node
		/*
		 grid.getChildren().get(11).setOnMouseClicked(e->{
			//grid.setPadding(new Insets(10));
		});
		*/
		
	}
	
	public GridPane getGrid(){
		return grid;
	}
	
	/*
	public void render(){
		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[i].length;j++){
				this.getGraphicsContext2D().drawImage(grid[i][j].getImage(), 100*i,100*j);
			}
		}
		
	}
	*/
}
