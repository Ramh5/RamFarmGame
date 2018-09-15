package homier.farmGame.model;

import java.util.Random;

import homier.farmGame.utils.GameClock;

public class WxForcast {
	private Weather today;
	private Weather tomorrow;
	private Random random;
	public WxForcast(){
		today = new Weather(0, Sky.SKC, Wind.WIND0);
		tomorrow = new Weather(0, Sky.SKC, Wind.WIND0);
		random = new Random();
	}
	
	public void forcastNewDay(GameClock gameClock) {
		today = tomorrow;
		
		
		//TODO fix first day of month forcast
		tomorrow = new Weather(newTemp(gameClock.getMonth()), Sky.values()[random.nextInt(Sky.values().length)], 
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
