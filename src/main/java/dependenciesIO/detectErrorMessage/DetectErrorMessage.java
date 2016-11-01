package dependenciesIO.detectErrorMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import customization.Constants;
import dataguide.DocNumbersFree;
import dependenciesIO.DGWithAllTheValues;
import dependenciesIO.outputPathWithInput.SolDetectOutputPathWithInput;
import download.WebFunction;

public class DetectErrorMessage {

	/**************************************************************************************************/
	 /*** Step 5: for every function in "to" compute the invalid document  **/
	/**************************************************************************************************/
	/** this function looks only in the cache **/
	public static final String detectInValidDocuments(WebFunction f, HashSet<String> sitesWithNoManySamples) {
		String dir=Constants.getDirectoryForFunction(f.functionName, f.site);
		File dirFile=new File(dir);
		File[] files=dirFile.listFiles();
		/**System.out.println("Detect invalid document for "+f.functionName+" "+f.site+", calls  "+files.length);**/
		if(files.length<SolDetectOutputPathWithInput.minNoOfSamples){
			/**System.err.println("Not enough samples for site  "+f.site+" and function "+f.functionName);**/
			if(sitesWithNoManySamples!=null) sitesWithNoManySamples.add(f.site);
			return null;
		}
		
		/** files are ordered in a tree in the ascending order of their lenght **/
		TreeMap<Long, ArrayList<File>> sortedInputs=new TreeMap<Long, ArrayList<File>>();
		for(int i=0;i<files.length;i++){
			File file=files[i];
			long lenght=new Long(file.length());
			
			ArrayList<File> list=sortedInputs.get(lenght);
			if(list==null){
				list=new ArrayList<File>();
				sortedInputs.put(lenght, list);
			}
			list.add(file);
		}
		
		DocNumbersFree doc=new DocNumbersFree();
		HashMap<String, Integer> sameAnswerCount=new HashMap<String, Integer>();
		int countSamples=0;
		for(Map.Entry<Long, ArrayList<File>> si: sortedInputs.entrySet()){
				for(File filePath:si.getValue()){
						if(filePath.length()>=1024) continue; 
						doc.reInit(Constants.revertTransformation(filePath.getName()));
						doc.makeparse(filePath.getAbsolutePath());
						String valueDoc=doc.toString();
						Integer count=sameAnswerCount.get(valueDoc);
						if(count==null) sameAnswerCount.put(valueDoc, new Integer(1));
						else sameAnswerCount.put(valueDoc, new Integer(1+count));	
						countSamples++;
				}
		}
		
		Integer max=0;
		String frequentText="";
		for(String text:sameAnswerCount.keySet()){
			Integer count=sameAnswerCount.get(text);
			if(count>max){
				max=count;
				frequentText=text;
			}
		}
		
		System.out.println(f.site+" "+f.functionName+" files="+files.length+" occurrencesOfTheFrequent="+max+" **"+frequentText+"**");
		if(max>=SolDetectOutputPathWithInput.percentangeOfBadResults*countSamples && frequentText.length()>0) return frequentText;
		return null;	
	}

	/** for every function, eliminate the paths with invalid calls **/
	public static final HashMap<WebFunction,HashSet<String>> eliminatePathsWithInvalidCalls(WebFunction f_to, String invalidMessage, HashMap<WebFunction,HashSet<String>> dependencies,HashMap<WebFunction,DGWithAllTheValues> dataguides){
		HashMap<WebFunction,HashSet<String>> pathsWithInvalidAnswers=new HashMap<WebFunction,HashSet<String>>();
		DocNumbersFree doc=new DocNumbersFree();
		
		for(WebFunction f_from:dependencies.keySet()){
			pathsWithInvalidAnswers.put(f_from, new HashSet<String>());
			DGWithAllTheValues dg=dataguides.get(f_from);
			Iterator<String> itPaths=dependencies.get(f_from).iterator();
			while(itPaths.hasNext()){
				String path=itPaths.next();
				Collection<String> values=dg.getAllValuesUnderPath(path);
				boolean found=false;
				int occurencesInvalid=0;
				for(String input:values){
					String filePath=f_to.getFileFromHistory(input);
					if(filePath==null) continue;
					if(new File(filePath).length()>1024) continue;
					doc.reInit(Constants.revertTransformation(new File(filePath).getName()));
					doc.makeparse(filePath);
					String valueDoc=doc.toString();
					
					if(valueDoc.equalsIgnoreCase(invalidMessage)){
						occurencesInvalid++;
						if(occurencesInvalid>=3){
								/**System.out.println("Detect document with error message: "+valueDoc);**/
								/**System.out.println("I eliminate path "+path+" from "+f_from.functionName);**/
								pathsWithInvalidAnswers.get(f_from).add(path);
								found=true;
								break;
						}
					}
					
				}
				if(found) itPaths.remove();
			}
		}
		
		/**for(HashSet<String> paths:pathsWithInvalidAnswers.values()){
			for(String path:paths){
				System.out.println("***** I record to eliminate "+path);
			}
		}**/
		
		return pathsWithInvalidAnswers;
	}

}
