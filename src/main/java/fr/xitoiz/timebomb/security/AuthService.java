package fr.xitoiz.timebomb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.xitoiz.timebomb.dao.IDAOUser;
import fr.xitoiz.timebomb.models.User;
import fr.xitoiz.timebomb.services.UserSession;

@Service
public class AuthService implements UserDetailsService {
	@Autowired
	private IDAOUser daoUser;

	@Autowired
	private UserSession userSession;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.daoUser.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException("Username not found."));
		
		this.userSession.setId(user.getId());
		this.userSession.setName(user.getPseudo());
		this.userSession.setAccountType(user.getAccountType());

		return new UserPrincipal(user);
	}
}