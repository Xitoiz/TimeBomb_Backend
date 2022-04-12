package fr.xitoiz.timebomb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import fr.xitoiz.timebomb.models.Card;
import fr.xitoiz.timebomb.models.Match;

public interface IDAOCard extends JpaRepository<Card, Integer> {
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Card c SET c.owner = NULL WHERE c.match.id = :id", nativeQuery = false)
	public void clearMatch(@Param("id") int id);
	
	@Modifying
	@Transactional
	@Query(value="DELETE FROM Card c WHERE c.match.id = :id", nativeQuery = false)
	public void deleteCardMatch(@Param("id") int id);

	public List<Card> findAllByMatch(Match match);
	
}