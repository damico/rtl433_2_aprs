package org.jdamico.rtl433toaprs.threads;

public class IgateThread extends Thread {
	private String callSign;
	private String igateHost;
	private Integer igatePort;
	private String igatePasscode;
	public IgateThread(String callSign, String igateHost, Integer igatePort, String igatePasscode, Double latitude, Double longitude) {
		this.callSign = callSign.split("-")[0]+"-10";
		this.igateHost = igateHost;
		this.igatePort = igatePort;
		this.igatePasscode = igatePasscode;
	}
}
