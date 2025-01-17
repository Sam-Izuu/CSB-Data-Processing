package ca.cidco.csb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import ca.cidco.csb.georeference.BathymetryPoint;
import ca.cidco.csb.georeference.ErsGeoreferencing;
import ca.cidco.csb.surveydata.Attitude;
import ca.cidco.csb.surveydata.Depth;
import ca.cidco.csb.surveydata.Position;

public class GeoreferencingTest {

	
	@Test
	public void testGeoreferencePositionOnly() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(0.0);
			testAttitude.setPitch(0.0);
			testAttitude.setHeading(0.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(0.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{0,0,0}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000251,sounding.getLatitude(),	0.001);
			assertEquals("Longitude values incorrect",-68.0999966,sounding.getLongitude(),	0.001);  
			assertEquals("Height values incorrect",20.0,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}

	@Test
	public void testGeoreferencePositionWithDepth() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(0.0);
			testAttitude.setPitch(0.0);
			testAttitude.setHeading(0.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{0,0,0}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000251,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0999966,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",10.0,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	
	@Test
	public void testGeoreferencePositionWithLeverarm() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(0.0);
			testAttitude.setPitch(0.0);
			testAttitude.setHeading(0.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000090,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0999731,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",7.0000005 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferencePositiveRoll() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(45.0);
			testAttitude.setPitch(0.0);
			testAttitude.setHeading(0.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000090,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.1001044,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",9.3934032 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferenceNegativeRoll() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(-45.0);
			testAttitude.setPitch(0.0);
			testAttitude.setHeading(0.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000090,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0998576,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",12.2218344 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferencePositivePitch() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(0.0);
			testAttitude.setPitch(45.0);
			testAttitude.setHeading(0.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000890,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0999731,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",11.5147267 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferenceNegativePitch() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(0.0);
			testAttitude.setPitch(-45.0);
			testAttitude.setHeading(0.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.0999237,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0999731,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",10.1005111 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}

	@Test
	public void testGeoreferencePositiveHeading() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(0.0);
			testAttitude.setPitch(0.0);
			testAttitude.setHeading(45.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.0999936,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0999715,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",7.0000005 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferenceNegativeHeading() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(0.0);
			testAttitude.setPitch(0.0);
			testAttitude.setHeading(-45.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000191,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0999905,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",7.0000005 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferencePositiveAttitude() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(30.0);
			testAttitude.setPitch(30.0);
			testAttitude.setHeading(45.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000748,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0999789,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",9.8839803 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferenceNegativeAttitude() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(-30.0);
			testAttitude.setPitch(-30.0);
			testAttitude.setHeading(-45.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000252,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0998814,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",10.6160322 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferenceNegativeLatitude() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			testPosition.setLatitude(-34.42);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(-30.0);
			testAttitude.setPitch(-30.0);
			testAttitude.setHeading(-45.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",-34.4199747,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0999039,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",10.6160322 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferencePositiveLongitude() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			testPosition.setLatitude(-34.42);
			testPosition.setLongitude(42.42);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(-30.0);
			testAttitude.setPitch(-30.0);
			testAttitude.setHeading(-45.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",-34.4199747,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",42.4200961,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",10.6160322 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}

	@Test
	public void testGeoreferenceNegativeEllipsoidalHeight() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			testPosition.setHeight(-42.0);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(-30.0);
			testAttitude.setPitch(-30.0);
			testAttitude.setHeading(-45.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(10.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.1000252,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.0998814,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",-51.3839678 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	@Test
	public void testGeoreferenceAttitudeVariation() {
		try {
			Timestamp t = new Timestamp(new Date().getTime());
			
			
			Position    testPosition 	= buildPosition(t);
			testPosition.setHeight(-42.0);
			Attitude 	testAttitude 	= buildAttitude(t);
			testAttitude.setRoll(40.0);
			testAttitude.setPitch(-30.0);
			testAttitude.setHeading(15.0);
			Depth 		testDepth 		= buildDepth(t);
			testDepth.setDepth(42.0);
			
			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); 
			
			ErsGeoreferencing geo = new ErsGeoreferencing(leverArm);
	
			BathymetryPoint sounding = geo.georeference(testPosition, testAttitude, testDepth);
		
			assertEquals("Latitude values incorrect",48.0997884,sounding.getLatitude(),	0.001); 
			assertEquals("Longitude values incorrect",-68.1002961,sounding.getLongitude(),	0.001); 
			assertEquals("Height values incorrect",-73.4668870 ,sounding.getEllipsoidalHeight(),	0.00001); 
		}
		catch(Exception e) {
			fail(e.getMessage());	 
		}
	}
	
	
	Position buildPosition(Timestamp t) {
		Double latitude = 48.1;
		Double longitude = -68.1;
		Double ellipsoidalHeight = 20.0;
		Double sdLatitude = 0.1;
		Double sdLongitude = 0.1;
		Double sdHeight = 0.1;
		int nsv = 32;
		Double gdop = 3.0;
		
		return new Position(t, latitude, longitude, ellipsoidalHeight, sdLatitude, sdLongitude, sdHeight, nsv, gdop);
	}
	
	Attitude buildAttitude(Timestamp t) {
		Double heading = 45.0;
		Double pitch   = 45.0;
		Double roll    = 45.0;
		
		return new Attitude(t, heading, pitch, roll);
	}
	
	Depth buildDepth(Timestamp t) {
		Double depth = 100.0;
		return new Depth(t,depth);
	}

}
