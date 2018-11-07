package homier.farmGame.utils;

import homier.farmGame.model.tile.FarmPlot;
import homier.farmGame.model.tile.Tile;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextFlowDataSet {
	
	private Tile tile;
	private boolean isOn;
	private TextFlow textFlow;
	private Text[] textList;
	
	
	public TextFlowDataSet(Tile tile, boolean isOn, TextFlow textFlow){
		this.tile=tile;
		this.isOn=isOn;
		this.textFlow=textFlow;
		textList=new Text[6];
	}
	
	public void init(){
		if(tile.getType()=="FarmPlot"){
			FarmPlot farmTile = (FarmPlot)tile;
			textList[0]=new Text("Seed name: " + farmTile.getSeed().getName());
			textList[1]=new Text(String.format("  Growth: %.0f%%", farmTile.getGrowth()));
			textList[2]=new Text(String.format("  Yield: %.0fkg\n", farmTile.getYield()));
			textList[3]=new Text(String.format("Quality: %d%%", farmTile.getQual()));
			textList[4]=new Text(String.format("  Growth today: %.0f%%\n", farmTile.getGrowthFactor()));
			textList[5]=new Text(farmTile.getSeed().toString());
		}


	}

	public void update(){
		if(tile.getType()=="FarmPlot"){
			if(textList[0]==null){
				init();
			}
			FarmPlot farmTile = (FarmPlot)tile;
			if(isOn){
				textList[0].setText("Seed name: " + farmTile.getSeed().getName());
				textList[1].setText(String.format("  Growth: %.0f%%", farmTile.getGrowth()));
				textList[2].setText(String.format("  Yield: %.0fkg\n", farmTile.getYield()));
				textList[3].setText(String.format("Quality: %d%%", farmTile.getQual()));
				textList[4].setText(String.format("  Growth today: %.0f%%\n", farmTile.getGrowthFactor()));
				textList[5].setText(farmTile.getSeed().toString());

			}else{
				textList[0].setText("Seed name:          ");
				textList[1].setText("Growth:          ");
				textList[2].setText("Yield:\n");
				textList[3].setText("Quality:          ");
				textList[4].setText("Growth today:\n");
				textList[5].setText("Mouse over seed details...");
			}
			textFlow.getChildren().setAll(textList);
			textFlow.getChildren().add(5, new Line(0, 0, 200, 0));
			textFlow.getChildren().add(6, new Text("\n"));
			//textFlow.
		}

		
	}

	public void on() {
		this.isOn=true;
		
	}
	public void off(){
		this.isOn=false;
	}
	
	
}
