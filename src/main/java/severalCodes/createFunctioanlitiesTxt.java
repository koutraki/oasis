package severalCodes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class createFunctioanlitiesTxt {

	public static void parseFunctinalitiesFile(BufferedWriter out, String path) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = (Document) docBuilder.parse(new File(path));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doc.getDocumentElement().normalize();

		NodeList listOffunctionalities = doc
				.getElementsByTagName("functionality");
		int totalfunc = listOffunctionalities.getLength();

		for (int s = 0; s < listOffunctionalities.getLength(); s++) {

			Node firstFunctionalityNode = listOffunctionalities.item(s);
			if (firstFunctionalityNode.getNodeType() == Node.ELEMENT_NODE) {

				Element firstFunctionalityElement = (Element) firstFunctionalityNode;

				NodeList objectList = firstFunctionalityElement
						.getElementsByTagName("object");
				Element objectElement = (Element) objectList.item(0);
				NodeList textobjectList = objectElement.getChildNodes();
				// -------
				NodeList valueList = firstFunctionalityElement
						.getElementsByTagName("value");
				Element valueElement = (Element) valueList.item(0);
				NodeList textvalueList = valueElement.getChildNodes();
				// ---------
				NodeList inverseList = firstFunctionalityElement
						.getElementsByTagName("inverse");
				Element inverseElement = (Element) inverseList.item(0);
				NodeList textinverseList = inverseElement.getChildNodes();

				try {
					out.write(((Node) textobjectList.item(0)).getNodeValue()
							.trim()+"\t"+((Node) textvalueList.item(0)).getNodeValue()
							.trim()+ "\t"+((Node) textinverseList.item(0))
							.getNodeValue().trim()+"\n");
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				addFunctionality(((Node) textobjectList.item(0)).getNodeValue()
//						.trim(), ((Node) textvalueList.item(0)).getNodeValue()
//						.trim(), false);
//				addFunctionality(((Node) textobjectList.item(0)).getNodeValue()
//						.trim(), ((Node) textinverseList.item(0))
//						.getNodeValue().trim(), true);

			}// end of if clause
		}// end of for

	}
	
	static BufferedWriter CreateFile(String filePath) {

		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(filePath);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);

			} else {
				out = new BufferedWriter(new FileWriter(file));

			}

			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;

	}

	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedWriter out = CreateFile("/Users/mary/Dropbox/OASIS/Mary-Data/BNF/config/BNF_functionalities.txt");
		parseFunctinalitiesFile(out, "/Users/mary/Dropbox/OASIS/Mary-Data/BNF/config/functionality_predicates.xml");
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
