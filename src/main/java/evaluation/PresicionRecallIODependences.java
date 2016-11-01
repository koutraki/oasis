package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

import classAlignment.ComputePrecisionRecall;
import customization.Constants;

public class PresicionRecallIODependences {
	DecimalFormat df = new DecimalFormat("#.##");
	static String htmlTable;

	String webService;
	float precision;
	float recall;
	float fmeasure;

	ArrayList<GoldSetObjectIODependences> goldSet = new ArrayList<GoldSetObjectIODependences>();
	ArrayList<GoldSetObjectIODependences> IODependences = new ArrayList<GoldSetObjectIODependences>();

	public void parseGoldSetFile(String filepath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;
			String tmpFunctionFrom = null;
			String tmpFunctionTo = null;

			while ((sCurrentLine = br.readLine()) != null) {

				if (!sCurrentLine.isEmpty()) {
					sCurrentLine.trim();
					if (sCurrentLine.startsWith("D:")) {
						String[] lineparts = sCurrentLine.split("  ---> ");

						tmpFunctionFrom = lineparts[0].split("D:")[1];
						tmpFunctionTo = lineparts[1];

						GoldSetObjectIODependences goldSetObjct = new GoldSetObjectIODependences(
								tmpFunctionFrom, tmpFunctionTo);
						goldSet.add(goldSetObjct);

					} else {
						for (GoldSetObjectIODependences goldSetLine : goldSet) {
							if (goldSetLine.functionFrom
									.equals(tmpFunctionFrom)
									&& goldSetLine.functionTo
											.equals(tmpFunctionTo))
								goldSetLine.paths.add(sCurrentLine.trim());
						}
					}

				} else {
					// do nothing
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseIODependencesFile(String filepath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;
			String tmpFunctionFrom = null;
			String tmpFunctionTo = null;

			while ((sCurrentLine = br.readLine()) != null) {

				if (!sCurrentLine.isEmpty()) {
					sCurrentLine.trim();
					if (sCurrentLine.startsWith("DEBUG:"))
						break;

					if (sCurrentLine.startsWith("D:")) {
						String[] lineparts = sCurrentLine.split("  ---> ");

						tmpFunctionFrom = lineparts[0].split("D:")[1];
						tmpFunctionTo = lineparts[1];

						GoldSetObjectIODependences goldSetObjct = new GoldSetObjectIODependences(
								tmpFunctionFrom, tmpFunctionTo);
						IODependences.add(goldSetObjct);

					} else {
						for (GoldSetObjectIODependences dependence : IODependences) {
							if (dependence.functionFrom.equals(tmpFunctionFrom)
									&& dependence.functionTo
											.equals(tmpFunctionTo))
								dependence.paths.add(sCurrentLine.trim());
						}
					}

				} else {
					// do nothing
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void calculatePrecisionRecall() {
		int correctFunctions = 0;
		int retrivedFunctions = 0;
		ArrayList<String> executedFunctions = new ArrayList<String>();
		System.out.println(IODependences.size());
		System.out.println(goldSet.size());
		for (GoldSetObjectIODependences dependence : IODependences) {
			if(dependence.paths.size()>0)
				retrivedFunctions ++;

			for (GoldSetObjectIODependences goldSetObjct : goldSet) {
				if (dependence.functionFrom.equals(goldSetObjct.functionFrom)
						&& dependence.functionTo
								.equals(goldSetObjct.functionTo)) {

					int tmp = 0;
					int tmp2 = 0;
					for (String path : dependence.paths) {
						for (String goldSetpath : goldSetObjct.paths) {
							if (path.equals(goldSetpath)) {
								tmp = 1;
								break;
								// correctPaths++;
							} else {
								tmp = 0;
							}
						}
						if (tmp == 0) {
							tmp2 = 0;
							break;
						} else {
							tmp2 = 1;
						}
					}

					if (tmp2 == 1) {
						correctFunctions++;
					} else {
						break;
					}

					break;
				}

			}

		}

		for (GoldSetObjectIODependences goldSetObjct : goldSet) {

			for (GoldSetObjectIODependences dependence : IODependences) {

				if (dependence.functionFrom.equals(goldSetObjct.functionFrom)) {
					executedFunctions.add(dependence.functionFrom);
					System.out.println("-->" + dependence.functionFrom);
					break;
				}
			}
		}

	
		System.out.println("\n\nCorrect Paths: " + correctFunctions
				+ "\t RetrievedPahts: " + retrivedFunctions
				+ "\tRelevantPaths: " + executedFunctions.size());

		this.precision = (float) correctFunctions / retrivedFunctions;
		this.recall = (float) correctFunctions / executedFunctions.size();
		this.fmeasure = (float) 2
				* ((precision * recall) / (precision + recall));

		System.out.println("Precision: " + precision + "\tRecall:" + recall
				+ "\n\n");

	}

	public void calculatePrecisionRecallNumberOfPaths() {
		int correctPaths=0;
		int retrievedPaths=0;
		HashSet<String> executedFunctions = new HashSet<String>();
		
		for (GoldSetObjectIODependences dependence : IODependences) {
			
			retrievedPaths +=dependence.paths.size();
			
			for (GoldSetObjectIODependences goldSetObjct : goldSet) {
				
				if(dependence.functionFrom.equals(goldSetObjct.functionFrom)){
					executedFunctions.add(dependence.functionFrom);
				}
				
				if(dependence.functionFrom.equals(goldSetObjct.functionFrom) && dependence.functionTo.equals(goldSetObjct.functionTo)){
					
					
					for (String path : dependence.paths) {
						for (String goldSetpath : goldSetObjct.paths) {
							if(path.equals(goldSetpath))
								correctPaths++;
						}
					}
					
					break;
				}
				
			}
			
		}
		
		/** Denominator For Recall**/
		int relevantPaths=0;
		for (String functionFrom : executedFunctions) {
			for (GoldSetObjectIODependences goldsetObject : goldSet) {
				if(goldsetObject.functionFrom.equals(functionFrom)){
					relevantPaths+=goldsetObject.paths.size();
				}
			}
		}
		
		System.out.println("\n\nCorrect Paths: "+correctPaths +"\t RetrievedPahts: "+ retrievedPaths + "\tRelevantPaths: "+relevantPaths);
		
		this.precision = (float)correctPaths/retrievedPaths;
		this.recall = (float)correctPaths/relevantPaths;
		this.fmeasure=(float) 2*((precision*recall)/ (precision+recall));
		
		System.out.println("Precision: "+precision+"\tRecall:"+recall+"\n\n");

	}

	
	
	static void initializeHTMLTablePrecisionRecall() {

		htmlTable = "<!DOCTYPE html>\n" + "<html>\n" + "<body>\n\n"
				+ "<table border=\"1\" style=\"width:300px\">\n" + "<tr>\n"
				+ "<th>Web Site</th>\n" + "<th>Precision</th>\n"
				+ "<th>Recall</th>\n" + "<th>F-Measure</th>\n" + "</tr>\n";
	}

	public void addLineInHTMLTable() {
		htmlTable += "<tr>\n" + "<td>" + this.webService + "</td>\n" + "<td>"
				+ df.format(this.precision) + "</td>\n" + "<td>"
				+ df.format(this.recall) + "</td>\n" + "<td>"
				+ df.format(this.fmeasure) + "</td>\n" + "</tr>\n";
	}

	public void printGoldSetList() {
		for (GoldSetObjectIODependences goldSetObject : goldSet) {
			System.out.println(goldSetObject.functionFrom + " --> "
					+ goldSetObject.functionTo);
			for (String path : goldSetObject.paths) {
				System.out.println("--" + path + "--");
			}
		}
	}

	public void printIODependences() {
		for (GoldSetObjectIODependences dependences : IODependences) {
			System.out.println(dependences.functionFrom + " --> "
					+ dependences.functionTo);
			for (String path : dependences.paths) {
				System.out.println("--" + path + "--");
			}
		}
	}

	public static void htmlTableToFile() {

		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(Constants.HTMLPrecisionRecallDependencesTable);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
				out.write(htmlTable);
			} else {
				out = new BufferedWriter(new FileWriter(file));
				out.write(htmlTable);
			}

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		File folder = new File(Constants.GoldSetsIODependences);
		File[] listOfFiles = folder.listFiles();

		initializeHTMLTablePrecisionRecall();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				String goldSetFilename = file.getName();
				String IODependencesFile = goldSetFilename
						.split(".goldset.txt")[0];

				PresicionRecallIODependences precisionRecallObject = new PresicionRecallIODependences();

				precisionRecallObject.webService = IODependencesFile
						.split(".txt")[0];

				precisionRecallObject
						.parseGoldSetFile(Constants.GoldSetsIODependences
								+ goldSetFilename);
				System.out.println("\n\nGoldSet: " + goldSetFilename + "\n");
				precisionRecallObject.printGoldSetList();

				precisionRecallObject
						.parseIODependencesFile(Constants.IODependencesDir
								+ IODependencesFile);

				System.out.println("\n\nDependences File: " + IODependencesFile
						+ "\n");
				precisionRecallObject.printIODependences();

				precisionRecallObject.calculatePrecisionRecall();

				precisionRecallObject.addLineInHTMLTable();

				System.out.println(IODependencesFile + "  Done!!!");
			}
		}

		htmlTable += "</table>\n" + "</body>\n" + "</html>\n";
		htmlTableToFile();
	}
}
