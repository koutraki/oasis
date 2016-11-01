package classAlignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import customization.Constants;
import download.WebFunction.PairValidityAndErrorMessage;
import java.util.Iterator;

public final class ForFunctionMetadata{
	public String function;
	public String site;
	public String inputType;
	public String fileWithAlignmentResults;
	
	/** the list with all pairs; will be null once the pairs are transfered to the other two lists **/
	public ArrayList<PathPair> annotatedPairs=null;
	
	/** the union of the two lists is the set of all pairs **/
	public ArrayList<PathPair> pairsEndingWithFunctinalRelations=new ArrayList<PathPair>();
	public ArrayList<PathPair> pairsWhereAtLeastOnePathEndsWithOneToManyRelation=new ArrayList<PathPair>();
	
	/** the list of candidates for mapping **/
	HashMap<String,ArrayList<ClassPair>> candidates=new HashMap<String,ArrayList<ClassPair>>();
        
        /* HashMap with concat relations , Array classPair**/
        HashMap<String,ArrayList<ClassPair>> newDeduplicate=new HashMap<String,ArrayList<ClassPair>>();

	public ForFunctionMetadata(String function, String site, String fileWithAlignementResults, String inputType){
		this.function=function;
		this.site=site;
		this.inputType=inputType;
		this.fileWithAlignmentResults=fileWithAlignementResults;
		
	}
	
	public ClassPair getCandidateEqualTo(ClassPair candidate, HashMap<String,ArrayList<ClassPair>> map){
			ArrayList<ClassPair> treeClasses=map.get(candidate.classKB.path);
			if(treeClasses==null) return null;
			for(ClassPair c:treeClasses){
				if(c.classTree.path.equals(candidate.classTree.path)) return c;
			}
			return null;
		}
		
	public ClassPair addCandidate(ClassPair candidate, HashMap<String,ArrayList<ClassPair>> map){
			ArrayList<ClassPair> treeClasses=map.get(candidate.classKB.path);
			if(treeClasses==null){
				treeClasses=new ArrayList<ClassPair>();
				map.put(candidate.classKB.path, treeClasses);
				treeClasses.add(candidate);
				return candidate;
			}
			
			for(ClassPair c:treeClasses){
				if(c.classTree.path.equals(candidate.classTree.path)) return null;
			}
			treeClasses.add(candidate);
			return candidate;
		}
	
	public final String printResultsToFile(String filePath) throws Exception{
		FileWriter fOut = new FileWriter(filePath);
		String newLine = System.getProperty("line.separator");
		String print=getResultToPrint();
		fOut.write(print+newLine);
		fOut.close();
		return print;
	}
		
	/** is used when we print results that are used for computing the precision and the recall **/
	public String getResultToPrint(){
              //  getDeduplicatedClasses();
            
            
		StringBuffer buff=new StringBuffer();
		buff.append(ComputePrecisionRecall.prefixLineFunction+" "+function+" "+site+"\n");
		for(String KBClass: candidates.keySet()){
			ArrayList<ClassPair> classPairs=candidates.get(KBClass);
			for(ClassPair c:classPairs){
//					if(!AlgorithmClassAlignment.removeSteps234) 
//                                            if(c.isDominated) 
//                                                continue;
                                        //if(c.classKB.asList!=null && c.classKB.asList.size()>1) continue;
					buff.append(ComputePrecisionRecall.prefixLineClass+"\t"+c.classKB.path+"\t"+c.classTree.path+"\n");
					for(PathPair p:c.predicatePaths){
						if(!AlgorithmClassAlignment.removeSteps234) if(p.isDominated) continue;
						buff.append(ComputePrecisionRecall.prefixLineRelations+"\t\t"+p.KBpath.path+"\t\t"+p.treePath.path+"\t"+p.confidence+"\n");
					}	
					buff.append("\n");
				}
			}
		buff.append("\n"+ComputePrecisionRecall.prefixDebug);
		
		buff.append(toString());
		
		return buff.toString();
	}
        
        
        /** is used when we print results that are used for computing the precision and the recall **/
	public void getDeduplicatedClasses(){
		HashMap<String, ArrayList<ClassPair>> newMap=new HashMap<String, ArrayList<ClassPair>>();
		for(String KBClass: candidates.keySet()){
			ArrayList<ClassPair> classPairs=candidates.get(KBClass);
			for(ClassPair c:classPairs){
                            
                                        StringBuffer buff= new StringBuffer();
                                        for(PathPair p:c.predicatePaths){
                                            if(!AlgorithmClassAlignment.removeSteps234) if(p.isDominated) continue;
				            buff.append("r:"+p.treePath.path);
                                        }
                                        String newKey=buff.toString();
                                        ArrayList<ClassPair> list=newMap.get(newKey);
                                        if(list==null){
                                            list= new ArrayList<ClassPair>();
                                            newMap.put(newKey, list);
                                        }
                                        list.add(c);
				}
			}
                
                for(String KBClass: newMap.keySet()){
                    ArrayList<ClassPair> classPairs=newMap.get(KBClass);
	            Iterator<ClassPair> it=classPairs.iterator();
                    it.next();
                    
                    while(it.hasNext()){
                        it.next();
                        it.remove();
                    }
                }
                
                newDeduplicate=newMap;
	}
		
		
	@Override
	public String toString(){
		StringBuffer buff=new StringBuffer();
				
		buff.append("----STEP 2 & 3----: Find candidates for class alignment (some classes appear in several places) \n");
		buff.append("Good Candidates:\n");
		for(String KBClass: candidates.keySet()){
		ArrayList<ClassPair> mapTreeClasses=candidates.get(KBClass);
				
				
		for(ClassPair c:mapTreeClasses){
				if(c.isDominated) continue;
				buff.append(c.toString()+"\n");
			}
				
		}
		
		buff.append("\n");
			
		StringBuffer buff2=new StringBuffer();
		for(String KBClass: candidates.keySet()){
			ArrayList<ClassPair> mapTreeClasses=candidates.get(KBClass);
				
			for(ClassPair c:mapTreeClasses){
					if(!c.isDominated) continue;
					buff2.append(c.toString()+"\n");
				}
			}
		if(buff2.length()>0)	buff.append("Dominated/Bad Candidates :\n");
		buff.append(buff2.toString()+"\n");
			
			
		buff.append("----STEP 1----:  remove the paths ending with one-to-many relations.\n");
		buff.append("Paths ending with functional relations:\n");
		for(PathPair p: pairsEndingWithFunctinalRelations){
				buff.append(p.KBpath.getAnnotatedPath()+" "+p.treePath.getAnnotatedPath()+" "+p.confidence+"\n");
			}
		buff.append("\n");
			
		buff.append("Removed paths from the computation:\n");
		for(PathPair p: pairsWhereAtLeastOnePathEndsWithOneToManyRelation){
				buff.append(p.KBpath.getAnnotatedPath()+" "+p.treePath.getAnnotatedPath()+" "+p.confidence+"\n");
		}
			
		buff.append("****************************\n\n");
		return buff.toString();
	}
}