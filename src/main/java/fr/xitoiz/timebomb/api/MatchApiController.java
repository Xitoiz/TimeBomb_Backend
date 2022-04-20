package fr.xitoiz.timebomb.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.xitoiz.timebomb.dao.IDAOMatch;
import fr.xitoiz.timebomb.services.UserSession;

@RestController
@CrossOrigin("*")
@RequestMapping("/match")
public class MatchApiController {
	
}
