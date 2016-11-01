/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transformation;

import classAlignment.ComputePrecisionRecall;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static transformation.TransformExistingResultsIntoIntermidiate.htmlTable;

/**
 *
 * @author mary
 */
class relationObj {

    public String yCounter;
    public String xmlPath;

    public relationObj(String yCounter, String xmlPath) {
        this.yCounter = yCounter;
        this.xmlPath = xmlPath;
    }

}

public class CreateXSLTCode {

    static String webSite;
    static String function;

    static String xsltCode;

    HashMap<String, ArrayList<relationObj>> classesPaths = new LinkedHashMap<String, ArrayList<relationObj>>();

    public void parseClassAlignmentNewFormFile(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            String sCurrentLine;
            boolean wasOnceInClasees = false;

            ArrayList<relationObj> rels = null;
            String tmpClass = null;
            while ((sCurrentLine = br.readLine()) != null) {

                if (!sCurrentLine.isEmpty()) {
                    String[] lineparts = sCurrentLine.split(Constants.separatorSpace);
                    if (lineparts[0]
                            .equals(ComputePrecisionRecall.prefixLineFunction)) {
                        //previousWasRelation = false;
                        this.function = lineparts[1];
                        this.webSite = lineparts[2];
                        continue;
                    } else if (lineparts[0]
                            .equals(ComputePrecisionRecall.prefixLineClass)) {

                        wasOnceInClasees = true;

                        if (lineparts.length == 4) {
                            tmpClass = lineparts[2];
                        } else {
                            tmpClass = lineparts[5];
                        }
                        rels = new ArrayList<relationObj>();
                    } else if (lineparts[0]
                            .equals(ComputePrecisionRecall.prefixLineRelations)) { //relations
                        relationObj relObj = new relationObj(lineparts[3], lineparts[5]);
                        rels.add(relObj);
                    }

                } else {
                    if (wasOnceInClasees == true) {
                        ArrayList<relationObj> existingRelations = null;
                        if (!classesPaths.containsKey(tmpClass)) {
                            classesPaths.put(tmpClass, rels);
                        } else {
                            existingRelations = classesPaths.get(tmpClass);
                            for (relationObj rel : rels) {
                                existingRelations.add(rel);
                            }
                            classesPaths.put(tmpClass, existingRelations);
                        }
                    }
                }

            }//while

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateXSLTCode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> putClassPathsInOrder() {

        ArrayList<String> order = new ArrayList<String>();

        String firstlevel = null;

        Iterator it = classesPaths.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            System.out.println("key:" + key);

            boolean added = false;
            for (int i = 0; i < order.size(); i++) {
                if (order.get(i).startsWith(key) && order.get(i) != "/") {
                    order.add(i, key);
                    added = true;
                    break;
                }
            }

            if (!added) {
                order.add(key);
            }
        }

        /**
         * PRINTING
         */
        for (String order1 : order) {
            System.out.println("order:" + order1);
        }

        return order;

    }

    public LinkedHashMap<String, ArrayList<relationObj>> removeSubpaths(ArrayList<String> order) {

        LinkedHashMap<String, ArrayList<relationObj>> orderClasses = new LinkedHashMap<String, ArrayList<relationObj>>();
        LinkedHashMap<String, ArrayList<relationObj>> orderClassesFinal = new LinkedHashMap<String, ArrayList<relationObj>>();

        for (String order1 : order) {
            if (classesPaths.containsKey(order1)) {
                orderClasses.put(order1, classesPaths.get(order1));
            }
        }

//        String oldKey = null;
//        String newKey;
//        Iterator it = orderClasses.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry entry = (Map.Entry) it.next();
//            String key = (String) entry.getKey();
//            ArrayList<relationObj> value = (ArrayList<relationObj>) entry.getValue();
//
//            System.out.println("key:" + key);
//
//            if (oldKey == null) {
//                orderClassesFinal.put(key, value);
//                oldKey = key;
//                continue;
//            }
//
//            if (key.contains(oldKey) && !oldKey.equals("/")) {
//                newKey = key.split(oldKey)[1];
//                if (newKey.startsWith("/")) {
//                    newKey = newKey.replaceFirst("/", "");
//                }
//
//                orderClassesFinal.put(newKey, value);
//                System.out.println("newkey:" + newKey);
//            } else {
//                System.out.println("NOT CONTAIN!");
//                orderClassesFinal.put(key, value);
//            }
//            oldKey = key;
//        }
        Iterator it2 = orderClasses.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry entry = (Map.Entry) it2.next();
            String key = (String) entry.getKey();
            ArrayList<relationObj> value = (ArrayList<relationObj>) entry.getValue();

            boolean inserted = false;

            for (String or : order) {
                if (key.contains(or) && !key.equals(or) && !or.equals("/")) {
                    key = key.split(or)[1];
                    orderClassesFinal.put(key, value);
                    inserted = true;
                    break;

                }
            }

            if (!inserted) {
                orderClassesFinal.put(key, value);
            }

        }

