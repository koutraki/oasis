package dependenciesIO.detectErrorMessage;

import org.xml.sax.Attributes;

import dataguide.DataGuide;
import dataguide.Node;

public class DataGuideWithSignature extends DataGuide {

	
	/** signature **/
	public StringBuffer signature= new StringBuffer();
	
	@Override
	public void startElement (String uri, String local, String qName, Attributes atts)
	{
		super.startElement(uri, local, qName, atts);
		XMLDocSignature.addStartElement(signature, local);
	}
	
	public void endElement(String uri, String localName, String qName) 
	{
		super.endElement(uri, localName, qName);
		XMLDocSignature.addEndElement(signature, localName);
	}
	
	public void parseAttributes(Node parent, Attributes attribs)
  	{ 
		super.parseAttributes(parent, attribs);
		for ( int i=0;i<attribs.getLength();i++)
    		{
    			String local=attribs.getLocalName(i);  
    			String value=attribs.getValue(i);
    			XMLDocSignature.addAttributeName(signature, local);
    		}
  	}
	
	public String getSignature(){
		return signature.toString();
	}
	
}
