/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionality;

import customization.Constants;
import static functionality.FunctionalityComputation.processQueryResult;
import static functionality.FunctionalityComputation.queryDBPedia;
import static functionality.FunctionalityComputation.xmlOutput;
import graphguide.GraphNode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import knowledgebase.QueryDBPedia;

/**
 *
 * @author mary
 */
public class FunctionalityCompNormalizedValues {

    private final String SPLIT_REGEX = "[^0-9a-zA-Z]+";

    public static QueryDBPedia queryDBPedia = new QueryDBPedia();

//    public static HashSet<String> uniqueObjects = new HashSet<String>();
    static String allFunctionalities;

    // public static int Dinominator;
    public FunctionalityCompNormalizedValues() {

    }

    public void parseProperties(String filePath) {

        try (BufferedReader br1 = new BufferedReader(new FileReader(
                filePath))) {
            String sCurrentLine;//propertyPath
            while ((sCurrentLine = br1.readLine()) != null) {
                String[] properties = sCurrentLine.split(Constants.separatorSpace);
                System.out.println("Property:" + properties[0] + "!");
                computeFunctionality(properties[0]);

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FunctionalityCompNormalizedValues.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FunctionalityCompNormalizedValues.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void computeFunctionality(String property) {
        String numeratorQuery = "SELECT (COUNT(distinct ?x) AS ?count) { ?x " + property + " []  }";
        String dinominatorQuery = "SELECT DISTINCT ?x ?y where { ?x " + property + " ?y }";

        String numeratorInverseQuery = "SELECT (COUNT(distinct ?y) AS ?count) { [] " + property + " ?y  }";
        String dinominatorInverseQuery = "SELECT DISTINCT ?y ?x where { ?y " + property + " ?x }";

        float numerator = computeNumerator(numeratorQuery);
        int dinominator = computeDenominator(dinominatorQuery);
        float numeratorInverse = computeNumeratorInverse(numeratorInverseQuery);
        int dinominatorInverse = computeDenominator(dinominatorInverseQuery);

        float functionality;
        float inverseFunctionality;
        
        if (numerator > 20 && dinominator > 0) {
            functionality = numerator / (float)dinominator;
            System.out.println("functionality:"+functionality);
        } else {
            functionality = 0.0f;
        }
        if (numeratorInverse > 20 && dinominatorInverse > 0) {
            inverseFunctionality = numeratorInverse / (float) dinominatorInverse;
             System.out.println("Inversefunctionality:"+inverseFunctionality);
        } else {
            inverseFunctionality = 0.0f;
        }

        allFunctionalities += property + "\t" + functionality + "\t" + inverseFunctionality + "\n";
    }

    public int computeDenominator(String dinominatorQuery) {

        HashSet<String> uniqueObjects = new HashSet<String>();

        List list2 = new ArrayList();
        list2 = queryDBPedia.queryExecutionDBPedia(dinominatorQuery);
        for (int i = 0; i < list2.size(); i++) {
            String queryResultY = getYResult(list2.get(i).toString());
            String queryResultX = getXResult(list2.get(i).toString());
            String normalizedObject = normalization(queryResultY);
            if (normalizedObject != null && !normalizedObject.equals("")) {
                uniqueObjects.add(normalizedObject + "+" + queryResultX);
            }
        }
        int dinominator = uniqueObjects.size();
        System.out.println("D:" + dinominator);
        //  uniqueObjects.clear();

        
        return dinominator;

    }

    public float computeNumerator(String numeratorQuery) {
        List list = new ArrayList();
        list = queryDBPedia.queryExecutionDBPedia(numeratorQuery);
        System.out.println("N:" + list.get(0).toString().split(Constants.separatorSpace)[3]);
        return Float.parseFloat(list.get(0).toString().split(Constants.separatorSpace)[3]);

    }

    public float computeNumeratorInverse(String numeratorInverseQuery) {
        List list = new ArrayList();
        list = queryDBPedia.queryExecutionDBPedia(numeratorInverseQuery);
        System.out.println("NIn:" + list.get(0).toString().split(Constants.separatorSpace)[3] + "!");
        return Float.parseFloat(list.get(0).toString().split(Constants.separatorSpace)[3]);

    }

    public String normalization(String objectValue) {
        String normalizedValue = null;

        if (objectValue.startsWith("<")) {
            return objectValue;
        } else {
            if (objectValue.contains("@")) {
                String[] values = objectValue.split("@");
                normalizedValue = ComputeNormalization(values[0]);
            } else {
                normalizedValue = ComputeNormalization(objectValue);
            }
        }

        return normalizedValue;
    }

    public String ComputeNormalization(String value) {
        String lower = value.trim().toLowerCase();
        String[] words = lower.split(SPLIT_REGEX);

        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if (words[i].compareTo(words[j]) > 0) {
                    String temp = words[i];
                    words[i] = words[j];
                    words[j] = temp;
                }
            }// for
        }// for
        String normalizedString = new String();
        for (int i = 0; i < words.length; i++) {
            if (i == words.length - 1) {
                normalizedString = normalizedString + words[i];
            } else {
                if (words[i].equals("") && i == 0) {
                    continue;
                }
                normalizedString = normalizedString + words[i] + " ";
            }
        }
        return normalizedString;
    }

    public String getYResult(String queryResult) {
        //result example: ( ?y = "Bowie" )
        //System.out.println("lala:"+queryResult);
        String[] result = queryResult.split("= ");

        return result[1];
    }

    public String getXResult(String queryResult) {
        //result example: ( ?y = "Bowie" )
        //System.out.println("lala:"+queryResult);
        String[] result = queryResult.split("= ");

        return result[2].split(" \\)")[0];
    }

//    public void printUniqueObjects() {
//        for (String uniqueObject : uniqueObjects) {
//            System.out.println(uniqueObject);
//        }
//    }
    public void functionalitiesToFile(String filePath) {
        FileWriter fstream;
        BufferedWriter out = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);
            } else {
                out = new BufferedWriter(new FileWriter(file, true));
            }

            out.write(allFunctionalities);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        FunctionalityCompNormalizedValues f = new FunctionalityCompNormalizedValues();

        f.parseProperties("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/config/DBPedia_functionalities.txt");

        f.functionalitiesToFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/config/DBPedia_functionalitiesNormalized.txt");

    }

}
