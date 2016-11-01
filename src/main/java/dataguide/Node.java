package dataguide;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;




/*** @author Nicoleta Preda (andapreda@gmail.com) */

public class Node implements Comparable<Object>{

	public static final int ATT=0;
	public static final int ELEM=1;
	public static final int DG_ROOT=3;
	public static int counter=1;
	
	public String name; 
	public HashSet<String> inputCalls=null;
	public int kid;		
	public int type=ELEM;
	public Node parent=null;
	
	/** nodes that are elements */
	public HashMap<String, Node> sons=null;
	
	/** list of values **/
	private ArrayList<ValueNode> values=null;
	
	/** for convenience, we store the string representation of the path from the root to this node; 
	 *  the value is lazily computed**/
	public String pathFromTheRoot=null;
	
	/** lists of calls where the node appears (only for the nodes with values we record this value**/
	public ArrayList<String> callsWhereItAppears=null;
	
	public int maxSiblings=0;

	/*******************************************/
	/** auxiliary: used only during the construction of the dataguide **/
	public int currentSiblings=0;
	
	public Node(String name, int type, Node parent)
	{
		this.name=name;
		this.kid=counter++;	
		this.sons= new HashMap<String, Node>();
		inputCalls=new HashSet<String>();
		this.type=type;
		this.parent=parent;
	} 

	/*******************************************/
	public boolean equals(Object node)
	{
		if(name==null) return false;
		if(!  (node instanceof Node)) return false;
		
		Node n =(Node)node;		
		if(!name.equals(n.name)) return false;
		return n.kid==kid;
	}	
	
	public int hashCode()
	{return (name+kid).hashCode();}
	
	public int compareTo(Object o)
	{
		if( !(o instanceof Node)) return -2;
		Node n=(Node)o;
		if(kid<n.KID()) return -1;
		if(kid>n.KID()) return 1;		
		if(name==null) return -1;
		return  name.compareToIgnoreCase(n.name());
	}
	/***********************************/
	public ArrayList<Node> getPathRootToNode(){
		Node p=parent;
		ArrayList<Node> list= new ArrayList<Node>();
		while (p!=null)
		{
			list.add(0, p);
			p=p.parent;
		}
		list.add(this);
		/** remove the artificial root that we have added */
		list.remove(0);
		return list;
	}
	
	public String getStringPathRootToNode(){
		if(pathFromTheRoot!=null) return pathFromTheRoot;
		ArrayList<Node> path=getPathRootToNode();
		StringBuffer b= new StringBuffer();
		for(Node n:path) b.append(n.name+"/");
		pathFromTheRoot=b.toString();
		return pathFromTheRoot;
	}
	
	/***********************************/
	public String toString()
	{
		
		Node p=parent;
		ArrayList<Node> list= new ArrayList<Node>();
		while (p!=null)
		{
			list.add(0, p);
			p=p.parent;
		}
		list.add(this);
		
		StringBuffer b= new StringBuffer();
		b.append("Path=");
		for(Node n: list){
			b.append(n.name+"/");
		}
		if(values!=null){
			b.append("\nValues={\n\t");
			for(ValueNode n: values){
				b.append(n.value+"\n\t");
			}
			b.append("}");
		}
		if(callsWhereItAppears!=null){
			b.append("\nCalls={\n\t");
			for(String c: callsWhereItAppears){
				b.append(c+"\n\t");
			}
			b.append("}");
		}
		
		return b.toString();
	}

  	/***********************************/
  	public String name()
  	{	return name;		}
  	
  	public int KID()
  	{	return kid;	}
 
  	/***********************************/	
  	public Node getOrCreate(String name, int type){
  		Node n=sons.get(name);
  		if(n==null) {
  			n=new Node(name,type,this);
  			sons.put(name, n);
  			n.parent=this;
  			} 
  		return n;
  	}

  	/***********************************/
  	public ArrayList<ValueNode> getValues(){
  		return values;
  	}
  	
  	public void addValue(String value){
  		if(values==null) values=new ArrayList<ValueNode>();
  		ValueNode v=new ValueNode(this, value);
  		if (!values.contains(v)) values.add(v);
  	}
 	/******************************************/
  	/** nodes with siblings with the same name **/
  	/*****************************************/
  	/** this method should be used when the node is pushed on the stack **/
  	public void updateSiblingInfoForThisNodeAndItsChildrens(){
  		/** I add one to the sibling of this node **/
  		currentSiblings++;
  		
  		/** I clear the siblings of children nodes **/
  		for(Node n: sons.values()){
  			n.currentSiblings=0;
  		}
  	}
  	
  	/** this method should be used the node is popped from the stack **/
  	public void forChildrenNodesRebattleForMaxSiblings(ArrayList<Node> nodesWithSiblingsWithTheSameName){
  		/** I clear the siblings of children nodes **/
  		for(Node n: sons.values()){
  			if(n.currentSiblings>n.maxSiblings) {
  				n.maxSiblings=n.currentSiblings;
  				if(n.maxSiblings>=2 && ! nodesWithSiblingsWithTheSameName.contains(n)){
  					nodesWithSiblingsWithTheSameName.add(n);
  				}
  			}
  		}
  	}
  	
  	public static final class NodeComparatorFunctionPath implements Comparator<Node> {
		@Override
		public int compare(Node o1, Node o2) {
			return o1.getStringPathRootToNode().compareTo(o2.getStringPathRootToNode());
		}

		
  	}
}
