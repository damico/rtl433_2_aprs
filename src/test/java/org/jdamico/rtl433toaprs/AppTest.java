package org.jdamico.rtl433toaprs;

import org.jdamico.rtl433toaprs.entities.WeatherStationDataEntity;

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
     */
    public void testApp()
    {
    	
    	WeatherStationDataEntity weatherStationDataEntity = new WeatherStationDataEntity();
    	weatherStationDataEntity.setTemperatureC(15.6);
    	weatherStationDataEntity.setWindAvgMS(1.084);
    	weatherStationDataEntity.setWindMaxMS(1.530);
    	weatherStationDataEntity.setRainMm(1628.648);
    	weatherStationDataEntity = weatherStationDataEntity.toImperial();
    	System.out.println(weatherStationDataEntity.getTemperatureF());
        assertTrue( true );
    }
}
