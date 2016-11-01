package dependenciesIO.joinInputInstanceWithOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import arq.sse;
import customization.Constants;
import dependenciesIO.PreparePairInputs;
import dependenciesIO.Site;
import dependenciesIO.joinInputInstanceWithOutput.TreeClasses.NodeClass;
import download.DownloadManager;
import download.WebFunction;

public class LoadTreeClass {

	public static void main(String[] args) throws Exception
	{  
		 DownloadManager.initStubs();
		
		/** functions accepting these inputs are considered as being known **/
		HashSet<String> types=new HashSet<String>();
		types.add("singers");
		types.add("songs");
		types.add("albums");
		types.add("actors");
		types.add("books");
		types.add("writers");
		
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
		
		DownloadManager.processFunctionCallsInBatch(DownloadManager.prepareCallsToExecuteInBatch(types));
		
		/** separate the function of each site into from and to */
		HashMap<String, Site> map=PreparePairInputs.getSitesToProcess(types);
		
		LoadTreeClass.load(map);
		
		/** now we test the some paths **/
		Site s=map.get("musixmatch");
		if(s==null){
			System.out.println("Ups, the site is null ");
			System.exit(0);
		}
		
		WebFunction f=DownloadManager.getWebFunctionForSiteAndFunctionName("musixmatch", "getTracksByArtistName");
		
	
		TreeClasses tree=f.tree;
		NodeClass n=tree.getClassOfProperty("/message/body/track_list/track/artist_id/");
		HashSet<String> paths=new HashSet<String>();
		if(n!=null){
			n.getRelativePathsToChildren(paths, null);
			for(String l:paths){
				System.out.println("     "+l);
			}
		}
	
	}

	public static final void parseResultsAlignment(String filepath,HashMap<String, Site> sites) {
		TreeClasses tree=new TreeClasses();
		
		/** add class of the input **/
		ArrayList<String> root_to_x=new ArrayList<String>();
		root_to_x.add("");
		
		ArrayList<TreeClasses.NodeClass> nodes_x=new ArrayList<TreeClasses.NodeClass>();
		int count_x=0;
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
			String sCurrentLine;
			
			while ((sCurrentLine = br.readLine()) != null) {
	
				if (!sCurrentLine.isEmpty()) {
					sCurrentLine.trim();
					if (sCurrentLine.startsWith("F:"))
						{
								String functionAndSite=sCurrentLine.split("F:")[1].trim();
								String[] parts = functionAndSite.split(Constants.separatorSpace);
								
								String site=parts[0];
								String function=parts[1];
								
								String msg="***"+site+"*** ***"+function;
								//if(! "***music_brainz*** ***getTracksByArtistName".equals(msg)) return;
								System.out.println("The function:"+msg);
								
								/** if the site does not belong to the working set **/
								if(sites.get(site)==null) return;
								
								WebFunction f=DownloadManager.getWebFunctionForSiteAndFunctionName(site, function);
								f.tree=tree;
								
						}
					else {
						System.out.println(sCurrentLine);
						String pair=null;
						if(sCurrentLine.startsWith("R:")){ pair=sCurrentLine.split("R:")[1];}
						if(sCurrentLine.startsWith("C:")){  pair=sCurrentLine.split("C:")[1];
														   count_x++;
														}
						
						String[] parts = pair.trim().split(Constants.separatorSpace);
						/** 1 : is the variable for the KB **/
						/** 2 : if it starts with / then is the path in the XML **/
						if(parts[1].trim().startsWith("/")) {
							String pathFromRoot=(parts[1].equals("//"))?"":parts[1];
							root_to_x.add(pathFromRoot);
							String variable=parts[2]+count_x;
							TreeClasses.NodeClass v1=new TreeClasses.NodeClass (variable);
							tree.classes.put(variable, v1 );
							nodes_x.add(v1);
						}
						else {
							String var1=(parts[3].startsWith("x"))?parts[3]+count_x:parts[3];
							String pathBetween=parts[4];
							String var2=(parts[5].startsWith("x"))?parts[5]+count_x:parts[5];
							
							/** if it's the first line: that's the line with the x **/
							TreeClasses.NodeClass v1=tree.classes.get(var1);
							if(v1==null) {
								v1=new TreeClasses.NodeClass(var1);
								tree.classes.put(var1,v1);
								if(var1.startsWith("x")) nodes_x.add(v1);
							}
							
							if(pathBetween.startsWith("=") ){
								tree.classes.put(var2, v1);
								v1.names.add(var2);
							}else{
								
								TreeClasses.NodeClass v2=tree.classes.get(var2);
								if(v2==null){
									v2=new TreeClasses.NodeClass(var2);
									tree.classes.put(var2, v2);
								}
								
								v2.completePathInfo(pathBetween, v1); 
							}	
						}
					}
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/** post-process the prefixes **/
		LoadTreeClass.initPrefixesForX(nodes_x,root_to_x);
		for(TreeClasses.NodeClass n: tree.classes.values()){
			n.fillPrefixesFromRoot(root_to_x);
		}
		
		/** create the unique tree structure **/
		tree.createOneTree(nodes_x);
		
		System.out.println(tree);
	}

	/*************************************************************/
	/** STATIC FUNCTIONS
	/*************************************************************/
	
	public static final void load(HashMap<String, Site> sites){
		
		/** for each file with class and relation alignments **/
		File folder = new File(Constants.newformClassAndRelation);
		File[] listOfFiles = folder.listFiles();
	
		for (File file : listOfFiles) {
			if (file.isFile()) {
				System.out.println("***************************");
				System.out.println("I load "+file.getName());
				parseResultsAlignment(file.getAbsolutePath(), sites);
			}
		}
		
	}

	/** this function applies only to x nodes **/
	public static final void initPrefixesForX(ArrayList<TreeClasses.NodeClass> x_nodes, ArrayList<String> root_to_x){
		/** get the non-empty path to the XML root **/
		String max_path="";
		for(String path:root_to_x){
			if(max_path.length()<path.length()) max_path=path;
		}
		if(max_path.length()==0) return;
		String[] max_path_elems=max_path.split("/");
		String root_x=( max_path_elems[0].length()>0)? max_path_elems[0]: max_path_elems[1];
		
		/** if the child starts with the root node, then set the prefix to void **/ 
		for(TreeClasses.NodeClass x:x_nodes){	
				/** child connected to the root **/
				boolean childConnectedToTheRoot=false;
				
				for(TreeClasses.NodeClass child:x.children){
					        //System.out.println("      Child="+child.names);
					        String[] childElems=child.pathToParent.split("/");
							String root_child=(childElems[0].length()>0)?childElems[0]:childElems[1];
							if(root_x.equalsIgnoreCase(root_child))  childConnectedToTheRoot=true;
				}
				
				x.prefixesFromRoot=new HashSet<String>();
				if(childConnectedToTheRoot) {
					x.prefixesFromRoot.add("");
					x.pathToParent="/";
				}else {
					for(String path:root_to_x){
						if(path.length()==0) continue;
						x.prefixesFromRoot.add(path);
					}
					
				}
			
		}
	}

}
