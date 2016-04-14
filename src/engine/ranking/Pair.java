package engine.ranking;

// paire of docID and it's score for ranking (sort)
public class Pair {
	public int docID;
	double score;
	
	public Pair(int id, double s) {
		docID=id;
		score=s;
	}
	
	public double getScore() {
		return score;
	}
	public void setScore(double s) {
		score = s;
	}
}
