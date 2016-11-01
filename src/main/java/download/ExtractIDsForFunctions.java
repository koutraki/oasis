package download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
import org.xml.sax.SAXException;

import customization.Constants;
import download.WebFunction.PairValidityAndErrorMessage;

public class ExtractIDsForFunctions {
	private static DocumentBuilderFactory factory; 
	private static DocumentBuilder builder;
	private static final XPathFactory xpathFactory = XPathFactory.newInstance();
	private static final XPath xpath = xpathFactory.newXPath();

  
	public static final void init(){
		 try {
			 	factory = DocumentBuilderFactory.newInstance();
			 	factory.setNamespaceAware(true);
			 	builder = factory.newDocumentBuilder();	 	
		 } catch (ParserConfigurationException e) {
	            e.printStackTrace();
	        }
	}
	
	

	public static ArrayList<PathToIdsSpecification> computeIdsForFunctionsWithSpecifiedIdExtractors() throws Exception{
		ArrayList<PathToIdsSpecification> listSpecs=new ArrayList<PathToIdsSpecification>();
		
		BufferedReader br = new BufferedReader(new FileReader(Constants.fileWithIDExtractorsSpecifications));
		String line;
		while ((line = br.readLine()) != null) {
			line=line.trim();
			String[] splits = line.split(Constants.separatorSpace);
			
			/** if the function has no input type defined, continue **/
			if(splits.length<8) continue;
			
			/** if the site is not among the sites of the working set, continue **/
			String site=splits[1];
			if(DownloadManager.stubs.get(site)==null) continue;
			
			String type=splits[2];
			String file=Constants.getFileWithInputsForType(type);
			File f= new File(file);
			if(!(f.exists() && f.isFile())) {
				System.err.println("Unknown type at line "+line);
				System.err.println("No file "+file+" with input values");
				continue;
			}
			
			String function=splits[0];
			WebFunction proxy=DownloadManager.getWebFunctionForSiteAndFunctionName(site, function);
			if(proxy==null) continue;
			
			String pathToEntity=splits[3];
			String pathToInput=splits[4];
			String pathToId=splits[5];
			String pathToControlData=splits[6];
			String output=splits[7];
			
			PathToIdsSpecification spec=new PathToIdsSpecification(proxy, type, pathToEntity, pathToInput, pathToId, pathToControlData, output);
			listSpecs.add(spec);
			
			/** get the stub of the function & compute samples using as input values from the file associated to the type **/
			/**
			WebFunction F=DownloadManager.stubs.get(site).get(function);
			ArrayList<String> inputs=WebFunction.readInputsFromInputsAndEntitiesFiles(file, 1);
			System.out.println("Function "+function+": I have to execute calls for "+inputs);
			if(F==null){System.out.println("Stub is null **"+site+"**"+function+"**");}
			F.executeCallsAndSetValidInputs(inputs);**/
			
			
		}
		br.close();
		return listSpecs;
	}
	/*********************************************************/
	  /** ClassEntityInXML PathToIdsSpecification **/
	/********************************************************/
	public static final class PathToIdsSpecification{
		WebFunction proxy;
		String inputType;
		String pathToInput; 
		String pathToEntity; 
		String pathToId;
		String pathToControlData;
		String output;
		
		public PathToIdsSpecification(WebFunction proxy, String inputType, 
							String pathToEntity, String pathToInput, String pathToId, String pathToControlData, String output){
			this.proxy=proxy;
			this.inputType=inputType.trim();
			this.pathToEntity=pathToEntity.trim();
			this.pathToInput=pathToInput.trim();
			this.pathToId=pathToId.trim();
			this.pathToControlData=pathToControlData.trim();
			this.output=output.trim();
			
			normalizePathsForExtraction();
		}
		
		public void normalizePathsForExtraction(){
			/** remove the last slash and add a slash in the beginning if none */
			if(pathToEntity.endsWith("/")) pathToEntity= pathToEntity.substring(0, pathToEntity.length()-1);
			if(! pathToEntity.startsWith("/")) pathToEntity= "/"+pathToEntity;
			
			/** all other paths are relative paths; thus they should not start with slash**/
			if(pathToInput.startsWith("/")) pathToInput= pathToInput.substring(1, pathToInput.length());
			if(pathToId.startsWith("/")) pathToId= pathToId.substring(1, pathToId.length());
			if(pathToControlData.startsWith("/")) pathToControlData= pathToControlData.substring(1, pathToControlData.length());
			
			/** write the paths as XPath queries **/
			pathToEntity=toGeneralXPath(pathToEntity);
			pathToInput=appendText(toGeneralXPath(pathToInput));
			pathToId=appendText(toGeneralXPath(pathToId));
			pathToControlData=appendText(toGeneralXPath(pathToControlData));
		}
	
		/** functions transform the paths to XPaths that do not take ignore the namespaces **/
		public String toGeneralXPath(String path){
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
		
		@Override
		public String toString(){
			return 	"*****\n Site: "+proxy.site
					+"\n Function: "+proxy.functionName
					+"\n Input: "+inputType
					+"\n Path to ClassEntityInXML: "+pathToEntity
					+"\n Path to Input: "+pathToInput
					+"\n Path to Id: "+pathToId
					+"\n Path to Control Data: "+pathToControlData
					+"\n Output: "+output;
					
		}
		
	}
	
