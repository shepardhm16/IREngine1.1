package engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.StringTokenizer;

import engine.query.QueryProcessor;
import engine.ranking.Pair;
import engine.ranking.Ranking;
import indexer.Stemmer;
import indexer.TermTable;
import utility.FileNames;
import utility.HtmlGenerator;
import utility.Utility;


public class SearchEngine {
	public static final int W_BOOL = 1001;
	public static final int W_TF = 1002;
	public static final int W_IDF = 1003;
	public static final int W_TFIDF = 1004;
	public static final int W_BM11 = 1005;
	public static final int W_BM15 = 1006;
	public static final int W_BM25 = 1007;

	private static int weightingOptions = SearchEngine.W_BOOL;
	private static boolean indexingOptions = false;

	QueryProcessor processor = new QueryProcessor();
	TermTable termTable = new TermTable();
	int [] docpointer = new int[9];
	int noOfDocs = 0;
	float avgDocLength = 0;
	RandomAccessFile postingsFile = null;
	RandomAccessFile indexingFile = null;
	Ranking ranking = null;
	String[] docFileNames = null;
	String[] docTitles = null;

	public static void main(String args[]) {
		SearchEngine engine = new SearchEngine();
		engine.start();

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);


		System.out.print("Enter Your Query: ");
		String q = scanner.nextLine();
		while(q.compareTo("exit") != 0) {
			engine.query(q);
			System.out.print("\nEnter Your Query: ");
			q = scanner.nextLine();

		}

	}
	public void start() {
		ArrayList<String> table = new ArrayList<String>();
		String line = null;
		try {
			FileReader fileReader = new FileReader(FileNames.TERMS);

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null) {
				table.add(line.trim());
			}   

			termTable.initTermTable(table.size());
			for(int i=0; i<table.size();i++)
				termTable.addTerm(table.get(i), i);

			bufferedReader.close();

			fileReader = new FileReader(FileNames.DOCFREQ);

			bufferedReader = new BufferedReader(fileReader);

			int idx = 0;
			while((line = bufferedReader.readLine()) != null) {
				termTable.setDocumentFrequency(idx, Integer.parseInt(line));
				idx++;
			}   
			bufferedReader.close();

			fileReader = new FileReader(FileNames.POSTINGSPTR);

			bufferedReader = new BufferedReader(fileReader);

			idx = 0;
			while((line = bufferedReader.readLine()) != null) {
				termTable.setPostingsPointer(idx, Integer.parseInt(line));
				idx++;
			}   
			bufferedReader.close();


			fileReader = new FileReader(FileNames.DOCPTR);
			bufferedReader = new BufferedReader(fileReader);
			idx = 0;
			while((line = bufferedReader.readLine()) != null) {
				if(line.trim().length()<1) break;
				docpointer[idx] = Integer.parseInt(line);
				idx++;
			}   
			bufferedReader.close();

			fileReader = new FileReader(FileNames.METAFILE);
			bufferedReader = new BufferedReader(fileReader);
			noOfDocs = Integer.parseInt(bufferedReader.readLine());
			avgDocLength = Float.parseFloat(bufferedReader.readLine());
			docFileNames = new String[noOfDocs];
			docTitles = new String[noOfDocs];
			for(int i=0; i<noOfDocs;i++) {
				docFileNames[i] = bufferedReader.readLine();
				docTitles[i] = bufferedReader.readLine();
			}

			bufferedReader.close();

		} catch(FileNotFoundException ex) {
			System.out.println("Unable to open file: " + ex.getMessage());

		} catch(IOException ex) {
			System.out.println("Error reading file'");                  

		}

		try {
			postingsFile = new RandomAccessFile(FileNames.POSTINGS, "r");
			indexingFile = new RandomAccessFile(FileNames.INDEXING, "r");
			ranking = new Ranking(termTable, noOfDocs, avgDocLength, docpointer, indexingFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Search Engine is Ready");
	}

	public String query(String query) {
		if(query.length()<1)
			return "<div><h1 style='color:red'>Enter Your Query</h1></div>";
		Stemmer stemmer = new Stemmer();
		StringTokenizer st = new StringTokenizer(query, " ");
		String token = null;
		int qt[] = new int[st.countTokens()];
		int qtf[] = new int[qt.length];
		Arrays.fill(qt, -1);
		Arrays.fill(qtf, 0);
		int qidx = 0;

		while(st.hasMoreTokens()) {
			token = st.nextToken().toLowerCase();

			stemmer.add(token.toCharArray(), token.length());
			stemmer.stem();
			int termID = Utility.search(termTable.getTermTable(), stemmer.toString());
			if(termID != -1) {
				for(int i=0; i<=qidx;i++) {
					if(qt[i] == -1) {
						qt[i] = termID;
						qtf[i]++;
						qidx++;
						break;
					} else if(qt[i] == termID) {
						qtf[i]++;
						break;
					}
				}
			}
		}


		ArrayList<Pair> rank = new ArrayList<Pair>();
		if(qt[0] == -1)
			return HtmlGenerator.getNoResultFound(query); 
		else {
			int result[] = processor.getPostings(termTable,postingsFile, qt, qtf, docpointer);

			if(SearchEngine.weightingOptions == SearchEngine.W_BOOL) {
				for (int i=0 ; i<result.length;i++) {
					rank.add(new Pair(result[i], 0));
				}
			} else {
				for (int i=0 ; i<result.length;i++) {
					rank.add(new Pair(result[i], ranking.getScore(result[i], qt, qtf, 
							weightingOptions, indexingOptions, docTitles)));
				}
				Collections.sort(rank, new ScoreComparator());
			}
			/**********************************
			 * EXTRA POINTS 15
			 * the static function in next line returns search result in HTML format.
			 * Each result includes document title and it's score.
			 * Change the function  to display in next format 
			 *    first-line: Document title (font-size:20px;color#222;font-weight:bold)
			 *    second-line: Document Description (100 characters) (font-size:14px;color:#333)
			 *    third-line: Score (red color;font-weight:bold;font-size:12px)
			 */
			return HtmlGenerator.getSearchResult(rank, this.docFileNames, this.docTitles);
		}


	}



	public static void setWeightingOption(int opt) {  
		weightingOptions = opt;
	}

	public static void setIndexingOption(boolean opt) {
		indexingOptions = opt;
	}

	static class ScoreComparator implements Comparator<Pair>
	{
		public int compare(Pair d1, Pair d2)
		{
			double s1 = d1.getScore();
			double s2 = d2.getScore();
			return s1>s2 ? -1 : (s1==s2 ? 0 : 1);
		}
	}


}
