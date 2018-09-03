package homier.farmGame.utils;

import homier.farmGame.Game;

public class Tools {

	public static String elapsedSecondsFormatter(double seconds){
		//based on 5 minutes (300s) real time is 14 hours is 1 day, 12 days is 1 month, 12 months is one year
		
		int years = (int)(seconds/Game.secondsInADay/12/12);
		int months = (int)(seconds%(Game.secondsInADay*12*12)/Game.secondsInADay/12);
		int days = (int)(seconds%(Game.secondsInADay*12)/Game.secondsInADay);
		int hours = (int)(seconds%(Game.secondsInADay)/Game.secondsInADay*14);
		
		return  (days+1) +" "+ monthStr(months) +", " + (hours+6) + "h  |  " + "Annee: " + (years+1) ;
				//years + " y " + months + " m " + days + " d " + (hours+6) + " h " + "  Real seconds: "+ (int)seconds;
	}
	
	//helper method to get the month string from a month number
	private static String monthStr(int monthNum){
		String[] monthList = new String[]{"Jan","Fev","Mar","Avr","Mai","Jun","Jul","Aou","Sep","Oct","Nov","Dec"};
		return monthList[monthNum];
	}
}


