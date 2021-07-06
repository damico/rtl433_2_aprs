package org.jdamico.rtl433toaprs;

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

	private Double windMaxMH;
	
	private Double windAvgMH;
	
	private Double temperatureF;
	
	private Double rainIn;
	
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
		setTemperatureF( (temperatureC*(9/5))+32 );
		setRainIn(rainMm/25.4);
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

}