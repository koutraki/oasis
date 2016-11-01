package entityResolution;

import customization.Constants;

public class OntologyPropertyValue {

	public String property;
	public String value;
	
	public OntologyPropertyValue(String property, String value){
		this.property = property;
		this.value = Constants.normalization(value);
	}
	
}
