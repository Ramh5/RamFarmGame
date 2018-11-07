package homier.farmGame.utils;

public class TextFlowManager {
	public static TextFlowDataSet farmTileTFDS;
	
//	public TextFlowManager(){
//		farmTileTFDS=new TextFlowDataSet(new FarmPlot(), false, new TextFlow());
//	}
	
	public static void update(){
		if(farmTileTFDS!=null){
			farmTileTFDS.update();
		}
		
	}
}
