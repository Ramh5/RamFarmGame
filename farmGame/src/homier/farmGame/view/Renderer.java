package homier.farmGame.view;

import java.util.ArrayList;

import homier.farmGame.controller.App;
import homier.farmGame.model.Tile;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Renderer {
	public static void render(ArrayList<Tile> tileList, GridPane grid) {
		for (int i = 0; i < App.gridColumns; i++) {
			for (int j = 0; j < App.gridRows; j++) {

				Tile tile = tileList.get(App.gridRows * i + j);

				Node currentImageView = new ImageView();
				try {
					ImageView newImageView = tile.getImageToRender();
					currentImageView = grid.getChildren().get(App.gridRows * i + j);
					if (!newImageView.equals(currentImageView)) {
						GridPane.setConstraints(newImageView, i, j);
						grid.getChildren().set(App.gridRows * i + j, newImageView);

					}
				} catch (IndexOutOfBoundsException e) {
					grid.getChildren().add(App.gridRows * i + j, currentImageView);
				}

			}
		}
	}// render method
}
