package Overlapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import graphguide.GeneralValueNode;
import graphguide.GraphGuide;
import graphguide.GraphNode;
import knowledgebase.QueryYAGOTDB;
import customization.Constants;
import dataguide.DataGuide;
import dataguide.XMLPathPair;
import dataguide.ValueNode;

public class OverlappingGuides {

	public static int NumberofInputvalues = 0;
	GraphGuide graphG = null;
	DataGuide dataG = null;

	/**
	 * several comparators can be used => Clement's methods, simple string
	 * comparator
	 **/
	Comparator<GeneralValueNode> c = null;

	/** stores the results **/
	public HashSet<XMLPathPair> pathPairsSet;
	
	
	public static HashMap<String, Integer> totalPathsWithValues;
	public static HashSet<String> pathPairsSetWithValues;

	public OverlappingGuides(GraphGuide graphG, DataGuide dataG,
			Comparator<GeneralValueNode> c) {
		this.graphG = graphG;
		this.dataG = dataG;

		pathPairsSet = new HashSet<XMLPathPair>();
		this.c = c;

		overlapping(c);
	}

	public void overlapping(Comparator<GeneralValueNode> c) {
		PriorityQueue<GraphNode> pq1 = graphG
				.getValuesInAPriorityQueue(new GraphNode.NormalizeCompare());
		PriorityQueue<ValueNode> pq2 = dataG
				.getValuesInAPriorityQueue(new ValueNode.NormalizedCompare());

		// for(GraphNode g : pq1){
		// System.out.println(g.normalizedName);
		// }
		// System.out.println("\n\n\n\n\n\nDATA GUIDE!!!!!!\n\n\n");
		// for(ValueNode v : pq2){
		// System.out.println(v.normalizedValue);
		// }

		HashSet<XMLPathPair> processedPaths = new HashSet<XMLPathPair>();

		while (!pq1.isEmpty() && !pq2.isEmpty()) {
	//		System.out.println("MPIKE STI WHILE!");
			GraphNode peek1 = pq1.peek();
			ValueNode peek2 = pq2.peek();
			int comp = c.compare(peek1, peek2);
			if (comp < 0) {
				pq1.poll();
				/** System.out.println("Value q1: "+vn.value); **/
			} else if (comp > 0) {
				pq2.poll();
				/** System.out.println("Value q2: "+vn.value); **/
			} else {
				/**
				 * since there might be several nodes with the same value, we
				 * treat the problem for the general case
				 **/
//				System.out.println("Overlapping detected:  value="
//						+ peek2.normalizedValue);
				ArrayList<GraphNode> list1 = new ArrayList<>();
				list1.add(pq1.poll());
				while (!pq1.isEmpty()
						&& c.compare(pq1.peek(), list1.get(0)) == 0) {
					list1.add(pq1.poll());
				}

				ArrayList<ValueNode> list2 = new ArrayList<>();
				list2.add(pq2.poll());
				while (!pq2.isEmpty()
						&& c.compare(pq2.peek(), list2.get(0)) == 0) {
					list2.add(pq2.poll());
				}

//				System.out.println("\t occurs in GraphGuide " + list1.size()
//						+ " times and occurs in DataGuide " + list2.size()
//						+ " times\n");
				processPathPairs(list1, list2, pathPairsSet, processedPaths);
			}
		}

	}

	public void processPathPairs(ArrayList<GraphNode> list1,
			ArrayList<ValueNode> list2, HashSet<XMLPathPair> pathPairsCount,
			HashSet<XMLPathPair> processedPaths) {
		for (GraphNode gn1 : list1)
			for (ValueNode n2 : list2) {
				String p1 = gn1.relationPathFromRoot;
				String p2 = n2.parent.getStringPathRootToNode();
				XMLPathPair pair = new XMLPathPair(p1, p2);
				if (processedPaths.contains(pair)) {
				} // return;
				else
					processedPaths.add(pair);

				if (!pathPairsCount.contains(pair)) {
					pathPairsCount.add(pair);
				}
				/*Extra for new confidence!*/
//				if(!pathPairsSetWithValues.contains(pair)){
//					System.out.println("PROSTHETEI: "+pair.pathDG1+"  "+pair.pathDG2);
//					pathPairsSetWithValues.add(pair.pathDG2);
//				}
				// else {
				// int count=pathPairsCount.get(pair);
				// pathPairsCount.put(pair, new Integer(count+1));
				// }

			}
	}

