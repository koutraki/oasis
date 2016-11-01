package dependenciesIO.joinInputInstanceWithOutput;


import java.util.HashMap;
import java.util.HashSet;

import customization.Constants;
import dependenciesIO.IOPair;
import dependenciesIO.LoadIODependencies;
import dependenciesIO.PreparePairInputs;
import dependenciesIO.Site;
import download.DownloadManager;


public class JoinClassesAndDependenciesPaths {
	
	/** associate to each path it's input **/
	public static final HashMap<String, ClassEntityInXML> joinPaths(HashSet<String> validPaths, IOPair pair) throws Exception{
		HashMap<String, ClassEntityInXML> entityOfProperty=new HashMap<String, ClassEntityInXML>();
		for(String path:validPaths){
			System.out.println("Valid path: "+path);
			if(pair.f_from.tree==null){
				System.err.println("Tree does not exit for "+pair.f_from.functionName+"  of "+pair.f_from.site);
				continue;
			}
			
			/**System.out.println("Tree: "+pair.f_from.tree.toString());**/
			TreeClasses.NodeClass n=pair.f_from.tree.getClassOfProperty(path);
			
			if(n==null) n=pair.f_from.tree.root;
			//System.out.println("Path from the root "+n.prefixFromRoot);
			ClassEntityInXML entity=pair.f_from.classEntities.get(n.prefixFromRoot);
			
			if(entity==null){
				HashSet<String> relativePaths = n.getRelativePathsToChildren(new HashSet<String>(), null);
				
				entity=new ClassEntityInXML(n.prefixFromRoot, relativePaths);
				pair.f_from.classEntities.put(n.prefixFromRoot, entity);
			}
			
			
			addPathWithInput(path, entity,pair);
			entityOfProperty.put(path, entity);	
		}
		return entityOfProperty;
	}
	
	/** associate to each path the relative path **/
	public static void addPathWithInput(String path, ClassEntityInXML entity, IOPair pair) throws Exception{
	    if(entity.pathFromRoot.equals("/")){
	    		String p=(path.startsWith("/"))?path.substring(1):path;
	    		p=(p.endsWith("/"))?p.substring(0,p.length()-1):path;
	    		entity.addExtraPaths(p);
	    		pair.validPathsAsRelativePaths.put(path, p);
	    		return;
	    }
		
		String p=path.startsWith("/")?path:"/"+path;
		p=p.endsWith("/")?p.substring(0,p.length()-1):p;
	
		if(entity.pathFromRoot.length()>0 && p.contains(entity.pathFromRoot)){
				
				/** System.out.println(" Path relation  "+entity.pathFromRoot+"  normalize path **"+normalizePath);**/
				String relPath=p.split(entity.pathFromRoot)[1];
				if(relPath.startsWith("/")) relPath=relPath.substring(1);
				
				/** System.out.println("New property path "+relPathOfTheDiscoveredProperty);**/
				if(relPath.length()>0 && !entity.pathsFromKB.containsKey((relPath))) {
					entity.addExtraPaths(relPath);
				}
		
				pair.validPathsAsRelativePaths.put(path, relPath);
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
		
		LoadIODependencies.loadIODependencies(Constants.step1,map);
		
		LoadTreeClass.load(map);
		
		for(Site site:map.values()){
			System.out.println("-------------------------------");
			System.out.println("Site "+site.site);
			for(IOPair p:site.sortedFirstFrom){
				System.out.println("   Function "+p.f_from.functionName+" \n");
				System.out.println("   Tree :"+p.f_from.tree+" \n");
				HashMap<String, ClassEntityInXML> entityOfProperty=joinPaths(p.validPaths, p);
				for(String path:entityOfProperty.keySet()){
					System.out.println("          "+entityOfProperty.get(path));
				}
			}
		}

	}
	
}
