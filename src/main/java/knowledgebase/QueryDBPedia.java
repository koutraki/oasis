package knowledgebase;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.atlas.web.HttpException;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

import customization.Constants;

public class QueryDBPedia {

	public static int queryCNT = 0;
	public static Dataset dataset;

	// public static final String tdbDirectory =
	// "C:\\TDBLoadGeoCoordinatesAndLabels";
	//
	// /** The Constant dbdump0. */
	// public static final String dbdump0 =
	// "C:\\Users\\Public\\Documents\\TDB\\dbpedia_3.8\\dbpedia_3.8.owl";
	//
	// /** The Constant dbdump1. */
	// public static final String dbdump1 =
	// "C:\\Users\\Public\\Documents\\TDB\\geo_coordinates_en\\geo_coordinates_en.nt";

	public QueryDBPedia() {

		dataset = TDBFactory.createDataset(Constants.DBPediaPath);

		Model tdb = dataset.getDefaultModel();
		// Model tdbModel = TDBFactory.createModel(tdbDirectory);
		// /*Incrementally read data to the Model, once per run , RAM > 6 GB*/
		// FileManager.get().readModel( tdbModel, dbdump0);
		// FileManager.get().readModel( tdbModel, dbdump1, "N-TRIPLES");
		// tdbModel.close();
	}

	// public static List queryExecutionDBPedia(String queryString) {
	// queryCNT++;
	// List resultsList = new ArrayList();
	//
	// // System.out.println(queryString);
	// try{
	// com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
	// QueryExecution qexec = QueryExecutionFactory.sparqlService(
	// "http://dbpedia.org/sparql", query);
	//
	// ResultSet results = qexec.execSelect();
	// resultsList = ResultSetFormatter.toList(results);
	// qexec.close();
	// } catch (HttpException e){
	// System.err.println("HttpException: "+queryString);// + e.getMessage());
	// }
	//
	// return resultsList;
	// }

	public static List queryExecutionDBPedia(String queryString) {
		queryCNT++;

		
		dataset.begin(ReadWrite.READ);
		Query query;
		ResultSet results = null;
		QueryExecution qexec = null;
		List resultsList = new ArrayList();

	//	System.out.println(queryString);
		
		try {
			query = QueryFactory.create(queryString);
			qexec = QueryExecutionFactory.create(query, dataset);
			results = qexec.execSelect();

			resultsList = ResultSetFormatter.toList(results);
			// ResultSetFormatter.out(results) ; //print on the console

			qexec.close();

		} catch (QueryParseException e) {
			System.err.println("Caught QueryParseException: " + queryString);// +
																				// e.getMessage());

		}
		dataset.end();
		return resultsList;

	}
        
        
        public static void main(String[] args){
            QueryDBPedia dbpedia = new QueryDBPedia();
            List list = new ArrayList();
            list = dbpedia.queryExecutionDBPedia("select DISTINCT ?x1  where { "
                    + " <http://dbpedia.org/resource/David_Bowie> <http://xmlns.com/foaf/0.1/name> ?x1}");
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i).toString());
                
            }
        }

}
