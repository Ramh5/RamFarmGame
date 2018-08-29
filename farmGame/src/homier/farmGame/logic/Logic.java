package homier.farmGame.logic;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Logic {
	
	public static void update(GridPane grid){
		ObservableList<Node> children = grid.getChildren();
		int size = children.size();
		
		((ImageView) children.get((int)(Math.random()*(size-1)))).setImage(new Image("dirt_tile.png"));
	}
	
}
