package download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import customization.Constants;
import dependenciesIO.joinInputInstanceWithOutput.ClassEntityInXML;
import dependenciesIO.joinInputInstanceWithOutput.TreeClasses;

public class WebFunction implements Comparable<WebFunction>
{
	/** counter number of calls executed for all functions **/
	public static int counterCalls=0;
	
	/** function name and the name of the site **/
	public final String functionName;
	public final String site;
	public final String signature; /** this is used for comparison **/
	
	/** informations to call the URL**/
	public final String prefix;
	public final String suffix;
	
	/** inputs and where to store the results **/
	private String dirWithResults=null;
	
	/** the input type**/
	public String type;
	
	/** this field will contain the error message of the last download page */
	public String lastDownloadErrorMessage;
	
	/** cache for calls: the input is the key in the map **/
	HashMap<String, PairValidityAndErrorMessage> historyOfCalls;

	/** valid inputs tested recently **/
	public ArrayList<String> validInputs=null;
	
	/** the site timing object recalling the last time a call has been executed **/
	public SiteMeta timingInfo=null;
	
	/** tree with the result of the alignment **/
	public TreeClasses tree=null;
	public HashMap<String, ClassEntityInXML> classEntities=new HashMap<String, ClassEntityInXML>();

	/** profiles for dummy inputs **/
	public int profile_for_dummy_mix=Constants.INDEF;
	public int profile_for_dummy_integer=Constants.INDEF;
	public int profile_for_dummy_string=Constants.INDEF;
	

	/** signatures for dummy inputs **/
	public String signatureForMix=null;
	public String signatureForInteger=null;
	public String signatureForString=null;
	
	
	/****************************************/
	/** CONSTRUCTORS **/
	/****************************************/
	public WebFunction(String name, String site, String prefix, String suffix) throws Exception{
		String siteDir=Constants.getDirectoryForSite(site);
		String filesDir=siteDir+"/"+name+"/";
		this.dirWithResults=filesDir;
		
		this.prefix=prefix;
		this.suffix=suffix;
		this.functionName=name;
		this.site=site;
		this.signature=site+":"+name;
	
		/** if the directory for the site does not exit, create it **/
		File f= new File(siteDir);
		if(!(f.exists() && f.isDirectory())){
			boolean success = (new File(siteDir)).mkdirs();
			if (!success) {
			    return;
			}
		}
		
		/** if the directory for the function is created, create it **/ 
		f= new File(filesDir);
		if(!(f.exists() && f.isDirectory())){
			boolean success = (new File(filesDir)).mkdirs();
			if (!success) {
			    return;
			}
		}
	
		
		/** load the history of the calls if any **/
		loadCache();
		
		/** print the history of calls **/
		//System.out.println("The calls have been previously tested "+historyOfCalls.keySet());
	}

	
	/****************************************/
	/** If the class is used as key **/
	/****************************************/
	@Override
	public int hashCode(){
		return signature.hashCode();
	}
		
	@Override
	public boolean equals(Object o){
		if (!(o instanceof WebFunction)) {
			return false;
		}
		WebFunction of=(WebFunction)o;
		return signature.equals(of.signature);
	}
	
	/****************************************/
	/** Execute calls  **/
	/****************************************/ 
    /** For Mary: returns the filePath where the result of the call is stored  
     * returns null if problems downloading the file; 
     * the error message is stored in the field (see above) **/
    public String executeCall(String input, boolean wait) {
    			lastDownloadErrorMessage=null;
    			String filePath=getFileFromHistory(input);
    			if(filePath==null  || !filePath.equals("")) return filePath;
    			String url=getURLForInput(input);
			filePath=executeCall(input, url, getPathResultForCall(input), wait);
			return filePath;
    }
    
   
    public String executeCall(String inputValue, String host, String filePath, boolean wait){
    			
    			if(wait) if(System.currentTimeMillis()-timingInfo.timeLastCall<1000) {
    					try{
    						Thread.sleep(1000);
    					}catch (Exception e){e.printStackTrace();}
    			}
    			
    			String file=getFileFromURL(inputValue, host, filePath);
    			timingInfo.timeLastCall=System.currentTimeMillis();
    			return file;
    }
    
