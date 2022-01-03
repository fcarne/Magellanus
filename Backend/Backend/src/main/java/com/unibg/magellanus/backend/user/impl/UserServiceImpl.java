package com.unibg.magellanus.backend.user.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.unibg.magellanus.backend.user.UserRepository;
import com.unibg.magellanus.backend.user.UserService;
import com.unibg.magellanus.backend.user.model.User;

@Service
public class UserServiceImpl implements UserService {

	UserRepository repository;

	@Autowired
	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public boolean checkIfExists(String uid) {
		return repository.existsById(uid);
	}

	@Override
	public User signUp(User user) {
		if (repository.findById(user.getUid()).isEmpty())
			return repository.save(user);
		else
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
					"Seems like you are already registered!");
	}

	@Override
	public boolean delete(String uid) {
		repository.deleteById(uid);
		return true;
	}

	@Override
	public User updatePreferences(String uid, Map<String, Object> preferences) {
		return repository.findById(uid).map(user -> {
			user.setPreferences(preferences);
			return repository.save(user);
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested user not found"));
	}

	@Override
	public Map<String, Object> getPreferences(String uid) {
		return repository.findById(uid).map(User::getPreferences)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested user not found"));
	}
}
