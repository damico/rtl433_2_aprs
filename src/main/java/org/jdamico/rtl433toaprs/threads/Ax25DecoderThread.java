package org.jdamico.rtl433toaprs.threads;

import org.jdamico.javax25.soundcard.Soundcard;

public class Ax25DecoderThread extends Thread {
	private Soundcard sc;
	public Ax25DecoderThread(Soundcard sc) {
		this.sc = sc;
	}
	public void run() {
		if (sc != null) {
			System.out.printf("Listening for packets\n");
			//sc.openSoundInput(input);			
			sc.receive();
		}else {
			System.err.println("Input is null!");
		}
	}

}
