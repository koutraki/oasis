package dependenciesIO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import dependenciesIO.joinInputInstanceWithOutput.ClassEntityInXML;
import download.WebFunction;

public class IOPair implements IOPairAbstract {

	public final WebFunction f_from;
	public final WebFunction f_to;
	
	public final HashSet<String> validPaths=new  HashSet<String>();
	
	public final HashSet<String> pathsWithConstantValues=new HashSet<String>();
	
	public final HashSet<String> pathsWithSmallNumericValues=new HashSet<String>();
	
	public final HashSet<String> pathsWithURLExceptions=new HashSet<String>();
	
	
	public final HashSet<String> pathsWithLessPropertyOverlap=new HashSet<String>();
	
	public final HashSet<String> pathsWithLessOverlap=new HashSet<String>();
	
	
	
	/** the structures necessary to the identification of the input in the output of f_to **/
	public final HashSet<String> pathsSteriles=new HashSet<String>();
	public final HashSet<String> pathsWithInputsThosePossitionVariesInf_toResults=new HashSet<String>();
	public final HashMap<String, HashMap<String,InputPathStat>> pathsWithInputsInTo=new HashMap<String, HashMap<String, InputPathStat>>();
	
	/** the structures necessary to the recognition of the KB properties **/
	public HashMap<String, ClassEntityInXML> classEntities;
	public HashMap<String, String> validPathsAsRelativePaths=new HashMap<String,String>();
	
	public IOPair(WebFunction f_from, WebFunction f_to){
		this.f_from=f_from;
		this.f_to=f_to;
	}
	
	public String getResults(){
		StringBuffer buffer=new StringBuffer();

		buffer.append("D:"+f_from.functionName+"  ---> "+f_to.functionName+"\n");
		
		for(String path:validPaths){
			buffer.append("\t\t"+path+"\n");
		}
		
		buffer.append("\n");
		
		return buffer.toString();
	}
	
	public void filterValidPathsBasedOnConcisenessInTheOutputOfTheToFunction(){
		String function="D:"+f_from.functionName+"  ---> "+f_to.functionName;
		
		float max=0;
		HashSet<String> newValidPaths=new HashSet<String>();
		for(String path:validPaths){
			HashMap<String,InputPathStat> mapWithPathsInTo=pathsWithInputsInTo.get(path);
			if(mapWithPathsInTo==null) continue;
			
			float maxMax=0;
			for(String pInTo: mapWithPathsInTo.keySet() ){
			
				float ratio=((float)mapWithPathsInTo.get(pInTo).noFoundUnderThisPath)/mapWithPathsInTo.get(pInTo).trueCalls;
				if(ratio>maxMax) 
					maxMax=ratio;
			}
		    
			if(function.equals("D:getAuthorInfoById  ---> getAuthorInfoById")){
				System.out.println("For Path"+path+"Maxmax="+maxMax);
			}
			
			if(maxMax==max){
				newValidPaths.add(path);
			}
			else if(maxMax>max){
				newValidPaths.clear();
				newValidPaths.add(path);
				max=maxMax;
			}
			
		}
		if(function.equals("D:getAuthorInfoById  ---> getAuthorInfoById")){
			System.out.println("Max "+max+" and new valid"+newValidPaths);
		}
		if(max<1) return;
		for(String path:validPaths){
				if(!newValidPaths.contains(path))  
					pathsWithInputsThosePossitionVariesInf_toResults.add(path);
		}
		
		validPaths.clear();
		validPaths.addAll(newValidPaths);
		
	}
	
	public final void initValidOutputPathsForInputsInf_to(HashMap<WebFunction, HashSet<String>> maps){
		/** set for each function the set of paths where is input is to be found **/
		for(String path:validPaths){
			HashMap<String,InputPathStat> mapWithPathsInTo=pathsWithInputsInTo.get(path);
			for(String pInTo: mapWithPathsInTo.keySet() ){
				float ratio=((float)mapWithPathsInTo.get(pInTo).noFoundUnderThisPath)/mapWithPathsInTo.get(pInTo).trueCalls;
				if(ratio<1) continue;
				HashSet<String> pathsWithInput=maps.get(f_to);
				if(pathsWithInput==null){
					pathsWithInput=new HashSet<String>();
					maps.put(f_to, pathsWithInput);
				}
				pathsWithInput.add(pInTo);
			}
		}
	}
	
