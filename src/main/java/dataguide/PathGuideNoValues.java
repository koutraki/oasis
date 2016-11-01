package dataguide;

import customization.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import transformWSResults.WSResultsToTriples;
import transformWSResults.transformXMLFilesUsingXSLTcode;
import static transformWSResults.transformXMLFilesUsingXSLTcode.executeXSLT;
import static transformWSResults.transformXMLFilesUsingXSLTcode.getInputTypeForWS;
import static transformWSResults.transformXMLFilesUsingXSLTcode.getInputs;
import transformation.Triple;

/**
 * This structure stores only the paths It does not store the textual values
 */
public class PathGuideNoValues extends DataGuide {

    LinkedHashMap<String, EntityClass> classes = new LinkedHashMap<String, EntityClass>();
    
    static String xsltCode;

     int counter = 0;
    ArrayList<Triple> triples = new ArrayList<Triple>();
                
    @Override
    protected void addValueNode(Node parent, String value) {

    }

    @Override
    public void parseAttributes(Node parent, Attributes attribs) {
        for (int i = 0; i < attribs.getLength(); i++) {
            String local = attribs.getLocalName(i);
            String value = attribs.getValue(i);
            Node sonNode = parent.getOrCreate("@" + local, Node.ATT);
            if (value != null) {
                value = value.trim();
                if (value.length() > 0) {
                    /**
                     * normalize the text *
                     */
                    value = value.replaceAll("\\s+", " ");
                    addValueNode(sonNode, value);
                    if (!nodesWithTextValues.contains(sonNode)) {
                        nodesWithTextValues.add(sonNode);
                    }
                }
            }
        }

    }

    public void parseTheStructureOfXMLFile(DataGuide dg1) {

        //   LinkedHashMap<String, EntityClass> classes = new LinkedHashMap<String, EntityClass>();
        EntityClass root = new EntityClass(dg1.root);
        classes.put("/", root);

        for (Node n : dg1.nodesWithSiblingsWithTheSameName) {
            EntityClass c = new EntityClass(n);
            classes.put(n.getStringPathRootToNode(), c);
        }

        for (EntityClass c : classes.values()) {
            Node parent = c.node.parent;
            String buff = c.node.name;

            //      System.out.println(c.node.name + " has parent  node " + parent + " " + ((parent != null) ? parent.currentSiblings : 0));
            while (parent != null && parent != dg1.root && !dg1.nodesWithSiblingsWithTheSameName.contains(parent)) {
                if (buff == null) {
                    buff = parent.name;
                } else {
                    buff = parent.name + "/" + buff;
                }
                parent = parent.parent;

            }

            if (parent != null && dg1.nodesWithSiblingsWithTheSameName.contains(parent)) {
                EntityClass cp = classes.get(parent.getStringPathRootToNode());
                c.parentClass = cp;
                c.pathToParentClass = buff;

            } else if (parent == dg1.root) {
                c.parentClass = root;
                c.pathToParentClass = c.node.getStringPathRootToNode();

            }
        }

        for (EntityClass c : classes.values()) {
            //     System.out.println(c);
            for (EntityClass c1 : classes.values()) {
                if (c1.parentClass != null) {
                    if (c1.parentClass.node.equals(c.node)) {
                        c.pathsToChildClasses.add(c1.pathToParentClass);
                    }
                }
            }
        }

        for (Node n : dg1.nodesWithTextValues) {
            /**
             * find the parent *
             */
            //        System.out.println("Annalysing " + n.name);
            if (n.currentSiblings > 1) {
            //    System.out.println("    class " + n.getStringPathRootToNode());
                classes.get(n.getStringPathRootToNode()).pathsWithValuesInTheSubtree.add(n.name);
                continue;
            }

            Node parent = n.parent;
            String buff = n.name;
            while (parent != null && !dg1.nodesWithSiblingsWithTheSameName.contains(parent)) {
                buff = parent.name + "/" + buff;
                parent = parent.parent;
            }

            if (parent == null) {
                root.pathsWithValuesInTheSubtree.add(n.getStringPathRootToNode().substring(0, n.getStringPathRootToNode().length() - 1));
            } else {
                //     System.out.println("   Found parent " + parent.name);
                classes.get(parent.getStringPathRootToNode()).pathsWithValuesInTheSubtree.add(buff);
            }
        }

        for (EntityClass c : classes.values()) {
            System.out.println(c);
        }

    }


