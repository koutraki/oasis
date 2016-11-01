package dependenciesIO.detectErrorMessage;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import customization.Constants;
import dependenciesIO.PreparePairInputs;
import dependenciesIO.Site;
import download.DownloadManager;
import download.WebFunction;

public class ComputeProfilesForDummyInputs {

	public static final String[] dummy_strings = {"hdqjd", "jdqsjhd", "dqsdsqdkj", "dskdjs", "dsdsz", "hlgkhmj", "qspop", "apkdijf", "cnjvdn", "apzdjkdnf"};

	public static final String[] dummy_mix = {"h.d3qjd", "jdq43s%jhd", "d2q*szdsqdkj", "ds7kdjs", "dsd12sz", "hlg22khmj", "qs23pop", "ap43kdijf", "cn854jvdn", "ap2zdjk2dnf"};

	public static final String[] dummy_numbers = {"7", "77", "7777", "1777777", "1111111111", "2345", "3456577987654334556", "35465578", "854", "9743"};

	
	/******************************************************************/
			/** compute profile **/
	/******************************************************************/
	public static final int getProfile(WebFunction f_to,String[] dummy_inputs, StringBuffer forInfo, String purpose){
		HashMap<String, Integer> map=new HashMap<String, Integer>(); 
		
		int count_errors=0;
		for(String input:dummy_inputs){
			String file=f_to.executeCall(input, true);
			if(file==null && f_to.lastDownloadErrorMessage!=null){
					count_errors++;
					System.out.println ("   ----> Error "+count_errors+" for"+f_to.functionName);
			}else if(file!=null){
				System.out.println ("   ----> Download file for "+f_to.functionName+" recorded errors:"+count_errors);
				
				XMLDocSignature doc=new XMLDocSignature();
				doc.makeparse(file);
				String signature=doc.getSignature();
				Integer count=map.get(signature);
				if(count==null) map.put(signature, new Integer(1));
				else map.put(signature, new Integer(count.intValue()+1));
				System.out.println("Signature **"+signature+"**");
			}
		}
		if(((float) count_errors)/dummy_inputs.length>Constants.thresholdForDummy)  return Constants.EXCEPTION;
		else {
			boolean isDocError=getSignatureMax(f_to.functionName, dummy_inputs.length, map, forInfo, purpose);
			if(isDocError) return Constants.DOC_ERROR;
		}
		return Constants.INDEF;
	}
	
	public static boolean getSignatureMax(String function_name, int total, HashMap<String, Integer> map, StringBuffer maxSignature, String purpose){
		int max_count=0;
		String frequentSignature=null;
		for(String signature:map.keySet()){
			int count=map.get(signature);
			if(count>max_count){
				frequentSignature=signature;
				max_count=count;
			}
		}
		if(max_count>0 && ((float) max_count)/total>Constants.thresholdForDummy) 
			{
				maxSignature.append(function_name+":\t"+frequentSignature+"\t"+purpose+"\t"+max_count+"\n");
				return true;
			}
		return false;
	}
	
	
	/******************************************************************/
		/** store/load profile  profile **/
	/******************************************************************/
	public static final String indef="indef";
	public static final String exception="exception";
	public static final String doc_error="doc_error";
	
	public static final String getMesssage(int code){
		switch (code){
				case Constants.INDEF: return indef;
				case Constants.EXCEPTION: return exception;
				case Constants.DOC_ERROR: return doc_error;
		}
		
		return indef;
	}
	
	public static final int getCodeForText(String text){
		if(text.equalsIgnoreCase(indef)) return Constants.INDEF;
		if(text.equalsIgnoreCase(exception)) return Constants.EXCEPTION;
		if(text.equalsIgnoreCase(doc_error)) return Constants.DOC_ERROR;
		return Constants.INDEF;
		
	}

