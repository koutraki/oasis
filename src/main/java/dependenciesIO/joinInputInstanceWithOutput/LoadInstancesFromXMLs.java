package dependenciesIO.joinInputInstanceWithOutput;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import customization.Constants;
import dependenciesIO.IOPair;
import dependenciesIO.LoadIODependencies;
import dependenciesIO.PreparePairInputs;
import dependenciesIO.Site;
import download.DownloadManager;
import download.WebFunction;


public class LoadInstancesFromXMLs {
	
	private static final XPathFactory xpathFactory = XPathFactory.newInstance();
	private static final XPath xpath = xpathFactory.newXPath();

	/*************************************************************************/
	/*** Load Classes **/ 
	/*************************************************************************/
	public static final void loadTheClassEntities(Site site)throws Exception{
		for(IOPair p:site.sortedFirstFrom){
			/**System.out.println("\n   Function "+p.f_from.functionName+" ");**/
			/**System.out.println("   Tree :"+p.f_from.tree+" \n");**/
			
			HashMap<String, ClassEntityInXML> entityOfProperty=
					JoinClassesAndDependenciesPaths.joinPaths(p.validPaths, p);
			
			p.classEntities=entityOfProperty;
			
			/**for(String path:entityOfProperty.keySet()){
				System.out.println("          "+entityOfProperty.get(path));
			}**/
			
			/** 
			for(String pathProperty:p.validPaths){
					System.out.println("     "+pathProperty+" ---> "+p.validPathsAsRelativePaths.get(pathProperty));
			}**/
			
		
		}
	}
	
	/*************************************************************************/
	/*** Load Instances in Source Function (F From) **/ 
	/*************************************************************************/	
	public static final void loadInstancesForSourceFunctions(Site site, int maxSamples) throws Exception {
		ArrayList<WebFunction> loaded=new ArrayList<WebFunction>();
		
		for(IOPair p:site.sortedFirstFrom){
			//if(!"getAlbumsByArtistMBID".equalsIgnoreCase(p.f_from.functionName)) continue;
			
			if(loaded.contains(p.f_from)) continue;
			loaded.add(p.f_from);
			
			
			/** load inputs for f_from **/
			System.out.println("   "+p.f_from.functionName+": "+p.f_from.type+"="+DownloadManager.getValuesForType(p.f_from.type));
			
			/** for every input value, issue the calls and load the resulted values  **/
			int count=0;
			for(String input:DownloadManager.getValuesForType(p.f_from.type)){
				if(count>maxSamples) break;
				count++;
				/**System.out.println("     call "+input);**/
				loadInstancesReturnedByCall(p.f_from, input);
			}
			
			/** show instances **/
			for(ClassEntityInXML entity:p.f_from.classEntities.values()){
				System.out.println("Class "+entity+" "+entity.getInstancesToString());
				System.out.println();
			}
		}
	}
	
	
	
	
	
	/**************************************************************************/
	/*** XML Extraction **/
	/**************************************************************************/
	private static DocumentBuilderFactory factory; 
	private static DocumentBuilder builder;
  
	public static final void initXMLExtractor(){
		 try {
			 	factory = DocumentBuilderFactory.newInstance();
			 	factory.setNamespaceAware(true);
			 	builder = factory.newDocumentBuilder();	 	
		 } catch (ParserConfigurationException e) {
	            e.printStackTrace();
	        }
	}
	

	public static final void loadInstancesReturnedByCall(WebFunction f_from, String input){
		/** for each class of objects  **/
		Document doc=getResultForInput(f_from, input);
		for(ClassEntityInXML entity:f_from.classEntities.values()){
			//System.out.println("          "+f_from.classEntities.values());
			getInstances(input,doc,entity);
		}
	}
	
	private static Document getResultForInput(WebFunction f, String input) {
		Document doc=null;
	
		String fileResult=f.executeCall(input, true);
		//System.out.println("The file is "+fileResult+"**");
		if(fileResult==null || !(new File(fileResult)).isFile()) return doc;
		try{
					doc=builder.parse(fileResult);
		}catch (Exception e){return null;}
		return doc;
	}
	
