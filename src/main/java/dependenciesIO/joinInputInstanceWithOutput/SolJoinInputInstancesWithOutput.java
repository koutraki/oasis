package dependenciesIO.joinInputInstanceWithOutput;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import customization.Constants;
import dataguide.DataGuide;
import dataguide.ValueNode;
import dependenciesIO.IOPair;
import dependenciesIO.LoadIODependencies;
import dependenciesIO.PreparePairInputs;
import dependenciesIO.Site;
import dependenciesIO.IOPair.InputPathStat;
import download.DownloadManager;
import download.WebFunction;


public class SolJoinInputInstancesWithOutput {
	
	/**************************************************************************/
						/*** compare instances to call results  **/
	/**************************************************************************/
	public static final void compareInstanceWithCallResults(Site site) throws Exception {
		for(IOPair p:site.sortedFirstFrom){
			
			//if(!"getAlbumsByArtistMBID".equalsIgnoreCase(p.f_from.functionName)) continue;
			
			/** test only the functions with more remaining paths to test  **/
			if(p.validPaths.size()<=1 && Constants.testOnlyPairsWithSeveralValidPaths) continue;
			
			System.out.println("\n   "+p.f_from.functionName+"  ---> "+p.f_to.functionName);
			
			int max_matched_properties=0;
			ArrayList<String> newValidPaths=new ArrayList<String>();
			for(String validPath:p.validPaths){
				System.out.println("     "+validPath+" ---> "+p.validPathsAsRelativePaths.get(validPath));
				//System.out.println("         class: "+p.classEntities.get(validPath));
				ClassEntityInXML entity=p.classEntities.get(validPath);
				if(entity==null) continue;
				HashMap<String, ClassEntityInXML.Instance> instances=entity.instances;
				
				int calls2=0;
				HashMap<String, String> inputs2=new HashMap<String,String>();
				/** the alignments **/
				HashMap<String, HashMap<String, Integer>> alignments=new HashMap<String, HashMap<String, Integer>>();
				for(String input1:instances.keySet()){
					ClassEntityInXML.Instance instance=instances.get(input1);
					
					//System.out.println("    call="+input1+"  ---> call "+instances.get(input1).values.get(p.validPathsAsRelativePaths.get(validPath))+"  ");
					ArrayList<String> listInputs2=instances.get(input1).values.get(p.validPathsAsRelativePaths.get(validPath));
					if(listInputs2==null || listInputs2.size()==0) continue;
					String input2=listInputs2.get(0);
					if(input2==null) continue;
							
				    inputs2.put(input1, input2);
					
					boolean success=getPathMatches(p.f_to,input2, p.classEntities.get(validPath), p.validPathsAsRelativePaths.get(validPath), instance, alignments);
					if(success) calls2++;
				}
				printAlignments(alignments);
				System.out.println("Number of calls "+calls2);
				if(calls2<Constants.minSamplesForIO) continue;
				
				int no_matched_prop= countMatchesWithMoreThanThresholdOccurences(calls2, alignments);
				if(no_matched_prop==max_matched_properties){
					newValidPaths.add(validPath);
				}
				else if(no_matched_prop>max_matched_properties){
					max_matched_properties=no_matched_prop;
					newValidPaths.clear();
					newValidPaths.add(validPath);
				}
			}
		
			if(max_matched_properties>0){
				Iterator<String> it=p.validPaths.iterator();
				while(it.hasNext()){
					String path=it.next();
					if(!newValidPaths.contains(path)){
						p.pathsWithLessPropertyOverlap.add(path);
						it.remove();
				}
				/** print the new valid paths **/
				
			 }
				System.out.println("New Valid Paths: "+p.validPaths+"\n\n\n");
			}
			
			
		}
	}
	
	
	public static final boolean getPathMatches(WebFunction f_to, String input2, ClassEntityInXML entity, String relPathInput, ClassEntityInXML.Instance instance,HashMap<String, HashMap<String, Integer>> alignments){
		//System.out.println("        Execute call "+input2);
		String file=f_to.executeCall(input2, true);
		if(file==null) return false;
		
		DataGuide dg2=new DataGuide();
		dg2.makeparse(file);
		dg2.reInitMap();
		
	
		ValueNode.SimpleCompare c=new ValueNode.SimpleCompare();
		PriorityQueue<ValueNode> pq2=dg2.getValuesInAPriorityQueue(c);
		
		while(!pq2.isEmpty()){
			ValueNode valueNode=pq2.poll();
			for(String pathKB:entity.pathsFromKB.keySet()){
				ArrayList<String> values=instance.values.get(pathKB);
				if(values==null) continue;
				String value=values.get(0);
				
				if(valueNode.value.equalsIgnoreCase(value)){
								String pathFromRoot=valueNode.parent.getStringPathRootToNode();
								//System.out.println(" Found **"+valueNode.value+"**  under  "+pathFromRoot);
								//System.out.println(" Match "+pathKB+" & "+pathFromRoot);
								incrementCouterMatch(pathKB, pathFromRoot, alignments);
				}
		    }}
		
		return true;
										  
		}
	

