package fr.xitoiz.timebomb.projection;

public interface Views {
	
	public static interface Common {}
	public static interface MatchSummary extends Common {}
	public static interface Match extends MatchSummary {}
	public static interface User extends Common {}
	public static interface Card extends Common {}
}
