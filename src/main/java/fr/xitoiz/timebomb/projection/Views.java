package fr.xitoiz.timebomb.projection;

public final class Views {
	
	private Views() {
	    throw new IllegalStateException("Utility class");
	  }
	
	public static class Common {}
	public static class Match extends Common {}
	public static class MatchAdmin extends Match {}
	public static class User extends Common {}
	public static class Card extends Common {}
}
