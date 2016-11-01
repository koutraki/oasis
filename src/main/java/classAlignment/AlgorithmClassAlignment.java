package classAlignment;

import java.io.File;
import java.util.ArrayList;

import classAlignment.PathPair;
import customization.Constants;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AlgorithmClassAlignment {

    public static final String dirWithPathMatchingResults = Constants.projectDirectory + "results_pairs/";
    public static final String dirWithClassAlignmentResults = Constants.projectDirectory + "solution_class_alignment_without_steps_2345_Threshold_0.5NoDublFromPaths/"; //"solution_class_alignment/";

    public static final boolean removeSteps234 = true; //SOS true= without step 234

    /**
     * we assume that every file in the directory has the form
     * <number>_<function>_<site>_Pairs.txt
     */
    public static final String getFileWithPathAlignementResultsForFunction(String function, String site, int samples) {
        return samples + "_" + function + "_" + site + "_Pairs.txt";
    }

    public static final String getFileWithTheResults_of_the_Alignement(String function, String site, int samples) {
        return samples + "_" + function + "_" + site + "_Solution.txt";
    }

    /**
     * *****************************************
     */
    /**
     * STEP 1 *
     */
    /**
     * *****************************************
     */
    public static final void prunePairsFinishingWithAFunctionalRelation(ArrayList<ForFunctionMetadata> functions) {
        for (ForFunctionMetadata f : functions) {
            for (PathPair p : f.annotatedPairs) {
                if (p.KBpath.asList.get(p.KBpath.asList.size() - 1).isMultiple
                        || p.treePath.asList.get(p.treePath.asList.size() - 1).isMultiple) {
                    f.pairsWhereAtLeastOnePathEndsWithOneToManyRelation.add(p);
                } else {

                    f.pairsEndingWithFunctinalRelations.add(p);
                }
            }
            f.annotatedPairs = null;

        }
    }

    /**
     * *****************************************
     */
    /**
     * STEP 2 *
     */
    /**
     * *****************************************
     */
    public static final void computeCandidates(ArrayList<ForFunctionMetadata> functions) {
        for (ForFunctionMetadata f : functions) {
            for (PathPair p : f.pairsEndingWithFunctinalRelations) {
                AbstractPath entityFromKB = p.KBpath.getPrefixForLastMultiple();

                if (entityFromKB == null) {
                    entityFromKB = new AbstractPath(ComputePrecisionRecall.input);
                }

                AbstractPath entityFromTreeDoc = p.treePath.getPrefixForLastMultiple();
                if (entityFromTreeDoc == null) {
                    entityFromTreeDoc = new AbstractPath(ComputePrecisionRecall.root);
                }

                ClassPair candidate = new ClassPair(entityFromKB, entityFromTreeDoc);

                ClassPair fromHashMap = f.getCandidateEqualTo(candidate, f.candidates);
                if (fromHashMap == null) {
                    f.addCandidate(candidate, f.candidates);
                    fromHashMap = candidate;
                }

                /**
                 * update the candidate with the paths of the properties *
                 */
                AbstractPath propertyKB = p.KBpath.getSufixForLastMultiple();
                if (propertyKB == null) {
                    propertyKB = p.KBpath;
                }

                AbstractPath propertyTree = p.treePath.getSufixForLastMultiple();
                if (propertyTree == null) {
                    propertyTree = p.treePath;
                }

                PathPair prop = new PathPair(propertyKB, propertyTree, p.confidence);
                fromHashMap.predicatePaths.add(prop);
            }

        }
    }

    /**
     * *******************************************************
     */
    /**
     * STEP 3: remove candidates with less valuable mappings *
     */
    /**
     * *******************************************************
     */
    public static final void elimintatedDominatedCandidates(ArrayList<ForFunctionMetadata> functions) {
        for (ForFunctionMetadata f : functions) {
            for (String KBClass : f.candidates.keySet()) {
                ArrayList<ClassPair> alignments = f.candidates.get(KBClass);

                /**
                 * get the leader: the alignment that has the pair with the
                 * highest confidence *
                 */
                float max = 0;
                ClassPair leader = null;
                for (ClassPair a : alignments) {
                    if (max < a.getMaxConfidence()) {
                        leader = a;
                        max = a.getMaxConfidence();
                    }
                }

                /**
                 * get the pairs that have the value of confidence equal to max
                 * (there might be several) *
                 */
                for (PathPair dominator : leader.getPairWithHighestConfidence(max)) {
                    for (ClassPair a : alignments) {
                        /**
                         * get the pair that maps the same relation from the KB
                         * *
                         */
                        boolean isDominated = true;
                        for (PathPair p : a.predicatePaths) {
                            if (p.KBpath.path.equals(dominator.KBpath.path)) {
                                if (p.confidence >= dominator.confidence) {
                                    isDominated = false;
                                }
                            }
                        }
                        if (isDominated) {
                            a.isDominated = true;
                        }
                    }
                }
            }
        }
    }

    /**
     * *******************************************************
     */
    /**
     * STEP 4 eliminate duplicate properties *
     */
    /**
     * *******************************************************
     */
    public static final void selectBestRelationFromKBToMatch(ArrayList<ForFunctionMetadata> functions) {
        for (ForFunctionMetadata f : functions) {
            for (String KBClass : f.candidates.keySet()) {
                for (ClassPair cp : f.candidates.get(KBClass)) {
                    for (int i = 0; i < cp.predicatePaths.size(); i++) {
                        for (int j = i + 1; j < cp.predicatePaths.size(); j++) {
                            PathPair pi = cp.predicatePaths.get(i);
                            PathPair pj = cp.predicatePaths.get(j);
                            if (pi.treePath.path.equalsIgnoreCase(pj.treePath.path) || pi.KBpath.path.equalsIgnoreCase(pj.KBpath.path)) {
                                cp.predicatePaths.get(j).isDominated = true;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * STEP 5 eliminate KB paths where the min path functionality is lower than
     * the threshold *
     */
    /**
     * *******************************************************
     */
    public static final void removePathsWithMinPathFunctionalityLowerThanThreshold(ArrayList<ForFunctionMetadata> functions) {
        boolean multipleInKBPath = false;
        boolean multipleInTreePath = false;

        for (ForFunctionMetadata f : functions) {
            for (String KBClass : f.candidates.keySet()) {
                for (ClassPair cp : f.candidates.get(KBClass)) {
                    ArrayList<PathPair> predicates = cp.predicatePaths;

                    for (PathPair p : predicates) {
                        for (int i = 0; i < p.KBpath.asList.size(); i++) {
                            if (p.KBpath.asList.get(i).isMultiple) {
                                multipleInKBPath = true;
                            }
                        }

                        for (int i = 0; i < p.treePath.asList.size(); i++) {
                            if (p.treePath.asList.get(i).isMultiple) {
                                multipleInTreePath = true;
                            }
                        }

                        if (multipleInKBPath == true && multipleInTreePath == false) {
                            System.err.println("Delete path: " + p.KBpath.path + "\t" + p.treePath.path);
                            cp.predicatePaths.remove(p);
                        }

                        multipleInKBPath = false;
                        multipleInTreePath = false;
                    }
                }
            }
        }
    }

    /**
     * STEP 6 eliminate classes that map the same property
     */
    /**
     * *******************************************************
     */
//    public static final void removeClassesMappingSameProperties(ArrayList<ForFunctionMetadata> functions) {
//        boolean multipleInKBPath = false;
//        boolean multipleInTreePath = false;
//
//        
//        for (ForFunctionMetadata f : functions) {
//            HashMap<String, ArrayList<ClassPair>> newCandidates = new HashMap<String, ArrayList<ClassPair>>();
//            ArrayList<ClassPair> newCandidatesClassPairs = new ArrayList<ClassPair>();
//           
//            for (String KBClass : f.candidates.keySet()) {
//
//               // System.err.println("BIKE!");
//                for (ClassPair cp : f.candidates.get(KBClass)) {
//                    List<String> sortedList = new ArrayList<String>();
//                    for (PathPair p : cp.predicatePaths) {
//                        sortedList.add(p.treePath.path);
//                    }
//                    Collections.sort(sortedList);
//                    String concatedProperty = null;
//                    for (String treePropertyPath : sortedList) {
//                        concatedProperty += treePropertyPath + ",";
//                    }
//                    concatedProperty = concatedProperty.substring(0, concatedProperty.length() - 1);
//                    ArrayList<ClassPair> newClassPair;
//                    if (newCandidates.containsKey(concatedProperty)) {
//                        newClassPair = newCandidates.get(concatedProperty);
//                    } else {
//                        newClassPair = new ArrayList<ClassPair>();
//                    }
//                    newClassPair.add(cp);
//                    newCandidates.put(concatedProperty, newClassPair);
//                }
//
//                Iterator it = newCandidates.entrySet().iterator();
//                while (it.hasNext()) {
//                    Map.Entry pairs = (Map.Entry) it.next();
//                    ArrayList<ClassPair> candidateClassPairs = (ArrayList<ClassPair>) pairs.getValue();
//
//                    float higherConfidence = 0.0f;
//                    ClassPair selectedClassPair = null;
//                    for (ClassPair candidateClassPair : candidateClassPairs) {
//                        //for (PathPair p : candidateClassPair.predicatePaths) {
//                        if (candidateClassPair.predicatePaths.get(0).confidence > higherConfidence) {
//                            higherConfidence = candidateClassPair.predicatePaths.get(0).confidence;
//                            selectedClassPair = candidateClassPair;
//                        }
//                        // }
//
//                    }
//                    newCandidatesClassPairs.add(selectedClassPair);
//
//                }
//                 System.out.println("newCandidatesClassPairs SIZE:" + newCandidatesClassPairs.size());
//            newDeduplicate.put(KBClass, newCandidatesClassPairs);
//
//            }// for
//                       f.newDeduplicate = newDeduplicate;
//        }
//
//    }

    /**
     * **********************************************************
     */
    /**
     * *** print the results in a file **
     */
    /**
     * **********************************************************
     */
    public static final void writeFunctionsAndTheirSitesToFile(ArrayList<ForFunctionMetadata> processedFunctions) throws Exception {

        /**
         * prepare the directory where to output *
         */
        File d = new File(AlgorithmClassAlignment.dirWithClassAlignmentResults);
        if (!(d.exists() && d.isDirectory())) {
            boolean success = (new File(AlgorithmClassAlignment.dirWithClassAlignmentResults)).mkdirs();
            if (!success) {
                return;
            }
        }

        for (ForFunctionMetadata f : processedFunctions) {
            String filePath = AlgorithmClassAlignment.dirWithClassAlignmentResults + getFileWithTheResults_of_the_Alignement(f.function, f.site, Constants.noSamples);
            String print = f.printResultsToFile(filePath);
            System.out.println(print);
        }

    }

    public static void main(String[] args) throws Exception {
//    	  	if(args==null ||  args.length<1){
//    	  		System.out.println("The directory should be given");
//    	  		System.exit(1);
//    	  	}

        //String dir=Constants.projectDirectory+args[0];
        String dir = Constants.projectDirectory + "results100_LastVersion_0.1NoDuplicates/";

        ArrayList<ForFunctionMetadata> functions = OneToManyPathAnnotations.process(dir);

        /**
         * step 1 *
         */
        prunePairsFinishingWithAFunctionalRelation(functions);

        /**
         * step 2 *
         */
        computeCandidates(functions);

        /**
         * step 3 *
         */
        elimintatedDominatedCandidates(functions);

        /**
         * step 4 *
         */
        selectBestRelationFromKBToMatch(functions);

        /**
         * step 5 *
         */
        removePathsWithMinPathFunctionalityLowerThanThreshold(functions);

        /**
         * step 6 *
         */
     //   removeClassesMappingSameProperties(functions);

        writeFunctionsAndTheirSitesToFile(functions);

    }

}
