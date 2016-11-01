package severalCodes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import customization.Constants;

public class agvPrecisionRecallFmeasure {

	float totalPrecision;
	float totalRecall;
	float totalFmeasure;
	
	public agvPrecisionRecallFmeasure(){}

	public void parsePrecisionFile(String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;
			int lineCounter=0;
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine != "") {
					lineCounter++;
					String[] lineParts = sCurrentLine
							.split(Constants.separatorSpace);
					totalPrecision += Float.valueOf(lineParts[2]);
					totalRecall += Float.valueOf(lineParts[4]);
					totalFmeasure += Float.valueOf(lineParts[6]);
				}
			}
			float avgPrecision = (float)totalPrecision/lineCounter;
			float agvRecall = (float) totalRecall/lineCounter;
			float agvFmeasure = (float) totalFmeasure/lineCounter;
			
			System.out.println("Precision: "+avgPrecision +" Recall: "+agvRecall+" Fmeasure: "+agvFmeasure);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		agvPrecisionRecallFmeasure avg = new agvPrecisionRecallFmeasure();
		
		avg.parsePrecisionFile("/Users/mary/Dropbox/OASIS/Mary-Data/precisionRecall/PrecisionRecallResults0.7.txt");

	}

}
