package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;


public class VMICrawler2 {
	private  static ArrayList<String> allLinks = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException {
		if(addLinksFromFile("vmilinks.txt") == false) {
			System.out.println("Critical Error: Cannot Open File");
			System.exit(1);
		}

		int i = 0;
		String url = null;
		Document doc = null;
		Elements elements = null;
		String contents = null;

		while( i<allLinks.size()) {
			contents = "";
			url = allLinks.get(i);
			System.out.println("processing " + i + "th url: " + url);
			try {
				doc = Jsoup.connect(url).timeout(2000).	get();
				elements = doc.getAllElements();

				for (Element e : elements) {
					contents += (e.text().trim() + " ");
				}
			} catch(Exception e) {
				System.out.println("Error: " + i + ":" + url + "[" + e.getMessage() + "]");
			}
			contents = contents.replaceAll("[^a-zA-Z0-9 ]+"," ");
			writeContentsToFile(i, contents);
			i++;
			
		}	
		
		System.out.println("All done! (" + i + " documents collected.)");
	}

	private static boolean addLinksFromFile(String fileName) {
		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null) {
				allLinks.add(line);
			}   
			// Always close files.
			bufferedReader.close();         
		} catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
			return false;
		} catch(IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");                  
			// Or we could just do this: 
			// ex.printStackTrace();
			return false;
		}

		return true;
	}


	private static void writeContentsToFile(int fileIndex, String contents) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("./docs/doc-"+fileIndex));
			writer.write(contents);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

