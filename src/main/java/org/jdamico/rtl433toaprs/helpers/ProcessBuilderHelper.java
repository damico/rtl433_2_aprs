package org.jdamico.rtl433toaprs.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jdamico.gpsd.client.threads.VerifierThread;
import org.jdamico.javax25.PacketHandlerImpl;
import org.jdamico.javax25.ax25.Afsk1200Modulator;
import org.jdamico.javax25.ax25.Afsk1200MultiDemodulator;
import org.jdamico.javax25.ax25.Packet;
import org.jdamico.javax25.ax25.PacketDemodulator;
import org.jdamico.javax25.soundcard.Soundcard;
import org.jdamico.rtl433toaprs.App;
import org.jdamico.rtl433toaprs.Constants;
import org.jdamico.rtl433toaprs.entities.ConfigEntity;
import org.jdamico.rtl433toaprs.entities.PressureEntity;
import org.jdamico.rtl433toaprs.entities.RainEntity;
import org.jdamico.rtl433toaprs.entities.WeatherStationDataEntity;
import org.jdamico.rtl433toaprs.threads.Ax25DecoderThread;
import org.jdamico.rtl433toaprs.threads.IgateThread;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ProcessBuilderHelper {

	private static int rate = 48000;
	private static int buffer_size = 100;
	private Soundcard sc = null;
	private Afsk1200Modulator mod =  null;
	private static int minutes = 0;
	private static int hours = 0;
	private static int lastMinute = 0;
	private Double hourRainMm = .0;
	private static String baseDistPath;
	private static String basePythonPath;
	private static String rainJsonFilePath;
	private static String pressureJsonFilePath;
	private Double latitude; 
	private Double longitude; 
	private Integer tz;
	private String callsign;
	private String soundcardOutput;
	private String soundcardInput;
	private Boolean isIgate;
	private String igateHost;
	private Integer igatePort;
	private String igatePasscode;
	private Gson gson;
	private RainEntity rainEntity;
	private File rainJsonFile;
	private File logFile;
	private Double rainMmSinceLocalMidnight = .0;
	private Double dailyRainMm = .0;
	private int zuluCalHour;
	private String stationName;
	private String rtl433Cli;
	public boolean rtl433Fine = false;
	public Process rtlProcess;
	public String rtlUsbDevice;
	public String[] digiPath;
	public String destination;
	public Integer loopIntervalMinutes = 15;
	
	public ProcessBuilderHelper(String soundcardOutput, String soundcardInput, String callsign) {
		prepareModem(soundcardOutput, soundcardInput, callsign);
	}
	public ProcessBuilderHelper(ConfigEntity configEntity) throws Exception {


		if(configEntity.getDestination() !=null) destination = configEntity.getDestination();
		else destination = Constants.DEFAULT_DESTINATION;
		
		
		if(configEntity.getDigiPath() !=null) digiPath = configEntity.getDigiPath().replaceAll(" ", "").split(",");
		else digiPath = Constants.DEFAULT_DIGIPATH;
		
		
		if(configEntity.getRtlUsbDevice() != null) rtlUsbDevice = configEntity.getRtlUsbDevice();
		
		if(configEntity.getLoopIntervalMinutes() != null) loopIntervalMinutes = configEntity.getLoopIntervalMinutes();
		
		if(configEntity.getRunningPath() != null) {
			baseDistPath = configEntity.getRunningPath()+"/dist/";
			basePythonPath = configEntity.getRunningPath()+"/python-script/";
		}else {
			baseDistPath = BasicHelper.getInstance().getAbsoluteRunningPath()+"/dist/";
			basePythonPath = BasicHelper.getInstance().getAbsoluteRunningPath()+"/python-script/";
		}

		rainJsonFilePath = baseDistPath+"rain.json";
		pressureJsonFilePath = baseDistPath+"pressure.json";

		System.out.println("baseAppPath: "+baseDistPath);

		gson = new Gson();
		rainJsonFile = new File(rainJsonFilePath);
		if(rainJsonFile != null && rainJsonFile.exists() && rainJsonFile.isFile()) {

			rainEntity = RainEntity.fromJsonFile(rainJsonFile);
			long diffHoursFromLastUpdate = BasicHelper.getInstance().getDiffHoursBetweenDates(rainEntity.getLastUpdateDate(), new Date());
			if(diffHoursFromLastUpdate > Constants.LAST_UPDATE_LIMIT || rainEntity.getHour_rain_mm() < 0) rainJsonFile.delete();
			else {

				rainMmSinceLocalMidnight = rainEntity.getRain_mm_since_local_midnight();
				dailyRainMm = rainEntity.getDaily_rain_mm();
				hourRainMm = rainEntity.getHour_rain_mm();

				System.out.println("Hours from lastUpdate: "+diffHoursFromLastUpdate+" | Recovering saved data: ");
				System.out.println("rainMmSinceLocalMidnight: "+rainMmSinceLocalMidnight);
				System.out.println("dailyRainMm: "+dailyRainMm);
				System.out.println("hourRainMm: "+hourRainMm);

			}
		}

		if(configEntity.getRtl433Cli() != null 
				&& configEntity.getRtl433Cli().toLowerCase().contains("rtl_433") 
				&& configEntity.getRtl433Cli().toLowerCase().contains("-f")
				&& configEntity.getRtl433Cli().toLowerCase().contains("json")) rtl433Cli = configEntity.getRtl433Cli();
		if(configEntity.getInitialRainMm() !=null) rainEntity = new RainEntity(configEntity.getInitialRainMm());
		if(configEntity.getStationName() !=null) stationName = configEntity.getStationName();
		else stationName = Constants.APP_NAME;

		this.soundcardOutput = configEntity.getSoundcardOutput();
		this.soundcardInput = configEntity.getSoundcardInput();
		this.isIgate = configEntity.getIsIgate();
		this.igateHost = configEntity.getIgateHost();
		this.igatePort = configEntity.getIgatePort();
		this.igatePasscode = configEntity.getIgatePasscode();
		
		this.latitude = configEntity.getDecimalLat();
		this.longitude = configEntity.getDecimalLng();
		this.tz = configEntity.getTimezone();
		
		prepareModem(soundcardOutput, soundcardInput, configEntity.getCallsign());



	}
	private void prepareModem(String soundcardOutput, String soundcardInput, String callsign) {
		PacketDemodulator multi = null;

		try {
			multi = new Afsk1200MultiDemodulator(rate, new PacketHandlerImpl());
			mod = new Afsk1200Modulator(rate);
			sc = new Soundcard(rate, soundcardInput, soundcardOutput, buffer_size, multi, mod);
			this.callsign = callsign;
			
		} catch (Exception e) {
			System.err.println("Error initializing: "+this.getClass().getName());
			System.err.println("Exception at "+this.getClass().getName()+" class: "+e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public boolean rtlResetUsb() {
		
		boolean isTestFine = false;
		
		String[] split = rtlUsbDevice.split(":");
		String cliSuffix = "0x" + split[0] + " 0x" +split[1];

		String resetCli = Constants.DEFAULT_PYTHON_CLI + " " +basePythonPath+Constants.DEFAULT_RESET_RTL_CLI + " "+cliSuffix;

		System.out.println("Calling rtlResetUsb...("+resetCli+")");
		ProcessBuilder processBuilder = new ProcessBuilder().inheritIO().command(BasicHelper.getInstance().stringToListCli(resetCli));

		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		try {

			rtlProcess = processBuilder.start();
			inputStreamReader = new InputStreamReader(rtlProcess.getInputStream());
			reader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("Return from rtlResetUsb: "+line);
				isTestFine = true;
			}
			int ret = rtlProcess.waitFor();
			System.out.println("Process rtlTestCaller finished: "+ret);
			

		} catch (Exception e) {
			System.out.println("Error calling rtlResetUsb: "+this.getClass().getName());
			System.out.println("Exception at (rtlResetUsb) "+this.getClass().getName()+" class: "+e.getMessage());
		}finally {
			if(reader!=null) try{ reader.close(); }catch (Exception e) {e.printStackTrace();}
			if(inputStreamReader!=null) try{ inputStreamReader.close(); }catch (Exception e) {e.printStackTrace();}
			if(rtlProcess!=null) rtlProcess.destroy();
		}
		return isTestFine;
	}
	
	public boolean rtlTestCaller() {
		
		boolean isTestFine = false;

		System.out.println("Calling rtl_test...("+Constants.DEFAULT_RTL_TEST_CLI+")");
		ProcessBuilder processBuilder = new ProcessBuilder().inheritIO().command(BasicHelper.getInstance().stringToListCli(Constants.DEFAULT_RTL_TEST_CLI));

		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		try {
			String line;
			rtlProcess = processBuilder.start();
			inputStreamReader = new InputStreamReader(rtlProcess.getInputStream());
			reader = new BufferedReader(inputStreamReader);
			
			int ret = rtlProcess.waitFor();
			System.out.println("Process rtlTestCaller finished: "+ret);
			
			if (rtlProcess.exitValue() != 0) {
				isTestFine = false;
				System.out.println("Looking for possible errors calling rtlTestCaller...");
				inputStreamReader = new InputStreamReader(rtlProcess.getErrorStream());
				reader = new BufferedReader(inputStreamReader);
				while ((line = reader.readLine()) != null) {
					System.out.println("Error Return from rtlTestCaller: "+line);
				}
			}else isTestFine = true;

			

		} catch (Exception e) {
			System.out.println("Error calling rtlTestCaller: "+this.getClass().getName());
			System.out.println("Exception at (rtlTestCaller) "+this.getClass().getName()+" class: "+e.getMessage());

		}finally {
			if(reader!=null) try{ reader.close(); }catch (Exception e) {e.printStackTrace();}
			if(inputStreamReader!=null) try{ inputStreamReader.close(); }catch (Exception e) {e.printStackTrace();}
			if(rtlProcess!=null) rtlProcess.destroy();
		}
		return isTestFine;
	}

	public void rtl433Caller() {

		if(isIgate) {
			Thread ax25DecoderThread = new Ax25DecoderThread(sc);
			ax25DecoderThread.start();
			sc.receivedPackedMap = new HashMap<>();
			Thread igateThread = new IgateThread(callsign, igateHost, igatePort, igatePasscode, latitude, longitude);
			igateThread.start();
		}
		
		List<String> rtl_cli = null;
		if(rtl433Cli == null) {
			rtl433Cli = Constants.DEFAULT_RTL_433_CLI;
			System.out.println("Using: DEFAULT_RTL_433_CLI.");
		}else System.out.println("Using: rtl_433_cli from config.");
		rtl_cli = BasicHelper.getInstance().stringToListCli(rtl433Cli);
		System.out.println("Calling rtl_433...("+rtl433Cli+")");
		ProcessBuilder processBuilder = new ProcessBuilder().command(rtl_cli);
		processBuilder.redirectErrorStream(true);
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		
		try {
			BasicHelper.getInstance().writeStrToFile("1@"+App.pid, App.lockFile);
			rtlProcess = processBuilder.start();
			inputStreamReader = new InputStreamReader(rtlProcess.getInputStream());
			reader = new BufferedReader(inputStreamReader);
			String line = null;

			while ((line = reader.readLine()) != null) {
				System.out.println("Return from RTL_433: "+line);
				try {
					if(line.length() > 10) jsonParser(latitude, longitude, tz, line);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (rtlProcess.exitValue() != 0) {
				System.out.println("Looking for possible errors calling RTL_433...");
				inputStreamReader = new InputStreamReader(rtlProcess.getErrorStream());
				reader = new BufferedReader(inputStreamReader);
				while ((line = reader.readLine()) != null) {
					System.out.println("Error Return from RTL_433: "+line);
				}
			}
			int ret = rtlProcess.waitFor();
			System.out.println("Process RTL_433 finished: "+ret);

		} catch (Exception e) {
			System.out.println("Error calling: "+this.getClass().getName());
			System.out.println("Exception at "+this.getClass().getName()+" class: "+e.getMessage());
		}finally {
			if(reader!=null) try{ reader.close(); }catch (Exception e) {e.printStackTrace();}
			if(inputStreamReader!=null) try{ inputStreamReader.close(); }catch (Exception e) {e.printStackTrace();}
			if(rtlProcess!=null && rtlProcess.isAlive()) rtlProcess.destroyForcibly();
		}
	}

	public void jsonParser(Double latitude, Double longitude, Integer tz, String jsonStr) {

		try {

			WeatherStationDataEntity weatherStationDataEntity = gson.fromJson(jsonStr, WeatherStationDataEntity.class);
			weatherStationDataEntity.setInitDate(App.initDate);
			
			if(!rtl433Fine) {
				BasicHelper.getInstance().writeStrToFile("0@"+App.pid, App.lockFile);
				rtl433Fine = true;
			}
			
			
			if(rainEntity == null) {
				rainEntity = new RainEntity(weatherStationDataEntity.getRainMm());
				File rainJsonFolder = new File(baseDistPath);
				if(rainJsonFolder == null || !rainJsonFolder.exists()) rainJsonFolder.mkdir();
				String rainJsonStr = gson.toJson(rainEntity);
				BasicHelper.getInstance().writeStrToFile(rainJsonStr, rainJsonFilePath);
				System.out.println(rainJsonFilePath+" created, with "+weatherStationDataEntity.getRainMm()+" | "+rainJsonStr);
			}


			String pressureValue = "...";
			Double pressureDbl = null; 
			String pressureJsonStr = null;
			File pressureFile = new File(pressureJsonFilePath);
			if(pressureFile!=null && pressureFile.exists() && pressureFile.isFile()) {
				try {
					pressureJsonStr = BasicHelper.getInstance().readTextFileToString(pressureFile);
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
			int localCalHour =cal.get(Calendar.HOUR_OF_DAY);

			if(lastMinute != calMinute) {
				lastMinute = calMinute;
				minutes++;
				System.out.println("Time: "+hours+"h"+minutes+" (loopIntervalMinutes: "+loopIntervalMinutes+")");

				double rainMM = weatherStationDataEntity.getRainMm()-rainEntity.getInitialRain();
				if(rainMM >= 0) {
					System.out.println("Raining: "+rainMM+" | "+weatherStationDataEntity.getRainMm()+" | "+rainEntity.getInitialRain());
					rainEntity.setInitialRain(weatherStationDataEntity.getRainMm());
					
					hourRainMm = hourRainMm + rainMM;
					weatherStationDataEntity.setPastHourRainMM(hourRainMm);
					rainMmSinceLocalMidnight = rainMmSinceLocalMidnight + rainMM;
					dailyRainMm = dailyRainMm + rainMM;
					weatherStationDataEntity.setRainMmSinceLocalMidnight(rainMmSinceLocalMidnight);
					rainEntity.rainEntitySetRainUpdateMM(rainEntity.getInitialRain()+rainMM, dailyRainMm, rainMmSinceLocalMidnight, hourRainMm);
					setRainHourly(hourRainMm, zuluCalHour, rainEntity);
				}else if(rainMM < 0) {
					System.err.println("Wrong negative rainMM value: "+rainMM);
					weatherStationDataEntity.setPastHourRainMM(0.0);
					weatherStationDataEntity.setRainMmSinceLocalMidnight(0.0);
				}

				

				weatherStationDataEntity.setRainMm(dailyRainMm);	
				weatherStationDataEntity = weatherStationDataEntity.toImperial();

				System.out.println("RainHourly: "+hourRainMm+" | "+zuluCalHour + " | "+weatherStationDataEntity.getRainIn().intValue());

				if(VerifierThread.X != null && VerifierThread.Y != null) {
					longitude = VerifierThread.X;
					latitude = VerifierThread.Y;
					System.out.println("Position updated by GPSD Client.");
				}
				
				String humMsg = "";
				String tempMsg = "";
				if(weatherStationDataEntity.getTemperatureF() !=null) tempMsg="t"+String.format("%03d" , weatherStationDataEntity.getTemperatureF().intValue());
				if(weatherStationDataEntity.getHumidity() !=null) humMsg = "h"+String.format("%02d" , weatherStationDataEntity.getHumidity().intValue());
				
				

				cal.add(Calendar.HOUR_OF_DAY, tz);
				zuluCalHour = cal.get(Calendar.HOUR_OF_DAY);
				String complete_weather_data = (
						"@"+String.format("%02d" , cal.get(Calendar.DAY_OF_MONTH))
						+String.format("%02d" , zuluCalHour)
						+String.format("%02d" , calMinute)
						+"z"+toString(latitude, longitude)
						+"_"+String.format("%03d" , weatherStationDataEntity.getWindDirDeg())
						+"/"+String.format("%03d" , weatherStationDataEntity.getWindAvgMH().intValue())
						+"g"+String.format("%03d" , weatherStationDataEntity.getWindMaxMH().intValue())
						+tempMsg
						+"r"+String.format("%03d" , weatherStationDataEntity.getPastHourRainIn().intValue())
						+"p"+String.format("%03d" , weatherStationDataEntity.getRainIn().intValue())
						+"P"+String.format("%03d" , weatherStationDataEntity.getRainInSinceLocalMidnight().intValue())
						+"b"+pressureValue
						+humMsg
						+stationName.substring(0, stationName.length() >= 36 ? 36: stationName.length()));


				if(minutes == 1 || (minutes % loopIntervalMinutes == 0)) {
					sendPacket(destination, complete_weather_data, digiPath);
					weatherStationDataEntity.setMessageCount(App.messageCount++);
					logToDisk(weatherStationDataEntity);
				}

				if(minutes == 60) {
					if(localCalHour == 0) rainMmSinceLocalMidnight = .0;
					hourRainMm = .0;
					minutes = 0;
					hours++;
				}

				if(hours == 24) {
					hours = 0;
					dailyRainMm = 0.0;
				}
			}

		}catch (JsonSyntaxException e) {
			System.out.println("No json output: "+jsonStr);
		} catch (IOException e) {
			System.err.println("Error calling jsonParser: "+this.getClass().getName());
			System.err.println("Exception at "+this.getClass().getName()+" class: "+e.getMessage());
		}


	}


	private void transmitWithoutRtl433(String pressureJsonFilePath) {
		
		String pressureValue = "...";
		Double pressureDbl = null; 
		String pressureJsonStr = null;
		File pressureFile = new File(pressureJsonFilePath);
		if(pressureFile!=null && pressureFile.exists() && pressureFile.isFile()) {
			try {
				pressureJsonStr = BasicHelper.getInstance().readTextFileToString(pressureFile);
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
		cal.add(Calendar.HOUR_OF_DAY, tz);
		zuluCalHour = cal.get(Calendar.HOUR_OF_DAY);
		String weather_data = (
				"@"+String.format("%02d" , cal.get(Calendar.DAY_OF_MONTH))
				+String.format("%02d" , zuluCalHour)
				+String.format("%02d" , calMinute)
				+"z"+toString(latitude, longitude)
				+"b"+pressureValue
				+stationName.substring(0, stationName.length() >= 36 ? 36: stationName.length()));
		sendPacket(destination, weather_data, digiPath);
	}
	

	private void logToDisk(WeatherStationDataEntity weatherStationDataEntity) throws IOException {
		logFile = new File("/tmp/latest.data");
		if(logFile.exists()) logFile.delete();
		BasicHelper.getInstance().writeStrToFile(gson.toJson(weatherStationDataEntity), logFile);		
	}
	
	private void setRainHourly(Double hourRainMm, int calHour, RainEntity rainEntity) throws IOException {
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

		rainEntity.setLastUpdate();

		BasicHelper.getInstance().writeStrToFile(gson.toJson(rainEntity), rainJsonFilePath);
		System.out.println("Updating: "+rainJsonFilePath);

	}

	public void sendPacket(String destination, String complete_weather_data, String[] digiPath) {
		Packet packet = new Packet(destination,
				callsign,
				digiPath, //new String[] {"WIDE1-1", "WIDE2-2"},
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

