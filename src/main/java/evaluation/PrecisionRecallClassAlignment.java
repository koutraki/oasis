package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import classAlignment.ComputePrecisionRecall;
import customization.Constants;

public class PrecisionRecallClassAlignment {

    DecimalFormat df = new DecimalFormat("#.##");

    static String webSite;
    static String function;

    ArrayList<GoldSetObjectPathPairs> classesGoldSet;
    ArrayList<GoldSetObjectPathPairs> relationsGoldSet;

    ArrayList<GoldSetObjectPathPairs> candidateClasses;
    ArrayList<GoldSetObjectPathPairs> candidateRelations;

    static float classesPrecision;
    static float classesRecall;
    static float classesFmeasure;

    static float relationsPrecision;
    static float relationsRecall;
    static float relationsFmeasure;

    static String htmlTable;

    static double relationsThreshold = 0.5;

    public PrecisionRecallClassAlignment() {
        classesGoldSet = new ArrayList<GoldSetObjectPathPairs>();
        relationsGoldSet = new ArrayList<GoldSetObjectPathPairs>();

        candidateClasses = new ArrayList<GoldSetObjectPathPairs>();
        candidateRelations = new ArrayList<GoldSetObjectPathPairs>();
    }

    void parseGoldSetFile(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            String sCurrentLine;
            String tmpClassPart1 = null;
            String tmpClassPart2 = null;

            while ((sCurrentLine = br.readLine()) != null) {

                if (!sCurrentLine.isEmpty()) {
                    String[] lineparts = sCurrentLine
                            .split(Constants.separatorSpace);
                    if (lineparts[0]
                            .equals(ComputePrecisionRecall.prefixLineFunction)) {
                        this.function = lineparts[1];
                        this.webSite = lineparts[2];
                    } else if (lineparts[0]
                            .equals(ComputePrecisionRecall.prefixLineClass)) {
                        if (lineparts.length == 4) {
                            GoldSetObjectPathPairs pair = new GoldSetObjectPathPairs(
                                    lineparts[2], "null");
                            tmpClassPart1 = lineparts[2];
                            tmpClassPart2 = null;
                            this.classesGoldSet.add(pair);
                        } else {
                            System.out.println("line:"+sCurrentLine);
                            GoldSetObjectPathPairs pair = new GoldSetObjectPathPairs(
                                    lineparts[2], lineparts[5]);
                            tmpClassPart1 = lineparts[2];
                            tmpClassPart2 = lineparts[5];
                            this.classesGoldSet.add(pair);
                        }

//                        GoldSetObjectPathPairs pair = new GoldSetObjectPathPairs(
//                                lineparts[1], lineparts[2]);
//                        tmpClassPart1 = lineparts[1];
//                        tmpClassPart2 = lineparts[2];
//                        this.classesGoldSet.add(pair);
                    } else if (lineparts[0]
                            .equals(ComputePrecisionRecall.prefixLineRelations)) {
                        if (tmpClassPart2 == null) {
                            GoldSetObjectPathPairs pair = new GoldSetObjectPathPairs(
                                    tmpClassPart1 + "+" + lineparts[2], //changed from [1]
                                    tmpClassPart1 + "+" + lineparts[5]); // changed from [2]
                            this.relationsGoldSet.add(pair);
                        } else {
                            GoldSetObjectPathPairs pair = new GoldSetObjectPathPairs(
                                    tmpClassPart1 + "+" + lineparts[2], //changed from [1]
                                    tmpClassPart2 + "+" + lineparts[5]); // changed from [2]
                            this.relationsGoldSet.add(pair);
                        }

                    }
                } else {
                    // do nothing
                }
                boolean goldSetNotEmpty = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void parseClassAlignmentFile(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            String sCurrentLine;

            String tmpClassPart1 = null;
            String tmpClassPart2 = null;
            boolean isTheFirstTime = true;
            GoldSetObjectPathPairs Classpair = null;

            while ((sCurrentLine = br.readLine()) != null) {

                if (!sCurrentLine.isEmpty()) {
                    String[] lineparts = sCurrentLine
                            .split(Constants.separatorSpace);
                    if (lineparts[0]
                            .equals(ComputePrecisionRecall.prefixLineFunction)) {
                        continue;
                    } else if (lineparts[0]
                            .equals(ComputePrecisionRecall.prefixLineClass)) {
                        
                        if (lineparts.length == 4) {
                            Classpair = new GoldSetObjectPathPairs(
                                    lineparts[2], "null");
                            tmpClassPart1 = lineparts[2];
                            tmpClassPart2 = null;
                             this.candidateClasses.add(Classpair);
                        } else {
                            Classpair = new GoldSetObjectPathPairs(
                                    lineparts[2], lineparts[5]);
                            tmpClassPart1 = lineparts[2];
                            tmpClassPart2 = lineparts[5];
                             this.candidateClasses.add(Classpair);
                        }

//                        Classpair = new GoldSetObjectPathPairs(
//                                lineparts[1], lineparts[2]);
//                        tmpClassPart1 = lineparts[1];
//                        tmpClassPart2 = lineparts[2];
                        //        isTheFirstTime = true;
                       
                    } else if (lineparts[0]
                            .equals(ComputePrecisionRecall.prefixLineRelations)) {
//						GoldSetObjectPathPairs pair = null;
//						if (lineparts.length == 4) {
                        //  if (Double.parseDouble(lineparts[3]) >= relationsThreshold) {
                        
                        if (tmpClassPart2 == null) {
                            GoldSetObjectPathPairs pair = new GoldSetObjectPathPairs(tmpClassPart1
                                + "+" + lineparts[2], tmpClassPart1 + "+"
                                + lineparts[5]);
                        this.candidateRelations.add(pair);
                        }else{
                        GoldSetObjectPathPairs pair = new GoldSetObjectPathPairs(tmpClassPart1
                                + "+" + lineparts[2], tmpClassPart2 + "+"
                                + lineparts[5]);
                        this.candidateRelations.add(pair);
                        }
                        

//                            if(isTheFirstTime == true){
//                                 this.candidateClasses.add(Classpair);
//                                 isTheFirstTime = false;
//                            }
//                                
//                        }
//                        GoldSetObjectPathPairs pair = new GoldSetObjectPathPairs(tmpClassPart1
//                                + "+" + lineparts[1], tmpClassPart2 + "+"
//                                + lineparts[2]);
//                        this.candidateRelations.add(pair);
//							System.out.println("lenth 4");
//						} else if (lineparts.length == 3) {
//							pair = new GoldSetObjectPathPairs(tmpClassPart1
//									+ "+" + "..", tmpClassPart2 + "+"
//									+ lineparts[1]);
//							this.candidateRelations.add(pair);
//							System.out.println("lenth 3");
//						} else if (lineparts.length < 3) {
//							// this.candidateRelations.add(pair);
//							System.out.println("lenth :" + lineparts.length);
//						}
                    }

                } else {
                    // do nothing
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void calculatePrecisionRecallForClasses() {
        int numberofPairsFoundInGoldSet = 0;
        int totalNumberOfPairs = this.candidateClasses.size();
        int totalnumberofPairsInGoldset = this.classesGoldSet.size();

        for (GoldSetObjectPathPairs goldsetPair : this.classesGoldSet) {
            for (GoldSetObjectPathPairs candidatePair : this.candidateClasses) {
                if (goldsetPair.getPart1().equals(candidatePair.getPart1())
                        && goldsetPair.getPart2().equals(
                                candidatePair.getPart2())) {
            //        System.err.println(candidatePair.getPart1() + "\t" + candidatePair.getPart2());
                    numberofPairsFoundInGoldSet++;
                }
            }
        }

        System.err.println(numberofPairsFoundInGoldSet + "\t" + totalNumberOfPairs + "\t" + totalnumberofPairsInGoldset);

        if (totalnumberofPairsInGoldset == 0) {
            this.classesPrecision = -2.0f;
            this.classesRecall = -2.0f;
            this.classesFmeasure = -2.0f;
        } else if (totalNumberOfPairs == 0) {
            System.out.println(this.webSite + "/" + this.function
                    + " totalNumberOfPairs==0");
            this.classesPrecision = -1.0f;
            this.classesRecall = -1.0f;
            this.classesFmeasure = -1.0f;
        } else {
            this.classesPrecision = (float) numberofPairsFoundInGoldSet
                    / totalNumberOfPairs;
            this.classesRecall = (float) numberofPairsFoundInGoldSet
                    / totalnumberofPairsInGoldset;
            this.classesFmeasure = (float) FmeasureClasses();
        }
    }

    @SuppressWarnings("static-access")
    void calculatePrecisionRecallForRelations() {
        int numberofPairsFoundInGoldSet = 0;
        int totalNumberOfPairs = this.candidateRelations.size();
        int totalnumberofPairsInGoldset = this.relationsGoldSet.size();

        for (GoldSetObjectPathPairs goldsetPair : this.relationsGoldSet) {
            for (GoldSetObjectPathPairs candidatePair : this.candidateRelations) {
                if (goldsetPair.getPart1().equals(candidatePair.getPart1())
                        && goldsetPair.getPart2().equals(
                                candidatePair.getPart2())) {
                    numberofPairsFoundInGoldSet++;
                }
            }
        }

        if (totalnumberofPairsInGoldset == 0) {
            this.relationsPrecision = -2.0f;
            this.relationsRecall = -2.0f;
            this.relationsFmeasure = -2.0f;
        } else if (totalNumberOfPairs == 0) {
            System.out.println(this.webSite + "/" + this.function
                    + " totalNumberOfPairs==0");
            this.relationsPrecision = -1.0f;
            this.relationsRecall = -1.0f;
            this.relationsFmeasure = -1.0f;
        } else {
            this.relationsPrecision = (float) numberofPairsFoundInGoldSet
                    / totalNumberOfPairs;
            this.relationsRecall = (float) numberofPairsFoundInGoldSet
                    / totalnumberofPairsInGoldset;
            this.relationsFmeasure = (float) FmeasureRelations();
        }
    }

    public float FmeasureClasses() {
        if ((this.classesPrecision + this.classesRecall) != 0) {
            return 2 * ((this.classesPrecision * this.classesRecall) / (this.classesPrecision + this.classesRecall));
        } else {
            return (float) 0.0;
        }
    }

    public float FmeasureRelations() {
        if ((this.relationsPrecision + this.relationsRecall) != 0) {
            return 2 * ((this.relationsPrecision * this.relationsRecall) / (this.relationsPrecision + this.relationsRecall));
        } else {
            return (float) 0.0;
        }
    }

    static void initializeHTMLTablePrecisionRecall() {

        htmlTable = "<!DOCTYPE html>\n" + "<html>\n" + "<body>\n\n"
                + "<table border=\"1\" style=\"width:300px\">\n" + "<tr>\n"
                + "<th>Web Site</th>\n" + "<th>Function</th>\n"
                + "<th colspan=\"3\">Classes</th>\n"
                + "<th colspan=\"3\">Relations</th>\n" + "</tr>\n" + "<tr>\n"
                + "<th></th>\n" + "<th></th>\n" + "<th>Precision</th>\n"
                + "<th>Recall</th>\n" + "<th>F-Measure</th>\n"
                + "<th>Precision</th>\n" + "<th>Recall</th>\n"
                + "<th>F-Measure</th>\n" + "</tr>\n";
    }

    void addLineToHTMLTable() {
        if (this.classesPrecision == -1.0) {

            htmlTable += "<tr>\n" + "<td>" + this.webSite + "</td>\n" + "<td>"
                    + this.function + "</td>\n" + "<td>#</td>\n"
                    + "<td>#</td>\n" + "<td>#</td>\n" + "<td>#</td>\n"
                    + "<td>#</td>\n" + "<td>#</td>\n" + "</tr>\n";
        } else if (this.classesPrecision == -2.0) {

        } else {
            htmlTable += "<tr>\n" + "<td>" + this.webSite + "</td>\n" + "<td>"
                    + this.function + "</td>\n" + "<td>"
                    + df.format(this.classesPrecision) + "</td>\n" + "<td>"
                    + df.format(this.classesRecall) + "</td>\n" + "<td>"
                    + df.format(this.classesFmeasure) + "</td>\n" + "<td>"
                    + df.format(this.relationsPrecision) + "</td>\n" + "<td>"
                    + df.format(this.relationsRecall) + "</td>\n" + "<td>"
                    + df.format(this.relationsFmeasure) + "</td>\n" + "</tr>\n";
        }

    }

    public static void htmlTableToFile() {

        FileWriter fstream;
        BufferedWriter out = null;
        try {

            File file = new File(Constants.HTMLPrecisionRecallClassAlignmentTable);   //<--- normaly is this the path
            //
            if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);
                out.write(htmlTable);
            } else {
                out = new BufferedWriter(new FileWriter(file));
                out.write(htmlTable);
            }

            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        File folder = new File(Constants.GoldSetsClassAlignmentNewForm);
        File[] listOfFiles = folder.listFiles();

        initializeHTMLTablePrecisionRecall();

        for (File file : listOfFiles) {
            if (file.isFile()) {

                if (file.getName().equals(".DS_Store")) {
                    continue;
                }

                String goldSetFilename = file.getName();
                String classAlignmentFilename = goldSetFilename
                        .split(".goldset")[0];

                PrecisionRecallClassAlignment precisionRecallObject = new PrecisionRecallClassAlignment();

                precisionRecallObject
                        .parseGoldSetFile(Constants.GoldSetsClassAlignmentNewForm
                                + goldSetFilename);
                precisionRecallObject
                        .parseClassAlignmentFile( "/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/ClassAlignmentNewFormV2/" //  Constants.classAliggnmentDir         // "/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/AlignmentsNewForm/"              //
                                + classAlignmentFilename);
                //"/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/AlignmentsNewForm/"+ 

                precisionRecallObject.calculatePrecisionRecallForClasses();
                precisionRecallObject.calculatePrecisionRecallForRelations();

                precisionRecallObject.addLineToHTMLTable();
                System.out.println(goldSetFilename + "\t" + classAlignmentFilename + " Done!!!");
                //	System.out.println(classAlignmentFilename + "  Done!!!");
            }
        }

        htmlTable += "</table>\n" + "</body>\n" + "</html>\n";
        htmlTableToFile();
    }

}