	private static void getInstances(String input, Document doc,ClassEntityInXML entity)  {
		try {  
		/**System.out.println(" Execute "+entity.pathFromRoot);*/
        /** evaluate expression result on XML document */
		
        NodeList entities = (NodeList) entity.pathFromRootXPath.evaluate(doc, XPathConstants.NODESET);
        
    		//System.out.println(" Obtained the list "+entity.pathFromRoot);
        /** prepare an expression for each property **/
        for (int i = 0; i < entities.getLength(); i++){
			Node n=entities.item(i);
			/**System.out.println("  node: "+n.getNodeName());**/
			
			ClassEntityInXML.Instance instance= new ClassEntityInXML.Instance();
			
			for(String relPath:entity.pathsFromKB.keySet()){
				XPathExpression exprForRelativePath = entity.pathsFromKB.get(relPath);
				setProperties(n, entity, relPath, exprForRelativePath, instance);
	        }
			
			
			for(String relPath:entity.extraPaths.keySet()){
				XPathExpression exprForRelativePath = entity.extraPaths.get(relPath);
	        		setProperties(n, entity, relPath, exprForRelativePath, instance);
	        }
			
			
			entity.instances.put(input, instance);
        }
        
		} catch (XPathExpressionException e) {
			System.out.print(e.getMessage());
		}
	}
	
	public static void setProperties(Node n, ClassEntityInXML entity, String relPath,XPathExpression exprForRelativePath, ClassEntityInXML.Instance instance)throws XPathExpressionException {
		
		
		/** evaluate expression result on node */
		NodeList nodes = (NodeList) exprForRelativePath.evaluate(n, XPathConstants.NODESET);
		//System.out.println("Relative path  "+relPath+" "+nodes);
		if(nodes!=null && nodes.getLength()>0){
			ArrayList<String> listWithValues=new ArrayList<String>();
			for(int j=0;j<nodes.getLength();j++){
				String value=nodes.item(j).getNodeValue();
				if(value==null) continue;
				listWithValues.add(value.trim());
			}
			instance.values.put(relPath, listWithValues);
		}
	}
	/**************************************************************************/
	/*** main **/
	/**************************************************************************/
	
	public static void main(String[] args) throws Exception
	{  
		 DownloadManager.initStubs();
		
		/** functions accepting these inputs are considered as being known **/
		HashSet<String> types=new HashSet<String>();
		types.add("singers");types.add("songs");types.add("albums");
		types.add("actors");types.add("books");types.add("writers");
		
		types.add("singers_id_deezer");types.add("singers_id_discogs");types.add("singers_id_echonest");
		types.add("singers_id_last_fm");types.add("singers_id_music_brainz");types.add("singers_id_musixmatch");
		types.add("singers_mbid_musixmatch");
		
		types.add("actors_id_themoviedb");
	
		types.add("songs_id_echonest");types.add("songs_id_last_fm");	
		types.add("songs_id_music_brainz");types.add("songs_id_musixmatch");
		
		types.add("writers_id_isbndb");types.add("writers_id_library_thing");
		
		types.add("albums_id_deezer");types.add("albums_id_discogs");types.add("albums_id_last_fm");
		types.add("albums_id_music_brainz");types.add("albums_id_musixmatch");
		
		DownloadManager.processFunctionCallsInBatch(DownloadManager.prepareCallsToExecuteInBatch(types));
		
		/** separate the function of each site into from and to */
		HashMap<String, Site> map=PreparePairInputs.getSitesToProcess(types);
		
		LoadIODependencies.loadIODependencies(Constants.step1, map);
		LoadTreeClass.load(map);
		
		initXMLExtractor();
	
		for(Site site:map.values()){
			System.out.println("-------------------------------");
			System.out.println("Site "+site.site);
			loadTheClassEntities(site);
			/**loadInstancesForSourceFunctions(site);**/
			
			
		}
	}
}
