package com.unibg.magellanus.backend.usermanager.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.unibg.magellanus.backend.usermanager.User;
import com.unibg.magellanus.backend.usermanager.UserRepository;
import com.unibg.magellanus.backend.usermanager.UserService;

@Service
public class UserServiceImpl implements UserService {

	UserRepository repository;

	@Autowired
	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public User signUp(User user) {
		if (repository.findById(user.getId()).isEmpty())
			return repository.save(user);
		else
			return null;
	}

	@Override
	public User updatePreferences(Map<String, Object> preferences) {
		User loggedUser = getUserFromContext();
		if (loggedUser != null) {
			loggedUser.setPreferences(preferences);
			return repository.save(loggedUser);
		} else
			return null;
	}

	@Override
	public Map<String, Object> getPreferences() {
		User loggedUser = getUserFromContext();
		if (loggedUser != null)
			return repository.findById(loggedUser.getId()).get().getPreferences();
		else
			return null;
	}

	@Override
	public boolean deleteUser() {
		User loggedUser = getUserFromContext();
		if (loggedUser != null) {
			repository.deleteById(loggedUser.getId());
			return true;
		} else
			return false;
	}

	private User getUserFromContext() {
		Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
		if (User.class.isAssignableFrom(credentials.getClass()))
			return (User) credentials;
		else
			return null;
	}
}
