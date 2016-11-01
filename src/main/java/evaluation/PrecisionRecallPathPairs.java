package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import customization.Constants;
import dataguide.XMLPathPair;

public class PrecisionRecallPathPairs {

	static ArrayList<GoldSetObjectPathPairs> goldSet;
	static float precision;
	static float recall;

	public PrecisionRecallPathPairs() {

	}

	public void parseGoldSetFile(String filepath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(Constants.separatorSpace);
				GoldSetObjectPathPairs pair = new GoldSetObjectPathPairs(line[0], line[1]);
				goldSet.add(pair);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void parseResultsFile(String filepath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;
			int numberofPairsFoundInGoldSet = 0;
			int totalNumberOfPairsGreaterThanThreshold = 0;
			while ((sCurrentLine = br.readLine()) != null) {

				String[] line = sCurrentLine.split(Constants.separatorSpace);
				if (Double.parseDouble(line[2]) < Constants.precisionRecallThreshold)
					break;

				totalNumberOfPairsGreaterThanThreshold++;
				/* This for is for the case in PcaConf of XML implies Yago, where the first column of the file is the xml path*/
//				for (int i = 0; i < goldSet.size(); i++) {
//					System.out.println(i);
//					if (line[1].equals(goldSet.get(i).getPart1())
//							&& line[0].equals(goldSet.get(i).getPart2())) {
//						numberofPairsFoundInGoldSet++;
//					}
//				}
				/* This for is for the case in PcaConf of Yago implies XML, where the first column of the file is the Yago property path*/
				for (int i = 0; i < goldSet.size(); i++) {
					if (line[0].equals(goldSet.get(i).getPart1())
							&& line[1].equals(goldSet.get(i).getPart2())) {
						numberofPairsFoundInGoldSet++;
					}
				}
			}
			System.out.println("File:"+filepath);
			System.out.println("numberOfPairsFoundInGoldSet: "
					+ numberofPairsFoundInGoldSet);
			System.out.println("totalNumberOfPairsGreaterThanThreshold:"
					+ totalNumberOfPairsGreaterThanThreshold);
			System.out.println("goldSetSize:" + goldSet.size() + "------------------------------------\n\n\n");

			PrecisionRecallPathPairs.precision = precision(numberofPairsFoundInGoldSet,
					totalNumberOfPairsGreaterThanThreshold);
			PrecisionRecallPathPairs.recall = recall(numberofPairsFoundInGoldSet,
					(float) goldSet.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static float precision(float numberofPairsFoundInGoldSet,
			float totalNumberOfPairs) {
		return numberofPairsFoundInGoldSet / totalNumberOfPairs;
	}

	public static float recall(float numberofPairsFoundInGoldSet,
			float nuberOfPairsInGoldSet) {
		return numberofPairsFoundInGoldSet / nuberOfPairsInGoldSet;
	}

	public float Fmeasure() {
		return 2 * ((PrecisionRecallPathPairs.precision * PrecisionRecallPathPairs.recall) / (PrecisionRecallPathPairs.precision + PrecisionRecallPathPairs.recall));
	}

	public void PrecisionRecalleInFile(String functionPath, BufferedWriter out) {

		try {

			out.write(functionPath + ":\t\t");
			out.write("Precision:\t" + PrecisionRecallPathPairs.precision + "\t\tRecall:\t"
					+ PrecisionRecallPathPairs.recall + "\t\tF-measure:\t" + Fmeasure() + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static BufferedWriter createFileForPrecisionRecall(String path) {
	
		FileWriter fstream;
		BufferedWriter out = null;
		try {
			
			File file = new File(path);
			 if (!file.exists()) {
				 fstream = new FileWriter(file);
				 out = new BufferedWriter(fstream);
//				 out.write("-------\t\tThreshold = "
//							+ Constants.precisionRecallThreshold + "\t\t-------\n\n");
			 }else{
				 out = new BufferedWriter(new FileWriter(file, true));
			 }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

	public static void main(String[] args) throws Exception {

		BufferedWriter out = createFileForPrecisionRecall(Constants.precisionRecallDirectory
				+ "PrecisionRecallResults"
				+ Constants.precisionRecallThreshold + "XMLImpliesDBP.txt");
				
				File folder = new File(Constants.sortedPairsDirectory);
				File[] listOfFiles = folder.listFiles();

				for (File file : listOfFiles) {
				    if (file.isFile()) {
				    	
				    	goldSet = new ArrayList<GoldSetObjectPathPairs>();

						PrecisionRecallPathPairs precisionRecall = new PrecisionRecallPathPairs();

				    
				    	String fileName = file.getName();
				    	System.out.println(fileName);
				    	String[] fileNameTable = fileName.split("_");
				    	String pathForGoldSet = null;
				    	String pathForSortedPairs;
				    	String pathForFunction = null;
				    	
				    	
				    	if(fileNameTable.length==4){
				    		pathForGoldSet = Constants.GoldSetPathPairs+fileNameTable[2]+"_"+fileNameTable[1]+"_"+"GoldSet.txt";
				    		pathForFunction = fileNameTable[0]+"_"+fileNameTable[1]+"/"+fileNameTable[2];
				    	}else if(fileNameTable.length>4){
				    		pathForGoldSet = Constants.GoldSetPathPairs+fileNameTable[2]+"_"+fileNameTable[3]+"_"+fileNameTable[1]+"_"+"GoldSet.txt";
				    		pathForFunction = fileNameTable[0]+"_"+fileNameTable[1]+"/"+fileNameTable[2]+"_"+fileNameTable[3];
				    	}else{
				    		continue;
				    	}
				    	
				    	
				    	System.out.println("PathForGoldSet:"+pathForGoldSet);
				    	
				    	pathForSortedPairs = Constants.sortedPairsDirectory+fileName;
				    	System.out.println("pathForSortedPairs"+pathForSortedPairs);
				    	
				    	
				    			
				    	precisionRecall.parseGoldSetFile(pathForGoldSet);
				    	precisionRecall.parseResultsFile(pathForSortedPairs);
				    	
				    	 System.out.println(goldSet.size());
				    	precisionRecall.PrecisionRecalleInFile(pathForFunction,out);
				    }
				}

		out.close();

	}

}
