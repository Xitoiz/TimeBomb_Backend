package fr.xitoiz.timebomb.user;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.xitoiz.timebomb.exeption.LoginAlreadyUsedException;
import fr.xitoiz.timebomb.exeption.TransactionErrorException;
import fr.xitoiz.timebomb.exeption.UserNotFoundException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/user")
public class UserApiController {
	
    @Autowired
    private UserDAO daoUser;
    
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserSession userSession;
    
    @PostMapping("/inscription")
    private void inscription(@Valid @RequestBody User user) {
    	Optional<User> dbUser = this.daoUser.findByLogin(user.getLogin());
    	if (!dbUser.isEmpty()) {
    		throw new LoginAlreadyUsedException();
    	}
    	
    	user.setPassword(this.passwordEncoder.encode(user.getPassword()));
    	
    	try {
    		this.daoUser.save(user);
    	} catch(Exception e) {
    		throw new TransactionErrorException();
    	}
    }
    
	@PostMapping("/login")
	private User login(@RequestBody User user) {
		User dbUser = this.daoUser.findByLogin(user.getLogin()).orElseThrow(UserNotFoundException::new);
		if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
			throw new UserNotFoundException();
		}
		
		this.userSession.setId(dbUser.getId());
		this.userSession.setPseudo(dbUser.getPseudo());
		this.userSession.setAccountType(dbUser.getAccountType());

		return dbUser;
	}
	
}
