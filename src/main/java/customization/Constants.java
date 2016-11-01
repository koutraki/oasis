package customization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constants {

	public static final boolean offline = true;
	public static final boolean testWith100 = true;
	public static final boolean filterWorkingSet = false; // it was true but I
															// don't know why
	/**
	 * "music_brainz","last_fm", "deezer", "discogs", "echonest", "musixmatch",
	 * "isbndb", "library_thing"
	 **/
	/**
	 * "music_brainz","last_fm", "discogs", "echonest", "musixmatch", "isbndb",
	 * "library_thing", "themoviedb"
	 **/
	public static final String[] workingSetWebSites = new String[] { "isbndb",
			"library_thing" , "music_brainz","last_fm", "discogs", "echonest", "musixmatch", "themoviedb", "geonames"};
	public static final boolean ExperimentsOnYago = true;

	public static final String targetKB = "YAGO"; // YAGO or BNF or DBPedia

	public static final int queringLevel = 2;
 
	public static final String separatorForInputsFiles = "-->";
	public static final String separatorSpace = "\\s+";
	public static final String separatorProperties = "||";

	/***********************************************/
	/** Project directory **/
	/**********************************************/
	// public static String
	// projectDirectory="/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/";
	public static String projectDirectory = "/Users/mary/Dropbox/OASIS/Mary-Data/"
			+ targetKB + "/";
//	public static String projectDirectory="";
	public static String configurationDir = projectDirectory + "config/";
	public static String dirWithFunctions = projectDirectory + "functions/";
	// public static String dirWithFunctions=projectDirectory+"functions/";
	public static String dirWithInputs = projectDirectory + "inputs/";

	/** configuration files **/
	public static final String fileWithTheURLsOfTheFunctions = configurationDir
			+ "urls.txt";
	public static final String fileWithTheInputTypesOfTheFunctions = configurationDir
			+ "input-types.txt";
	public static final String fileWithIDExtractorsSpecifications = configurationDir
			+ "id-extractors.txt";

	// public static final String functionalityYago =
	// configurationDir+"functionality_predicates.xml";

	public static final String functionality = configurationDir
			+ "functionality_predicates.txt"; //used to be xml --> changed

	/** names of files with input values **/
	public static final String fileWithInputsAndEntities = dirWithFunctions
			+ "singers_entities.txt";

	public static final String getFileWithInputsForType(String type) {
		if (testWith100)
			return dirWithInputs + "100Inputs/" + "100_" + type
					+ "_entities.txt";
		else
			return dirWithInputs + "20Inputs/" + "20_" + type + "_entities.txt";
	}

	/** names of directories for site and function **/
	public static final String getDirectoryForSite(String site) {
		return dirWithFunctions + site;
	}

	/** get directory for function **/
	public static final String getDirectoryForFunction(String function,
			String site) {
		return dirWithFunctions + site + "/" + function + "/";
	}

	/***********************************************/
	/** Cache for XML results **/
	/**********************************************/
	/** words to be used in the file storing history/cache of the calls **/
	public static final String VALID = "OK";
	public static final String INVALID = "INVALID";

	/** cache results **/
	public static final String getHistoryOfCallsFileForFunction(String site,
			String function) {
		return dirWithFunctions + site + "/" + "calls_" + function + ".txt";
	}

	/***********************************************/
	/** YAGO **/
	/**********************************************/
//	public static final String yagoPath=projectDirectory+"YAGO/yago2core_jena_20120109/";
	public static final String yagoPath = "/Users/mary/Dropbox/YAGO/yago2core_jena_20120109/";
	public static final String BNFPath = "/Users/mary/Documents/Education/PhD/BNF/DATA/BnF_Version_2/tdb/";
//	public static final String BNFPath = projectDirectory +"BNF/tdb/";
	public static final String DBPediaPath = "/Users/mary/Documents/Education/PhD/DataSets/DBPedia/tdb/";
//	public static final String DBPediaPath = projectDirectory + "tdb/";

	// public static final String tagetKB = "BNF";
	public static final String getFileNameWithTheFunctionalities() {
		return targetKB + "_functionalities.txt";
	}

	/***********************************************/
	/** Dependency Module **/
	/**********************************************/
	public static final int maxRounds = 2;
	public static final String fileIODependencies = projectDirectory
			+ "ioDependencies.txt";
	public static final int minSamplesToDecideExistanceInvalidDocument = 20;
	public static final int percentangeOfBadResults = 20 / 100;

	/****************************************************/
	/** Nico's thresholds **/
	/****************************************************/
	/** percentages and lower set sizes to be used for statistical computations **/
	public static final int lowerSetSizeForStatistics = 20;
	public static final float fiftyPerCent = ((float) 50) / 100;

	/****************************************************/
	/** Mary's Algorithm **/
	/****************************************************/
	public static final String fileWithFunctionsToCall = configurationDir
			+ "FunctionsToCall.txt";
	public static final String fileWithPathsToCalculatePcaConf = configurationDir
			+ "PcaConf.txt";
	public static final String precisionRecallDirectory = projectDirectory
			+ "precisionRecall/";
	public static final String GoldSets = projectDirectory + "GoldSets/";
        
        public static final String GoldSetPathPairs = GoldSets + "goldsetsPathPairs/";
	
        public static final String GoldSetsClassAlignment = GoldSets
			+ "goldsetsClassRelationAlignment/";
	public static final String GoldSetsClassAlignmentNewForm = GoldSets
			+ "goldSetClassRelationAlignmentNewFormV2/";    // "goldSet-ClassAlignmentNewFormV2/";  //
	public static final String GoldSetsIODependences = projectDirectory
			+ "goldsets-IODependences/";
	public static final String pcaConfFolder = projectDirectory + "pcaConf/";
	public static final String sortedPairsDirectory = projectDirectory+
                "results100_LastVersion_0.08/";
               // +"pcaConf/XMLImpliesDBP/";
			//+ "results/";
	public static final String fileWithFunctionsAndInputTypes = configurationDir
			+ "FunctionsInputTypes.txt";
	public static final String inputTypes = configurationDir + "inputTypes.txt";
	public static final String classAliggnmentDir = projectDirectory
			+ "ClassAlignmentNewForm/";
	public static final String classAlignmentNewFormDir = projectDirectory
			+ "ClassAlignmentNewForm/";
	public static final String IODependencesDir = projectDirectory
			+ "io_dependencies/";
	public static final String HTMLPrecisionRecallDependencesTable = projectDirectory
			+ "PrecisionRecallIODependences.html";
	public static final String HTMLPrecisionRecallClassAlignmentTable = projectDirectory
			+ "classAlignmentNewFormV2.html";
	public static final String OverlappingWithGoldSetsDirectory = projectDirectory
			+ "overlapping_with_goldsets/";
	public static final String viewsDirectory = projectDirectory + "views/";
        
        public static final String xsltDirectory = projectDirectory + "XSLTCodes/";
        
	public static final String propertiesPathsDir = configurationDir
			+ "propertiesPaths_0.08Threshold/"; // <-- changed
	public static final String BasicInputTypesDir = dirWithInputs
			+ "BasicInputTypes/";
        public static String newformClassAndRelation=projectDirectory+"ClassAlignmentNewForm";
	
        
         public static String XSLTResults=projectDirectory+"XSLTResults/";
         
         public static String triples=projectDirectory+"triples/";
        

	/* Return file with properties paths for given inputType */
	public static final String getFileWithPropertyPathsForType(String type) {
		return propertiesPathsDir + type + "_propertiesPaths.txt";
  	}

	public static final double precisionRecallThreshold = 0.5;
	public static final double pcaConfThreshold = 0.05;

	
	/*****************************************************/
	/***** QUERIES LEVEL1 **********/
	/*****************************************************/

	public static final String queryLevel0 = "select DISTINCT ?p where { inputEntity ?p ?x . } ";
	public static final String inverseQueryLevel0 = "select DISTINCT ?p where { ?x ?p inputEntity . } ";

	/****************************************************/
	/** Parameters class alignment **/
	/****************************************************/
	public static final int noSamples = 100;
	public static final float thresholdOfFunctionalityWhenRelationBecomesOneToOne = (float) 0.5;
	public static final float thresholdForTheConfidenceOfTheRootToLeafPathAlignement = (float) 0.5;
        
        public static final boolean isDirectFunctional = true; // If false, the alignment is based on direct AND inverse functions
        
        
        
        /****************************************************/
	/** Parameters for dependencies**/
	/****************************************************/
	public static final String dirWithDependenciesResult=projectDirectory+"io_dependencies/";
	public static final int maxAverageAuxiliaryMetadata=2050;
	public static final int minSamplesForIO=10;
	public static final boolean testOnlyPairsWithSeveralValidPaths=true;
	public static final boolean testAllPairs=true;
	public static final boolean testOnlyMixt=true;
	
	public static String step1="step1";
	public static String step2="step2";
	
	
	/** profiles **/
	public static final String profileDir= projectDirectory+"profiles/";
	public static final int INDEF=-1;
	public static final int EXCEPTION=0;
	public static final int DOC_ERROR=1;
	public static final int INCONCLUSIVE=2;
	public static float thresholdForDummy=(float)0.5;
	

	/****************************************************/
	/** Parameters for tests/debug **/
	/****************************************************/
	public static final boolean debugMessages = true;

	/****************************************************/
	/** Parameters for tests/debug **/
	/****************************************************/
	
	
        
        
        
	/****************************************************/
	/** String comparison **/
	/****************************************************/
	public static final String SPLIT_REGEX = "[^0-9a-zA-Z]+";
	public static final String normalization(String value) {
		String lower = value.trim().toLowerCase();
		String[] words = lower.split(SPLIT_REGEX);

		for (int i = 0; i < words.length; i++) {
			for (int j = i + 1; j < words.length; j++) {
				if (words[i].compareTo(words[j]) > 0) {
					String temp = words[i];
					words[i] = words[j];
					words[j] = temp;
				}
			}// for
		}// for
		String normalizedString = new String();
		for (int i = 0; i < words.length; i++) {
			if (i == words.length - 1)
				normalizedString = normalizedString + words[i];
			else
				normalizedString = normalizedString + words[i] + " ";
		}
		return normalizedString;
	}
	
        public static final String transformStringForURL(String input) {
		// input=input.replaceAll("\\p{Punct}", " ");
		return input.trim().replaceAll("\\s+", "+");
	}
	
	
	public static final String revertTransformation(String file){
		if(file==null) return null;
		System.out.flush();
		String name=file.replaceAll("\\+"," ");
		return name.contains(".")?name.substring(0, name.lastIndexOf(".")):name;
		
	}
	

        
        
        
        
        

	public static HashMap<String, String> pathToNamespase = new HashMap<String, String>();
	public static HashMap<String, String> namespaseToPath = new HashMap<String, String>();

	public static void initializePathNamespaseMaps() {
		pathToNamespase.put("http://yago-knowledge.org/resource/", "Y:");
		namespaseToPath.put("Y:", "http://yago-knowledge.org/resource/");

		pathToNamespase
				.put("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>",
						"rdf:type");
		namespaseToPath.put("rdf:type",
				"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");

		pathToNamespase.put("<http://www.w3.org/2000/01/rdf-schema#label>",
				"rdfs:label");
		namespaseToPath.put("rdfs:label",
				"<http://www.w3.org/2000/01/rdf-schema#label>");

		pathToNamespase.put(
				"<http://www.w3.org/2000/01/rdf-schema#subClassOf>",
				"rdfs:subClassOf");
		namespaseToPath.put("rdfs:subClassOf",
				"<http://www.w3.org/2000/01/rdf-schema#subClassOf>");

		pathToNamespase.put("<http://www.w3.org/2002/07/owl#sameAs>",
				"owl:sameAs");
		namespaseToPath.put("owl:sameAs",
				"<http://www.w3.org/2002/07/owl#sameAs>");

		pathToNamespase.put("<http://www.w3.org/2000/01/rdf-schema#seeAlso>",
				"rdfs:seeAlso");
		namespaseToPath.put("rdfs:seeAlso",
				"<http://www.w3.org/2000/01/rdf-schema#seeAlso>");
		
		pathToNamespase.put("<http://www.w3.org/2000/01/rdf-schema#comment>",
				"rdfs:comment");
		namespaseToPath.put("rdfs:comment",
				"<http://www.w3.org/2000/01/rdf-schema#comment>");
		

		pathToNamespase.put("http://dbpedia.org/", "DBP:");
		namespaseToPath.put("DBP:", "http://dbpedia.org/");
	}

}
