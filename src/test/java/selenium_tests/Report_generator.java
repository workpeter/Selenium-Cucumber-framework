package selenium_tests;

import java.io.File;
import java.util.*;

import net.masterthought.cucumber.ReportBuilder;

public class Report_generator {

	public synchronized static String GenerateMasterthoughtReport() {

		String rootDir = System.getProperty("user.dir");

		try{

			File reportOutputDirectory = new File("target/Masterthought");
			List<String> list = new ArrayList<String>();
			list.addAll(getListOfJsonReports(rootDir , "target"));
			//list.addAll(getListOfJsonReports(rootDir , "target\\parallel-tests"));
			String pluginUrlPath = "";
			String buildNumber = "1";
			String buildProject = "cucumber-jvm";
			boolean skippedFails = false;
			boolean pendingFails = false;
			boolean undefinedFails = true;
			boolean missingFails = true;
			boolean flashCharts = true;
			boolean runWithJenkins = false;
			boolean highCharts = true;
			boolean parallelTesting = true;
			//boolean artifactsEnabled = false;
			//String artifactConfig = "";

			ReportBuilder reportBuilder = new ReportBuilder(list, reportOutputDirectory, pluginUrlPath, buildNumber,
					buildProject, skippedFails, pendingFails, undefinedFails, missingFails, flashCharts, runWithJenkins,
					highCharts, parallelTesting);

			reportBuilder.generateReports();
		}catch(Exception e){
			e.printStackTrace();
		}

		 return System.getProperty("user.dir") + "/target/Masterthought/feature-overview.html";
		
	}

	public static List<String> getListOfJsonReports(String rootDir, String dir){

		List<String> list = new ArrayList<String>();

		File folder = new File(rootDir + "/" + dir);

		if (folder.listFiles() != null){

			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile() && listOfFiles[i].length()> 0) {
					if (listOfFiles[i].getName().contains(".json")){

						String filepath = dir + "/" + listOfFiles[i].getName();

						//System.out.println(filepath);

						list.add(filepath);

					}
				} 
			}
		}
		return list;

	}

}
