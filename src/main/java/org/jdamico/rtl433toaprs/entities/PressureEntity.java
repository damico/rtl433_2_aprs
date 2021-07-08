package org.jdamico.rtl433toaprs.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PressureEntity {

	@SerializedName("pressure")
	@Expose
	private Double pressure;
	
	@SerializedName("datetime")
	@Expose
	private String datetime;

	public Double getPressure() {
		return pressure;
	}

	public void setPressure(Double pressure) {
		this.pressure = pressure;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	
}
