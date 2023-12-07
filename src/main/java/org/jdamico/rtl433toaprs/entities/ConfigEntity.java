package org.jdamico.rtl433toaprs.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/*
 * 
 
 {
	"callsign": "",
	"decimal_lat": 1,
	"decimal_lng": 1,
	"timezone": 1,
	"soundcard_name": "",
	"initial_rain_mm": 1,
	"gpsd_host": "",
	"gpsd_port": "",
	"station_name": "SciCrop SE-2900",
	"rtl_433_cli": "/usr/local/bin/rtl_433 -F json"
	"loop_interval_minutes": 30
}
 
 * 
 */

public class ConfigEntity {
	
	
	@SerializedName("destination")
	@Expose
	private String destination;
	
	@SerializedName("digi_path")
	@Expose
	private String digiPath;


	@SerializedName("rlt_usb_device")
	@Expose
	private String rtlUsbDevice;
	

	@SerializedName("rtl_433_cli")
	@Expose
	private String rtl433Cli;
	
	@SerializedName("running_path")
	@Expose
	private String runningPath;
	
	@SerializedName("is_igate")
	@Expose
	private Boolean isIgate;
	
	@SerializedName("igate_host")
	@Expose
	private String igateHost;
	
	@SerializedName("igate_port")
	@Expose
	private Integer igatePort;
	
	@SerializedName("igate_passcode")
	@Expose
	private String igatePasscode;
	
	@SerializedName("station_name")
	@Expose
	private String stationName;
	
	@SerializedName("callsign")
	@Expose
	private String callsign;
	@SerializedName("decimal_lat")
	@Expose
	private Double decimalLat;
	@SerializedName("decimal_lng")
	@Expose
	private Double decimalLng;
	@SerializedName("timezone")
	@Expose
	private Integer timezone;
	
	@SerializedName("soundcard_input")
	@Expose
	private String soundcardInput;

	@SerializedName("soundcard_output")
	@Expose
	private String soundcardOutput;
	
	@SerializedName("initial_rain_mm")
	@Expose
	private Double initialRainMm;
	@SerializedName("gpsd_host")
	@Expose
	private String gpsdHost;
	@SerializedName("gpsd_port")
	@Expose
	private Integer gpsdPort;
	
	@SerializedName("loop_interval_minutes")
	@Expose
	private Integer loopIntervalMinutes;

	public Integer getLoopIntervalMinutes() {
		return loopIntervalMinutes;
	}

	public void setLoopIntervalMinutes(Integer loopIntervalMinutes) {
		this.loopIntervalMinutes = loopIntervalMinutes;
	}

	public String getCallsign() {
		return callsign;
	}

	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}

	public Double getDecimalLat() {
		return decimalLat;
	}

	public void setDecimalLat(Double decimalLat) {
		this.decimalLat = decimalLat;
	}

	public Double getDecimalLng() {
		return decimalLng;
	}

	public void setDecimalLng(Double decimalLng) {
		this.decimalLng = decimalLng;
	}

	public Integer getTimezone() {
		return timezone;
	}

	public void setTimezone(Integer timezone) {
		this.timezone = timezone;
	}

	public String getSoundcardOutput() {
		return soundcardOutput;
	}

	public void setSoundcardOutput(String soundcardOutput) {
		this.soundcardOutput = soundcardOutput;
	}

	
	
	public String getSoundcardInput() {
		return soundcardInput;
	}

	public void setSoundcardInput(String soundcardInput) {
		this.soundcardInput = soundcardInput;
	}

	public Double getInitialRainMm() {
		return initialRainMm;
	}

	public void setInitialRainMm(Double initialRainMm) {
		this.initialRainMm = initialRainMm;
	}

	public String getGpsdHost() {
		return gpsdHost;
	}

	public void setGpsdHost(String gpsdHost) {
		this.gpsdHost = gpsdHost;
	}

	public Integer getGpsdPort() {
		return gpsdPort;
	}

	public void setGpsdPort(Integer gpsdPort) {
		this.gpsdPort = gpsdPort;
	}

	public ConfigEntity(String callsign, Double decimalLat, Double decimalLng, Integer timezone, String soundcardName,
			Double initialRainMm, String gpsdHost, Integer gpsdPort) {
		super();
		this.callsign = callsign;
		this.decimalLat = decimalLat;
		this.decimalLng = decimalLng;
		this.timezone = timezone;
		this.soundcardOutput = soundcardName;
		this.initialRainMm = initialRainMm;
		this.gpsdHost = gpsdHost;
		this.gpsdPort = gpsdPort;
	}

	public ConfigEntity(String callsign, Double decimalLat, Double decimalLng, Integer timezone, String soundcardName,
			Double initialRainMm) {
		super();
		this.callsign = callsign;
		this.decimalLat = decimalLat;
		this.decimalLng = decimalLng;
		this.timezone = timezone;
		this.soundcardOutput = soundcardName;
		this.initialRainMm = initialRainMm;
	}
	
	public ConfigEntity() {
		super();
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getRtl433Cli() {
		return rtl433Cli;
	}

	public void setRtl433Cli(String rtl433Cli) {
		this.rtl433Cli = rtl433Cli;
	}

	public String getRunningPath() {
		return runningPath;
	}

	public void setRunningPath(String runningPath) {
		this.runningPath = runningPath;
	}

	public String getRtlUsbDevice() {
		return rtlUsbDevice;
	}

	public void setRtlUsbDevice(String rtlUsbDevice) {
		this.rtlUsbDevice = rtlUsbDevice;
	}

	public String getDigiPath() {
		return digiPath;
	}

	public void setDigiPath(String digiPath) {
		this.digiPath = digiPath;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Boolean getIsIgate() {
		return isIgate;
	}

	public void setIsIgate(Boolean isIgate) {
		this.isIgate = isIgate;
	}

	public String getIgateHost() {
		return igateHost;
	}

	public void setIgateHost(String igateHost) {
		this.igateHost = igateHost;
	}

	public Integer getIgatePort() {
		return igatePort;
	}

	public void setIgatePort(Integer igatePort) {
		this.igatePort = igatePort;
	}

	public String getIgatePasscode() {
		return igatePasscode;
	}

	public void setIgatePasscode(String igatePasscode) {
		this.igatePasscode = igatePasscode;
	}

	

}