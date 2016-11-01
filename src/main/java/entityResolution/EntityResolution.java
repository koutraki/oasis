package entityResolution;

import graphguide.Functionality;
import graphguide.InputTypesObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import knowledgebase.QueryYAGOTDB;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import customization.Constants;

public class EntityResolution {

	String function;
	String webSite;
	String PathToEntityXML; // candidate entity
	public ArrayList<CandidatePairXMLToOntolotyProperty> candidatePairsOfProperties = new ArrayList<CandidatePairXMLToOntolotyProperty>();

	public static String htmlTable;

	public static QueryYAGOTDB queryYago = new QueryYAGOTDB();

	static Functionality functionality = new Functionality();

	void parseCandidateFileOfXMLFunction(String filepath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;
			int tmp = 0;
			int inputFound = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				if (tmp == 0) {
					String[] lineparts = sCurrentLine
							.split(Constants.separatorSpace);
					this.function = lineparts[1];
					this.webSite = lineparts[2];
					tmp = 1;
				} else {
					if (!sCurrentLine.isEmpty()) {
						String[] lineparts = sCurrentLine
								.split(Constants.separatorSpace);
						if (lineparts[0].equals("C:")) {
							inputFound = 0;
							if (lineparts[1].equals("{input}")) {
								this.PathToEntityXML = lineparts[2];
								// normalizeEntityPathXML();
								// this.PathToEntityXML =
								// toGeneralXPath(this.PathToEntityXML);
								inputFound = 1;
							}
						} else if (lineparts[0].equals("R:") && inputFound == 1) {
							// String xpathProperty =
							// normalizePropertyPathXML(lineparts[2]);
							// xpathProperty =
							// appendText(toGeneralXPath(xpathProperty));
							CandidatePairXMLToOntolotyProperty pair = new CandidatePairXMLToOntolotyProperty(
									lineparts[1], lineparts[2]);
							this.candidatePairsOfProperties.add(pair);
						}
					} else {
						// do nothing
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static HashMap<String, ArrayList<InputTypesObject>> parseInputTypesFile(
			String filepath) {
		HashMap<String, ArrayList<InputTypesObject>> inputTypesOfFunctions = new HashMap<String, ArrayList<InputTypesObject>>();
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;

			int tmp = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				if (tmp == 0) {
					tmp++;
					continue;
				} else {
					if (!sCurrentLine.isEmpty()) {
						String[] lineColumns = sCurrentLine
								.split(Constants.separatorSpace);
						System.out.println(lineColumns.length);
						if (lineColumns.length >= 3) {
							ArrayList<InputTypesObject> functionsAndInputs;
							InputTypesObject functionAndType;
							if (inputTypesOfFunctions
									.containsKey(lineColumns[1])) {
								functionsAndInputs = inputTypesOfFunctions
										.get(lineColumns[1]);

							} else {
								functionsAndInputs = new ArrayList<InputTypesObject>();
							}
							functionAndType = new InputTypesObject(
									lineColumns[0], lineColumns[2]);
							functionsAndInputs.add(functionAndType);
							inputTypesOfFunctions.put(lineColumns[1],
									functionsAndInputs);

						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputTypesOfFunctions;
	}

	void findInputValuesFileForThefunction(
			HashMap<String, ArrayList<InputTypesObject>> inputTypesOfFunctions) {
		System.out.println("function:" + this.function + "\t webService:"
				+ this.webSite);

		ArrayList<InputTypesObject> functionsAndInputTypes = inputTypesOfFunctions
				.get(this.webSite);

		String inputType = null;
		for (InputTypesObject inputTypesObject : functionsAndInputTypes) {
			if (inputTypesObject.function.equals(this.function)) {
				inputType = inputTypesObject.inputType;
			}
		}
		System.out.println(inputType);

		String inputValuesPath = Constants.getFileWithInputsForType(inputType);
		System.out.println("path:" + inputValuesPath);

		try (BufferedReader br = new BufferedReader(new FileReader(
				inputValuesPath))) {

			/** for each input value **/
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.isEmpty()) {
					String[] lineColumns = sCurrentLine
							.split(Constants.separatorForInputsFiles);
					String ontologyEntity = lineColumns[1];
					String xmlInput = lineColumns[0].replace(" ", "+");

					String pathToXMLInputFile = Constants.dirWithFunctions
							+ this.webSite + "/" + this.function + "/"
							+ xmlInput;

					File file = new File(pathToXMLInputFile);
					if (!file.exists()) {
						System.out.println("Xml File " + pathToXMLInputFile
								+ " Does not Exist!");
						continue;
					}

					HashMap<String, ArrayList<XMLPathValuePair>> xmlEntityPropertiesValues = xqueryXMLFile(pathToXMLInputFile);
					ArrayList<OntologyPropertyValue> ontologyPropertiesAndValues = queryYago(ontologyEntity);

					/**
					 * for each input value i should calculate the confidence
					 * for the entity resolution
					 **/

					Iterator it = xmlEntityPropertiesValues.entrySet()
							.iterator();
					HashMap<String, Float> tempEntityConfidence = new HashMap<String, Float>();
					while (it.hasNext()) {
						Map.Entry pairs = (Entry) it.next();
						ArrayList<XMLPathValuePair> xmlPropertiesValues = (ArrayList<XMLPathValuePair>) pairs
								.getValue();
						float confidence = entityResolutionConfidence(
								ontologyPropertiesAndValues,
								xmlPropertiesValues, ontologyEntity);

						System.out.println("XML Entity " + pairs.getKey()
								+ "\t resolved to entity: " + ontologyEntity
								+ "\tWith confidence: " + confidence);

						if (confidence > 0.0) {
							System.out.println("Confidence grater zero !");
							tempEntityConfidence.put((String) pairs.getKey(),
									confidence);
							// add to a temp array... to count the number and
							// add the lines in the table after.
						}
						// add a line in the HTML table
					}

					addNewLineInHTMLTable(tempEntityConfidence, lineColumns[0],
							ontologyEntity);

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public float entityResolutionConfidence(
			ArrayList<OntologyPropertyValue> ontologyPropertiesAndValues,
			ArrayList<XMLPathValuePair> xmlPropertiesValues,
			String ontologyEntity) {

		float confidence = 0;
		float currentConf = 1;
		for (CandidatePairXMLToOntolotyProperty pair : candidatePairsOfProperties) {
			String ontologyProperty = replaceNamespaceWithActualPath(pair.OntologyProperty);
			ontologyProperty += "-";
			double inverseFunctionality = 0.0;
			if (functionality.functionalities.containsKey(ontologyProperty)) {
				inverseFunctionality = functionality.functionalities
						.get(ontologyProperty);
			}
			String ontologyValue = null;
			for (OntologyPropertyValue ontologypair : ontologyPropertiesAndValues) {
				if (ontologypair.property.equals(pair.OntologyProperty)) {
					ontologyValue = ontologypair.value;
				}
			}
			String xmlValue = null;
			for (XMLPathValuePair xmlPair : xmlPropertiesValues) {
				if (xmlPair.propertyPath.equals(pair.XMLpathProperty)) {
					xmlValue = xmlPair.value;
				}
			}

			int propabilityOfComparison;
			if (ontologyValue == null || xmlValue == null)
				propabilityOfComparison = 0;
			else {
				if (ontologyValue.equals(xmlValue))
					propabilityOfComparison = 1;
				else
					propabilityOfComparison = 0;
			}
			currentConf = currentConf
					* (float) (1 - (inverseFunctionality * propabilityOfComparison));

		}
		// / total confidence for the current entity
		confidence = 1 - currentConf;
		// System.out.println("XML Entity " + pairs.getKey()
		// + "\t resolved to entity: " + ontologyEntity
		// + "\tWith confidence: " + confidence);

		return confidence;
		// }//while

	}

	HashMap<String, ArrayList<XMLPathValuePair>> xqueryXMLFile(String filepath) {

		HashMap<String, ArrayList<XMLPathValuePair>> xmlResults = new HashMap<String, ArrayList<XMLPathValuePair>>();

		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		XPathFactory xpathFactory;
		XPath xpath = null;
		Document doc = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();

			xpathFactory = XPathFactory.newInstance();
			xpath = xpathFactory.newXPath();
			doc = (Document) builder.parse(new File(filepath));

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/** create XPathExpression object */
		try {

			// System.out.println("pathToEntityBeforeNormilization:"
			// + this.PathToEntityXML);
			if (this.PathToEntityXML.equals("/")) {
				String nodeName = "/";
				String propertyValue;
				for (CandidatePairXMLToOntolotyProperty pair : this.candidatePairsOfProperties) {

					String xpathProperty = normalizePropertyPathXML(pair.XMLpathProperty);
					xpathProperty = appendText(toGeneralXPath(xpathProperty));

					XPathExpression exprProperty = xpath.compile(xpathProperty);
					NodeList nodes = (NodeList) exprProperty.evaluate(doc,
							XPathConstants.NODESET);

					if (nodes != null && nodes.getLength() > 0) {
						propertyValue = nodes.item(0).getNodeValue();
						System.out.println("property:" + pair.XMLpathProperty
								+ " value:" + propertyValue);
						/* I should do the normalization for the value */
						XMLPathValuePair pathValue = new XMLPathValuePair(
								pair.XMLpathProperty, propertyValue);
						ArrayList<XMLPathValuePair> xmlPairs;
						if (xmlResults.containsKey(nodeName)) {
							System.out
									.println(nodeName
											+ " Yparxei idi sto hashMap to entity!!!!!");
							xmlPairs = xmlResults.get(nodeName);
						} else {
							xmlPairs = new ArrayList<XMLPathValuePair>();
						}
						xmlPairs.add(pathValue);
						xmlResults.put(nodeName, xmlPairs);
					}

				}

			} else {
				normalizeEntityPathXML();
				// System.out.println("pathToEntityAfterNormilization:"
				// + this.PathToEntityXML);
				String pathToEntityXMLXpath = toGeneralXPath(this.PathToEntityXML);

				System.out.println("xpath:" + pathToEntityXMLXpath);

				XPathExpression expr = xpath.compile(pathToEntityXMLXpath);

				/** evaluate expression result on XML document */
				NodeList entities = (NodeList) expr.evaluate(doc,
						XPathConstants.NODESET);
				// }

				String propertyValue;

				for (int i = 0; i < entities.getLength(); i++) {
					Node n = entities.item(i);
					String nodeName = this.PathToEntityXML + "[" + i + "]";
					System.out.println(nodeName);
					// String nodeName = n.getNodeName()+"["+i+"]";
					for (CandidatePairXMLToOntolotyProperty pair : this.candidatePairsOfProperties) {

						String xpathProperty = normalizePropertyPathXML(pair.XMLpathProperty);
						xpathProperty = appendText(toGeneralXPath(xpathProperty));

						XPathExpression exprProperty = xpath
								.compile(xpathProperty);
						NodeList nodes = (NodeList) exprProperty.evaluate(n,
								XPathConstants.NODESET);

						if (nodes != null && nodes.getLength() > 0) {
							propertyValue = nodes.item(0).getNodeValue();
							System.out.println("property:"
									+ pair.XMLpathProperty + " value:"
									+ propertyValue);
							/* I should do the normalization for the value */
							XMLPathValuePair pathValue = new XMLPathValuePair(
									pair.XMLpathProperty, propertyValue);
							ArrayList<XMLPathValuePair> xmlPairs;
							if (xmlResults.containsKey(nodeName)) {
								System.out
										.println(nodeName
												+ " Yparxei idi sto hashMap to entity!!!!!");
								xmlPairs = xmlResults.get(nodeName);
							} else {
								xmlPairs = new ArrayList<XMLPathValuePair>();
							}
							xmlPairs.add(pathValue);
							xmlResults.put(nodeName, xmlPairs);
						}

					}

				}
			}

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return xmlResults;
		// excequte the queries here. First for the entity and after parse the
		// arrayList and do the xpaths for the properties

	}

	void normalizeEntityPathXML() {
		if (this.PathToEntityXML.length() > 1) {
			if (this.PathToEntityXML.endsWith("/"))
				this.PathToEntityXML = this.PathToEntityXML.substring(0,
						this.PathToEntityXML.length() - 1);
			if (!this.PathToEntityXML.startsWith("/"))
				this.PathToEntityXML = "/" + this.PathToEntityXML;
		}
	}

	String normalizePropertyPathXML(String pathToProperty) {
		/**
		 * all other paths are relative paths; thus they should not start with
		 * slash
		 **/
		if (pathToProperty.startsWith("/"))
			pathToProperty = pathToProperty.substring(1,
					pathToProperty.length());
		return pathToProperty;
	}

	public String toGeneralXPath(String path) {
		StringBuffer newPath = new StringBuffer();
		String[] splits = path.split("/");

		if (splits.length == 0)
			return newPath.toString();
		if (splits[0].length() > 0) {
			if (path.startsWith("/"))
				newPath.append("/");
			if (splits[0].startsWith("@"))
				newPath.append("@*[local-name()='" + splits[0].substring(1)
						+ "']");
			else
				newPath.append("*[local-name()='" + splits[0] + "']");
		}

		for (int i = 1; i < splits.length; i++) {
			if (splits[i].startsWith("@"))
				newPath.append("/@*[local-name()='" + splits[i].substring(1)
						+ "']");
			else
				newPath.append("/*[local-name()='" + splits[i] + "']");
		}

		return newPath.toString();
	}

	public String appendText(String path) {
		if (!path.contains("@"))
			return path + "/text()";
		else
			return path;
	}

	ArrayList<OntologyPropertyValue> queryYago(String ontologyEntity) {

		ArrayList<OntologyPropertyValue> propertyValue = new ArrayList<OntologyPropertyValue>();
		for (CandidatePairXMLToOntolotyProperty pair : candidatePairsOfProperties) {
			String ontologyPropertyFullURI = replaceNamespaceWithActualPath(pair.OntologyProperty);

			String query = "select DISTINCT ?x where{ " + ontologyEntity + " "
					+ ontologyPropertyFullURI + " ?x ." + "}";
			System.out.println(query);
			List list = new ArrayList();
			list = queryYago.queryExecutionYago(query);

			for (int i = 0; i < list.size(); i++) {
				String[] splitedResults = list.get(i).toString().split("= ");
				String object = splitedResults[1].replace("]", "");
				object = object.replace(" )", "");
				object = object.replaceAll("\"", "");
				OntologyPropertyValue propValue = new OntologyPropertyValue(
						pair.OntologyProperty, object);
				propertyValue.add(propValue);
				System.out.println("object:" + object + "--");
			}

			// System.out.println(list);
		}

		return propertyValue;

	}

	public String replaceNamespaceWithActualPath(String path) {
		Iterator it = Constants.namespaseToPath.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Entry) it.next();
			if (path.contains(pairs.getKey().toString())) {
				path = path.replace(pairs.getKey().toString(), pairs.getValue()
						.toString());
				return path;
			}
		}
		return path;
	}

	public String replaceActualPathWithNamespace(String path) {
		Iterator it = Constants.pathToNamespase.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Entry) it.next();
			if (path.contains(pairs.getKey().toString())) {
				path = path.replace(pairs.getKey().toString(), pairs.getValue()
						.toString());
				return path;
			}
		}
		return path;
	}

	public static void initializeHTMLTable() {
		htmlTable = "<!DOCTYPE html>\n" + "<html>\n" + "<body>\n\n"
				+ "<table border=\"1\" style=\"width:300px\">\n" + "<tr>\n"
				+ "<td>Web Site</td>\n" + "<td>Function</td>\n"
				+ "<td>Input Value</td>\n" + "<td>XML Entity</td>\n"
				+ "<td>Ontology Entity</td>\n" + "<td>Confidence</td>\n"
				+ "</tr>\n";
	}

	public void addNewLineInHTMLTable(
			HashMap<String, Float> tempXMLEntityConfidence, String inputValue,
			String ontologyEntity) {
		System.out.println("OntologyEntity Before:" + ontologyEntity);
		// ontology entity replace namespace...
		ontologyEntity = replaceActualPathWithNamespace(ontologyEntity);
		ontologyEntity = ontologyEntity.replace("<", "");
		ontologyEntity = ontologyEntity.replace(">", "");
		System.out.println("OntologyEntity After:" + ontologyEntity);

		if (tempXMLEntityConfidence.size() == 1) {

			Iterator it = tempXMLEntityConfidence.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Entry) it.next();
				htmlTable += "<tr>\n" + "<td>" + this.webSite + "</td>\n"
						+ "<td>" + this.function + "</td>\n" + "<td>"
						+ inputValue + "</td>\n" + "<td>" + pairs.getKey()
						+ "</td>\n" + "<td>" + ontologyEntity + "</td>\n"
						+ "<td>" + pairs.getValue() + "</td>\n" + "</tr>\n";
			}

		} else {
			int flag = 0;
			Iterator it = tempXMLEntityConfidence.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Entry) it.next();
				if (flag == 0) {
					htmlTable += "<tr>\n" + "<th rowspan=\""
							+ tempXMLEntityConfidence.size() + "\">"
							+ this.webSite + "</th>\n" + "<th rowspan=\""
							+ tempXMLEntityConfidence.size() + "\">"
							+ this.function + "</th>\n" + "<th rowspan=\""
							+ tempXMLEntityConfidence.size() + "\">"
							+ inputValue + "</th>\n" + "<td>" + pairs.getKey()
							+ "</td>\n" + "<th rowspan=\""
							+ tempXMLEntityConfidence.size() + "\">"
							+ ontologyEntity + "</th>\n" + "<td>"
							+ pairs.getValue() + "</td>\n" + "</tr>\n";
					flag = 1;

				} else {
					htmlTable += "<tr>\n" + "<td>" + pairs.getKey() + "</td>\n"
							+ "<td>" + pairs.getValue() + "</td>\n" + "</tr>\n";
				}
			}// while

		}// else
	}

	public static void htmlTableToFile() {

		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(Constants.projectDirectory
					+ "entityResolution.html");
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
				out.write(htmlTable);
			} else {
				out = new BufferedWriter(new FileWriter(file, true));
				out.write(htmlTable);
			}

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Print just for testing **/
	void printEntityResolutionObject() {
		System.out.println(this.function + "\t" + this.webSite + "\t"
				+ this.PathToEntityXML);
		for (CandidatePairXMLToOntolotyProperty pair : this.candidatePairsOfProperties) {
			System.out.println(pair.OntologyProperty + "\t"
					+ pair.XMLpathProperty);
		}
	}

	public static void main(String[] args) {
		Constants.initializePathNamespaseMaps();
		functionality.parseFunctinalitiesFile(Constants.functionality);
		HashMap<String, ArrayList<InputTypesObject>> inputTypesOfFunctions = parseInputTypesFile("/Users/mary/Dropbox/OASIS/Mary-Data/config/input-types.txt");

		initializeHTMLTable();

		File folder = new File(Constants.classAliggnmentDir);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {

				String fileName = file.getName();

				EntityResolution entityResolution = new EntityResolution();
				entityResolution
						.parseCandidateFileOfXMLFunction(Constants.classAliggnmentDir
								+ fileName);
				if (entityResolution.PathToEntityXML == null
						|| entityResolution.candidatePairsOfProperties.size() == 0) {
					System.out.println("LATHOOOOS");
					continue;
				}

				entityResolution
						.findInputValuesFileForThefunction(inputTypesOfFunctions);
				entityResolution.printEntityResolutionObject();
			}
		}// for

		htmlTable += "</table>\n" + "</body>\n" + "</html>\n";

		htmlTableToFile();

	}

}
