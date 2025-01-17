package ca.cidco.csb.test;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import ca.cidco.csb.surveydata.Position;
import ca.cidco.csb.utilities.Conversion;
import ca.cidco.csb.utilities.Geodesy;

public class GeodesyTest {

	Timestamp t = new Timestamp(new Date().getTime());
	double latitude= 45.025;
	double longitude= -51.24;
	double height= -12.0;
	
	Position position = new Position(t, 48.0, -68.0, 10, 0.01, 0.01, 0.01, 20, 0.05 );
			
// 		(45.025, -51.24, -12.0)  tested on
//		https://www.oc.nps.edu/oc2902w/coord/llhxyz.htm
//		Result =>  X: 2827.046km 	Y: -3521.167km  Z: 4489.304km
			
	
	@Test
	public void testLLH2ecef() {
		
		RealVector vector = Geodesy.LLH_2_ECEF(45.025, -51.24, -12.0);
		assertTrue (vector.getEntry(0)<2827046+1 && vector.getEntry(0)>2827046 -1);
		assertTrue (vector.getEntry(1)<-3521167+1 && vector.getEntry(1)>-3521167 -1);
		assertTrue (vector.getEntry(2)<4489304+1 && vector.getEntry(2)>4489304 -1);
	}
	
	@Test
	public void testLLH2ecefHeight50() {
		
		RealVector vector = Geodesy.LLH_2_ECEF(45.025, -51.24, 50.0);
		assertEquals ("Latitude values incorrect",2827073, vector.getEntry(0),1);
		assertEquals ("Longitude values incorrect",-3521201, vector.getEntry(1),1);
		assertEquals("Height values incorrect",4489348, vector.getEntry(2), 1);
	}
	
	
	@Test
	public void testECEF_2_LatLonH() {
		RealVector coord = Geodesy.ECEF_2_LatLonH(2827045.533123545, -3521166.5935412007, 4489304.046374496);
		assertTrue(coord.getEntry(0)<latitude+0.001 && coord.getEntry(0)>latitude-0.001);
		assertTrue(coord.getEntry(1)<-longitude+0.001 && coord.getEntry(1)>longitude-0.001);
		assertTrue(coord.getEntry(2)<height+0.001 && coord.getEntry(2)>height-0.001);
	}
}
