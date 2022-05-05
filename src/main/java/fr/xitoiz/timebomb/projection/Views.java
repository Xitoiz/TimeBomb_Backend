package fr.xitoiz.timebomb.projection;

public interface Views {
	
	public static interface Common {}
	public static interface Match extends Common {}
	public static interface MatchAdmin extends Match {}
	public static interface User extends Common {}
	public static interface Card extends Common {}
}