	/********************************************************************************/
	/****** Print Priority Queue ********************/
	/*******************************************************************************/
	public void checkPriorityQueues() {
		PriorityQueue<GraphNode> pq1 = graphG
				.getValuesInAPriorityQueue(new GraphNode.NormalizeCompare());
		PriorityQueue<ValueNode> pq2 = dataG
				.getValuesInAPriorityQueue(new ValueNode.NormalizedCompare());

		while (!pq1.isEmpty()) {
			System.out.println(pq1.poll().normalizedName);
		}

		System.out.println("\n\nDATA GUIDE \n\n");

		while (!pq2.isEmpty()) {
			System.out.println(pq2.poll().normalizedValue);
		}

	}

	public void printPathPairsCount() {
		for (XMLPathPair pp : pathPairsSet) {
			System.out.println("Path GG: " + pp.pathDG1 + "\t Path DG: "
					+ pp.pathDG2);
		}

	}

	public static void addTotalPathPairs(HashMap<XMLPathPair, Integer> totalPathPairs,
			HashSet<XMLPathPair> currentPathPairs) {
		for (XMLPathPair pp : currentPathPairs) {
			if (totalPathPairs.containsKey(pp)) {
				int cnt = (int) totalPathPairs.get(pp);
				totalPathPairs.put(pp, new Integer(cnt + 1));
			} else {
				totalPathPairs.put(pp, new Integer(1));
			}

		}
	}

	/*Extra for new Confidence!*/
	public static void addTototalPathsWithValues() {
		for (String pp : pathPairsSetWithValues) {
			if (totalPathsWithValues.containsKey(pp)) {
				int cnt = (int) totalPathsWithValues.get(pp);
				totalPathsWithValues.put(pp, new Integer(cnt + 1));
			//	System.out.println("XML path: "+pp.pathDG1+"   "+pp.pathDG2+"  count:"+(cnt+1));
				System.out.println("XML path: "+pp+"  count:"+(cnt+1));
				
			} else {
				totalPathsWithValues.put(pp, new Integer(1));
				//System.out.println("XML path: "+pp.pathDG1+ "   "+pp.pathDG2);
				System.out.println("XML path: "+pp);
				
			}

		}
	}
	
