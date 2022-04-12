package fr.xitoiz.timebomb.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.xitoiz.timebomb.models.User;

public interface IDAOUser extends JpaRepository<User, Integer> {
	
	public Optional<User> findByLogin(String username);

}