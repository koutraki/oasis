package transformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import classAlignment.ComputePrecisionRecall;
import customization.Constants;

public class ViewDefinition {

	static String webSite;
	static String function;

	ArrayList<String> outputParamiters;
	ArrayList<Triple> viewBody;

	void parseClassAlignmentFile(String filepath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

			String sCurrentLine;

			outputParamiters = new ArrayList<String>();
			viewBody = new ArrayList<Triple>();

			while ((sCurrentLine = br.readLine()) != null) {

				if (!sCurrentLine.isEmpty()) {
					// System.out.println(sCurrentLine);
					String[] lineparts = sCurrentLine
							.split(Constants.separatorSpace);
					if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineFunction)) {

						function = lineparts[2];
						webSite = lineparts[1];
					} else if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineClass)) {

						if (lineparts[2].equals(".")) { /**/

						} else {
							Triple classTriple = new Triple(lineparts[1],
									lineparts[2], lineparts[3]);

							viewBody.add(classTriple);
						}

					} else if (lineparts[0]
							.equals(ComputePrecisionRecall.prefixLineRelations)) {

						Triple relationTriple = new Triple(lineparts[1],
								lineparts[2], lineparts[3]);

						viewBody.add(relationTriple);
						outputParamiters.add(lineparts[3]);

					}
				} else {

					// do nothing
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void produceViewDefinition(String filePath) {
		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(filePath);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
			} else {
				out = new BufferedWriter(new FileWriter(file));
			}

			out.write(webSite + ":" + function + "(");
			for (int i = 0; i < outputParamiters.size() - 1; i++) {
				out.write(outputParamiters.get(i) + ",");
			}
			if (outputParamiters.size() > 0)
				out.write(outputParamiters.get(outputParamiters.size() - 1)
						+ ")  <--  ");

			for (int i = 0; i < viewBody.size() - 1; i++) {
				out.write(viewBody.get(i).predicate + "( "
						+ viewBody.get(i).subject + ", "
						+ viewBody.get(i).object + " ),  ");
			}
			if (viewBody.size() > 0)
				out.write(viewBody.get(viewBody.size() - 1).predicate + "( "
						+ viewBody.get(viewBody.size() - 1).subject + ", "
						+ viewBody.get(viewBody.size() - 1).object + " )");

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		//File folder = new File("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/ClassAlignmentNewForm/");
            File folder = new File("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/ClassAlignmentNewFormV2/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {

				String goldSetFilename = file.getName();
				System.out.println(goldSetFilename);
				if (goldSetFilename.equals(".DS_Store"))
					continue;
				ViewDefinition view = new ViewDefinition();
				view.parseClassAlignmentFile("/Users/mary/Dropbox/OASIS/Mary-Data/YAGO/ClassAlignmentNewFormV2/"
						+ goldSetFilename);

				view.produceViewDefinition(Constants.viewsDirectory + webSite
						+ "_" + function + "_View");

			}
		}
	}

}
