package webSite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import customization.Constants;

public class pathPairsToTable {
	
	DecimalFormat df = new DecimalFormat("#.##");
	ArrayList<TableLineObject> lines = new ArrayList<TableLineObject>();
	public static String htmlTable;
	
	
	public void parsePrecisionRecallFile(String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.isEmpty()) {
					String[] line = sCurrentLine
							.split(Constants.separatorSpace);

					String[] split1 = line[0].split("/");

					String webServiceName = split1[1].replaceFirst(":", "");
					String[] split2 = split1[0].split("_");

					int numberOfSamples = Integer.parseInt(split2[0]);
					String functionName = split2[1];

					float precision = Float.parseFloat(line[2]);
					float recall = Float.parseFloat(line[4]);
					
					
					int exists = 0;
					for (TableLineObject tableLine : lines) {
						if (tableLine.function.equals(functionName)
								&& tableLine.webSite.equals(webServiceName)) {
							if (numberOfSamples == 20) {
								tableLine.precision20Inputs = precision;
								tableLine.recall20Inputs = recall;
							} else {
								tableLine.precision100Inputs = precision;
								tableLine.recall100Inputs = recall;
							}
							exists = 1;
						}
					}

					if (exists == 0) {
						if (numberOfSamples == 20) {
							TableLineObject tableLine = new TableLineObject(
									webServiceName, functionName, precision,
									recall, 0.0f, 0.0f);
							lines.add(tableLine);

						} else {
							TableLineObject tableLine = new TableLineObject(
									webServiceName, functionName, 0.0f, 0.0f,
									precision, recall);
							lines.add(tableLine);
						}

					}

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
	
	public void printHTMLTable(){
		

			htmlTable = "<!DOCTYPE html>\n" + "<html>\n" + "<body>\n\n"
					+ "<table border=\"1\" style=\"width:300px\">\n" 
					+ "<tr>\n"
					+ "<th>Web Site</th>\n" 
					+ "<th>Function</th>\n"
					+ "<th colspan=\"2\">20 Inputs</th>\n"
					+ "<th colspan=\"2\">100 Inputs</th>\n" 
					+ "</tr>\n" 
					+ "<tr>\n"
					+ "<th></th>\n" 
					+ "<th></th>\n" 
					+ "<th>Precision</th>\n"
					+ "<th>Recall</th>\n"
					+ "<th>Precision</th>\n" 
					+ "<th>Recall</th>\n"
					+  "</tr>\n";
			
			for (TableLineObject tableLine : lines) {
				
				htmlTable += 
						"<tr>\n" 
						+ "<td>" + tableLine.webSite + "</td>\n" 
						+ "<td>"+ tableLine.function + "</td>\n" 
						+ "<td>"+ df.format(tableLine.precision20Inputs)+ "</td>\n" 
						+ "<td>"+ df.format(tableLine.recall20Inputs) + "</td>\n" 
						+ "<td>"+ df.format(tableLine.precision100Inputs) + "</td>\n" 
						+ "<td>"+ df.format(tableLine.recall100Inputs) + "</td>\n" 
						+ "</tr>\n";
				
			}
			
			htmlTable += "</table>\n" + "</body>\n" + "</html>\n";
			
		htmlTableToFile();	
		
	}
	
	public static void htmlTableToFile() {

		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File("/Users/mary/Dropbox/OASIS/Mary-Data/DBpedia/precisionRecall/ARM/PrecisionRecallResults0.5Total.html");
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
		pathPairsToTable pairsForTable = new pathPairsToTable();
		pairsForTable.parsePrecisionRecallFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBpedia/precisionRecall/ARM/PrecisionRecallResults0.5Total.txt");
		pairsForTable.printHTMLTable();
	}

}
