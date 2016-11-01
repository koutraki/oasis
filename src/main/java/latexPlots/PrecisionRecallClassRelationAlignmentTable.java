package latexPlots;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import customization.Constants;
import java.util.Collections;
import java.util.Comparator;

class PrecisionRecallPerFunction {

    String webService;
    String function;
    String classPrecision;
    String classRecall;
    String classFmeasure;
    String relationPrecision;
    String relationRecall;
    String relationFmeasure;

    public PrecisionRecallPerFunction(String webService, String function,
            String classPrecision, String classRecall, String classFmeasure,
            String relationPrecision, String relationRecall,
            String relationFmeasure) {
        this.webService = webService;
        this.function = function;
        this.classPrecision = classPrecision;
        this.classRecall = classRecall;
        this.classFmeasure = classFmeasure;
        this.relationPrecision = relationPrecision;
        this.relationRecall = relationRecall;
        this.relationFmeasure = relationFmeasure;

    }

}

public class PrecisionRecallClassRelationAlignmentTable {

    public ArrayList<PrecisionRecallPerFunction> htmlTable = new ArrayList<PrecisionRecallPerFunction>();

    public HashMap<String, ArrayList<AvgPrecisionRecallEntityResolutionObj>> LinesOfEachCategoryInTable = new HashMap<String, ArrayList<AvgPrecisionRecallEntityResolutionObj>>();

    public static PrecisionRecallPathPairsTable tableObject = new PrecisionRecallPathPairsTable();

    public void ParsePrecisionRecallClassAlignmentFile(String filePath) {
        

        File input = new File(filePath);
        try {
            Document doc = Jsoup.parse(input, "UTF-8", "");

            Elements tableLines = doc.getElementsByTag("tr");
            int flag = 0;
            for (Element line : tableLines) {
                if (flag < 2) {
                    flag++;
                    continue;
                }
                String lineText = line.text();
                String[] values = lineText.split(Constants.separatorSpace);

                System.out.println("value:"+values[1]);
                String category = tableObject.WebServicesAndCategories
                        .get(values[1]);
                System.out.println(tableObject.WebServicesAndCategories.size());
                System.out.println("cat:"+category);

                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals("#")) {
                        values[i] = "0.0f";
                    }
                }

                if (LinesOfEachCategoryInTable.containsKey(category)) {
                    ArrayList<AvgPrecisionRecallEntityResolutionObj> lineObjects = LinesOfEachCategoryInTable
                            .get(category);
                    int exists = 0;
                    for (AvgPrecisionRecallEntityResolutionObj PrecRecobject : lineObjects) {
                        /* If the function exists already */
                        if (PrecRecobject.functionName.equals(values[0])) {
                            PrecRecobject.addValuesToExistingObject(
                                    Float.parseFloat(values[2]),
                                    Float.parseFloat(values[3]),
                                    Float.parseFloat(values[4]),
                                    Float.parseFloat(values[5]),
                                    Float.parseFloat(values[6]),
                                    Float.parseFloat(values[7]));
                            exists = 1;
                        }
                    }
                    if (exists == 0) {
                        // new object
                        AvgPrecisionRecallEntityResolutionObj newTableLine = new AvgPrecisionRecallEntityResolutionObj(
                                values[0], Float.parseFloat(values[2]),
                                Float.parseFloat(values[3]),
                                Float.parseFloat(values[4]),
                                Float.parseFloat(values[5]),
                                Float.parseFloat(values[6]),
                                Float.parseFloat(values[7]));
                        lineObjects.add(newTableLine);
                    }
                } else {// category does not exist
                    ArrayList<AvgPrecisionRecallEntityResolutionObj> ListOfTableLines = new ArrayList<AvgPrecisionRecallEntityResolutionObj>();

                    AvgPrecisionRecallEntityResolutionObj newTableLine = new AvgPrecisionRecallEntityResolutionObj(
                            values[0], Float.parseFloat(values[2]),
                            Float.parseFloat(values[3]),
                            Float.parseFloat(values[4]),
                            Float.parseFloat(values[5]),
                            Float.parseFloat(values[6]),
                            Float.parseFloat(values[7]));

                    ListOfTableLines.add(newTableLine);

                    LinesOfEachCategoryInTable.put(category, ListOfTableLines);
                }
            }

