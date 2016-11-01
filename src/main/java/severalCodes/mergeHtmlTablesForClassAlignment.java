package severalCodes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import customization.Constants;
import transformation.TriplesSet;

public class mergeHtmlTablesForClassAlignment {

	private String mergedTables;

	public mergeHtmlTablesForClassAlignment() {
		// TODO Auto-generated constructor stub
		this.mergedTables = new String();
	}

	public void parseClassAlignmentFile(String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;

			mergedTables += "DORIS Result:\n\n\n"
					+ "<p>&nbsp;</p>";
			while ((sCurrentLine = br.readLine()) != null) {

				if (!sCurrentLine.isEmpty()) {
					mergedTables += sCurrentLine + "\n";
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void parseGlodSetFile(String filePath) {

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;
			mergedTables += "\n <p>&nbsp;</p> GoldSet:\n\n\n"
					+ "<p>&nbsp;</p>";
			while ((sCurrentLine = br.readLine()) != null) {

				if (!sCurrentLine.isEmpty()) {
					mergedTables += sCurrentLine + "\n";
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void creatNewFileForBothTables(String filePath) {

		FileWriter fstream;
		BufferedWriter out = null;
		try {

			File file = new File(filePath);
			if (!file.exists()) {
				fstream = new FileWriter(file);
				out = new BufferedWriter(fstream);
				out.write(this.mergedTables);
			} else {
				out = new BufferedWriter(new FileWriter(file));
				out.write(this.mergedTables);
			}

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File folder = new File(
				"/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/ClassAlignmentNewFormHTML/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				mergeHtmlTablesForClassAlignment obj = new mergeHtmlTablesForClassAlignment();

				String fileName = file.getName();

				// System.out.println(file.getAbsolutePath());
				if (fileName.equals(".DS_Store"))
					continue;

				String[] fileNameNoExtention = fileName.split(".html");
				System.out.println(fileName);

				File goldSetFolder = new File(
						"/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/GoldSets/goldSetClassRelationAlignmentNewFormHTML/");
				File[] listOfGoldSetFiles = goldSetFolder.listFiles();

				for (File goldSetfile : listOfGoldSetFiles) {
					
					if (goldSetfile.isFile()) {
						if (goldSetfile.getName().equals(".DS_Store"))
							continue;

						String[] goldSetfileNoExtention = goldSetfile.getName()
								.split(".html");
						System.out.println(goldSetfile.getName());
						if (goldSetfileNoExtention[0]
								.equals(fileNameNoExtention[0] + "_goldset")) {
							obj.parseClassAlignmentFile(file.getAbsolutePath());
							obj.parseGlodSetFile(goldSetfile.getAbsolutePath());
							obj.creatNewFileForBothTables("/Users/mary/Dropbox/OASIS/Mary-Data/DBPedia/HTMLTables/ClassRelationAlignmentAndGoldSet/"
									+ fileName.replace("ClassAlignment", "Alignment"));
							break;
						}
					}
				}//for

			}
		}

	}
}