    public String createObject(String WS, String API) {
     //   this.counter = this.counter++;
        return "<http://" + WS + "/" + API + "/" + ++counter + ">";
    }

    public String createLiteral(String literal) {
        return "\"" + literal + "\"";
    }

    public String createPredicate(String WS, String API, String path) {
        return "<" + API + "/" + WS + "/" + path + ">";
    }

    public void createXSLTCode() {
        xsltCode = new String();
        xsltCode = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"> \n"
                + " \n"
                + "<xsl:template match=\"/\">\n";

        ArrayList<EntityClass> queue = new ArrayList<EntityClass>();
        EntityClass c = classes.get("/"); // this is root node! We start from root!
        while (c != null) {

            if (!c.node.name.equals("root")) {
                String[] pathPart = c.pathToParentClass.split("/");
                xsltCode += "<xsl:for-each select=\"";
                for (String pathPart1 : pathPart) {
                    xsltCode += "*[local-name()='" + pathPart1 + "']/";
                }
                xsltCode = xsltCode.substring(0, xsltCode.length() - 1);
                xsltCode += "\">\nE:" + c.node.name + ":" + c.pathToParentClass + ":" + c.parentClass.node.name + "\n{\n";

            } else {
                xsltCode += "<xsl:for-each select=\"/\">\nE:root:" + c.pathToParentClass + ":null\n{\n";
            }

            for (String textEntitiesPath : c.pathsWithValuesInTheSubtree) {
                String[] paths = textEntitiesPath.split("/");

                xsltCode += textEntitiesPath + "==<xsl:for-each select=\"";
                for (String path1 : paths) {
                    if (path1.startsWith("@")) {
                        xsltCode += "@*[local-name()='" + path1.replaceFirst("@", "") + "']/";
                    } else {
                        xsltCode += "*[local-name()='" + path1 + "']/";
                    }
                }
                xsltCode = xsltCode.substring(0, xsltCode.length() - 1);
                xsltCode += "\"><xsl:value-of  select=\".\"/></xsl:for-each>\n";
            }

            for (String StringEntitiesPath : c.pathsToChildClasses) {
                for (EntityClass entity : classes.values()) {
                    if (StringEntitiesPath.equals(entity.pathToParentClass)) { // is the child entity
                        queue.add(entity);
                    }
                }
            }

            if (!queue.isEmpty()) {
                c = queue.get(0);
                queue.remove(0);
            } else {
                for (int i = 0; i < classes.size(); i++) {
                    xsltCode += "}</xsl:for-each>\n";
                }
                // xsltCode+="}</xsl:for-each>";
                break;
            }
        }

        xsltCode += "</xsl:template>\n"
                + "</xsl:stylesheet>";

    }

