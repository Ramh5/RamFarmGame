package homier.farmGame;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import rs.homier.farmGame.Game;
import rs.homier.farmGame.tile.Tile;

/**
 * Grid extends Canvas
 * @author Ram
 * 
 */
public class Grid extends Canvas{
	

	private Tile[][] grid;
	
	public Grid(int width, int height) {
		super(width,height);
		grid = new Tile[Game.gridRows][Game.gridColumns];
		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[i].length;j++){
				grid[i][j]= new Tile();
			}
		}
	}
	
	public void render(){
		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[i].length;j++){
				this.getGraphicsContext2D().drawImage(grid[i][j].getImage(), 100*i,100*j);
			}
		}
	}
}
