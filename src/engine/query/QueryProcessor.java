package engine.query;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;


import indexer.TermTable;

public class QueryProcessor {
	public final static int BOOL_OR = 1; 
	final static int BOOL_AND = 2;

	public int[] getPostings(TermTable table, RandomAccessFile postingFile, int[] qt, int[] qtf, int[] docpointer) {
		int[][] postings = new int[qt.length][];

		for(int i=0; i<qt.length;i++) {
			if(qt[i]<0)  
				continue;
			long pointer = table.getPostingsPointer(qt[i]);
			int length = table.getDocumentFrequency(qt[i]);

			try {
				postingFile.seek(pointer*Integer.BYTES);
				byte[] arr = new byte[length*Integer.BYTES];
				postingFile.read(arr);
				IntBuffer intBuf = ByteBuffer.wrap(arr).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
				postings[i] = new int[intBuf.remaining()];
				intBuf.get(postings[i]);
				
				// seek to front for next use.
				postingFile.seek(0);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		if(qt.length==1)
			return postings[0];
		
		return mergeResult(postings, QueryProcessor.BOOL_OR);

	}
	
	// merge function for multi-term query
	private  int[] mergeResult(int[][] data, int mode) {
		int[] result = null;
		ArrayList<Integer> merged = new ArrayList<Integer>();
		data = removeNullData(data);
		
		if(mode == QueryProcessor.BOOL_OR) { // OR
			try {
			for(int i=0; i<data.length;i++) {
				for(int j=0; j<data[i].length;j++)
					if(merged.contains(data[i][j]) == false)
						merged.add(data[i][j]);
			}
			} catch(Exception e) {
				System.out.println(data.length);
				
			}
			
		} else if (mode == QueryProcessor.BOOL_AND) { //AND
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int i=0; i<data.length;i++) {
				for(int j=0; j<data[i].length;j++) {
					if(i==0) {
						temp.add(data[i][j]);
					} else {
						if(temp.contains(data[i][j]) == true)
							merged.add(data[i][j]);
						else if(merged.contains(data[i][j]))
							merged.remove(data[i][j]);
					}
				}
			}
		}
		result = new int[merged.size()];
		for(int i=0; i<merged.size();i++)
			result[i] = merged.get(i);
		
		return result;
	}
	
	private int[][] removeNullData(int[][] data) {
		ArrayList<int[]> list = new ArrayList<int[]>();
		int[][] ret = null;
		
		for(int i=0; i<data.length;i++) {
			if(data[i] != null)
				list.add(data[i]);
		}
		
		ret = new int[list.size()][];
		for(int i=0; i<list.size();i++) {
			ret[i] = list.get(i);
		}

		return ret;
	}
}