        System.out.println("\n");
        Iterator it3 = orderClassesFinal.entrySet().iterator();
        while (it3.hasNext()) {
            Map.Entry entry = (Map.Entry) it3.next();
            String key = (String) entry.getKey();
            System.out.println("final:" + key);
        }

        return orderClassesFinal;
    }

    public void createXSLT(LinkedHashMap<String, ArrayList<relationObj>> finalOrder) {

        xsltCode = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"> \n"
                + " \n"
                + "<xsl:template match=\"/\">\n";

        int i = 0;
        Iterator it = finalOrder.entrySet().iterator();
        int tmp =0;
        while (it.hasNext()) {

            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            tmp++;
            ArrayList<relationObj> value = (ArrayList<relationObj>) entry.getValue();

            if (key.startsWith("/") && !key.equals("/")) {
                key = key.replaceFirst("/", "");
            }

            String[] keyParts = key.split("/");
            if (keyParts.length > 1) {
                xsltCode += "<xsl:for-each select=\"";
                for (int j = 0; j < keyParts.length; j++) {
                 
                    xsltCode += "*[local-name()=\'" + keyParts[j] + "\']/";
                }
                xsltCode = xsltCode.substring(0, xsltCode.length() - 1);
                xsltCode += "\">\n";
            } else {
                xsltCode += "<xsl:for-each select=\"" + key + "\">  \n";
            }
            if(tmp == 1)
                xsltCode+="{\n";

            for (relationObj value1 : value) {
                if (value1.xmlPath.endsWith("/")) {
                    value1.xmlPath = value1.xmlPath.substring(0, value1.xmlPath.length() - 1);
                }

                String[] value1Parts = value1.xmlPath.split("/");

                xsltCode += " " + value1.yCounter + "=<xsl:for-each select=\"";
                for (int j = 0; j < value1Parts.length; j++) {
                    xsltCode += "*[local-name()=\'" + value1Parts[j] + "\']/";
                }
                xsltCode = xsltCode.substring(0, xsltCode.length() - 1);
                xsltCode += "\"><xsl:value-of  select=\".\"/>|</xsl:for-each>\n";
                //+ "}   \n";
            }

//            if(i!=0){
//                xsltCode +="} </xsl:for-each>  \n";
//            }
//            
//            i++;
        }
        for (int j = 0; j < finalOrder.size(); j++) {
            if(j==finalOrder.size()-1)
                xsltCode += "} </xsl:for-each>  \n";
            else
            xsltCode += "</xsl:for-each>  \n";

        }
        //   xsltCode +="} </xsl:for-each>  \n";
        xsltCode += "</xsl:template>\n"
                + "</xsl:stylesheet>";
    }

    public static void xsltToFile(String filePath) {

        FileWriter fstream;
        BufferedWriter out = null;
        try {

            File file = new File(filePath);
            if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);
                out.write(xsltCode);
            } else {
                out = new BufferedWriter(new FileWriter(file));
                out.write(xsltCode);
            }

            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File folder = new File("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/ClassAlignmentNewForm/");//(Constants.GoldSetsClassAlignment);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {

                String fileName = file.getName();
                // System.out.println(goldSetFilename);
                if (fileName.equals(".DS_Store")) {
                    continue;
                }
                
                String fileNameForXSLT;
                String[] fileParts = fileName.split("_");
                if(fileParts.length == 4){
                    fileNameForXSLT = fileParts[1] + "_" + fileParts[2] +"_xslt.xsl" ;
                }else{
                    fileNameForXSLT = fileParts[1] + "_" + fileParts[2] +"_"+ fileParts[3]+"_xslt.xsl" ;
                }

                CreateXSLTCode obj = new CreateXSLTCode();
                obj.parseClassAlignmentNewFormFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/ClassAlignmentNewForm/"+fileName);
                ArrayList<String> order = obj.putClassPathsInOrder();
                LinkedHashMap<String, ArrayList<relationObj>> finalOrder = obj.removeSubpaths(order);
                obj.createXSLT(finalOrder);

                obj.xsltToFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/XSLTCodes/"+fileNameForXSLT);
            }
        }

        /**
         * TESTING **
         */
//        CreateXSLTCode obj = new CreateXSLTCode();
//        obj.parseClassAlignmentNewFormFile("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/ClassAlignmentNewFormV2/"
//                + "100_getReleasesByArtistName_music_brainz_Solution.txt");
//        ArrayList<String> order = obj.putClassPathsInOrder();
//        LinkedHashMap<String, ArrayList<relationObj>> finalOrder = obj.removeSubpaths(order);
//        obj.createXSLT(finalOrder);
//
//        obj.xsltToFile("/Users/mary/Desktop/100_getReleasesByArtistName_music_brainz_Solution.xsl");

    }
}
