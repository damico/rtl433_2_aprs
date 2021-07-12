package org.jdamico.rtl433toaprs.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import org.jdamico.javax25.PacketHandlerImpl;
import org.jdamico.javax25.ax25.Afsk1200Modulator;
import org.jdamico.javax25.ax25.Afsk1200MultiDemodulator;
import org.jdamico.javax25.ax25.Packet;
import org.jdamico.javax25.ax25.PacketDemodulator;
import org.jdamico.javax25.soundcard.Soundcard;
import org.jdamico.rtl433toaprs.entities.PressureEntity;
import org.jdamico.rtl433toaprs.entities.RainEntity;
import org.jdamico.rtl433toaprs.entities.WeatherStationDataEntity;

import com.google.gson.Gson;

public class ProcessBuilderHelper {

	private static int rate = 48000;
	private static int buffer_size = 100;
	private Soundcard sc = null;
	private Afsk1200Modulator mod =  null;
	private static int hour_minutes = 0;
	private static int day_minutes = 0;
	private static int lastMinute = 0;
	private Double hourRainMm;
	private static final String rainJsonPath = "dist/";
	private static final String rainJsonFilePath = rainJsonPath+"rain.json";
	private static final String pressureJsonFilePath = rainJsonPath+"pressure.json";
	private String strLat; 
	private String strLng; 
	private String strTz;
	private String callsign;
	private String soundcardName;

