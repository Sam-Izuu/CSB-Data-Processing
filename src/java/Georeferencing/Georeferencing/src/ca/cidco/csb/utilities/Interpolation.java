package ca.cidco.csb.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import ca.cidco.csb.surveydata.Attitude;
import ca.cidco.csb.surveydata.Position;

public class Interpolation {

	
    /**
     * The interpolation algorithm.
     *
     * @param y1 composante y du premier point
     * @param y2 composante y du second point
     * @param x position en x à interpoler
     * @param x1 composante x du premier point
     * @param x2 composante x du second point
     * @return composante y interpolée
     */
	
    public static BigDecimal linearInterpolation(BigDecimal y1, BigDecimal y2, BigDecimal x, BigDecimal x1, BigDecimal x2) {
        BigDecimal deltaTime = BigDecimalFactory.create((double) (x.doubleValue() - x1.doubleValue()) / (double) (x2.doubleValue() - x1.doubleValue()));
        BigDecimal newValue = (y1.add(y2.subtract(y1).multiply(deltaTime))).setScale(20, RoundingMode.HALF_UP);
        return newValue;
    }
    
    /**
     * Returns an interpolated position between two position(position)
     *
     * @param p1 first position
     * @param p2 second position
     * @param timestamp time in microsecond since 1st January 1970
     */
     public static Position interpolatePosition(Position p1, Position p2, Timestamp timestamp) throws Exception {
    // FIXME use angles interpolation   
       double interpLat = linearInterpolationByTime(p1.getLatitude(), p2.getLatitude(), timestamp, p1.getTimestamp(), p2.getTimestamp());
       double interpLon = linearInterpolationByTime(p1.getLongitude(), p2.getLongitude(), timestamp, p1.getTimestamp(), p2.getTimestamp());
       double interpAlt = linearInterpolationByTime(p1.getHeight(), p2.getHeight(), timestamp, p1.getTimestamp(), p2.getTimestamp());
       
//       take worse case scenario quality metrics
       double sdlat= Math.max(p1.getSdLatitude(), p2.getSdLatitude());
       double sdlon= Math.max(p1.getSdLongitude(), p2.getSdLongitude());
       double sdheight= Math.max(p1.getSdHeight(), p2.getSdHeight());
       long nsv= Math.min(p1.getNumberOfSatellites(), p2.getNumberOfSatellites());
       double gdop= Math.max(p1.getGdop(), p2.getGdop());
       
       return new Position(timestamp,interpLat, interpLon, interpAlt, sdlat, sdlon, sdheight, nsv, gdop);
     }


  /**
  * Returns an interpolated attitude between two attitude(attitude)
  *
  * @param a1 first attitude
  * @param a2 second attitude
  * @param timestamp time in microsecond since 1st January 1970
  */
    public static Attitude interpolateAttitude(Attitude a1, Attitude a2,Timestamp timestamp) throws Exception {
	    double interpRoll = linearAngleInterpolationByTime(a1.getRoll(), a2.getRoll(), timestamp, a1.getTimestamp(), a2.getTimestamp());
	    double interpPitch = linearAngleInterpolationByTime(a1.getPitch(), a2.getPitch(), timestamp, a1.getTimestamp(), a2.getTimestamp());
	    double interpHeading = linearAngleInterpolationByTime(a1.getHeading(), a2.getHeading(), timestamp, a1.getTimestamp(), a2.getTimestamp());
	    return new Attitude(timestamp,interpRoll, interpPitch, interpHeading);

    }
    
