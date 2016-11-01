package severalCodes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import customization.Constants;

public class GoldSetResultsComparison {

	private class GoldSetObj {
		String path1;
		String path2;
		String WebService;
		String WebFunction;

		public GoldSetObj(String path1, String path2, String WebService,
				String WebFunction) {
			this.path1 = path1;
			this.path2 = path2;
			this.WebService = WebService;
			this.WebFunction = WebFunction;
		}
	}

	private class ResultFileObject {
		String path1;
		String path2;
		float value;
		String WebService;
		String WebFunction;

		public ResultFileObject(String path1, String path2, float value,
				String WebService, String WebFunction) {
			this.path1 = path1;
			this.path2 = path2;
			this.value = value;
			this.WebService = WebService;
			this.WebFunction = WebFunction;
		}
	}

	private ArrayList<GoldSetObj> goldSetLines;
	private ArrayList<ResultFileObject> resultLines;
	private ArrayList<ResultFileObject> ARMLines;

	private ArrayList<ResultFileObject> existInGoldSetAndInResults;

	private static ArrayList<ResultFileObject> discoveredWithARMAll = new ArrayList<ResultFileObject>();

	private static ArrayList<ResultFileObject> NotdiscoveredWithARMAll = new ArrayList<ResultFileObject>();

	private ArrayList<GoldSetObj> existOnlyInGoldSet;
	private ArrayList<ResultFileObject> existOnlyInResults;

	private static ArrayList<GoldSetObj> OnlyInGoldSetAll = new ArrayList<GoldSetObj>();

	// private static ArrayList<GoldSetObj> OnlyInGoldSetAll = new
	// ArrayList<GoldSetObj>();

	public GoldSetResultsComparison() {
		goldSetLines = new ArrayList<GoldSetObj>();
		resultLines = new ArrayList<ResultFileObject>();
		ARMLines = new ArrayList<ResultFileObject>();

		existInGoldSetAndInResults = new ArrayList<ResultFileObject>();
		existOnlyInGoldSet = new ArrayList<GoldSetObj>();
		existOnlyInResults = new ArrayList<ResultFileObject>();
	}

