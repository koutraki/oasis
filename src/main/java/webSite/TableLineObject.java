package webSite;

public class TableLineObject {
	
	String webSite;
	String function;
	float precision20Inputs = 0.0f;
	float recall20Inputs = 0.0f;
	float precision100Inputs = 0.0f;
	float recall100Inputs = 0.0f;
	
	public TableLineObject(String webSite,String function,float precision20Inputs,float recall20Inputs,float precision100Inputs,
	float recall100Inputs) {
		this.webSite = webSite;
		this.function = function;
		this.precision20Inputs = precision20Inputs;
		this.recall20Inputs = recall20Inputs;
		this.precision100Inputs= precision100Inputs;
		this.recall100Inputs = recall100Inputs;
	}
	

}
