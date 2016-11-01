package dataguide;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;


/* a bug: in the case there are attributes with the same name for different nodes or an attribute and an element with the same name: bug*/
/* so add the type and the name of the element for the attributes*/
public class DataGuide extends DefaultHandler{
	/** entry point: the root **/
	public Node root=new Node("root", Node.DG_ROOT, null);

	/** another entry point: list of all nodes which have text values underneath **/
	public HashSet<Node> nodesWithTextValues;
	
	/** Get a node with values by path from the root **/
	public HashMap<String, Node> getNodeWithValuesByPathFromTheRoot=new HashMap<String, Node>();
	
	/** structures needed for the construction of the dataguide **/
	protected StringBuffer buff=null;
	protected Stack<Node> st=new Stack<Node>();
	
	/** list of nodes with siblings with the same name **/
	public ArrayList<Node> nodesWithSiblingsWithTheSameName= new ArrayList<Node>();
	
	/** **/
	public DataGuide()
	{	
		nodesWithTextValues=new HashSet<Node>();
		st.push(root);
	}
	
	public void clear(){
		buff=null;
		st.clear();
		st.push(root);
	}

	public boolean makeparse(String file) 
  	{try{
      		SAXParser parser=new SAXParser();
      		clear();
      		reInitMap();
     
      		System.setProperty( "org.xml.sax.driver","org.apache.xerces.parsers.SAXParser"	 );
   	   	     	  
   	  		parser.setContentHandler(this);
   	  		/**
   	  		try {
   				parser.setFeature("http://xml.org/sax/features/validation", true);
   				parser.setFeature("http://xml.org/sax/features/namespaces", true);   				
   				parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", true);
   				parser.setFeature("http://apache.org/xml/features/"+ "validation/schema", true);
                parser.setFeature("http://apache.org/xml/features/"+ "validation/schema-full-checking",true);               
 				} catch (SAXException e) {
   				System.err.println("Cannot activate validation."); 
 				}
 				PSVIProvider provider=(PSVIProvider)parser;	
 			**/
   	  		parser.parse(new InputSource(new FileInputStream(new File(file))));
   	  	
   	  		return true;
  		
  		}catch(Exception e){
  			 return false;
  		}
   	  	
   	  		
   	  		
  	}
	/*****************************************************************************************/
	/**	PUSH/POP NODES ON THE STACK: re-groups all the operations that should be done */
	/****************************************************************************************/
	protected void pushOnTheStack(Node n){
		st.push(n);
		n.updateSiblingInfoForThisNodeAndItsChildrens();
	}
	
	protected Node popFromTheStack(){
		Node n=st.pop();
		n.forChildrenNodesRebattleForMaxSiblings(nodesWithSiblingsWithTheSameName);
		return n;
	}

	/*****************************************************************************************/
							/**	PARSE ELEMENT	*/
	/****************************************************************************************/
	public void startElement (String uri, String local, String qName, Attributes atts)
	{
		flushText();
		Node current=null;
		
		/** by construction, our dataguide is never empty  **/
		if(! st.empty()) 
    		 	{
				Node parent=(Node)st.peek();
    		 		current=parent.getOrCreate(local,Node.ELEM);
    		 		pushOnTheStack(current);
    		 	}
    		 	
		 /* parse the attributes*/
		 parseAttributes(current,atts); 		
	}
	
