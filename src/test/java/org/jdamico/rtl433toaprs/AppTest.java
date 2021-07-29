package org.jdamico.rtl433toaprs;

import java.io.File;
import java.math.BigDecimal;

import org.jdamico.rtl433toaprs.entities.PressureEntity;
import org.jdamico.rtl433toaprs.entities.WeatherStationDataEntity;
import org.jdamico.rtl433toaprs.helpers.IOHelper;

import com.google.gson.Gson;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void testApp() throws Exception
    {
    	
    	WeatherStationDataEntity weatherStationDataEntity = new WeatherStationDataEntity();
    	weatherStationDataEntity.setTemperatureC(15.6);
    	weatherStationDataEntity.setWindAvgMS(1.084);
    	weatherStationDataEntity.setWindMaxMS(1.530);
    	weatherStationDataEntity.setRainMm(1628.648);
    	weatherStationDataEntity = weatherStationDataEntity.toImperial();
    	System.out.println(weatherStationDataEntity.getTemperatureF());
    	
    	
    	File f = new File("dist/pressure.json");
    	if(f !=null && f.exists() && f.isFile()) {
    		String jsonStr = IOHelper.getInstance().readTextFileToString(f);
    		Gson gson = new Gson();
    		PressureEntity pressureEntity = gson.fromJson(jsonStr, PressureEntity.class);
    		System.out.println(pressureEntity.getPressure()/10);
    	}
    	
    	
    	Double d = (5/25.4)*100;
    	System.out.println(String.format("%03d" , d.intValue()));
    	
    	
        assertTrue( true );
    }
}
