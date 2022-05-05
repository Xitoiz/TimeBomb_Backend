package fr.xitoiz.timebomb.match_result;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MatchResultDAO extends JpaRepository<MatchResult, Integer>{

	@Query(nativeQuery = true, 
			value = "SELECT DISTINCT (user_id), user_pseudo FROM `match_result` m JOIN( SELECT user_id, user_pseudo, `match_result_match_id` FROM USER u JOIN `match_result_loosers` ON `loosers_user_id` = u.`user_id` ) table1 ON table1.`match_result_match_id` = m.`match_id` UNION SELECT DISTINCT (user_id), user_pseudo FROM `match` m JOIN( SELECT user_id, user_pseudo, `match_result_match_id` FROM USER u JOIN `match_result_winners` ON `winners_user_id` = u.`user_id` ) table2 ON table2.`match_result_match_id` = m.`match_id` ORDER BY user_id ASC")
	public List<Object> listPlayer();

	@Query(nativeQuery = true, 
			value = "SELECT user_id, user_pseudo, COUNT(match_id) AS Victories FROM `match_result` m JOIN(SELECT * FROM USER u JOIN `match_result_winners` ON `winners_user_id` = u.`user_id`) mwu ON m.match_id = mwu.match_result_match_id WHERE `match_winners_role` = `SHERLOCK` GROUP BY user_pseudo ORDER BY user_id ASC")
	public List<Object> sherlockWinners();
	
	@Query(nativeQuery = true, 
			value = "SELECT user_id, user_pseudo, COUNT(match_id) AS Victories FROM `match_result` m JOIN(SELECT * FROM USER u JOIN `match_result_winners` ON `winners_user_id` = u.`user_id`) mwu ON m.match_id = mwu.match_result_match_id WHERE `match_winners_role` = `MORIARTY` GROUP BY user_pseudo ORDER BY user_id ASC")
	public List<Object> moriartyWinners();
	
	@Query(nativeQuery = true,
			value = "SELECT user_id, user_pseudo, COUNT(match_id) AS Defeats FROM `match_result` m JOIN(SELECT * FROM USER u JOIN `match_result_loosers` ON `loosers_user_id` = u.`user_id`) mlu ON m.match_id = mlu.match_result_match_id WHERE `match_winners_role` = `MORIARTY` GROUP BY user_pseudo ORDER BY user_id ASC")
	public List<Object> sherlockLoosers();
	
	@Query(nativeQuery = true,
			value = "SELECT user_id, user_pseudo, COUNT(match_id) AS Defeats FROM `match_result` m JOIN(SELECT * FROM USER u JOIN `match_result_loosers` ON `loosers_user_id` = u.`user_id`) mlu ON m.match_id = mlu.match_result_match_id WHERE `match_winners_role` = `SHERLOCK` GROUP BY user_pseudo ORDER BY user_id ASC")
	public List<Object> moriartyLoosers();
}
