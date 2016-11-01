package graphguide;

import customization.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Functionality {

    public static HashMap<String, Double> functionalities;
    public static HashMap<String, Double> inverseFunctionalities;

    public Functionality() {

        functionalities = new HashMap<String, Double>();
    }

    public void parseFunctinalitiesFile(String path) {
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

                addFunctionality(((Node) textobjectList.item(0)).getNodeValue()
                        .trim(), ((Node) textvalueList.item(0)).getNodeValue()
                        .trim(), false);
                addFunctionality(((Node) textobjectList.item(0)).getNodeValue()
                        .trim(), ((Node) textinverseList.item(0))
                        .getNodeValue().trim(), true);

            }// end of if clause
        }// end of for

    }

    /**
     * ***********************************************************
     */
    public void addFunctionality(String property, String functionalityString,
            boolean inverse) {

        if (property.startsWith("y:")) {
            property = property.replace("y:",
                    "<http://yago-knowledge.org/resource/");
            property = property + ">";
        } else if (property.startsWith("u:")) {
            property = property.replace("u:", "rdfs:");
        } else if (property.startsWith("w:")) {
            property = property.replace("w:", "rdf:");
        }
        if (inverse == true) {
            property = property + "-";
        }
        double functionality = Double.parseDouble(functionalityString);

        functionalities.put(property, new Double(functionality));

    }

    public void parseFunctionalitiesTXTFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                String[] line = sCurrentLine
                        .split(Constants.separatorSpace);
                
                functionalities.put(line[0], Double.parseDouble(line[1]));
                functionalities.put(line[0]+"-", Double.parseDouble(line[2]));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Functionality.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * *************************************************************
     */
    public void printFunctionality() {
        Iterator it = functionalities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.print(pairs.getKey());
            System.out.println(pairs.getValue());
        }
    }
    
    public static void main(String[] args){
        Functionality f = new Functionality();
        f.parseFunctionalitiesTXTFile(Constants.functionality);
        f.printFunctionality();
    }

}