	public ProcessBuilderHelper(String callsign, String strLat, String strLng, String strTz, String soundcardName) {

		PacketDemodulator multi = null;

		try {
			multi = new Afsk1200MultiDemodulator(48000, new PacketHandlerImpl());
			mod = new Afsk1200Modulator(rate);
			sc = new Soundcard(rate, null, soundcardName, buffer_size, multi, mod);
			this.callsign = callsign;
			this.strLat = strLat;
			this.strLng = strLng;
			this.strTz = strTz;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		


	}

	public void caller() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("rtl_433", "-F", "json");

		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		Process process = null;
		try {

			process = processBuilder.start();
			inputStreamReader = new InputStreamReader(process.getInputStream());
			reader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("Return from RTL_433: "+line);
				jsonParser(strLat, strLng, strTz, line);
			}


		} catch (Exception e) {
			e.printStackTrace();

		}finally {
			if(reader!=null) try{ reader.close(); }catch (Exception e) {e.printStackTrace();}
			if(inputStreamReader!=null) try{ inputStreamReader.close(); }catch (Exception e) {e.printStackTrace();}
			if(process!=null) process.destroy();
		}
	}

	public void jsonParser(String strLat, String strLng, String strTz, String jsonStr) {

		try {

			Gson gson = new Gson();
			WeatherStationDataEntity weatherStationDataEntity = gson.fromJson(jsonStr, WeatherStationDataEntity.class);

			File rainJsonFile = new File(rainJsonFilePath);
			RainEntity rainEntity = null;
			if(rainJsonFile != null && rainJsonFile.exists() && rainJsonFile.isFile()) {
				String rainJsonStr = IOHelper.getInstance().readTextFileToString(rainJsonFile);
				rainEntity = gson.fromJson(rainJsonStr, RainEntity.class);
			}else{
				rainEntity = new RainEntity(weatherStationDataEntity.getRainMm());
				File rainJsonFolder = new File(rainJsonPath);
				if(rainJsonFolder == null || !rainJsonFolder.exists()) rainJsonFolder.mkdir();
				IOHelper.getInstance().writeStrToFile(gson.toJson(rainEntity), rainJsonFilePath);
			}
			
			
			String pressureValue = "...";
			Double pressureDbl = null; 
			String pressureJsonStr = null;
			File pressureFile = new File(pressureJsonFilePath);
	    	if(pressureFile!=null && pressureFile.exists() && pressureFile.isFile()) {
	    		try {
		    		pressureJsonStr = IOHelper.getInstance().readTextFileToString(pressureFile);
		    		gson = new Gson();
		    		PressureEntity pressureEntity = gson.fromJson(pressureJsonStr, PressureEntity.class);
		    		pressureDbl = pressureEntity.getPressure();
		    		pressureEntity.setPressure(pressureDbl/10);
		    		pressureValue = String.format("%05d" , pressureEntity.getPressure().intValue());
	    		}catch (Exception e) {
	    			System.out.println("Exception: "+e.getMessage()+ " | pressureFile.getAbsolutePath(): "+pressureFile.getAbsolutePath()+" | pressureJsonStr: "+pressureJsonStr+" | pressureDbl: "+pressureDbl);
				}
	    	}else System.out.println("No pressure json file found at: "+pressureFile.getAbsolutePath());


			Date now = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(now);
			int calMinute = cal.get(Calendar.MINUTE);
			int calHour = cal.get(Calendar.HOUR_OF_DAY);

			if(lastMinute != calMinute) {
				lastMinute = calMinute;
				day_minutes++;
				hour_minutes++;
				System.out.println(day_minutes);

				double rainMM = weatherStationDataEntity.getRainMm()-rainEntity.getInitialRain();
				weatherStationDataEntity.setRainMm(rainMM);		
				weatherStationDataEntity = weatherStationDataEntity.toImperial();
				
				double latitude = Double.parseDouble(strLat);
				double longitude = Double.parseDouble(strLng);
				int tz = Integer.parseInt(strTz);	
				cal.add(Calendar.HOUR_OF_DAY, tz);
				String complete_weather_data = (
						"@"+String.format("%02d" , cal.get(Calendar.DAY_OF_MONTH))
						+String.format("%02d" , cal.get(Calendar.HOUR_OF_DAY))
						+String.format("%02d" , calMinute)
						+"z"+toString(latitude, longitude)
						+"_"+String.format("%03d" , weatherStationDataEntity.getWindDirDeg())
						+"/"+String.format("%03d" , weatherStationDataEntity.getWindAvgMH().intValue())
						+"g"+String.format("%03d" , weatherStationDataEntity.getWindMaxMH().intValue())
						+"t"+String.format("%03d" , weatherStationDataEntity.getTemperatureF().intValue())
						+"r"+String.format("%03d" , weatherStationDataEntity.getPastHourRainIn().intValue())
						+"p"+String.format("%03d" , weatherStationDataEntity.getRainIn().intValue())
						+"P..."
						+"b"+pressureValue
						+"h"+String.format("%02d" , weatherStationDataEntity.getHumidity().intValue()));


				sendPacket(complete_weather_data, soundcardName);



				if(day_minutes == 1440) {
					day_minutes = 0;
					if(rainMM > 0) {
						rainEntity.setInitialRain(rainEntity.getInitialRain()+rainMM);
						IOHelper.getInstance().writeStrToFile(gson.toJson(rainEntity), rainJsonFilePath);
					}
				}
				
				if(hour_minutes <= 60) {
					if(rainMM > 0) {
						hourRainMm = hourRainMm + rainMM;
						weatherStationDataEntity.setPastHourRainMM(hourRainMm);
					}
				}else {
					setRainHourly(hourRainMm, calHour, rainEntity);
					hourRainMm = .0;
					hour_minutes = 0;
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
		}


	}

	private void setRainHourly(Double hourRainMm, int calHour, RainEntity rainEntity) throws Exception {
		switch (calHour) {
		case 1:
			rainEntity.setDayRain1(hourRainMm);
			break;
		case 2:
			rainEntity.setDayRain2(hourRainMm);
			break;
		case 3:
			rainEntity.setDayRain3(hourRainMm);
			break;
		case 4:
			rainEntity.setDayRain4(hourRainMm);
			break;
		case 5:
			rainEntity.setDayRain5(hourRainMm);
			break;
		case 6:
			rainEntity.setDayRain6(hourRainMm);
			break;
		case 7:
			rainEntity.setDayRain7(hourRainMm);
			break;
		case 8:
			rainEntity.setDayRain8(hourRainMm);
			break;
		case 9:
			rainEntity.setDayRain9(hourRainMm);
			break;
		case 10:
			rainEntity.setDayRain10(hourRainMm);
			break;
		case 11:
			rainEntity.setDayRain11(hourRainMm);
			break;
		case 12:
			rainEntity.setDayRain12(hourRainMm);
			break;
		case 13:
			rainEntity.setDayRain13(hourRainMm);
			break;
		case 14:
			rainEntity.setDayRain14(hourRainMm);
			break;
		case 15:
			rainEntity.setDayRain15(hourRainMm);
			break;
		case 16:
			rainEntity.setDayRain16(hourRainMm);
			break;
		case 17:
			rainEntity.setDayRain17(hourRainMm);
			break;
		case 18:
			rainEntity.setDayRain18(hourRainMm);
			break;
		case 19:
			rainEntity.setDayRain19(hourRainMm);
			break;
		case 20:
			rainEntity.setDayRain20(hourRainMm);
			break;
		case 21:
			rainEntity.setDayRain21(hourRainMm);
			break;
		case 22:
			rainEntity.setDayRain22(hourRainMm);
			break;
		case 23:
			rainEntity.setDayRain23(hourRainMm);
			break;
		case 24:
			rainEntity.setDayRain24(hourRainMm);
			break;
		default:
			break;
		}
		Gson gson = new Gson(); 
		IOHelper.getInstance().writeStrToFile(gson.toJson(rainEntity), rainJsonFilePath);
		
	}

	private void sendPacket(String complete_weather_data, String soundcardName) {
		Packet packet = new Packet("APRS",
				callsign,
				new String[] {"WIDE1-1", "WIDE2-2"},
				Packet.AX25_CONTROL_APRS,
				Packet.AX25_PROTOCOL_NO_LAYER_3,
				complete_weather_data.getBytes());

		System.out.println(packet);
		sc.displayAudioLevel();
		mod.prepareToTransmit(packet);
		sc.transmit();



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
		return getDMS(latitude,true)+symbolTable+getDMS(longitude,false);
	}

}

