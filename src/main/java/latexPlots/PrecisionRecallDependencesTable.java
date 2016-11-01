package latexPlots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import customization.Constants;

public class PrecisionRecallDependencesTable {

	String LatexTable;
	
	
	public void initializeLatexTable(){
		LatexTable = "\\begin{tabular}{|l|l|l|l|} \n"
				+ "\\hline\n"
				+ "\\quad \\bf Web Servise & \\quad \\bf Precision & \\quad \\bf Recall & \\quad \\bf F-Measure  \\\\ \n"
				+ "\\hline\n";
		
	}
		
	public void ParsePrecisionRecallClassAlignmentFile(String filePath) {

		File input = new File(filePath);
		try {
			Document doc = Jsoup.parse(input, "UTF-8", "");

			Elements tableLines = doc.getElementsByTag("tr");
			int flag = 0;
			for (Element line : tableLines) {
				if (flag < 2) {
					flag++;
					continue;
				}
				String lineText = line.text();
				String[] values = lineText.split(Constants.separatorSpace);
				LatexTable += "\\quad \\bf "+values[0].replace("_", "\\_")+" & \\quad  "+values[1]+" & \\quad  "+values[2]+" & \\quad  "+values[3]+"  \\\\ \n"
						+ "\\hline\n";
				
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		LatexTable += " \\end{tabular}";

	}
	
	
	public void createFileForLatex(String path) {

		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(path);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
				out.write(LatexTable);
			} else {
				out = new BufferedWriter(new FileWriter(file));
				out.write(LatexTable);
			}

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		PrecisionRecallDependencesTable tableToLatex = new PrecisionRecallDependencesTable();
		tableToLatex.initializeLatexTable();
		tableToLatex.ParsePrecisionRecallClassAlignmentFile(Constants.HTMLPrecisionRecallDependencesTable);//"/Users/mary/Dropbox/OASIS/Mary-Data/PrecisionRecallIODependences.html");
		tableToLatex.createFileForLatex("/Users/mary/Dropbox/OASISPaper/exp/exp_table_IODependences.tex");
	}

}
