package ca.cidco.csb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import ca.cidco.csb.ppp.NrcanPPP;
import ca.cidco.csb.ppp.PppFile;

public class NrcanPppTest {
	
	String test_ubxPath = "data/ubx/2022.10.10_194410.ubx"; 

	
	@Test
	public void nrcanPppConstructorTest() throws Exception {
		NrcanPPP nrcan = new NrcanPPP(test_ubxPath);
		
		assertTrue( nrcan.getUbxFilePath() == "data/ubx/2022.10.10_194410.ubx");
		assertTrue( nrcan.getFileName().equals("2022.10.10_194410.ubx"));
		assertTrue( nrcan.getNameNoExt().equals("2022_10_10_194410"));
		assertTrue( nrcan.getUbxFileDirectory().equals("data/ubx"));
		assertTrue( nrcan.getWorkingDirectoryName().equals("data/ubx/2022_10_10_194410"));
		assertTrue( nrcan.getDirectoryRinexName().equals("data/ubx/2022_10_10_194410/Rinex"));
		assertTrue(nrcan.getPppDirectoryName().equals("data/ubx/2022_10_10_194410/ppp"));
	}
	
	@Test
	public void nrcanGetNrcanScriptPath() throws Exception {
		NrcanPPP nrcan = new NrcanPPP(test_ubxPath);
		
		assertTrue( nrcan.getNrcanScriptPath() == "scripts/csrs_ppp_auto.py");
	}

	
	@Test
    public void ubxToObsTest() throws Exception {
		
		File rinexFileBefore = new File("data/ubx/2022_10_10_194410/Rinex/2022.10.10_194410.obs");
		// delete 2022.10.10_194410.obs if exist
		rinexFileBefore.delete();
		assertFalse(rinexFileBefore.exists());
		
		NrcanPPP nrcan = new NrcanPPP(test_ubxPath);
		nrcan.ubxToObs();
		
		// validate 2022.10.10_194410.obs created
		File rinexFileAfter = new File("data/ubx/2022_10_10_194410/Rinex/2022.10.10_194410.obs");
		assertTrue(rinexFileAfter.exists());
	}

	@Test
    public void obsToNrcanPppTest() throws Exception {
		
		File pppFileZip = new File("data/ubx/2022_10_10_194410/ppp/2022_10_10_194410_full_output.zip");
//		delete 022_10_10_194410_full_output.zip if exist
		pppFileZip.delete();
		assertFalse(pppFileZip.exists());
		
		NrcanPPP nrcan = new NrcanPPP(test_ubxPath);
		nrcan.obsToNrcanPpp("dominic.gonthier@cidco.ca");
		
		// validate receive 2022_10_10_194410_full_output.zip from Nrcan
		pppFileZip = new File("data/ubx/2022_10_10_194410/ppp/2022_10_10_194410_full_output.zip");
		assertTrue(pppFileZip.exists());		
	}
	
	@Test
    public void	unzipNrcanFileTest() throws Exception {
	
		File posFile = new File("data/ubx/2022_10_10_194410/ppp/2022_10_10_194410.pos");
		// delete 022_10_10_194410.pos if exist
		posFile.delete();
		assertFalse(posFile.exists());
		
		NrcanPPP nrcan = new NrcanPPP(test_ubxPath);
		nrcan.unzipNrcanFile();
		
		posFile = new File("data/ubx/2022_10_10_194410/ppp/2022_10_10_194410.pos");
		assertTrue(posFile.exists());
	}
	
	@Test 
	public void posToPppFileTest() throws Exception {
		
		NrcanPPP nrcan = new NrcanPPP(test_ubxPath);
		PppFile ppp= nrcan.posToPppFile();
		assertEquals(3601, ppp.getPositions().size());
		// assert that first row (longitude -68 degrees 30 min 52.68288 secondes match with externe converter "-68.51463 DD")
		assertTrue (ppp.getPositions().get(0).getLongitude()<= -68.51463+0.00001 && ppp.getPositions().get(0).getLongitude() >-68.51463 -0.00001 );
	}

	@Test 
	public void nrcanPipelineTest() throws Exception {
		
		NrcanPPP nrcan = new NrcanPPP(test_ubxPath);
		PppFile ppp=nrcan.fetchPPP("dominic.gonthier@cidco.ca");
		// assert pipeline result same than previous tests
		assertEquals(3601, ppp.getPositions().size());
	}
}
	
