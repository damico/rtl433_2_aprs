package org.jdamico.rtl433toaprs.entities;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherStationDataEntity {

	@SerializedName("time")
	@Expose
	private String time;
	@SerializedName("model")
	@Expose
	private String model;
	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("battery_ok")
	@Expose
	private Integer batteryOk;
	@SerializedName("temperature_C")
	@Expose
	private Double temperatureC;
	@SerializedName("humidity")
	@Expose
	private Integer humidity;
	@SerializedName("wind_dir_deg")
	@Expose
	private Integer windDirDeg;
	@SerializedName("wind_avg_m_s")
	@Expose
	private Double windAvgMS;
	@SerializedName("wind_max_m_s")
	@Expose
	private Double windMaxMS;
	@SerializedName("rain_mm")
	@Expose
	private Double rainMm;
	@SerializedName("uv")
	@Expose
	private Integer uv;
	@SerializedName("uvi")
	@Expose
	private Integer uvi;
	@SerializedName("light_lux")
	@Expose
	private Double lightLux;
	@SerializedName("mic")
	@Expose
	private String mic;
	
	private int messageCount;
	private Date initDate;

	private Double windMaxMH;
	
	private Double windAvgMH;
	
	private Double temperatureF;
	
	private Double rainIn;
	
	private Double pastHourRainIn;
	
	private Double pastHourRainMM;
	
	private Double rainMmSinceLocalMidnight;
	
	private Double rainInSinceLocalMidnight;
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBatteryOk() {
		return batteryOk;
	}

	public void setBatteryOk(Integer batteryOk) {
		this.batteryOk = batteryOk;
	}

	public Double getTemperatureC() {
		return temperatureC;
	}

	public void setTemperatureC(Double temperatureC) {
		this.temperatureC = temperatureC;
	}

	public Integer getHumidity() {
		return humidity;
	}

	public void setHumidity(Integer humidity) {
		this.humidity = humidity;
	}

	public Integer getWindDirDeg() {
		return windDirDeg;
	}

	public void setWindDirDeg(Integer windDirDeg) {
		this.windDirDeg = windDirDeg;
	}

	public Double getWindAvgMS() {
		return windAvgMS;
	}

	public void setWindAvgMS(Double windAvgMS) {
		this.windAvgMS = windAvgMS;
	}

	public Double getWindMaxMS() {
		return windMaxMS;
	}

	public void setWindMaxMS(Double windMaxMS) {
		this.windMaxMS = windMaxMS;
	}

	public Double getRainMm() {
		return rainMm;
	}

	public void setRainMm(Double rainMm) {
		this.rainMm = rainMm;
	}

	public Integer getUv() {
		return uv;
	}

	public void setUv(Integer uv) {
		this.uv = uv;
	}

	public Integer getUvi() {
		return uvi;
	}

	public void setUvi(Integer uvi) {
		this.uvi = uvi;
	}

	public Double getLightLux() {
		return lightLux;
	}

	public void setLightLux(Double lightLux) {
		this.lightLux = lightLux;
	}

	public String getMic() {
		return mic;
	}

	public void setMic(String mic) {
		this.mic = mic;
	}

	public WeatherStationDataEntity toImperial() {
		setWindAvgMH(windAvgMS*2.23694);
		setWindMaxMH(windMaxMS*2.23694);
		setTemperatureF( (temperatureC*9/5)+32 );
		setRainIn(rainMm != null ? (rainMm/25.4)*100 : 0); //in hundredths of inches
		setPastHourRainIn(pastHourRainMM !=null ? (pastHourRainMM/25.4)*100 : 0); //in hundredths of inches
		setRainInSinceLocalMidnight(rainMmSinceLocalMidnight != null ? (rainMmSinceLocalMidnight/25.4)*100 : 0); //in hundredths of inches
		
		System.out.println("getRainIn: "+ getRainIn()+" | getPastHourRainIn: "+getPastHourRainIn()+" | getRainInSinceLocalMidnight: "+getRainInSinceLocalMidnight());
		
		return this;
	}

	public Double getWindAvgMH() {
		return windAvgMH;
	}

	public void setWindAvgMH(Double windAvgMH) {
		this.windAvgMH = windAvgMH;
	}

	public Double getWindMaxMH() {
		return windMaxMH;
	}

	public void setWindMaxMH(Double windMaxMH) {
		this.windMaxMH = windMaxMH;
	}

	public Double getTemperatureF() {
		return temperatureF;
	}

	public void setTemperatureF(Double temperatureF) {
		this.temperatureF = temperatureF;
	}

	public Double getRainIn() {
		return rainIn;
	}

	public void setRainIn(Double rainIn) {
		this.rainIn = rainIn;
	}

	public Double getPastHourRainMM() {
		return pastHourRainMM;
	}

	public void setPastHourRainMM(Double pastHourRainMM) {
		this.pastHourRainMM = pastHourRainMM;
	}

	public Double getPastHourRainIn() {
		return pastHourRainIn;
	}

	public void setPastHourRainIn(Double pastHourRainIn) {
		this.pastHourRainIn = pastHourRainIn;
	}

	public Double getRainMmSinceLocalMidnight() {
		return rainMmSinceLocalMidnight;
	}

	public void setRainMmSinceLocalMidnight(Double rainMmSinceLocalMidnight) {
		this.rainMmSinceLocalMidnight = rainMmSinceLocalMidnight;
	}

	public Double getRainInSinceLocalMidnight() {
		return rainInSinceLocalMidnight;
	}

	public void setRainInSinceLocalMidnight(Double rainInSinceLocalMidnight) {
		this.rainInSinceLocalMidnight = rainInSinceLocalMidnight;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public Date getInitDate() {
		return initDate;
	}

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

}