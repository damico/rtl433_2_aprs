package org.jdamico.rtl433toaprs.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RainEntity {
	
	@SerializedName("initial_rain_mm")
	@Expose
	private Double initialRain;

	public RainEntity() {
		super();
	}
	
	public RainEntity(Double initialRain) {
		this.initialRain = initialRain;
	}

	public Double getInitialRain() {
		return initialRain;
	}

	public void setInitialRain(Double initialRain) {
		this.initialRain = initialRain;
	}

}
