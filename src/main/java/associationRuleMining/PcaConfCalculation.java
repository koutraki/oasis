package associationRuleMining;

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
import java.util.Map;
import java.util.TreeMap;

import customization.Constants;
import graphguide.*;
import dataguide.*;
import evaluation.GoldSetObjectPathPairs;
import evaluation.PrecisionRecallPathPairs;
import java.util.HashSet;

public class PcaConfCalculation {

    private int Numerator = 0;
    private int Denominator = 0;
    private int DenominatorOld = 0; /* only for testing */

    /* String: input Entity (e.g. <Madona>), Graph Guide for input entity */
    private static HashMap<String, GraphGuideV2> GraphGuides = new HashMap<String, GraphGuideV2>();
    private static HashMap<String, DataGuide> DataGuides = new HashMap<String, DataGuide>();

    public PcaConfCalculation() {

    }

    public void YagoImpliesXML(String sortedPairsFile) {

        String filePath = Constants.sortedPairsDirectory + sortedPairsFile;

        System.out.println("YAGOImpliesXML filepath:" + filePath);

        FileWriter fstream;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(Constants.pcaConfFolder
                    + "YagoImpliesXML/" + sortedPairsFile);
            out = new BufferedWriter(fstream);
            out.write("Yago Property\tXML Path\tpcaConf\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try (BufferedReader br1 = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = br1.readLine()) != null) {
                String[] lineColumns = line.split(Constants.separatorSpace);
                /* if it larger than a  threshold  */
                if (Double.parseDouble(lineColumns[2]) < Constants.pcaConfThreshold) {
                    break;
                }

                Numerator = 0;
                Denominator = 0;
                DenominatorOld = 0;

                String B = lineColumns[0]; // B relationship : YAGO
                String r = lineColumns[1]; // r relationship : XML
                System.out.println("B:" + B + "\tr:" + r + "!");

                Iterator<Map.Entry<String, DataGuide>> iteratorDataGuides = DataGuides
                        .entrySet().iterator();

                while (iteratorDataGuides.hasNext()) {
                    Map.Entry<String, DataGuide> entry = iteratorDataGuides
                            .next();
                    String inputValue = entry.getKey();
                    DataGuide currentDataGuide = entry.getValue();

                    /**
                     * ***** DEBUGING *****
                     */
//					Iterator<Map.Entry<String, Node>> iteratortest = currentDataGuide.getNodeWithValuesByPathFromTheRoot.entrySet().iterator();
//					System.out.println("SIZE malakias:"+currentDataGuide.getNodeWithValuesByPathFromTheRoot.size());
//					while (iteratortest.hasNext()) {
//						Map.Entry<String, Node> entrytest = iteratortest
//								.next();
//						String pathTest = entrytest.getKey();
//						System.out.println("Path is:"+pathTest+"---");
//						Node nodeTest = entrytest.getValue();
//					}
                    /**
                     * ***** DEBUGING *****
                     */
                    if (currentDataGuide.getNodeWithValuesByPathFromTheRoot
                            .containsKey(r)) {
                        Node node = currentDataGuide.getNodeWithValuesByPathFromTheRoot
                                .get(r);
                        ArrayList<ValueNode> YsOfr = node.getValues();
                        HashMap<String, ValueNode> YsOfrWithoutDuplicates = new HashMap<String, ValueNode>();
                        for (ValueNode valueNode : YsOfr) {
                            YsOfrWithoutDuplicates.put(
                                    valueNode.normalizedValue, valueNode);
                        }

                        GraphGuideV2 currentGraphGuide = GraphGuides
                                .get(inputValue);
                        System.out.println("InputValue:" + inputValue);

                        if (currentGraphGuide
                                .getPropertyPathsInputEntitiesAndNodes()
                                .containsKey(B)) {

                            TreeMap<String, ArrayList<GraphNode>> inputEntityAndNodes = (TreeMap) currentGraphGuide
                                    .getPropertyPathsInputEntitiesAndNodes()
                                    .get(B);

                            ArrayList<GraphNode> YsOfB = (ArrayList<GraphNode>) inputEntityAndNodes
                                    .get(inputValue);

                            HashMap<String, GraphNode> YsOfBWithoutDuplicates = new HashMap<String, GraphNode>();
                            for (GraphNode graphNode : YsOfB) {
                                YsOfBWithoutDuplicates.put(
                                        graphNode.normalizedName, graphNode);
                            }

                            // System.out.println("Ys in " + r +
                            // " with Douplicates = "+YsOfr.size()
                            // +"  Without douplicates = "+
                            // YsOfrWithoutDuplicates.size());
                            // System.out.println("Ys of " + B);
                            int found = 0;

                            Iterator<Map.Entry<String, GraphNode>> iterator2 = YsOfBWithoutDuplicates
                                    .entrySet().iterator();
                            while (iterator2.hasNext()) {
                                Map.Entry<String, GraphNode> entry2 = iterator2
                                        .next();
                                String yOfB = entry2.getKey();
                                // System.out.println("Data Value is:"+yOfB+"---");
                                if (YsOfrWithoutDuplicates.containsKey(yOfB)) {
                                    // System.out.println("Mpike!!");
                                    Numerator++;
                                    Denominator++;
                                } else {
                                    Denominator++;
                                }

                            }

                        } else {// property does not exist in dataguide
                            // DO nothing
                            System.out.println("Property " + B
                                    + " Does not exist in GraphGuide!");
                        }

                    } else { // property does not exist in graphguide
                        // what?
                        System.out.println("Property " + r
                                + " Does not exist in DataGuide!");
                    }

                }
                System.out.println("\n\nNumerator: " + Numerator
                        + "\tDenominator: " + Denominator
                        + "\tDenominatorOld: " + DenominatorOld);
                float pcaConf = (float) Numerator / (float) Denominator;
                System.out.println(B + "\t Implies\t" + r
                        + "\t with PcaConf:\t" + pcaConf + "\n\n");

                out.write(B + "\t" + r + "\t" + pcaConf + "\n");
            }// while
            out.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void XMLImpliesYago(String sortedPairsFile) {

        String filePath = Constants.sortedPairsDirectory + sortedPairsFile;

        FileWriter fstream;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(Constants.pcaConfFolder
                    + "XMLImpliesYago/" + sortedPairsFile);
            out = new BufferedWriter(fstream);
            out.write("XML Path\tYago Property\tpcaConf\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try (BufferedReader br1 = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = br1.readLine()) != null) {
                String[] lineColumns = line.split(Constants.separatorSpace);
                /*if it larger than a threshold*/
                if (Double.parseDouble(lineColumns[2]) < Constants.pcaConfThreshold) {
                    break;
                }

                Numerator = 0;
                Denominator = 0;
                DenominatorOld = 0;

                String B = lineColumns[1]; // B relationship : XML
                String r = lineColumns[0]; // r relationship : YAGO

                System.out.println("B:" + B + "\tr:" + r + "!");

                Iterator<Map.Entry<String, DataGuide>> iteratorDataGuide = DataGuides
                        .entrySet().iterator();
                while (iteratorDataGuide.hasNext()) {
                    Map.Entry<String, DataGuide> entry = iteratorDataGuide
                            .next();
                    String inputValue = entry.getKey();
                    DataGuide currentDataGuide = entry.getValue();

                    if (currentDataGuide.getNodeWithValuesByPathFromTheRoot
                            .containsKey(B)) {
                        Node node = currentDataGuide.getNodeWithValuesByPathFromTheRoot
                                .get(B);
                        ArrayList<ValueNode> YsOfB = node.getValues();
                        /*
                         * I create this Map to put all the values without
                         * duplicates
                         */
                        HashMap<String, ValueNode> YsOfBWithoutDuplicates = new HashMap<String, ValueNode>();
                        for (ValueNode valueNode : YsOfB) {
                            YsOfBWithoutDuplicates.put(
                                    valueNode.normalizedValue, valueNode);
                        }

                        GraphGuideV2 currentGraphGuide = GraphGuides
                                .get(inputValue);
                        System.out.println("InputValue:" + inputValue);

                        if (currentGraphGuide
                                .getPropertyPathsInputEntitiesAndNodes()
                                .containsKey(r)) {
                            TreeMap<String, ArrayList<GraphNode>> inputEntityAndNodes = (TreeMap) currentGraphGuide
                                    .getPropertyPathsInputEntitiesAndNodes()
                                    .get(r);

                            ArrayList<GraphNode> YsOfr = (ArrayList<GraphNode>) inputEntityAndNodes
                                    .get(inputValue);
                            /*
                             * I create this Map to put all the values without
                             * duplicates
                             */
                            HashMap<String, GraphNode> YsOfrWithoutDuplicates = new HashMap<String, GraphNode>();
                            for (GraphNode graphNode : YsOfr) {
                                YsOfrWithoutDuplicates.put(
                                        graphNode.normalizedName, graphNode);
                            }

                            Iterator<Map.Entry<String, ValueNode>> iterator2 = YsOfBWithoutDuplicates
                                    .entrySet().iterator();
                            while (iterator2.hasNext()) {
                                Map.Entry<String, ValueNode> entry2 = iterator2
                                        .next();
                                String yOfB = entry2.getKey();
                                if (YsOfrWithoutDuplicates.containsKey(yOfB)) {
                                    Numerator++;
                                    Denominator++;
                                } else {
                                    Denominator++;
                                }
                            }
                        } else {// property does not exist in dataguide
                            // DO nothing
                            System.out.println("Property " + r
                                    + " Does not exist in GraphGuide!");
                        }

                    } else { // property does not exist in graphguide
                        System.out.println("Property " + B
                                + " Does not exist in DataGuide!");
                    }

                }
                System.out.println("\n\nNumerator: " + Numerator
                        + "\tDenominator: " + Denominator
                        + "\tDenominatorOld: " + DenominatorOld);
                float pcaConf = (float) Numerator / (float) Denominator;
                System.out.println(B + "\t Implies\t" + r
                        + "\t with PcaConf:\t" + pcaConf + "\n\n");

                out.write(B + "\t" + r + "\t" + pcaConf + "\n");
            }// while
            out.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void createGraphGuides(String filepath, String propertyPaths) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {

                String[] columns = sCurrentLine
                        .split(Constants.separatorForInputsFiles);
                /* GRAPH GUIDE */
                if (GraphGuides.containsKey(columns[1])) {

                } else {
                    GraphGuideV2 graphG = new GraphGuideV2();
                    graphG.parseInputsEntitiesFile(columns[1], propertyPaths);

                    GraphGuides.put(columns[1], graphG);
                    System.out.println("New GraphGuide created:" + columns[1]);

                }
            }// while
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void createDataGuide(String inputEntitiesType,
            String pathForFunctionResults) {
        try (BufferedReader br = new BufferedReader(new FileReader(
                Constants.getFileWithInputsForType(inputEntitiesType)))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {

                String[] columns = sCurrentLine
                        .split(Constants.separatorForInputsFiles);

                /* DATA GUIDE */
                DataGuide dataG = new DataGuide();
                /*ATTENTION*/
                /*The endings of the paths change all the times in mac...*/
                dataG.makeparse(pathForFunctionResults
                        + columns[0].replace(" ", "+") + ".xml");
                dataG.reInitMap();
                DataGuides.put(columns[1], dataG);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static String getInputType(String fileName) {

        String[] fileNameTable = fileName.split("_");
        String webSite;
        String function;
        if (fileNameTable.length == 4) {
            webSite = fileNameTable[2];
        } else {
            webSite = fileNameTable[2] + "_" + fileNameTable[3];
        }
        function = fileNameTable[1];
        try (BufferedReader br = new BufferedReader(new FileReader(
                Constants.fileWithFunctionsAndInputTypes))) {
            String line;
            int tmp = 0;
            while ((line = br.readLine()) != null) {
                if (tmp == 0) {
                    tmp++;
                    continue;
                }
                String[] parts = line.split(Constants.separatorSpace);
                if (parts[0].equals(webSite) && parts[1].equals(function)) {
                    return parts[2];
                }

            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public static String pathToFunctionsResults(String[] fileNameTable) {
        String pathForFunctionResults = null;

        if (fileNameTable.length == 4) {
            pathForFunctionResults = Constants.dirWithFunctions
                    + fileNameTable[2] + "/" + fileNameTable[1] + "/";
        } else if (fileNameTable.length > 4) {
            pathForFunctionResults = Constants.dirWithFunctions
                    + fileNameTable[2] + "_" + fileNameTable[3] + "/"
                    + fileNameTable[1] + "/";
        }
        return pathForFunctionResults;
    }

    public static void main(String[] args) {

        HashSet<String> ParsedInputTypes = new HashSet<String>();

        try (BufferedReader br2 = new BufferedReader(new FileReader(
                Constants.inputTypes))) {
            String line;
            int tmp = 0;
            while ((line = br2.readLine()) != null) {
                String[] lineParts = line.split(Constants.separatorSpace);

                createGraphGuides(Constants.getFileWithInputsForType(lineParts[0]), lineParts[1]);
                ParsedInputTypes.add(lineParts[0]);
            }

        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // createGraphGuides(Constants.getFileWithInputsForType("singers"));
        // createGraphGuides(Constants.getFileWithInputsForType("writers"));
        System.out.println("No Of GraphGuides:" + GraphGuides.size());

        File folder = new File(Constants.sortedPairsDirectory);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().equals(".DS_Store")) {
                    continue;
                }

                System.out.println(file.getName());
                String fileName = file.getName();

                String inputValuesType = getInputType(fileName);

                if (!ParsedInputTypes.contains(inputValuesType)) {
                    continue;
                }

                String[] fileNameTable = fileName.split("_");

                String pathToFunction = pathToFunctionsResults(fileNameTable);
                if (pathToFunction.equals("null")) {
                    continue;
                }

                System.out.println("inputValuesType:" + inputValuesType);
                System.out.println("pathForFunctionResults:"
                        + pathToFunction);
                DataGuides.clear();
                System.out.println("DataGuides should be zero:"
                        + DataGuides.size());

                createDataGuide(inputValuesType, pathToFunction);
                System.out.println("DataGuides size:" + DataGuides.size());

                PcaConfCalculation pcaConf = new PcaConfCalculation();

                pcaConf.YagoImpliesXML(fileName);
                pcaConf.XMLImpliesYago(fileName);

            }
        }

        
    }

}
