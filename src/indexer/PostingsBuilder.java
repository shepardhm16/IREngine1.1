package indexer;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import utility.FileNames;
import utility.StringUtility;
import utility.Utility;

public class PostingsBuilder {
	
	DataOutputStream postingsWriter = null;
	DataOutputStream indexingWriter = null;
	BufferedWriter postingsTestWriter = null; // postings test file
	BufferedWriter indexingTestWriter = null; // indexing test file
	BufferedWriter docPtrWriter = null;
	BufferedWriter metaFileWriter = null;
	TermTable table = null;
	public PostingsBuilder() {
		try {
			postingsWriter = new DataOutputStream(new FileOutputStream(FileNames.POSTINGS));
			indexingWriter = new DataOutputStream(new FileOutputStream(FileNames.INDEXING));
			docPtrWriter = new BufferedWriter(new FileWriter(FileNames.DOCPTR));
			postingsTestWriter = new BufferedWriter(new FileWriter(FileNames.POSTINGS + ".test"));
			indexingTestWriter = new BufferedWriter(new FileWriter(FileNames.INDEXING + ".test"));
			metaFileWriter = new BufferedWriter(new FileWriter(FileNames.METAFILE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// close all files 
	public void finalize() {
		try {
			postingsWriter.close();
			indexingWriter.close();
			docPtrWriter.close();
			postingsTestWriter.close();
			indexingTestWriter.close();
			metaFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*************************************
	 * 10 points
	 * Add comments for this function
	 * find ??? (triple question mark) and add comments 
	 *   
	 */
	 
	public void build(String dir, TermTable tbl) {
		this.table = tbl;
		int termIdx = 0;
		int docIdx = 0;
		int pointer = 0;
		long avgDocLen = 0;
		Stemmer stemmer = new Stemmer();
		String[] terms = table.getTermTable();
		
		ArrayList<String> docs = null;
		ArrayList<String> docTitles = new ArrayList<String>();

		try {
			docs = Utility.getAllDocuments(dir);
		} catch (IOException e) {
			System.out.println("Unable to open files." + e.getMessage());
			return;
		}

		String text = null;
		StringTokenizer st = null;
		String token = null;

		ArrayList<Integer> postings = null;
		int[] docptr = new int[docs.size()];
		String[] docnames =new String[docs.size()];
		
		for(int i=0; i<docnames.length;i++)
			docnames[i] = docs.get(i);
		
		while(termIdx < terms.length) {
			postings = new ArrayList<Integer>();
			docIdx = 0;
			int t[] = null;
			int tf[] = null;
			int tlen = 0;
			int dlen = 0;
			int ptr = 0;
			
			while(docIdx < docs.size()) {
				try {
					text = new String(Files.readAllBytes(Paths.get(docs.get(docIdx))), StandardCharsets.UTF_8);
					
					st = new StringTokenizer(text, " ");
					t = new int[st.countTokens()];
					tf = new int[t.length];
					Arrays.fill(t, -1);
					Arrays.fill(tf, 0);
					tlen = 0;
					dlen = 0;

					while(st.hasMoreTokens()) {
						token = StringUtility.refineToken(stemmer, st.nextToken());
						
						if(token == null) continue;
						
						int termID = Utility.search(table.getTermTable(), token);
						if(termID != -1) {
							if(termID==termIdx && postings.contains(docIdx) == false)
								postings.add(docIdx);

							dlen++; // increase doc length
							
							// set current term frequency for current doc
							// if termIdx is 0 => ???
							if(termIdx == 0) {
								// ??? how next for loop works
								for(int i=0; i<=tlen;i++) {
									if(t[i] == -1) {
										t[i] = termID;
										tf[i]++;
										tlen++;
										break;
									} else if(t[i] == termID) {
										tf[i]++;
										break;
									}
								}
							}
						}
					}
				} catch(FileNotFoundException ex) {
					System.out.println("Unable to open file '" + docs.get(docIdx) + "'");
				} catch(IOException ex) {
					System.out.println("Error reading file '" + docs.get(docIdx) + "'");                  
				} 

				// ??? why if termIdx == 0 => 
				if(termIdx == 0) {
					try {
						// ??? explain next 5 lines
						writeIndexing(docIdx, dlen, tlen, t, tf, table);
						docptr[docIdx] = ptr;
						ptr += (1+1+1+2*(tlen));
						avgDocLen += dlen;
						docTitles.add(text.substring(text.indexOf("<title>")+7, text.indexOf("</title>")));

					} catch(IOException e){
						e.printStackTrace();
					} catch(StringIndexOutOfBoundsException e) {
						System.out.println(e.getMessage() + " " + text);
					}
				}
				docIdx++;
			}

			try {
				writePostings(postings);

			} catch(IOException e){
				e.printStackTrace();
			}

			table.setDocumentFrequency(termIdx, postings.size());
			table.setPostingsPointer(termIdx, pointer);
			pointer += postings.size();


			termIdx++;
			if(termIdx % 1000 == 0)
				System.out.println(termIdx + " out of " + terms.length);

		}	

		try {
			writeDocPtr(docptr);
			writemetaFileWriter(docIdx, avgDocLen, docs, docTitles);
			postingsWriter.close();
			indexingWriter.close();
			postingsTestWriter.close();
			indexingTestWriter.close();
			docPtrWriter.close();
			metaFileWriter.close();
			System.out.println("***Postings List Created.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	

	public void writePostings(ArrayList<Integer> postings) throws IOException {

		for(Integer i: postings) {
			try {
				postingsTestWriter.write(i.toString() + " ");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		postingsTestWriter.newLine();


		for(Integer i: postings) {
			postingsWriter.writeInt(i.intValue()); 
		}

	}

	public void writeDocPtr(int[] ptr) throws IOException {

		for(int i=0; i<ptr.length;i++) {
			try {
				docPtrWriter.write(ptr[i] + "");
				docPtrWriter.newLine();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public void writemetaFileWriter(int noOfDocs, long avgDocLen, ArrayList<String> docs,  ArrayList<String> docTitles)
			throws IOException {

		try {
			metaFileWriter.write(noOfDocs + "");
			metaFileWriter.newLine();
			metaFileWriter.write((float)(avgDocLen/noOfDocs) + "");
			metaFileWriter.newLine();
			for(int i=0; i<docs.size();i++) {
				metaFileWriter.write(docs.get(i));
				metaFileWriter.newLine();
				metaFileWriter.write(docTitles.get(i));
				metaFileWriter.newLine();
			}
				

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	public void writeIndexing(int docID, int dlen, int tlen, int[] t, int[] tf, TermTable table) throws IOException {

		indexingTestWriter.write("doc-id:" + docID + ", doc-length:" + dlen + ", term-length:" + tlen + ", terms[term:freq]-");
		for(int i=0;i<tlen;i++) {
			try {
				indexingTestWriter.write(table.getTerm(t[i]) + " " +tf[i] + " ");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		indexingTestWriter.newLine();

		indexingWriter.writeInt(docID);
		indexingWriter.writeInt(dlen);
		indexingWriter.writeInt(tlen);
		for(int i=0;i<tlen;i++) {
			indexingWriter.writeInt(t[i]); 
			indexingWriter.writeInt(tf[i]);
		}

	}


}
