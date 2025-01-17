package ca.cidco.csb.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.cidco.csb.utilities.Conversion;
import junit.framework.AssertionFailedError;

public class ConversionTest {


	@Test
	public void testConvertDMStoDecimalDegree() throws Exception {
		double result =Conversion.convertDMStoDecimalDegree(32.4, 48.234 , 56.456);
		assertTrue((33.21958-0.00001)< result && result <33.21958+0.00001);
		// Same with negative degrees
		double result_neg =Conversion.convertDMStoDecimalDegree(-32.4, 48.234 , 56.456);
		assertTrue((-33.21958-0.00001)< result_neg && result_neg <-33.21958+0.00001);
			
		double result_1 =Conversion.convertDMStoDecimalDegree(12.456, 18.234 , 6.456);
		assertTrue((12.76169-0.00001)< result_1 && result_1 <12.76169+0.00001);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testConvertDMStoDecimalDegree_DegreesOutOfBoundHigher() {
			Conversion.convertDMStoDecimalDegree(412.456, 28.234 , 6.456);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConvertDMStoDecimalDegree_DegreesOutOfBoundLower() {
			Conversion.convertDMStoDecimalDegree(-361.0, 28.234 , 6.456);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testConvertDMStoDecimalDegree_MinutesOutOfBound() {
			Conversion.convertDMStoDecimalDegree(12.456, 68.234 , 6.456);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConvertDMStoDecimalDegree_SecondesOutOfBound() {
			Conversion.convertDMStoDecimalDegree(12.456, 48.234 , 74.457);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConvertDMStoDecimalDegree_NegativesSecondesValue() {
			Conversion.convertDMStoDecimalDegree(12.456, 8.234 , -12.457);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConvertDMStoDecimalDegree_NegativesMinutesValue() {
			Conversion.convertDMStoDecimalDegree(12.456, -42.234 , 12.457);
	}
	
	@Test
	public void testDeg2Rad() throws Exception {
		double result =Conversion.deg2Rad(180);
		assertTrue(result==Conversion.PI);
	}

	@Test
	public void testNegativeDeg2Rad() throws Exception {
		double result =Conversion.deg2Rad(-180);
		assertTrue(result==-Conversion.PI);
	}
	
	@Test
	public void testRad2Deg() throws Exception {
		double result =Conversion.rad2Deg(Conversion.PI/2);
		assertTrue(result==90);
	}

	@Test
	public void testNegativeRad2Deg() throws Exception {
		double result =Conversion.rad2Deg(-Conversion.PI/2);
		assertTrue(result==-90);
	}
	
}
