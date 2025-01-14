package ca.cidco.csb.utilities;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import ca.cidco.csb.surveydata.Attitude;
import ca.cidco.csb.surveydata.Position;

public class Geodesy {
	
	public final static double PI = 3.14159265358979323846;
	public final static double a = 6378137.0; //demi grand axe (WGS84) meters 
	public final static double e = 0.081819190842622; //first eccentricity (WGS84)
	public final static double e2 = Math.pow(e, 2); // eccentricity squared
	

	/**
	 * Convert latitude, longitude, height in WGS84 to ECEF X,Y,Z
	 * latitude and longitude given in decimal degrees.
	 * Height should be given in meters
	 */
	public static RealVector LLH_2_ECEF (double latitude, double longitude, double h) {
		
		  double lat = latitude/180*PI; // converting to radians
		  double lon = longitude/180*PI; // converting to radians
		 
		  double chi = Math.sqrt(1-e2*Math.pow(Math.sin(lat),2));
		  double X = (a/chi +h)*Math.cos(lat)*Math.cos(lon);
		  double Y = (a/chi +h)*Math.cos(lat)*Math.sin(lon);
		  double Z = (a*(1-e2) /chi + h)*Math.sin(lat);
		  
		  return new ArrayRealVector(new double[] { X, Y, Z });
	}
	
	/**
	 *  convert ECEF coordinates to LLH
	 */
	public static RealVector ECEF_2_LatLonH(double x, double y, double z){
		// Computation of parameters
		double b   = Math.sqrt((a*a)*(1-e2));  
		double e1  = Math.sqrt( (a*a-b*b)/(b*b) ); 
		double p = Math.sqrt((x*x) + (y*y));		 
		double q = Math.atan2((z*a), (p*b));  

		// computation Latitude and longitude in rad
		double lon = Math.atan2(y,x);
		double lat = Math.atan2((z + (e1*e1) *b* Math.pow(Math.sin(q), 3)), (p - (e2) * a * Math.pow(Math.cos(q), 3)));  

		// Altitude computation in m
		double N = a / Math.sqrt(1-(e2*(Math.sin(lat)*Math.sin(lat))));  
		double h = (p / Math.cos(lat)) - N;  	
		
		return new ArrayRealVector(new double[]{Conversion.rad2Deg(lat),Conversion.rad2Deg(lon), h });
	}
	
	// Navigation frame to ECEF
	/* This function is used to transform navigation frame to terrestrial frame
	Input data:
	- lat, lon: latitude and longitude in (rad)
	- xn, yn, zn: vector coordinated in n-frame
	Output data:
	- x, y, z: vector coordinated in TRF-frame
	*/
	public static RealVector Nav_2_ECEF(double lat, double lon, double xn, double yn, double zn){
		double clat = Math.cos(lat); 
		double clon = Math.cos(lon);
		double slat = Math.sin(lat); 
		double slon = Math.sin(lon);

		// Xn to XTRF
		double x = -clat*clon*zn-slon*yn-clon*slat*xn;
		double y = -clat*slon*zn+clon*yn-slat*slon*xn;
		double z =  clat*xn-slat*zn;
		
		return new ArrayRealVector(new double[] { x, y, z });
	}
	
	public static Array2DRowRealMatrix ned2ecef(Position position) {
		Array2DRowRealMatrix m = new Array2DRowRealMatrix(3, 3);
				
		Double slon = Math.sin(Conversion.deg2Rad(position.getLongitude()));
		Double slat = Math.sin(Conversion.deg2Rad(position.getLatitude()));
		Double clon = Math.cos(Conversion.deg2Rad(position.getLongitude()));
		Double clat = Math.cos(Conversion.deg2Rad(position.getLatitude()));
		
		m.setEntry(0, 0, -clon * slat);
		m.setEntry(0, 1, -slon);
		m.setEntry(0, 2, -clat * clon);
		m.setEntry(1, 0, -slat * slon);
		m.setEntry(1, 1, clon);
		m.setEntry(1, 2, -clat * slon);
		m.setEntry(2, 0, clat);
		m.setEntry(2, 1, 0);
		m.setEntry(2, 2, -slat);
			
		return m;
	}
	
	public static Array2DRowRealMatrix imu2ned(Attitude attitude) {
		double rollRadians = Conversion.deg2Rad(attitude.getRoll());
		double pitchRadians =Conversion.deg2Rad(attitude.getPitch());
		double headingRadians = Conversion.deg2Rad(attitude.getHeading());
		
		double cp = Math.cos(pitchRadians);
		double sp = Math.sin(pitchRadians);
		double ch = Math.cos(headingRadians);
		double sh = Math.sin(headingRadians);
		double cr = Math.cos(rollRadians);
		double sr = Math.sin(rollRadians);
		
		Array2DRowRealMatrix m = new Array2DRowRealMatrix(3, 3);
		
		m.setEntry(0, 0, ch*cp);
		m.setEntry(0, 1, ch*sp*sr-cr*sh);
		m.setEntry(0, 2, ch*cr*sp+sr*sh);
		m.setEntry(1, 0, cp*sh);
		m.setEntry(1, 1, ch*cr+sp*sr*sh);
		m.setEntry(1, 2, sh*cr*sp-ch*sr);
		m.setEntry(2, 0, -sp);
		m.setEntry(2, 1, cp*sr);
		m.setEntry(2, 2, cr*cp);
				 		
		return m;
	}
}
