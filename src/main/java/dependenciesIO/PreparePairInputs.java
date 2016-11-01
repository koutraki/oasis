package dependenciesIO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import customization.Constants;
import download.DownloadManager;
import download.WebFunction;

public class PreparePairInputs {

	public static final HashSet<String>  setInputTypes(){
		/** functions accepting these inputs are considered as being known **/
		HashSet<String> types=new HashSet<String>();
		types.add("singers");
		types.add("songs");
		types.add("albums");
		types.add("actors");
		types.add("books");
		types.add("writers");
                types.add("cities");
		types.add("countries");
		
		types.add("singers_id_deezer");
		types.add("singers_id_discogs");
		types.add("singers_id_echonest");
		types.add("singers_id_last_fm");
		types.add("singers_id_music_brainz");
		types.add("singers_id_musixmatch");
		types.add("singers_mbid_musixmatch");
		
		
		types.add("actors_id_themoviedb");
	
		types.add("songs_id_echonest");
		types.add("songs_id_last_fm");		
		types.add("songs_id_music_brainz");		
		types.add("songs_id_musixmatch");
		
		types.add("writers_id_isbndb");
		types.add("writers_id_library_thing");
		
		types.add("albums_id_deezer");	
		types.add("albums_id_discogs");		
		types.add("albums_id_last_fm");		
		types.add("albums_id_music_brainz");
		types.add("albums_id_musixmatch");
		return types;
	}

	/*************************************************/
	/** Initialize sites to process **/
	/*************************************************/
	public static HashMap<String, Site> getSitesToProcess(Collection<String> knownTypes) throws Exception{
		HashMap<String, Site> sites=new HashMap<String, Site>();
		
		BufferedReader br = new BufferedReader(new FileReader(Constants.fileWithTheInputTypesOfTheFunctions));
		String line;
		while ((line = br.readLine()) != null) {
			line=line.trim();
			String[] splits = line.split(Constants.separatorSpace);
			
			if(splits.length<2) continue;
			/** if the site is not among the sites of the working set, continue **/
			String site=splits[1];
			if(DownloadManager.stubs.get(site)==null) continue;
			
			/** if the type is not among the analyzed types, continue **/
			/** if the function has no input type defined, continue **/
			String type=null;
			if(splits.length>=3) type=splits[2];
			
			/** get the stub of the function & compute samples using as input values from the file associated to the type **/
			String function=splits[0];
			WebFunction f=DownloadManager.getWebFunctionForSiteAndFunctionName(site, function);
			f.type=type;
			
			Site siteMeta=sites.get(site);
			if(siteMeta==null) {
				siteMeta=new Site(site);
				sites.put(site, siteMeta);
			}
			
			/** if the set of types is not known then all functions are from and to **/
			if(knownTypes==null){
				siteMeta.from.add(f);
				if(!siteMeta.to.contains(f)) siteMeta.to.add(f);
			}else 
				{	
					if(knownTypes.contains(type) ) {
												siteMeta.from.add(f);
						}
					else if(!siteMeta.to.contains(f)) siteMeta.to.add(f); 
			    		if(Constants.testAllPairs || (type!=null && type.contains("id"))){
			    			if(!siteMeta.to.contains(f)) siteMeta.to.add(f);
			    		}
				}
			
		}
		br.close();
		return sites;
	}

}
