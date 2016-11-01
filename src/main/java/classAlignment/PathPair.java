package classAlignment;



public final class PathPair{
	
	public final AbstractPath KBpath;
	public final AbstractPath treePath;
	public final float confidence;
	
	/** set to true if there is a mapping of the same treePath to another relation from KB that has greater confidence **/
	public boolean isDominated=false;

	public PathPair(AbstractPath KBpath, AbstractPath treePath, float confidence){
		this.KBpath=KBpath;
		this.treePath=treePath;
		this.confidence=confidence;
	}
	
	public String toString(){
		return KBpath+" "+treePath+" "+confidence;
	}
}
