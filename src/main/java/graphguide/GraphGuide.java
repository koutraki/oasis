package graphguide;

//import org.openjena.atlas.iterator.Iter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeMap;

import knowledgebase.QueryDBPedia;
import knowledgebase.QueryYAGOTDB;
import customization.Constants;

//import dataguide.Node;

public class GraphGuide {

	public static final int graphLevel = 2;
	public static final double threshold = 0.1;

	public static HashMap<String, ArrayList<GraphNode>> graphGuide; // DATAGUIDE
																	// for the
																	// knowledge
																	// base
																	// part. All
																	// the
																	// nodes,
																	// literal
																	// nodes and
																	// entity
																	// nodes
	// path
	private HashMap<String, TreeMap<String, ArrayList<GraphNode>>> propertyPathsInputEntitiesAndNodes; // All
																										// the
																										// literal
																										// nodes
	// associated to each
	// property path

	HashSet<GraphNode> literalNodesList; // List with all the literal nodes

	// HashMap<String, String> pathToNamespase;
	// HashMap<String, String> namespaseToPath;

	public static QueryYAGOTDB queryYago = new QueryYAGOTDB(); // Object of
																// class
																// responsible
																// to query YAGO

	/***************************************************************************************/
	public GraphGuide() {
		graphGuide = new HashMap<String, ArrayList<GraphNode>>();
		propertyPathsInputEntitiesAndNodes = new HashMap<String, TreeMap<String, ArrayList<GraphNode>>>();
		literalNodesList = new HashSet<GraphNode>();

		// pathToNamespase = new HashMap<String, String>();
		// namespaseToPath = new HashMap<String, String>();

		Constants.initializePathNamespaseMaps();
	}

	/***************************************************************/
	// public void initializePathNamespaseMaps() {
	// pathToNamespase.put("http://yago-knowledge.org/resource/", "Y:");
	// namespaseToPath.put("Y:", "http://yago-knowledge.org/resource/");
	// }

	/*****************************************************************/

	public HashMap getPropertyPathsInputEntitiesAndNodes() {
		return propertyPathsInputEntitiesAndNodes;
	}

