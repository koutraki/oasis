package entityResolution;

import customization.Constants;

public class XMLPathValuePair {

	public String propertyPath;
	public String value;
	
	public XMLPathValuePair(String propertyPath, String value){
		this.propertyPath = propertyPath;
		this.value = Constants.normalization(value);
	}
}
