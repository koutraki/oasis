/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transformWSResults;

import customization.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import transformation.Triple;

/**
 *
 * @author mary
 */
public class WSResultsToTriples {

    String API;
    String WS;
    HashSet<String> outputValues = new HashSet<String>();

    ArrayList<Triple> viewBody = new ArrayList<Triple>();

    int counter = 0; //counter should reinitialize for each view! Should be unique for each instance of each WS.

    String totalNtTriplesForViewinString = new String();

    /**
     * ************* Parse View File ***************
     */
    void parseViewFile(String filePath) {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) { //All the view should be in one line! NO new line!!
                String[] line = sCurrentLine
                        .split("<--");
                String head = line[0]; //API + WS + OutputParams
                String body = line[1]; //

                parseHeadOfView(head);
                parseBodyOfView(body);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WSResultsToTriples.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WSResultsToTriples.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void parseHeadOfView(String head) {
        API = head.trim().split(":")[0];//API
        String wsAndValues = head.trim().split(":")[1];
        WS = wsAndValues.split("\\(")[0];//WS

        String[] outputs = wsAndValues.split("\\(")[1].split(",");

        if (outputs.length == 1) {
            outputValues.add(outputs[0].substring(0, outputs[0].length() - 1));
        } else {

            for (int i = 0; i < outputs.length; i++) {
                if (i == outputs.length - 1) {
                    outputValues.add(outputs[i].substring(0, outputs[i].length() - 1));
                    break;
                }
                outputValues.add(outputs[i]);

            }
        }

    }

    void parseBodyOfView(String body) {

        String[] triples = body.split("\\),");

        for (int i = 0; i < triples.length; i++) {
            //   System.out.println(triples[i]);

            String predicate = triples[i].split("\\(")[0].trim();

            String subject = triples[i].split("\\(")[1].trim().split(",")[0];

            String objectBeforeProcess = triples[i].split("\\(")[1].trim().split(",")[1].trim();
            String object;
            if (objectBeforeProcess.endsWith(")")) {
                object = objectBeforeProcess.trim().substring(0, objectBeforeProcess.length() - 1).trim();
            } else {
                object = objectBeforeProcess;
            }

            if (!subject.equals(object)) {
                Triple tripleObj = new Triple(subject, predicate, object);
                viewBody.add(tripleObj);
            }

        }

    }

    void postProccessingTriplesFromView() {
        /*Split complex properties*/
        splitComplexTriples();

        /* Inverse attributes for inverse properties*/
        invereseAttributesForInverseProp();

    }

    /* Inverse attributes for inverse properties*/
    public void invereseAttributesForInverseProp() {

        for (int i = 0; i < viewBody.size(); i++) {
            /* For inverse properties*/
            if (viewBody.get(i).predicate.endsWith("-")) {

                String tmp = viewBody.get(i).subject;
                viewBody.get(i).setSubject(viewBody.get(i).object);
                viewBody.get(i).setObject(tmp);
                viewBody.get(i).setPredicate(viewBody.get(i).predicate.substring(0, viewBody.get(i).predicate.length() - 1));
            }

        }
    }

    /*Split complex properties*/
    public void splitComplexTriples() {
        ArrayList<Triple> viewBodyTmp = new ArrayList<Triple>();

        for (int i = 0; i < viewBody.size(); i++) {
            if (Constants.targetKB.equals("YAGO")) {
                if (viewBody.get(i).predicate.contains("/")) {
                    /* This is the seperator for YAGO SOS!!! 
                     For DBpedia should be different*/
                    String[] complexpredicate = viewBody.get(i).predicate.split("/");
                    Triple t1 = new Triple(viewBody.get(i).subject, complexpredicate[0], "z" + i);
                    Triple t2 = new Triple("z" + i, complexpredicate[1], viewBody.get(i).object);
                    viewBodyTmp.add(t1);
                    viewBodyTmp.add(t2);
                } else {
                    viewBodyTmp.add(viewBody.get(i));
                }
            } else if (Constants.targetKB.equals("DBPedia")) {
                /* These are the seperators for DBPedia SOS!!! */
                boolean splited = false;
                String[] complexpredicate = null;
                if (viewBody.get(i).predicate.contains("/<")) {
                    complexpredicate = viewBody.get(i).predicate.split("/<");
                    splited = true;
                } else if (viewBody.get(i).predicate.contains(",<")) {
                    complexpredicate = viewBody.get(i).predicate.split(",<");
                    splited = true;
                }
                if (splited == true) {
                    Triple t1 = new Triple(viewBody.get(i).subject, complexpredicate[0], "z" + i);
                    Triple t2 = new Triple("z" + i, "<" + complexpredicate[1], viewBody.get(i).object);
                    viewBodyTmp.add(t1);
                    viewBodyTmp.add(t2);
                } else {
                    viewBodyTmp.add(viewBody.get(i));
                }
            }
            
            /*If I have other KBs with other seperators between*/

        }

        this.viewBody.clear();

        this.viewBody = viewBodyTmp;
    }

    /**
     * ************* END Parse View File ***************
     */
    public void parseXMLResultFile(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            String sCurrentLine;
            int n = 0;
            boolean inblock = false;
            HashMap<String, ArrayList<String>> outputsAndValues = null;
            while ((sCurrentLine = br.readLine()) != null) {
                if (n == 0) {
                    n = 1;
                    continue;
                }
                if (sCurrentLine.startsWith("{")) {
                    outputsAndValues = new HashMap<String, ArrayList<String>>();
                    inblock = true;
                } else if (sCurrentLine.startsWith("}")) {
                    inblock = false;

                    /*Here I have to process the view. 
                     I have to create the triples for the block!*/
                    if (!outputsAndValues.isEmpty()) {
                        createTriples(outputsAndValues);
                    }
                } else { /*line with output results*/

                    String[] variableAndValues = sCurrentLine.split("=");
                    String variable = variableAndValues[0].trim();
                    if (!outputValues.contains(variable)) {
                        System.err.println("Variable " + variable + "  do not exist in the output values of the view!");
                        continue;
                    }
                    if (variableAndValues.length > 1) {
                        //      System.out.println("before:"+variableAndValues[1]);
                        String[] values = variableAndValues[1].split("\\|");
                        ArrayList<String> valuesList = new ArrayList<String>();
                        System.out.println("variable:" + variable);
                        //     System.out.println("length:"+values.length);
                        for (int i = 0; i < values.length; i++) {
                            valuesList.add(values[i]);
                            System.out.println("value:" + values[i]);
                        }
                        outputsAndValues.put(variable, valuesList);

                    }
                }

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WSResultsToTriples.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WSResultsToTriples.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void createTriples(HashMap<String, ArrayList<String>> outputsAndValues) {
        HashMap<String, String> variableToURI = new HashMap<String, String>();
        for (int i = 0; i < viewBody.size(); i++) {
            // for predicate
            String predicate;
            String subjectUri = new String();
            String object;

            String TripleLine;

            if (viewBody.get(i).predicate.contains("Y:")) {
                predicate = viewBody.get(i).predicate.replace("Y:", API + ":");
            } else if(viewBody.get(i).predicate.contains("DBP:ontology")){
                predicate = viewBody.get(i).predicate.replace("DBP:ontology", API+"/"+WS);
            } else {
                predicate = viewBody.get(i).predicate;
            }
            //for subject
            if (variableToURI.containsKey(viewBody.get(i).subject)) {
                subjectUri = variableToURI.get(viewBody.get(i).subject);
            } else {
                subjectUri = "<http://" + WS + "/" + API + "/" + ++counter + ">";
                variableToURI.put(viewBody.get(i).subject, subjectUri);
            }

            //for object
            if (variableToURI.containsKey(viewBody.get(i).object)) {
                object = variableToURI.get(viewBody.get(i).object);
                TripleLine = subjectUri + " " + predicate + " " + object + " .";
                this.totalNtTriplesForViewinString += TripleLine + "\n";
            } else if (outputValues.contains(viewBody.get(i).object)) {
                if (outputsAndValues.containsKey(viewBody.get(i).object)) {
                    ArrayList<String> values = outputsAndValues.get(viewBody.get(i).object);
                    for (int j = 0; j < values.size(); j++) {
                        object = values.get(j);
                        TripleLine = subjectUri + " " + predicate + " " + "\"" + object + "\" .";
                        this.totalNtTriplesForViewinString += TripleLine + "\n";
                    }
                }

            } else {
                object = "<http://" + WS + "/" + API + "/" + ++counter + ">";
                variableToURI.put(viewBody.get(i).object, object);
                TripleLine = subjectUri + " " + predicate + " " + object + " .";
                this.totalNtTriplesForViewinString += TripleLine + "\n";
            }
        }
    }

    public void writeTotalNtTriplesForViewStringInFile(String filePath) {
        FileWriter fstream;
        BufferedWriter out = null;
        try {

            File file = new File(filePath);
            if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);
            } else {
                out = new BufferedWriter(new FileWriter(file));
            }

            out.write(this.totalNtTriplesForViewinString);

            out.close();

        } catch (IOException ex) {
            Logger.getLogger(WSResultsToTriples.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        /*This is just for testing....*/
        //    File folder = new File("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/TestView/");

        File folder = new File(Constants.viewsDirectory);

        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {

                String fileName = file.getName();
                if (fileName.equals(".DS_Store")) {
                    continue;
                }
                System.out.println(fileName);
                WSResultsToTriples wsToTriples = new WSResultsToTriples();
                wsToTriples.parseViewFile(file.getAbsolutePath());

                wsToTriples.postProccessingTriplesFromView();

                /**
                 * ***** TESTING *********
                 */
                System.out.println("API:" + wsToTriples.API);
                System.out.println("WS:" + wsToTriples.WS);
                for (String outputs : wsToTriples.outputValues) {
                    System.out.print("output:" + outputs + "\t");
                }
                System.out.println("");

                for (int i = 0; i < wsToTriples.viewBody.size(); i++) {
                    System.out.println("!" + wsToTriples.viewBody.get(i).subject + "!" + "\t" + "!" + wsToTriples.viewBody.get(i).predicate + "!" + "\t" + "!" + wsToTriples.viewBody.get(i).object + "!");

                }
                System.out.println("");
                System.out.println("");

                /**
                 * **** TESTING **************
                 */
                /**
                 * XSLT Results *
                 */
                /*This is just for testing....*/
                //   File folderXML = new File("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/TestXSLTResults/");
                File folderXML = new File(Constants.XSLTResults);

                File[] listOfXMLFiles = folderXML.listFiles();

                for (File file2 : listOfXMLFiles) {
                    if (file2.isFile()) {
                        String fileName2 = file2.getName();

                        //     System.out.println("xml results file:"+fileName2);
                        if (fileName2.equals(".DS_Store")) {
                            continue;
                        }
                        /*The xml files that are related to this view*/
                        if (fileName2.startsWith(wsToTriples.WS + "_" + wsToTriples.API)) {
                            System.out.println("xml results file:" + fileName2);
                            //parse this file 
                            wsToTriples.parseXMLResultFile(file2.getAbsolutePath());
                        }

                    }
                }
                System.out.println(wsToTriples.totalNtTriplesForViewinString);

                /**
                 * *
                 * Create .nt file for view *
                 */
                wsToTriples.writeTotalNtTriplesForViewStringInFile(Constants.triples + wsToTriples.WS + "_" + wsToTriples.API + "_triples.nt");
            }
        }

        //parse view files
        //for each view file run xslt file for the WS result files
    }

}
