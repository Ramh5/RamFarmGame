package homier.farmGame.utils;

import java.util.ArrayList;

import homier.farmGame.model.tile.FarmPlot;
import homier.farmGame.model.tile.Tile;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextFlowDataSet {
	
	private Tile tile;
	private boolean isOn;
	private TextFlow textFlow;
	private ArrayList<Text> textList;
	
	
	public TextFlowDataSet(Tile tile, boolean isOn, TextFlow textFlow){
		this.tile=tile;
		this.isOn=isOn;
		this.textFlow=textFlow;
		textList=new ArrayList<>();
	}
	
	public void init(){
		if(tile.getType()=="FarmPlot"){
			FarmPlot farmTile = (FarmPlot)tile;
			for(int i=0;i<8;i++){
				textList.add(new Text());
			}
			textFlow.setId("MSTextFlow");
		}


	}

	public void update(){
		if(tile.getType()=="FarmPlot"){
			if(textList.size()==0){
				init();
			}
			FarmPlot farmTile = (FarmPlot)tile;
			if(isOn){
				textList.get(0).setText("Seed name: " + farmTile.getSeed().getName() +"\n");
				textList.get(1).setText(String.format("Growth:%2.0f%%", farmTile.getGrowth()));
				textList.get(2).setText(String.format("   Yield:%3.0fkg", farmTile.getYield()));
				textList.get(3).setText(String.format("   Quality:%3d%%", farmTile.getQual()));
				textList.get(4).setText(String.format("   Growth today:%2.0f%%\n", farmTile.getGrowthFactor()));
				textList.get(5).setText(String.format("Irrigation:%3.0f", farmTile.getWaterLevel()));
				textList.get(6).setText(String.format("   Yield penalty:%3.0fkg\n", farmTile.getYieldPenalty()));
				textList.get(7).setText(farmTile.getSeed().toString());

			}else{
				textList.get(0).setText("Seed name:\n");
				textList.get(1).setText("Growth:");
				textList.get(2).setText("      Yield:");
				textList.get(3).setText("        Quality:");
				textList.get(4).setText("       Growth today:\n");
				textList.get(5).setText("Irrigation:");
				textList.get(6).setText("      Yield penalty:\n");
				textList.get(7).setText("Mouse over seed details...");
			}
			textFlow.getChildren().setAll(textList);
			textFlow.getChildren().add(7, new Line(0, 0, 200, 0));
			textFlow.getChildren().add(8, new Text("\n"));
			
		}

		
	}

	public void on() {
		this.isOn=true;
		
	}
	public void off(){
		this.isOn=false;
	}
	
	
}
