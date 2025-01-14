package ca.cidco.csb.test;

import static org.junit.Assert.*;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.cidco.csb.georeference.BathymetryPoint;
import ca.cidco.csb.surveydata.Attitude;
import ca.cidco.csb.surveydata.Position;
import ca.cidco.csb.utilities.BigDecimalFactory;
import ca.cidco.csb.utilities.Conversion;
import ca.cidco.csb.utilities.Interpolation;

public class InterpolationTest {
	
	private Timestamp buildTimestamp(String dateTime) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		java.util.Date parsedDate = dateFormat.parse(dateTime);
		Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
		return timestamp ;
	}
	
	
//    private static final String TIDE_PATH = "test/rsc/TIDE/fake_tide.tid";
    private static final BigDecimal SEUIL = BigDecimalFactory.create("0.01");
    private static final double EPSILON = 0.00000000000001;
    
    
	//Create timestamp 
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
	String dateTime1= "2022-10-10 20:44:50.0";
	String dateTime2= "2022-10-10 20:44:55.0";
	String dateTime3= "2022-10-10 20:44:58.0";
	String dateTime4= "2022-10-11 01:34:27.0";
	String dateTime5= "2022-10-10 20:44:28.0"; //same as dateTime1
	
    BigDecimal y1Latitude =			BigDecimalFactory.create("48");
    BigDecimal y1Longitude =		BigDecimalFactory.create("-69");
    BigDecimal y1EllipsoidalHeight=	BigDecimalFactory.create("0");
    BigDecimal y1Heading =			BigDecimalFactory.create("0");
    BigDecimal y1Pitch =			BigDecimalFactory.create("0");
    BigDecimal y1Roll =				BigDecimalFactory.create("0");
	
    BigDecimal y2Latitude =			BigDecimalFactory.create("49");
    BigDecimal y2Longitude =		BigDecimalFactory.create("-68");
    BigDecimal y2EllipsoidalHeight=	BigDecimalFactory.create("1");
    BigDecimal y2Heading=			BigDecimalFactory.create("30");
    BigDecimal y2Pitch =			BigDecimalFactory.create("50");
    BigDecimal y2Roll =				BigDecimalFactory.create("30");
	
	//	x (Timestamp)	
    BigDecimal xLatitude =			BigDecimalFactory.create("500");
    BigDecimal xLongitude =			BigDecimalFactory.create("500");
    BigDecimal xEllipsoidalHeight =	BigDecimalFactory.create("500");
    BigDecimal xHeading=			BigDecimalFactory.create("500");
    BigDecimal xPitch =				BigDecimalFactory.create("500");
    BigDecimal xRoll =				BigDecimalFactory.create("500");

	//	x1 (Timestamp)	
    BigDecimal x1Latitude	=		BigDecimalFactory.create("0");
    BigDecimal x1Longitude =		BigDecimalFactory.create("0");
    BigDecimal x1EllipsoidalHeight=	BigDecimalFactory.create("0");
    BigDecimal x1Heading =			BigDecimalFactory.create("200");
    BigDecimal x1Pitch =			BigDecimalFactory.create("200");
    BigDecimal x1Roll	=			BigDecimalFactory.create("200");

	//	x2 (Timestamp)	
    BigDecimal x2Latitude	=		BigDecimalFactory.create("800");
    BigDecimal x2Longitude =		BigDecimalFactory.create("800");
    BigDecimal x2EllipsoidalHeight=	BigDecimalFactory.create("800");
    BigDecimal x2Heading =			BigDecimalFactory.create("800");
    BigDecimal x2Pitch =			BigDecimalFactory.create("800");
    BigDecimal x2Roll	=			BigDecimalFactory.create("800");
	
    //	∆t
    BigDecimal tLat= 				BigDecimalFactory.create("0.625");
    BigDecimal tLong = 				BigDecimalFactory.create("0.625");
    BigDecimal tHeight =			BigDecimalFactory.create("0.625");
    BigDecimal tHeading=			BigDecimalFactory.create("0.5");
    BigDecimal tPitch= 				BigDecimalFactory.create("0.5");
	BigDecimal tRoll= 				BigDecimalFactory.create("0.5");

	// y Interpolated values
    BigDecimal yLatitude =			BigDecimalFactory.create("48.62500000000000000000");
    BigDecimal yLongitude =		BigDecimalFactory.create("-68.37500000000000000000");
    BigDecimal yEllipsoidalHeight=	BigDecimalFactory.create("0.62500000000000000000");
    BigDecimal yHeading =			BigDecimalFactory.create("15.00000000000000000000");
    BigDecimal yPitch =			BigDecimalFactory.create("25.00000000000000000000");
    BigDecimal yRoll =				BigDecimalFactory.create("15.00000000000000000000");
	
	@Test
	public void testLinearInterpolation() {
		
		assertEquals(Interpolation.linearInterpolation(y1Latitude, y2Latitude, xLatitude, x1Latitude, x2Latitude), yLatitude);
		assertEquals(Interpolation.linearInterpolation(y1Longitude, y2Longitude, xLongitude, x1Longitude, x2Longitude), yLongitude);
		assertEquals(Interpolation.linearInterpolation(y1EllipsoidalHeight, y2EllipsoidalHeight, xEllipsoidalHeight, x1EllipsoidalHeight, x2EllipsoidalHeight), yEllipsoidalHeight);
		assertEquals(Interpolation.linearInterpolation(y1Heading, y2Heading, xHeading, x1Heading, x2Heading), yHeading);
		assertEquals(Interpolation.linearInterpolation(y1Pitch, y2Pitch, xPitch, x1Pitch, x2Pitch), yPitch);
		assertEquals(Interpolation.linearInterpolation(y1Roll, y2Roll, xRoll, x1Roll, x2Roll), yRoll);
	}

	@Test
	public void testLinearInterpolationByTimeValid() throws Exception {
		Timestamp ts = buildTimestamp(dateTime2);
		Timestamp tsBefore = buildTimestamp(dateTime1);
		Timestamp tsAfter = buildTimestamp(dateTime3);
		
		assertTrue(Interpolation.linearInterpolationByTime( y1Latitude.doubleValue(), y2Latitude.doubleValue(), ts, tsBefore, tsAfter)==48.625);
	}
	
	@Test (expected = Exception.class)
	public void testLinearInterpolationByTimeEqualsTimestamps() throws Exception {
		Timestamp ts = buildTimestamp(dateTime2);
		Timestamp tsBefore = buildTimestamp(dateTime1);
		Timestamp tsAfter = buildTimestamp(dateTime5);
		
		Interpolation.linearInterpolationByTime( y1Latitude.doubleValue(), y2Latitude.doubleValue(), ts, tsBefore, tsAfter);
	}
	@Test (expected = Exception.class)
	public void testLinearInterpolationByTimeFirstTimestampsHigherThenInterpolate() throws Exception {
		Timestamp ts = buildTimestamp(dateTime1);
		Timestamp tsBefore = buildTimestamp(dateTime2);
		Timestamp tsAfter = buildTimestamp(dateTime3);
		
		Interpolation.linearInterpolationByTime( y1Latitude.doubleValue(), y2Latitude.doubleValue(), ts, tsBefore, tsAfter);
	}
		
	@Test (expected = Exception.class)
	public void testLinearInterpolationByTimeFirstTimestampsHigherThenTheLast() throws Exception {
		Timestamp ts = buildTimestamp(dateTime2);
		Timestamp tsBefore = buildTimestamp(dateTime3);
		Timestamp tsAfter = buildTimestamp(dateTime1);
		
		Interpolation.linearInterpolationByTime( y1Latitude.doubleValue(), y2Latitude.doubleValue(), ts, tsBefore, tsAfter);
	}
	
	@Test (expected = Exception.class)
	public void testLinearAngleInterpolationByTimeEqualsTimestamps() throws Exception {
		Timestamp l= 	new Timestamp(1527636114); 
		Timestamp t1= 	new Timestamp(1527776114);
		Timestamp t2 = 	new Timestamp(1527776114);
			
		Interpolation.linearAngleInterpolationByTime(47.997475, 48.005745, l, t1, t2);
	}
	
	@Test (expected = Exception.class)
	public void testLinearAngleInterpolationByTimeFirstTimestampsHigherThenTheLast() throws Exception {
		Timestamp l= 	new Timestamp(1527636114); 
		Timestamp t1= 	new Timestamp(1527778114);
		Timestamp t2 = 	new Timestamp(1527776114);
			
		Interpolation.linearAngleInterpolationByTime(47.997475, 48.005745, l, t1, t2);
	}
	
	@Test (expected = Exception.class)
	public void testLinearAngleInterpolationByTimeFirstTimestampsHigherThenInterpolate() throws Exception {
		Timestamp l= 	new Timestamp(1527636114); 
		Timestamp t1= 	new Timestamp(1527778114);
		Timestamp t2 = 	new Timestamp(1527779114);
			
		Interpolation.linearAngleInterpolationByTime(47.997475, 48.005745, l, t1, t2);
	}
	
	@Test
	public void testLinearAngleInterpolationByTimePass360() throws Exception {
		Timestamp l= 	new Timestamp(1527766114); 
		Timestamp t1= 	new Timestamp(1527756114);
		Timestamp t2 = 	new Timestamp(1527776114);
		
		assertTrue(0.0 == Interpolation.linearAngleInterpolationByTime(358.00, 2.00, l, t1, t2));
		assertTrue(0.0 == Interpolation.linearAngleInterpolationByTime(2.00, 358.00, l, t1, t2));
	}
	
	@Test
	public void testLinearAngleInterpolationByTimeNotPass360() throws Exception {
		Timestamp l= 	new Timestamp(1527766114); 
		Timestamp t1= 	new Timestamp(1527756114);
		Timestamp t2 = 	new Timestamp(1527776114);
		
		assertTrue(16.0 == Interpolation.linearAngleInterpolationByTime(12.00, 20.00, l, t1, t2));
		assertTrue(16.0 == Interpolation.linearAngleInterpolationByTime(20.00, 12.00, l, t1, t2));
	}
	
	@Test
	public void testLinearAngleInterpolationByTimeTimestampDecale75Percent() throws Exception {
		Timestamp l= 	new Timestamp(1527766114); 
		Timestamp t1= 	new Timestamp(1527706114);
		Timestamp t2 = 	new Timestamp(1527786114);
		
		assertTrue(18.0 == Interpolation.linearAngleInterpolationByTime(12.00, 20.00, l, t1, t2));
	}
	
	@Test
	public void testLinearAngleInterpolationByTimeNegativeAngle() throws Exception {
		Timestamp l= 	new Timestamp(1527746114); 
		Timestamp t1= 	new Timestamp(1527706114);
		Timestamp t2 = 	new Timestamp(1527786114);
		// -16.0 degrees equal 344 degrees
		assertTrue(360-16.0 == Interpolation.linearAngleInterpolationByTime(-12.00, -20.00, l, t1, t2));
		assertTrue(344.0 == Interpolation.linearAngleInterpolationByTime(-12.00, -20.00, l, t1, t2));
		assertTrue(360-16.0 == Interpolation.linearAngleInterpolationByTime(-20.00, -12.00, l, t1, t2));
	}
	
	@Test
	public void testLinearAngleInterpolationByTimeNegativeAnglePass360() throws Exception {
		Timestamp l= 	new Timestamp(1527746114); 
		Timestamp t1= 	new Timestamp(1527706114);
		Timestamp t2 = 	new Timestamp(1527786114);
		
//		assertTrue(1.0 == Interpolation.linearAngleInterpolationByTime(-2.00, 4.00, l, t1, t2));
		assertTrue(359.0 == Interpolation.linearAngleInterpolationByTime(2.00, -4.00, l, t1, t2));
//		assertTrue(5.0 == Interpolation.linearAngleInterpolationByTime(-5.00, -345.00, l, t1, t2));
	}
	
	@Test (expected = Exception.class)
	public void testLinearAngleInterpolationByTime180() throws Exception {
		Timestamp l= 	new Timestamp(1527766114); 
		Timestamp t1= 	new Timestamp(1527756114);
		Timestamp t2 = 	new Timestamp(1527776114);
		
		Interpolation.linearAngleInterpolationByTime(90.00, -90.00, l, t1, t2);
	}
	
	@Test
	public void testInterpolateAttitude() throws Exception {
		Timestamp t1= 	new Timestamp(1527726114);
		Timestamp t2 = 	new Timestamp(1527786114);
		Timestamp timestamp = 	new Timestamp(1527756114); // delta .5
		
		Attitude a1= new Attitude(t1, 0.0, 0.0, 0.0);
		Attitude a2= new Attitude(t2, 30.0, 50.0, 30.0);

		Attitude interpolated=(Interpolation.interpolateAttitude(a1, a2, timestamp));
		assertTrue(15.0==interpolated.getHeading());
		assertTrue(25.0==interpolated.getPitch());
		assertTrue(15.0==interpolated.getRoll());
	}
	@Test
	public void testInterpolatePosition()  throws Exception {
		Timestamp t1= 			new Timestamp(1496215545);
		Timestamp t2= 			new Timestamp(1496220420);
		Timestamp timestamp= 	new Timestamp(1496219055); //delta 0.72

		Position pos1 = new Position(t1, 44.00492, -76.739474, -22.5, 0.0123, 0.042, 0.005, 12, 0.0024);
		Position pos2 = new Position(t2, 43.988017, -76.884121, -21.75, 0.0123, 0.042, 0.005, 12, 0.0024);
		
		Position interpolated=Interpolation.interpolatePosition(pos1, pos2, timestamp);
		assertTrue(43.992749840 ==interpolated.getLatitude());
		assertTrue(-76.843619840 ==interpolated.getLongitude());
		assertTrue(-21.960000000 ==interpolated.getHeight());
	}
}
