package latexPlots;

import java.text.DecimalFormat;

public class AvgPrecisionRecallPathPairsObj {

	
	DecimalFormat df = new DecimalFormat("#.##");
	
	
	String functionName;
	String inputType;
	float totalPrecision20Inputs = 0.0f;
	float totalRecall20Inputs = 0.0f;
	float totalPrecision100Inputs = 0.0f;
	float totalRecall100Inputs = 0.0f;
	
	String averagePrecision20Inputs;
	String averageRecall20Inputs;
	String averagePrecision100Inputs;
	String averageRecall100Inputs;
	
	int numberOfFunctionsWithTheSameName20Inputs = 0;
	int numberOfFunctionsWithTheSameName100Inputs = 0;

	public AvgPrecisionRecallPathPairsObj(String functionName, String inputType,
			float precision20, float recall20, float precision100,
			float recall100, int numberOfFunc20, int numberOfFunc100) {

		this.functionName = functionName;
		this.inputType = inputType;
		this.totalPrecision20Inputs = precision20;
		this.totalRecall20Inputs = recall20;
		this.totalPrecision100Inputs = precision100;
		this.totalRecall100Inputs = recall100;

		this.numberOfFunctionsWithTheSameName20Inputs = numberOfFunc20;
		this.numberOfFunctionsWithTheSameName100Inputs = numberOfFunc100;
	}

	public void averagePrecisionCalculator20Inputs() {
		if (this.totalPrecision20Inputs == 0.0f) {
			this.averagePrecision20Inputs = "--";
		} else {
			this.averagePrecision20Inputs = df.format(this.totalPrecision20Inputs
					/ this.numberOfFunctionsWithTheSameName20Inputs);
		}
	}

	public void averageRecallCalculator20Inputs() {
		if (this.totalRecall20Inputs == 0.0f) {
			this.averageRecall20Inputs = "--";
		} else {
			this.averageRecall20Inputs = df.format(this.totalRecall20Inputs
					/ this.numberOfFunctionsWithTheSameName20Inputs);
		}
	}

	public void averagePrecisionCalculator100Inputs() {
		if (this.totalPrecision100Inputs == 0.0f) {
			this.averagePrecision100Inputs = "--";
		} else {
			this.averagePrecision100Inputs = df.format(this.totalPrecision100Inputs
					/ this.numberOfFunctionsWithTheSameName100Inputs);
		}
	}

	public void averageRecallCalculator100Inputs() {
		if (this.totalRecall100Inputs == 0.0f) {
			this.averageRecall100Inputs = "--";
		} else {
			this.averageRecall100Inputs = df.format(this.totalRecall100Inputs
					/ this.numberOfFunctionsWithTheSameName100Inputs);
		}
	}

	public void addToPrecision20Inputs(float precision) {
		this.totalPrecision20Inputs += precision;
	}

	public void addToRecall20Inputs(float recall) {
		this.totalRecall20Inputs += recall;
	}

	public void addToPrecision100Inputs(float precision) {
		this.totalPrecision100Inputs += precision;
	}

	public void addToRecall100Inputs(float recall) {
		this.totalRecall100Inputs += recall;
	}

	public void addNumberOfFunctions100Inputs() {
		this.numberOfFunctionsWithTheSameName100Inputs++;
	}

	public void addNumberOfFunctions20Inputs() {
		this.numberOfFunctionsWithTheSameName20Inputs++;
	}

}
