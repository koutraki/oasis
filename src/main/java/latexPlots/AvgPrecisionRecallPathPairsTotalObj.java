package latexPlots;

import java.text.DecimalFormat;

public class AvgPrecisionRecallPathPairsTotalObj {
	DecimalFormat df = new DecimalFormat("#.##");
	
	
	String functionName;
	String inputType;
        
	float totalPrecisionOverlapping = 0.0f;
	float totalRecallOverlapping = 0.0f;
        
	float totalPrecisionWithCycles = 0.0f;
	float totalRecallWithCycles = 0.0f;
	float totalPrecisionKBToXML = 0.0f;
	float totalRecallKBToXML = 0.0f;
        float totalPrecisionXMLToKB = 0.0f;
	float totalRecallXMLToKB = 0.0f;
        
	
	String averagePrecisionOverlapping;
	String averageRecallOverlapping;
	String averagePrecisionWithCycles;
	String averageRecallWithCycles;
	String averagePrecisionKBToXML;
	String averageRecallKBToXML;
        String averagePrecisionXMLToKB;
	String averageRecallXMLToKB;
	
	int numberOfFunctionsWithTheSameNameOverlapping = 0;
	int numberOfFunctionsWithTheSameNameWithCycles = 0;
	int numberOfFunctionsWithTheSameNameKBToXML = 0;
        int numberOfFunctionsWithTheSameNameXMLToKB = 0;

	public AvgPrecisionRecallPathPairsTotalObj(String functionName, String inputType,
			float precisionOverlapping, float recallOverlapping, float precisionWithCycles,
			float recallWithCycles, float precisionKBToXML,
			float recallKBToXML, float precisionXMLToKB,
			float recallXMLToKB, int numberOfFuncOverlapping, int numberOfFuncWithCycles, int numberOfFuncKBToXML , int numberOfFuncXMLToKB) {

		this.functionName = functionName;
		this.inputType = inputType;
		this.totalPrecisionOverlapping = precisionOverlapping;
		this.totalRecallOverlapping = recallOverlapping;
		this.totalPrecisionWithCycles = precisionWithCycles;
		this.totalRecallWithCycles = recallWithCycles;
		
		this.totalPrecisionKBToXML = precisionKBToXML;
		this.totalRecallKBToXML = recallKBToXML;
                
                this.totalPrecisionXMLToKB = precisionXMLToKB;
		this.totalRecallXMLToKB = recallXMLToKB;

		this.numberOfFunctionsWithTheSameNameOverlapping = numberOfFuncOverlapping;
		this.numberOfFunctionsWithTheSameNameWithCycles = numberOfFuncWithCycles;
		this.numberOfFunctionsWithTheSameNameKBToXML = numberOfFuncKBToXML;
                this.numberOfFunctionsWithTheSameNameXMLToKB = numberOfFuncXMLToKB;
	}

	public void averagePrecisionCalculatorOverlapping() {
		if (this.totalPrecisionOverlapping == 0.0f) {
			this.averagePrecisionOverlapping = "--";
		} else {
			this.averagePrecisionOverlapping = df.format(this.totalPrecisionOverlapping
					/ this.numberOfFunctionsWithTheSameNameOverlapping);
		}
	}

	public void averageRecallCalculatorOverlapping() {
		if (this.totalRecallOverlapping == 0.0f) {
			this.averageRecallOverlapping = "--";
		} else {
			this.averageRecallOverlapping = df.format(this.totalRecallOverlapping
					/ this.numberOfFunctionsWithTheSameNameOverlapping);
		}
	}

	public void averagePrecisionCalculatorKBToXML() {
		if (this.totalPrecisionKBToXML == 0.0f) {
			this.averagePrecisionKBToXML = "--";
		} else {
			this.averagePrecisionKBToXML = df.format(this.totalPrecisionKBToXML
					/ this.numberOfFunctionsWithTheSameNameKBToXML);
		}
	}

	public void averageRecallCalculatorKBToXML() {
		if (this.totalRecallKBToXML == 0.0f) {
			this.averageRecallKBToXML = "--";
		} else {
			this.averageRecallKBToXML = df.format(this.totalRecallKBToXML
					/ this.numberOfFunctionsWithTheSameNameKBToXML);
		}
	}
	
	
	public void averagePrecisionCalculatorWithCycles() {
		if (this.totalPrecisionWithCycles == 0.0f) {
			this.averagePrecisionWithCycles = "--";
		} else {
			this.averagePrecisionWithCycles = df.format(this.totalPrecisionWithCycles
					/ this.numberOfFunctionsWithTheSameNameWithCycles);
		}
	}

	public void averageRecallCalculatorWithCycles() {
		if (this.totalRecallWithCycles == 0.0f) {
			this.averageRecallWithCycles = "--";
		} else {
			this.averageRecallWithCycles = df.format(this.totalRecallWithCycles
					/ this.numberOfFunctionsWithTheSameNameWithCycles);
		}
	}
        
        public void averagePrecisionCalculatorXMLToKB() {
		if (this.totalPrecisionXMLToKB == 0.0f) {
			this.averagePrecisionXMLToKB = "--";
		} else {
			this.averagePrecisionXMLToKB = df.format(this.totalPrecisionXMLToKB
					/ this.numberOfFunctionsWithTheSameNameXMLToKB);
		}
	}

	public void averageRecallCalculatorXMLToKB() {
		if (this.totalRecallXMLToKB == 0.0f) {
			this.averageRecallXMLToKB = "--";
		} else {
			this.averageRecallXMLToKB = df.format(this.totalRecallXMLToKB
					/ this.numberOfFunctionsWithTheSameNameXMLToKB);
		}
	}
	

	public void addToPrecisionOverlapping(float precision) {
		this.totalPrecisionOverlapping += precision;
	}

	public void addToRecallOverlapping(float recall) {
		this.totalRecallOverlapping += recall;
	}

	public void addToPrecisionKBToXML(float precision) {
		this.totalPrecisionKBToXML += precision;
	}

	public void addToRecallKBToXML(float recall) {
		this.totalRecallKBToXML += recall;
	}
	
	public void addToPrecisionWithCycles(float precision) {
		this.totalPrecisionWithCycles += precision;
	}

	public void addToRecallWithCycles(float recall) {
		this.totalRecallWithCycles += recall;
	}
        
        
        public void addToPrecisionXMLToKB(float precision) {
		this.totalPrecisionXMLToKB += precision;
	}

	public void addToRecallXMLToKB(float recall) {
		this.totalRecallXMLToKB += recall;
	}

	public void addNumberOfFunctionsWithCycles() {
		this.numberOfFunctionsWithTheSameNameWithCycles++;
	}

	public void addNumberOfFunctionsOverlapping() {
		this.numberOfFunctionsWithTheSameNameOverlapping++;
	}
	
	public void addNumberOfFunctionsKBToXML() {
		this.numberOfFunctionsWithTheSameNameKBToXML++;
	}

        public void addNumberOfFunctionsXMLToKB() {
		this.numberOfFunctionsWithTheSameNameXMLToKB++;
	}

}
