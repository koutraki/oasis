package dependenciesIO.joinInputInstanceWithOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;



public class TreeClasses {
	
	
	HashMap<String,NodeClass> classes=new HashMap<String,NodeClass>();
	NodeClass root=null;
    HashMap<String, NodeClass> prefixSearchMap=new HashMap<String,NodeClass>();
    

	public TreeClasses(){
	}
	
	public String toString(){
		StringBuffer buff=new StringBuffer();
		for (NodeClass val:classes.values()){
			buff.append(val.prefixFromRoot+"\t"+val.names+"\n");
		}
		buff.append("Prefixes : \n");
		for (String prefix :prefixSearchMap.keySet()){
			NodeClass n=prefixSearchMap.get(prefix);
			if(n.parent!=null) buff.append(n.parent.names+" "+n.pathToParent+n.names+"\n");
		}
		return buff.toString();
	}
	
	
	/** finds the class those path in the XML represents the largest prefix included in the given path**/
	public NodeClass  getClassOfProperty(String path){
		/** get the iterator in the inverse oder **/
		IterateurElement it=new IterateurElement(path, true);
		
		String prefix="";
		while(it.hasNext()){
			String elem=it.next();
			prefix=prefix+"/"+elem;
			//System.out.println("Check :"+prefix);
		}
		
		it=new IterateurElement(path, true);
		NodeClass n=recursiveSearch("", it);
		return n;
	}
	
	public NodeClass recursiveSearch(String prefix, IterateurElement it){
		if(!it.hasNext()) return null;
		String elem=it.next();
		prefix=prefix+"/"+elem;
		NodeClass descEntity=recursiveSearch(prefix, it);
		if(descEntity==null) {
			NodeClass n=prefixSearchMap.get(prefix);
			if(n!=null && n.isClassFromKB && n.children.size()>0) return n;
		}
		return descEntity;
	}
	
	
	
	/***************************************************************************************/
	/*** CREATE ONE TREE **/
	/***************************************************************************************/
	
	public void createOneTree(ArrayList<NodeClass> x_nodes){
		root=new NodeClass("root");
		root.completePathInfo("", null);
        
		/** eliminate the nodes X that are empty & change references from/to children to root*/
		ArrayList<NodeClass> validX=new ArrayList<NodeClass>();
		for(NodeClass x:x_nodes){
			if(x.prefixesFromRoot.contains("")){
				for(NodeClass child:x.children){
					  child.parent=root;
					  root.children.add(child);
				}
				for(String name:x.names) classes.remove(name);
			}
			else{	
					x.parent=root;
					x.pathToParent=x.prefixesFromRoot.iterator().next();
					validX.add(x);
			}
		}
		
		
		/** now process all the paths to parent of the nodes x and create nodes for the prefixes **/
	    for(NodeClass node:classes.values()){
	    		String prefixFromRoot=node.prefixesFromRoot.iterator().next();
	    		if(prefixSearchMap.get(prefixFromRoot)!=null) continue;
	    		IterateurElement it=new IterateurElement(prefixFromRoot);
	    		
	    		String prefix="";
	    		NodeClass parent=root;
	    		
	    		while(it.hasNext()){
	    			it.next();
	    			prefix+="/"+it.currentElement;
	    			NodeClass z=prefixSearchMap.get(prefix);
	    			if(z==null){
	    				z=new NodeClass();
	    				z.completePathInfo(it.currentElement, parent);
	    				prefixSearchMap.put(prefix,z);
	    				//System.out.println("  "+prefix);
	    			}
	    			parent=z;
	    		}
	    }
	    
	    /**label them with the old nodes of the mappings & updates the nodes in the map classes **/
	   for(String key:classes.keySet()){
		   		NodeClass oldNode=classes.get(key);
    				String prefixFromRoot=oldNode.prefixesFromRoot.iterator().next();
    				if(prefixFromRoot.length()==0) continue;
    				if(prefixFromRoot.endsWith("/")) prefixFromRoot=prefixFromRoot.substring(0, prefixFromRoot.length()-1);
    				
    				NodeClass nNew=prefixSearchMap.get(prefixFromRoot);
    				//System.out.println("Searched prefix: "+prefixFromRoot);
    				nNew.names.addAll(oldNode.names);
    				nNew.prefixFromRoot=prefixFromRoot;
    				nNew.isClassFromKB=true;

    				oldNode.prefixFromRoot=prefixFromRoot;	
	    }
	   
	   /** change the nodes in the map classes **/
	   for(String name: classes.keySet()){
		   classes.put(name, prefixSearchMap.get(classes.get(name).prefixFromRoot));
	   }
	   
	 
	}
	
	
	/*************************************************************/
	/** CLASS NODE */
	/*************************************************************/
	
