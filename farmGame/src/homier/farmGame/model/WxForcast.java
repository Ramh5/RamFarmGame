package homier.farmGame.model;

import java.util.Random;

import homier.farmGame.utils.GameClock;

public class WxForcast {
	private Weather today;
	private Weather tomorrow;
	private Random random;
	private GameClock gameClock;
	
	public WxForcast(GameClock gameClock){
		this.gameClock=gameClock;
		random = new Random();
		today = randomWx(gameClock.getMonth());
		tomorrow = randomWx(tomorrowMonth());
	}
	
	public void forcastNewDay() {
		today = tomorrow;
		//TODO Fix: raining during winter
		tomorrow = randomWx(tomorrowMonth());
	}
	
	private int tomorrowMonth(){
		int month = gameClock.getMonth();
		if(gameClock.getDay()==12){
			month++;
			month = month%12;
		}
		return month;
	}
	
	private Weather randomWx(int month){
		return new Weather(newTemp(month), Sky.values()[random.nextInt(Sky.values().length)], 
				Wind.values()[random.nextInt(Wind.values().length)]);
	}
	
	private int newTemp(int month){
		int[] baseTemp = {-15,-10,-2,2,10,15,20,15,12,7,2,-7};
		int newTemp= (int)(baseTemp[month]+3*random.nextGaussian());
		return newTemp;
	}
	
	public Weather getToday(){
		return today;
	}
	
	public Weather getTomorrow(){
		return tomorrow;
	}

	public String toString(){
		return "weatherForcast";
	}
}
