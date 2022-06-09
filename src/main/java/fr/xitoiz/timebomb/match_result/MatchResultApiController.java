package fr.xitoiz.timebomb.match_result;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import fr.xitoiz.timebomb.projection.Views;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/resultats")
public class MatchResultApiController {

	@Autowired
	private MatchResultDAO daoMatchResult;
	
	@GetMapping
	@JsonView(Views.MatchSummary.class)
	public List<MatchResult> getAllMatch() {
		return this.daoMatchResult.findAll();
	}
	
	@GetMapping("/list")
	public List<Object> getListPlayer() {
		return this.daoMatchResult.listPlayer();
	}
	
	@GetMapping("/resultat")
	public List<Object> getResultat() {
		return this.daoMatchResult.resultat();
	}
	
	@GetMapping("/classement")
	public List<Object> getClassement() {
		return this.daoMatchResult.classement();
	}
	
	@GetMapping("/historique/{id}")
	public List<Object> getHistriqueById(@PathVariable("id") int id) {
		return this.daoMatchResult.historiqueById(id);
	}
	
	

}
