package dependenciesIO.outputPathWithInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import customization.Constants;
import dataguide.DataGuide;
import dataguide.Node;
import dataguide.ValueNode;
import dataguide.ValueNode.SimpleCompare;
import dataguide.XMLPathPair;
import dependenciesIO.DGWithAllTheValues;
import dependenciesIO.IOPair;
import dependenciesIO.Site;
import dependenciesIO.DGWithAllTheValues.PairValueListOfDocuments;
import dependenciesIO.IOPair.InputPathStat;
import dependenciesIO.XMLPathsOverlapping.OverlappingPathsInDGs;

public class IOManager {

	public static final void  initValidPaths(Site s) throws Exception {
		for(IOPair p:s.sortedFirstFrom){
			for(String path:s.dataGuides.get(p.f_from).pathsValuesAndTheirDocuments.keySet()){
				p.validPaths.add(path);
			}
		}
	}
	
	
	
	
	public static final void  initializeDependenciesNew(Site s, int noValuesPerPath) throws Exception {
		for(IOPair p:s.sortedFirstFrom){
			DGWithAllTheValues paths=s.dataGuides.get(p.f_from);
			paths.initIterator();
			int rounds=0;
			
			while(paths.hasNext() && rounds<noValuesPerPath){
				DGWithAllTheValues.PairValueListOfDocuments pathValueDocuments=paths.next();
				if(pathValueDocuments==null){
					  /**System.out.println("A round is finished \n\n");**/
					  rounds++;
					  continue;
				  }
				if(!p.validPaths.contains(pathValueDocuments.path)) continue;
				
				String input=pathValueDocuments.value;
				if(input!=null)  input=Constants.transformStringForURL(input.trim());
				
				String file=p.f_to.executeCall(input,true);
				if(file!=null){  /**add the path to the list of paths that are valid for this function **/
						  		/** now is already there : p.validPaths.add(pathValueDocuments.path); **/
					  }
				else{ /** URL exception: I add path to the list of invalid paths **/
					 p.pathsWithURLExceptions.add(pathValueDocuments.path);
					 p.validPaths.remove(pathValueDocuments.path);
				} 
			}
			
		}
	}
	
	

	public static void eliminatePathsWithConstants(Site s ){
	
		for(int i=0; i<s.sortedFirstFrom.size();i++){
			/** compute the paths for the first occurrence of the f_from */
			IOPair p=s.sortedFirstFrom.get(i);
			
			/** get the GataGuide for the f_from function **/ 
			DGWithAllTheValues dg=s.dataGuides.get(p.f_from);
			
			/** get the valid paths **/
			Iterator<String> it=p.validPaths.iterator();
			while(it.hasNext()){
				String path=it.next();
				int no_values=dg.getAllValuesUnderPath(path).size();
				
				if(no_values<=SolDetectOutputPathWithInput.skip_path_with_cardinalities_lower_than){
					it.remove();
					p.pathsWithConstantValues.add(path);
				}
			 }
			
			/** then, copy the results to the other pairs **/
			for(i=i+1;i<s.sortedFirstFrom.size() && s.sortedFirstFrom.get(i).f_from.equals(p.f_from);i++){
				s.sortedFirstFrom.get(i).pathsWithConstantValues.addAll(p.pathsWithConstantValues);
				s.sortedFirstFrom.get(i).validPaths.removeAll(p.pathsWithConstantValues);
			}
			i--;
		}
	}
	
	
	public static void eliminatePathsWithSmallNumericValues(Site s ){
		
		for(int i=0; i<s.sortedFirstFrom.size();i++){
			/** compute the paths for the first occurrence of the f_from */
			IOPair p=s.sortedFirstFrom.get(i);
			
			///get the GataGuide for the f_from function 
			DGWithAllTheValues dg=s.dataGuides.get(p.f_from);
			
			//get the valid paths
			Iterator<String> it=p.validPaths.iterator();
			while(it.hasNext()){
				String path=it.next();
				
				
				/** compute the average value for the numerical values **/
				boolean isAllNumeric=true;
				double sum=0;
				int count=0;
				Iterator<String> values=dg.getAllValuesUnderPath(path).iterator();
				while(values.hasNext() && isAllNumeric){
					String val=values.next();
					if(val==null) continue;
					if(!isNumeric(val)) isAllNumeric=false;
					else {
						sum+=Double.parseDouble(val);
						count++;
					}
				}
				if(isAllNumeric) {
					int avg=((int)sum/count);
					if(avg<Constants.maxAverageAuxiliaryMetadata){
						it.remove();
						p.pathsWithSmallNumericValues.add(path);
						//System.out.println("Under path "+path+" found values with average "+((int)avg));
					}
				}
				
				
			 }
			
			/** then, copy the results to the other pairs **/
			for(i=i+1;i<s.sortedFirstFrom.size() && s.sortedFirstFrom.get(i).f_from.equals(p.f_from);i++){
				s.sortedFirstFrom.get(i).pathsWithSmallNumericValues.addAll(p.pathsWithSmallNumericValues);
				s.sortedFirstFrom.get(i).validPaths.removeAll(p.pathsWithSmallNumericValues);
			}
			i--;
		}
	}
	
