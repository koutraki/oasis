package dependenciesIO.outputPathWithInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

//import org.openjena.atlas.io.IO;

import customization.Constants;
import dataguide.DataGuide;
import dataguide.ValueNode;
import dataguide.XMLPathPair;
import dependenciesIO.DGWithAllTheValues;
import dependenciesIO.IOPair;
import dependenciesIO.LoadIODependencies;
import dependenciesIO.PreparePairInputs;
import dependenciesIO.Site;
import dependenciesIO.DGWithAllTheValues.PairValueListOfDocuments;
import dependenciesIO.joinInputInstanceWithOutput.LoadTreeClass;
import download.DownloadManager;
import download.WebFunction;

public class SolDetectOutputPathWithInput {
	
	/***********************************************/
			/** Dependency Module **/
	/**********************************************/
	public static final int maxRounds=2;
	public static final String fileIODependencies=Constants.projectDirectory+"ioDependencies.txt";
	
	/** step 2: to eliminate paths whose values take values in a limited set **/
	public static final int skip_path_with_cardinalities_lower_than=10; /** 10% of the 100 input samples that we have **/
	
	/** step 4: overlapping paths **/
	public static final float thresholdForOverlappingPaths=(float)0.5;
	
	/** step 5: to find the document encoding an error message **/
	public static final float percentangeOfBadResults=(float)0.2;
	public static final int minNoOfSamples=20;
	
	/*******************************************************/
	 /** read the inputs of every type  **/
	/*******************************************************/
	public static final HashMap<String, ArrayList<String>> readInputsForTypes(Collection<String> types) throws Exception{
		HashMap<String, ArrayList<String>> inputsForTypes=new HashMap<String, ArrayList<String>>();

		for(String type:types){
			ArrayList<String> inputs=DownloadManager.readInputsFromInputsAndEntitiesFiles(Constants.getFileWithInputsForType(type), 1);
			inputsForTypes.put(type, inputs);
		}
		return inputsForTypes;
	}

	
	/***********************************************************************************/
	/** Step 1: select input samples, execute calls round robin fashion to avoid suffocating the sites, compute dependencies **/
	/***********************************************************************************/
	public static final void selectInputSamples(Site site, int max_rounds, HashMap<String, ArrayList<String>> inputsForTypes) throws Exception {

		for( int i=0; i<site.from.size();i++){
			WebFunction f_from=site.from.get(i);
			
			DGWithAllTheValues paths=new DGWithAllTheValues();
			site.dataGuides.put(f_from, paths);
			
			 for(String input: inputsForTypes.get(f_from.type)){
				  	f_from.executeCall(Constants.transformStringForURL(input),true);
				    paths.makeparse(f_from.getPathResultForCall(input), input);  
				 }
			 
			 paths.initIterator();
			 int rounds=0;
			 while(paths.hasNext() && rounds<max_rounds){
				DGWithAllTheValues.PairValueListOfDocuments pathValueDocuments=paths.next();
				if(pathValueDocuments==null){
					/**System.out.println("A round is finished \n\n");**/
					rounds++;
					if(rounds>=max_rounds) break;
				}
				else {	
					String input=pathValueDocuments.value; 
					/**System.out.println("Test:"+pathValueDocuments.path+"  "+input);**/
					site.inputs.add(input);
				}
				
			 }
		}
	}
	
	public static final void executeCallsForSelectedInputs(Collection<Site> processedSites){
		boolean existsSiteWithCalls;
		do{
			existsSiteWithCalls=false;
			for(Site s:processedSites){
				if(s.inputs.size()==0) continue;
				
				/** execute call for the next function **/
				WebFunction f=s.to.get(s.indexNextToFunctionToCall);
				String input=s.inputs.get(0);
				/**System.out.println("Function "+f.site+" "+f.functionName+" process input: "+input);**/
				f.executeCall(Constants.transformStringForURL(input),true);
				
				/** advance the cursor to the next function and possibly to the next input as well **/
				s.indexNextToFunctionToCall=s.indexNextToFunctionToCall+1;
				if(s.indexNextToFunctionToCall>=s.to.size()){
					s.inputs.remove(0);
					s.indexNextToFunctionToCall=0;
					}
				
				existsSiteWithCalls=true;
			}
		}while(existsSiteWithCalls);	
	}
	
