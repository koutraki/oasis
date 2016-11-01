package KBExtraction;

import graphguide.Functionality;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import knowledgebase.QueryBNFTDB;
import knowledgebase.QueryDBPedia;
import knowledgebase.QueryYAGOTDB;
import customization.Constants;

public class PropertiesExtraction {

    public static QueryYAGOTDB queryYago = new QueryYAGOTDB();

    public static QueryBNFTDB queryBNF = new QueryBNFTDB();

    public static QueryDBPedia queryDBPedia = new QueryDBPedia();

    HashSet<String> propertiesLevel;

    public static final double threshold = 0.08;
    public static final double thresholdInverse = 0.08;

    public PropertiesExtraction() {
        propertiesLevel = new HashSet<String>();

    }

    void parseInputsFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] line = sCurrentLine
                        .split(Constants.separatorForInputsFiles);
                String inputEntity = line[1];
                System.out.println("inputEntity:" + inputEntity);

                HashSet<String> propertiesPerInputEntity = new HashSet<String>();

                for (int currentLeve = 0; currentLeve <= Constants.queringLevel; currentLeve++) {
                    System.out.println("CURRENT LEVEL: " + currentLeve);
                    if (currentLeve == 0) {

                        String q1 = Constants.queryLevel0.replace(
                                "inputEntity", inputEntity);
                        String q2 = Constants.inverseQueryLevel0.replace(
                                "inputEntity", inputEntity);

                        List list = new ArrayList();
                        List list2 = new ArrayList();
                        if (Constants.targetKB.equals("YAGO")) {
                            //			System.out.println("target KB YAGO");
                            list = queryYago.queryExecutionYago(q1);
                            list2 = queryYago.queryExecutionYago(q2);
                        } else if (Constants.targetKB.equals("BNF")) {
                            //			System.out.println("target KB BNF");
                            list = queryBNF.queryExecutionBNF(q1);
                            list2 = queryBNF.queryExecutionBNF(q2);
                        } else if (Constants.targetKB.equals("DBPedia")) {
                            list = queryDBPedia.queryExecutionDBPedia(q1);
                            list2 = queryDBPedia.queryExecutionDBPedia(q2);
                        }

                        String property;
                        for (int i = 0; i < list.size(); i++) {
                            // System.out.println("LEVEL 0 DIRECT PROP!");
                            property = getPropertyFromQueryResult(list.get(i));
                            if (property.contains("sameAs")) {
                                System.out.println("Property contains sameAs");
                                continue;
                            }

                            System.out.println(property);
                            if (Constants.namespaseToPath.containsKey(property)) {
                                property = Constants.namespaseToPath
                                        .get(property);
                            }
                            System.out.println("AfterNS:" + property);
                            if (Functionality.functionalities
                                    .containsKey(property)) {
                                if (Functionality.functionalities.get(property) > threshold) {
                                    propertiesPerInputEntity.add(property);
                                    System.out.println("-->"+property+Functionality.functionalities.get(property));
                                } else {
                                    System.out
                                            .println("Functionality less than the threshold!  --> "
                                                    + property);
                                }

                            }else{
                                 System.out
                                            .println("Does not contain property  --> "
                                                    + property);
                            }
                            
                        }//for 
                        // System.out.println(list2.size());
                        for (int i = 0; i < list2.size(); i++) {
                            // System.out.println("LEVEL 0 INVERSE PROP!");

                            property = getPropertyFromQueryResult(list2.get(i));
                            if (property.contains("sameAs")) {
                                System.out.println("property contains sameAs");
                                continue;
                            }
                            if (Constants.namespaseToPath.containsKey(property)) {
                                property = Constants.namespaseToPath
                                        .get(property);
                            }
                            if (Functionality.functionalities
                                    .containsKey(property + "-")) {
                                if (Functionality.functionalities.get(property
                                        + "-") > thresholdInverse) {
                                    String inverseProp = property + "-";
                                    propertiesPerInputEntity.add(inverseProp);
                                } else {
								 System.out
                                     .println("Functionality less than the threshold!  --> "
                                     + property + "-");
                                }
                            }else{
                                
                                 System.out
                                            .println("Does not contain property  --> "
                                                    + property);
                            
                            }
                        }//for

                    } // currentLevel == 0
                    else {

                        HashSet<String> tmpProperties = new HashSet<String>();
                        for (String prop : propertiesPerInputEntity) {
                            tmpProperties.add(prop);
                        }

                        for (String prop : tmpProperties) {
                            System.out.println("tmpProp: " + prop);

                            if (checkForDataTypeProperty(prop)) {
                                System.out.println("DATA Type Continue: " + prop);
                                continue;
                            }
                            if (prop.contains("sameAs")) {
                                System.out.println("property Contains sameAs");
                                continue;
                            }
                            String query = constractQueryFromEncodedProperties(
                                    inputEntity, prop, "d", currentLeve);
                            String queryInverse = constractQueryFromEncodedProperties(
                                    inputEntity, prop, "i", currentLeve);

                            List list = new ArrayList();
                            List listInverse = new ArrayList();
                            if (Constants.targetKB.equals("YAGO")) {
                                list = queryYago.queryExecutionYago(query);
                                listInverse = queryYago
                                        .queryExecutionYago(queryInverse);
                            } else if (Constants.targetKB.equals("BNF")) {
                                list = queryBNF.queryExecutionBNF(query);
                                listInverse = queryBNF
                                        .queryExecutionBNF(queryInverse);
                            } else if (Constants.targetKB.equals("DBPedia")) {
                                list = queryDBPedia
                                        .queryExecutionDBPedia(query);
                                listInverse = queryDBPedia
                                        .queryExecutionDBPedia(queryInverse);
                            }

                            String property;
                            for (int i = 0; i < list.size(); i++) {
                                property = getPropertyFromQueryResult(list
                                        .get(i));
                                if (Constants.namespaseToPath
                                        .containsKey(property)) {
                                    property = Constants.namespaseToPath
                                            .get(property);
                                }

                                if (Functionality.functionalities
                                        .containsKey(property)) {
                                    if (Functionality.functionalities
                                            .get(property) > threshold) {

                                        propertiesPerInputEntity.remove(prop);
                                        propertiesPerInputEntity.add(prop
                                                + ", " + property);
                                    }
                                } else {
                                    // System.out
                                    // .println("Functionality less than the threshold!  --> "
                                    // + property);
                                }
                            }

                            for (int i = 0; i < listInverse.size(); i++) {
                                // System.out.println("INVERSE QUERY");
                                property = getPropertyFromQueryResult(listInverse
                                        .get(i));
                                if (Constants.namespaseToPath
                                        .containsKey(property)) {
                                    property = Constants.namespaseToPath
                                            .get(property);
                                }

                                if (Functionality.functionalities
                                        .containsKey(property + "-")) {
                                    if (Functionality.functionalities
                                            .get(property + "-") > thresholdInverse) {

                                        propertiesPerInputEntity.remove(prop);
                                        propertiesPerInputEntity.add(prop
                                                + ", " + property + "-");
                                    }
                                } else {
                                    // System.out
                                    // .println("Functionality less than the threshold!  --> "
                                    // + property + "-");
                                }
                            }
                        }// for
                    }// else

                }// for Levels

                for (String propertyPath : propertiesPerInputEntity) {
                    propertiesLevel.add(propertyPath);
                }

            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private boolean checkForDataTypeProperty(String propertyPath) {
        String[] properties = propertyPath.split(", ");
        String lastProperty = properties[properties.length - 1];

        // String[] property = lastProperty.split(",");
        String property;
        if (lastProperty.endsWith("-")) {
            property = lastProperty.substring(0, lastProperty.length() - 1);
        } else {
            property = lastProperty;
        }
        // ask query...

        property = replaceNamespaceWithActualPath(property);
        // System.out.println("Replaced:"+property);

        String query = "Select ?y where { ?x " + property + " ?y. } Limit 1";
        // System.out.println(query);

        List list = new ArrayList();
        if (Constants.targetKB.equals("YAGO")) {
            list = queryYago.queryExecutionYago(query);
        } else if (Constants.targetKB.equals("BNF")) {
            list = queryBNF.queryExecutionBNF(query);
        } else if (Constants.targetKB.equals("DBPedia")) {
            list = queryDBPedia.queryExecutionDBPedia(query);
        }

        for (int i = 0; i < list.size(); i++) {
            // System.out.println("  result:"+ list.get(i));
            String[] splited = list.get(i).toString().split("= ");
            if (!splited[1].startsWith("<")) {// object prop
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private void removeObjectPropertiesFromLastLevel() {

        HashSet<String> tmp = new HashSet<String>();
        for (String propertyPath : propertiesLevel) {
            tmp.add(propertyPath);
        }

        for (String propertyPath : tmp) {
            // System.out.println("property: "+propertyPath);
            String[] properties = propertyPath.split(", ");
            String lastProperty = properties[properties.length - 1];

            // String[] property = lastProperty.split(",");
            String property;
            if (lastProperty.endsWith("-")) {
                property = lastProperty.substring(0, lastProperty.length() - 1);
            } else {
                property = lastProperty;
            }

            property = replaceNamespaceWithActualPath(property);
            // ask query...

            String query = "Select ?y where { ?x " + property
                    + " ?y. } Limit 1";
            // System.out.println(query);
            List list = new ArrayList();
            if (Constants.targetKB.equals("YAGO")) {
                list = queryYago.queryExecutionYago(query);
            } else if (Constants.targetKB.equals("BNF")) {
                list = queryBNF.queryExecutionBNF(query);
            } else if (Constants.targetKB.equals("DBPedia")) {
                list = queryDBPedia.queryExecutionDBPedia(query);
            }

            for (int i = 0; i < list.size(); i++) {
                // System.out.println("  result:"+ list.get(i));
                String[] splited = list.get(i).toString().split("= ");
                if (splited[1].startsWith("<")) {// object prop
                    propertiesLevel.remove(propertyPath);
                    //		System.out.println("PROPERTY REMOVED: " + propertyPath);

                } else if (splited[1].startsWith("\"http")) {
                    propertiesLevel.remove(propertyPath);
                    System.out.println("PROPERTY URL REMOVED: " + propertyPath);
                }

            }

        }
    }

    public String replaceNamespaceWithActualPath(String path) {
        // System.out.println("For replacement:"+path+"---");
        Iterator it = Constants.namespaseToPath.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Entry) it.next();
            if (path.contains(pairs.getKey().toString())
                    || path.equals(pairs.getKey().toString())) {
                // System.out.println("Ton perneis k gerneis");
                path = path.replace(pairs.getKey().toString(), pairs.getValue()
                        .toString());
                return path;
            }
        }
        return path;
    }

    private String constractQueryFromEncodedProperties(String inputEntity,
            String propertyAndDirection, String direction, int currentLevel) {

        System.out.println("propertyAndDirection: " + propertyAndDirection);

        String[] properties = propertyAndDirection.split(", ");
        // System.out.println(properties.length);
        String query = "select DISTINCT ?p where { ";
        for (int i = 0; i < properties.length; i++) {
            // String[] propAndDirec = properties[i].split(",");
            properties[i] = replaceNamespaceWithActualPath(properties[i]);
            System.out.println("Replaced Prop:" + properties[i]);

            if (!properties[i].endsWith("-")) { // direct prop
                if (i == 0) {
                    query += inputEntity + " " + properties[i] + " ?x" + i
                            + " . ";
                } else {
                    query += "?x" + (i - 1) + " " + properties[i] + " ?x" + i
                            + " . ";
                }
            } else { // inverse prop
                if (i == 0) {
                    query += "?x"
                            + i
                            + " "
                            + properties[i].substring(0,
                                    properties[i].length() - 1) + " "
                            + inputEntity + " . ";
                } else {
                    query += " ?x"
                            + i
                            + " "
                            + properties[i].substring(0,
                                    properties[i].length() - 1) + " ?x"
                            + (i - 1) + " .";
                }
            }

        }
        if (direction.equals("d")) {
            query += "?x" + (properties.length - 1) + " ?p ?x"
                    + properties.length + " . ";
        } else {
            query += "?x" + properties.length + " ?p ?x"
                    + (properties.length - 1) + " . ";
        }

        // if(currentLevel==Constants.queringLevel){
        // query += " ?p ?a owl:DatatypeProperty .";
        //
        // }
        query += " }";

        // System.out.println("Query: " + query);
        return query;
    }

    private String getPropertyFromQueryResult(Object object) {
        //	System.out.println("property:" + object.toString());
        String[] splited = object.toString().split("=");
        String property = splited[1].trim().replace("> )", ">");
        property = property.trim().replace(" )", "");
        // System.out.println("property After:"+property);
        return property;
    }

    void printProperties(HashSet<String> set) {
        for (String prop : set) {
            System.out.println("(" + prop + ")");
        }
    }

    void PropertyPathsToFile(String filePath) {

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

            for (String prop : propertiesLevel) {
                String propertyPathWithNS = replacePath(prop);
                // System.out.println("propertyPathWithNS: "+propertyPathWithNS);
                out.write(propertyPathWithNS + "\n");
            }

            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String replacePath(String path) {

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

    public static void main(String[] args) {

        Functionality functionality = new Functionality();
        //functionality.parseFunctinalitiesFile(Constants.functionality);
        functionality.parseFunctionalitiesTXTFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/config/functionality_predicates.txt");
      //  functionality.parseFunctionalitiesTXTFile("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/config/YAGO_functionalities.txt");
      //  System.out.println(Constants.functionality);
        functionality.printFunctionality();

        Constants.initializePathNamespaseMaps();

//		File folder = new File(Constants.dirWithInputs+"/20Inputs/");
//		File[] listOfFiles = folder.listFiles();
//
//		for (File file : listOfFiles) {
//			if (file.isFile()) {
//
//				String fileName = file.getName();
//
//				// System.out.println(file.getAbsolutePath());
//				if (fileName.equals(".DS_Store"))
//					continue;
//
//				PropertiesExtraction pe = new PropertiesExtraction();
//				pe.parseInputsFile(file.getAbsolutePath());
//				// pe.printProperties(pe.propertiesLevel);
//				// System.out.println("BEFORE:" + pe.propertiesLevel.size());
//				pe.removeObjectPropertiesFromLastLevel();
//				// System.out.println("AFTER:" + pe.propertiesLevel.size());
//				//
//
//				pe.PropertyPathsToFile(Constants.propertiesPathsDir
//						+ file.getName());
//
//				System.out.println("\n\n END OF: " + fileName + "\n\n");
//			}
//		}
        PropertiesExtraction pe = new PropertiesExtraction();
        pe.parseInputsFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/inputs/20Inputs/20_countries_entities.txt");
        pe.printProperties(pe.propertiesLevel);
        System.out.println("BEFORE:" + pe.propertiesLevel.size());

        pe.removeObjectPropertiesFromLastLevel();
        System.out.println("AFTER:" + pe.propertiesLevel.size());
        pe.PropertyPathsToFile("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/config/propertiesPaths_0.08Threshold/countries_propertiesPaths.txt");

    }

}
