package fr.xitoiz.timebomb.match_result;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchResultDAO extends JpaRepository<MatchResult, Integer> {

	@Query(nativeQuery = true, value = "SELECT DISTINCT (user_id), user_pseudo FROM `match_result` m JOIN( SELECT user_id, user_pseudo, `match_result_match_id` FROM USER u JOIN `match_result_loosers` ON `loosers_user_id` = u.`user_id` ) table1 ON table1.`match_result_match_id` = m.`match_id` UNION SELECT DISTINCT (user_id), user_pseudo FROM `match` m JOIN( SELECT user_id, user_pseudo, `match_result_match_id` FROM USER u JOIN `match_result_winners` ON `winners_user_id` = u.`user_id` ) table2 ON table2.`match_result_match_id` = m.`match_id` ORDER BY user_id ASC")
	public List<Object> listPlayer();

	@Query(nativeQuery = true, value = "SELECT * FROM ( SELECT match_id, Looser as State, match_winners_role, loosers_user_id as user_id, user_pseudo FROM (SELECT match_id, \"Looser\", match_winners_role, loosers_user_id FROM `match_result` JOIN `match_result_loosers` ON `match_result_match_id` = `match_id`) as l JOIN user ON user_id = loosers_user_id UNION SELECT match_id, Winner as State, match_winners_role, winners_user_id as user_id, user_pseudo FROM (SELECT match_id, \"Winner\", match_winners_role, winners_user_id FROM `match_result` JOIN `match_result_winners` ON `match_result_match_id` = `match_id`) as w JOIN user ON user_id = winners_user_id ) as t GROUP BY State, match_winners_role, user_id ORDER BY match_id ASC")
	public List<Object> resultat();

	@Query(nativeQuery = true, value="SELECT COUNT(match_id), State, match_winners_role, user_id, user_pseudo FROM ( SELECT match_id, Looser as State, match_winners_role, loosers_user_id as user_id, user_pseudo FROM (SELECT match_id, \"Looser\", match_winners_role, loosers_user_id FROM `match_result` JOIN `match_result_loosers` ON `match_result_match_id` = `match_id`) as l JOIN user ON user_id = loosers_user_id UNION SELECT match_id, Winner as State, match_winners_role, winners_user_id as user_id, user_pseudo FROM (SELECT match_id, \"Winner\", match_winners_role, winners_user_id FROM `match_result` JOIN `match_result_winners` ON `match_result_match_id` = `match_id`) as w JOIN user ON user_id = winners_user_id ) as t GROUP BY State, match_winners_role, user_id ORDER BY user_id ASC")
	public List<Object> classement();
	
	@Query(nativeQuery = true, value="SELECT match_id, State, match_winners_role FROM ( SELECT match_id, Looser as State, match_winners_role, loosers_user_id as user_id, user_pseudo FROM (SELECT match_id, \"Looser\", match_winners_role, loosers_user_id FROM `match_result` JOIN `match_result_loosers` ON `match_result_match_id` = `match_id`) as l JOIN user ON user_id = loosers_user_id UNION SELECT match_id, Winner as State, match_winners_role, winners_user_id as user_id, user_pseudo FROM (SELECT match_id, \"Winner\", match_winners_role, winners_user_id FROM `match_result` JOIN `match_result_winners` ON `match_result_match_id` = `match_id`) as w JOIN user ON user_id = winners_user_id ) as t where user_id = :id ORDER BY match_id DESC")
	public List<Object> historiqueById(@Param("id") int id);
	
}
