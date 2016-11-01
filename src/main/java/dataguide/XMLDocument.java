package dataguide;


import java.util.ArrayList;

import org.xml.sax.Attributes;

/** needs to be properly implemented  ***/
public class XMLDocument extends DataGuide {

	/*****************************************************************************************/
	/** PARSE ELEMENT */
	/****************************************************************************************/
	public void startElement(String uri, String local, String qName, Attributes atts) {
		flushText();
		
		Node n=null;
		/** by construction, our dataguide is never empty **/
		if (!st.empty()) {
			Node parent = (Node) st.peek();
			n=new Node(local,Node.ELEM,parent);
			pushOnTheStack(n);
		}else return;

		/* parse the attributes */
		parseAttributes(n, atts);
	}

	/*****************************************************************************************/
	/** SEARCH */
	/****************************************************************************************/
	
	public ArrayList<Node> getNodesOnThePathFromTheRoot(ArrayList<Node> pathFromDataGuide){
		return null;
	}
	
}
