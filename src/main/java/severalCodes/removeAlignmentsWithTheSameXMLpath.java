/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package severalCodes;

import customization.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author mary
 */
public class removeAlignmentsWithTheSameXMLpath {

    public static LinkedHashMap<String, String> newAlignmentLine;

    public removeAlignmentsWithTheSameXMLpath() {
        newAlignmentLine = new LinkedHashMap<String, String>();
    }

    public void parsePathAlignmentFile(String filePath) {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {

                if (!sCurrentLine.isEmpty()) {
                    String[] lineparts = sCurrentLine.split(Constants.separatorSpace);

                    if (!newAlignmentLine.containsKey(lineparts[1])) {
                        newAlignmentLine.put(lineparts[1], sCurrentLine);
                    }

                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(removeAlignmentsWithTheSameXMLpath.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeNewAlignmentFile(String filePath) {

        FileWriter fstream;
        BufferedWriter out = null;
        try {

            File file = new File(filePath);
            //if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);

                Iterator it = newAlignmentLine.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String pathAlignmentLine = (String) entry.getValue();
                    
                     out.write(pathAlignmentLine+"\n");
                }

               
//            } else {
//                out = new BufferedWriter(new FileWriter(file));
//                out.write(xsltCode);
//            }

            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        
        File folder = new File("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/results100_LastVersion_0.1WITHCycles/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {

                String fileName = file.getName();
                // System.out.println(goldSetFilename);
                if (fileName.equals(".DS_Store")) {
                    continue;
                }
                
                

                removeAlignmentsWithTheSameXMLpath obj = new removeAlignmentsWithTheSameXMLpath();
                obj.parsePathAlignmentFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/results100_LastVersion_0.1WITHCycles/"+fileName);
                obj.writeNewAlignmentFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/results100_LastVersion_0.1WITHCyclesNoDuplicates/"+fileName);
               
            }
        }
        
        
        
    }

}
