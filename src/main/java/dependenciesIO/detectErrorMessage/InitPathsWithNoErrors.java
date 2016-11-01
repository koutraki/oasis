package dependenciesIO.detectErrorMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import customization.Constants;
import dataguide.DataGuide;
import dataguide.Node;
import dataguide.ValueNode;
import dependenciesIO.IOPair;
import dependenciesIO.IOPairWithValues;
import dependenciesIO.LoadIODependencies;
import dependenciesIO.PreparePairInputs;
import dependenciesIO.Site;
import download.DownloadManager;
import download.WebFunction;

public class InitPathsWithNoErrors {
	
	
	public static final ArrayList<IOPairWithValues> initIOPathPairs(Site site, int maxSamples) throws Exception {
		ArrayList<IOPairWithValues> pairs= new ArrayList<IOPairWithValues>();
		
		for(WebFunction f_from:site.from){
			if(site.to.size()==0) continue;
			
			/** load inputs for f_from **/
			System.out.println("   "+f_from.functionName+": "+f_from.type+"="+DownloadManager.getValuesForType(f_from.type));
			HashMap<String, ArrayList<String>> pathWithValues=initCandidates(f_from, maxSamples);
			
			IOPairWithValues pair=new IOPairWithValues(pathWithValues, f_from); 
			pairs.add(pair);
			pair.f_to=site.to.get(0);
			
			for(int i=1;i<site.to.size();i++){
				IOPairWithValues clone=pair.clone();
				clone.f_to=site.to.get(i);
				pairs.add(clone);
			}
		}
		
		return pairs;
	}
	
	public static final  HashMap<String, ArrayList<String>> initCandidates(WebFunction f_from, int maxSamples) throws Exception{
		/** for every input value, issue the calls and load the resulted values  **/
		int count=0;
		HashMap<String, ArrayList<String>> pathWithValues=new HashMap<String, ArrayList<String>> ();
		for(String input:DownloadManager.getValuesForType(f_from.type)){
			if(count>maxSamples) break;
			count++;
			
			String file=f_from.executeCall(input, true);
			if(file!=null){
				//System.out.println ("   Extract instances from call to "+f_from.functionName+" with known input: "+input);
				DataGuide dg=new DataGuide();
				dg.makeparse(file);
				extractFirstValueForEveryPath(dg, pathWithValues);
			}
		}
		return pathWithValues;
	}
	
	
	
	public static final void extractFirstValueForEveryPath(DataGuide dg, HashMap<String, ArrayList<String>> pathWithValues){
		for(Node n:dg.nodesWithTextValues){
			ArrayList<ValueNode> values=n.getValues();
			if(values.size()==0) continue;
			String firstValue=values.get(0).value.trim();
			String path=n.getStringPathRootToNode();
			ArrayList<String> list = pathWithValues.get(path);
			if(list==null){
				list=new ArrayList<String>();
				pathWithValues.put(path, list);
			}
			if(!list.contains(firstValue)) list.add(firstValue);
		}
	}
	
	public static void main(String[] args) throws Exception
	{  
		 DownloadManager.initStubs();
		
		/** functions accepting these inputs are considered as being known **/
		HashSet<String> types=PreparePairInputs.setInputTypes();
		
		DownloadManager.processFunctionCallsInBatch(DownloadManager.prepareCallsToExecuteInBatch(types));
		
		/** separate the function of each site into from and to */
		HashMap<String, Site> map=PreparePairInputs.getSitesToProcess(types);
		
		ComputeProfilesForDummyInputs.load(map);
		
				
		/** 0) init **/
		HashMap<String, ArrayList<IOPairWithValues>> mapPairs=new HashMap<String, ArrayList<IOPairWithValues>>();
		for(Site site:map.values()){
			ArrayList<IOPairWithValues> pairs=initIOPathPairs(site, 100);
			mapPairs.put(site.site, pairs);
			
			String file=Constants.dirWithDependenciesResult+site.site+"_0_init.txt";
			LoadIODependencies.storeDependenciesForSite(pairs, file);
		}
		
		/** 1) remove paths with less then 10 elements **/
		for(Site site:map.values()){
			ArrayList<IOPairWithValues> pairs=mapPairs.get(site.site);
			for(IOPairWithValues p:pairs){
				p.eliminateLessThan(5);
				p.eliminatePathsWithURLs();
				System.out.println(p);
			}
			String file=Constants.dirWithDependenciesResult+site.site+"_1_init.txt";
			LoadIODependencies.storeDependenciesForSite(pairs, file);
		}
		
		/** 2) remove paths with errors **/
		for(Site site:map.values()){
			ArrayList<IOPairWithValues> pairs=mapPairs.get(site.site);
			for(IOPairWithValues p:pairs){
				p.eliminatePathsWithErrors(10);
				System.out.println("The pair again "+p);
			}
			String file=Constants.dirWithDependenciesResult+site.site+"_2_errors.txt";
			System.out.println("Final result: "+file);
			LoadIODependencies.storeDependenciesForSite(pairs, file);
			
		}
		
		
	}
	
	
}
