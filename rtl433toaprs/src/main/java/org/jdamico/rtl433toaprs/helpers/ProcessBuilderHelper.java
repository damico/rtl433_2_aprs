package org.jdamico.rtl433toaprs.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.jdamico.javax25.PacketHandlerImpl;
import org.jdamico.javax25.ax25.Afsk1200Modulator;
import org.jdamico.javax25.ax25.Afsk1200MultiDemodulator;
import org.jdamico.javax25.ax25.Packet;
import org.jdamico.javax25.ax25.PacketDemodulator;
import org.jdamico.javax25.radiocontrol.TransmitController;
import org.jdamico.javax25.soundcard.Soundcard;
import org.jdamico.rtl433toaprs.entities.RainEntity;
import org.jdamico.rtl433toaprs.entities.WeatherStationDataEntity;

import com.google.gson.Gson;

public class ProcessBuilderHelper {

	private static int minutes = 0;
	private static int lastMinute = 0;
	private static final String rainJsonFilePath = "dist/rain.json";
	private String strLat; 
	private String strLng; 
	private String strTz;
	private String callsign;
	private String soundcardName;

	public ProcessBuilderHelper(String callsign, String strLat, String strLng, String strTz, String soundcardName) {
		this.callsign = callsign;
		this.strLat = strLat;
		this.strLng = strLng;
		this.strTz = strTz;
		this.soundcardName = soundcardName;
	}

	public void caller() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("rtl_433", "-F", "json");

		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;

		try {

			Process process = processBuilder.start();
			inputStreamReader = new InputStreamReader(process.getInputStream());
			reader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = reader.readLine()) != null) {
				jsonParser(strLat, strLng, strTz, line);
			}


		} catch (Exception e) {
			e.printStackTrace();

		}finally {
			if(reader!=null) try{ reader.close(); }catch (Exception e) {e.printStackTrace();};
			if(inputStreamReader!=null) try{ inputStreamReader.close(); }catch (Exception e) {e.printStackTrace();};
		}
	}

	public void jsonParser(String strLat, String strLng, String strTz, String jsonStr) throws Exception {
		Gson gson = new Gson();
		WeatherStationDataEntity entity = gson.fromJson(jsonStr, WeatherStationDataEntity.class);

		File rainJson = new File(rainJsonFilePath);
		RainEntity rainEntity = null;
		if(rainJson != null && rainJson.exists() && rainJson.isFile()) {
			String rainJsonStr = IOHelper.getInstance().readTextFileToString(rainJson);
			rainEntity = gson.fromJson(rainJsonStr, RainEntity.class);
		}else{
			rainEntity = new RainEntity(entity.getRainMm());
			IOHelper.getInstance().writeStrToFile(gson.toJson(rainEntity), rainJsonFilePath);
		}
		double rainMM = entity.getRainMm()-rainEntity.getInitialRain();
		entity.setRainMm(rainMM);		
		entity = entity.toImperial();


		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		int calMinute = cal.get(Calendar.MINUTE);

		if(lastMinute != calMinute) {
			lastMinute = calMinute;
			minutes++;
			System.out.println(minutes);

			double latitude = Double.parseDouble(strLat);
			double longitude = Double.parseDouble(strLng);
			int tz = Integer.parseInt(strTz);	
			cal.add(Calendar.HOUR_OF_DAY, tz);
			String complete_weather_data = (
					"@"+String.format("%02d" , cal.get(Calendar.DAY_OF_MONTH))
					+String.format("%02d" , cal.get(Calendar.HOUR_OF_DAY))
					+String.format("%02d" , calMinute)
					+"z"+toString(latitude, longitude)
					+"_"+String.format("%03d" , entity.getWindDirDeg())
					+"/"+String.format("%03d" , entity.getWindAvgMH().intValue())
					+"g"+String.format("%03d" , entity.getWindMaxMH().intValue())
					+"t"+String.format("%03d" , entity.getTemperatureF().intValue())
					+"r..."
					+"p"+String.format("%03d" , entity.getRainIn().intValue())
					+"P..."
					+"b..."
					+"h"+String.format("%03d" , entity.getHumidity().intValue()));


			sendPacket(complete_weather_data, soundcardName);



			if(minutes == 1440) {
				minutes = 0;
				if(rainMM > 0) {
					rainEntity.setInitialRain(rainEntity.getInitialRain()+rainMM);
					IOHelper.getInstance().writeStrToFile(gson.toJson(rainEntity), rainJsonFilePath);
				}
			}
		}




	}

	private void sendPacket(String complete_weather_data, String soundcardName) {
		Packet packet = new Packet("APRS",
				callsign,
				new String[] {"WIDE1-1", "WIDE2-2"},
				Packet.AX25_CONTROL_APRS,
				Packet.AX25_PROTOCOL_NO_LAYER_3,
				complete_weather_data.getBytes());

		System.out.println(packet);
		int rate = 48000;
		PacketHandlerImpl t = new PacketHandlerImpl();
		PacketDemodulator multi = null;
		try {
			multi = new Afsk1200MultiDemodulator(48000, new PacketHandlerImpl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Afsk1200Modulator mod = new Afsk1200Modulator(rate);
		int buffer_size = 100;
		Soundcard sc = new Soundcard(rate, null, soundcardName, buffer_size,multi,mod);
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
		char symbolCode = '.';
		return getDMS(latitude,true)+symbolTable+getDMS(longitude,false)+symbolCode;
	}

}

