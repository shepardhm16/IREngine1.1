package utility;

import java.util.ArrayList;

import engine.ranking.Pair;

public class HtmlGenerator {
	public static String getSearchResult(ArrayList<Pair> rank, String[] docNames, String[] docTitles) {
		String html = "<div><h2 style='font-size:32px;'>" + rank.size() + " out of " + docNames.length + " documents are matched</h2><div><ul>";


		for (int i=0; i<rank.size(); i++) {
			String url = System.getProperty("user.dir") + docNames[rank.get(i).docID];
			html += "<li style='line-height:30px;font-size:20px;'"
					+ "><a href=\"file:///" + url + "\">" +
					docTitles[rank.get(i).docID] + "</a> "
							+ "<span style='font-size:15px;padding-left:20px'>score " + rank.get(i).getScore() + "</span></li>";
		}

		html += "</ul></div></div>";
		return html;
	}
	
	public static String getNoResultFound(String q) {
		String html = "<div> <h2>There is no document relevant to the query <span style='color:red'>" + 
					q + "</span></h2><br><br>Please check your query and try with another query.</div>";
		
		return html;
	}
	

}
