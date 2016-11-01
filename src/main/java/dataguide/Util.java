package dataguide;


import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;




/**
 * 
 * @author Nicoleta Preda (andapreda@gmail.com)
 *
 */
public class Util {
	
	public Util(){}
	
		
	
	
	
	
	/***************************************************************************/
							/** NORMALIZE WORDS*/
	/***************************************************************************/
	public String normalize(String s)
	{
			if(s==null) return "";
			return s.trim().toLowerCase();
	}
	
	/***************************************************************************/
							/** TRANSFORM FILE TO PATH*/
	/***************************************************************************/
	
	public String transformPathToURL(String path, char separator) {
		String newPath = "";
		if (separator == '/') {
			if (path.charAt(0) == '/')
				newPath = path;
			else
				newPath = "/" + path;
		} else {
			newPath = "file:\\\\\\";
			for (int i = 0; i < path.length(); i++)
				if (path.charAt(i) != '\\')
					newPath = newPath + path.charAt(i);
				else
					newPath = newPath + '/';
		}
		return newPath;
	}
	
	/***************************************************************************/
			/**HASH WITH ENTRY LIST ASSOCIATED*/
	/***************************************************************************/
/**	public static void addToConceptListIfNot(HashMap map, Object key,Object value)
	{
		Pair  pair=(Pair)map.get(key);
		if(pair==null) 	{ pair=new Pair(new ArrayList(),null); map.put(key,pair);}									
		if(pair.first()==null) pair.o1=new ArrayList();
		
		ArrayList list=(ArrayList)pair.first();
		if(!list.contains(value)) list.add(value);	
	}*/
	
/*	public static void addToFragmentsIfNot(HashMap map, Object key,Object value)
	{
		Pair  pair=(Pair)map.get(key);
		ArrayList fragments;
		if(pair==null) 	{ pair=new Pair(null,new ArrayList()); map.put(key,pair);}									
		if(pair.second()==null) pair.o2=new ArrayList();
		
		ArrayList list=(ArrayList)pair.second();
		if(!list.contains(value)) list.add(value);		
	}
	
	public String cleanQuery(String fragm)
	{
		fragm=fragm.replace('\"','\'');
		StringTokenizer st = new StringTokenizer(fragm);
		fragm="";
		while (st.hasMoreTokens()) {
     				 String s=st.nextToken();
     				 fragm+=" "+s;
    			}
		return fragm;
	}
	*/
	/** ************************************************************************ */
	/** SIMPLE CHARACTERS for the outputfile otherwise drapgviz doesn't compile*/
	/** ************************************************************************ */
	public static String simpleCaracters(String s) {
		if (s == null)		return "null";
		s=s.trim().toLowerCase();
		
		int i=s.lastIndexOf("/");
		if(i>0) s=s.substring(0,i);
		
		int j=s.lastIndexOf("\\");
		if(j>0) s=s.substring(0,j);
		
		j=s.indexOf(".");
		if(j>0) s=s.substring(0,j);
		
		s=s.replaceAll("\\p{Punct}","X");
		
		return s;
	}
	
	/** ************************************************************************ */
	/** PROCESS SYSTEM FILE AND WEB FILES */
	/** ************************************************************************ */

	public String getFileName(String filePath)
	{
		if(filePath==null) return null;
		int i=filePath.lastIndexOf("/");
		if(i>0) return filePath.substring(i+1);
		
		
		int j=filePath.lastIndexOf("\\");
		if(j>0) return filePath.substring(j+1);
		
		return filePath;
	}
	
	
	public String getDirPath(String filePath)
	{
		if(filePath==null) return null;
		int i=filePath.lastIndexOf("/");
		if(i>0) return filePath.substring(0,i);
		
		
		int j=filePath.lastIndexOf("\\");
		if(j>0) return filePath.substring(0,j);
		
		return "";
	}
		
	public String getFileNameOfURI(String sourceURI)
	{
			    URI uri=getURI(sourceURI);
			    return getFileNameOfURI(uri);
	}