	public static void writeInFileTotalPathPairs(HashMap totalPathPairs,
			String resultsFolder, int numberOfInputValues,
			String function, String webSite) {

		FileWriter fstream;
		try {
			fstream = new FileWriter(resultsFolder + "/"
					+ numberOfInputValues + "_" + function + "_" + webSite
					+ "_" + "Pairs.txt");

			BufferedWriter out = new BufferedWriter(fstream);

			Iterator it = totalPathPairs.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs =  (Entry) it.next();
				XMLPathPair pp = (XMLPathPair) pairs.getKey();
				int cnt = (int) pairs.getValue();
				/*For old confidence!*/
				float posibility = (float) cnt / (float) NumberofInputvalues;
				/*Extra for new confidence!*/
			//	int NoOfSamplesWithValue = totalPathsWithValues.get(pp.pathDG2);
			//	float posibility = (float) cnt/ (float) NoOfSamplesWithValue;
				//System.out.println("path1: "+pp.pathDG1+"   path2: "+pp.pathDG2);
			//	System.out.println("Cnt: "+cnt+"   NoOfSamples:  "+NoOfSamplesWithValue+"  posibility: "+posibility);
				
				out.write(pp.pathDG1 + "\t\t" + pp.pathDG2 + "\t\t"
						+ posibility + "\n");
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		
		
        //    Constants.projectDirectory = args[0];
	
      //      Constants.yagoPath = args[1];
      
		HashMap<String, GraphGuide> graphGuides = new HashMap<String, GraphGuide>();

		final long startTime = System.currentTimeMillis();

		int functionsCounter = 0;
		try (BufferedReader br1 = new BufferedReader(new FileReader(
				Constants.fileWithFunctionsToCall))) {
//			String dirName = Constants.projectDirectory + "Results";
//			File dir = new File(dirName);
//			dir.mkdirs();

			String line;
			int tmp = 0;
			while ((line = br1.readLine()) != null) {
				if (tmp == 0) {
					tmp++;
					continue;
				}
				functionsCounter++;
				HashMap<XMLPathPair, Integer> totalPathPairs = new HashMap<XMLPathPair, Integer>();
				
				totalPathsWithValues = new HashMap<String, Integer>();
				

				String[] lineColumns = line.split(Constants.separatorSpace);

				String pathForFunctionResults = Constants.dirWithFunctions + lineColumns[0] + "/" + lineColumns[1]
						+ "/"; // To allaksa auto, ta apotelesmata einai ena
								// epipedo mesa, oxi 2
//				String pathForFunctionFolder = Constants.projectDirectory
//						+ lineColumns[0] + "/" + lineColumns[1] + "/";

				try (BufferedReader br = new BufferedReader(new FileReader(
						Constants.getFileWithInputsForType(lineColumns[2])))) {

					String sCurrentLine;
					while ((sCurrentLine = br.readLine()) != null) {

						NumberofInputvalues++;
						String[] columns = sCurrentLine
								.split(Constants.separatorForInputsFiles);
						/* GRAPH GUIDE */
						GraphGuide graphG = new GraphGuide();
						if (graphGuides.containsKey(columns[1])) {
							graphG = graphGuides.get(columns[1]);
						} else {
							graphG.parseInputsEntitiesFile(columns[1]);
							graphGuides.put(columns[1], graphG);
						}
						/* DATA GUIDE */
						DataGuide dataG = new DataGuide();
						
//						System.out.println("path:"+pathForFunctionResults
//								+ columns[0].replace(" ", "+"));
						
						dataG.makeparse(pathForFunctionResults
								+ columns[0].replace(" ", "+")+".xml");
						dataG.reInitMap(); //////////
						
	//					pathPairsSetWithValues = new HashSet<String>();
						
						OverlappingGuides ov = new OverlappingGuides(graphG,
								dataG, new GraphNode.NormalizeCompareMIX());

						addTotalPathPairs(totalPathPairs, ov.pathPairsSet);
		//				addTototalPathsWithValues();
					
						System.out.println("totalPathPairs: "+totalPathPairs.size());

						System.out.println("FINISH OVERLAPPING FOR "
								+ columns[0].replace(" ", "+")+".xml");

					} // while

				} catch (IOException e) {
					e.printStackTrace();
				}
				
				System.out.println("totalPathsWithValues\n\n");
				Iterator it = totalPathsWithValues.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs =  (Entry) it.next();
					String pp = (String) pairs.getKey();
					int emfaniseis = (int) pairs.getValue();
					System.out.println(pp+"  emfaniseis: "+emfaniseis);
				}
				

				writeInFileTotalPathPairs(totalPathPairs, Constants.sortedPairsDirectory,
						NumberofInputvalues, lineColumns[1], lineColumns[0]);
				// ov.printPathPairsCount();
				// ov.checkPriorityQueues();

				System.out
						.println("NumberOfInputvalues:" + NumberofInputvalues);

				System.out.println("Queries: " + QueryYAGOTDB.queryCNT);
				System.out.println("========================================");
				System.out.println("========================================");
				NumberofInputvalues = 0;
			}// while
			System.out.println("\n\nNumber of GraphGuides: "+graphGuides.size());
			System.out.println("\n\nNumber of Functions: " + functionsCounter);
			final long endTime = System.currentTimeMillis();
			System.out.println("\n\n\nTotal execution time: "
					+ ((endTime - startTime) / 1000) / 60);
		}// try
	}

}
