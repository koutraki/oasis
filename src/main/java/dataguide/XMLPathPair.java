package dataguide;

public class XMLPathPair implements Comparable<XMLPathPair>{

	public String pathDG1;
	public String pathDG2;
	
	public XMLPathPair(String pathDG1, String pathDG2){
		this.pathDG1=pathDG1;
		this.pathDG2=pathDG2;
	}
	
	@Override
	public int compareTo(XMLPathPair o) {
		int c=this.pathDG1.compareTo(o.pathDG1);
		if(c!=0) return c;
		
		c=this.pathDG2.compareTo(o.pathDG2);
		return c;
	}
	
	@Override
	public int hashCode(){
		return new String(pathDG1+" "+pathDG2).hashCode();
	}
	
	@Override
	public boolean equals(Object o){  
		  if(!(o instanceof XMLPathPair)) return false;
		  XMLPathPair p=(XMLPathPair) o; 
		  return p.pathDG1.equals(pathDG1) && p.pathDG2.equals(pathDG2);
		
		 }  
	
	
}
