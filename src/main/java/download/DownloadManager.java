package download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import customization.Constants;

public class DownloadManager {
	/** the stubs of the functions can be searched by their signatures */
	/** HashMap(site,HashMap(functionName, WebFunction object))**/
	public static final HashMap<String,HashMap<String, WebFunction>>  stubs = 
											new HashMap<String,HashMap<String, WebFunction>>();
	public static final HashMap<String, SiteMeta> siteTimming=new HashMap<String, SiteMeta>();
	
	public static final HashMap<String, ArrayList<String>> inputsForTypes=new  HashMap<String, ArrayList<String>>();
	
	/**************************************************************/
	/*** Keep the values of the working set of types in the memory **/
	/**************************************************************/
	public static final ArrayList<String> getValuesForType(String type) throws Exception{
		ArrayList<String> values=inputsForTypes.get(type);
		if(values!=null) return values;
		
		values=DownloadManager.readInputsFromInputsAndEntitiesFiles(Constants.getFileWithInputsForType(type), 1);
		inputsForTypes.put(type, values);
		
		return values;
	}

	
	/*************************************************************/
	/******** Stub management *****/
	/************************************************************/	
	public static final void initStubs() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(Constants.fileWithTheURLsOfTheFunctions));
		
		/** the first line contains the header, hence, ignore **/
		String line=br.readLine();
		if(line==null) {
			br.close();
			return;
		}
		
		while ((line = br.readLine()) != null) {
			line=line.trim();
 		    /**System.out.println("Line: "+line);**/
			String[] splits = line.split(Constants.separatorSpace);
			if(splits.length<3) {
				//System.out.println("Not enough elements for the function "+line);
			}
			else {
				String function=splits[0];
				String site=splits[1];
				/** check if the site is from the working set **/
				if(Constants.filterWorkingSet){
					boolean isFromWorkingSet=false;
					for(String s:Constants.workingSetWebSites){
						if(s.equalsIgnoreCase(site)) {
							isFromWorkingSet=true;
							break;
						}
					}
					/** the site is not from the working set **/
					if(!isFromWorkingSet) continue;
				}
				
				String prefix=splits[2];
				String suffix=(splits.length==4)?splits[3]:"";
				//System.out.println("I add to the working set: **"+function+"** site is **"+site+"** prefix is "+prefix+"; suffix is "+suffix);
				
				/** create the stub **/
				WebFunction stub=new WebFunction(function, site, prefix, suffix);
				SiteMeta timing=null;
				
				long timeLastCall=System.currentTimeMillis();
				
				/** add the stub to the corresponding site **/
				HashMap<String, WebFunction> clusterFunction=stubs.get(site);
				if(clusterFunction==null){
					clusterFunction= new HashMap<String, WebFunction>();
					stubs.put(site, clusterFunction);
					timing=new SiteMeta(site);
					siteTimming.put(site, timing);
					
				}else {
					timing=siteTimming.get(site);
				}
				clusterFunction.put(function, stub);
				stub.timingInfo=timing;
			}
		}
		br.close();
		
	}
	
	public static WebFunction getWebFunctionForSiteAndFunctionName(String site, String function){
		HashMap<String, WebFunction> siteHub=stubs.get(site);
		if(siteHub==null) {
			System.out.println("The site "+site+" is unkown ");
			return null;
		}
		return siteHub.get(function);
	}
	
	public static final void writeFunctionsAndTheirSitesToFile(String filePath) throws Exception{
		FileWriter fOut = new FileWriter(filePath);
		BufferedReader br = new BufferedReader(new FileReader(Constants.fileWithTheURLsOfTheFunctions));
		String newLine = System.getProperty("line.separator");
		
		String line;
		while ((line = br.readLine()) != null) {
			line=line.trim();
			String[] splits = line.split(Constants.separatorSpace);
			if(splits.length<2) continue;
			String function=splits[0];
			String site=splits[1];
		    fOut.write(function+" \t\t "+site+" "+newLine);
		}
		fOut.close();
		br.close();
	}
	
	/***********************************************************************/
			/*** Process Functions In Batch **/
	/***********************************************************************/
	public static final void processFunctionCallsInBatch(HashMap<String, HashMap<WebFunction, ArrayList<String>>> sitesWithFunctionsAndInputs){
		while (!sitesWithFunctionsAndInputs.isEmpty()){
			Iterator<HashMap<WebFunction, ArrayList<String>>> itFunctionsAndInputs=sitesWithFunctionsAndInputs.values().iterator();
			/** should have at least a value **/
			while(itFunctionsAndInputs.hasNext()){
				HashMap<WebFunction, ArrayList<String>> functions=itFunctionsAndInputs.next();
			
				if(functions.isEmpty()) itFunctionsAndInputs.remove();
				else {
						/** get the first function **/
						WebFunction f=functions.keySet().iterator().next();
						ArrayList<String> inputs=functions.get(f);
						if(inputs==null || inputs.size()==0) {
							functions.remove(f);
						}else {
							String in=inputs.remove(0);
							f.executeCall(in,true);
							/**System.out.println("Process "+f.site+" "+f.functionName+" "+in);**/
						}
				
				}
			
			}
		}
	}
	
	/** prepare calls to execute in batch **/
	public static  HashMap<String, HashMap<WebFunction, ArrayList<String>>> prepareCallsToExecuteInBatch(Collection<String> knownTypes) throws Exception{
		HashMap<String, HashMap<WebFunction, ArrayList<String>>> sitesWithFunctionsAndTheirInputs=new HashMap<String, HashMap<WebFunction, ArrayList<String>>>();
		
		BufferedReader br = new BufferedReader(new FileReader(Constants.fileWithTheInputTypesOfTheFunctions));
		String line;
		while ((line = br.readLine()) != null) {
			line=line.trim();
			String[] splits = line.split(Constants.separatorSpace);
			
			/** if the function has no input type defined, continue **/
			if(splits.length<3) continue;
			
			/** if the site is not among the sites of the working set, continue **/
			String site=splits[1];
			if(stubs.get(site)==null) continue;
			
			/** if the type is not among the analyzed types, continue **/
			String type=splits[2];
			if(knownTypes!=null && ! knownTypes.contains(type) ) continue;
			
			String file=Constants.getFileWithInputsForType(type);
			if(file==null) {
				/**System.out.println("File is  "+null);**/
				continue;
			}
			/**System.out.println("File is  "+file);**/
			File f= new File(file);
			if(!(f.exists() && f.isFile())) {
				/**System.out.println("Unknown type at line "+line);**/
				System.out.println("No file "+file+" with input values");
				continue;
			}
			
			/** get the stub of the function & compute samples using as input values from the file associated to the type **/
			String function=splits[0];
			WebFunction F=stubs.get(site).get(function);
			ArrayList<String> inputs=DownloadManager.readInputsFromInputsAndEntitiesFiles(file, 1);
			
			
			/** add to the prepared result **/
			HashMap<WebFunction, ArrayList<String>> functions=sitesWithFunctionsAndTheirInputs.get(site);
			if(functions==null){
				functions=new HashMap<WebFunction, ArrayList<String>>();
				sitesWithFunctionsAndTheirInputs.put(site, functions);
			}
			
			functions.put(F, inputs);
			
			
			/**System.out.println("Function "+function+": prepare inputs "+inputs);**/
			
			
		}
		br.close();
		return sitesWithFunctionsAndTheirInputs;
	}
	
	
	public static ArrayList<WebFunction> computeSamplesForFunctionsWithKnownTypes(ArrayList<String> knownTypes) throws Exception{
		ArrayList<WebFunction> listFunctionsWithSamples=new ArrayList<WebFunction>();
		
		BufferedReader br = new BufferedReader(new FileReader(Constants.fileWithTheInputTypesOfTheFunctions));
		String line;
		while ((line = br.readLine()) != null) {
			line=line.trim();
			String[] splits = line.split(Constants.separatorSpace);
			
			/** if the function has no input type defined, continue **/
			if(splits.length<3) continue;
			
			/** if the site is not among the sites of the working set, continue **/
			String site=splits[1];
			if(stubs.get(site)==null) continue;
			
			/** if the type is not among the analyzed types, continue **/
			String type=splits[2];
			if(knownTypes!=null && ! knownTypes.contains(type) ) continue;
			
			String file=Constants.getFileWithInputsForType(type);
			if(file==null) {
				System.out.println("File is  "+null);
				continue;
			}
			System.out.println("File is  "+file);
			File f= new File(file);
			if(!(f.exists() && f.isFile())) {
				System.out.println("Unknown type at line "+line);
				System.out.println("No file "+file+" with input values");
				continue;
			}
			
			/** get the stub of the function & compute samples using as input values from the file associated to the type **/
			String function=splits[0];
			WebFunction F=stubs.get(site).get(function);
			ArrayList<String> inputs=DownloadManager.readInputsFromInputsAndEntitiesFiles(file, 1);
			System.out.println("Function "+function+": I have to execute calls for "+inputs);
			if(F==null){System.out.println("Stub is null **"+site+"**"+function+"**");}
			
			for(String input: inputs) {
	 			F.executeCall(input,true);
	 		}
			
			listFunctionsWithSamples.add(F);
		}
		br.close();
		return listFunctionsWithSamples;
	}
	
	/**********************************/
	/** Store the cache on the disk **/
	/*********************************/
	public static final void storeCacheOnDisk() throws Exception{
		for(String site: stubs.keySet()){
			System.out.println("I store the cache for the site "+site);
			HashMap<String, WebFunction> clusterFunction=stubs.get(site);
				
			for(String function: clusterFunction.keySet()){
				 WebFunction download=clusterFunction.get(function);
				 download.storeCache();
			}
		}
	}
	
	
	
	
	public static void main(String[] args) throws Exception
	    {  
	    	  	/** DownloadManager.writeFunctionsAndTheirSitesToFile(Constants.fileWithTheInputTypesOfTheFunctions);**/
			DownloadManager.initStubs();
			
			ArrayList<String> types=new ArrayList<String>();
			types.add("singers");
				
			/**ArrayList<WebFunction> listF=DownloadManager.computeSamplesForFunctionsWithKnownTypes(null);
			System.out.println("Functions with known inputs:");
			for(WebFunction F: listF){
				System.out.print(F.functionName+"   ");	
			}
			System.out.println();**/
			
			
			DownloadManager.processFunctionCallsInBatch(DownloadManager.prepareCallsToExecuteInBatch(null));
			
			
			DownloadManager.storeCacheOnDisk();
	    }

	/*********************************/
	/** Read inputs from files **/
	/********************************/
	public static final ArrayList<String> readLineByLineTheInputs(String fileInputs) throws Exception{
		ArrayList<String> inputsList= new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(fileInputs));
		String line;
		while ((line = br.readLine()) != null) {
			String input=line.trim();
			inputsList.add(input);
		}
		br.close();
		return inputsList;
	}

	public static final ArrayList<String> readInputsFromInputsAndEntitiesFiles(String fileInputs, int column) throws Exception{
		ArrayList<String> inputsList= new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(fileInputs));
		String line;
		while ((line = br.readLine()) != null) {
			String input=line.trim();
			String[] splits = input.split(Constants.separatorForInputsFiles);
			if(splits.length>=column) inputsList.add(splits[column-1]);
		}
		br.close();
		return inputsList;
	}
	
	
}
