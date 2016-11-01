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
import java.util.Collections;
import java.util.Comparator;

public class PrecisionRecallPathPairsTotalTable {

    public HashMap<String, ArrayList<AvgPrecisionRecallPathPairsTotalObj>> LinesOfEachCategoryInTable = new HashMap<String, ArrayList<AvgPrecisionRecallPathPairsTotalObj>>();

    public HashMap<String, String> FunctionsAndCorrespontingInputTypes = new HashMap<String, String>();

    public HashMap<String, String> WebServicesAndCategories = new HashMap<String, String>();
    public HashMap<String, ArrayList<String>> CategoriesAndWebServices = new HashMap<String, ArrayList<String>>();

    public void AddValuesToFunctionsAndCorrespondingValues() {
        FunctionsAndCorrespontingInputTypes.put("getActorInfoById",
                "Id");
        FunctionsAndCorrespontingInputTypes.put("getActorInfoByName", "Actor");
        FunctionsAndCorrespontingInputTypes.put("getAlbumByArtistName",
                "Singer");
        FunctionsAndCorrespontingInputTypes.put("getAlbumsByArtistId",
                "Id");
        FunctionsAndCorrespontingInputTypes.put("getReleasesByArtistMBID",
                "Id");
        FunctionsAndCorrespontingInputTypes.put("getArtistInfoById",
                "Id");
        FunctionsAndCorrespontingInputTypes.put("getArtistInfoByMBID",
                "Id");
        FunctionsAndCorrespontingInputTypes
                .put("getArtistInfoByName", "Singer");
        FunctionsAndCorrespontingInputTypes
                .put("getAuthorInfoByName", "Author");
        FunctionsAndCorrespontingInputTypes.put("getMoviesByActorId",
                "Id");
        FunctionsAndCorrespontingInputTypes.put("getReleasesByArtistId",
                "Id");
        FunctionsAndCorrespontingInputTypes.put("getReleasesByArtistName",
                "Singer");
        FunctionsAndCorrespontingInputTypes.put("getSimilarArtistsById",
                "Id");
        FunctionsAndCorrespontingInputTypes.put("getTopTracksByArtistId",
                "Id");
        FunctionsAndCorrespontingInputTypes.put("getTracksByArtistId",
                "Id");
        FunctionsAndCorrespontingInputTypes.put("getTracksByArtistName",
                "Singer");
        FunctionsAndCorrespontingInputTypes.put("getBookInfoByName", "Book");

	//	FunctionsAndCorrespontingInputTypes.put("getAlbumByName", "Album");
        FunctionsAndCorrespontingInputTypes.put("getTrackByName", "Song");

        FunctionsAndCorrespontingInputTypes.put("getAuthorInfoById",
                "Id");

        FunctionsAndCorrespontingInputTypes.put("getReleaseByName", "Album");

        FunctionsAndCorrespontingInputTypes.put("getMasterReleaseByName",
                "Album");

        FunctionsAndCorrespontingInputTypes.put("getCountryByName", "Country");

        FunctionsAndCorrespontingInputTypes.put("getCityByName", "City");

        FunctionsAndCorrespontingInputTypes.put("getCountryInfoByTLD",
                "TLD Code");

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

    public void AddValuesToCategoriesAndWebServices() {

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

                    String kind = split2[0];

					// int numberOfSamples = Integer.parseInt(split2[0]);
                    String functionName = split2[1];

                    float precision = Float.parseFloat(line[2]);
                    float recall = Float.parseFloat(line[4]);

                    String inputType = FunctionsAndCorrespontingInputTypes
                            .get(functionName);
                    String category = WebServicesAndCategories
                            .get(webServiceName);

                    if (LinesOfEachCategoryInTable.containsKey(category)) {
                        ArrayList<AvgPrecisionRecallPathPairsTotalObj> lineObjects = LinesOfEachCategoryInTable
                                .get(category);
                        int exists = 0;
                        for (AvgPrecisionRecallPathPairsTotalObj PrecRecobject : lineObjects) {
                            /* If the function exists already */
                            if (PrecRecobject.functionName.equals(functionName)) {
                                if (kind.equals("overlap")) {
                                    PrecRecobject
                                            .addToPrecisionOverlapping(precision);
                                    // .addToPrecision20Inputs(precision);
                                    PrecRecobject
                                            .addToRecallOverlapping(recall);// .addToRecall20Inputs(recall);
                                    PrecRecobject
                                            .addNumberOfFunctionsOverlapping();
                                    // .addNumberOfFunctions20Inputs();
                                } else if (kind.equals("cycle")) {
                                    PrecRecobject
                                            .addToPrecisionWithCycles(precision);
                                    // .addToPrecision100Inputs(precision);
                                    PrecRecobject.addToRecallWithCycles(recall); // .addToRecall100Inputs(recall);
                                    PrecRecobject
                                            .addNumberOfFunctionsWithCycles();
                                    // .addNumberOfFunctions100Inputs();
                                } else if (kind.equals("KBToXML")) {
                                    PrecRecobject.addToPrecisionKBToXML(precision);
										//		.addToPrecisionWithCycles(precision);
                                    // .addToPrecision100Inputs(precision);
                                    PrecRecobject.addToRecallKBToXML(recall);//addToRecallWithCycles(recall); // .addToRecall100Inputs(recall);
                                    PrecRecobject.addNumberOfFunctionsKBToXML();
											//	.addNumberOfFunctionsWithCycles();
                                    // .addNumberOfFunctions100Inputs();
                                } else if (kind.equals("XMLToKB")) {
                                    PrecRecobject.addToPrecisionXMLToKB(precision);
										//		.addToPrecisionWithCycles(precision);
                                    // .addToPrecision100Inputs(precision);
                                    PrecRecobject.addToRecallXMLToKB(recall);//addToRecallWithCycles(recall); // .addToRecall100Inputs(recall);
                                    PrecRecobject.addNumberOfFunctionsXMLToKB();
											//	.addNumberOfFunctionsWithCycles();
                                    // .addNumberOfFunctions100Inputs();
                                }
                                exists = 1;
                            }
                        }
                        if (exists == 0) {
                            // new object
                            AvgPrecisionRecallPathPairsTotalObj newTableLine = null;
                            if (kind.equals("overlap")) {
                                newTableLine = new AvgPrecisionRecallPathPairsTotalObj(
                                        functionName, inputType, precision,
                                        recall, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1, 0, 0, 0);
                            } else if (kind.equals("cycle")) {
                                newTableLine = new AvgPrecisionRecallPathPairsTotalObj(
                                        functionName, inputType, 0.0f, 0.0f,
                                        precision, recall, 0.0f, 0.0f, 0.0f, 0.0f, 0, 1, 0, 0);
                            } else if (kind.equals("KBToXML")) {
                                newTableLine = new AvgPrecisionRecallPathPairsTotalObj(
                                        functionName, inputType, 0.0f, 0.0f, 0.0f, 0.0f,
                                        precision, recall, 0.0f, 0.0f, 0, 0, 1, 0);
                            } else if (kind.equals("XMLToKB")) {
                                newTableLine = new AvgPrecisionRecallPathPairsTotalObj(
                                        functionName, inputType, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                                        precision, recall, 0, 0, 0, 1);
                            }
                            lineObjects.add(newTableLine);
                        }
                    } else {// category does not exist
                        ArrayList<AvgPrecisionRecallPathPairsTotalObj> ListOfTableLines = new ArrayList<AvgPrecisionRecallPathPairsTotalObj>();

                        AvgPrecisionRecallPathPairsTotalObj newTableLine = null;
                        if (kind.equals("overlap")) {
                            newTableLine = new AvgPrecisionRecallPathPairsTotalObj(
                                    functionName, inputType, precision, recall,
                                    0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1, 0, 0, 0);

                        } else if (kind.equals("cycle")) {
                            newTableLine = new AvgPrecisionRecallPathPairsTotalObj(
                                    functionName, inputType, 0.0f, 0.0f,
                                    precision, recall, 0.0f, 0.0f, 0.0f, 0.0f, 0, 1, 0, 0);

                        } else if (kind.equals("KBToXML")) {
                            newTableLine = new AvgPrecisionRecallPathPairsTotalObj(
                                    functionName, inputType, 0.0f, 0.0f, 0.0f, 0.0f,
                                    precision, recall, 0.0f, 0.0f, 0, 0, 1, 0);

                        } else if (kind.equals("XMLToKB")) {
                            newTableLine = new AvgPrecisionRecallPathPairsTotalObj(
                                    functionName, inputType, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                                    precision, recall, 0, 0, 0, 1);

                        }
                        ListOfTableLines.add(newTableLine);

                        LinesOfEachCategoryInTable.put(category,
                                ListOfTableLines);
                    }
                }
            }

