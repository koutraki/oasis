package dependenciesIO.joinInputInstanceWithOutput;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class ClassEntityInXML {
	
	private static final XPathFactory xpathFactory = XPathFactory.newInstance();
	private static final XPath xpath = xpathFactory.newXPath();

	public final String pathFromRoot;
	public final XPathExpression pathFromRootXPath;
	
	public final HashMap<String,XPathExpression> pathsFromKB=new HashMap<String,XPathExpression>();
	public final HashMap<String,XPathExpression> extraPaths=new HashMap<String,XPathExpression>();
	
	/** pairs (input-value, resulted-object-instance) **/
	public final HashMap<String, Instance> instances=new HashMap<String,Instance>();
	
	public ClassEntityInXML(String pathFromRoot,HashSet<String> pathsFromKBSet) throws Exception{
		/**System.out.println("The path is "+pathFromRoot);**/
		this.pathFromRoot=(pathFromRoot==null)?"/":pathFromRoot;
		
		String extendedPath=(pathFromRoot==null)?"/":toGeneralXPath(pathFromRoot);
		this.pathFromRootXPath=	xpath.compile(extendedPath);	
			
		for(String path: pathsFromKBSet){
			String p=(path.endsWith("/"))?path.substring(0,path.length()-1):path;
			pathsFromKB.put(p, xpath.compile(appendText(toGeneralXPath(p))));
		}
	}
	
	public void addExtraPaths(String path) throws Exception{
		extraPaths.put(path, xpath.compile(appendText(toGeneralXPath(path))));
		
	}
	
	public String toString(){
		StringBuffer buff=new StringBuffer();
		buff.append(" "+pathFromRoot+" (");
		for(String l:pathsFromKB.keySet()){
			buff.append(l+",");
		}
		buff.append(") :");
		
		
		buff.append("  (");
		for(String l:extraPaths.keySet()){
			buff.append(l+",");
		}
		buff.append("):");
		return buff.toString();
	}
	
	public String getInstancesToString(){
		StringBuffer buff=new StringBuffer();
		for(String input:instances.keySet()){
			buff.append("["+input+", "+instances.get(input)+"]\n");
		}
		return buff.toString();
	}
	
	/** functions transform the paths to XPaths that do not take ignore the namespaces **/
	public static final String toGeneralXPath(String path){
		if(path.equals("")) return null;
		StringBuffer newPath= new StringBuffer();
		String[] splits = path.split("/");
		
		if(splits.length==0) return newPath.toString();
		if(splits[0].length()>0) {
			if(path.startsWith("/")) newPath.append("/");
			if(splits[0].startsWith("@")) newPath.append("@*[local-name()='"+splits[0].substring(1)+"']");
			else newPath.append("*[local-name()='"+splits[0]+"']");
		}
		
		for(int i=1; i<splits.length; i++){
			if(splits[i].startsWith("@")) newPath.append("/@*[local-name()='"+splits[i].substring(1)+"']");
			else newPath.append("/*[local-name()='"+splits[i]+"']");
		}
	
		return newPath.toString();
	}
	
	public String appendText(String path){
		if(! path.contains("@")) return path+"/text()";
		else return path;
	}
	
	/***************************************************/
	/** Class Instances **/
	/***************************************************/
	public static final class Instance{
		public HashMap<String, ArrayList<String>> values=new HashMap<String, ArrayList<String>>(); 
		
		public String toString(){
			StringBuffer buff=new StringBuffer();
			buff.append("(");
			for(String k:values.keySet()){
				//buff.append(k+"="+values.get(k)+",");
				buff.append(values.get(k)+",");
			}
			buff.append(")");
			return buff.toString();
		}
	}
	
}
