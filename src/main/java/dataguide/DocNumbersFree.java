package dataguide;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParserFactory;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import customization.Constants;


public class DocNumbersFree extends DefaultHandler {


	StringBuffer document=new StringBuffer();
	StringBuffer characters=new StringBuffer();
	String input=null;
	
	public final void reInit(String input){
		document=new StringBuffer();
		characters=null;
		this.input=(input==null)?"???":input;
	}
	
	/*****************************************************************************************/
	/**	PARSE ELEMENT	*/
	/****************************************************************************************/
	public void startElement (String uri, String local, String qName, Attributes attribs)
	{
		flushText();
		
		document.append("<"+local);
		/** parse the attributes**/
		for ( int i=0;i<attribs.getLength();i++)
		{
    			String name=attribs.getLocalName(i);  
    			String value=attribs.getValue(i);
    			if(value!=null) {
    				value=value.trim();
    				if(value.length()>0 && !value.matches("(\\d|\\p{Punct}|\\s)+") && !value.contains(input)){ 
    								/** normalize the text **/
    								value=value.replaceAll("\\s+"," ");
    								document.append(" "+name+"="+value);
    								}
    				}
    		}	
		document.append(">");
	}

	public void endElement(String uri, String localName, String qName) 
	{
		document.append("</"+localName+">");
		flushText();
	}	


	
	/*****************************************************************************************/
	/**	PARSE TEXT	*/
	/****************************************************************************************/
	public void characters (char[] ch, int start, int length)  {
		if(characters==null) characters=new StringBuffer();
		characters.append(ch,start,length);
	}
	
	public void flushText(){
		if(characters==null) return;
		String value=characters.toString().trim();
		if(value.matches("(\\d|\\p{Punct}|\\s)+")) return;
		if(value.contains(input)) return;
		if(value.length()<1) return;

		/** normalize the text **/
		value=value.replaceAll("\\s+"," ");
		if(value.contains(input)) return;
		
		/** if simply the text contains the part of input as it appears in the url*/
		if(value.contains(Constants.transformStringForURL(input)));
		
		document.append(value);
		characters=null;
	}

	
	/*****************************************************************************************/
	/**	TO STRING	*/
	/****************************************************************************************/
	public String toString(){
		return document.toString();
	}


	/*****************************************************************************************/
	/**	LOAD FILE TO PARSE	*/
	/****************************************************************************************/

	public boolean makeparse(String file) 
  	{try{
  			SAXParserFactory factory = SAXParserFactory.newInstance();
      		SAXParser parser=new SAXParser();
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
   	  		//System.err.println("File "+file+"is a correct XML");
   	  		return true;
  		
  		}catch(Exception e){
  			//System.err.println("File "+file+"is not a correct XML");
  			return false;
  		}	
  	}
	
	
	public static void main(String[] args) throws Exception
	{
		
		DocNumbersFree doc=new DocNumbersFree();
		doc.makeparse("/Users/adi/Dropbox/OASIS/Nico-Data/functions/library_thing/getBookInfoById/1001+Books+You+Must+Read+Before+You+Die+%282006+Edition%29.xml");
		System.out.println("*"+doc.toString()+"*");
	}   
}
