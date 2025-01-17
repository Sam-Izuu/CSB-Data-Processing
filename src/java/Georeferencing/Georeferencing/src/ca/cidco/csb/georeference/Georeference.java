package ca.cidco.csb.georeference;

import java.util.ArrayList;

import ca.cidco.csb.surveydata.Attitude;
import ca.cidco.csb.surveydata.Depth;
import ca.cidco.csb.surveydata.Position;
import ca.cidco.csb.utilities.Interpolation;

public abstract class Georeference {

	public ArrayList<BathymetryPoint> process(ArrayList<Position> positions, ArrayList<Attitude> attitudes, ArrayList<Depth> depths){
		
		ArrayList<BathymetryPoint> bathymetryPoints = new ArrayList<BathymetryPoint>();
				
		int attitudeIndex =0;
		int positionIndex =0;
		
		
		for (Depth depth : depths) {
			try {
				// set attitude index to the highest timestamp before depth timestamp 
				while (attitudeIndex+1 < attitudes.size() && attitudes.get(attitudeIndex+1).getTimestamp().before(depth.getTimestamp())) {
					attitudeIndex ++;
				}
				// if end of attitude list
				if(attitudeIndex >= attitudes.size()-1) {
					break;
				}
				// set positionIndex to the highest timestamp before depth timestamp 
				while (positionIndex+1 < positions.size() && positions.get(positionIndex+1).getTimestamp().before(depth.getTimestamp())) {
					positionIndex ++;
				}
				// if end of positions list
				if(positionIndex >= positions.size()-1) {
					break;
				}
				//No position or attitude smaller than depth, so discard this depth
				if (positions.get(positionIndex).getTimestamp().after(depth.getTimestamp()) || attitudes.get(attitudeIndex).getTimestamp().after(depth.getTimestamp())) {
					continue;
				}
				
				Attitude beforeAttitude = attitudes.get(attitudeIndex);
				Attitude afterAttitude = attitudes.get(attitudeIndex+1);
				
				Position beforePosition = positions.get(positionIndex);
				Position afterPosition = positions.get(positionIndex+1);
				
				
				Attitude interpolatedAttitude = Interpolation.interpolateAttitude(beforeAttitude, afterAttitude, depth.getTimestamp());
				Position interpolatedPosition = Interpolation.interpolatePosition(beforePosition, afterPosition, depth.getTimestamp());
				
				bathymetryPoints.add(georeference(interpolatedPosition, interpolatedAttitude, depth));
				
			}
			catch (Exception e) {
				System.err.println("Error while processing point: "+e.getMessage());
			}
			
		}
		return bathymetryPoints;
	}
		
	protected abstract BathymetryPoint georeference(Position position, Attitude attitude, Depth depth)  throws Exception;
}
