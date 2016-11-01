package classAlignment;

import java.util.ArrayList;


public final class ClassPair{
public final AbstractPath classKB;
public final AbstractPath classTree;
public final ArrayList<PathPair> predicatePaths=new ArrayList<PathPair>();

/** if several options for the same class, then we have to chose the best ones
 * some options become dominated by others **/
boolean isDominated=false;


	public  ClassPair(AbstractPath classKB, AbstractPath classTree){
			this.classKB=classKB;
			this.classTree=classTree;
	}
	/*** confidence **/
	public float getMaxConfidence(){
		float max=0;
		for(PathPair p: predicatePaths){
			if(p.confidence>max) max=p.confidence;
		}
		return max;
	}

	public ArrayList<PathPair> getPairWithHighestConfidence(float val){
		ArrayList<PathPair> list=new ArrayList<PathPair>();
		for(PathPair p: predicatePaths){
		if(p.confidence>=val) list.add(p);
		}
		return list;
	}

	/** toString **/
	@Override
	public String toString(){
		StringBuffer buff=new StringBuffer();
   
		buff.append(classKB.getAnnotatedPath()+" "+classTree.getAnnotatedPath()+"\n");
		for(PathPair p:predicatePaths){
			String annotatedKBPath=(p.KBpath==null)?ComputePrecisionRecall.text:p.KBpath.getAnnotatedPath();
			String annotatedTreePath=(p.treePath==null)?ComputePrecisionRecall.text:p.treePath.getAnnotatedPath();
			buff.append("\t"+annotatedKBPath+" "+annotatedTreePath+" "+p.confidence+"\n");
		}
		return buff.toString();
	}


	public String toStringOnlyBestMatches(){
	   StringBuffer buff=new StringBuffer();
	   
	   buff.append(classKB.getAnnotatedPath()+" "+classTree.getAnnotatedPath()+"\n");
	   for(PathPair p:predicatePaths){
		   if(p.isDominated) continue;
		   String annotatedKBPath=(p.KBpath==null)?ComputePrecisionRecall.text:p.KBpath.getAnnotatedPath();
		   String annotatedTreePath=(p.treePath==null)?ComputePrecisionRecall.text:p.treePath.getAnnotatedPath();
		   buff.append("\t"+annotatedKBPath+" "+annotatedTreePath+" "+p.confidence+"\n");
	   }
	   return buff.toString();
	}

}