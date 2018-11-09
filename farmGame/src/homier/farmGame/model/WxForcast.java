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
	
	/**
	 * generate a random weather given the month, converts rain into snow if temperature negative
	 * @param month
	 * @return
	 */
	private Weather randomWx(int month){
		Weather wx = new Weather(newTemp(month), Sky.values()[random.nextInt(Sky.values().length-2)], 
				Wind.values()[random.nextInt(Wind.values().length)]);
		if(wx.getTemp()<0){
			if(wx.getSky()==Sky.RAIN1){
				wx.setSky(Sky.SNOW1);
			}else if(wx.getSky()==Sky.RAIN2){
				wx.setSky(Sky.SNOW2);
			}
		}
		return wx;
	}
	
	private int newTemp(int month){
		int[] baseTemp = {-6,-6,2,9,16,21,25,23,17,12,6,-1};
		int newTemp= (int)(baseTemp[month]+2*random.nextGaussian());
		return newTemp;
	}
	
	public Weather getToday(){
		return today;
	}
	
	public Weather getTomorrow(){
		return tomorrow;
	}

	public void setGameClock(GameClock gameClock){
		this.gameClock=gameClock;
	}
	public String toString(){
		return "weatherForcast";
	}
}
