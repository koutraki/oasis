package latexPlots;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import customization.Constants;
import evaluation.GoldSetObjectPathPairs;

public class PrecisionRecallPathPairsTable {

	public HashMap<String, ArrayList<AvgPrecisionRecallPathPairsObj>> LinesOfEachCategoryInTable = new HashMap<String, ArrayList<AvgPrecisionRecallPathPairsObj>>();

	public HashMap<String, String> FunctionsAndCorrespontingInputTypes = new HashMap<String, String>();

	public HashMap<String, String> WebServicesAndCategories = new HashMap<String, String>();
	public HashMap<String, ArrayList<String>> CategoriesAndWebServices = new HashMap<String, ArrayList<String>>();

	public void AddValuesToFunctionsAndCorrespondingValues() {
		FunctionsAndCorrespontingInputTypes.put("getActorInfoById",
				"Identifier");
		FunctionsAndCorrespontingInputTypes.put("getActorInfoByName", "Actor");
	//	FunctionsAndCorrespontingInputTypes.put("getAlbumByArtistName", "Singer");
	//	FunctionsAndCorrespontingInputTypes.put("getAlbumsByArtistId",
	//			"Identifier");
		FunctionsAndCorrespontingInputTypes.put("getReleasesByArtistMBID",
				"Identifier");
		FunctionsAndCorrespontingInputTypes.put("getArtistInfoById",
				"Identifier");
		FunctionsAndCorrespontingInputTypes.put("getArtistInfoByMBID",
				"Identifier");
		FunctionsAndCorrespontingInputTypes
				.put("getArtistInfoByName", "Singer");
		FunctionsAndCorrespontingInputTypes
				.put("getAuthorInfoByName", "Author");
		FunctionsAndCorrespontingInputTypes.put("getMoviesByActorId",
				"Identifier");
		FunctionsAndCorrespontingInputTypes.put("getReleasesByArtistId",
				"Identifier");
		FunctionsAndCorrespontingInputTypes.put("getReleasesByArtistName",
				"Singer");
		FunctionsAndCorrespontingInputTypes.put("getSimilarArtistsById",
				"Identifier");
		FunctionsAndCorrespontingInputTypes.put("getTopTracksByArtistId",
				"Identifier");
		FunctionsAndCorrespontingInputTypes.put("getTracksByArtistId",
				"Identifier");
		FunctionsAndCorrespontingInputTypes.put("getTracksByArtistName",
				"Singer");
		FunctionsAndCorrespontingInputTypes.put("getBookInfoByName", "Book");
		
	//	FunctionsAndCorrespontingInputTypes.put("getAlbumByName", "Album");
		
		FunctionsAndCorrespontingInputTypes.put("getTrackByName", "Song");
		
		FunctionsAndCorrespontingInputTypes.put("getAuthorInfoById", "Identifier");
		
		FunctionsAndCorrespontingInputTypes.put("getReleaseByName", "Album");
		
		FunctionsAndCorrespontingInputTypes.put("getMasterReleaseByName", "Album");
		
		FunctionsAndCorrespontingInputTypes.put("getCountryByName", "Country");
		
		FunctionsAndCorrespontingInputTypes.put("getCityByName", "City");
		
		FunctionsAndCorrespontingInputTypes.put("getCountryInfoByTLD", "TLD Code");
	
	}

	public void AddValuesToWebServicesAndCategories() {

		WebServicesAndCategories.put("music_brainz", "Music");
		WebServicesAndCategories.put("musixmatch", "Music");
		WebServicesAndCategories.put("last_fm", "Music");
		WebServicesAndCategories.put("echonest", "Music");
		WebServicesAndCategories.put("deezer", "Music");
		WebServicesAndCategories.put("discogs", "Music");

		WebServicesAndCategories.put("themoviedb", "Movies");

		WebServicesAndCategories.put("isbndb", "Books");
		WebServicesAndCategories.put("library_thing", "Books");
		
		WebServicesAndCategories.put("geonames", "GeoData");
	}
	
	public void AddValuesToCategoriesAndWebServices(){
		
		ArrayList<String> WebServicesForMusic = new ArrayList<String>();
		WebServicesForMusic.add("music_brainz");
		WebServicesForMusic.add("musixmatch");
		WebServicesForMusic.add("last_fm");
		WebServicesForMusic.add("echonest");
		WebServicesForMusic.add("deezer");
		WebServicesForMusic.add("discogs");
		
		ArrayList<String> WebServicesForMovies = new ArrayList<String>();
		WebServicesForMovies.add("themoviedb");
		
		ArrayList<String> WebServicesForBooks = new ArrayList<String>();
		WebServicesForBooks.add("isbndb");
		WebServicesForBooks.add("library_thing");
		
		ArrayList<String> WebServicesForGeoData = new ArrayList<String>();
		WebServicesForGeoData.add("geonames");
		
		
		CategoriesAndWebServices.put("Movies", WebServicesForMovies);
		
		CategoriesAndWebServices.put("Music", WebServicesForMusic);
		
		CategoriesAndWebServices.put("Books", WebServicesForBooks);
		
		CategoriesAndWebServices.put("GeoData", WebServicesForGeoData);
                
                
		
	}

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