            Iterator it = LinesOfEachCategoryInTable.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Entry) it.next();
                String category = (String) pairs.getKey();
                ArrayList<AvgPrecisionRecallEntityResolutionObj> averageObj = (ArrayList<AvgPrecisionRecallEntityResolutionObj>) pairs
                        .getValue();
                for (AvgPrecisionRecallEntityResolutionObj precisionRecallAverage : averageObj) {
                    precisionRecallAverage.calculateAveragePrecisionRecall();
                }
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public void ParsePrecisionRecallClassAlignmentFileForCompetitor(
            String filePath) {

        File input = new File(filePath);
        try {
            Document doc = Jsoup.parse(input, "UTF-8", "");

            Elements tableLines = doc.getElementsByTag("tr");
            int flag = 0;
            for (Element line : tableLines) {
                if (flag < 2) {
                    flag++;
                    continue;
                }
                String lineText = line.text();
                String[] values = lineText.split(Constants.separatorSpace);

                String category = tableObject.WebServicesAndCategories
                        .get(values[1]);

                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals("#")) {
                        values[i] = "0.0f";
                    }
                }

                if (LinesOfEachCategoryInTable.containsKey(category)) {
                    System.out.println("category comp. \t"+category);
                    ArrayList<AvgPrecisionRecallEntityResolutionObj> lineObjects = LinesOfEachCategoryInTable
                            .get(category);
                    int exists = 0;
                    for (AvgPrecisionRecallEntityResolutionObj PrecRecobject : lineObjects) {
                        /* If the function exists already */
                        System.out.println("value[0]"+values[0]);
                        System.out.println("PrecRecobject.functionName:"+PrecRecobject.functionName);
                        if (PrecRecobject.functionName.equals(values[0])) {
                            PrecRecobject
                                    .addValuesToExistingObjectForCompetitor(
                                            Float.parseFloat(values[2]),
                                            Float.parseFloat(values[3]),
                                            Float.parseFloat(values[4]),
                                            Float.parseFloat(values[5]),
                                            Float.parseFloat(values[6]),
                                            Float.parseFloat(values[7]));
                            exists = 1;
                        }
                    }
                    if (exists == 0) {
                        System.out.println("exists == 0");
						// new object
                        // AvgPrecisionRecallEntityResolutionObj newTableLine =
                        // new AvgPrecisionRecallEntityResolutionObj(
                        // values[1], Float.parseFloat(values[2]),
                        // Float.parseFloat(values[3]),
                        // Float.parseFloat(values[4]),
                        // Float.parseFloat(values[5]),
                        // Float.parseFloat(values[6]),
                        // Float.parseFloat(values[7]));
                        // lineObjects.add(newTableLine);
                    }
                } else {// category does not exist
                    System.out.println("category does not exist");

					// ArrayList<AvgPrecisionRecallEntityResolutionObj>
                    // ListOfTableLines = new
                    // ArrayList<AvgPrecisionRecallEntityResolutionObj>();
                    //
                    // AvgPrecisionRecallEntityResolutionObj newTableLine = new
                    // AvgPrecisionRecallEntityResolutionObj(
                    // values[1], Float.parseFloat(values[2]),
                    // Float.parseFloat(values[3]),
                    // Float.parseFloat(values[4]),
                    // Float.parseFloat(values[5]),
                    // Float.parseFloat(values[6]),
                    // Float.parseFloat(values[7]));
                    //
                    // ListOfTableLines.add(newTableLine);
                    //
                    // LinesOfEachCategoryInTable.put(category,
                    // ListOfTableLines);
                }
            }

            Iterator it = LinesOfEachCategoryInTable.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Entry) it.next();
                String category = (String) pairs.getKey();
                ArrayList<AvgPrecisionRecallEntityResolutionObj> averageObj = (ArrayList<AvgPrecisionRecallEntityResolutionObj>) pairs
                        .getValue();
                for (AvgPrecisionRecallEntityResolutionObj precisionRecallAverage : averageObj) {
                    precisionRecallAverage
                            .calculateAveragePrecisionRecallCompetitor();
                }
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public void print() {
        Iterator it = LinesOfEachCategoryInTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Entry) it.next();
            String category = (String) pairs.getKey();
            System.out.println(category);
            ArrayList<AvgPrecisionRecallEntityResolutionObj> averageObj = (ArrayList<AvgPrecisionRecallEntityResolutionObj>) pairs
                    .getValue();
            for (AvgPrecisionRecallEntityResolutionObj precisionRecallAverage : averageObj) {
                System.out.println(precisionRecallAverage.functionName + " "
                        + precisionRecallAverage.averageClassPrecision + " "
                        + precisionRecallAverage.averageClassRecall + " "
                        + precisionRecallAverage.averageClassFmeasure + " "
                        + precisionRecallAverage.averageRelationPrecision + " "
                        + precisionRecallAverage.averageRelationRecall + " "
                        + precisionRecallAverage.averageRelationFmeasure + " "
                        + precisionRecallAverage.CompetitoraverageClassPrecision + " "
                        + precisionRecallAverage.CompetitoraverageClassRecall + " "
                        + precisionRecallAverage.CompetitoraverageClassFmeasure + " "
                        + precisionRecallAverage.CompetitoraverageRelationPrecision + " "
                        + precisionRecallAverage.CompetitoraverageRelationRecall + " "
                        + precisionRecallAverage.CompetitoraverageRelationFmeasure);
            }
        }

    }

    public String PlotLatexTable() {
        String LatexTable = "\\resizebox{1\\textwidth}{!}{ \n"
                + "\\begin{tabular}{|@{$\\;$}c@{$\\;$}|@{$\\;$}c@{$\\;$}|@{$\\;$}c@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|@{$\\;$}l@{$\\;$}|} \n"
                + "\\hline\n"
                + "\\multirow{2}{*}{API} & \\multirow{2}{*}{No} & \\multirow{2}{*}{Service} &  \\multicolumn{4}{@{$\\;$}c@{$\\;$}|@{$\\;$}}{{Classes}}  &  \\multicolumn{4}{@{$\\;$}c|}{{Relations}}  \\\\ "
                + "& & & \\multicolumn{2}{@{$\\;$}c@{$\\;$}|@{$\\;$}}{{Precision}} "
                + "& \\multicolumn{2}{@{$\\;$}c@{$\\;$}|@{$\\;$}}{{Recall}} & \\multicolumn{2}{@{$\\;$}c@{$\\;$}|@{$\\;$}}{{Precision}}  &  "
                + "\\multicolumn{2}{@{$\\;$}c@{$\\;$}|}{{Recall}} \\\\  "
                + " & & & \\bf Doris &  $+$Duplicates & \\bf Doris & $+$Duplicates & \\bf Doris & $+$Duplicates & \\bf Doris & $+$Duplicates \\\\ \n"
                + "\\hline\n";

        Iterator it = LinesOfEachCategoryInTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Entry) it.next();
            String category = (String) pairs.getKey();
           
           
            
            
            ArrayList<AvgPrecisionRecallEntityResolutionObj> averageObj = (ArrayList<AvgPrecisionRecallEntityResolutionObj>) pairs
                    .getValue();

            Collections.sort(averageObj, new Comparator<AvgPrecisionRecallEntityResolutionObj>() {
                @Override
                public int compare(AvgPrecisionRecallEntityResolutionObj obj1, AvgPrecisionRecallEntityResolutionObj obj2) {

                    return obj1.functionName.compareTo(obj2.functionName);
                }
            });

            LatexTable += "\\rotatebox[origin=c]{90}{" + category
                    + "}$\\left\\{%\n";

            LatexTable += "\\begin{array}{l} \\text{";

            ArrayList<String> webServices = tableObject.CategoriesAndWebServices
                    .get(category);
            System.out.println("Category:"+category);

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
				LatexTable += averageObj.get(j).numberOfFunctions + " \\\\ ";;
			}
			LatexTable += averageObj.get(averageObj.size() - 1).numberOfFunctions
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

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averageClassPrecision
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averageClassPrecision
                    + " \\\\ \\end{tabular} & \n";

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).CompetitoraverageClassPrecision
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).CompetitoraverageClassPrecision
                    + " \\\\ \\end{tabular} & \n";

            
            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averageClassRecall + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averageClassRecall
                    + " \\\\ \\end{tabular} & \n";

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).CompetitoraverageClassRecall
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).CompetitoraverageClassRecall
                    + " \\\\ \\end{tabular} & \n";
            
            
            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averageRelationPrecision
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averageRelationPrecision
                    + " \\\\ \\end{tabular} & \n";

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).CompetitoraverageRelationPrecision
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).CompetitoraverageRelationPrecision
                    + " \\\\ \\end{tabular} & \n";

            

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).averageRelationRecall
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).averageRelationRecall
                    + " \\\\ \\end{tabular} & \n";

            LatexTable += "\\begin{tabular}{@{$\\;$}l@{$\\;$}} ";
            for (int j = 0; j < averageObj.size() - 1; j++) {

                LatexTable += averageObj.get(j).CompetitoraverageRelationRecall
                        + " \\\\ ";
            }
            LatexTable += averageObj.get(averageObj.size() - 1).CompetitoraverageRelationRecall
                    + " \\\\ \\end{tabular} \\\\ \n";

            LatexTable += "\\hline \n";
        }

        LatexTable += " \\end{tabular} \n}";

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

        PrecisionRecallClassRelationAlignmentTable latexTable = new PrecisionRecallClassRelationAlignmentTable();
        
        tableObject.AddValuesToCategoriesAndWebServices();
        tableObject.AddValuesToWebServicesAndCategories();
        tableObject.AddValuesToFunctionsAndCorrespondingValues();

  //      PrecisionRecallClassRelationAlignmentTable latexTable = new PrecisionRecallClassRelationAlignmentTable();
        latexTable
                .ParsePrecisionRecallClassAlignmentFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/classAlignmentNewForm.html");
                    //    + "solution_class_alignment_with_steps_2345_Threshold_0.5NoDublFromPaths.html");// "/Users/mary/Dropbox/OASIS/Mary-Data/PrecisionRecallClassAlignment.html");

        latexTable
                .ParsePrecisionRecallClassAlignmentFileForCompetitor("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/classAlignmentNewFormAfterVerificationStep0.4.html");
     //   /Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/solution_class_alignment_without_steps_2345_Threshold_0.5NoDublFromPaths.html");

        latexTable.print();
        String table = latexTable.PlotLatexTable();
        latexTable
                .createFileForLatex(
                        "/Users/mary/Dropbox/exp_table_ClassRelationAlignmentDORIS-PARIS-DBpedia_27_02.tex",
                        table);

    }

}
