package indexer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import utility.FileNames;


public class VMIParser {
	TermTable termTable = new TermTable();
	PostingsBuilder postings = new PostingsBuilder();

	public static void main(String[] args) {

		VMIParser parser = new VMIParser();
		String dir = "./docs";
		parser.buildTermTable(dir);
		parser.buildPostings(dir);
		try {
			parser.writeTermTableToFile();
		} catch(IOException e) {
			System.out.println("Cannot Open File to Write!!!");
		}
		
	}
	
	
	public void buildTermTable(String dir) {
		termTable.build(dir);

	}
	
	public void buildPostings(String dir) {
		postings.build(dir, this.termTable);
		postings.finalize();
		
	}
	
	public void writeTermTableToFile() throws IOException {
		BufferedWriter writer1 = new BufferedWriter(new FileWriter(FileNames.TERMS));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(FileNames.DOCFREQ));
		BufferedWriter writer3 = new BufferedWriter(new FileWriter(FileNames.POSTINGSPTR));
		
		String[] terms = termTable.getTermTable();
		int[] docFreq = termTable.getDocumentFrequencies();
		int[] postingsPtr = termTable.getPostingsPointers();
		
		
		for(String str: terms) {			
			try {
				writer1.write(str);
				writer1.newLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		writer1.close();
		for(int i: docFreq) {			
			try {
				writer2.write(i + "");
				writer2.newLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		writer2.close();
		
		for(int i: postingsPtr) {			
			try {
				writer3.write(i + "");
				
				writer3.newLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	
		writer3.close();
	}
}
 