					String inputType = FunctionsAndCorrespontingInputTypes
							.get(functionName);
					String category = WebServicesAndCategories
							.get(webServiceName);

					if (LinesOfEachCategoryInTable.containsKey(category)) {
						ArrayList<AvgPrecisionRecallPathPairsObj> lineObjects = LinesOfEachCategoryInTable
								.get(category);
						int exists = 0;
						for (AvgPrecisionRecallPathPairsObj PrecRecobject : lineObjects) {
							/* If the function exists already */
							if (PrecRecobject.functionName.equals(functionName)) {
								if (numberOfSamples == 20) {
									PrecRecobject
											.addToPrecision20Inputs(precision);
									PrecRecobject.addToRecall20Inputs(recall);
									PrecRecobject
											.addNumberOfFunctions20Inputs();
								} else if (numberOfSamples == 100) {
									PrecRecobject
											.addToPrecision100Inputs(precision);
									PrecRecobject.addToRecall100Inputs(recall);
									PrecRecobject
											.addNumberOfFunctions100Inputs();
								}
								exists = 1;
							}
						}
						if (exists == 0) {
							// new object
							AvgPrecisionRecallPathPairsObj newTableLine = null;
							if (numberOfSamples == 20) {
								newTableLine = new AvgPrecisionRecallPathPairsObj(
										functionName, inputType, precision,
										recall, 0.0f, 0.0f, 1, 0);
							} else if (numberOfSamples == 100) {
								newTableLine = new AvgPrecisionRecallPathPairsObj(
										functionName, inputType, 0.0f,0.0f, precision,
										recall, 0, 1);
							}
							lineObjects.add(newTableLine);
						}
					} else {// category does not exist
						ArrayList<AvgPrecisionRecallPathPairsObj> ListOfTableLines = new ArrayList<AvgPrecisionRecallPathPairsObj>();
						
						AvgPrecisionRecallPathPairsObj newTableLine = null;
						if(numberOfSamples ==20){
							newTableLine = new AvgPrecisionRecallPathPairsObj(functionName, inputType, precision, recall, 0.0f, 0.0f, 1, 0);
								
						}else if(numberOfSamples ==100){
							newTableLine = new AvgPrecisionRecallPathPairsObj(functionName, inputType, 0.0f, 0.0f, precision, recall, 0, 1);
							
						}
						ListOfTableLines.add(newTableLine);

						LinesOfEachCategoryInTable.put(category, ListOfTableLines);
					}
				}
			}

			Iterator it = LinesOfEachCategoryInTable.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs =  (Entry) it.next();
				String category = (String) pairs.getKey();
				ArrayList<AvgPrecisionRecallPathPairsObj> averageObj = (ArrayList<AvgPrecisionRecallPathPairsObj>) pairs
						.getValue();
				for (AvgPrecisionRecallPathPairsObj precisionRecallAverage : averageObj) {
					precisionRecallAverage.averagePrecisionCalculator20Inputs();
					precisionRecallAverage.averageRecallCalculator20Inputs();
					precisionRecallAverage.averagePrecisionCalculator100Inputs();
					precisionRecallAverage.averageRecallCalculator100Inputs();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String plotLatexTable() {
		String LatexTable = "\\begin{tabular}{|c|l|l|l|l|l|l|} \n"+
						"\\hline\n"+
						"\\multirow{2}{*}{\\bf Web Service} & \\multirow{2}{*}{\\quad \\bf Functions} & \\quad \\bf Input  & \\multicolumn{2}{c|}{\\bf{20 Inputs}}  &  \\multicolumn{2}{c|}{\\bf{100 Inputs}}  \\\\ & &  \\quad \\bf Type & \\quad \\bf Precision & \\quad \\bf Recall & \\quad \\bf Precision & \\quad \\bf Recall  \\\\ \n"+
						"\\hline\n";

		Iterator it = LinesOfEachCategoryInTable.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Entry) it.next();
			String category = (String) pairs.getKey();
			ArrayList<AvgPrecisionRecallPathPairsObj> averageObj = (ArrayList<AvgPrecisionRecallPathPairsObj>) pairs
					.getValue();
			LatexTable += "\\rotatebox[origin=c]{90}{"+category+"}$\\left\\{%\n";
			
			LatexTable+= "\\begin{array}{l} \\text{";
		
			ArrayList<String> webServices = CategoriesAndWebServices.get(category);
			
			for (int i=0; i<webServices.size()-1; i++) {
	
				if(webServices.get(i).contains("_")){
					LatexTable+=webServices.get(i).replace("_", "\\_")+"} \\\\ \\text{";
				}else{
					LatexTable+=webServices.get(i)+"} \\\\ \\text{";
				}
				
			}
	
			if(webServices.get(webServices.size()-1).contains("_")){
				LatexTable +=webServices.get(webServices.size()-1).replace("_", "\\_")+"} \\\\\\end{array}\\right.$ &\n";
			}else{
				LatexTable +=webServices.get(webServices.size()-1)+"} \\\\\\end{array}\\right.$ &\n";
			}
			
			LatexTable+="\\begin{tabular}{l} ";
			for (int j=0; j<averageObj.size()-1; j++) {
				if(averageObj.get(j).functionName.contains("_")){
					String function=averageObj.get(j).functionName.replace("_", "\\_");
					LatexTable+=function+" \\\\ ";
				}else{
					LatexTable+=averageObj.get(j).functionName+" \\\\ ";
				}
			}
			if(averageObj.get(averageObj.size()-1).functionName.contains("_")){
				String function = averageObj.get(averageObj.size()-1).functionName.replace("_", "\\_");
				LatexTable+=function+" \\\\ \\end{tabular} & \n";
			}else{
				LatexTable+=averageObj.get(averageObj.size()-1).functionName+" \\\\ \\end{tabular} & \n";
			}
			
			LatexTable+="\\begin{tabular}{l} ";
			for (int j=0; j<averageObj.size()-1; j++) {
				
				LatexTable+=averageObj.get(j).inputType+" \\\\ ";
			}
			LatexTable+=averageObj.get(averageObj.size()-1).inputType+" \\\\ \\end{tabular} & \n";
			
			LatexTable+="\\begin{tabular}{l} ";
			for (int j=0; j<averageObj.size()-1; j++) {
				
				LatexTable+=averageObj.get(j).averagePrecision20Inputs+" \\\\ ";
			}
			LatexTable+=averageObj.get(averageObj.size()-1).averagePrecision20Inputs+" \\\\ \\end{tabular} & \n";
			
			
			LatexTable+="\\begin{tabular}{l} ";
			for (int j=0; j<averageObj.size()-1; j++) {
				
				LatexTable+=averageObj.get(j).averageRecall20Inputs+" \\\\ ";
			}
			LatexTable+=averageObj.get(averageObj.size()-1).averageRecall20Inputs+" \\\\ \\end{tabular} & \n";
			
			LatexTable+="\\begin{tabular}{l} ";
			for (int j=0; j<averageObj.size()-1; j++) {
				
				LatexTable+=averageObj.get(j).averagePrecision100Inputs+" \\\\ ";
			}
			LatexTable+=averageObj.get(averageObj.size()-1).averagePrecision100Inputs+" \\\\ \\end{tabular} & \n";
			
			LatexTable+="\\begin{tabular}{l} ";
			for (int j=0; j<averageObj.size()-1; j++) {
				
				LatexTable+=averageObj.get(j).averageRecall100Inputs+" \\\\ ";
			}
			LatexTable+=averageObj.get(averageObj.size()-1).averageRecall100Inputs+" \\\\ \\end{tabular} \\\\ \n";
			
			LatexTable+="\\hline \n";
		}
		
	
		LatexTable+=" \\end{tabular}";
		
		return LatexTable;
		
	}

	

	public void createFileForLatex(String path, String table) {
		
		FileWriter fstream;
		BufferedWriter out = null;
		try {
			
			File file = new File(path);
			 if (!file.exists()) {
				 fstream = new FileWriter(file);
				 out = new BufferedWriter(fstream);
				 out.write(table);
			 }else{
				 out = new BufferedWriter(new FileWriter(file));
				 out.write(table);
			 }
			 
			 out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		
		
		PrecisionRecallPathPairsTable tableObject = new PrecisionRecallPathPairsTable();
		
		tableObject.AddValuesToFunctionsAndCorrespondingValues();
		tableObject.AddValuesToCategoriesAndWebServices();
		tableObject.AddValuesToWebServicesAndCategories();
		
		tableObject.parsePrecisionRecallFile("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/TotalPrecisionRecall0.5NewMethodWITHCycles.txt");
		String toPrint = tableObject.plotLatexTable();
		tableObject.createFileForLatex("/Users/mary/Dropbox/OASISPaper (1)/EDBT/exp/exp_table_1_0.5NewMethodWITHCycles.tex", toPrint);
		

	}

}
