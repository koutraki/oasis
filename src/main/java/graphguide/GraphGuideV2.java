package graphguide;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.Map.Entry;

import customization.Constants;
import knowledgebase.QueryBNFTDB;
import knowledgebase.QueryDBPedia;
import knowledgebase.QueryYAGOTDB;

public class GraphGuideV2 {

    /* the literal nodes associated to each property path */
    private HashMap<String, TreeMap<String, ArrayList<GraphNode>>> propertyPathsInputEntitiesAndNodes;
    /* List with all the literal nodes, important structure for Overlp paths */
    HashSet<GraphNode> literalNodesList; // List with all the literal nodes

    /* Object of class responsible to query YAGO */
    public static QueryYAGOTDB queryYago = new QueryYAGOTDB();

    public static QueryBNFTDB queryBNF = new QueryBNFTDB();

    public static QueryDBPedia queryDBPedia = new QueryDBPedia();

    public GraphGuideV2() {
        literalNodesList = new HashSet<GraphNode>();
        propertyPathsInputEntitiesAndNodes = new HashMap<String, TreeMap<String, ArrayList<GraphNode>>>();
        Constants.initializePathNamespaseMaps();
    }

    /* Initial method */
    public void parseInputsEntitiesFile(String inputEntity, String inputType) {

        String filePathProperties = Constants
                .getFileWithPropertyPathsForType(inputType);

        try (BufferedReader br1 = new BufferedReader(new FileReader(
                filePathProperties))) {
            String sCurrentLine;//propertyPath
            while ((sCurrentLine = br1.readLine()) != null) {
                  String query = queryConstractionWithoutDataCycles(inputEntity, sCurrentLine);
	//        String query = queryConstraction(inputEntity, sCurrentLine);
        //        String query = queryConstractionWithCycles(inputEntity, sCurrentLine); /** SOS   is with cycles.....  **/

                String[] properties = sCurrentLine.split(", ");
                String propertyPath = "";
                for (int i = 0; i < properties.length; i++) {
                    propertyPath += properties[i] + ",";
                }
                propertyPath = propertyPath.substring(0, propertyPath.length() - 1);

                List list = new ArrayList();
                if (Constants.targetKB.equals("YAGO")) {
                    list = queryYago.queryExecutionYago(query);
                } else if (Constants.targetKB.equals("BNF")) {
                    list = queryBNF.queryExecutionBNF(query);
                } else if (Constants.targetKB.equals("DBPedia")) {
                    list = queryDBPedia.queryExecutionDBPedia(query);
                }

                for (int i = 0; i < list.size(); i++) {
                    String queryResult = processQueryResult(list.get(i));
                    GraphNode gn = new GraphNode(0, queryResult, propertyPath,
                            null, 0);
                    if (gn.normalizedName.equals("")) {
                  //      System.out.println("Empty normalized value for:"+gn.relationPathFromRoot);
                        continue;
                    }
                    literalNodesList.add(gn);
                    addToPropertyPathsInputEntitiesAndNodes(propertyPath, inputEntity, gn);
                    //			System.out.println(list.get(i));
                }
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String processQueryResult(Object queryResult) {
		//result example: ( ?y = "Bowie" )
        //	System.out.println("lala:"+queryResult);
        String[] result;
        if (queryResult.toString().contains("\"")) {
            result = queryResult.toString().split("\"");

            return result[1];
        } else {
            result = queryResult.toString().split(Constants.separatorSpace);
            return result[3];
        }

    }

    public void parsePropertiesPathsFile(String filePath) {
        try (BufferedReader br1 = new BufferedReader(new FileReader(
                filePath))) {

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * **********************************************************************************************************
     */
    public HashMap getPropertyPathsInputEntitiesAndNodes() {
        return propertyPathsInputEntitiesAndNodes;
    }

    /**
     * **********************************************************************************************************
     */
    public String queryConstraction(String inputEntity, String propertyPathLine) {
        String query = "Select DISTINCT ?y where { ";

        String filter = null;

        String[] properties = propertyPathLine.split(", ");
        for (int i = 0; i < properties.length; i++) {
            String currentProperty = replaceNamespaceWithActualPath(properties[i]);

            if (!currentProperty.endsWith("-")) {

                if (i == 0 && i < properties.length - 1) {
                    query += inputEntity + " " + currentProperty + " ?y" + i + " . ";
                } else if (i == 0 && i == properties.length - 1) {
                    query += inputEntity + " " + currentProperty + " ?y . ";
                } else if (i == properties.length - 1) {
                    query += " ?y" + (i - 1) + " " + currentProperty + " ?y . ";
                } else {
                    String prevProp = replaceNamespaceWithActualPath(properties[i - 1]);
                    if ((currentProperty + "-").equals(prevProp)) {//potential cycle
                        //		System.out.println("Potential Cycle!");
                        if (i - 1 == 0) {
                            filter = "FILTER ( " + inputEntity + " != ?y" + i + " )";
                        } else {
                            filter = "FILTER ( y" + (i - 1) + " != ?y" + i + " )";
                        }
                    }

                    query += " ?y" + (i - 1) + " " + currentProperty + " ?y" + i + " . ";
                }

            } else {
                if (i == 0 && i < properties.length - 1) {
                    query += " ?y" + i + " " + currentProperty.substring(0, currentProperty.length() - 1) + " " + inputEntity + " . ";
                } else if (i == 0 && i == properties.length - 1) {
                    query += "?y " + currentProperty.substring(0, currentProperty.length() - 1) + inputEntity + " . ";
                } else if (i == properties.length - 1) {
                    query += "?y " + currentProperty.substring(0, currentProperty.length() - 1) + " ?y" + (i - 1) + " . ";
                } else {
                    String prevProp = replaceNamespaceWithActualPath(properties[i - 1]);
                    if (currentProperty.substring(0, currentProperty.length() - 1).equals(prevProp)) {//potential cycle
                        //				System.out.println("Potential Cycle!");
                        if (i - 1 == 0) {
                            filter = "FILTER ( " + inputEntity + " != ?y" + i + " )";
                        } else {
                            filter = "FILTER ( y" + (i - 1) + " != ?y" + i + " )";
                        }
                    }
                    query += "?y" + i + " " + currentProperty.substring(0, currentProperty.length() - 1) + " ?y" + (i - 1) + " . ";
                }

            }

        }
	//	 FILTER ( ?x != ?y )
        //for (int i = 0; i < properties.length-2; i++) {
        if (filter != null) {
            query += filter;
        }
        //	}
        query += " }";

        return query;
    }

    /**
     * **********************************************************************************************************
     */
    /**
     * ***************	Query Constraction WITHOUT DATA Cycles			************************
     */
    /**
     * **********************************************************************************************************
     */
    public String queryConstractionWithoutDataCycles(String inputEntity, String propertyPathLine) {
        String query = "Select DISTINCT ?y where { ";

        ArrayList<String> propertiesLine = new ArrayList<String>();

        String[] properties = propertyPathLine.split(", ");
        for (int i = 0; i < properties.length; i++) {
            String currentProperty = replaceNamespaceWithActualPath(properties[i]);

            String property;
            if (currentProperty.endsWith("-")) {
                property = currentProperty.substring(0, currentProperty.length() - 1);
            } else {
                property = currentProperty;
            }

            if (!currentProperty.endsWith("-")) {
                if (i == 0 && i < properties.length - 1) { //first node and there are more
                    query += inputEntity + " " + property + " ?y" + i + " . ";
                    propertiesLine.add(inputEntity);
                    propertiesLine.add("?y" + i);
                } else if (i == 0 && i == properties.length - 1) {//first and last node
                    query += inputEntity + " " + property + " ?y . ";
			//	propertiesLine.add(inputEntity);
                    //	propertiesLine.add("?y");
                } else if (i == properties.length - 1) { //last node
                    query += " ?y" + (i - 1) + " " + property + " ?y . ";
                    //	propertiesLine.add("?y");
                } else {
                    query += " ?y" + (i - 1) + " " + property + " ?y" + i + " . ";
                    propertiesLine.add("?y" + i);
                }
            } else {
                if (i == 0 && i < properties.length - 1) {
                    query += " ?y" + i + " " + property + " " + inputEntity + " . ";
                    propertiesLine.add(inputEntity);
                    propertiesLine.add("?y" + i);
                } else if (i == 0 && i == properties.length - 1) {
                    query += "?y " + property + inputEntity + " . ";
                } else if (i == properties.length - 1) {
                    query += "?y " + property + " ?y" + (i - 1) + " . ";
                } else {
                    query += "?y" + i + " " + property + " ?y" + (i - 1) + " . ";
                    propertiesLine.add("?y" + i);
                }

            }
        } //for
 
        if (propertiesLine.size() > 0) {
            query += "FILTER ( "; // y"+(i-1)+" != ?y"+i+" )";
            for (int i = propertiesLine.size() - 1; i > 0; i--) {
                for (int j = i - 1; j >= 0; j--) {
                    query += propertiesLine.get(i) + " != " + propertiesLine.get(j) + " && ";
                }

            }
            query = query.substring(0, query.length() - 3);
            query += " )";

        }

        query += " }";

        return query;
    }

    /**
     * **********************************************************************************************************
     */
    /**
     * ***	query constraction without eliminated the cycles - simple query
     * constraction		*****
     */
    /**
     * **********************************************************************************************************
     */
    public String queryConstractionWithCycles(String inputEntity, String propertyPathLine) {
        
     //   System.out.println("\n\n\t\t Queries with Cycles!! \n\n");
        String query = "Select DISTINCT ?y where { ";

        String[] properties = propertyPathLine.split(", ");
        for (int i = 0; i < properties.length; i++) {
            String currentProperty = replaceNamespaceWithActualPath(properties[i]);

            if (!currentProperty.endsWith("-")) {

                if (i == 0 && i < properties.length - 1) {
                    query += inputEntity + " " + currentProperty + " ?y" + i + " . ";
                } else if (i == 0 && i == properties.length - 1) {
                    query += inputEntity + " " + currentProperty + " ?y . ";
                } else if (i == properties.length - 1) {
                    query += " ?y" + (i - 1) + " " + currentProperty + " ?y . ";
                } else {
                    query += " ?y" + (i - 1) + " " + currentProperty + " ?y" + i + " . ";
                }

            } else {
                if (i == 0 && i < properties.length - 1) {
                    query += " ?y" + i + " " + currentProperty.substring(0, currentProperty.length() - 1) + " " + inputEntity + " . ";
                } else if (i == 0 && i == properties.length - 1) {
                    query += "?y " + currentProperty.substring(0, currentProperty.length() - 1) + inputEntity + " . ";
                } else if (i == properties.length - 1) {
                    query += "?y " + currentProperty.substring(0, currentProperty.length() - 1) + " ?y" + (i - 1) + " . ";
                } else {
                    query += "?y" + i + " " + currentProperty.substring(0, currentProperty.length() - 1) + " ?y" + (i - 1) + " . ";
                }

            }

        }

        query += " }";

        return query;
    }

    /**
     * **********************************************************************************************************
     */

    /**
     * ************************************************************************************
     */
    /*
     * Structure with the info for the calculation of the confidence for a
     * property path and an xml path
     */
    /**
     * **********************************************************************************************************
     */
    public void addToPropertyPathsInputEntitiesAndNodes(String propertyPath,
            String inputEntity, GraphNode gn) {

        TreeMap<String, ArrayList<GraphNode>> entitiesNodes;
        ArrayList<GraphNode> nodes;

        if (propertyPathsInputEntitiesAndNodes.containsKey(propertyPath)) {
            entitiesNodes = propertyPathsInputEntitiesAndNodes
                    .get(propertyPath);

            if (entitiesNodes.containsKey(inputEntity)) {
                nodes = entitiesNodes.get(inputEntity);
            } else {
                nodes = new ArrayList<GraphNode>();
            }
        } else {
            entitiesNodes = new TreeMap<String, ArrayList<GraphNode>>();
            nodes = new ArrayList<GraphNode>();
        }

        nodes.add(gn);
        entitiesNodes.put(inputEntity, nodes);
        propertyPathsInputEntitiesAndNodes.put(propertyPath, entitiesNodes);

    }

    public String replaceNamespaceWithActualPath(String path) {
        Iterator it = Constants.namespaseToPath.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Entry) it.next();
            if (path.contains(pairs.getKey().toString())) {
                path = path.replace(pairs.getKey().toString(), pairs.getValue()
                        .toString());
                return path;
            }
        }
        return path;
    }

    public void printLiteralNodesList() {

        for (GraphNode graph : literalNodesList) {
            System.out.println(graph.normalizedName + "\t" + graph.relationPathFromRoot);
        }
    }

    /**
     * **************************************************************************************
     */
    /**
     * ADD GRAPHNODES in PRIORITY QUEUE
     */
    /**
     * *************************************************************************************
     */
    public PriorityQueue<GraphNode> getValuesInAPriorityQueue(
            Comparator<GraphNode> c) {
        PriorityQueue<GraphNode> pq = new PriorityQueue<GraphNode>(200, c);
        /**
         * construct the heap *
         */
        // System.out.println("literalNodesList.size: "+literalNodesList.size());
        for (GraphNode gn : literalNodesList) {
            pq.add(gn);
        }
        return pq;
    }

    /**
     * **************************************************************************************
     */
    /**
     * *************************************************************************************
     */

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        int NumberofInputvalues = 0;

        /** FOR SELF TESTING OF GRAPH GUIDE**/
        GraphGuideV2 graphG = new GraphGuideV2();

        graphG.parseInputsEntitiesFile("<http://dbpedia.org/resource/David_Bowie>", "singers");

        System.out.println("Literal Node List:");
        graphG.printLiteralNodesList();

        System.out.println(graphG.literalNodesList.size());
        final long endTime = System.currentTimeMillis();
        System.setOut(System.out);
        System.out.println("Total execution time: " + (endTime - startTime)
                / 1000);

    }

}