    /**
     * Returns a linear interpolation between two meter
     *
     * @param y1 first meter
     * @param y2 second meter
     * @param x number of microsecond since 1st January 1970
     * @param x1 timestamp link y1
     * @param x2 timestamp link to y2
     * @throws Exception 
     */
     public static double linearInterpolationByTime(double y1, double y2, Timestamp timestamp, Timestamp t1, Timestamp t2) throws Exception {
    	 long x= timestamp.getTime();
    	 long x1= t1.getTime();
    	 long x2= t2.getTime();
    	 
         if (x1 == x2) {
        	 throw new Exception("The two positions timestamp are the same");
         }
         if (x1 > x) {
        	 throw new Exception("The first position timestamp is higher than interpolation timestamp");
         }
         if (x1 > x2) {
             throw new Exception("The first position timestamp is higher than the second position timestamp");
         }
       double result = (y1 + (y2 - y1)*(x - x1) / (x2 - x1));
       return result;
     }
     
     
     

     /**
     * Returns a linear interpolation between two angle
     *
     * @param psi1 first angle
     * @param psi2 second angle
     * @param l number of microsecond since 1st January 1970
     * @param t1 timestamp link to psi1
     * @param t2 timestamp link to psi2
     * @throws Exception 
     */
     
     public static double linearAngleInterpolationByTime(double psi1, double psi2, Timestamp l, Timestamp t1, Timestamp t2) throws Exception {
    	 
    	 long x= l.getTime();
    	 long x1= t1.getTime();
    	 long x2= t2.getTime();
    	 
    	 if (x1 == x2) {
	         throw new Exception("The two positions timestamp are the same");
    	 }
    	 if (x1 > x) {
	         throw new Exception("The first position timestamp is higher than interpolation timestamp");
    	 }
    	 if (x1 > x2){
	         throw new Exception("The first position timestamp is higher than the second position timestamp");
    	 }
    	 if ( Math.abs(psi2 - psi1)==180){
    		 throw new Exception("The angles " + psi1 + " and " + psi2 + " have a difference of 180 degrees which means there are two possible answers at timestamp " + x);
    	 }
    	 if (psi1 == psi2) {
    		 return psi1;
    	 }
	
		 double z1 = x-x1;
		 double z2 = x2-x1;
		 double delta = (z1 / z2);
		 double dpsi=0;
		 double total=0;
		 // convert negative to positive degrees		 
		 if(psi1<0) {
			 psi1= 360.0 + psi1;
		 }
		 if(psi2<0) {
			 psi2= 360.0 + psi2;
		 }
		 
		 if (Math.max(psi1, psi2)-(Math.min(psi1, psi2))<180) {
			 // no step over 360 
			 if (psi1<psi2) {
				 dpsi = Math.IEEEremainder((Math.IEEEremainder(psi1 -360 -psi2, 360) + 540), 360) - 180;
				 dpsi= Math.abs(dpsi);
				 total = psi1 + dpsi*delta;
			 }
			 else {
				 dpsi = Math.IEEEremainder((Math.IEEEremainder(psi2 -360 -psi1, 360) + 540), 360) - 180;
				 if(dpsi>0) {
					 dpsi= dpsi*-1;
				 }
				 total = psi1 + dpsi*delta;
			 }
		 }
		 
		 if (Math.max(psi1, psi2)-(Math.min(psi1, psi2))>180) {
			 // step over 360  
			 if(psi1<psi2){
				 dpsi = Math.IEEEremainder((Math.IEEEremainder(psi2- psi1, 360) + 540), 360) - 180;
				 total = psi1 + dpsi*delta;
			 }
			 else {
				 dpsi = Math.IEEEremainder((Math.IEEEremainder(psi1-360 - psi2, 360) + 540), 360) - 180;
				 total = psi1 + Math.abs(dpsi)*delta;
			 }
		 }
//		 System.err.println(psi1);
//		 System.err.println(psi2);
//		 System.err.println(dpsi);
//		 System.err.println(total);
		 
		 if(total > 0){
			 return (total < 360.0)? total : Math.IEEEremainder(total,360.0);
		 }
		 else if(total < 0){
			 return total + 360.0; // convert to positive degrees
		 }
		 else{
			 return 0.0;
		 }
     }
}
