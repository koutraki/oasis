/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package severalCodes;

import customization.Constants;
import graphguide.Functionality;
import static graphguide.Functionality.functionalities;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mary
 */
public class removeZerosFromFunctionality {
    
    public static void parseFunctionalityFile(String filePath){
        
        FileWriter fstream;
        BufferedWriter out = null;
        try {

            File file = new File(filePath+"noZeros");
            if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);

            } else {
                out = new BufferedWriter(new FileWriter(file));

            }
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                String[] line = sCurrentLine
                        .split(Constants.separatorSpace);
                
                if(line[1].equals("0.0") && line[2].equals("0.0")){
                    continue;
                }else{
                    out.write(sCurrentLine+"\n");
                }
            }
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Functionality.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        parseFunctionalityFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/config/DBPedia_functionalitiesNormalized.txt");
    }
    
}
