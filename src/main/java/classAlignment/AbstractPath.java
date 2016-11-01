package classAlignment;

import java.util.ArrayList;


public  final class AbstractPath{
	final String path;
	ArrayList<Atom> asList=null;
	
	public AbstractPath(String orginal){
		this.path=orginal;
	}
	
	public String toString(){
		return path;
	}
	
	public String getAnnotatedPath(){
		if(asList==null) return path;
		StringBuffer buff=new StringBuffer();
		for(Atom a:asList){
			if(buff.length()>0) buff.append("/");
			buff.append(a.name);
			if(a.isMultiple) buff.append("+");
			
		}
		return buff.toString();
	}
	
	public AbstractPath  getPrefixForLastMultiple(){
		int i=asList.size()-1;
		for(; i>=0; i--){
			if(asList.get(i).isMultiple) break;
		}
		
		if (i<0) return null;
		ArrayList<Atom> asListPredicate=new ArrayList<Atom>();
		StringBuffer prefix=new StringBuffer();
		for(int j=0; j<=i; j++){
			if(prefix.length()>0) prefix.append("/");
			prefix.append(asList.get(j).name);
			Atom copyAtom=new Atom(asList.get(j).name);
			copyAtom.isMultiple=asList.get(j).isMultiple;
			asListPredicate.add(copyAtom);
		}
		AbstractPath path=new AbstractPath(prefix.toString());
		path.asList=asListPredicate;
		return path;
	}

	
	
	public AbstractPath getSufixForLastMultiple(){
		if(asList==null) return null;
		/** get the last element with + **/
		int i=asList.size()-1;
		for(; i>=0; i--){
			if(asList.get(i).isMultiple) break;
		}
		if (i<0) return null;
		
		/**create the new relative path **/
		
		ArrayList<Atom> asListPredicate=new ArrayList<Atom>();
		StringBuffer suffix=new StringBuffer();
		for(int j=i+1; j<asList.size(); j++){
			if(suffix.length()>0) suffix.append("/");
			suffix.append(asList.get(j).name);
			Atom copyAtom=new Atom(asList.get(j).name);
			copyAtom.isMultiple=asList.get(j).isMultiple;
			asListPredicate.add(copyAtom);
		}
		AbstractPath predicatePath=new AbstractPath(suffix.toString());
		predicatePath.asList=asListPredicate;
		return predicatePath;
	}

	
	public static final class Atom{
		public final String name;
		public boolean isMultiple=false;
		
		public Atom(String name){
			this.name=name;
		}

		@Override
		public String toString(){
			if(isMultiple==true) return name+"+";
			else return name;
		}
		
	}
}