	/** print the results in a file and a file 
	 * @throws IOException **/
	public final static void storesProfiles(Site site, Collection<WebFunction> functions, StringBuffer pourInfo) throws IOException{
		/** prepare the output file for dependencies **/
		FileWriter fOut = new FileWriter(Constants.profileDir+site.site+".txt");
		
		fOut.append("S:\t"+site.site+"\n");
		
		for(WebFunction f:functions){
			fOut.append("\t"+f.functionName+"\t\t"+getMesssage(f.profile_for_dummy_string)+"\t"+getMesssage(f.profile_for_dummy_integer)+"\t"+getMesssage(f.profile_for_dummy_mix)+"\n");
		}
		
		fOut.append("DEBUG:\n");
		
		for(WebFunction f:functions){
			fOut.append(pourInfo.toString());
		}
		
		fOut.close();
	}
	
	public final static void loadProfiles(Site site) throws IOException{
		boolean readSignatures=false;
		
		try (BufferedReader br = new BufferedReader(new FileReader(Constants.profileDir+site.site+".txt"))) {
			
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
	
				if (!sCurrentLine.isEmpty()) {
					sCurrentLine.trim();
					if (sCurrentLine.startsWith("S:"))
						{
							String siteName=sCurrentLine.split("S:")[1];
						}
					else if (sCurrentLine.startsWith("DEBUG:")){
						readSignatures=true;
					}
					else {
						if(readSignatures) { setSignature(site.site, sCurrentLine);}
						else setProfile(site.site, sCurrentLine);
					}
				} 
			}
		} catch (IOException e) { e.printStackTrace();}
	}
	
	public static final void setProfile(String siteName, String sCurrentLine){
		 String[] elems=sCurrentLine.split(Constants.separatorSpace);  
		    
		    int i=0;
		    if(elems[0]==null  ||  elems[0].length()==0) i=1;
		    String functionName=elems[i];
		    String profile_string=elems[i+1];
		    String profile_integer=elems[i+2];
		    String profile_mix=elems[i+3];
		    
		    WebFunction f=DownloadManager.getWebFunctionForSiteAndFunctionName(siteName, functionName);
		    if(f!=null){
		    				System.out.println(siteName+" "+functionName+" "+profile_string+"  "+profile_integer+" "+profile_mix);
		    				f.profile_for_dummy_string=getCodeForText(profile_string);
		    				f.profile_for_dummy_mix=getCodeForText(profile_mix);
		    				f.profile_for_dummy_integer=getCodeForText(profile_integer);
		    }	
	}
	
	public static final void setSignature(String siteName, String sCurrentLine){
		 String[] elems=sCurrentLine.split(Constants.separatorSpace);  
		    
		 int i=0;
		 if(elems[0]==null  ||  elems[0].length()==0) i=1;
		 String functionName=elems[i].substring(0,elems[i].length()-1);
		 
		 String signature=elems[i+1];
		 String profile_text=elems[i+2];
		
		 WebFunction f=DownloadManager.getWebFunctionForSiteAndFunctionName(siteName, functionName);
		 if(f==null) return;
		 
		 System.out.println(f.functionName+"  set signature: "+signature+" for "+profile_text);
		 if(profile_text.equals("dummy_mix")){ f.signatureForMix=signature; return;}
		 if(profile_text.equals("dummy_integer")){ f.signatureForInteger=signature; return;}
		 if(profile_text.equals("dummy_string")){ f.signatureForString=signature; return;}
		 
	}
	
	
	public static final void store(HashMap<String, Site> map)throws Exception{
		for(Site s: map.values()){
			StringBuffer pourInfo=new StringBuffer();
			for(WebFunction f: s.to){
				System.out.println("Test "+f.functionName+" of "+s.site);
				f.profile_for_dummy_mix = getProfile(f,dummy_mix,pourInfo, "dummy_mix");
				f.profile_for_dummy_integer = getProfile(f,dummy_numbers,pourInfo,"dummy_integer");
				f.profile_for_dummy_string = getProfile(f,dummy_strings,pourInfo, "dummy_string");
			}
			storesProfiles(s, s.to, pourInfo);
		}
	}
	
	public static final void load(HashMap<String, Site> map)throws Exception{
		for(Site s: map.values()){
			loadProfiles(s);
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
		
		load(map);
		
		
	}
}
