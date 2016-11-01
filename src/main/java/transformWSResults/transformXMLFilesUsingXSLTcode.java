/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transformWSResults;

import customization.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author mary
 */
public class transformXMLFilesUsingXSLTcode {

    public static String getInputTypeForWS(String WS, String API) {

        try (BufferedReader br = new BufferedReader(new FileReader(Constants.fileWithTheInputTypesOfTheFunctions))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {

                String[] line = sCurrentLine
                        .split(Constants.separatorSpace);
                if (line.length > 2) {
                    if (line[0].equals(WS) && line[1].equals(API)) {
                        return line[2];
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(transformXMLFilesUsingXSLTcode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(transformXMLFilesUsingXSLTcode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void getInputs(String WS, String API, String inputsFilePath,  String xslCodePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.dirWithInputs + "/100Inputs/" + inputsFilePath))) { /* Testing with 20 inputs!!!! It is manual!!!*/


            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {

                String[] line = sCurrentLine
                        .split(Constants.separatorForInputsFiles);
                String resultFileName = line[0].replace(" ", "+") + ".xml";

                String xmlFilePath = Constants.dirWithFunctions + API + "/" + WS + "/" + resultFileName;

                System.out.println(xmlFilePath);

                executeXSLT(xmlFilePath, xslCodePath, Constants.XSLTResults+WS+"_"+API+"_"+line[0]+"_transformed.txt");
                
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(transformXMLFilesUsingXSLTcode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(transformXMLFilesUsingXSLTcode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void executeXSLT(String xmlFilePath, String xslCode, String resutl) {

        Source xmlInput = new StreamSource(new File(xmlFilePath));
        Source xsl = new StreamSource(new File(xslCode));
        Result xmlOutput = new StreamResult(new File(resutl));

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xsl);
            transformer.transform(xmlInput, xmlOutput);
        } catch (TransformerException e) {
    // Handle.
        }
    }

    public static void main(String[] args) {

        File folder = new File(Constants.xsltDirectory);

        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {

                String fileName = file.getName();
                if (fileName.equals(".DS_Store")) {
                    continue;
                }
                System.out.println(fileName);

                String[] fileNameParts = fileName.split("_");

                String WS;
                String API;
                if (fileNameParts.length == 3) {
                    WS = fileNameParts[0];
                    API = fileNameParts[1];
                } else { // we supose length>3
                    WS = fileNameParts[0];
                    API = fileNameParts[1] + "_" + fileNameParts[2];
                }
                String inputType = getInputTypeForWS(WS, API);

                String inputTypesFileName = "100_" + inputType + "_entities.txt";

                getInputs(WS, API, inputTypesFileName, file.getAbsolutePath());
              //  System.out.println(inputTypesFileName);
                //   System.out.println(xmlFilePath+"\n");

            }
        }

    }

}