	/**********************************************************/
			/** DOM XML Extractor
	/**********************************************************/
	private static void getEntityNodes(RecordInput inputRecord, PathToIdsSpecification spec, Document doc,XPath xpath, FileWriter fOut) throws IOException {
		try {
        /** create XPathExpression object */
        XPathExpression expr = xpath.compile(spec.pathToEntity);
        /** evaluate expression result on XML document */
        NodeList entities = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        /** prepare an expression for each property **/
        XPathExpression exprInputPath = xpath.compile(spec.pathToInput);
        XPathExpression exprIdPath = xpath.compile(spec.pathToId);
        XPathExpression exprControlPath = xpath.compile(spec.pathToControlData);
        String newLine = System.getProperty("line.separator");
        
        for (int i = 0; i < entities.getLength(); i++){
        			Node n=entities.item(i);
        			
        			/** the records for the entity **/
        			String input=null;
        			String id=null;
        			String controlData=null;
	
        			/** evaluate expression result on node */
        			NodeList nodes = (NodeList) exprInputPath.evaluate(n, XPathConstants.NODESET);
        			if(nodes!=null && nodes.getLength()>0) input=nodes.item(0).getNodeValue();
        			
        			if(input==null) continue;
        			input=input.replaceAll("\\s+"," ");
        			boolean isInput=(Constants.normalization(input).equalsIgnoreCase(inputRecord.input.replace("+", " ")));
        			
        			if(!isInput) continue;
        			
        			nodes = (NodeList) exprIdPath.evaluate(n, XPathConstants.NODESET);
        			if(nodes!=null && nodes.getLength()>0) id=nodes.item(0).getNodeValue();
        			if(id==null) continue;
        			
        			nodes = (NodeList) exprControlPath.evaluate(n, XPathConstants.NODESET);
        			if(nodes!=null && nodes.getLength()>0) controlData=nodes.item(0).getNodeValue();
        			
        			if(inputRecord.id!=null) return;
        			else{
        				inputRecord.id=id;
        				inputRecord.control=controlData;
        			}
        			
        			System.out.println(input+"\t\t"+id+"\t\t"+controlData);
        			
        	}
        
        /** we output something only if we haven't detected a duplicate **/
        if(inputRecord.id!=null)
        		fOut.write(inputRecord.id+Constants.separatorForInputsFiles+inputRecord.KBentity+Constants.separatorForInputsFiles+inputRecord.input+Constants.separatorForInputsFiles+inputRecord.control+newLine);
		
        
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
   
	}
	
	/*********************************************************/
			 /** processing file with inputs **
	/*********************************************************/
	/** the expected configuration of the file is:
	 *  filed 1: the input 
	 *  filed 2: the entity
	 *  the file may contain other fields but only the first two are mandatory
	 ***/
	public static final ArrayList<RecordInput> readInputRecords(String fileInputs) throws Exception{
		ArrayList<RecordInput> inputs= new ArrayList<RecordInput>();
		BufferedReader br = new BufferedReader(new FileReader(fileInputs));
		String line;
		while ((line = br.readLine()) != null) {
			String inputLine=line.trim();
			String[] splits = inputLine.split(Constants.separatorForInputsFiles);
			
			if(splits.length<2){
				System.err.println("The file "+fileInputs+" should include at least 2 fields");
			}
			/** get the input **/
			String input=splits[0].trim().replaceAll("\\s+","+");
			String KBentity=splits[1].trim();
			inputs.add(new RecordInput(input, KBentity));
			
		}
		br.close();
		return inputs;
	}
	
	public static final class RecordInput{
		final String input;
		final String KBentity;
		String id=null;
		String control=null;
		
		public RecordInput(String input, String KBentity){
			this.input=input;
			this.KBentity=KBentity;
		}
		
		@Override
		public String toString(){
			return input+" "+KBentity;
		}
	}
	

	/*********************************************************/
	
	public static void main(String[] args) throws Exception
    {  
    	  	/** DownloadManager.writeFunctionsAndTheirSitesToFile(Constants.fileWithTheInputTypesOfTheFunctions);**/
		DownloadManager.initStubs();
		
		/** initialize DOM **/
		ExtractIDsForFunctions.init();
		
		/** extract specifications from configuration files **/
		ArrayList<PathToIdsSpecification> listFunctionsForIDExtraction=computeIdsForFunctionsWithSpecifiedIdExtractors();
		System.out.println("Specifications:");
		for(PathToIdsSpecification s: listFunctionsForIDExtraction){
			System.out.println(s.toString());	
			String fileWithInputs=Constants.getFileWithInputsForType(s.inputType);
			System.out.println("The file with the inputs is "+fileWithInputs);
			
			/** prepare file with outputs  **/
			String outputFile=Constants.getFileWithInputsForType(s.output);
			System.out.println("The output file is "+outputFile+" for "+s.output);
			FileWriter fOut = new FileWriter(outputFile);
			
			
			ArrayList<RecordInput> records=readInputRecords(fileWithInputs);
			for(RecordInput r: records){
				/** System.out.println(r.toString());*/
				String callResult=s.proxy.executeCall(r.input,true);
				System.out.println("The file is "+callResult+"**");
				if(callResult==null || !(new File(callResult)).isFile()) continue;
				Document doc=null;
				try{
						doc=builder.parse(callResult);
				}catch (Exception e){
					continue;
				}
				getEntityNodes(r, s, doc,xpath, fOut);
			}
			
			fOut.close();
		}
		System.out.println();
	
		
		
		DownloadManager.storeCacheOnDisk();
		
    }
	
	

}
