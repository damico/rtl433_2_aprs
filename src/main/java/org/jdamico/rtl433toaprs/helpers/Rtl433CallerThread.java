package org.jdamico.rtl433toaprs.helpers;


public class Rtl433CallerThread extends Thread {
	private ProcessBuilderHelper processBuilderHelper;
	public Rtl433CallerThread(ProcessBuilderHelper processBuilderHelper) {
		this.processBuilderHelper = processBuilderHelper;	
	}

	public void run() {

		processBuilderHelper.rtl433Caller();

	}

}
