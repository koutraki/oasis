package severalCodes;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import customization.Constants;

public class SamlpesWithDiedOnDate {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int diedOnDate = 0;

		File folder = new File("/Users/mary/Dropbox/OASIS/Mary-Data/functions/music_brainz/getArtistInfoByName/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {

				try {

					DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docBuilderFactory
							.newDocumentBuilder();
					Document doc = docBuilder
							.parse(new File("/Users/mary/Dropbox/OASIS/Mary-Data/functions/music_brainz/getArtistInfoByName/"+
								file.getName()));

					System.out.println(file.getName());
					// normalize text representation
					doc.getDocumentElement().normalize();
					

					NodeList listOfBooks = doc.getElementsByTagName("artist");
					int totalBooks = listOfBooks.getLength();
					System.out.println("Total no of artists : " + totalBooks);

					Node firstBookNode = listOfBooks.item(0);
					if (firstBookNode.getNodeType() == Node.ELEMENT_NODE) {

						Element firstElement = (Element) firstBookNode;

						// -------
						NodeList firstNameList = firstElement
								.getElementsByTagName("life-span");
						Element firstNameElement = (Element) firstNameList
								.item(0);

						NodeList end = firstNameElement
								.getElementsByTagName("ended");
						Element ls = (Element) end.item(0);
						if (ls.getChildNodes().item(0).getNodeValue()
								.equals("true")) {
							System.out.println("true\n\n");
							diedOnDate++;
						}

					}

				} catch (SAXParseException err) {
					System.out.println("** Parsing error" + ", line "
							+ err.getLineNumber() + ", uri "
							+ err.getSystemId());
					System.out.println(" " + err.getMessage());
				} catch (SAXException e) {
					Exception x = e.getException();
					((x == null) ? e : x).printStackTrace();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}//if
		}//for
		
		System.out.println("\n\n\n DIED ON DATE: "+diedOnDate);

	}

}
