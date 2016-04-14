package crawler;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLDecoder;



public class Crawler {
	private  static ArrayList<String> allLinks = new ArrayList<String>();
	private static BufferedWriter writer = null;
	private static HashMap<String,String> queries = new HashMap<String,String>();
	
	public static void main(String[] args) throws IOException {
		//File is ready
		try {
			writer = new BufferedWriter(new FileWriter("vmilinks.txt"));
		} catch(IOException ioe) {
			System.out.println("File Handling Error!!!");
		}

		allLinks.add("http://www.vmi.edu");
		int i = 0;
		String url = null;
		Document doc = null;
		Elements links = null;
		
		while((url = allLinks.get(i++)) != null) {

			//use next line if you want to check your program is alive and works correctly
			//System.out.println(i + ": " + url);
			try {
				doc = Jsoup.connect(url).timeout(2000).	get();
				links = doc.select("a[href]");

				for (Element link : links) {
					addLink(link.attr("abs:href").trim());
				}
			} catch(Exception e) {
				System.out.println("Error: " + e.getMessage() + url);
				
			}
			// use next line for testing. i means number of url to be processed
			//if(i>3) break;

		}
		writer.close();
	}

	private static void setQueries(String link) {
		try {
			URL url = new URL(link);
			String query = url.getQuery();
		    String[] pairs = query.split("&");
		    
		    
		    for (String pair : pairs) {
		    	String[] temp = pair.split("=");
		    	if(temp.length>1)
		    		queries.put(temp[0], temp[1]);
		    }
		} catch(Exception e) {
			
		}
	}
	private static void addLink(String link) {
		if(isVMIpage(link) && isValidFormat(link) && !isMailTo(link) && !is404(link)) {
			setQueries(link);
			link = removeFragment(link);
			
			if(link.contains("calendar.vmi.edu")) {
				link =  getValidCalendarPage(link);
				if(link == null)
					return;
			} else if(link.contains("digitalcollections.vmi.edu")) {
				if(isValidDigitalCollectionPage(link) == false)
					return;
			} else if(link.contains("archivesspace.vmi.edu")) {
				if(isValidArchivesSpacePage(link) == false)
					return;
			} else if(isSearchPage(link))
				return;
			else if(link.contains("DownloadAsset.aspx") || link.contains("/getdownloaditem") 
					|| link.contains("uploadedFiles"))
				return;
			else if(link.contains("/WorkArea/"))
				return;
			else if(link.contains("catalog.vmi.edu")) {
				link = getValidCatalogPage(link);
				if(link == null)
					return;
			}
			if(!allLinks.contains(link)) {
				allLinks.add(link);
				writeLinkToFile(link);
			}
		}

	}


	private static boolean isSearchPage(String link) {
		return link.contains("www.vmi.edu/Search") || link.contains("www.vmi.edu/search")
				|| link.contains("advanced_search");
	}
	private static boolean isValidDigitalCollectionPage(String link) {
		return !link.contains("/search");
	}
	
	private static boolean isValidArchivesSpacePage(String link) {
		return !link.contains("/search");
	}
	
	private static boolean isVMIpage(String link) {
		return link.contains("vmi.edu") && !link.startsWith("https");
	}

	
	//http://archivesspace.vmi.edu/search
	
	//http://digitalcollections.vmi.edu/cdm/singleitem/
	private static String getValidCatalogPage(String link) {
		String base = "http://catalog.vmi.edu/";
		try {
			if(link.contains("help.php"))
				return base+"help.php";
			else if(link.contains("/misc/"))
					return base + "misc/catalog_list.php";
		     
		    String catoid = queries.get("catoid");
		    if(catoid == null) return null;
		    		
		    if(link.contains("content.php")) {
		    	if(queries.get("navoid") != null)
		    		return base + "content.php?catoid=" + catoid +
		    				"&navoid=" + queries.get("navoid");
		    } else if (link.contains("index.php")) {
		    	if(queries.get("catoid") != null)
		    		return base + "index.php?catoid=" + catoid;
		    	else return base;
		    } if(link.contains("preview_course_nopop.php")) {
		    	if(queries.get("coid") != null)
		    		return base + "preview_course_nopop.php?catoid=" +catoid +
		    				"&coid=" + queries.get("coid");
		    } if(link.contains("preview_entity.php")) {
		    	if(queries.get("ent_oid") != null)
		    		return base + "preview_course_nopop.php?catoid=" +catoid +
		    				"&ent_oid=" + queries.get("ent_oid");
		    } if(link.contains("preview_program.php")) {
		    	if(queries.get("poid") != null)
		    		return base + "preview_course_nopop.php?catoid=" +catoid +
		    				"&poid=" + queries.get("poid");
		    }
		} catch(Exception e) {
			
		}
		return null;
	}
	
	private static String getValidCalendarPage(String link) {
		try {
			URL url = new URL(link);
			String query = url.getQuery();
		    String[] pairs = query.split("&");
		    String eventID = null;
		    for (String pair : pairs) {
		    	int idx = pair.indexOf("=");
		    	String queryName = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
		    	if(queryName.equalsIgnoreCase("EVTID")) {
		    		eventID = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
		    		return "http://calendar.vmi.edu/content.asp?EVTID="+eventID;
		    	}
		    }
		} catch(Exception e) {
			
		}
		return null;
	}
	
	private static boolean isMailTo(String link) {
		return link.contains("mailto:");
	}
	
	private static boolean is404(String link) {
		return link.contains("404.aspx");
	}
	
	//ex:http://archivesspace.vmi.edu/repositories/3/resources/598/format/ead_pdf
	private static boolean isValidFormat(String link) {
		return !(link.endsWith(".pdf") || link.endsWith(".jpeg") 
				|| link.endsWith(".jpg") || link.endsWith(".png")
				|| link.endsWith(".gif") || link.endsWith("ead_pdf"));
	}
	private static String removeFragment(String link) {
		int pos = link.indexOf('#');
		if(pos>0)
			return link.substring(0, pos-1);
		return link;
					
		/* if(link.endsWith("#"))
			return link.substring(0, link.length()-2);
		return link.replace("#mainContent", "").replace("#menu", "")
				.replace("#nav-main", "").replace("#top", "");
		*/
	}

	private static void writeLinkToFile(String link) {
		try {

			writer.write(link);
			writer.newLine();

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
}


