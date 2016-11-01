/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package severalCodes;

import static KBExtraction.PropertiesExtraction.queryDBPedia;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import customization.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import knowledgebase.QueryBNFTDB;
import knowledgebase.QueryDBPedia;

/**
 *
 * @author mary
 */
public class createIDInputFilesUsingYagoFiles {

    public static void createFile(String yagoFilePath, String targetKB, String newFilePath) {

        FileWriter fstream;
        BufferedWriter out = null;
        try {

            File file = new File(newFilePath);
            if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);

            } else {
                out = new BufferedWriter(new FileWriter(file));
            }

            BufferedReader br = new BufferedReader(new FileReader(yagoFilePath));

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] line = sCurrentLine
                        .split(Constants.separatorForInputsFiles);
                String inputEntity = line[1];

                List list = new ArrayList();
                String query = "Select ?x where { ?x <http://www.w3.org/2002/07/owl#sameAs> " + inputEntity + "}";

                if (targetKB.equals("DBPedia")) {
                    list = queryDBPediaEndPoint(query);
                }

                for (int i = 0; i < list.size(); i++) {
                    String[] responsePart = list.get(i).toString().split(Constants.separatorSpace);

                    String newLine = sCurrentLine.replaceAll(inputEntity, responsePart[3]);
                    out.write(newLine + "\n");

                    System.out.println(responsePart[3]);
                }
            }

            out.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static List queryDBPediaEndPoint(String queryString) {

        com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);

        System.out.println("Query:" + query.getQueryPattern());

        QueryExecution qexec = QueryExecutionFactory.sparqlService(
                "http://dbpedia.org/sparql", query);

        ResultSet results = qexec.execSelect();
        List resultsList = new ArrayList();
        resultsList = ResultSetFormatter.toList(results);

        return resultsList;
    }

    public static void main(String[] args) {

        File folder = new File("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/inputs/idsInputTypes/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {

                String fileName = file.getName();

                // System.out.println(file.getAbsolutePath());
                if (fileName.equals(".DS_Store")) {
                    continue;
                }
                createFile(file.getAbsolutePath(), "DBPedia", "/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/inputs/100Inputs/"+fileName);
            
            }

        }
    }

}
