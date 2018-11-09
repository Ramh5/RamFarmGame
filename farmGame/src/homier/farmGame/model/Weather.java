package homier.farmGame.model;

enum Sky{
	SKC("Sunny",1,.4),CLOUD1("Partly clouded",.9,.2),CLOUD2("Overcast",.8,0),RAIN1( "Light rain",.7,-.3),RAIN2( "Heavy rain",.6,-.7),
	SNOW1("Light snow",.7,0),SNOW2("Heavy snow",.6,0);
	private double growthFactor;
	private double dryingFactor;//additive factor
	private String name;
	
	Sky( String name,double growthFactor,double dryingFactor){
		this.growthFactor=growthFactor;
		this.dryingFactor=dryingFactor;
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	
	public double getGrowthFactor(){
		return growthFactor;
	}
	
	public double getDryingFactor(){
		return dryingFactor;
	}
	
}

enum Wind{
	WIND0("Calm winds",0),WIND1("Light winds",0.2),WIND2("Strong winds",0.5);
	private double dryingFactor;
	private String name;
	
	Wind(String name,double dryingFactor){
		this.dryingFactor=dryingFactor;
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	
	public double getDryingFactor(){
		return dryingFactor;
	}
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
	/**
	 * get the growthFactor given an ideal temperature range for the current temperature and sky condition
	 * @param tempRange
	 * @return
	 */
	public double getGrowthFactor(int[] tempRange){ 
		double factor;
		
		int r1 = tempRange[0];
		int r2 = tempRange[1];
		double mean = (r2+r1)/2.0;
		double variance = Math.pow((r2-r1)/1.5, 2);
		
		factor = Math.exp(-Math.pow(temp-mean,2)/(2*variance))*sky.getGrowthFactor();
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
	
	//TODO include the wind in the wx display
	public String toString(){
		return String.format("%.0f\u00b0C %s   %s", temp,sky.getName(),wind.getName());
	}
}
