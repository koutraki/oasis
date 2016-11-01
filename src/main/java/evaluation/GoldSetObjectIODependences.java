package evaluation;

import java.util.ArrayList;

public class GoldSetObjectIODependences {

		public String functionFrom;
		public String functionTo;
		public ArrayList<String> paths;
		
		
		public GoldSetObjectIODependences(String functionFrom, String functionTo){
			this.functionFrom = functionFrom;
			this.functionTo = functionTo;
			
			this.paths = new ArrayList<String>();
		}
}
