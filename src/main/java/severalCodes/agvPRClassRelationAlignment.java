/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package severalCodes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mary
 */
public class agvPRClassRelationAlignment {

    public static int numberOfMethods = 0;

    public static float totalPrClass;
    public static float totalRcClass;

    public static float totalPrProperty;
    public static float totalRcProperty;

    public static void parsePRFile(String filePath) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;
            boolean newTR = false;
            int i = 0;
            while ((sCurrentLine = br.readLine()) != null) {

                if (sCurrentLine.startsWith("<tr>")) {
                    if (i < 2) {
                        i++;
                        continue;
                    }
                    numberOfMethods++;
                    sCurrentLine = br.readLine();
                    sCurrentLine = br.readLine();
                    String prClass = sCurrentLine = br.readLine().split(">")[1].split("<")[0];
                    totalPrClass += Float.parseFloat(prClass);
                    String rcClass = sCurrentLine = br.readLine().split(">")[1].split("<")[0];
                    totalRcClass += Float.parseFloat(rcClass);
                    String fmClass = sCurrentLine = br.readLine().split(">")[1].split("<")[0];
                    String prProperty = sCurrentLine = br.readLine().split(">")[1].split("<")[0];
                    totalPrProperty += Float.parseFloat(prProperty);
                    String rcProperty = sCurrentLine = br.readLine().split(">")[1].split("<")[0];
                    totalRcProperty += Float.parseFloat(rcProperty);
                    String fmProperty = sCurrentLine = br.readLine().split(">")[1].split("<")[0];
                    System.out.println(prClass + "\t" + rcClass + "\t" + fmClass + "\t" + prProperty + "\t" + rcProperty + "\t" + fmProperty);
                    newTR = true;

                }
            }
            System.out.println(numberOfMethods);
            System.out.println((Float) totalPrClass + "\t" + (Float) totalRcClass + "\t" + (Float) totalPrProperty + "\t" + (Float) totalRcProperty);
            System.out.println((Float) totalPrClass / numberOfMethods + "\t" + (Float) totalRcClass / numberOfMethods + "\t" + (Float) totalPrProperty / numberOfMethods + "\t" + (Float) totalRcProperty / numberOfMethods);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(agvPRClassRelationAlignment.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) throws IOException {

        parsePRFile("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/ForLatexTable/ClassRelationAlignment/"
                + "PrecisionRecallClassAlignmentWithout_123_Threshold0,5.html");

    }
}
