package org.jdamico.rtl433toaprs.helpers;


public class Rtl433CheckerThread extends Thread {
	private ProcessBuilderHelper processBuilderHelper;
	public Rtl433CheckerThread(ProcessBuilderHelper processBuilderHelper) {
		this.processBuilderHelper = processBuilderHelper;
		this.processBuilderHelper.rtlTestCaller();	
	}

	public void run() {
		int count = 0;
		while(!ProcessBuilderHelper.rtl433Fine) {
			try {
				if(count == 60) {
					System.out.println("Trying to destroy RTL process.");
					Process process = processBuilderHelper.getRtlProcess();
					if(process != null) {
						process.destroy();
						System.out.println("RTL Process destroyed.");
						processBuilderHelper.rtlTestCaller();
						count = 0;
						System.out.println("New RTL process started.");
					}
					
				}
				Thread.sleep(1000);
				count++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("RTL Process is running fine.");
	}
	
}
