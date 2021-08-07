package org.jdamico.rtl433toaprs;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.SysexMessage;

import org.jdamico.gpsd.client.GpsdClientRuntime;
import org.jdamico.javax25.soundcard.Soundcard;
import org.jdamico.rtl433toaprs.entities.ConfigEntity;
import org.jdamico.rtl433toaprs.helpers.BasicHelper;
import org.jdamico.rtl433toaprs.helpers.ProcessBuilderHelper;

import com.google.gson.Gson;


/*
 *  The underscore "_" followed by 3 numbers represents wind direction in degrees from true north. This is the direction that the wind is blowing from. 
 *  The slash "/" followed by 3 numbers represents the average wind speed in miles per hour. 
 *  The letter "g" followed by 3 numbers represents the peak instaneous value of wind in miles per hour. 
 *  The letter "t" followed by 3 characters (numbers and minus sign) represents the temperature in degrees F. 
 *  The letter "r" followed by 3 numbers represents the amount of rain in hundredths of inches that fell the past hour. 
 *  The letter "p" followed by 3 numbers represents the amount of rain in hundredths of inches that fell in the past 24 hours. 
 *  Only these two precipitation values are accepted by MADIS. 
 *  The letter "P" followed by 3 numbers represents the amount of rain in hundredths of inches that fell since local midnight. 
 *  The letter "b" followed by 5 numbers represents the barometric pressure in tenths of a millibar. 
 *  The letter "h" followed by 2 numbers represents the relative humidity in percent, where "h00" implies 100% RH. 
 *  The first four fields (wind direction, wind speed, temperature and gust) are required, in that order, and if a particular measurement is not present, the three numbers should be replaced by "..." to indicate no data available.
 */

public class App {

	public static File lockFile; 
	public static String pid;
	public static void main( String[] args ){


		String lockFilePath = "/tmp/"+Constants.APP_NAME+".lock";

		lockFile = new File(lockFilePath);

		pid = null;

		if(lockFile != null && lockFile.exists() && lockFile.isFile()) {

			try {
				String[] pStatus = BasicHelper.getInstance().readTextFileToString(lockFile).split("@");
				pid = pStatus[1];
				if(pStatus[0].equals("0")) {
					System.out.println("There is a FINE process already running: "+pid);
					System.exit(0);
				}else if(pStatus[0].equals("1")){
					BasicHelper.getInstance().posixKill("9", pid);
					lockFile.delete();
					System.out.println("There is a STUCKED process already running: "+pid+". Killing it.");

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}



		ConfigEntity configEntity = null;

		String helpInfo = "Usage parameters options A: callsign decimal_lat decimal_lng timezone \"soundcard name\"\n"
				+ "Usage parameters options B: callsign decimal_lat decimal_lng timezone \"soundcard name\" initial_rain_mm\n"
				+ "Usage parameters options C: config_filepath_name";

		if(args.length !=5 &&  args.length !=6 &&  args.length !=1) {

			System.out.println("No valid parameters for config file: "+helpInfo);

		}else {

			if(args.length == 5 || args.length == 6) {
				System.out.println("Trying to run by command-line parameters: "+printParams(args));

				Double initialRainMm = null;

				try {
					if(args.length == 6) initialRainMm = Double.parseDouble(args[5]);
				} catch (NumberFormatException e) {}

				try {
					configEntity  = new ConfigEntity(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]), Integer.parseInt(args[3]), args[4], initialRainMm);
				} catch (Exception e) {
					System.err.println("Error trying to process config parameters.");
					System.err.println("Exception at Main class: "+e.getMessage());
					e.printStackTrace();
					System.exit(0);	
				}
			} else if(args.length == 1) {
				System.out.println("Trying to run by config file: "+args[0]);
				File configFile = new File(args[0]);
				if(configFile != null && configFile.exists() && configFile.isFile()) {

					String configJsonStr = null;
					try {
						configJsonStr = BasicHelper.getInstance().readTextFileToString(configFile);
						Gson gson = new Gson();
						configEntity = gson.fromJson(configJsonStr, ConfigEntity.class);
					} catch (Exception e) {
						System.err.println("Error trying to connect to GPSD.");
						System.err.println("Exception at Main class: "+e.getMessage());
					}

				}else {
					System.out.println("No valid parameters for config file: "+helpInfo);

				}
			}else {
				System.out.println("No valid parameters for command-line call: "+helpInfo);
				System.out.println("Trying to use config file: "+args[0]);
				System.out.println("No valid parameters for config file: "+helpInfo);
			}

			try {
				if(configEntity !=null) {

					pid = BasicHelper.getInstance().getCurrentPid();
					BasicHelper.getInstance().writeStrToFile("-@"+pid, lockFilePath);
					System.out.println("My PID: "+pid);

					Soundcard.enumerate();
					ProcessBuilderHelper processBuilderHelper = new ProcessBuilderHelper(configEntity);
					int usbResetTries = 0;
					boolean isRtlDeviceFine = false;
					while(!isRtlDeviceFine && usbResetTries < Constants.USB_REST_TRIES) {
						if(!isRtlDeviceFine){
							System.out.println("Trying to reset usb device: "+usbResetTries);
							processBuilderHelper.rtlResetUsb();
							usbResetTries++;
							System.out.println("isRtlDeviceFine: "+isRtlDeviceFine);
							Thread.sleep(5000);
							isRtlDeviceFine = processBuilderHelper.rtlTestCaller();
						}else break;
					}

					if(isRtlDeviceFine){
						processBuilderHelper.rtl433Caller();
						try {
							GpsdClientRuntime gpsdClientRuntime = new GpsdClientRuntime(configEntity.getGpsdHost(), configEntity.getGpsdPort());
							//gpsdClientRuntime.connetAndCollectFromGpsD();
						}catch (IOException e) {
							System.err.println("Error trying to connect to GPSD.");
							System.err.println("Exception at Main class: "+e.getMessage());
						}
					}else {
						System.err.println("Unable to call RTL-SDR devce.");
						System.exit(1);
					}


				}else {
					System.err.println("Unable to parse configuration params.");
					System.exit(1);
				}
			} catch (Exception e) {
				System.err.println("Error trying to connect to call ProcessBuilderHelper.");
				System.err.println("Exception at Main class: "+e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}



		}
	}

	private static String printParams(String[] args) {
		StringBuffer sb = new StringBuffer();
		for (String param : args) {
			sb.append(param + " ");
		}
		return sb.toString();
	}



}
