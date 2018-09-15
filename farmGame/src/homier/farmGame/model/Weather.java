package homier.farmGame.model;

enum Sky{
	SKC(1,"Sunny"),CLOUD1(.9,"Partly clouded"),CLOUD2(.8, "Overcast"),RAIN1(.7, "Light rain"),RAIN2(.6, "Heavy rain");
	private double factor;
	private String name;
	
	Sky(double factor, String name){
		this.factor=factor;
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	
	public double getFactor(){
		return factor;
	}
	
}

enum Wind{
	WIND0,WIND1,WIND2;	
}

public class Weather {
	private double temp;
	private Sky sky;
	private Wind wind;
	
	
	public Weather(double temp, Sky sky, Wind wind){
		this.temp=temp;
		this.sky=sky;
		this.wind=wind;
	}
	
	//attempt at a gaussian distribution for the growth factor vs temperature, 
	//maybe just a list of points and interpolation would be better, like yield vs growth in FarmPlot
	public double getFactor(int[] tempRange){ 
		double factor;
		
		int r1 = tempRange[0];
		int r2 = tempRange[1];
		double mean = (r2+r1)/2.0;
		double variance = Math.pow((r2-r1)/1.5, 2);
		
		factor = Math.exp(-Math.pow(temp-mean,2)/(2*variance))*sky.getFactor();
		
		
		System.out.println(factor);
		if(Double.isNaN(factor)) factor = 0;
		
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
	
	public String toString(){
		return String.format("%.0f\u00b0C %s", temp,sky.getName());
	}
}
