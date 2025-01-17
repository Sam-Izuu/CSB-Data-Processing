package ca.cidco.csb.utilities;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Conversion {
	
    /**
     * Le rayon approximatif de la Terre
     */
	final static String EARTH_RADIUS = "6371"; // Approx Earth radius in KM
	public final static double PI = 3.14159265358979323846;

    /**
     * La précision sur les latitudes et de longitudes
     */
    public static final MathContext LAT_LON_PRECISION = new MathContext(12, RoundingMode.HALF_UP);
    


    public static double convertDMStoDecimalDegree(double degrees, double minutes, double seconds) throws IllegalArgumentException {
    	BigDecimal bigDecimalDegrees = null;
    	BigDecimal bigDecimalMinutes = null;
    	BigDecimal bigDecimalSeconds = null;
    	double signe =0;
        // second out of range
        if(degrees>360|degrees<(-360)){
            throw new IllegalArgumentException();
        }
        else{
        	bigDecimalDegrees= BigDecimalFactory.create(degrees);
        }
        if(degrees<0){
        	signe=  -1.0;
        }
        else if (degrees>0) {
        	signe = 1.0;
        }
        
        if(signe <0){
        	bigDecimalDegrees=bigDecimalDegrees.negate();
                    }
        // out of range: minutes >60 or <0
        if(minutes>60|minutes<0){
            throw new IllegalArgumentException();
        }
        else {
        	bigDecimalMinutes= BigDecimalFactory.create(minutes);
        }
        
      // out of range: seconds >60 or <0
         if(seconds>60|seconds<0){
            throw new IllegalArgumentException();
        }
         else {
        	 bigDecimalSeconds= BigDecimalFactory.create(seconds);
         }

        
        return (bigDecimalDegrees.add(bigDecimalMinutes.divide(BigDecimalFactory.create("60"),LAT_LON_PRECISION).setScale(20, RoundingMode.HALF_UP)).add(bigDecimalSeconds.divide(BigDecimalFactory.create("3600"),LAT_LON_PRECISION).setScale(20, RoundingMode.HALF_UP)).multiply(BigDecimalFactory.create(signe))).doubleValue();
    }

    public static double deg2Rad(double deg) {
    	return deg*PI/180;
    }
    public static double rad2Deg(double rad) {
    	return rad*180/PI;
    }

}
