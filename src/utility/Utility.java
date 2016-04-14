package utility;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;



public class Utility {

	public static ArrayList<String> getAllDocuments(String dir) throws IOException {
		ArrayList<String> docs = new ArrayList<String>();

		Files.walk(Paths.get(dir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				docs.add(filePath.toString());
			}
		});
		return docs;
	}

	public static int search(String[] tree, String key) {
		int low = 0;
		int high = tree.length - 1;
		int mid;
		int v;
		while (low <= high) {
			mid = (low + high) / 2;
			v = tree[mid].compareTo(key);
			if      (v > 0) high = mid - 1;
			else if (v < 0) low = mid + 1;
			else return mid;
		}
		return -1;
	}

	public static void createDirectory(String path) {
		File theDir = new File(path);

		// if the directory does not exist, create it
		if (!theDir.exists()) {

			try{
				theDir.mkdir();
			} 
			catch(SecurityException se){
				//handle it
			}        
		}
	}
	
	
	
	
}
