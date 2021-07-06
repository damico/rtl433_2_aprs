package org.jdamico.rtl433toaprs;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

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
		String helpInfo = "Usage parameters: json_file_path callsign decimal_lat decimal_lng timezone";
		if(args.length != 5) {
			System.err.println("Incorrect usage. "+helpInfo);
		}else {
			String jsonFilePath = args[0];
			File jsonFile = new File(jsonFilePath);
			try {


				String jsonStr = Helpers.getInstance().readTextFileToString(jsonFile);
				jsonStr = jsonStr.split("\\}")[0]+"}";
				Gson gson = new Gson();
				WeatherStationDataEntity entity = gson.fromJson(jsonStr, WeatherStationDataEntity.class);
				entity = entity.toImperial();


				Date now = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(now);

				double latitude = Double.parseDouble(args[2]);
				double longitude = Double.parseDouble(args[3]);
				int tz = Integer.parseInt(args[4]);
				

				System.out.println(
						"@"+String.format("%02d" , cal.get(Calendar.DAY_OF_MONTH))
						+(cal.get(Calendar.HOUR_OF_DAY)-tz)
						+cal.get(Calendar.MINUTE)
						+"z"+toString(latitude, longitude)
						+"_"+entity.getWindDirDeg()
						+"/"+String.format("%03d" , entity.getWindAvgMH().intValue())
						+"g"+String.format("%03d" , entity.getWindMaxMH().intValue())
						+"t"+String.format("%03d" , entity.getTemperatureF().intValue())
						+"r..."
						+"p"+String.format("%03d" , entity.getRainIn().intValue())
						+"P..."
						+"b..."
						+"h"+String.format("%03d" , entity.getHumidity().intValue()));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getDMS(double decimalDegree, boolean isLatitude) {
		
		Integer positionAmbiguity = 0;
		
		
		int minFrac = (int)Math.round(decimalDegree*6000); ///< degree in 1/100s of a minute
		boolean negative = (minFrac < 0);
		if (negative)
			minFrac = -minFrac;
		int deg = minFrac / 6000;
		int min = (minFrac / 100) % 60;
		minFrac = minFrac % 100;
		String ambiguousFrac;

		switch (positionAmbiguity) {
		case 1: // "dd  .  N"
			ambiguousFrac = "  .  "; break;
		case 2: // "ddm .  N"
			ambiguousFrac = String.format("%d .  ", min/10); break;
		case 3: // "ddmm.  N"
			ambiguousFrac = String.format("%02d.  ", min); break;
		case 4: // "ddmm.f N"
			ambiguousFrac = String.format("%02d.%d ", min, minFrac/10); break;
		default: // "ddmm.ffN"
			ambiguousFrac = String.format("%02d.%02d", min, minFrac); break;
		}
		if ( isLatitude ) {
			return String.format("%02d%s%s", deg, ambiguousFrac, ( negative ? "S" : "N"));
		} else {
			return String.format("%03d%s%s", deg, ambiguousFrac, ( negative ? "W" : "E"));
		}
	}

	public static String toString(double latitude, double longitude) {
		char symbolTable = '/';
		char symbolCode = '.';
		return getDMS(latitude,true)+symbolTable+getDMS(longitude,false)+symbolCode;
	}

}