	public void endElement(String uri, String localName, String qName) 
	{
		flushText();
		popFromTheStack();
	}	
	
	
	/*****************************************************************************************/
							/**	PARSE ATTTRIBUTES	*/
	/****************************************************************************************/
	public void parseAttributes(Node parent, Attributes attribs)
  	{ 
		for ( int i=0;i<attribs.getLength();i++)
    	{
    		String local=attribs.getLocalName(i);  
    		String value=attribs.getValue(i);
    		Node sonNode=parent.getOrCreate(local, Node.ATT);
    		if(value!=null) {
    			value=value.trim();
    			if(value.length()>0){ 
    								/** normalize the text **/
    								value=value.replaceAll("\\s+"," ");
    								addValueNode(sonNode,value);
    								if(!nodesWithTextValues.contains(sonNode)){ nodesWithTextValues.add(sonNode);
    								}
    				}
    		}
    	}
		
  	}
	/*****************************************************************************************/
							/**	PARSE TEXT	*/
	/****************************************************************************************/
	public void characters (char[] ch, int start, int length)  {
		if(buff==null) buff=new StringBuffer();
		buff.append(ch,start,length);
  	}
	public void flushText(){
		if(buff==null) return;
		String value=buff.toString().trim();
		if(value.length()<1) return;
		
		if(st.empty()) return;
		Node parent=st.peek();
		
		/** normalize the text **/
		value=value.replaceAll("\\s+"," ");
		addValueNode(parent,value);
		nodesWithTextValues.add(parent);
		buff=null;
	}
	
	public void parseText(String value)
  	{
  	 	StringTokenizer st = new StringTokenizer(value);
  	 	while (st.hasMoreTokens()) {
     				 String s=st.nextToken();
     				 if(!(s==null)){
     		
     				 		}
     				 }		 				 
  	 }	
	
	/*****************************************************************************************/
		/**	ADD VALUE NODE	*/
	/****************************************************************************************/
	protected void addValueNode(Node parent, String value){
		parent.addValue(value);
	}
	
	
	/*****************************************************************************************/
			/**	INIT MAP WITH PATHS	*/
	/****************************************************************************************/
	public void reInitMap(){	
		getNodeWithValuesByPathFromTheRoot.clear();
		for(Node node: nodesWithTextValues){
			String path=node.getStringPathRootToNode();
			getNodeWithValuesByPathFromTheRoot.put(path, node);
		}
	}
	
	public void showMap(){
		for(String path:getNodeWithValuesByPathFromTheRoot.keySet()){
			Node node=getNodeWithValuesByPathFromTheRoot.get(path);
			System.out.println("Path="+path+" Values={");
			for(Node son: node.sons.values()){
				System.out.println("\t"+son.name);
			}
		}
	}
	
	/*****************************************************************************************/
	/**	GET OVERLAPPING PATH	*/
   /****************************************************************************************/
	public PriorityQueue<ValueNode> getValuesInAPriorityQueue(Comparator<ValueNode> c){
		PriorityQueue<ValueNode> pq = new PriorityQueue<ValueNode>(200, c);
		 /** construct the heap **/
	//	System.out.println("nodesWithTextValues.size: "+nodesWithTextValues.size());
		for(Node n: nodesWithTextValues){
			for (ValueNode vn: n.getValues()){
				pq.add(vn);
			}
		}
		return pq;
	}
	
	/*****************************************************************************************/
	/**	GET CANDIDATES FOR MAPPING 	*/
    /****************************************************************************************/
	public String getStringMessageCandidatesForMapping(){
		StringBuffer b= new StringBuffer();
		b.append("\nCandidate nodes (nodes with siblings): ");
		PriorityQueue<Node> p = new PriorityQueue<Node>(30, new Node.NodeComparatorFunctionPath());
		p.addAll(nodesWithSiblingsWithTheSameName);
		
		while(!p.isEmpty()){
			Node n=p.poll();
			b.append("\n\t"+n.name+" \t"+n.maxSiblings+"\t "+n.getStringPathRootToNode());
		}
		
		b.append("\n");
		return b.toString();
	}
	
	/*****************************************************************************************/
	/**	TO STRING	*/
	/****************************************************************************************/
	public String toString(){
		StringBuffer b= new StringBuffer();
		PriorityQueue<String> pathsQueue= new PriorityQueue<String>(getNodeWithValuesByPathFromTheRoot.keySet());
		while(!pathsQueue.isEmpty()){
			String path=pathsQueue.poll();
			b.append(getNodeWithValuesByPathFromTheRoot.get(path).toString()+"\n\n");
		}
		return b.toString();
	}
	
	public static void main(String[] args) throws Exception
	{
		
	}    
}