	public static final class NodeClass{
		HashSet<String> names=new HashSet<String>();
		
		NodeClass parent=null;	
		String pathToParent=null;
		ArrayList<NodeClass> children=new ArrayList<NodeClass>();
		
		String prefixFromRoot=null;
		boolean isClassFromKB=false;
		
		/** only temporal in the computation **/
		HashSet<String> prefixesFromRoot=null;
		
		public NodeClass(String variable){
			this.names.add(variable);
		}
		
		public NodeClass(){
			
		}
		
		public void completePathInfo(String pathToParent, NodeClass parent) {
			this.pathToParent=pathToParent;
			this.parent=parent;
			
			if(parent!=null) {
				//System.out.println(parent.names+" hasChild "+this.names);
				parent.children.add(this);
			}
		}
		
		public String getMatching(){
			return " \t\t "+((parent!=null)?parent.names+"\t ":"")+pathToParent+"\t "+names;
		}
		
		public String getPrefix(){
			return  prefixesFromRoot+" \t\t"+names;
		}
		
		public void fillPrefixesFromRoot(ArrayList<String> root_to_x){
			
			if(parent==null)
			{
				if(prefixesFromRoot==null) {
					prefixesFromRoot=new HashSet<String>();
					prefixesFromRoot.addAll(root_to_x);
				}
				return;
			}
			
			if(parent.prefixesFromRoot==null){
				parent.fillPrefixesFromRoot(root_to_x);
			}
			
			for(String prefixParent: parent.prefixesFromRoot){
				if(prefixesFromRoot==null) prefixesFromRoot=new HashSet<String>();
				prefixesFromRoot.add(prefixParent+"/"+pathToParent);
			}			
		}
		
		
		public void  getLeafDescendants(ArrayList<NodeClass> desc){
			if(children.size()==0) desc.add(this);
			for(NodeClass nc:children) nc.getLeafDescendants(desc);
		}
		
		public HashSet<String> getRelativePathsToChildren(HashSet<String> paths, String prefix){
			if(children.size()==0) {
				paths.add(prefix);
				return paths;
			}
			for(NodeClass c: children){
				String cPrefix=(prefix==null)?c.pathToParent:prefix+"/"+c.pathToParent;
				c.getRelativePathsToChildren(paths, cPrefix);
			}
			return paths;
		}
		
	}
	
	
	/*************************************************************/
	/** CLASS ITERATEUR POUR LES PREFIX */
	/*************************************************************/
	public static final class IterateurElement implements Iterator<String>{
		
		String[] elements;
		int first;
		int last;
		boolean ascendant=true;
		
		public String currentElement;
		
		public IterateurElement(String prefix){
			elements=prefix.split("/");
			
			first=0;
			if(elements[0].length()==0) first=1;
			
			last=elements.length-1;
			if(last>=0) if(elements[last].length()==0) last--;
		}
		

		public IterateurElement(String prefix, boolean ascendant){
			elements=prefix.split("/");
			
			first=0;
			if(elements[0].length()==0) first=1;
			
			last=elements.length-1;
			if(last>=0) if(elements[last].length()==0) last--;
			
			this.ascendant=ascendant;
		}

		@Override
		public boolean hasNext() {
			return first<=last;
		}
		
		@Override
		public String next() {
			if(ascendant) currentElement=elements[first++];
			else currentElement=elements[last--];
			return currentElement;
		}

		@Override
		public void remove() {
			//not implemented 
		}

	}
	
	
}
