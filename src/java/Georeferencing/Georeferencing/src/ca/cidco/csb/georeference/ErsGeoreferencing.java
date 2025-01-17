package ca.cidco.csb.georeference;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import ca.cidco.csb.surveydata.Attitude;
import ca.cidco.csb.surveydata.Depth;
import ca.cidco.csb.surveydata.Position;
import ca.cidco.csb.utilities.Conversion;
import ca.cidco.csb.utilities.Geodesy;

public class ErsGeoreferencing extends Georeference{

	RealVector leverArm;
	double a= Geodesy.a;
	double e= Geodesy.e;
	
	public ErsGeoreferencing(RealVector leverArm) {
		this.leverArm= leverArm;
	}
	
	@Override
	public BathymetryPoint georeference(Position position, Attitude attitude, Depth depth)  throws Exception {
		
		//Convert Euler angles to radians
		
		//Init IMU to NED matrix
		Array2DRowRealMatrix imu2ned = Geodesy.imu2ned(attitude);

		//Init NED to ECEF Matrix
		Array2DRowRealMatrix ned2ecef = Geodesy.ned2ecef(position);

		//We have a position in the ECEF frame
		RealVector positionECEF= Geodesy.LLH_2_ECEF(position.getLatitude(), position.getLongitude(), position.getHeight());
		
		//convert ping from IMU frame to ECEF frame
		RealVector pingECEF = ned2ecef.operate(imu2ned.operate(new ArrayRealVector(new double[] { 0.0, 0.0, depth.getDepth() })));
		//convert lever arm from IMU frame to ECEF
		RealVector leverArmECEF = ned2ecef.operate(imu2ned.operate(leverArm));
		
		//Georeferenced sounding is the sum of the 3 vectors (in the ECEF frame)
		RealVector georefPing = positionECEF.add(pingECEF).add(leverArmECEF) ;
		
		
		//Convert georeferenced ping in ECEF frame to Lat/Lon coordinates
		RealVector georef = Geodesy.ECEF_2_LatLonH(georefPing.getEntry(0), georefPing.getEntry(1), georefPing.getEntry(2));
		
		double latGeoref = georef.getEntry(0);
		double lonGeoref = georef.getEntry(1);
		double hGeoref = georef.getEntry(2);
		
		BathymetryPoint bathymetryPoint= new BathymetryPoint(depth.getTimestamp(), lonGeoref, latGeoref, hGeoref, position.getSdLongitude(), position.getSdLatitude(), position.getSdHeight());
		
		return bathymetryPoint;	
	}

}
