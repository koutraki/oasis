package knowledgebase;

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

public class QueryBNFTDB {

	public static Dataset dataset;
	
	public static int queryCNT =0;
	
	public QueryBNFTDB(){
        dataset = TDBFactory.createDataset(Constants.BNFPath) ;
        
        Model tdb = dataset.getDefaultModel();
		
	}
    

    
    public static List queryExecutionBNF(String queryString){
    	
    //	System.out.println("In BNF:"+queryString);
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
    
	
}