	public final void eliminateSterilePaths(HashSet<String> paths_f_to_with_input){
		if(paths_f_to_with_input==null) return;
		
		/** if none of the paths provided at least one input for the paths with inputs, then eliminate them from the valid paths **/
		ArrayList<String> newValid=new ArrayList<String>();
		
		for(String path:validPaths){
			boolean foundOneNonSterile=false;
			HashMap<String,InputPathStat> paths_f_to=pathsWithInputsInTo.get(path);
			
			for(String mathces_in_f_to:paths_f_to.keySet()){
				InputPathStat stat=paths_f_to.get(mathces_in_f_to);
				if(stat.noFoundUnderThisPath==0) continue;
				if(paths_f_to_with_input.contains(mathces_in_f_to)) foundOneNonSterile=true;
			}
			if(foundOneNonSterile) {
				newValid.add(path);
			}
			else pathsSteriles.add(path);
		}
		
		validPaths.clear();
		validPaths.addAll(newValid);
		
	}
	

	public String getPathsInTo(){
		StringBuffer buffer=new StringBuffer();

		buffer.append("D:"+f_from.functionName+"  ---> "+f_to.functionName+"\n");
		
		for(String path:pathsWithInputsInTo.keySet()){
			boolean foundOne=false;
			HashMap<String,InputPathStat> mapWithPathsInTo=pathsWithInputsInTo.get(path);
			for(String pInTo: mapWithPathsInTo.keySet() ){
				int found = mapWithPathsInTo.get(pInTo).noFoundUnderThisPath;
				int total=mapWithPathsInTo.get(pInTo).trueCalls;
				if(found!=0) {
					if(!foundOne){
						buffer.append("\t"+path+"\t {");
						foundOne=true;
					}
					buffer.append("\n\t\t"+pInTo+"="+found+((found==0)?"":"/"+total+mapWithPathsInTo.get(pInTo).values)+" ");			}
				}
			if(foundOne) buffer.append("}\n");
		}
		
		buffer.append("\n");
		
		return buffer.toString();
	}
	
	public String toString(){
		StringBuffer buffer=new StringBuffer();

		buffer.append(f_from.functionName+"  ---> "+f_to.functionName+"\n");
		
		buffer.append(" Valid Paths: \n");
		for(String path:validPaths){
			buffer.append("\t"+path+"\n");
		}
		
		buffer.append(" URL exceptions for : \n");
		for(String path:pathsWithURLExceptions){
			buffer.append("\t"+path+"\n");
		}
		
		buffer.append(" Paths with constant values : \n");
		for(String path:pathsWithConstantValues){
			buffer.append("\t"+path+"\n");
		}
		
		buffer.append(" Paths with values those average is less than 1000 : \n");
		for(String path:pathsWithSmallNumericValues){
			buffer.append("\t"+path+"\n");
		}
		
		
		/**buffer.append(" Less overlap for : \n");
		for(String path:pathsWithLessOverlap){
			buffer.append("\t"+path+"\n");
		}**/
		
		buffer.append(" Sterile paths (not found in the output even if the output contains the input : \n");
		for(String path:pathsSteriles){
			buffer.append("\t"+path+"\n");
		}
		
		buffer.append("\n\n\n");
		
		return buffer.toString();
		
	}
	
	public static final class ComparatorFromFirst  implements Comparator<IOPair> {
		@Override
		public int compare(IOPair o1, IOPair o2) {
			int c=o1.f_from.signature.compareTo(o2.f_from.signature);
			if(c!=0) return c;
			return o1.f_to.signature.compareTo(o2.f_to.signature);
		}
	}
	
	
	public static final class ComparatorToFirst  implements Comparator<IOPair> {
		@Override
		public int compare(IOPair o1, IOPair o2) {
			int c=o1.f_to.signature.compareTo(o2.f_to.signature);
			if(c!=0) return c;
			return o1.f_from.signature.compareTo(o2.f_from.signature);
		}
	}


	/*****************************/
	/** ClassEntityInXML Path With Input Statistics **/
	/*****************************/
	public static final class InputPathStat{
		//int validInputs=0;
		public int noFoundUnderThisPath=0;
		public int trueCalls=0;
		public HashSet<String> values=new HashSet<String>();
	}


	/******************************************************/
	/** Methods of the IOPath **/
	/******************************************************/
	@Override
	public WebFunction getSource() {
		return f_from;
	}

	@Override
	public WebFunction getDestination() {
		return f_to;
	}

	@Override
	public Collection<String> getValidPaths() {
		return validPaths;
	}

	@Override
	public String getDebugMessage() {
		return null;
	}
}
