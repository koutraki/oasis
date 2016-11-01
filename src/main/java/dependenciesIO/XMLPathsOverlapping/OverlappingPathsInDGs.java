package dependenciesIO.XMLPathsOverlapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import dataguide.DataGuide;
import dataguide.ValueNode;
import dataguide.XMLPathPair;
import download.DownloadManager;
import download.WebFunction;



public class OverlappingPathsInDGs {
	DataGuide dg1=null;
	DataGuide dg2=null;
	
	/** several comparators can be used => Clement's methods, simple string comparator **/
	Comparator<ValueNode> c=null; 
	
	/** stores the results **/
	public HashMap<XMLPathPair, Integer> pathPairsCount;
	
	public OverlappingPathsInDGs(DataGuide dg1, DataGuide dg2, Comparator<ValueNode> c){
		this(dg1, dg2, c, new HashMap<XMLPathPair, Integer>());
	}
	
	public OverlappingPathsInDGs(DataGuide dg1, DataGuide dg2, Comparator<ValueNode> c, HashMap<XMLPathPair, Integer> pathPairsCount) {
		this.dg1=dg1;
		this.dg2=dg2;
		this.pathPairsCount=pathPairsCount;
		this.c=c;
		
		/** get the pairs of paths whose values overlap 
		 * the result is to be found in the HashMap above
		 */
		getOverlappingPaths();
	}
	
	  public void getOverlappingPaths(){
		/** form two lists, with the values from the two documents **/
		PriorityQueue<ValueNode> pq1=dg1.getValuesInAPriorityQueue(c);
		PriorityQueue<ValueNode> pq2=dg2.getValuesInAPriorityQueue(c);
			
		HashSet<XMLPathPair> processedPaths= new HashSet<XMLPathPair>();
		  
		  while(!pq1.isEmpty() && !pq2.isEmpty()){
			  ValueNode peek1=pq1.peek();
			  ValueNode peek2=pq2.peek();
			  int comp=c.compare(peek1, peek2);
			  if(comp<0){
				  pq1.poll();
				  //System.out.println("Value q1: "+peek1.value);
			  }
			  else if(comp>0){
				  pq2.poll();
				  //System.out.println("Value q2: "+peek2.value); 
			  }else {
				  /** since there might be several nodes with the same value, we treat the problem for the general case **/
				  //System.out.println("Overlapping detected:  value="+peek1.value);
				  ArrayList<ValueNode> list1=new ArrayList<>();
				  list1.add(pq1.poll());
				  while(!pq1.isEmpty() && c.compare(pq1.peek(), list1.get(0))==0){
					  list1.add(pq1.poll());
				  }
				  
				  ArrayList<ValueNode> list2=new ArrayList<>();
				  list2.add(pq2.poll());
				  while(!pq2.isEmpty() && c.compare(pq2.peek(), list2.get(0))==0){
					  list2.add(pq2.poll());
				  }
				  
				  //System.out.println("\t occurs in DG1 "+list1.size()+" times and occurs in DG2 "+list2.size()+" times\n");
				  processPathPairs(list1, list2, pathPairsCount, processedPaths); 
			  }
		  }
		  /** we are only interested in the overlapping values, so the remaining values are useless **/
	  }
	
	  public void processPathPairs(ArrayList<ValueNode> list1, ArrayList<ValueNode> list2, HashMap<XMLPathPair, Integer> pathPairsCount,HashSet<XMLPathPair> processedPaths){
		  for(ValueNode n1: list1)
			  for(ValueNode n2: list2){
				  String p1=n1.parent.getStringPathRootToNode();
				  String p2=n2.parent.getStringPathRootToNode();
				  XMLPathPair pair= new XMLPathPair(p1,p2);
				  if(processedPaths.contains(pair)) return;
				  else processedPaths.add(pair);
				  
				  if(!pathPairsCount.containsKey(pair)){
					  pathPairsCount.put(pair, new Integer(1));
				  }
				  else {
					  	int count=pathPairsCount.get(pair);
					  	pathPairsCount.put(pair, new Integer(count+1));
				  }
				  		
			  }
	  }
	  
	  public static final void showPathPairCount(HashMap<XMLPathPair, Integer>  pathPairsCount){
		   if(pathPairsCount==null) return;
		   PriorityQueue<XMLPathPair> orderedPaths= new PriorityQueue<XMLPathPair>(pathPairsCount.keySet());
			  while(!orderedPaths.isEmpty()){
				  XMLPathPair p=orderedPaths.poll();
				  System.out.println("                "+p.pathDG1+"\t"+p.pathDG2+"\t"+pathPairsCount.get(p));
			  }
	   }
		
	  
	  public static void main(String[] args) throws Exception
	   {  
		  /**DownloadManager.initStubs(); 
		  WebFunction F1= DownloadManager.getWebFunctionForSiteAndFunctionName("music_brainz", "getArtistInfoByName");
		  WebFunction F2= DownloadManager.getWebFunctionForSiteAndFunctionName("music_brainz", "getReleasesByArtistId");
		 **/
		  HashMap<XMLPathPair, Integer> pathPairsCount= new HashMap<XMLPathPair, Integer>();

		  DataGuide dg1=new DataGuide();
		  dg1.makeparse("/Users/adi/Dropbox/OASIS/Nico-Data/functions/echonest/getArtistInfoByName/Christina+Aguilera.xml");
		  dg1.reInitMap();
		  
		  DataGuide dg2=new DataGuide();
		  dg2.makeparse("/Users/adi/Dropbox/OASIS/Nico-Data/functions/echonest/getArtistInfoById/AR0S7TA1187FB4D024.xml");
		  dg2.reInitMap();
		  
			
		  OverlappingPathsInDGs overlap=new OverlappingPathsInDGs(dg1, dg2, new ValueNode.SimpleCompare(), pathPairsCount);
		 
		  System.out.println("Overlapping paths :");
			 
		  showPathPairCount(pathPairsCount); 
		  
		  
	   }
		
	
}
