package ca.cidco.csb;

import ca.cidco.csb.ppp.PppFile;
import ca.cidco.csb.surveydata.Position;
import java.io.IOException;


public class GnssQualifier {
	private boolean ersValid=false;
	private boolean wlrsValid=false;
	
	public GnssQualifier(){
	}
	
	public boolean isErsValid() {
		return ersValid;
	}

	public boolean isWlrsValid() {
		return wlrsValid;
	}

	public void validate(PppFile ppp) throws IOException, InterruptedException {

        double nsv_ppp = 6.0;
        double gdop_ppp = 3.5;
        double sdlat_ppp = 5.0;
        double sdlon_ppp = 5.0;
        double sdhgt_ppp = 0.5;
    	ersValid=true;
    	wlrsValid=true;
    	
		//Read PppFile line by line

        for (Position pos : ppp.getPositions()) {
			
        	// if nsv, gdop, sdlat or sdlon are invalid, invalidate everything  and stop processing file
        	if (pos.getNumberOfSatellites()< nsv_ppp){
        		System.out.println("return false <nsv> " +pos.getNumberOfSatellites()+ "<" +nsv_ppp);
        		ersValid=false;
        		wlrsValid=false;
        		break;
        	}
        	if(pos.getGdop()> gdop_ppp){
        		System.out.println("return false <gdop> " +pos.getGdop()+ ">" +gdop_ppp);
        		ersValid=false;
        		wlrsValid=false;
        		break;
        	}
        	if (pos.getSdLatitude()> sdlat_ppp){
        		System.out.println("return false <sdlat> " +pos.getSdLatitude()+ ">" +sdlat_ppp);
        		ersValid=false;
        		wlrsValid=false;
        		break;
        	}
        	if (pos.getSdLongitude()> sdlon_ppp){
        		System.out.println("return false <sdlon> "+ pos.getSdLongitude()+ ">" +sdlon_ppp);
        		ersValid=false;
        		wlrsValid=false;
        		break;
        	}            		
        	// if gnss height invalid, invalidate ERS only and continue
        	if (pos.getSdHeight()> sdhgt_ppp){
//        		System.out.println("return false <sdhgt> " + pos.getSdHeight()+">" +sdhgt_ppp);
        		ersValid=false;
        	}               				
    	} 
	}
}