package indexer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import utility.StringUtility;
import utility.Utility;


public class TermTable {
	ArrayList<String> table = new ArrayList<String>();
	String terms[];
	int[] documentFrequency;
	int[] postingsPointer;
	Stemmer stemmer = new Stemmer();
	public void build(String dir) {
		int i = 0;
		ArrayList<String> docs = null;
		
		try {
			docs = Utility.getAllDocuments(dir);
		} catch (IOException e) {
			System.out.println("Unable to open files.");
			return;
		}
		
		String text = null;
		StringTokenizer st = null;
		String token = null;
		while(i < docs.size()) {
			try {
				text = new String(Files.readAllBytes(Paths.get(docs.get(i))), StandardCharsets.UTF_8);
				st = new StringTokenizer(text, " ");
				while(st.hasMoreTokens()) {
					token = StringUtility.refineToken(stemmer, st.nextToken());

					if(token != null && token.length()>0 && table.contains(token) == false)
						table.add(stemmer.toString());
				
				}
			} catch(FileNotFoundException ex) {
				System.out.println("Unable to open file '" + docs.get(i) + "'");
			} catch(IOException ex) {
				System.out.println("Error reading file '" + docs.get(i) + "'");                  
			}
			i++;
		}
		this.sortTable();
		
		terms = this.table.toArray(new String[table.size()]);
		this.documentFrequency = new int[terms.length];
		this.postingsPointer = new int[terms.length];
		
		System.out.println("***Term Table Created.");
		System.out.println("   Total Number of Terms: " + terms.length);
	}

	
	
	public void sortTable() {
		Collections.sort(table);
	}
	
	public String[] getTermTable() {
		return this.terms;
	}
	
	public int[] getPostingsPointers() {
		return this.postingsPointer;
	}
	
	public int[] getDocumentFrequencies() {
		return this.documentFrequency;
	}
	
	public void initTermTable(int len) {
		this.terms = new String[len];
		this.documentFrequency = new int[len];
		this.postingsPointer = new int[len];
	}
	public void addTerm(String t, int idx) {
		this.terms[idx] = t;
	}
	
	
	public void setPostingsPointer(int termID, int ptr) {
		this.postingsPointer[termID] = ptr;
	}
	
	public void setDocumentFrequency(int termID, int freq) {
		this.documentFrequency[termID] = freq;
	}

	public int getPostingsPointer(int termID) {
		return this.postingsPointer[termID];
	}
	
	public int getDocumentFrequency(int termID) {
		return this.documentFrequency[termID];
	}
	
	public String getTerm(int termID) {
		return this.terms[termID];	
	}
}
