package transformation;

public class Triple {

	public  String subject;
	public  String predicate;
	public  String object;
	
	public Triple(String subject, String predicate, String object){
		this.subject = subject;
		this.object = object;
		this.predicate = predicate;
	}
	
        
        
        public void setSubject(String newSubject){
            this.subject = newSubject;
        }
        
        public void setPredicate(String newPredicate){
            this.predicate = newPredicate;
        }
	
        public void setObject(String newObject){
            this.object = newObject;
            
        }
}
