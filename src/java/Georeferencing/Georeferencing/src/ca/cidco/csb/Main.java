package ca.cidco.csb;

import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import ca.cidco.csb.georeference.BathymetryPoint;
import ca.cidco.csb.georeference.ErsGeoreferencing;
import ca.cidco.csb.georeference.Georeference;
import ca.cidco.csb.georeference.WlrsGeoreferencing;
import ca.cidco.csb.ppp.NrcanPPP;
import ca.cidco.csb.ppp.PppFile;
import ca.cidco.csb.surveyplatform.hydroblock.Hydroblock20;
import ca.cidco.csb.utilities.Conversion;

public class Main {

	public static void main(String[] args) {
		//TODO: Testing CLI 

/*		String gnssBinaryFile="";
		if (args.length > 0) 	
		    try {
		    	gnssBinaryFile = new java.io.File(args[0]).getAbsolutePath();
		    } catch (Exception e) {
		        System.err.println("Argument " + args[0] + " must be an Binary File.");
		        System.exit(1);
		    }
		}
		//If no valid args, use test File 
		else {
			gnssBinaryFile = test8;
			System.err.println("No arg in CLI, processing this test file :"+gnssBinaryFile);
		}
*/		
		try {
			String nrcanUsername = "dominic.gonthier@cidco.ca";
//			String dataFolder = "/home/dominic/Bureau/Git/CSB-Data-Processing/src/java/Georeferencing/Georeferencing/src/ca/cidco/csb/test/data/2022_10_11_224413";
			String dataFolder = "data/hydroblockReadDataTest/completeData/";

			RealVector leverArm = new ArrayRealVector(new double[]{1,2,3}); //TODO: get lever arm from....CLI ? 
			
			//read Hydroblock Data
			Hydroblock20 hydro = new Hydroblock20();
			hydro.read(dataFolder);
			//get ppp data from NRCAn using ubxFile, and replace hydroblock position data with nrcan ppp data
			NrcanPPP nrcan = new NrcanPPP(hydro.getUbxPath());
			PppFile pppFile = nrcan.fetchPPP(nrcanUsername);
			hydro.setPositions(pppFile.getPositions());
			
			//Qualify Gnss data to determine which georeferencing method to use
			GnssQualifier gnss = new GnssQualifier();
			gnss.validate(pppFile);
			
			if (gnss.isWlrsValid() || gnss.isErsValid()){
				Georeference geo=(gnss.isErsValid())?new ErsGeoreferencing(leverArm):new WlrsGeoreferencing(); 
				List<BathymetryPoint> points = geo.process(hydro.getPositions(), hydro.getAttitudes(), hydro.getDepths());
				for(BathymetryPoint p : points) {
					System.out.println(p.toString());
				}
				System.err.println("points.size : "+points.size());
			}
			else {
				throw new Exception("No valid georeferencing method available");
			}
			
			
			

			
		}
		catch(Exception e) {
			System.err.println("Error while georeferencing: " + e.getMessage() );
		}
			

		

	}
}
