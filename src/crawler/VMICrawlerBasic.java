package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class VMICrawlerBasic {
	private  static ArrayList<String> allLinks = new ArrayList<String>();
	private static BufferedWriter writer = null;

	public static void main(String[] args) throws IOException {

		// open the ouput file
		try {
			writer = new BufferedWriter(new FileWriter("vmilinks.txt"));
		} catch(IOException ioe) {
			System.out.println("[System Error]" + ioe.getMessage());
		}

		allLinks.add("http://www.vmi.edu");
		int i = 0;
		String url = null;
		Document doc = null;
		Elements links = null;
		while((url = allLinks.get(i++)) != null) {
			// use next line to check that your program is alive and works correctly
			// System.out.println(i + ": " + url);
			try {
				doc = Jsoup.connect(url).get();
				links = doc.select("a[href]");

				for (Element link : links) {
					addLink(link.attr("abs:href").trim());
				}
			} catch(Exception e) {
				//if you get error on an url, then analyze and handle it.
				System.out.println("Error: " + url + " [" + e.getMessage() + "]");
			}

			// use next line for testing. i means (number of url + 1) to be processed.
			// if(i>3) break;
		}

		writer.close();
	}

	private static void addLink(String link) {
		/************************************************
		 * check list before adding the link			 	*
		 * 1. is the link a vmi page?					*
		 * 2. is the link not a mailto link? 			*
		 * 3. is the link not a 404 page?				*
		 * 4. is the link one of search result page? 	*
		 * 	4.1 general search (vmi.edu/Search)			*
		 * 	4.2 digitalcollection search 				*
		 * 	4.3 archivesspace search					*
		 * 5. is the link a valid calendar page?			*
		 * 												*
		 * YOU MUST INVESTIGATE MORE RULES BY ANALYZING	*
		 * YOUR RESULT SET								*
		 ************************************************/
		if(!allLinks.contains(link)) {
			allLinks.add(link);
			writeLinkToFile(link);
		}

	}

	private static void writeLinkToFile(String link) {
		try {
			writer.write(link);
			writer.newLine();
		} catch (Exception e) {
			System.out.println("[System Error] BYE BYE");
			System.exit(1);
		}
	}
}

