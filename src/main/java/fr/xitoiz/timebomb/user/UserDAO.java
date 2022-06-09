package fr.xitoiz.timebomb.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {
	
	public Optional<User> findByLogin(String username);

}