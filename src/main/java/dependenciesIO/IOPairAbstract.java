package dependenciesIO;

import java.util.Collection;

import download.WebFunction;

public interface IOPairAbstract  {

	public WebFunction getSource();
	public WebFunction getDestination();
	
	public Collection<String> getValidPaths();
	
	public String getResults();
	
	public String getDebugMessage();
	
}
