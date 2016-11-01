package dependenciesIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import customization.Constants;
import download.DownloadManager;
import download.WebFunction;


public class LoadIODependencies {
	
	/*******************************************/
	
	/** print the results in a file and a directory  **/
	public final static void storeDependenciesForSite(ArrayList<IOPairWithValues> pairs, String file) throws IOException{
		/** prepare the output file for dependencies **/
		FileWriter fOut = new FileWriter(file);
		for(IOPairAbstract p:pairs){
			fOut.append(p.getResults());
		}
		
		fOut.append("DEBUG: \n");
		for(IOPairAbstract p:pairs){
			fOut.append(p.getDebugMessage());
		}
		fOut.close();
	}
	
	
	/*******************************************/
	
	

	public static final void loadIODependencies(String suffix,HashMap<String, Site> sites){
		
		/** for each file with class and relation alignments **/
		File folder = new File(Constants.dirWithDependenciesResult);
		File[] listOfFiles = folder.listFiles();
	
		for (File file : listOfFiles) {
			if (file.isFile()) {
				
				String site=file.getName().split("_"+suffix+".txt")[0];
				if(sites.get(site)==null) continue;
				
				System.out.println("***************************");
				System.out.println("Process site "+site);
				ArrayList<IOPair> pairs=parseResultIODependencies(site, file.getAbsolutePath());
				sites.get(site).sortedFirstFrom=pairs;
			}
		}
	}
	
	
	
	public static final ArrayList<IOPair> parseResultIODependencies(String site, String filepath) {
		ArrayList<IOPair> list=new ArrayList<IOPair>();
		IOPair currentIO=null;
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
			
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
	
				if (!sCurrentLine.isEmpty()) {
					sCurrentLine.trim();
					if (sCurrentLine.startsWith("D:"))
						{
							String pair=sCurrentLine.split("D:")[1];
							String f_from=pair.trim().split("--->")[0];
							String f_to=pair.trim().split("--->")[1];
							
							WebFunction F_from=DownloadManager.getWebFunctionForSiteAndFunctionName(site, f_from.trim());
							WebFunction F_to=DownloadManager.getWebFunctionForSiteAndFunctionName(site, f_to.trim());
							if(F_from==null || F_to==null) {
								System.err.println("One of the functions does not exit f_from="+f_from.trim()+" f_to="+f_to.trim());
								continue;
							}
							currentIO=new IOPair(F_from, F_to);
							list.add(currentIO);
						}
					else if (sCurrentLine.startsWith("DEBUG:")) {
							return list;
					} 
					else {
							/**System.out.println("     Valid path: "+sCurrentLine);**/
							currentIO.validPaths.add(sCurrentLine.trim());
					}
				} 
			}
			
			return list;
		} catch (IOException e) { e.printStackTrace(); return list;}
	}



	/** print the results in a file and a directory 
	 * @throws IOException **/
	public final static void storeDependenciesForSite(Site s,String debugMsg, String file) throws IOException{
		/** prepare the output file for dependencies **/
		FileWriter fOut = new FileWriter(file);
		for(IOPair p:s.sortedFirstFrom){
			fOut.append(p.getResults());
		}
		
		fOut.append("DEBUG: \n");
		for(IOPair p:s.sortedFirstFrom){
			fOut.append(p.getPathsInTo());
		}
		
		fOut.append(debugMsg);
		
		for(IOPair p:s.sortedFirstFrom){
			fOut.append(p.toString()+"\n");
		}
		
		fOut.close();
	}
	
	
}
