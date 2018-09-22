package homier.farmGame.utils;

public enum FarmTimeUnits{
	//based on 300 seconds in a day 14 hours is 1 day, 12 days is 1 month, 12 months is one year
	SECOND(1),HOUR(300/14),DAY(300),MONTH(300*12),YEAR(300*12*12);
	public double seconds;
	
	FarmTimeUnits(double seconds){
		this.seconds = seconds;
	}
}