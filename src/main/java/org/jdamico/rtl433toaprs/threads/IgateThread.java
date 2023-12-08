package org.jdamico.rtl433toaprs.threads;

import org.jdamico.jbasigate.IgateComponent;

public class IgateThread extends Thread {
	private String callSign;
	private String igateHost;
	private Integer igatePort;
	private String igatePasscode;
	private Double latitude;
	private Double longitude;
	public IgateThread(String callSign, String igateHost, Integer igatePort, String igatePasscode, Double latitude, Double longitude) {
		this.callSign = callSign.split("-")[0]+"-10";
		this.igateHost = igateHost;
		this.igatePort = igatePort;
		this.igatePasscode = igatePasscode;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public void run() {
		IgateComponent igateComponent = new IgateComponent();
		igateComponent.controller(igateHost, igatePort, callSign, igatePasscode, latitude, longitude);
	}
}
