package dependenciesIO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

//import org.openjena.atlas.io.IO;

import customization.Constants;
import download.WebFunction;

public final class Site{

	public final String site;
	public final ArrayList<WebFunction> from=new ArrayList<WebFunction>();
	public final ArrayList<WebFunction> to=new ArrayList<WebFunction>();
	
	public final  HashMap<WebFunction, HashSet<String>> paths_with_input_in_f_to=new  HashMap<WebFunction, HashSet<String>>();
	
	/** XMLDocSignature for the from functions **/
	public final HashMap<WebFunction,DGWithAllTheValues> dataGuides=new HashMap<WebFunction, DGWithAllTheValues>();

	/** inputs to be executed by every "to" function**/
	public final ArrayList<String> inputs=new ArrayList<String>();
	public int indexNextToFunctionToCall=0;

	public ArrayList<IOPair> sortedFirstFrom=null;
	
	/** this search is inefficient and should be avoided **/
	public IOPair searchFromToTuple(WebFunction f_from, WebFunction f_to){
		for(IOPair p:sortedFirstFrom){
			if(p.f_from.equals(f_from) && p.f_to.equals(f_to)) return p;
		}
		return null;
	}
	
	public void initPairs(ArrayList<WebFunction> from, ArrayList<WebFunction> to){
		sortedFirstFrom=new ArrayList<IOPair>();
		
		PriorityQueue<IOPair> queue=new PriorityQueue<>(20, new IOPair.ComparatorFromFirst());
		for(WebFunction f_from:from)
			for(WebFunction f_to:to){
				queue.add(new IOPair(f_from, f_to));
			}
		
		while(!queue.isEmpty()){
			IOPair p=queue.poll();
			sortedFirstFrom.add(p);
		}
	}
	
	
	/** step 1**/
	/** for each function in the to list, the results of the dependencies **/
	public final ArrayList<HashMap<WebFunction,HashSet<String>>> dependencies=new  ArrayList<HashMap<WebFunction, HashSet<String>>>();
	public final HashMap<WebFunction, HashSet<String>>  pathsWithValuesReturningURLExceptions=new HashMap<WebFunction, HashSet<String>>();

	
	/** step 2: eliminate paths with constants **/
	/** for each f_from function, paths that were eliminated because they lead to too few values **/
	public final HashMap<WebFunction, HashSet<String>> pathsEliminatedBecauseTooFewValues= new HashMap<WebFunction, HashSet<String>>();
	
	/** step 4: for each f_from, paths eliminated because their results do not overlap **/
	public final HashMap<WebFunction, HashSet<String>> pathsLeadingToDocumentsWithFewDataInCommon= new HashMap<WebFunction, HashSet<String>>();
	
	/** step 5: discover invalid message **/
	/** for each function in the "to" list get the answer that they return when they return an error **/
	public final ArrayList<String> messageForInvalidCall=new ArrayList<String>();
	
	/** paths that were eliminated because their results are error messages **/
	public final HashMap<WebFunction,HashMap<WebFunction,HashSet<String>>> pathsWithInputsGeneratingErrors= new HashMap<WebFunction,HashMap<WebFunction,HashSet<String>>> ();
	
	
	public Site(String site){
		this.site=site;
	}
	
	
	
	
	
}

