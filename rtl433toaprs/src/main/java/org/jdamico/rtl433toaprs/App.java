package org.jdamico.rtl433toaprs;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.jdamico.javax25.soundcard.Soundcard;
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

public class App 
{
	public static void main( String[] args ){





		String helpInfo = "Usage parameters: callsign decimal_lat decimal_lng timezone \"soundcard name\"";
		if(args.length != 5) {
			System.err.println("Incorrect usage. "+helpInfo);
		}else {

			try {
				Soundcard.enumerate();
				ProcessBuilderHelper processBuilderHelper = new ProcessBuilderHelper(args[0], args[1], args[2], args[3], args[4]);
				processBuilderHelper.caller();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



}
