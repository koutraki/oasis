package functionality;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import knowledgebase.QueryBNFTDB;
import knowledgebase.QueryDBPedia;

public class FunctionalityComputation {

    static String xmlOutput;
    static String txtOutput;

    public static QueryBNFTDB queryBNF = new QueryBNFTDB();

    public static QueryDBPedia queryDBPedia = new QueryDBPedia();

    static void parseBNFPropertiesFile(String filePath, boolean printInXML) {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;
            int numberofPairsFoundInGoldSet = 0;
            int totalNumberOfPairsGreaterThanThreshold = 0;
            while ((sCurrentLine = br.readLine()) != null) {

                String[] line = sCurrentLine.split("\\s+");
                String property = line[0];

                String numeratorQuery = "SELECT (COUNT(distinct ?x) AS ?count) { ?x " + property + " []  }";
                List list = new ArrayList();
                //  list =  queryBNF.queryExecutionBNF(numeratorQuery);
                list = queryDBPedia.queryExecutionDBPedia(numeratorQuery);
                String queryResult = processQueryResult(list.get(0).toString());
                System.out.println(queryResult);

                String dinominatorQuery = "SELECT (count(*) AS ?count){ ?x " + property + " ?y }";
                List list2 = new ArrayList();
                //    list2 =  queryBNF.queryExecutionBNF(dinominatorQuery);
                list2 = queryDBPedia.queryExecutionDBPedia(dinominatorQuery);
                String queryResult2 = processQueryResult(list2.get(0).toString());
                System.out.println(queryResult2);

                float functinality = Float.parseFloat(queryResult) / Float.parseFloat(queryResult2);
                System.out.println("functionality:" + functinality);

                /* INVERSE FUNCTIONALITY*/
                String numeratorInverseQuery = "SELECT (COUNT(distinct ?y) AS ?count) { [] " + property + " ?y  }";
                List list3 = new ArrayList();
                //  list3 = queryBNF.queryExecutionBNF(numeratorInverseQuery);
                list3 = queryDBPedia.queryExecutionDBPedia(numeratorInverseQuery);
                String queryResult3 = processQueryResult(list3.get(0).toString());
                System.out.println(queryResult3);

//                String dinominatorInverseQuery = "SELECT (count(*) AS ?count){ ?y " + property + " ?x }";
//                List list4 = new ArrayList();
//                list4 = queryBNF(dinominatorInverseQuery);
//                String queryResult4 = processQueryResult(list4.get(0).toString());
//                System.out.println(queryResult4);
                float inverseFunctinality = Float.parseFloat(queryResult3) / Float.parseFloat(queryResult2);
                System.out.println("Inverse functionality:" + inverseFunctinality);

                if (printInXML) {
                    xmlOutput += "<functionality>"
                            + "<object>" + property + "</object>"
                            + "<value>" + functinality + "</value>"
                            + "<inverse>" + inverseFunctinality + "</inverse>"
                            + "</functionality>\n";
                } else {
                    txtOutput += property + "\t" + functinality + "\t" + inverseFunctinality + "\n";
                }

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FunctionalityComputation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FunctionalityComputation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static String processQueryResult(String queryResult) {
        //result example: ( ?y = "Bowie" )
        //System.out.println("lala:"+queryResult);
        String[] result = queryResult.split(" ");

        return result[3];
    }

    public static void initializeXMLOutput() {
        xmlOutput = "<?xml version=\"1.0\" standalone=\"yes\"?><functionalities date=\"October 14, 2014\">\n";
    }

    public static void closingXMLOutput() {
        xmlOutput += "</functionalities>\n";
    }

    public static void createFile(String filePath, String stringToWright) {
        FileWriter fstream;
        BufferedWriter out = null;
        try {

            File file = new File(filePath);
            if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);
//				 out.write("-------\t\tThreshold = "
//							+ Constants.precisionRecallThreshold + "\t\t-------\n\n");
            } else {
                out = new BufferedWriter(new FileWriter(file, true));
            }

            out.write(stringToWright);
            out.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //For printing in xml
        boolean printInXML = false;
        if (printInXML) {
            initializeXMLOutput();
            parseBNFPropertiesFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/PropertiesDBpedia.txt", printInXML);
            closingXMLOutput();
        } else {
            parseBNFPropertiesFile("/Users/mary/Dropbox/UQSparqlEndpoints/Mary-Data/config/DBPediaproperties.txt", printInXML);
        }

        createFile("/Users/mary/Dropbox/UQSparqlEndpoints/Mary-Data/config/functionality/functionalitiesDBPedia3.9.xml", txtOutput);
    }

}
