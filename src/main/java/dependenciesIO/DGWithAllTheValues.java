package dependenciesIO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import dataguide.DataGuide;
import dataguide.Node;

/**
 * This structure stores only the paths
 * It does not store the textual values 
 */
public class DGWithAllTheValues extends DataGuide implements Iterator<DGWithAllTheValues.PairValueListOfDocuments>{

	/** the current call that is being treated **/
	String currentCall=null;
	
	/** we record the values and the documents where they occur under the given path **/
	public HashMap<String, ValuesAndListOfDocuments> pathsValuesAndTheirDocuments=new HashMap<String, ValuesAndListOfDocuments>();
	
	/** needed for the functionality **/
	ArrayList<ValuesAndListOfDocuments> iterators=null;
	
	/***********************************/
	/*** Overrides classes of the super class **/
	/***********************************/
	@Override
	protected void addValueNode(Node parent, String value){
		/** for this structure we do not record the values **/
		String path=parent.getStringPathRootToNode();
		ValuesAndListOfDocuments valDocs= pathsValuesAndTheirDocuments.get(path);
		if(valDocs==null) {
			valDocs= new ValuesAndListOfDocuments();
			pathsValuesAndTheirDocuments.put(path, valDocs);
		}
		valDocs.addValue(path,value, currentCall);
	}
	
	public boolean makeparse(String file, String input) throws Exception{
		this.currentCall=input;
		return super.makeparse(file);
	}
	
	/***********************************/

	public int getNumberOfPaths(){
		return pathsValuesAndTheirDocuments.keySet().size();
	}
	
	public Collection<String> getAllValuesUnderPath(String path){
		return pathsValuesAndTheirDocuments.get(path).valuesAndDocuments.keySet();
	}
	public Collection<String> getAllDocumentsForPathAndValue(String path, String val){
		return pathsValuesAndTheirDocuments.get(path).valuesAndDocuments.get(val);
	}
	
	/***********************************/
	/*** Iterator **/
	/***********************************/
	public void initIterator() {
		iterators= new ArrayList<ValuesAndListOfDocuments>();
		for(String path:pathsValuesAndTheirDocuments.keySet()){
			ValuesAndListOfDocuments it=pathsValuesAndTheirDocuments.get(path);
			it.init();
			if(it.hasNext()) iterators.add(it);
		}
		if(iterators.size()==0) iterators=null;
		else {
				/** we insert one token null to signal the when a complete round is finalized **/
				/**System.out.println("************* I add marker ");**/
				iterators.add(null);
		}
	}

	
	@Override
	public boolean hasNext() {
		return (iterators!=null && iterators.size()!=1);
	}
	
	@Override
	public DGWithAllTheValues.PairValueListOfDocuments next() {
		ValuesAndListOfDocuments it=iterators.remove(0);
		
		/** if it's the token inserted by us, to check the finished of a round **/
		if(it==null) {
			iterators.add(iterators.size(), it);
			return null;
		}
		
		/** I am sure that there is an element in the iterator **/
		PairValueListOfDocuments pair=it.next();
	
		/** I add on the last position; this is why do a round robin exploration of the paths **/
		if(it.hasNext()) iterators.add(iterators.size(), it);
		
		/** if only the token remained this means that we have finished reading all the values **/
		if(iterators.size()==1) iterators=null;
		return pair;
	}


	@Override
	public void remove() {
		/** do nothing; the method next advances the iterators */
	}
	
	@Override 
	public String toString(){
		StringBuffer b= new StringBuffer();
		for(String path:pathsValuesAndTheirDocuments.keySet()){
			b.append(path+"\n");
			ValuesAndListOfDocuments it=pathsValuesAndTheirDocuments.get(path);
			it.init();
			if(it.hasNext()) b.append(" "+it.iteratorValues.toString()+"\n\n");
		}
		
		return b.toString();
	}

	/***********************************/
	/** CLASS 
	/***********************************/

	class DecrIntegerComparator implements Comparator<Integer>{

		@Override
		public int compare(Integer o1, Integer o2) {
			return o2-o1; 	
		}
	}

	class ValuesAndListOfDocuments implements Iterator<PairValueListOfDocuments>{
		String path=null;
		public HashMap<String, ArrayList<String>> valuesAndDocuments=new HashMap<String, ArrayList<String>>();
		
		/** for the iterator **/
		PriorityQueue<String> iteratorValues=null;
		
		public void addValue(String path,String value, String document){
			this.path=path;
			ArrayList<String> documents=valuesAndDocuments.get(value);
			if(documents==null) {
				documents=new ArrayList<String>();
				valuesAndDocuments.put(value, documents);
			}
			if(!documents.contains(document)) documents.add(document);
		}

		public void init(){
			iteratorValues=new PriorityQueue<String>(valuesAndDocuments.keySet());
		}
		
		@Override
		public boolean hasNext() {
			return !(iteratorValues.isEmpty());	
		}

		@Override
		public PairValueListOfDocuments next() {
			String value=iteratorValues.poll();
			return new PairValueListOfDocuments(path,value, valuesAndDocuments.get(value));
		}

		@Override
		public void remove() {
			/** nothing **/
		}	
	}
	
	/***********************************/
	/** CLASS 
	/***********************************/
	
	public class PairValueListOfDocuments{
		ArrayList<String> documents=null;
		public String value=null;
		public String path=null;
		
		public PairValueListOfDocuments(String path, String value, ArrayList<String> documents){
			this.path=path;
			this.value=value;
			this.documents=documents;
		}
		
		public String toString(){
			return path+" val="+value+" documents="+documents.toString();
		}
	}


}
