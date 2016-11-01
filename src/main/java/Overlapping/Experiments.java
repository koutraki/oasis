package Overlapping;

import java.util.ArrayList;

import download.DownloadManager;
import download.WebFunction;

public class Experiments {

	
	public static void main(String[] args) throws Exception
    {  
    	  	/** DownloadManager.writeFunctionsAndTheirSitesToFile(Constants.fileWithTheInputTypesOfTheFunctions);**/
		DownloadManager.initStubs();
			
		ArrayList<WebFunction> listF=DownloadManager.computeSamplesForFunctionsWithKnownTypes(new ArrayList<String>());
		System.out.println("\n\n\n\n\n\nFunctions with known inputs:");
		for(WebFunction F: listF){
			System.out.println(F.functionName+"   "+F.site);	
			System.out.println("\t\t"+F.validInputs);	
		}
		System.out.println();
		
		/** store the history of the calls in order to avoid in the future executing functions that return errors **/
		DownloadManager.storeCacheOnDisk();
    }

}