	public void parseGoldsetFile(String filePath, String WebService,
			String WebFunction) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.isEmpty()) {
					String[] line = sCurrentLine
							.split(Constants.separatorSpace);
					GoldSetObj goldObj = new GoldSetObj(line[0], line[1],
							WebService, WebFunction);
					goldSetLines.add(goldObj);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pareResultFile(String filePath, String WebService,
			String WebFunction) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.isEmpty()) {
					String[] line = sCurrentLine
							.split(Constants.separatorSpace);
					if (Double.parseDouble(line[2]) < Constants.precisionRecallThreshold)
						break;

					ResultFileObject resultObj = new ResultFileObject(line[0],
							line[1], Float.parseFloat(line[2]), WebService,
							WebFunction);
					resultLines.add(resultObj);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pareARMFile(int xmlToYago, String filePath, String WebService,
			String WebFunction) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.isEmpty()) {
					String[] line = sCurrentLine
							.split(Constants.separatorSpace);
					if (Double.parseDouble(line[2]) < Constants.precisionRecallThreshold)
						break;
					ResultFileObject resultObj;
					if (xmlToYago == 1) {
						resultObj = new ResultFileObject(line[1], line[0],
								Float.parseFloat(line[2]), WebService,
								WebFunction);
					} else {
						resultObj = new ResultFileObject(line[0], line[1],
								Float.parseFloat(line[2]), WebService,
								WebFunction);
					}
					ARMLines.add(resultObj);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void checkIfExistInResultAndGoldSetOrOnlyInResult() {
		for (ResultFileObject resultObj : resultLines) {
			int exist = 0;
			for (GoldSetObj goldSetObj : goldSetLines) {

				if (resultObj.path1.equals(goldSetObj.path1)
						&& resultObj.path2.equals(goldSetObj.path2)) {
					// Pair exists in Gold Set

					existInGoldSetAndInResults.add(resultObj);

					exist = 1;
                                        break;

				}
			}
			if (exist == 0) { // Pair Does not exist in Gold Set
				existOnlyInResults.add(resultObj);

			}
		}
	}

	public void checkIfExistOnlyInGoldSet() {
		for (GoldSetObj goldSetObj : goldSetLines) {
			int exist = 0;
			for (ResultFileObject resultObj : resultLines) {

				if (resultObj.path1.equals(goldSetObj.path1)
						&& resultObj.path2.equals(goldSetObj.path2)) {
					// Pair exists in Gold Set
					// I Do Not add it again to existInGoldSetAndInResults!
					// I added it in the function
					// checkIfExistInResultAndGoldSetOrOnlyInResult()

					exist = 1;

				}
			}
			if (exist == 0) {
				// Pair Does not exist in Result

				existOnlyInGoldSet.add(goldSetObj);
				OnlyInGoldSetAll.add(goldSetObj);

			}
		}
	}

	public void checkIfExistInARMandInGoldset() {
		for (ResultFileObject resultObj : ARMLines) {
			int exist = 0;
			for (GoldSetObj goldSetObj : existOnlyInGoldSet) {

				if (resultObj.path1.equals(goldSetObj.path1)
						&& resultObj.path2.equals(goldSetObj.path2)) {
					// Pair exists in Gold Set

					discoveredWithARMAll.add(resultObj);
					// existInGoldSetAndInResults.add(resultObj);

					exist = 1;

				}
			}
			if (exist == 0) { // Pair Does not exist in Gold Set
				NotdiscoveredWithARMAll.add(resultObj);

			}
		}
	}

	public void writeToFile(String filePath) {
		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(filePath);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
			} else {
				out = new BufferedWriter(new FileWriter(file, false));
			}

			out.write("-------\t\tThreshold = "
					+ Constants.precisionRecallThreshold + "\t\t-------\n\n");
			out.write("===========================================================\n");
			out.write("PATHS THAT EXIST TO BOTH GOLD SET AND RESULT FILE!\n");
			out.write("===========================================================\n\n");
			for (ResultFileObject resultObj : existInGoldSetAndInResults) {
				out.write(resultObj.path1 + "\t\t" + resultObj.path2 + "\t\t"
						+ resultObj.value + "\n");
			}
			out.write("\n\n===========================================================\n");
			out.write("PATHS THAT EXIST ONLY TO RESULT FILE!\n");
			out.write("===========================================================\n\n");

			for (ResultFileObject resultObj : existOnlyInResults) {
				out.write(resultObj.path1 + "\t\t" + resultObj.path2 + "\t\t"
						+ resultObj.value + "\n");
			}

			out.write("\n\n===========================================================\n");
			out.write("PATHS THAT EXIST ONLY TO GOLD SET FILE!\n");
			out.write("===========================================================\n\n");

			for (GoldSetObj goldSetObj : existOnlyInGoldSet) {
				out.write(goldSetObj.path1 + "\t\t" + goldSetObj.path2 + "\n");
			}

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void writeToFilePathsApearOnlyInGoldSet(String filePath) {
		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(filePath);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
			} else {
				out = new BufferedWriter(new FileWriter(file, false));
			}

		
			for (GoldSetObj goldSetObj : existOnlyInGoldSet) {
				out.write(goldSetObj.WebService+":"+goldSetObj.WebFunction +"\t"+goldSetObj.path1 + "\t\t" + goldSetObj.path2+"\n");
			}

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
        
        public  void writeToFilePathsApearOnlyInResultsFile(String filePath) {
		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(filePath);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
			} else {
				out = new BufferedWriter(new FileWriter(file, false));
			}
                        
                        for (ResultFileObject resultObj : existOnlyInResults) {
				out.write(resultObj.WebService+":"+resultObj.WebFunction+"\t\t"+resultObj.path1 + "\t\t" + resultObj.path2 + "\t\t"
						+ resultObj.value + "\n");
			}
                        
                        

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void writeToFileARM(String filePath) {
		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(filePath);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
			} else {
				out = new BufferedWriter(new FileWriter(file, false));
			}

			out.write("-------\t\tThreshold = "
					+ Constants.precisionRecallThreshold + "\t\t-------\n\n");

			out.write("\n\n===========================================================\n");
			out.write("PATHS THAT ARE DISCOVERED FROM ARM!\n");
			out.write("===========================================================\n\n");

			for (ResultFileObject goldSetObj : discoveredWithARMAll) {
				out.write(goldSetObj.path1 + "\t\t" + goldSetObj.path2 + "\t\t"
						+ goldSetObj.value + "\t\t" + goldSetObj.WebService
						+ "\t\t" + goldSetObj.WebFunction + "\n\n");
			}

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		File folder = new File(Constants.sortedPairsDirectory);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {

				GoldSetResultsComparison object = new GoldSetResultsComparison();

				String fileName = file.getName();
				String[] fileNameTable = fileName.split("_");

				String pathForGoldSet;
				String pathForSortedPairs;
//				String pathForARM = Constants.projectDirectory
//						+ "pcaConf/XMLImpliesYago/" + fileName;

				String WS;
				String WF;
				if (fileNameTable.length == 4) {
					pathForGoldSet = Constants.GoldSets +"goldsetsPathPairs/"+ fileNameTable[2]
							+ "_" + fileNameTable[1] + "_" + "GoldSet.txt";
					WS = fileNameTable[2];
					WF = fileNameTable[1];

				} else if (fileNameTable.length > 4) {
					pathForGoldSet = Constants.GoldSets+"goldsetsPathPairs/" + fileNameTable[2]
							+ "_" + fileNameTable[3] + "_" + fileNameTable[1]
							+ "_" + "GoldSet.txt";
					WS = fileNameTable[2] + "_" + fileNameTable[3];
					WF = fileNameTable[1];
				} else {
					continue;
				}
				System.out.println("PathForGoldSet:" + pathForGoldSet);

				pathForSortedPairs = Constants.sortedPairsDirectory + fileName;
				System.out.println("pathForSortedPairs" + pathForSortedPairs);
			//	System.out.println("pathForARM" + pathForARM);

				System.out.println("WS: " + WS + "\t WF: " + WF);

				object.parseGoldsetFile(pathForGoldSet, WS, WF);
				object.pareResultFile(pathForSortedPairs, WS, WF);
			//	object.pareARMFile(1, pathForARM, WS, WF);

				object.checkIfExistInResultAndGoldSetOrOnlyInResult();
				object.checkIfExistOnlyInGoldSet();

			//	object.checkIfExistInARMandInGoldset();
                         //       object.writeToFilePathsApearOnlyInResultsFile(Constants.OverlappingWithGoldSetsDirectory+"PathPairs_ResultsNotExistInGoldSet/"+fileName.split("Pairs.txt")[0]+"NotInGoldSet.txt");
                                object.writeToFilePathsApearOnlyInGoldSet(Constants.OverlappingWithGoldSetsDirectory+"PathPairs_ResultExistONLYInGoldSet/"+fileName.split("Pairs.txt")[0]+"OnlyInGoldSet.txt");
			//	 object.writeToFile(Constants.OverlappingWithGoldSetsDirectory+fileName.split("Pairs.txt")[0]+"OverlappingWithGoldSet.txt");
			}
		}

		// writeToFileOnlyGoldSet("/Users/mary/Dropbox/OASIS/Mary-Data/pcaConf/listOfPathsOnlyInGoldSets.txt");
		//writeToFileARM("/Users/mary/Dropbox/OASIS/Mary-Data/pcaConf/listOfPathsOnlyFromARM_XMLToYago.txt");
	}

}
