package latexPlots;

import java.text.DecimalFormat;
import java.util.Collections;

public class AvgPrecisionRecallEntityResolutionObj {

	DecimalFormat df = new DecimalFormat("#.##");
	
	String functionName;
	
	float totalClassPrecision = 0.0f;
	float totalClassRecall = 0.0f;
	float totalClassFmeasure = 0.0f;
	
	float totalRelationPrecision = 0.0f;
	float totalRelationRecall = 0.0f;
	float totalRelationFmeasure = 0.0f;
	
	String averageClassPrecision;
	String averageClassRecall;
	String averageClassFmeasure;
	
	
	String averageRelationPrecision;
	String averageRelationRecall;
	String averageRelationFmeasure;
	
	
	float CompetitortotalClassPrecision = 0.0f;
	float CompetitortotalClassRecall = 0.0f;
	float CompetitortotalClassFmeasure = 0.0f;
	
	float CompetitortotalRelationPrecision = 0.0f;
	float CompetitortotalRelationRecall = 0.0f;
	float CompetitortotalRelationFmeasure = 0.0f;
	
	String CompetitoraverageClassPrecision;
	String CompetitoraverageClassRecall;
	String CompetitoraverageClassFmeasure;
	
	
	String CompetitoraverageRelationPrecision;
	String CompetitoraverageRelationRecall;
	String CompetitoraverageRelationFmeasure;
	
	int numberOfFunctions = 0;
	int numberOfFunctionsCompetitor = 0;
	
	
	public AvgPrecisionRecallEntityResolutionObj(String functionName, float totalClassPrecision,float totalClassRecall,	float totalClassFmeasure,	float totalRelationPrecision,	float totalRelationRecall,float totalRelationFmeasure){
		this.functionName = functionName;
		
		this.totalClassPrecision = totalClassPrecision;
		this.totalClassRecall = totalClassRecall;
		this.totalClassFmeasure = totalClassFmeasure;
		
		this.totalRelationPrecision = totalRelationPrecision;
		this.totalRelationRecall = totalRelationRecall;
		this.totalRelationFmeasure = totalRelationFmeasure;
		
		addNumberOfFunctions();
	}
	
	
	public void addValuesToExistingObject(float totalClassPrecision,float totalClassRecall,	float totalClassFmeasure,	float totalRelationPrecision,	float totalRelationRecall,float totalRelationFmeasure){
		this.totalClassPrecision += totalClassPrecision;
		this.totalClassRecall += totalClassRecall;
		this.totalClassFmeasure += totalClassFmeasure;
		
		this.totalRelationPrecision += totalRelationPrecision;
		this.totalRelationRecall += totalRelationRecall;
		this.totalRelationFmeasure += totalRelationFmeasure;
		
		addNumberOfFunctions();
	}
	
	public void addValuesToExistingObjectForCompetitor(float totalClassPrecision,float totalClassRecall,	float totalClassFmeasure,	float totalRelationPrecision,	float totalRelationRecall,float totalRelationFmeasure){
		this.CompetitortotalClassPrecision += totalClassPrecision;
		this.CompetitortotalClassRecall += totalClassRecall;
		this.CompetitortotalClassFmeasure += totalClassFmeasure;
		
		this.CompetitortotalRelationPrecision += totalRelationPrecision;
		this.CompetitortotalRelationRecall += totalRelationRecall;
		this.CompetitortotalRelationFmeasure += totalRelationFmeasure;
		
		addNumberOfFunctionsCompetitor();
	}
	
	public void addNumberOfFunctions() {
		this.numberOfFunctions++;
	}
	
	public void addNumberOfFunctionsCompetitor() {
		this.numberOfFunctionsCompetitor++;
	}
	
	public void calculateAveragePrecisionRecall(){
		this.averageClassPrecision = df.format(this.totalClassPrecision/this.numberOfFunctions);
		this.averageClassRecall = df.format(this.totalClassRecall/this.numberOfFunctions);
		this.averageClassFmeasure = df.format(this.totalClassFmeasure/this.numberOfFunctions);
		
		
		this.averageRelationPrecision = df.format(this.totalRelationPrecision/this.numberOfFunctions);
		this.averageRelationRecall = df.format(this.totalRelationRecall/this.numberOfFunctions);
		this.averageRelationFmeasure = df.format(this.totalRelationFmeasure/this.numberOfFunctions);
	}
	
	public void calculateAveragePrecisionRecallCompetitor(){
		this.CompetitoraverageClassPrecision = df.format(this.CompetitortotalClassPrecision/this.numberOfFunctionsCompetitor);
		this.CompetitoraverageClassRecall = df.format(this.CompetitortotalClassRecall/this.numberOfFunctionsCompetitor);
		this.CompetitoraverageClassFmeasure = df.format(this.CompetitortotalClassFmeasure/this.numberOfFunctionsCompetitor);
		
		
		this.CompetitoraverageRelationPrecision = df.format(this.CompetitortotalRelationPrecision/this.numberOfFunctionsCompetitor);
		this.CompetitoraverageRelationRecall = df.format(this.CompetitortotalRelationRecall/this.numberOfFunctionsCompetitor);
		this.CompetitoraverageRelationFmeasure = df.format(this.CompetitortotalRelationFmeasure/this.numberOfFunctionsCompetitor);
	}
	
        
  
}