	 public static final String findPathsWhereInputsOccur(Site site, int no_samples_per_path){
			StringBuffer msg=new StringBuffer();
			for(IOPair p:site.sortedFirstFrom){
				//msg.append(p.f_from.functionName+"  ---> "+p.f_to.functionName+"\n");
				DGWithAllTheValues dg=site.dataGuides.get(p.f_from);
				
				
				for(String path:p.validPaths){
					HashMap<String, InputPathStat> pathsInToWhereInput=new HashMap<String, InputPathStat>();
					p.pathsWithInputsInTo.put(path, pathsInToWhereInput);
					
					Iterator<String> values=dg.getAllValuesUnderPath(path).iterator();
					int k=0;
					int trueCalls=0;
					while(k<no_samples_per_path && values.hasNext()){
						k++;
						String val=values.next();
						//Collection<String> documents=dg.getAllDocumentsForPathAndValue(path, val);
					
						String file_f_to=p.f_to.executeCall(val, true);
						//String fileTo=(file_f_to==null)?file_f_to:((file_f_to.lastIndexOf("/")>0)?file_f_to.substring(file_f_to.lastIndexOf("/")):file_f_to);
						
						if(file_f_to==null) continue;
						
						trueCalls++;
						
						DataGuide dg2=new DataGuide();
						dg2.makeparse(file_f_to);
						dg2.reInitMap();
						
						ValueNode.SimpleCompare c=new ValueNode.SimpleCompare();
						PriorityQueue<ValueNode> pq2=dg2.getValuesInAPriorityQueue(c);
						
						ValueNode valInput= new ValueNode(null, val);
						while(!pq2.isEmpty()){
							  ValueNode valueNode=pq2.poll();
							  //System.out.println("Compare: "+val+" and "+valueNode.value);
							  String pathInTo = valueNode.parent.getStringPathRootToNode();
							  InputPathStat stat=pathsInToWhereInput.get(pathInTo);
							  if(stat==null) {
								  stat=new InputPathStat();
								  pathsInToWhereInput.put(pathInTo, stat);
							  }
							  int comp=c.compare(valInput, valueNode);
							  stat.trueCalls=trueCalls;
							  if(comp==0) {
								  stat.noFoundUnderThisPath++;
								  stat.values.add(val);
							  }
							  if(comp<0) break;
						  }
					}
					
					System.out.println("PATH WITH INPUT"+pathsInToWhereInput);
				}
				
			}
			
			return msg.toString();
		}
	
	
	
