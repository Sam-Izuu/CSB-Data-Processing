package ca.cidco.csb.ppp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ca.cidco.csb.surveydata.Position;

public class NrcanPPP {
    //Path to NRCAN script	
	private static String nrcanScriptPath= "scripts/csrs_ppp_auto.py";

	
	private String ubxFilePath = "";
	private String fileName = "";
	private String nameNoExt = "";
	private String ubxFileDirectory = "";
	private String workingDirectoryName = "";
	private String directoryRinexName = "";
	private String pppDirectoryName = "";
	private File directoryPpp;

	public NrcanPPP(String ubxFilePath) throws Exception {
		this.ubxFilePath= ubxFilePath;
		initialize();
	}
	
	public PppFile fetchPPP(String nrcanUsername) throws Exception{		
		
        ubxToObs();
        obsToNrcanPpp(nrcanUsername);
        unzipNrcanFile();
        return posToPppFile();
	}
	
	public void initialize() throws Exception {
		
		System.err.println("Converting UBX file " + ubxFilePath);
		
		// Get file in commandline argument
		File ubxFile = new File(ubxFilePath);
		fileName= ubxFile.getName();
		nameNoExt= fileName.replaceAll(".ubx", "");
		nameNoExt = nameNoExt.replaceAll("\\.", "_");
		ubxFileDirectory= ubxFile.getParent();
		
		// Make working directory and sub-directories 
		workingDirectoryName 	= ubxFileDirectory + File.separator + nameNoExt;
		directoryRinexName 		= ubxFileDirectory + File.separator + nameNoExt + File.separator + "Rinex";
		pppDirectoryName 		= ubxFileDirectory + File.separator + nameNoExt + File.separator + "ppp";
		
		File workingDirectory = new File(workingDirectoryName);
		File directoryRinex = new File(directoryRinexName);
		directoryPpp = new File(pppDirectoryName);
		
		ArrayList<File> toMake = new ArrayList<File>(Arrays.asList(workingDirectory, directoryRinex, directoryPpp));
		
		for (File file : toMake) {
			file.mkdir();
		}
	}
	
	public void ubxToObs() throws Exception {
		
		// Use convbin to generate .obs file      
		System.err.println("Generating OBS file in " + directoryRinexName);
		ProcessBuilder processObsBuilder= new ProcessBuilder("convbin", "-r", "ubx","-d", directoryRinexName, ubxFilePath);
		processObsBuilder.inheritIO();
		Process processOBS= processObsBuilder.start();
		processOBS.waitFor();
	}
	public void obsToNrcanPpp(String nrcanUsername) throws Exception{
		
		// Send .obs to NRCAN
		System.err.println("Fetching PPP data from NRCAN in " + pppDirectoryName);
		
		//Rename OBS file to avoid name-chopping by NRCAN's PPP service
		String oldObsFileName = directoryRinexName + File.separator + fileName.replaceAll(".ubx", "")+".obs";
		String newObsFileName = directoryRinexName + File.separator + nameNoExt+".obs";
		File oldObs = new File(oldObsFileName);
		File newObs = new File(newObsFileName);
		oldObs.renameTo(newObs);
		
		//Send it
		ProcessBuilder processBuilderPPP = new ProcessBuilder("python3", nrcanScriptPath,"--user_name",nrcanUsername, "--rnx", newObsFileName, "--results_dir", directoryPpp.getAbsolutePath());
		processBuilderPPP.inheritIO();
		Process processPPP= processBuilderPPP.start();
		processPPP.waitFor();		
	}
	
	public void unzipNrcanFile() throws Exception {
		
		//unzip NRCAN file
		String zipFilePath = pppDirectoryName + File.separator + nameNoExt + "_full_output.zip";
		System.err.println("Unzipping PPP results in " + zipFilePath);
		
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = pppDirectoryName + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
				byte[] bytesIn = new byte[4096];
				int read = 0;
				while ((read = zipIn.read(bytesIn)) != -1) {
					bos.write(bytesIn, 0, read);
				}
				bos.close();
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdirs();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();        
	}
	
	public PppFile posToPppFile() {
		
		String pppPosFileName = pppDirectoryName + File.separator + nameNoExt + ".pos";
		File pppFile = new File(pppPosFileName);
		PppFile ppp = new PppFile();
		
//      Finding heading row
		try (FileReader fileReaderData = new FileReader(pppFile)) {
			BufferedReader bufferedReader = new BufferedReader(fileReaderData);
			String row;
			String[] split_row;			
			
			while ((row = bufferedReader.readLine()) !=null )  {
//    			System.out.println(" While pour le header");
				split_row= row.split("[\\s]{1,}");
				if(split_row[0].equalsIgnoreCase("DIR")){
					break;
				}
			}
			
//    		Match header row with the right pos_format (NRCan return)
			PppHeader header = new PppHeader(row); //now, header.
			
//    		System.out.println(header.toString());  // using to debug when no match
			while ((row = bufferedReader.readLine()) !=null )  {
				
				split_row= row.split("[\\s]{1,}");
				if(split_row[0].equalsIgnoreCase("FWD")|split_row[0].equalsIgnoreCase("FIN")|split_row[0].equalsIgnoreCase("FIX")){
					try {
						//Make timestamp with SimpleDateFormat
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
						String dateTime= split_row[header.getDateIndex()] +" "+ split_row[header.getTimeIndex()];
						java.util.Date parsedDate = dateFormat.parse(dateTime);
						Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
						//set Position parameter
						try {
							Double longitudeDD = Double.valueOf(split_row[header.getLonDdIndex()]);
							Double longitudeMN = Double.valueOf(split_row[header.getLonMnIndex()]);
							Double longitudeSS = Double.valueOf(split_row[header.getLonSsIndex()]);
							Double latitudeDD = Double.valueOf(split_row[header.getLatDdIndex()]); 
							Double latitudeMN = Double.valueOf(split_row[header.getLatMnIndex()]); 
							Double latitudeSS = Double.valueOf(split_row[header.getLatSsIndex()]); 
							Double height = Double.valueOf(split_row[header.getHgtIndex()]);
							Double sdLongitude = Double.valueOf(split_row[header.getSdlonIndex()]);
							Double sdLatitude = Double.valueOf(split_row[header.getSdlatIndex()]);
							Double sdHeight = Double.valueOf(split_row[header.getSdhgtIndex()]);
							Long numberOfSatellites = Long.valueOf(split_row[header.getNsvIndex()]);
							Double gdop = Double.valueOf(split_row[header.getGdopIndex()]);
							
//						    built position with each rows and add them to PppFile
							Position position= new Position(timestamp, longitudeDD,longitudeMN, longitudeSS, latitudeDD, latitudeMN, latitudeSS, height,sdLongitude,sdLatitude,sdHeight,numberOfSatellites,gdop);
							ppp.getPositions().add(position);
						}
						catch (Exception e) {
							System.err.println("Can not built position with : "+row);
							e.printStackTrace();
						}
						
					}
					catch(Exception e) {
						//invalid line, skip it
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println("PPP done");
		
		return ppp;
	}
	
	public static String getNrcanScriptPath() {
		return nrcanScriptPath;
	}
	
	public String getUbxFilePath() {
		return ubxFilePath;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getNameNoExt() {
		return nameNoExt;
	}
	
	public String getUbxFileDirectory() {
		return ubxFileDirectory;
	}
	
	public String getWorkingDirectoryName() {
		return workingDirectoryName;
	}
	
	public String getDirectoryRinexName() {
		return directoryRinexName;
	}
	
	public String getPppDirectoryName() {
		return pppDirectoryName;
	}
}