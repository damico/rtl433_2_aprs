package org.jdamico.rtl433toaprs;

import java.io.File;

import org.jdamico.gpsd.client.GpsdClientRuntime;
import org.jdamico.javax25.soundcard.Soundcard;
import org.jdamico.rtl433toaprs.entities.ConfigEntity;
import org.jdamico.rtl433toaprs.helpers.IOHelper;
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
	public static void main( String[] args ){


		ConfigEntity configEntity = null;

		String helpInfo = "Usage parameters options A: callsign decimal_lat decimal_lng timezone \"soundcard name\"\n"
						+ "Usage parameters options B: callsign decimal_lat decimal_lng timezone \"soundcard name\" initial_rain_mm";
		
		if(args.length !=5 &&  args.length !=6) {
			
			
			
			System.out.println("No valid parameters for command-line call: "+helpInfo);
			System.out.println("Trying to use config file.");
			
			File configFile = new File("gpsd.client.json");
			if(configFile != null && configFile.exists() && configFile.isFile()) {
				
				String configJsonStr = null;
				try {
					configJsonStr = IOHelper.getInstance().readTextFileToString(configFile);
					Gson gson = new Gson();
					configEntity = gson.fromJson(configJsonStr, ConfigEntity.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
	
				
			}
			
		}else {

			try {
				Soundcard.enumerate();
				
				try {
					configEntity  = new ConfigEntity(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]), Integer.parseInt(args[3]), args[4], Double.parseDouble(args[5]));
				} catch (Exception e) {}
				
				
				GpsdClientRuntime gpsdClientRuntime = new GpsdClientRuntime(configEntity.getGpsdHost(), configEntity.getGpsdPort());
				gpsdClientRuntime.connetAndCollectFromGpsD();
				
				ProcessBuilderHelper processBuilderHelper = new ProcessBuilderHelper(configEntity);
				processBuilderHelper.caller();
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



}
