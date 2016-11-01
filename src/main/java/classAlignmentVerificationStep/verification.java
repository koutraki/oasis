/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classAlignmentVerificationStep;

import customization.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mary
 */
public class verification {

    public HashSet<String> propertiesFromParis = new HashSet<String>();
    public static double Threshold = 0.4;

    String buffer = new String();

    public void parseParisResultFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                if (!sCurrentLine.isEmpty()) {
                    String[] line = sCurrentLine
                            .split(Constants.separatorSpace);
                    if (Double.parseDouble(line[2]) < Threshold) {
                        continue;
                    }
                    String property = line[0].toLowerCase();
                    if (property.endsWith("-")) {
                        property = property.substring(0, property.length() - 1);
                        propertiesFromParis.add("<" + property + ">-");
                    } else {
                        propertiesFromParis.add("<" + property + ">");
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(verification.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(verification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void parseAlignmentResultFile(String filePath) {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String sCurrentLine;
            boolean remove = false;
            boolean notExist = false;
            boolean relationToInputEntity = false;
            while ((sCurrentLine = br.readLine()) != null) {
                if (!sCurrentLine.isEmpty()) {
                    String[] line = sCurrentLine
                            .split(Constants.separatorSpace);
                    notExist = false;
                    if (sCurrentLine.startsWith("C:")) {
                        if (line.length == 4) {
                            buffer += sCurrentLine + "\n";
                            remove = false;
                            if (line[3].equals("x")) {
                                relationToInputEntity = true;
                            }
                        } else {
                            String[] properties = line[2].split("/<"); //SOS split only for DBpedia
                            for (int i = 0; i < properties.length; i++) {
                                String property;
                                if (i == 0) {
                                    property = properties[i];
                                } else {
                                    property = "<" + properties[i];
                                }
                                property = property.toLowerCase();
                                System.out.println("p:" + property);
                                if (!propertiesFromParis.contains(property)) {
                                    notExist = true;
                                    remove = true;
                                    break;
                                }
                            }
                            if (notExist == false) {
                                buffer += sCurrentLine + "\n";
                                remove = false;
                            } else {
                                if (relationToInputEntity == false) {
                                    buffer += sCurrentLine + "\n";
                                    remove = false;
                                }
                            }
                        }

                    } else if (remove == false) {
                        buffer += sCurrentLine + "\n";
                    }
                } else {
                    buffer += "\n";
                }
            }
            System.out.println(buffer);
        } catch (IOException ex) {
            Logger.getLogger(verification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeNewAlignmentFile(String filePath) {
        FileWriter fstream;
        BufferedWriter out = null;
        try {

            File file = new File(filePath);
            if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);
                out.write(buffer);
            } else {
                out = new BufferedWriter(new FileWriter(file));
                out.write(buffer);
            }

            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        File folder = new File("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/ClassAlignmentNewFormV2/");//(Constants.GoldSetsClassAlignment);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {

                String fileName = file.getName();
                // System.out.println(goldSetFilename);
                if (fileName.equals(".DS_Store")) {
                    continue;
                }
                String folderPart = fileName.split("_Solution.txt")[0].split("100_")[1];
                folderPart = folderPart + "_triples.nt";
                System.out.println("folderPart:" + folderPart);
                String fileNameForParisAlignment = "/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/PARISResult/yago/" + folderPart + "/9_superrelations1.tsv";

                verification v = new verification();
                v.parseParisResultFile(fileNameForParisAlignment);
                v.parseAlignmentResultFile("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/ClassAlignmentNewFormV2/" + fileName);
                v.writeNewAlignmentFile("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/ClassAlignmentNewFormAfterVerificationStep0.4/" + fileName);

                for (String arg : v.propertiesFromParis) {
                    System.out.println(arg);
                }
            }
        }

//        verification v = new verification();
//        v.parseParisResultFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/PARISResult/dbpedia/getReleasesByArtistId_discogs_triples.nt/9_superrelations1.tsv");
//        v.parseAlignmentResultFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/ClassAlignmentNewForm/100_getReleasesByArtistId_discogs_Solution.txt");
//        for (String arg : v.propertiesFromParis) {
//            System.out.println(arg);
//        }
    }

}