	public String getFileNameOfURI(URI uri)
	{
			    if(uri==null) return null;
			    uri=uri.normalize();			    		    
			    String path=uri.getPath();
			   
			    return getFileName(path);
	}
	
	public String getDirPathOfURI(String sourceURI)
	{
			    URI uri=getURI(sourceURI);
			    if(uri==null) return null;
			    uri=uri.normalize();			    		    
			    String path=uri.getPath();
			    return getDirPath(path);
	}

	 // [scheme:][//authority][path][?query][#fragment]
	 //	authority:- [user-info@]host[:port]
	 public URI getURI(String oldURI)
	 {
	 	
	 	URI uri;
	 	try{
	 		uri= new URI(oldURI);
	 	} catch(Exception e)
		{
	 		System.out.println("[ChangeDTDSystem] Exception Malformed URL"); 
	 		return null;	 		
		}
	 	
	 	return uri;
	 }
	 
	 public String getNormalizedURI(String stringURI)
	 {
	 	URI uri=getURI(stringURI);
		if(uri!=null) {
			uri=uri.normalize();
			stringURI=uri.toString();
		}
		return stringURI;	
	 }
	
	 public InputStream getInputStream(URI uri)
	 {
	 		
	 		if(uri.getScheme()==null) 
	 			try{	
	 					InputStream rez=new FileInputStream(uri.toString());
	 					return rez;
	 				}catch(Exception e)
						{	System.out.println("[Exception] "+e.getMessage());
							return null;
						}
    		try{
				URL fileURL=uri.toURL();
				URLConnection con = fileURL.openConnection();
				InputStream in = con.getInputStream();
				return in;								
			}
			catch(Exception e){ 
				System.out.println("[Util] "+e.getMessage());
				e.printStackTrace();
				return null;
				}	 
	 }
	 
	 public InputStream getInputStream(String stringURI)
	 {
	 	URI uri=getURI(stringURI);
	 	if(uri==null) return null;
	 	
	 	return getInputStream(uri);	 
	 }
	 
	 
	 public String resolve(String docSource, String dtdSource)
	 {
	 	URI docURI=getURI(docSource);
	 	URI dtdURI=getURI(dtdSource);
	 	
	 	if(docURI==null || dtdURI==null) return null;
	 	return (docURI.resolve(dtdURI)).normalize().toString();
	 
	 }
	 
	 
	 public boolean isSystemFile(String stringURI)
	 {
	 	URI uri=getURI(stringURI);
	 	if(uri==null) return false;
	 	
	 	if(uri.getScheme()==null) return true;
	 	return false;
	 
	 }
	 
	 

	 
	 /**
	  *	VECTOR OF BYTES 
	  */
	 
	 public static int compare(byte[] b1, int off1, byte[] b2, int off2, int length){
		/*
		System.out.println();
		for(int i=0;i<length;i++ )System.out.print(" "+b1[off1+i]);
		System.out.println();
		for(int i=0;i<length;i++ )System.out.print(" "+b2[off2+i]);
		System.out.println();
		*/
		
		for(int i=0;i<length; i++){
			if(b1[off1+i]==b2[off2+i]) continue;
			//System.out.println(" "+b1[off1+i]+" "+b2[off2+i]);
			
			if(b1[off1+i]<0)
				{
					if(b2[off2+i]>=0) return 1;
					else return (b1[off1+i]<b2[off2+i])?-1:1; 
				}
			else {
					if(b2[off2+i]<0)  return -1;
					else return (b1[off1+i]>b2[off2+i])?1:-1; 
				}			
		}
		return 0;
	}
	 

		
		public static boolean equalBytes(byte[] b1, int off1, byte[] b2, int off2, int length){
			for(int i=0;i<length; i++){
				if(b1[off1+i]!=b2[off2+i]) return false;
			}
			return true;
		}
	 
		
		public static byte[] concat(byte[] b1, byte[] b2){
			if(b1==null) return b2;
			if(b2==null) return b1;
			
			byte[] result= new byte[b1.length+b2.length];
			System.arraycopy(b1,0,result,0,b1.length);
			System.arraycopy(b2,0,result,b1.length,b2.length);
			
			return result;
			
		}
}



