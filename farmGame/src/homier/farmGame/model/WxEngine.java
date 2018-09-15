package homier.farmGame.model;

enum Sky{
	SKC(1),CLOUD1(.9),CLOUD2(.8),RAIN1(.7),RAIN2(.6);
	private double factor;
	
	Sky(double factor){
		this.factor=factor;
	}
	
	public double getFactor(){
		return factor;
	}
	
}

enum Wind{
	WIND0,WIND1,WIND2;	
}

public class WxEngine {
	private double temp;
	private Sky sky;
	private Wind wind;
	
	
	public WxEngine(double temp, Sky sky, Wind wind){
		this.temp=temp;
		this.sky=sky;
		this.wind=wind;
	}

	public void update(){
		//TODO create a wheather simulation vs day of the year and probablilities, random, etc
		//to be updated every day
	}
	
	//attempt at a gaussian distribution for the growth factor vs temperature
	public double getFactor(int[] tempRange){ 
		double factor;
		
		int r1 = tempRange[0];
		int r2 = tempRange[1];
		double mean = (r2+r1)/2.0;
		double variance = Math.pow((r2-r1)/1.5, 2);
		
		factor = Math.exp(-Math.pow(temp-mean,2)/(2*variance))*sky.getFactor();
		
		
		System.out.println(factor);
		return factor;
	}
	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public Sky getSky() {
		return sky;
	}

	public void setSky(Sky sky) {
		this.sky = sky;
	}

	public Wind getWind() {
		return wind;
	}

	public void setWind(Wind wind) {
		this.wind = wind;
	}
}
