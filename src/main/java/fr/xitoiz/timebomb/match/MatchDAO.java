package fr.xitoiz.timebomb.match;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MatchDAO extends JpaRepository<Match, Integer>{
	
	@Query(value = "SELECT m FROM Match m WHERE m.state = 'PLAYING' ", nativeQuery = false)
	public List<Match> findAllPlayingMatch();
	
	@Query(value = "SELECT m FROM Match m WHERE m.state = 'PENDING' ", nativeQuery = false)
	public List<Match> findAllPendingMatch();

}
