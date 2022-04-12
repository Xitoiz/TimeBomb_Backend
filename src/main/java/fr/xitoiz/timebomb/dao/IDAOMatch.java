package fr.xitoiz.timebomb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.xitoiz.timebomb.models.Match;

public interface IDAOMatch extends JpaRepository<Match, Integer>{
	
	@Query(value = "SELECT m FROM Match m WHERE m.state = 'TERMINATED' ", nativeQuery = false)
	public List<Match> findAllTerminatedMatch();
	
	@Query(value = "SELECT m FROM Match m WHERE m.state = 'PENDING' ", nativeQuery = false)
	public List<Match> findAllPendingMatch();

}
