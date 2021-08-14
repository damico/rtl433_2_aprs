package org.jdamico.rtl433toaprs;

import org.jdamico.rtl433toaprs.helpers.ProcessBuilderHelper;

public class PacketTester {

	public static void main( String[] args ){
		//[PU2LVM-13>APRS,ARISS,WIDE2-1:@141244z2332.53S/04645.51W_131/003g004t067r000p000P000b10142h70SciCrop SE-2900]

		try {
			String helpusage = "This tester requires 4 or 5 parameters: \n"
					+ "Option 4 parameters: soundcardname callsign aprsmsg digpath \n"
					+ "Option 5 parameters: soundcardname callsign aprsmsg digpath numberoftimestorepeatmsg\n\n"
					+ "Example: \"Headphones [plughw:1,0]\" PU2LVM-13 @141244z2332.53S/04645.51W_131/003g004t067r000p000P000b10142h70Test ARISS,WIDE2-1 3";
			if(args.length == 4 || args.length == 5) {

				ProcessBuilderHelper processBuilderHelper = new ProcessBuilderHelper(args[0], args[1]);
				int times = 1;
				if(args[4]!=null) {
					times = Integer.parseInt(args[4]);
				}
				for (int i = 0; i < times; i++) {
					processBuilderHelper.sendPacket(args[2], args[3].replaceAll(" ", "").split(","));
					System.out.println("waiting...");
					Thread.sleep(10000);

				}
			}else {
				System.out.println(helpusage);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

}