	public String replacePath(String path) {
		Iterator it = Constants.pathToNamespase.entrySet().iterator();
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

	/**************************************************************/
	public void parseInputsEntitiesFile(String inputEntity) {

		graphNodeFromQueryResults(inputEntity, inputEntity,
				queryForExtractingInfo(inputEntity), 0);

	}

	/*****************************************************************/

	public HashMap queryForExtractingInfo(String inputEntity) {
		//
		Functionality functionality = new Functionality();
		functionality.parseFunctinalitiesFile(Constants.functionality);

		HashMap<String, ArrayList<String>> finalResultsList = new HashMap<String, ArrayList<String>>();

		String query = "select DISTINCT ?r ?x where{ " + inputEntity
				+ " ?r ?x ." + "} limit 350";
		List list = new ArrayList();
		if (Constants.ExperimentsOnYago) {
			list = queryYago.queryExecutionYago(query);
		} else {
			list = QueryDBPedia.queryExecutionDBPedia(query);
		}

		for (int i = 0; i < list.size(); i++) {
			String[] splitedResults = list.get(i).toString()
					.split("\\s\\)\\s\\(\\s");
			String[] prop = splitedResults[0].split("= ");
			String objct1 = splitedResults[1].replace(" )", "").replace(
					"?x = ", "");
			String object = objct1.replaceAll("\"", ""); // remove the " "

			if (Constants.ExperimentsOnYago) {
				if (Functionality.functionalities.get(prop[1]) > threshold) {
					if (!object.startsWith("http:")) { // don't want urls

						if (finalResultsList.containsKey(prop[1])) {
							ArrayList objects = finalResultsList.get(prop[1]);
							objects.add(object);
							finalResultsList.put(prop[1], objects);
						} else {
							ArrayList objects = new ArrayList();
							objects.add(object);
							finalResultsList.put(prop[1], objects);
						}
					}
				}
			} else {
				if (!object.startsWith("http:")) { // don't want urls

					if (finalResultsList.containsKey(prop[1])) {
						ArrayList objects = finalResultsList.get(prop[1]);
						objects.add(object);
						finalResultsList.put(prop[1], objects);
					} else {
						ArrayList objects = new ArrayList();
						objects.add(object);
						finalResultsList.put(prop[1], objects);
					}
				}
			}
			// else {
			//
			// System.out.println("\nFunctionality of " + prop[1]
			// + " is lower than threshold!!!\n\n");
			// }

		}

		String oppositeQuery = "select DISTINCT ?r ?x where{ ?x ?r "
				+ inputEntity + ".} limit 350";
		List list2 = new ArrayList();
		if (Constants.ExperimentsOnYago) {
			list2 = queryYago.queryExecutionYago(oppositeQuery);
		} else {
			list2 = QueryDBPedia.queryExecutionDBPedia(query);
		}

		for (int i = 0; i < list2.size(); i++) {
			String[] splitedResults = list2.get(i).toString()
					.split("\\s\\)\\s\\(\\s");
			String[] prop = splitedResults[0].split("= ");
			String object = splitedResults[1].replace(" )", "").replace(
					"?x = ", "");
			if (object.startsWith("\"") && object.endsWith("\"")) {
				object = object.replaceFirst("\"", "");
				object = object.substring(0, object.length() - 1);
			}
			String propertyInverse = prop[1] + "-";

			if (Constants.ExperimentsOnYago) {
				if (Functionality.functionalities.get(propertyInverse) > threshold) {
					if (!object.startsWith("http:")) { // don't want urls
						if (finalResultsList.containsKey(propertyInverse)) {
							ArrayList objects = finalResultsList
									.get(propertyInverse);
							objects.add(object);
							finalResultsList.put(propertyInverse, objects);
						} else {
							ArrayList objects = new ArrayList();
							objects.add(object);
							finalResultsList.put(propertyInverse, objects);
						}
					}
				}
			}else{
				if (!object.startsWith("http:")) { // don't want urls
					if (finalResultsList.containsKey(propertyInverse)) {
						ArrayList objects = finalResultsList
								.get(propertyInverse);
						objects.add(object);
						finalResultsList.put(propertyInverse, objects);
					} else {
						ArrayList objects = new ArrayList();
						objects.add(object);
						finalResultsList.put(propertyInverse, objects);
					}
				}
			}
			// else {
			//
			// System.out.println("\nFunctionality of " + propertyInverse
			// + " is lower than threshold!!!\n\n");
			// }

		}
		return finalResultsList;
	}

	/*******************************************************************/
	/****** Constructs the graph by adding each time a Node ******/
	/****** Recursive function ******/
	/*******************************************************************/

	public void graphNodeFromQueryResults(String inputEntity,
			String parentEntity, HashMap results, int level) {

		if (level <= graphLevel) {

			Iterator it = results.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Entry) it.next();
				String property = (String) pairs.getKey();
				property = replacePath(property);
				ArrayList<String> values = (ArrayList) pairs.getValue();

				// Put in the hashmap of properties the property and the
				// values.size

				for (int i = 0; i < values.size(); i++) {
					// String property = splitedResults[0];
					String object = values.get(i);

					GraphNode parent = getParentNode(parentEntity, level);

					if (checkForCycles(property, object, parent)) {
						// System.out.println("Cycle eliminated!!");
						continue;
					}

					String relationPath;
					if (parent.relationPathFromRoot != null)
						relationPath = parent.relationPathFromRoot + "/"
								+ property;
					else
						relationPath = property;

					// System.out.println(relationPath+"\t"+object);

					// Here I should add to the new HashMap of properties and
					// maz occurencies
					// If(values.size()> get(relationPath) value)
					// Override the value with the values.size and

					if (object.startsWith("<")) { // entity node

						ArrayList<GraphNode> nodeList;
						if (graphGuide.containsKey(relationPath)) { // if
																	// already
							// contains
							nodeList = graphGuide.get(relationPath);
						} else {
							nodeList = new ArrayList<GraphNode>();
						}

						GraphNode gn = new GraphNode(1, object, relationPath,
								parent, level);
						nodeList.add(gn);
						graphGuide.put(relationPath, nodeList);
						graphNodeFromQueryResults(inputEntity, object,
								queryForExtractingInfo(object), level + 1);

					} else { // literal node

						ArrayList<GraphNode> nodeList;
						if (graphGuide.containsKey(relationPath)) {
							nodeList = graphGuide.get(relationPath);
						} else {
							nodeList = new ArrayList<GraphNode>();
						}
						GraphNode gn = new GraphNode(0, object, relationPath,
								parent, level);
						nodeList.add(gn);
						graphGuide.put(relationPath, nodeList);
						literalNodesList.add(gn);
						// addLiteralNodes(relationPath, gn);
						addToPropertyPathsInputEntitiesAndNodes(relationPath,
								inputEntity, gn);
						// literalNodes.add(gn);
					}
				}
				// }//else
			}// while
		}

	}

	/****************************************************************************************/

	public boolean checkForCycles(String property, String object,
			GraphNode parent) {
		if (PropertyFollowingByInverseProperty(property, parent)) {
			GraphNode parentsParent = parent.parent;
			if (parentsParent.name.equals(object)) {
				return true;
			} else
				return false;
		} else {
			return false;
		}
	}

	/***************************************************************************************/

	public boolean PropertyFollowingByInverseProperty(String property,
			GraphNode parent) {
		if (property.endsWith("-"))
			property = property.substring(0, property.length() - 1);
		else
			property = property + "-";

		if (parent.relationPathFromRoot != null) {

			if (parent.relationPathFromRoot.contains("/")) {
				String[] parts = parent.relationPathFromRoot.split("/");
				String propertyToCompair = parts[parts.length - 1];
				if (property.equals(propertyToCompair))
					return true;
				else
					return false;
			} else {
				if (property.equals(parent.relationPathFromRoot))
					return true;
				else
					return false;
			}
		} else
			return false;

	}

	/***************************************************************************************/
	/*
	 * Structure with the info for the calculation of the confidence for a
	 * property path and an xml path
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

	/**********************************************************************************/
	public static boolean contains(ArrayList<GraphNode> nodeList, String name) {

		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).name.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**************************************************************************************/

	public static GraphNode getParentNode(String name, int childLevel) {

		Iterator it = graphGuide.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Entry) it.next();
			ArrayList<GraphNode> nodeList = (ArrayList) pairs.getValue();

			for (int i = 0; i < nodeList.size(); i++) {
				if (nodeList.get(i).name.equals(name)
						&& nodeList.get(i).level == childLevel - 1) {
					return nodeList.get(i);
				}
			}
		}
		return new GraphNode(1, name, null, null, 0);
	}

	/**************************************************************************************/
	/* NOT USED */
	// public String calculatePaths(GraphNode gn) {
	//
	// if (gn.parent == null) {
	// return gn.name + "/";
	// } else {
	//
	// calculatePaths(gn.parent);
	// }
	// return gn.name;
	//
	// }

	/*****************************************************************************************************/
	public void printpropertyPathsInputEntitiesAndNodes() {

		Iterator it = propertyPathsInputEntitiesAndNodes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, TreeMap<String, ArrayList<GraphNode>>> pairs = (Entry<String, TreeMap<String, ArrayList<GraphNode>>>) it
					.next();
			System.out.println("Property Path:\t\t" + pairs.getKey());

			TreeMap<String, ArrayList<GraphNode>> entitiesNodes = pairs
					.getValue();

			Iterator iter = entitiesNodes.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, ArrayList<GraphNode>> pairs2 = (Entry<String, ArrayList<GraphNode>>) it
						.next();

				System.out.println("\n\nInput Entity: \t\t" + pairs2.getKey());
				ArrayList<GraphNode> nodes = pairs2.getValue();
				for (GraphNode node : nodes) {
					System.out.println("\t\t\t\t\t" + node.name);
				}
			}
		}

	}

	/*******************************************************************************************************/
	public void printLiteralNodesList() {
		for (GraphNode gn : literalNodesList) {
			System.out.println(gn.name + "\t\t" + gn.level + "\t\t"
					+ gn.normalizedName + "\t\t\t" + gn.relationPathFromRoot);
		}
	}

	/*******************************************************************************************/
	public void printGraph() {

		Iterator it = GraphGuide.graphGuide.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();

			System.out.println("Property:\t \t" + pairs.getKey());
			System.out.println("Edges:");
			// System.out.println("\tLiterals:\n\n");

			ArrayList<GraphNode> nodes = (ArrayList<GraphNode>) pairs
					.getValue();

			for (int i = 0; i < nodes.size(); i++) {

				System.out.println(nodes.get(i).level + "\t\t\t\t"
						+ nodes.get(i).parent.name + "\t\t\t\t\t\t\t\t\t\t"
						+ nodes.get(i).name);

			}
		}

	}

	/*****************************************************************************************/
	/** ADD GRAPHNODES in PRIORITY QUEUE */
	/****************************************************************************************/
	public PriorityQueue<GraphNode> getValuesInAPriorityQueue(
			Comparator<GraphNode> c) {
		PriorityQueue<GraphNode> pq = new PriorityQueue<GraphNode>(200, c);
		/** construct the heap **/
		// System.out.println("literalNodesList.size: "+literalNodesList.size());
		for (GraphNode gn : literalNodesList) {
			pq.add(gn);
		}
		return pq;
	}

	/**************************************************************/
	public static void main(String[] args) {

		final long startTime = System.currentTimeMillis();

		// PrintStream out = null;
		// try {
		// out = new PrintStream(
		// new FileOutputStream(
		// "/Users/mary/Dropbox/MapFunction_Document/Nico-Data/outputLogs.txt"));
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// System.setOut(out);
		GraphGuide graphG = new GraphGuide();

		graphG.parseInputsEntitiesFile("<http://yago-knowledge.org/resource/David_Bowie>");

		graphG.printLiteralNodesList();
		System.out.println(graphG.literalNodesList.size());

		final long endTime = System.currentTimeMillis();
		System.setOut(System.out);
		System.out.println("Total execution time: " + (endTime - startTime)
				/ 1000);
	}
}