	public static final String checkOverlapping(Site site, int no_samples_per_path){
		StringBuffer msg=new StringBuffer();
		for(IOPair p:site.sortedFirstFrom){
			/**if(!"getArtistInfoById  ---> getReleasesByArtistId".equals(p.f_from.functionName+"  ---> "+p.f_to.functionName))
				continue;**/
			
			msg.append(p.f_from.functionName+"  ---> "+p.f_to.functionName+"\n");
			DGWithAllTheValues dg=site.dataGuides.get(p.f_from);
			
			/** initialize the path overlapping counter **/
			ArrayList<HashMap<XMLPathPair, Integer>> largestOverlappings=new ArrayList<HashMap<XMLPathPair, Integer>>();
			int max_matched_paths=0;
			HashSet<String> bestPaths=new HashSet<String>();
			
			for(String path:p.validPaths){
				HashMap<XMLPathPair, Integer> pathPairsCount= new HashMap<XMLPathPair, Integer>();
				msg.append("     "+path+" no values "+dg.getAllValuesUnderPath(path).size()+"\n");
				if(path.equals("o/id/")) System.out.println("     "+path+" no values "+dg.getAllValuesUnderPath(path).size());
				
				Iterator<String> values=dg.getAllValuesUnderPath(path).iterator();
				int k=0;
				while(k<no_samples_per_path && values.hasNext()){
					k++;
					String val=values.next();
					Collection<String> documents=dg.getAllDocumentsForPathAndValue(path, val);
					
					
					/** I compute the overlapping only with the first document from the f_from **/
					String file_f_from=p.f_from.executeCall(documents.iterator().next(), true);
					String file_f_to=p.f_to.executeCall(val, true);
					
					/** initialize the DataGuides **/
					/**System.out.println(" Compute overlap "+file_f_from+"      "+file_f_to);**/
					String fileFrom=(file_f_from==null)?file_f_from:(file_f_from.lastIndexOf("/")>0)?file_f_from.substring(file_f_from.lastIndexOf("/")):file_f_from;
					String fileTo=(file_f_to==null)?file_f_to:((file_f_to.lastIndexOf("/")>0)?file_f_to.substring(file_f_to.lastIndexOf("/")):file_f_to);
					
					
					msg.append("              "+fileFrom+"  --->    "+fileTo+"\n");
					if(path.equals("o/id/")) System.out.println("*******************************"+fileFrom+"  --->    "+fileTo);
					/** initialize the DataGuides **/
					DataGuide dg1=new DataGuide();
					dg1.makeparse(file_f_from);
					dg1.reInitMap();
					  
					DataGuide dg2=new DataGuide();
					dg2.makeparse(file_f_to);
					dg2.reInitMap();
					
					new OverlappingPathsInDGs(dg1, dg2, new ValueNode.SimpleCompare(), pathPairsCount);
					
					if(path.equals("o/id/")) IOManager.inspectPaths(p, pathPairsCount, no_samples_per_path);
				}	
				
				int count_matches=IOManager.getMaximumMatches(p, pathPairsCount, no_samples_per_path);
				if(max_matched_paths<count_matches){
									max_matched_paths=count_matches;
									largestOverlappings.clear();
									bestPaths.clear();
									
									largestOverlappings.add(pathPairsCount);
									bestPaths.add(path);
							}
				 if(max_matched_paths==count_matches && max_matched_paths!=0){
								largestOverlappings.add(pathPairsCount);
								bestPaths.add(path);
							}
					
				
				
				String msgPairs=IOManager.showPathPairs(p, pathPairsCount, no_samples_per_path);
				if(path.equals("o/id/")) System.out.println("Final result: "+msgPairs);
				msg.append(msgPairs);
			}
			
			
			
			msg.append("    Best match: "+bestPaths+"\n\n");
			//System.out.println("    Best match: "+bestPaths);

			if(max_matched_paths==0){
				p.pathsWithLessOverlap.addAll(p.validPaths);
				p.validPaths.clear();
			}
			else if(bestPaths.size()!=0){
				/** if at least one is good, move all the other paths to the list of eliminated paths **/
				p.validPaths.remove(bestPaths);
				p.pathsWithLessOverlap.addAll(p.validPaths);
				p.validPaths.clear();
				p.validPaths.addAll(bestPaths);				
			}
			
			
		}
		
		return msg.toString();
	}