            Iterator it = LinesOfEachCategoryInTable.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Entry) it.next();
                String category = (String) pairs.getKey();
                ArrayList<AvgPrecisionRecallPathPairsTotalObj> averageObj = (ArrayList<AvgPrecisionRecallPathPairsTotalObj>) pairs
                        .getValue();
                for (AvgPrecisionRecallPathPairsTotalObj precisionRecallAverage : averageObj) {
                    precisionRecallAverage
                            .averagePrecisionCalculatorOverlapping();
                    precisionRecallAverage.averageRecallCalculatorOverlapping();
                    precisionRecallAverage
                            .averagePrecisionCalculatorWithCycles();
                    precisionRecallAverage.averageRecallCalculatorWithCycles();
                    precisionRecallAverage.averagePrecisionCalculatorKBToXML();
                    precisionRecallAverage.averageRecallCalculatorKBToXML();
                    precisionRecallAverage.averagePrecisionCalculatorXMLToKB();
                    precisionRecallAverage.averageRecallCalculatorXMLToKB();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String plotLatexTable() {
        String LatexTable = // "\\begin{tabular}{|c|l|l|l|l|l|l|} \n"+
                "\\resizebox{1\\textwidth}{!}{ \n"
                + "\\begin{tabular}{|@{$\\;$}c@{$\\;$}|@{$\\;$}c@{$\\;$}|@{$\\;$}c@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|} \n"
                + "\\hline\n"
                + "\\multirow{2}{*}{API} & \\multirow{2}{*}{ No} & \\multirow{2}{*}{Service} &  \\multirow{2}{*}{Input}  & \\multicolumn{4}{@{$\\;$}c@{$\\;$}|@{$\\;$}}{{Precision}}  &  \\multicolumn{4}{c|}{{Recall}}  \\\\ "
                + "& & & & $+$Cycles & \\bf Overlap &   KB$\\rightarrow$WS & WS$\\rightarrow$KB & $+$Cycles & \\bf Overlap &   KB$\\rightarrow$WS & WS$\\rightarrow$KB\\\\ \n"
                + "\\hline\n";

        Iterator it = LinesOfEachCategoryInTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Entry) it.next();
            String category = (String) pairs.getKey();
            ArrayList<AvgPrecisionRecallPathPairsTotalObj> averageObj = (ArrayList<AvgPrecisionRecallPathPairsTotalObj>) pairs
                    .getValue();

            Collections.sort(averageObj, new Comparator<AvgPrecisionRecallPathPairsTotalObj>() {
                @Override
                public int compare(AvgPrecisionRecallPathPairsTotalObj obj1, AvgPrecisionRecallPathPairsTotalObj obj2) {

                    return obj1.functionName.compareTo(obj2.functionName);
                }
            });

            LatexTable += "\\rotatebox[origin=c]{90}{" + category
                    + "}$\\left\\{%\n";

            LatexTable += "\\begin{array}{l} \\text{";

            ArrayList<String> webServices = CategoriesAndWebServices
                    .get(category);

            for (int i = 0; i < webServices.size() - 1; i++) {

                if (webServices.get(i).contains("_")) {
                    LatexTable += webServices.get(i).replace("_", "\\_")
                            + "} \\\\ \\text{";
                } else {
                    LatexTable += webServices.get(i) + "} \\\\ \\text{";
                }

            }

            if (webServices.get(webServices.size() - 1).contains("_")) {
                LatexTable += webServices.get(webServices.size() - 1).replace(
                        "_", "\\_")
                        + "} \\\\\\end{array}\\right.$ &\n";
            } else {
                LatexTable += webServices.get(webServices.size() - 1)
                        + "} \\\\\\end{array}\\right.$ &\n";
            }

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {
                LatexTable += averageObj.get(j).numberOfFunctionsWithTheSameNameOverlapping + " \\\\ ";;
            }
            LatexTable += averageObj.get(averageObj.size() - 1).numberOfFunctionsWithTheSameNameOverlapping
                    + " \\\\ \\end{tabular} & \n";

            LatexTable += "\\multicolumn{1}{@{$\\;$}l@{$\\;$}|@{$\\;$}}{\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {
                if (averageObj.get(j).functionName.contains("_")) {
                    String function = averageObj.get(j).functionName.replace(
                            "_", "\\_");
                    LatexTable += function + " \\\\ ";
                } else {
                    LatexTable += averageObj.get(j).functionName + " \\\\ ";
                }
            }
            if (averageObj.get(averageObj.size() - 1).functionName
                    .contains("_")) {
                String function = averageObj.get(averageObj.size() - 1).functionName
                        .replace("_", "\\_");
                LatexTable += function + " \\\\ \\end{tabular} } & \n";
            } else {
                LatexTable += averageObj.get(averageObj.size() - 1).functionName
                        + " \\\\ \\end{tabular} } & \n";
            }

            LatexTable += "\\multicolumn{1}{@{$\\;$}l@{$\\;$}|@{$\\;$}}{\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).inputType + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).inputType
                    + " \\\\ \\end{tabular} } & \n";

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averagePrecisionWithCycles
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averagePrecisionWithCycles
                    + " \\\\ \\end{tabular} & \n";

            
            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averagePrecisionOverlapping
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averagePrecisionOverlapping
                    + " \\\\ \\end{tabular} & \n";

            
            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averagePrecisionKBToXML
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averagePrecisionKBToXML
                    + " \\\\ \\end{tabular} & \n";

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averagePrecisionXMLToKB
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averagePrecisionXMLToKB
                    + " \\\\ \\end{tabular} & \n";

            
              LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averageRecallWithCycles
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averageRecallWithCycles
                    + " \\\\ \\end{tabular} & \n";
            
            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averageRecallOverlapping
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averageRecallOverlapping
                    + " \\\\ \\end{tabular} & \n";

            
            
          

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averageRecallKBToXML
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averageRecallKBToXML
                    + " \\\\ \\end{tabular}  & \n";

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averageRecallXMLToKB
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averageRecallXMLToKB
                    + " \\\\ \\end{tabular} \\\\ \n";

            LatexTable += "\\hline \n";
        }

        LatexTable += " \\end{tabular}\n";

        LatexTable += "}";

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
            } else {
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
        PrecisionRecallPathPairsTotalTable tableObject = new PrecisionRecallPathPairsTotalTable();

        tableObject.AddValuesToFunctionsAndCorrespondingValues();
        tableObject.AddValuesToCategoriesAndWebServices();
        tableObject.AddValuesToWebServicesAndCategories();

        tableObject
                .parsePrecisionRecallFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/precisionRecall/PrecisionRecallResults0.5Total.txt");

        String toPrint = tableObject.plotLatexTable();

        tableObject
                .createFileForLatex(
                        "/Users/mary/Dropbox/OASISPaper (1)/OASIS_13Pages/exp/exp_table_Path_Alignment_DBPedia_26_11.tex",
                        toPrint);

    }

}