	public static final void incrementCouterMatch(String pathKB, String pathFromRoot,HashMap<String, HashMap<String, Integer>> alignments){
		HashMap<String, Integer> matches=alignments.get(pathKB);
		if(matches==null) {
			matches=new HashMap<String,Integer>();
			alignments.put(pathKB, matches);
		}
		Integer countMatches=matches.get(pathFromRoot);
		if(countMatches==null)
				 matches.put(pathFromRoot, new Integer(1));
			else matches.put(pathFromRoot, new Integer(countMatches+1));
	}
	
	public static final int countMatchesWithMoreThanThresholdOccurences(int calls, HashMap<String, HashMap<String, Integer>> alignments){
		int no_paths=0;
		for(String property:alignments.keySet()){
			HashMap<String, Integer> pathsXML=alignments.get(property);
			int max=0;
			String pathXMLWithMaxMatches=null;
			for(String xmlPath:pathsXML.keySet()){
				if(pathsXML.get(xmlPath)>max) {
					max=pathsXML.get(xmlPath);
					pathXMLWithMaxMatches=xmlPath;
				}
			}
			System.out.println(" "+property+" --> "+pathXMLWithMaxMatches+"   "+max);
			if(((float)max)/calls>=Constants.thresholdForTheConfidenceOfTheRootToLeafPathAlignement){
				no_paths++;
			}
		}
		return no_paths;
	}
	
	public static final void printAlignments(HashMap<String, HashMap<String, Integer>> alignments){
		for(String property:alignments.keySet()){
			HashMap<String, Integer> pathsXML=alignments.get(property);
			for(String xmlPath:pathsXML.keySet()){
				System.out.println(" "+property+" --> "+xmlPath+"   "+pathsXML.get(xmlPath));
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{  
		 DownloadManager.initStubs();
		
		/** functions accepting these inputs are considered as being known **/
		HashSet<String> types=new HashSet<String>();
		types.add("singers");types.add("songs");types.add("albums");
		types.add("actors");types.add("books");types.add("writers");
		
		types.add("singers_id_deezer");types.add("singers_id_discogs");types.add("singers_id_echonest");
		types.add("singers_id_last_fm");types.add("singers_id_music_brainz");types.add("singers_id_musixmatch");
		types.add("singers_mbid_musixmatch");
		
		types.add("actors_id_themoviedb");
	
		types.add("songs_id_echonest");types.add("songs_id_last_fm");	
		types.add("songs_id_music_brainz");types.add("songs_id_musixmatch");
		
		types.add("writers_id_isbndb");types.add("writers_id_library_thing");
		
		types.add("albums_id_deezer");types.add("albums_id_discogs");types.add("albums_id_last_fm");
		types.add("albums_id_music_brainz");types.add("albums_id_musixmatch");
		
		DownloadManager.processFunctionCallsInBatch(DownloadManager.prepareCallsToExecuteInBatch(types));
		
		/** separate the function of each site into from and to */
		HashMap<String, Site> map=PreparePairInputs.getSitesToProcess(types);
		
		LoadIODependencies.loadIODependencies(Constants.step1,map);
		LoadTreeClass.load(map);
		
		LoadInstancesFromXMLs.initXMLExtractor();
	
		for(Site site:map.values()){
			System.out.println("-------------------------------");
			System.out.println("Site "+site.site);
			LoadInstancesFromXMLs.loadTheClassEntities(site);
			LoadInstancesFromXMLs.loadInstancesForSourceFunctions(site,100);
			compareInstanceWithCallResults(site);
			LoadIODependencies.storeDependenciesForSite(site,null,Constants.dirWithDependenciesResult+site.site+".txt");
			
		}
	}
	
	
}
