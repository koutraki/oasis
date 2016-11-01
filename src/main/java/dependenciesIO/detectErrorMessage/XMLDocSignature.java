/**
 * @author Nicoleta Preda (andapreda@gmail.com)
 */
package dependenciesIO.detectErrorMessage;
import java.io.File;
import java.io.FileInputStream;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import dataguide.Node;

/* a bug: in the case there are attributes with the same name for different nodes or an attribute and an element with the same name: bug*/
/* so add the type and the name of the element for the attributes*/
public class XMLDocSignature extends DefaultHandler{
		
	/** structures needed for the construction of the dataguide **/
	protected StringBuffer signature=new StringBuffer();

	/** **/
	public XMLDocSignature()
	{	}
	
	
	/*****************************************************************************************/
							/**	PARSE ELEMENT	*/
	/****************************************************************************************/
	public void startElement (String uri, String local, String qName, Attributes atts)
	{
		addStartElement(signature, local);	
	}
	
	public static final void addStartElement(StringBuffer sig, String local){
		sig.append("<"+local+">");
	}
	
	public void endElement(String uri, String localName, String qName) 
	{
		addEndElement(signature, localName);	
	}
	
	public static final void addEndElement(StringBuffer sig, String local){
		sig.append("</"+local+">");
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
    			addAttributeName(signature, local);
    		}
  	}
	
	public static final void addAttributeName(StringBuffer sig, String local){
		sig.append(local);
	}
	
	/*****************************************************************************************/
							/**	PARSE TEXT	*/
	/****************************************************************************************/
	public void characters (char[] ch, int start, int length)  {
		
  	}
	
	/*****************************************************************************************/
	/**	TO STRING	*/
	/****************************************************************************************/
	public String toString(){
		return signature.toString();
	}
	
	public String getSignature(){
		return signature.toString();
	}
	/*****************************************************************************************/
								/**Make	Parse	*/
	/****************************************************************************************/
	public boolean makeparse(String file) 
  	{try{
      		SAXParser parser=new SAXParser();
     
      		System.setProperty( "org.xml.sax.driver","org.apache.xerces.parsers.SAXParser"	 );
   	   	     	  
   	  		parser.setContentHandler(this);
   	  		parser.parse(new InputSource(new FileInputStream(new File(file))));
   	  	
   	  		return true;
  		
  		}catch(Exception e){
  			 return false;
  		}
  	}
	
	public static void main(String[] args) throws Exception
	{
		
	}    
}