    public String getFileFromURL(String inputValue, String host, String filePath) 
    {
    	 	this.lastDownloadErrorMessage=null;
    		if(Constants.offline) return null;
    		
    		/** if the input is an URL then the call is invalid **/
    		String toLowerInput=inputValue.toLowerCase();
    		if(toLowerInput.startsWith("http")){
    			 historyOfCalls.put(inputValue, new PairValidityAndErrorMessage(Boolean.FALSE, "Is URL")); 
    			return null;
    		}
    		
    		/** if the input is an image**/
    		if(toLowerInput.endsWith(".xml") || toLowerInput.endsWith(".jpg") 
    				|| toLowerInput.endsWith(".htm") 
    				|| toLowerInput.endsWith(".html")){
    			   historyOfCalls.put(inputValue, new PairValidityAndErrorMessage(Boolean.FALSE, "Is a file")); 
    			return null;
    		}
    		
    		
    		counterCalls++;
    		String newLine = System.getProperty("line.separator");
    		BufferedReader in=null;
        Writer writer = null;
      
        System.out.println("Download file "+host);
        try
        {
            URL url = new URL(host);
            URLConnection conn = url.openConnection();
            // fake a request coming from a browser in order to avoid error 403 (from discogs for instance)
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
            //conn.setRequestProperty("Accept-Charset", "UTF-8");
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           
            /** I remove empty lines until the first character **/
            String line;
            while ((line = in.readLine()) != null ) {
                line=line.trim();
                if(!line.equals("")) break;
            }
           
            /** create a string writer if JSON detected **/
            boolean isJSONData=false;
            if(line!=null){
          	  	if(line.startsWith("{")) {
          	  		System.out.println("JSON detected");
          	  		writer = new StringWriter();
          	  		isJSONData=true;
          	  	}else {
          	  		writer = new FileWriter(filePath);
          	  	}
          	  	writer.write(line+newLine);
            }
            System.out.println(line);
            
            /** write the rest of the input file **/
            while ((line = in.readLine()) != null) {
          	  	   writer.write(line+newLine);
          	  	   System.out.println(line);
            }
            writer.flush();
            
            /** if it's json data do the transformation **/
            if(isJSONData) JSONToXML.transformToXML(((StringWriter)writer).toString(), filePath);
            
            
            /** if the download is a success, add the input to the list of valid inputs for which a file is stored **/
            historyOfCalls.put(inputValue, new PairValidityAndErrorMessage(Boolean.TRUE));
            return filePath;
        }
        catch (IOException e)
        {
            System.out.println("Error download: **"+e.getMessage()+"**"+filePath+"**");
            this.lastDownloadErrorMessage=e.getMessage();
            
            /** add the input to the list of invalid invalid inputs **/
            String msg=(e.getMessage()==null)?null:e.getMessage().replaceAll("[\n\r]", " ");
            historyOfCalls.put(inputValue, new PairValidityAndErrorMessage(Boolean.FALSE, msg)); 
            return null;
        }
        catch (Exception e){
      	  	System.out.println("Error while converting JSON to XML "+ e.getMessage());
          this.lastDownloadErrorMessage=e.getMessage();
      	  	return null;
        }
        finally
        {
            try
            {
                if(writer!=null) writer.close();
                if(in!=null) in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /*********************************/
    /** Cache management **/
    /********************************/
    
    public String getFileFromHistory(String input){
    		String filePath=getPathResultForCall(input);
		File f= new File(filePath);
		if(f.exists()) {
			/** add the call to the cache **/
			historyOfCalls.put(input, new PairValidityAndErrorMessage(Boolean.TRUE));
			return filePath;
		}
		
		/** if it's a previously known invalid call, return null **/ 
		if(historyOfCalls.get(input)!=null && !historyOfCalls.get(input).validity) {
			if(Constants.debugMessages) System.out.println("The input "+input+" has been already checked as being invalid ");
			return null;
		}
		if(Constants.debugMessages) System.out.println("The input "+input+" does not appear in the history"); 
		
		if(Constants.offline){
			if(Constants.debugMessages) System.out.println(" Working offline: I do not download files ");
			return null;
		}
		
		/** returns the empty sting if a call must be issued **/
		return "";
		
    }
    
    
    
  	public void storeCache() throws Exception{
  		if(historyOfCalls.keySet().size()==0) return;
  		
  		String filePath=Constants.getHistoryOfCallsFileForFunction(site, functionName);
  		FileWriter fOut = new FileWriter(filePath);
		String newLine = System.getProperty("line.separator");
		
		for(String input: historyOfCalls.keySet()){
			StringBuffer line=new StringBuffer();
			line.append(input);
			line.append(Constants.separatorForInputsFiles);
			
			PairValidityAndErrorMessage valMdg=historyOfCalls.get(input);
			Boolean wasValidAnswer=valMdg.validity;
			if(wasValidAnswer) line.append(Constants.VALID);
			else {
				line.append(Constants.INVALID);
				line.append(Constants.separatorForInputsFiles);
				line.append(valMdg.errorMessage);
			}
			fOut.write(line.toString()+newLine);
		}
		fOut.close();
  	}
    
  	public void loadCache() throws Exception{
  		historyOfCalls= new HashMap<String, PairValidityAndErrorMessage>();
  		
  		String filePath=Constants.getHistoryOfCallsFileForFunction(site, functionName);
  		
  		/** if no file with the history return **/
  		File  file= new File(filePath);
  		if(!file.exists()) {
  			System.out.println("The file with the hisory does not exit: "+filePath);
  			return;
  		}
  		
  		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line;
		while ((line = br.readLine()) != null) {
		   line=line.trim();
		   String[] splits = line.split(Constants.separatorForInputsFiles);
		   if(splits.length<2) continue;
		   String input=splits[0];
		   Boolean valid=(splits[1].equalsIgnoreCase(Constants.VALID))?Boolean.TRUE: Boolean.FALSE;
		   String msg=(splits.length>=3)?splits[2]:null;
		   historyOfCalls.put(input, new PairValidityAndErrorMessage(valid,msg));
		}
		br.close();
  	}
  	
  	public static final class PairValidityAndErrorMessage{
  		public Boolean validity;
  		public String errorMessage;
  		
  		public PairValidityAndErrorMessage(Boolean validity, String errorMessage){
  			this.validity=validity;
  			this.errorMessage=errorMessage;
  		}
  		
  		public PairValidityAndErrorMessage(Boolean validity)
  		{
  			this.validity=validity;
  			this.errorMessage=null;
  		}
  	}
  	
  	/***************************************************/
  	/** The only functions that replace spaces by + **/
  	/**************************************************/
    public String getPathResultForCall(String input){
  		return dirWithResults+Constants.transformStringForURL(input)+".xml";
  	}
  	
    private String getURLForInput(String input){
  		if(input==null | input.length()==0) return null;
  		return prefix+Constants.transformStringForURL(input)+suffix;
  	}
  	
    public static void main(String[] args) throws Exception
    {  
    		
    }


	@Override
	public int compareTo(WebFunction o) {
		return this.signature.compareTo(o.signature);
	}
}
