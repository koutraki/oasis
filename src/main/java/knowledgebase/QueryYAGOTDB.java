package knowledgebase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import customization.Constants;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mary
 */
public class QueryYAGOTDB {
	
	public static Dataset dataset;
	
	public static int queryCNT =0;
	
	public QueryYAGOTDB(){
        dataset = TDBFactory.createDataset(Constants.yagoPath) ;
        
        Model tdb = dataset.getDefaultModel();
		
	}
    

    
    public static List queryExecutionYago(String queryString){
    	
    //	System.out.println("In Yago:"+queryString);
        queryCNT++;
 
        dataset.begin(ReadWrite.READ);
        Query query;
        ResultSet results = null;
        QueryExecution qexec = null;
        List resultsList = new ArrayList();
        
        try{
        query = QueryFactory.create(queryString);
        qexec = QueryExecutionFactory.create(query, dataset);
        results = qexec.execSelect();
        
        
        resultsList = ResultSetFormatter.toList(results);
    //    ResultSetFormatter.out(results) ; //print on the console
        
        qexec.close();
     
        }catch(QueryParseException e){
        	System.err.println("Caught QueryParseException: "+queryString);// + e.getMessage());
        
        	
        }
        dataset.end();
       return resultsList;
        
    }
    
    public static void extractEntitiesByInputValues(String fileWithInputs) {

		try (BufferedReader br = new BufferedReader(new FileReader(
				fileWithInputs))) {

			FileWriter fstream = new FileWriter( // Create new File with
													// inputValues AND
													// corresponding entities
					Constants.fileWithInputsAndEntities);
			BufferedWriter out = new BufferedWriter(fstream);
			List list = new ArrayList();

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {

				String query = "select ?x where{"
						+ "?x <http://yago-knowledge.org/resource/hasPreferredName> '"
						+ sCurrentLine
						+ "'."
						+ "?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://yago-knowledge.org/resource/wordnet_person_100007846>."
						+ "}";

				list = new ArrayList();
				list = queryExecutionYago(query);
				String entity = getEntityFromResult(list);
				out.write(sCurrentLine + "-->" + entity + "\n"); // add to
																	// fileWithInputsAndEntities-->
																	// inputValue:correspondingEnity

			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
    

	/*************************************************************/
	public static String getEntityFromResult(List list) {
		String entity = null;
		for (int i = 0; i < list.size(); i++) {
			String[] splited = list.get(i).toString().split("=");
			entity = splited[1].trim().replace("> )", ">");
		}

		return entity;
	}
	
//	public static void main (String[] args) throws Exception {
//		QueryYAGOTDB qYago = new QueryYAGOTDB();
//		qYago.extractEntitiesByInputValues(Constants.fileWithInputsForCalls);
//		
//	}


    
    
    
}
