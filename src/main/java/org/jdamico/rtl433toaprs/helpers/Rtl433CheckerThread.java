package org.jdamico.rtl433toaprs.helpers;


public class Rtl433CheckerThread extends Thread {
	private ProcessBuilderHelper processBuilderHelper;
	public Rtl433CheckerThread(ProcessBuilderHelper processBuilderHelper) {
		this.processBuilderHelper = processBuilderHelper;	
	}

	public void run() {
		int count = 0;
		while(!ProcessBuilderHelper.rtl433Fine) {
			try {
				System.out.println("No valid RTL return: "+count);
				if(count == 59) {
					System.out.println("Trying to destroy RTL process.");
					Process process = processBuilderHelper.getRtlProcess();
					if(process != null) {
						process.destroyForcibly();
						System.out.println("RTL Process destroyed.");
						Rtl433CallerThread rtl433CallerThread = new Rtl433CallerThread(processBuilderHelper);
						rtl433CallerThread.start();
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
