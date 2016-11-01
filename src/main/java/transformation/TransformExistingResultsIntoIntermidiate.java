package transformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import webSite.TableLineObject;

import com.hp.hpl.jena.graph.impl.TripleStore;

import classAlignment.ComputePrecisionRecall;
import customization.Constants;
import evaluation.GoldSetObjectPathPairs;
import java.util.LinkedHashMap;

public class TransformExistingResultsIntoIntermidiate {

	static String webSite;
	static String function;

	String tmpClassPredicateXMLInput = null;

	private HashMap<TriplesSet, ArrayList<TriplesSet>> document;
	private int counter;
	public static String htmlTable;

	public TransformExistingResultsIntoIntermidiate() {
		htmlTable = new String();
		this.counter = 0;
		this.document = new LinkedHashMap<TriplesSet, ArrayList<TriplesSet>>();
	}

	/*
	 * function of prepearing the intermidiate result First try
	 */
	void parseClassAlignmentFile(String filepath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;

			String tmpClassSubjectDB = null;
			String tmpClassPredicateDB = null;
			String tmpClassObjectDB = null;

			String tmpClassSubjectXML = null;
			String tmpClassPredicateXML = null;
			String tmpClassObjectXML = null;

			TriplesSet ClassLine = null;
			ArrayList<TriplesSet> relations = null;

			document = new HashMap<TriplesSet, ArrayList<TriplesSet>>();

			int counter = 0;
			boolean previousWasRelation = false;

			while ((sCurrentLine = br.readLine()) != null) {

				if (!sCurrentLine.isEmpty()) {
					// System.out.println(sCurrentLine);
					String[] lineparts = sCurrentLine
							.split(Constants.separatorSpace);
					if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineFunction)) {
						previousWasRelation = false;
						this.function = lineparts[1];
						this.webSite = lineparts[2];
					} else if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineClass)) {
						previousWasRelation = false;

						tmpClassSubjectDB = "x";
						tmpClassSubjectXML = "x";
						if (lineparts[1].equals(ComputePrecisionRecall.input)) {
							tmpClassPredicateDB = ".";
							tmpClassObjectDB = "x";
							tmpClassObjectXML = "x";
						} else {
							tmpClassPredicateDB = lineparts[1];
							tmpClassObjectDB = "y" + ++counter;
							tmpClassObjectXML = "y" + counter;

							Iterator entries = document.entrySet().iterator();
							while (entries.hasNext()) {
								Map.Entry entry = (Map.Entry) entries.next();
								TriplesSet key = (TriplesSet) entry.getKey();
								if (key.dbTriple.predicate
										.contains(lineparts[1] + "/")) {
									System.out.println("Mpike stin if!!!!!!");
									System.out.println(webSite + "  "
											+ function + "  oldPredicate:"
											+ key.dbTriple.predicate
											+ "  oldSubject:"
											+ key.dbTriple.subject);
									System.out.println("To replace: "
											+ lineparts[1] + "/");
									key.dbTriple.predicate = key.dbTriple.predicate
											.replace(lineparts[1] + "/", "");

									key.dbTriple.subject = tmpClassObjectDB;
									key.xmlTriple.subject = tmpClassObjectDB;

									System.out.println("New Predicate:"
											+ key.dbTriple.predicate
											+ "  new Subject:"
											+ key.dbTriple.subject);
								}
							}

						}

						tmpClassPredicateXML = lineparts[2];

						Triple xmlClassTriple = new Triple(tmpClassSubjectXML,
								tmpClassPredicateXML, tmpClassObjectXML);
						Triple dbClassTriple = new Triple(tmpClassSubjectDB,
								tmpClassPredicateDB, tmpClassObjectDB);
						ClassLine = new TriplesSet(xmlClassTriple,
								dbClassTriple);

						relations = new ArrayList<TriplesSet>();

					} else if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineRelations)) {
						previousWasRelation = true;

						Triple xmlRelationTriple = new Triple(
								tmpClassObjectXML, lineparts[2], "y"
										+ ++counter);

						/*
						 * System.out.println(xmlRelationTriple.subject + " " +
						 * xmlRelationTriple.predicate + " " +
						 * xmlRelationTriple.object);
						 */

						Triple dbRelationTriple = new Triple(tmpClassObjectDB,
								lineparts[1], "y" + counter);

						/*
						 * System.out.println(dbRelationTriple.subject + " " +
						 * dbRelationTriple.predicate + " " +
						 * dbRelationTriple.object);
						 */

						TriplesSet RelationLine = new TriplesSet(
								xmlRelationTriple, dbRelationTriple);

						relations.add(RelationLine);
					}
				} else {
					if (previousWasRelation == true) {
						document.put(ClassLine, relations);
					}
					previousWasRelation = false;
					// do nothing
				}
			}

			if (previousWasRelation == true) {
				document.put(ClassLine, relations);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// void parseClassAlignmentFileSecondTry(String filepath) {
	// try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
	//
	// String sCurrentLine;
	//
	// String tmpClassSubjectDB = null;
	// String tmpClassPredicateDB = null;
	//
	// String tmpClassObjectDB = null;
	//
	// String tmpClassSubjectXML = null;
	// String tmpClassPredicateXML = null;
	// String tmpClassObjectXML = null;
	//
	// TriplesSet ClassLine = null;
	// ArrayList<TriplesSet> relations = null;
	//
	//
	//
	//
	// boolean previousWasRelation = false;
	// boolean isClassWithSameXMLPath = false;
	// boolean inputIsParsed = false;
	//
	// while ((sCurrentLine = br.readLine()) != null) {
	//
	// if (!sCurrentLine.isEmpty()) {
	// // System.out.println(sCurrentLine);
	// String[] lineparts = sCurrentLine
	// .split(Constants.separatorSpace);
	// if (lineparts[0]
	// .equals(ComputePrecisionRecall.prefixLineClass)) {
	// previousWasRelation = false;
	//
	//
	//
	// tmpClassSubjectDB = "x";
	// tmpClassSubjectXML = "x";
	// if (lineparts[1].equals(ComputePrecisionRecall.input)) {
	// System.out.println("Input Class");
	// tmpClassPredicateXMLInput = lineparts[2];
	// inputIsParsed = true;
	// isClassWithSameXMLPath=true;
	// continue;
	// } else { /* If The class is not the input value class */
	//
	// inputIsParsed = false;
	//
	// System.out.println("Not input Class");
	// tmpClassPredicateXML = lineparts[2];
	// if (tmpClassPredicateXML
	// .equals(tmpClassPredicateXMLInput)) {
	// System.out
	// .println("Not input Class - Equal XML predicate");
	// tmpClassPredicateDB = lineparts[1];
	// isClassWithSameXMLPath = true;
	// continue;
	//
	// } else {
	//
	// tmpClassPredicateDB = lineparts[1];
	// tmpClassObjectDB = "y" + ++counter;
	// tmpClassObjectXML = "y" + counter;
	//
	// Iterator entries = document.entrySet()
	// .iterator();
	// while (entries.hasNext()) {
	// Map.Entry entry = (Map.Entry) entries
	// .next();
	// TriplesSet key = (TriplesSet) entry
	// .getKey();
	// if (key.dbTriple.predicate
	// .contains(lineparts[1] + "/")) {
	// key.dbTriple.predicate = key.dbTriple.predicate
	// .replace(lineparts[1] + "/", "");
	//
	// key.dbTriple.subject = tmpClassObjectDB;
	// key.xmlTriple.subject = tmpClassObjectDB;
	// }
	// }
	//
	// }
	// }
	//
	// Triple xmlClassTriple = new Triple(tmpClassSubjectXML,
	// tmpClassPredicateXML, tmpClassObjectXML);
	// Triple dbClassTriple = new Triple(tmpClassSubjectDB,
	// tmpClassPredicateDB, tmpClassObjectDB);
	// ClassLine = new TriplesSet(xmlClassTriple,
	// dbClassTriple);
	//
	// relations = new ArrayList<TriplesSet>();
	//
	// } else if (lineparts[0]
	// .equals(ComputePrecisionRecall.prefixLineRelations)) {
	//
	//
	// if(inputIsParsed == true){
	// continue;
	// }
	//
	//
	// previousWasRelation = true;
	//
	//
	// /*
	// * System.out.println(xmlRelationTriple.subject + " " +
	// * xmlRelationTriple.predicate + " " +
	// * xmlRelationTriple.object);
	// */
	//
	// boolean newRelationAdded = false;
	//
	// Iterator entries = document.entrySet().iterator();
	// while (entries.hasNext()) {
	// Map.Entry entry = (Map.Entry) entries.next();
	// TriplesSet classLine = (TriplesSet) entry.getKey();
	// if (classLine.xmlTriple.predicate
	// .equals(tmpClassPredicateXMLInput)) {
	//
	// String newObject = classLine.dbTriple.object;
	// ArrayList<TriplesSet> rel = (ArrayList<TriplesSet>) entry
	// .getValue();
	//
	// Triple dbRelationTriple = new Triple(newObject,
	// tmpClassPredicateDB + "/"
	// + lineparts[1], "y" + ++counter);
	//
	// Triple xmlRelationTriple = new Triple(
	// newObject, lineparts[2], "y"
	// + counter);
	//
	//
	// TriplesSet RelationLine = new TriplesSet(
	// xmlRelationTriple, dbRelationTriple);
	// rel.add(RelationLine);
	// newRelationAdded = true;
	// previousWasRelation = false;
	// break;
	//
	// }
	// }
	//
	// if (newRelationAdded == false) {
	//
	// Triple xmlRelationTriple = new Triple(
	// tmpClassObjectXML, lineparts[2], "y"
	// + ++counter);
	//
	//
	//
	// Triple dbRelationTriple = new Triple(
	// tmpClassObjectDB, lineparts[1], "y"
	// + counter);
	//
	// /*
	// * System.out.println(dbRelationTriple.subject + " "
	// * + dbRelationTriple.predicate + " " +
	// * dbRelationTriple.object);
	// */
	//
	// TriplesSet RelationLine = new TriplesSet(
	// xmlRelationTriple, dbRelationTriple);
	//
	// relations.add(RelationLine);
	// }
	// }
	// } else {
	// if (previousWasRelation == true) {
	// document.put(ClassLine, relations);
	// }
	// previousWasRelation = false;
	// // do nothing
	// }
	// }
	//
	// if (previousWasRelation == true) {
	// document.put(ClassLine, relations);
	// }
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//

	void parseClassAlignmentFileThirdTry(String filepath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;

			String tmpClassSubjectDB = null;
			String tmpClassPredicateDB = null;

			String tmpClassObjectDB = null;

			String tmpClassSubjectXML = null;
			String tmpClassPredicateXML = null;
			String tmpClassObjectXML = null;

			TriplesSet ClassLine = null;
			ArrayList<TriplesSet> relations = null;

			boolean previousWasRelation = false;
			boolean isClassWithSameXMLPath = false;
			boolean inputIsParsed = false;

			while ((sCurrentLine = br.readLine()) != null) {

				if (!sCurrentLine.isEmpty()) {
					// System.out.println(sCurrentLine);
					String[] lineparts = sCurrentLine
							.split(Constants.separatorSpace);
					if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineClass)) {
						previousWasRelation = false;

						tmpClassSubjectDB = "x";
						tmpClassSubjectXML = "x";
						if (lineparts[1].equals(ComputePrecisionRecall.input)) { /* INPUT */
							System.out.println("Input Class");
							tmpClassPredicateXMLInput = lineparts[2];
							inputIsParsed = true;
							isClassWithSameXMLPath = false;
							continue;
						} else { /* If The class is not the input value class */

							inputIsParsed = false;

							tmpClassPredicateXML = lineparts[2];
							
							System.out.println(lineparts[2]);

							Iterator entries = document.entrySet().iterator();
							while (entries.hasNext()) {
								
								Map.Entry entry = (Map.Entry) entries.next();
								TriplesSet key = (TriplesSet) entry.getKey();
								ArrayList<TriplesSet> value = (ArrayList<TriplesSet>) entry
										.getValue();
								if (key.xmlTriple.predicate.contains(tmpClassPredicateXML)) {
									isClassWithSameXMLPath = true;

									System.out
											.println("Not input Class - Equal XML predicate");

									tmpClassSubjectXML = key.xmlTriple.object;
//                                                                        if(key.xmlTriple.predicate.equals(tmpClassPredicateXML))  //if we want the = 
//                                                                            tmpClassPredicateXML = "=";
//                                                                        else
									    tmpClassPredicateXML = lineparts[2]; //changed
                                                                        
									tmpClassObjectXML = "y" + ++counter;

									if (key.dbTriple.object.equals(""))
										tmpClassSubjectDB = key.dbTriple.subject;
									else
										tmpClassSubjectDB = key.dbTriple.object;
									tmpClassPredicateDB = lineparts[1];
									tmpClassObjectDB = "y" + counter;

									break;
								}
							}// while

							if (isClassWithSameXMLPath != true) {
								
								tmpClassPredicateDB = lineparts[1];
								tmpClassObjectDB = "y" + ++counter;

								tmpClassPredicateXML = lineparts[2];
								tmpClassObjectXML = "y" + counter;

							}
						}// else

						Triple xmlClassTriple = new Triple(tmpClassSubjectXML,
								tmpClassPredicateXML, tmpClassObjectXML);
						Triple dbClassTriple = new Triple(tmpClassSubjectDB,
								tmpClassPredicateDB, tmpClassObjectDB);
						ClassLine = new TriplesSet(xmlClassTriple,
								dbClassTriple);

						relations = new ArrayList<TriplesSet>();

					} else if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineRelations)) {

						if (inputIsParsed == true) {
							continue;
						}

						isClassWithSameXMLPath = false;
						previousWasRelation = true;

						/*
						 * System.out.println(xmlRelationTriple.subject + " " +
						 * xmlRelationTriple.predicate + " " +
						 * xmlRelationTriple.object);
						 */

						Triple xmlRelationTriple = new Triple(
								tmpClassObjectXML, lineparts[2], "y"
										+ ++counter);

						Triple dbRelationTriple = new Triple(tmpClassObjectDB,
								lineparts[1], "y" + counter);

						/*
						 * System.out.println(dbRelationTriple.subject + " " +
						 * dbRelationTriple.predicate + " " +
						 * dbRelationTriple.object);
						 */

						TriplesSet RelationLine = new TriplesSet(
								xmlRelationTriple, dbRelationTriple);

						relations.add(RelationLine);

					}
				} else {
					if (previousWasRelation == true) {
						document.put(ClassLine, relations);
					}
					previousWasRelation = false;
					// do nothing
				}
			}

			if (previousWasRelation == true) {
				document.put(ClassLine, relations);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/********* JUST FOR INPUT CLASS AND RELATIONS ***********/
	void parseClassAlignmentJustInputClassAndRelations(String filepath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;

			String tmpClassSubjectDB = null;
			String tmpClassPredicateDB = null;

			String tmpClassObjectDB = null;

			String tmpClassSubjectXML = null;
			String tmpClassPredicateXML = null;
			String tmpClassObjectXML = null;

			TriplesSet ClassLine = null;
			ArrayList<TriplesSet> relations = null;

			boolean previousWasRelation = false;
			boolean inputIsParsed = false;

			while ((sCurrentLine = br.readLine()) != null) {

				if (!sCurrentLine.isEmpty()) {
					// System.out.println(sCurrentLine);
					String[] lineparts = sCurrentLine
							.split(Constants.separatorSpace);
					if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineFunction)) {
						previousWasRelation = false;
						this.function = lineparts[1];
						this.webSite = lineparts[2];
					} else if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineClass)
							&& lineparts[1]
									.equals(ComputePrecisionRecall.input)) {
						previousWasRelation = false;
						inputIsParsed = true;

						tmpClassSubjectDB = "x";
						tmpClassPredicateDB = "";
						tmpClassObjectDB = "";

						tmpClassSubjectXML = "";
						tmpClassPredicateXML = lineparts[2];
						tmpClassObjectXML = "x";

						tmpClassPredicateXMLInput = lineparts[2];

						Triple xmlClassTriple = new Triple(tmpClassSubjectXML,
								tmpClassPredicateXML, tmpClassObjectXML);
						Triple dbClassTriple = new Triple(tmpClassSubjectDB,
								tmpClassPredicateDB, tmpClassObjectDB);
						ClassLine = new TriplesSet(xmlClassTriple,
								dbClassTriple);

						relations = new ArrayList<TriplesSet>();

					} else if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineRelations)
							&& inputIsParsed == true) {

						previousWasRelation = true;

						Triple xmlRelationTriple = new Triple(
								tmpClassObjectXML, lineparts[2], "y"
										+ ++counter);

						/*
						 * System.out.println(xmlRelationTriple.subject + " " +
						 * xmlRelationTriple.predicate + " " +
						 * xmlRelationTriple.object);
						 */

						Triple dbRelationTriple = new Triple(tmpClassSubjectDB,
								lineparts[1], "y" + counter);

						/*
						 * System.out.println(dbRelationTriple.subject + " " +
						 * dbRelationTriple.predicate + " " +
						 * dbRelationTriple.object);
						 */

						TriplesSet RelationLine = new TriplesSet(
								xmlRelationTriple, dbRelationTriple);

						relations.add(RelationLine);

					}
				} else {
					if (previousWasRelation == true) {
						document.put(ClassLine, relations);
					}
					previousWasRelation = false;
					inputIsParsed = false;
					// do nothing
				}
			}

			if (previousWasRelation == true) {
				document.put(ClassLine, relations);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/******** PRINT FILE *************/
	void printToNewFile(String filepath) {

		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(filepath);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
			} else {
				out = new BufferedWriter(new FileWriter(file));
			}

			out.write("F: " + webSite + "  " + function + "\n\n");
			Iterator entries = document.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry entry = (Map.Entry) entries.next();
				TriplesSet key = (TriplesSet) entry.getKey();

				ArrayList<TriplesSet> value = (ArrayList<TriplesSet>) entry
						.getValue();
                                if(key.dbTriple.object.equals("") && key.dbTriple.predicate.equals(""))
                                    out.write("C: " + key.dbTriple.subject + " "
						+ key.xmlTriple.subject
						+ key.xmlTriple.predicate + " " + key.xmlTriple.object
						+ "\n");
                                    else
				out.write("C: " + key.dbTriple.subject + " "
						+ key.dbTriple.predicate + " " + key.dbTriple.object
						+ "  " + key.xmlTriple.subject + " "
						+ key.xmlTriple.predicate + " " + key.xmlTriple.object
						+ "\n");
				for (TriplesSet triplesSet : value) {
					out.write("R: " + triplesSet.dbTriple.subject + " "
							+ triplesSet.dbTriple.predicate + " "
							+ triplesSet.dbTriple.object + "  "
							+ triplesSet.xmlTriple.subject + " "
							+ triplesSet.xmlTriple.predicate + " "
							+ triplesSet.xmlTriple.object + "\n");
				}

				out.write("\n");
			}

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	void printAsHTMLTable(String filePath){
		
		System.out.println(filePath);
		htmlTable = "<!DOCTYPE html>\n" + "<html>\n" + "<body>\n\n"
				+ "<table border=\"1\" style=\"width:300px\">\n" 
				+ "<tr>\n"
				+ "<th colspan=\"6\">"+webSite+"\t"+function+"</th>\n"
				+  "</tr>\n"
				+ "<tr>\n"
				+ "<th colspan=\"6\"> x (input entity) is initially mapped to the root (/) of the call result.</th>\n"
				+  "</tr>\n"
				+ "<tr>\n"
				+ "<th colspan=\"3\"> KB path</th>\n"
				
				+ "<th colspan=\"3\"> WS path</th>\n"
				+  "</tr>\n";
		
		
		Iterator entries = document.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			TriplesSet key = (TriplesSet) entry.getKey();

			ArrayList<TriplesSet> value = (ArrayList<TriplesSet>) entry
					.getValue();
		
			htmlTable += "<tr>\n" 
					+ "<td align=\"center\">"+ key.dbTriple.subject + "</td>\n" 
					+"<td align=\"center\">"+ key.dbTriple.predicate.replace("<", "").replace(">", "") + "</td>\n" 
					+ "<td align=\"center\">"+ key.dbTriple.object+ "</td>\n" 
				
					+ "<td align=\"center\">"+ key.xmlTriple.subject + "</td>\n" 
					+ "<td align=\"center\">"+ key.xmlTriple.predicate + "</td>\n" 
					+ "<td align=\"center\">"+ key.xmlTriple.object + "</td>\n" 
					+ "</tr>\n";
		
		
			for (TriplesSet triplesSet : value) {
				
				htmlTable += "<tr>\n" 
						+ "<td align=\"center\">"+ triplesSet.dbTriple.subject + "</td>\n" 
						+ "<td align=\"center\">"+ triplesSet.dbTriple.predicate.replace("<", "").replace(">", "") + "</td>\n" 
						+ "<td align=\"center\">"+ triplesSet.dbTriple.object+ "</td>\n" 
					
						+ "<td align=\"center\">"+ triplesSet.xmlTriple.subject + "</td>\n" 
						+ "<td align=\"center\">"+ triplesSet.xmlTriple.predicate + "</td>\n" 
						+ "<td align=\"center\">"+ triplesSet.xmlTriple.object + "</td>\n" 
						+ "</tr>\n";
			}
		}
		htmlTable += "</table>\n" + "</body>\n" + "</html>\n";
		htmlTableToFile(filePath);	
		
	}

	
	public static void htmlTableToFile(String filePath) {

		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(filePath);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
				out.write(htmlTable);
			} else {
				out = new BufferedWriter(new FileWriter(file));
				out.write(htmlTable);
			}

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {

		File folder = new File("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/GoldSets/goldsetsClassRelationAlignment/");//(Constants.GoldSetsClassAlignment);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {

				String fileName = file.getName();
				// System.out.println(goldSetFilename);
				if (fileName.equals(".DS_Store"))
					continue;
				TransformExistingResultsIntoIntermidiate intermidateResult = new TransformExistingResultsIntoIntermidiate();
				intermidateResult
						.parseClassAlignmentJustInputClassAndRelations("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/GoldSets/goldsetsClassRelationAlignment/"
								+ fileName);
				intermidateResult
						.parseClassAlignmentFileThirdTry("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/GoldSets/goldsetsClassRelationAlignment/"
								+ fileName);
				intermidateResult
						.printToNewFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/GoldSets/goldSetClassRelationAlignmentNewForm/"
								+ fileName);
				
				String[] SplitFile = fileName.split("_");
				String newFileName;
				if(SplitFile.length==4){
					 newFileName = SplitFile[2]+"_"+SplitFile[1]+"_ClassAlignment.html";
				}else{
					 newFileName = SplitFile[2]+"_"+SplitFile[3]+"_"+SplitFile[1]+"_ClassAlignment.html";
				}
				
				intermidateResult.printAsHTMLTable("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/GoldSets/goldSetClassRelationAlignmentNewFormHTML/"+newFileName.split(".html")[0]+"_goldset.html");
			}
		}
		
//		TransformExistingResultsIntoIntermidiate intermidateResult = new TransformExistingResultsIntoIntermidiate();
//		intermidateResult
//				.parseClassAlignmentJustInputClassAndRelations("/Users/mary/Dropbox/OASIS/Mary-Data/goldsets-ClassAlignment/100_getBookInfoByName_isbndb_Solution.txt.goldset.txt");
//		intermidateResult
//				.parseClassAlignmentFileThirdTry("/Users/mary/Dropbox/OASIS/Mary-Data/goldsets-ClassAlignment/100_getBookInfoByName_isbndb_Solution.txt.goldset.txt");
//		intermidateResult
//				.printToNewFile("/Users/mary/Desktop/100_getBookInfoByName_isbndb_Solution.txt.goldset.txt");
//		
//		intermidateResult.printAsHTMLTable("/Users/mary/Desktop/100_getBookInfoByName_isbndb_Solution.txt.goldset.txt.html");
//		
//		
		
	}

}
