package homier.farmGame.utils;



public class GameClock {
	private int secondsInADay;
	private double totalSeconds;
	private int year;
	private int month;
	private int day;
	private int hour;
	
	public GameClock(int secondsInADay, double totalSeconds){
		this.secondsInADay = secondsInADay;
		this.totalSeconds=totalSeconds;
	}
	
	public void update(double dTime){
		
		//based on 5 minutes (when secondsInADay = 300s) real time is 14 hours is 1 day, 12 days is 1 month, 12 months is one year
		totalSeconds += dTime;
		year = (int)(totalSeconds/secondsInADay/12/12) +1 ;
		month = (int)(totalSeconds%(secondsInADay*12*12)/secondsInADay/12);
		day = (int)(totalSeconds%(secondsInADay*12)/secondsInADay) + 1;
		hour = (int)(totalSeconds%(secondsInADay)/secondsInADay*14) + 6; //+6 to start morning at 6AM
	}
	
	
	
	public String toString(){
		return  (day) +" "+ monthStr(month) +", " + (hour) + "h  |  " + "Annee: " + (year) ;
	}
	
	//helper method to get the month string from a month number
	private static String monthStr(int monthNum){
		String[] monthList = new String[]{"Jan","Fev","Mar","Avr","Mai","Jun","Jul","Aou","Sep","Oct","Nov","Dec"};
		return monthList[monthNum];
	}

	
	public int getSecondsInADay() {
		return secondsInADay;
	}

	public void setSecondsInADay(int secondsInADay) {
		this.secondsInADay = secondsInADay;
	}

	public double getTotalSeconds() {
		return totalSeconds;
	}

	public void setTotalSeconds(double totalSeconds) {
		this.totalSeconds = totalSeconds;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}
	
}
