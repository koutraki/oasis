package dependenciesIO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import customization.Constants;
import dependenciesIO.detectErrorMessage.XMLDocSignature;
import download.WebFunction;

public class IOPairWithValues implements IOPairAbstract{
 
	public WebFunction f_from;
	public WebFunction f_to;
	
	HashMap<String, ArrayList<String>> pathsWithValues;
	
	public IOPairWithValues(HashMap<String, ArrayList<String>> pathsWithValues,  WebFunction f_from){
		this.pathsWithValues=pathsWithValues;
		this.f_from=f_from;
	}
	
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
		return pathsWithValues.keySet();
	}

	@Override
	public String getDebugMessage() {
		return "No message";
	}

	public IOPairWithValues clone(){
		HashMap<String, ArrayList<String>> newPathsWithValues=new HashMap<String,ArrayList<String>>();
		for(String key:pathsWithValues.keySet()){
			newPathsWithValues.put(key, pathsWithValues.get(key));
		}
		return new IOPairWithValues(newPathsWithValues, f_from);
	}
	
	
	@Override
	public String getResults() {
		StringBuffer buffer=new StringBuffer();

		buffer.append("D:"+f_from.functionName+"  ---> "+f_to.functionName+"\n");
		
		for(String path:pathsWithValues.keySet()){
			buffer.append("\t\t"+path+"\n");
		}
		
		buffer.append("\n");
		
		return buffer.toString();
	}
	
	public String toString(){
		StringBuffer buffer=new StringBuffer();

		buffer.append("D:"+f_from.functionName+"  ---> "+f_to.functionName+"\n");
		
		for(String path:pathsWithValues.keySet()){
			buffer.append("\t\t"+path+":\t"+pathsWithValues.get(path)+"\n");
		}
		
		buffer.append("\n");
		
		return buffer.toString();
	}

	
	/*******************************************/
	/** eliminate paths with less then X values **/
	/*******************************************/
	public void eliminateLessThan(int x){
		Iterator<String> it=pathsWithValues.keySet().iterator();
		while(it.hasNext()){
			String path=it.next();
			if(pathsWithValues.get(path).size()<x) it.remove();
		}
	}
	
	public void eliminatePathsWithURLs(){
		Iterator<String> it=pathsWithValues.keySet().iterator();
		while(it.hasNext()){
			String path=it.next();
			ArrayList<String> inputs=pathsWithValues.get(path);
			
			int count_http=0;
			boolean areHttp=false;
			for(String input:inputs){
				if(input.startsWith("http:/")) count_http++;
				else if(input.startsWith("https:/")) count_http++;
				
				if(((float)count_http)/inputs.size()>Constants.thresholdForDummy) {areHttp=true; break;}
			}
			if(areHttp) it.remove();
		}
	}
	
	/*******************************************/
	/** eliminate paths with errors**/
	/*******************************************/
	public void eliminatePathsWithErrors(int maxCalls){
		System.out.println("Test:"+f_from.functionName+"  ---> "+f_to.functionName+"\n");
		System.out.println("Signature mix:"+f_to.signatureForMix);
		System.out.println("Signature integer:"+f_to.signatureForMix);
		System.out.println("Old Paths:"+toString());
	
		
		Iterator<String> it=pathsWithValues.keySet().iterator();
		while(it.hasNext()){
			String path=it.next();
			System.out.println("    Path "+path);
			ArrayList<String> inputs=pathsWithValues.get(path);
			
			/** get the type **/
			int count_numbers=0;
			for(String input:inputs){
				if(input.matches("\\d+")) count_numbers++;
			}
		
			boolean type_Number=false;
			if(inputs.size()==count_numbers) type_Number=true;
		
			/** get the numbers of errors **/
			int count_exceptions=0;
			int count_doc_errors=0;
			
			boolean toRemove=false;
			int executed=0;
			for(String input:inputs){
				String file=f_to.executeCall(input, true);
				executed++;
				if(executed>maxCalls) break;
				
				if(f_to.lastDownloadErrorMessage!=null){
					count_exceptions++;
					if(executed<=4 && count_exceptions>=2){ toRemove=true; break;}
				}
				
				if(file!=null){
					XMLDocSignature signature=new XMLDocSignature();
					signature.makeparse(file);
					System.out.println("Signature: "+signature.getSignature());
					if(type_Number && !Constants.testOnlyMixt) 
							{if(signature.getSignature().equals(f_to.signatureForInteger)) count_doc_errors++;}
					else 	{if(signature.getSignature().equals(f_to.signatureForMix)) count_doc_errors++;}
				}
				

				if(((float)count_doc_errors)/maxCalls>Constants.thresholdForDummy) { toRemove=true; break;}	
				if(((float)count_exceptions)/maxCalls>Constants.thresholdForDummy) { toRemove=true; break;}	
			}
			System.out.println("To remove"+toRemove);
			if(toRemove) it.remove();
		}
		System.out.println("New paths Paths:"+toString());
	}
	
	
}
