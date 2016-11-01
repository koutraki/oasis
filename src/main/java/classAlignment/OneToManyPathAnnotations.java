package classAlignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import customization.Constants;
import dataguide.Node;
import dataguide.PathGuideNoValues;
import download.DownloadManager;
import download.WebFunction;

/**
 * @author adi This class process the result of root-to-leaf to predicate-path
 * overlapping
 */
public class OneToManyPathAnnotations {

    public static final HashMap<String, RelationMetadata> oneToOneRelations = new HashMap<String, RelationMetadata>();
    public static final HashMap<String, RelationMetadata> oneToManyRelations = new HashMap<String, RelationMetadata>();

    /**
     * *************************************************************
     */
    /**
     * Process directory with files containing results *
     */
    /**
     * *************************************************************
     */
    public static ArrayList<ForFunctionMetadata> getFunctionsToProcess(String dir) throws Exception {
        ArrayList<ForFunctionMetadata> listFunctions = new ArrayList<ForFunctionMetadata>();
        BufferedReader br = new BufferedReader(new FileReader(Constants.fileWithTheInputTypesOfTheFunctions));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            String[] splits = line.split(Constants.separatorSpace);

            /**
             * each line should contain at least the function and the file *
             */
            if (splits.length < 3) {
                continue;
            }

            String function = splits[0].trim();
            String site = splits[1].trim();
            String typeInput = splits[2].trim();

            String fileWithAlignementResults = dir + "/" + AlgorithmClassAlignment.getFileWithPathAlignementResultsForFunction(function, site, Constants.noSamples);

            File f = new File(fileWithAlignementResults);
            if (!(f.exists() && f.isFile())) {
                System.out.println("No file " + f + " with results for " + function + " " + site);
                continue;
            } else {
                System.out.println("Found file with results for function: " + function + " " + site);
                ForFunctionMetadata record = new ForFunctionMetadata(function, site, fileWithAlignementResults, typeInput);
                listFunctions.add(record);
            }

        }
        br.close();
        return listFunctions;
    }

    /**
     * **********************************************************
     */
    /**
     * load functionalities *
     */
    /**
     * **********************************************************
     */
    public static final class RelationMetadata {

        String relation;
        float functionality;
        float inverseFunctionality;

        public RelationMetadata(String relation, String functionality) {
            this.relation = relation;
            this.functionality = Float.parseFloat(functionality);
        }

        public void setInverse(String inverse) {
            this.inverseFunctionality = Float.parseFloat(inverse);
        }

        @Override
        public String toString() {
            return relation + " " + functionality;
        }
    }

    /**
     * for the inverse relations a new record is created *
     */
    public static void loadFunctionalities(String pathFileWithFunctionalities) throws Exception {
        /**
         * initialize the maps with the functionalities *
         */
        oneToManyRelations.clear();
        oneToOneRelations.clear();

        BufferedReader br = new BufferedReader(new FileReader(pathFileWithFunctionalities));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            String[] splits = line.split(Constants.separatorSpace);

            /**
             * each line should contain at least the direct functionality *
             */
            if (splits.length < 2) {
                continue;
            }

            String relationName = splits[0].trim();
            String functionality = splits[1];
            RelationMetadata rel = new RelationMetadata(relationName, functionality);

            if (splits.length >= 3) {
                String inverseFunctionality = splits[2];
                String invRelation = relationName + "-";
                RelationMetadata inv = new RelationMetadata(invRelation, inverseFunctionality);

                rel.setInverse(inverseFunctionality);
                inv.setInverse(functionality);

                if (isFunctional(inv) ) {
                    if (relationName.startsWith("<")) {
                        oneToOneRelations.put((invRelation).toLowerCase(), inv);

                    } else {
                        oneToOneRelations.put(("<" + invRelation + ">").toLowerCase(), inv);
                    }
                } else {
                    if (relationName.startsWith("<")) {
                        oneToManyRelations.put((invRelation).toLowerCase(), inv);
                    } else {
                        oneToManyRelations.put(("<" + invRelation + ">").toLowerCase(), inv);
                    }
                }

                if (isFunctional(rel)) {
                    if (relationName.startsWith("<")) {
                        oneToOneRelations.put((relationName).toLowerCase(), rel);
                    } else {
                        oneToOneRelations.put(("<" + relationName + ">").toLowerCase(), rel);
                    }
                } else {
                    if (relationName.startsWith("<")) {
                        oneToManyRelations.put((relationName).toLowerCase(), rel);
                    } else {
                        oneToManyRelations.put(("<" + relationName + ">").toLowerCase(), rel);
                    }
                }

            }

        }
        br.close();
    }

    
    public static final boolean isFunctional(RelationMetadata rel){
    
        if(Constants.isDirectFunctional) return rel.functionality > Constants.thresholdOfFunctionalityWhenRelationBecomesOneToOne;
       
            return rel.functionality > Constants.thresholdOfFunctionalityWhenRelationBecomesOneToOne
                    &&    rel.inverseFunctionality > Constants.thresholdOfFunctionalityWhenRelationBecomesOneToOne;
    }
    
    
    /**
     * *************************************************************
     */
    /**
     * Process files with the results *
     */
    /**
     * *************************************************************
     */
    public static ArrayList<PathPair> processResults(String fileWithResults) throws Exception {
        ArrayList<PathPair> results = new ArrayList<PathPair>();

        BufferedReader br = new BufferedReader(new FileReader(fileWithResults));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            String[] splits = line.split(Constants.separatorSpace);

            /**
             * each line should contain the pairs and the confidence *
             */
            if (splits.length < 3) {
                continue;
            }

            String kbPath = splits[0].trim();
            String treePath = splits[1].trim();
            float conf = Float.parseFloat(splits[2].trim());

            if (conf >= Constants.thresholdForTheConfidenceOfTheRootToLeafPathAlignement) {
                results.add(new PathPair(new AbstractPath(kbPath), new AbstractPath(treePath), conf));
            }

        }
        return results;
    }

    /**
     * *************************************************************
     */
    /**
     * Add multiplicity information to the two paths *
     */
    /**
     * *************************************************************
     */
    public static final void addMultiplicityInfoToKBPaths(ArrayList<PathPair> pairs) {
        for (PathPair p : pairs) {
            String KBPath = p.KBpath.path;
            System.err.println("KBPath" + KBPath);
            String[] splits = KBPath.split(","); //CHANGE FROM "/"
            StringBuffer adnotatedKBpath = new StringBuffer();
            ArrayList<AbstractPath.Atom> pathAsAList = new ArrayList<AbstractPath.Atom>();

            for (int i = 0; i < splits.length; i++) {
                String relation = splits[i].trim();

                if (adnotatedKBpath.length() > 0) {
                    adnotatedKBpath.append("/");
                }
                adnotatedKBpath.append(relation);

                AbstractPath.Atom newAtom = new AbstractPath.Atom(relation);
                pathAsAList.add(newAtom);

                String stringToSearch = null;
                if (relation.startsWith("<")) {
                    stringToSearch = relation.toLowerCase();
                } else {
                    stringToSearch = "<" + relation.toLowerCase() + ">";
                }

                RelationMetadata r = oneToManyRelations.get(stringToSearch);
                if (r != null) {
                    newAtom.isMultiple = true; //HERE was the problem
                    System.err.print("NOT Functional Relation:" + stringToSearch + "!!");

                } else {
                    System.err.print("Functional Relation:" + stringToSearch + "!!");

                }
            }
            //     System.err.println("pathAsAList"+pathAsAList.toString());
            p.KBpath.asList = pathAsAList;
            //  System.err.println("pathAsAList2"+p.KBpath.asList);
        }
    }

    public static final void addMultiplicityToXMLPaths(ArrayList<PathPair> pairs, ForFunctionMetadata f) throws Exception {
        /**
         * process the XML documents *
         */
        WebFunction proxy = DownloadManager.getWebFunctionForSiteAndFunctionName(f.site, f.function);
        PathGuideNoValues dataG = new PathGuideNoValues();
        String pathToInputs = Constants.getFileWithInputsForType(f.inputType);
        System.out.println("I extract inputs from file " + pathToInputs);
        ArrayList<String> inputs = DownloadManager.readInputsFromInputsAndEntitiesFiles(pathToInputs, 1);
        for (String input : inputs) {
            //  System.err.println("input:" + f.site + " " + f.function);
            String pathToDocument = proxy.executeCall(input, true);
            dataG.makeparse(pathToDocument);
        }
        dataG.reInitMap();

        for (PathPair p : pairs) {
            /**
             * System.out.println(p.treePath);*
             */
            String[] nodes = p.treePath.path.split("/");
            ArrayList<AbstractPath.Atom> matchedPath = new ArrayList<AbstractPath.Atom>();
            StringBuffer prefix = new StringBuffer();
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i].length() == 0) {
                    continue;
                }
                prefix.append(nodes[i] + "/");
                matchedPath.add(new AbstractPath.Atom(nodes[i]));
            }

            for (Node n : dataG.nodesWithSiblingsWithTheSameName) {
                String pathFromRootInDG = n.getStringPathRootToNode();
                if (!p.treePath.path.startsWith(pathFromRootInDG)) {
                    continue;
                }
                StringBuffer prefixInTreePath = new StringBuffer();
                for (AbstractPath.Atom node : matchedPath) {
                    prefixInTreePath.append(node.name + "/");
                    if (prefixInTreePath.toString().equalsIgnoreCase(pathFromRootInDG)) {
                        /**
                         * System.out.println(" "+pathFromRootInDG);*
                         */
                        node.isMultiple = true;
                    }
                }

            }

            /**
             * System.out.println("New path"+newPath.toString());*
             */
            p.treePath.asList = matchedPath;
        }
    }

    /**
     * ***************************************************************
     */
    public static final ArrayList<ForFunctionMetadata> process(String dir) throws Exception {
        File d = new File(dir);
        if (!(d.exists() && d.isDirectory())) {
            System.out.println("The path " + dir + " is not a directory");
            System.exit(1);
        }

        if (Constants.filterWorkingSet) {
            System.out.println("This class should be run only with the variable filterWorkingSet from class Constants set to fale");
            System.exit(1);
        }

        /**
         * initialize the download manager *
         */
        DownloadManager.initStubs();

        /**
         * the file with the functionalities is in the file with the java
         * project *
         */
        String pathToFileWithFunctionalities = "data/" + Constants.getFileNameWithTheFunctionalities();
        File fileWithFunctionalities = new File(pathToFileWithFunctionalities);
        if (!(fileWithFunctionalities.exists() && fileWithFunctionalities.isFile())) {
            System.out.println("The file with functionalities " + pathToFileWithFunctionalities + " is not found");
            System.exit(1);
        }

        /**
         * load functionalities *
         */
        loadFunctionalities(pathToFileWithFunctionalities);
        /**
         * print the functionalities
         */
        for (RelationMetadata r : oneToManyRelations.values()) {
            System.out.println("Functionality:" + r.relation + " " + r.functionality);
        }

        /**
         * get the descriptions of the functions to process *
         */
        ArrayList<ForFunctionMetadata> functions = getFunctionsToProcess(dir);

        for (ForFunctionMetadata f : functions) {

            /**
             * System.out.println("**************** "+f.function+" of
             * "+f.site+"**************");*
             */
            ArrayList<PathPair> pairs = processResults(f.fileWithAlignmentResults);

            /**
             * process the KB paths *
             */
            addMultiplicityInfoToKBPaths(pairs);

            /**
             * process the XML part *
             */
            addMultiplicityToXMLPaths(pairs, f);

            /**
             * for(PathPair p: pairs){System.out.println(" "+p);}
             * System.out.println();*
             */
            /**
             * set the pair *
             */
            f.annotatedPairs = pairs;
        }
        return functions;
    }

    /**
     * *************************************************************
     */
}