    public static void xsltToFile(String filePath) {

        FileWriter fstream;
        BufferedWriter out = null;
        try {

            File file = new File(filePath);
            if (!file.exists()) {
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);
                out.write(xsltCode);
            } else {
                out = new BufferedWriter(new FileWriter(file));
                out.write(xsltCode);
            }

            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void executeXSLT(String xmlFilePath, String xslCode, String resutl) {

        Source xmlInput = new StreamSource(new File(xmlFilePath));
        Source xsl = new StreamSource(new File(xslCode));
        Result xmlOutput = new StreamResult(new File(resutl));

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xsl);
            transformer.transform(xmlInput, xmlOutput);
        } catch (TransformerException e) {
            // Handle.
        }
    }

    public void parseResultFileToCreateTriples(String filePath, String WS, String API) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String sCurrentLine;
            int n = 0;
            boolean inblock = false;
            HashMap<String, String> entitiesAndUris = new HashMap<String, String>();
            String subjectUri = null;
            while ((sCurrentLine = br.readLine()) != null) {
                if (n == 0) {
                    n = 1;
                    continue;
                }

                if (sCurrentLine.startsWith("E:")) {
                    //we have new entity...

                    subjectUri = createObject(WS, API);
                    //System.out.println(sCurrentLine);
                    if (!sCurrentLine.split(":")[1].equals("root")) {
                        String predicatePath = sCurrentLine.split(":")[2];
                        entitiesAndUris.put(sCurrentLine.split(":")[1], subjectUri);
                        triples.add(new Triple(entitiesAndUris.get(sCurrentLine.split(":")[3]), createPredicate(WS, API, predicatePath), subjectUri));
                    } else {
                        //     System.out.println("mpike");
                        entitiesAndUris.put("root", subjectUri);
                    }
                } else if (!sCurrentLine.startsWith("{") && !sCurrentLine.startsWith("}")) { //result line
                    String[] result = sCurrentLine.split("==");
                    if (result.length < 2) {
                        continue;
                    } else {
                        triples.add(new Triple(subjectUri, createPredicate(WS, API, result[0]), createLiteral(result[1])));
                    }
                }

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PathGuideNoValues.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PathGuideNoValues.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeTriplesToFile(String filePath) {
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
            for (Triple tr : triples) {
                out.write(tr.subject + "\t" + tr.predicate + "\t" + tr.object + " .\n");
            }

            //  out.write(this.totalNtTriplesForViewinString);
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(WSResultsToTriples.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public  void getInputs(String WS, String API, String inputsFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.dirWithInputs + "/100Inputs/" + inputsFilePath))) { /* Testing with 20 inputs!!!! It is manual!!!*/


            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {

                String[] line = sCurrentLine
                        .split(Constants.separatorForInputsFiles);
                String resultFileName = line[0].replace(" ", "+") + ".xml";

                String xmlFilePath = Constants.dirWithFunctions + API + "/" + WS + "/" + resultFileName;

                /**
                 * ******
                 */
                DataGuide dg1 = new DataGuide();
                dg1.makeparse(xmlFilePath);
                dg1.reInitMap();
                
                classes = new LinkedHashMap<String, EntityClass>();
                
                parseTheStructureOfXMLFile(dg1);
                createXSLTCode();
                xsltToFile("/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/xsltCodes/"+WS+"_"+API+"_"+resultFileName+".xslt");
                executeXSLT(xmlFilePath, "/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/xsltCodes/"+WS+"_"+API+"_"+resultFileName+".xslt", "/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/xsltResults/"+WS+"_"+API+"_"+resultFileName+".txt");
                parseResultFileToCreateTriples("/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/xsltResults/"+WS+"_"+API+"_"+resultFileName+".txt", WS, API);
                /**
                 * *******
                 */
                System.out.println(xmlFilePath);

         

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(transformXMLFilesUsingXSLTcode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(transformXMLFilesUsingXSLTcode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {

        File folder = new File(Constants.xsltDirectory);

        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                
                
                
                String fileName = file.getName();
                if (fileName.equals(".DS_Store")) {
                    continue;
                }
                System.out.println(fileName);

                String[] fileNameParts = fileName.split("_");

                String WS;
                String API;
                if (fileNameParts.length == 3) {
                    WS = fileNameParts[0];
                    API = fileNameParts[1];
                } else { // we supose length>3
                    WS = fileNameParts[0];
                    API = fileNameParts[1] + "_" + fileNameParts[2];
                }
                String inputType = getInputTypeForWS(WS, API);

                String inputTypesFileName = "100_" + inputType + "_entities.txt";

                PathGuideNoValues dg1 = new PathGuideNoValues();

                dg1.getInputs(WS, API, inputTypesFileName);

           //     dg1.makeparse("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/functions/music_brainz/getArtistInfoByName/Bono.xml");
            //    dg1.reInitMap();

            //    dg1.parseTheStructureOfXMLFile(dg1);
            //    dg1.createXSLTCode();
            //    dg1.xsltToFile("/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/getArtistInfoByName_music_brainz.xslt");
            //    dg1.executeXSLT("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/functions/music_brainz/getArtistInfoByName/Bono.xml", "/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/getArtistInfoByName_music_brainz.xslt", "/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/getArtistInfoByName_music_brainzResult.txt");
            //    dg1.parseResultFileToCreateTriples(triples,"/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/getArtistInfoByName_music_brainzResult.txt", WS, API , counter);
                dg1.writeTriplesToFile("/Users/mary/Dropbox/OASIS/Mary-Data/Competitor/triples/"+WS+"_"+API+"_Triples.nt");

            }
        }

    }

}