	/****************************************************************************/
	/** select more samples for the apparent valid paths  */
	/****************************************************************************/
	public static final void selectMoreSamples(Site site, int no_samples_per_path){
		HashSet<String> new_input_samples=new HashSet<String>();
		
		for(IOPair p:site.sortedFirstFrom){
			DGWithAllTheValues dg=site.dataGuides.get(p.f_from);
			for(String path:p.validPaths){
				Iterator<String> values=dg.getAllValuesUnderPath(path).iterator();
				int k=0;
				while(k<no_samples_per_path && values.hasNext()){
					String val=values.next();
					new_input_samples.add(val);
					k++;
				}
			}
		}
		site.inputs.clear();
		site.inputs.addAll(new_input_samples);
		site.indexNextToFunctionToCall=0;
		
		/**System.out.println("Site "+site.site+" I execute the next calls: "+site.inputs);**/
	}
	
	 
	public static void main(String[] args) throws Exception
    {  
    	  	DownloadManager.initStubs();
		
    	  	HashSet<String> types=PreparePairInputs.setInputTypes();
		
		DownloadManager.processFunctionCallsInBatch(DownloadManager.prepareCallsToExecuteInBatch(types));
		
		/** separate the function of each site into from and to */
		HashMap<String, Site> map=PreparePairInputs.getSitesToProcess(types);
		
		/** read the inputs for each type **/
		HashMap<String, ArrayList<String>> inputsForTypes=readInputsForTypes(types);
		
		
		for(Site site:map.values()){
			selectInputSamples(site, SolDetectOutputPathWithInput.maxRounds, inputsForTypes);
		}
		executeCallsForSelectedInputs(map.values());
		
		/** initialize the pairs of functions **/
		for(Site s:map.values()){
			s.initPairs(s.from, s.to);
		}
		
		/** STEP 1: initialize the valid paths and remove the paths with constants and the paths with small numerical values **/
		for(Site site:map.values()){
			IOManager.initValidPaths(site);
			IOManager.eliminatePathsWithConstants(site);
			IOManager.eliminatePathsWithSmallNumericValues(site);
		}
		
		/***************************************************/
		/** STEP 2: remove paths returning URL exceptions**/
		for(Site site:map.values()){
			IOManager.initializeDependenciesNew(site, SolDetectOutputPathWithInput.maxRounds);
		}
		
		/**for(Site site:map.values()){
			for(IOPair p: site.sortedFirstFrom){
				System.out.println(p);
			}
		}**/
		/*****************************************************/
		/** select more samples for the selected paths **/
		ArrayList<Site> sites_with_insufficient_samples=new ArrayList<Site>();
		for(Site site: map.values()){
			selectMoreSamples(site, 20);
			sites_with_insufficient_samples.add(site);
		}
		executeCallsForSelectedInputs(sites_with_insufficient_samples);
		
		/******************************************************/
		/** STEP 3: find the paths where the inputs are found in the output of the new function **/
		HashMap<String, String> debugMsgs=new HashMap<String, String>();
		for(Site site: map.values()){
			String msg=IOManager.findPathsWhereInputsOccur(site, 20);
			debugMsgs.put(site.site, msg);
			System.out.print(msg);
		}
		
		/** keep as valid path only the paths that occur concisely under the same path in the to-document**/
		for(Site site: map.values()){
			for(IOPair p:site.sortedFirstFrom){
				p.filterValidPathsBasedOnConcisenessInTheOutputOfTheToFunction();
			}
		}
		
		/******************************************************/
		/** STEP 4: spread the knowledge of the position of the input in f_to 
		 * & filter the pairs of functions with nothing in common **/
		for(Site site: map.values()){
			
			for(IOPair p:site.sortedFirstFrom){
				p.initValidOutputPathsForInputsInf_to(site.paths_with_input_in_f_to);
			}
			
			for(WebFunction f_to: site.paths_with_input_in_f_to.keySet()){
				System.out.println(f_to.functionName+": the place of the input is "+site.paths_with_input_in_f_to.get(f_to));
			}
		}
		
		/** identify the pairs of functions that have nothing in common **/
		for(Site site: map.values()){
			for(IOPair p:site.sortedFirstFrom){
				p.eliminateSterilePaths(site.paths_with_input_in_f_to.get(p.f_to));
			}
		}
		
		/********************************************************/
		/** STEP 5: find the other KB properties associated to the input that are present in first function */
		LoadTreeClass.load(map);
		
		/******************************************************/
		
		/** step 3: check overlapping **/
		/**for(Site site: map.values()){
			String msg=IOManager.checkOverlapping(site, 20);
			debugMsgs.put(site.site, msg);
			//System.out.print(msg);
		}**/
		
		/**for(Site site:map.values()){
			for(IOPair p: site.sortedFirstFrom){
				System.out.println(p);
			}
		}**/
	
		DownloadManager.storeCacheOnDisk();
		
		/** print results **/
		for(Site site:map.values()){
			//site.printDependenciesForSite(debugMsgs.get(site.site));
			LoadIODependencies.storeDependenciesForSite(site,null,Constants.dirWithDependenciesResult+site.site+"_"+Constants.step1+".txt");
		}
		
    }





}
