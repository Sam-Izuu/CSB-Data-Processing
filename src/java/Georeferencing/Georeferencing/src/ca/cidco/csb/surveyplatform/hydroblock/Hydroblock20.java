package ca.cidco.csb.surveyplatform.hydroblock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ca.cidco.csb.GnssQualifier;
import ca.cidco.csb.ppp.NrcanPPP;
import ca.cidco.csb.ppp.PppFile;
import ca.cidco.csb.surveydata.Attitude;
import ca.cidco.csb.surveydata.Depth;
import ca.cidco.csb.surveydata.Position;

public class Hydroblock20 {
	private ArrayList<Position> positions= new ArrayList<Position>();
	private ArrayList<Attitude> attitudes = new ArrayList<Attitude>();
	private ArrayList<Depth>	depths = new ArrayList<Depth>();
	private String imuPath = "";
	private String sonarPath = "";
	private String ubxPath = "";

	public Hydroblock20() { 	
	}
	
	public void read(String dataPath) throws Exception {
		//clear data lists
		positions.clear();
		attitudes.clear();
		depths.clear();
		
		//clear path
		imuPath= null;
		sonarPath = null;
		ubxPath = null;
		
		// Read folder and find imu, sonar and ubx files
        File directory = new File(dataPath);
        String[] fileList = directory.list();
        if (fileList == null) {
            throw new Exception("Hydroblock2.0: Directory is empty "+dataPath);
        }
        else {
  
            // Linear search in the array
            for (int i = 0; i < fileList.length; i++) {
                String filename = fileList[i];
                if (filename.contains("imu.txt")) {
//                    System.out.println(filename + " => imu found");
                    imuPath= dataPath+File.separator+filename;
                }
                else if (filename.contains("sonar.txt")) {
//                    System.out.println(filename + " => sonar found");
                    sonarPath= dataPath+File.separator+filename;
                }
                else if (filename.contains(".ubx")) {
//                    System.out.println(filename + "  => ubx found");
                    ubxPath= dataPath+File.separator+filename;
                }
            }
        }
//        Read IMU File
		if (imuPath!= null) {
//			System.out.println("imuPath :" + imuPath);
			readImu(imuPath); 
		}
		else {
			throw new Exception("No IMU file in the directory");
		}
		
//		Read Sonar File
		if (sonarPath!=null) {
//			System.out.println("sonarPath :" + sonarPath);
			readSonar(sonarPath); 
		}
		else {
            throw new Exception("No sonar file in the directory");
		}
		
		
//		TODO read GNSS File
		
//		Process and read UBX File
		if (ubxPath!=null) {
//			caller can use ubxFile
			
		}
		else {
			throw new Exception("No ubx file in the directory");
		}
	}
	
	
	public String getImuPath() {
		return imuPath;
	}

	public String getSonarPath() {
		return sonarPath;
	}

	public String getUbxPath() {
		return ubxPath;
	}

	protected void readImu(String imuPath) throws Exception{ 

		File imuFile = new File(imuPath);
		FileReader fileReaderData = new FileReader(imuFile);
		BufferedReader bufferedReader = new BufferedReader(fileReaderData);
		String row;
		String[] split_row;	
		
		while ((row = bufferedReader.readLine()) !=null )  {
			split_row= row.split("[;]{1}");
			
			//skip header
			if(split_row[0].contains("Timestamp")){
				//do nothing
			}
			else {
				try {
					
					//Make timestamp with SimpleDateFormat
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
					String dateTime= split_row[0];
				    java.util.Date parsedDate = dateFormat.parse(dateTime);
				    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
					
					Double heading = Double.valueOf(split_row[1]); 
					Double pitch = Double.valueOf(split_row[2]); 
					Double roll = Double.valueOf(split_row[3]);

//						    built attitude with each rows and add them to attitudes
					Attitude attitude = new Attitude(timestamp, heading, pitch, roll);
					
					attitudes.add(attitude);
					
				}
				catch (Exception e) {
					System.err.println("Can not built Attitude with : "+row);
					e.printStackTrace();
				}
			}

		}
		bufferedReader.close();
		if (attitudes.isEmpty()) {
			System.err.println("No Valid attitude in the IMU file");
			throw new Exception("No Valid attitude in the IMU file");
		}
	}

	protected void readSonar(String sonarPath) throws Exception{ 
		File sonarFile = new File(sonarPath);
		FileReader fileReaderData = new FileReader(sonarFile);
		BufferedReader bufferedReader = new BufferedReader(fileReaderData);
		String row;
		String[] split_row;	
		
		while ((row = bufferedReader.readLine()) !=null )  {
			split_row= row.split("[;]{1}");
			
			//skip header
			if(split_row[0].contains("Timestamp")){
				//do nothing
			}
			else {
				try {
					//Make timestamp with SimpleDateFormat
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
					String dateTime= split_row[0];
				    java.util.Date parsedDate = dateFormat.parse(dateTime);
				    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
					
					Double depth_ = Double.valueOf(split_row[1]); 

				 // built Depth with each rows and add them to depths
					Depth depth = new Depth(timestamp, depth_);
					depths.add(depth);
				}
				catch (Exception e) {
					System.err.println("Can not built Depth with : "+row);
					e.printStackTrace();
				}
			}
		}
		if (depths.isEmpty()) {
			System.err.println("No Valid depth in the sonar file");
			throw new Exception("No Valid depth in the sonar file");
		}

	}
		

	public ArrayList<Position> getPositions() {
		return positions;
	}


	public void setPositions(ArrayList<Position> positions) {
		this.positions = positions;
	}


	public ArrayList<Attitude> getAttitudes() {
		return attitudes;
	}


	public ArrayList<Depth> getDepths() {
		return depths;
	}
	
}