	/** number of valid matched paths: a path can be matched to at most one other path **/
	  public static final int getMaximumMatches(IOPair io, HashMap<XMLPathPair, Integer>  pathPairsCount, int no_samples_per_path){
		   if(pathPairsCount==null) return 0;
		   
		  
		   PriorityQueue<XMLPathPair> orderedPaths= new PriorityQueue<XMLPathPair>(pathPairsCount.keySet());
		   int no_paths=0;
		   String lastPath=null;
		   while(!orderedPaths.isEmpty()){
				  XMLPathPair p=orderedPaths.poll();
				  if(io.pathsWithConstantValues.contains(p.pathDG1)) continue;
				  if(io.pathsWithSmallNumericValues.contains(p.pathDG1))continue;
				  if(pathPairsCount.get(p)<SolDetectOutputPathWithInput.thresholdForOverlappingPaths*no_samples_per_path) continue;
				  if(lastPath!=null && lastPath.equals(p.pathDG1)) continue;
				  no_paths++;
				  lastPath=p.pathDG1;
				 
			  }
		   return no_paths;
	   }

	/** return true if the paths should be eliminated **/
	  public static final String showPathPairs(IOPair io, HashMap<XMLPathPair, Integer>  pathPairsCount, int no_samples_per_path){
		   if(pathPairsCount==null) return "";
		   StringBuffer buff=new StringBuffer();
		   PriorityQueue<XMLPathPair> orderedPaths= new PriorityQueue<XMLPathPair>(pathPairsCount.keySet());
		   String lastPath=null;
		   while(!orderedPaths.isEmpty()){
				  XMLPathPair p=orderedPaths.poll();
				  if(io.pathsWithConstantValues.contains(p.pathDG1)) continue;
				  if(io.pathsWithSmallNumericValues.contains(p.pathDG1)) continue;
				  if(pathPairsCount.get(p)<SolDetectOutputPathWithInput.thresholdForOverlappingPaths*no_samples_per_path) continue;
				  if(lastPath!=null && lastPath.equals(p.pathDG1)) continue;
				  buff.append("                "+p.pathDG1+"\t"+p.pathDG2+"\t"+pathPairsCount.get(p)+"\n");
				  lastPath=p.pathDG1;
				 
			  }
		   return buff.toString();
	   }

	  
	  public static final String inspectPaths(IOPair io, HashMap<XMLPathPair, Integer>  pathPairsCount, int no_samples_per_path){
		   if(pathPairsCount==null) return "";
		   StringBuffer buff=new StringBuffer();
		   PriorityQueue<XMLPathPair> orderedPaths= new PriorityQueue<XMLPathPair>(pathPairsCount.keySet());
		   String lastPath=null;
		   while(!orderedPaths.isEmpty()){
				  XMLPathPair p=orderedPaths.poll();
				  if(io.pathsWithConstantValues.contains(p.pathDG1)) continue;
				  if(io.pathsWithSmallNumericValues.contains(p.pathDG1)) continue;
				  if(pathPairsCount.get(p)<SolDetectOutputPathWithInput.thresholdForOverlappingPaths*no_samples_per_path) continue;
				  if(lastPath!=null && lastPath.equals(p.pathDG1)) continue;
				  System.out.println("                "+p.pathDG1+"\t"+p.pathDG2+"\t"+pathPairsCount.get(p));
				  lastPath=p.pathDG1;
				 
			  }
		   return buff.toString();
	   }
	  
	  
	  public static final  boolean isNumeric(String str)
	  {
		  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
		}
	  
	  
	 
	  
}
