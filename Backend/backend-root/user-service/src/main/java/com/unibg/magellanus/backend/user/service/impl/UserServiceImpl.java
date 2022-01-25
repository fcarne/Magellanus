package com.unibg.magellanus.backend.user.service.impl;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unibg.magellanus.backend.user.service.User;
import com.unibg.magellanus.backend.user.service.UserRepository;
import com.unibg.magellanus.backend.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	UserRepository repository;

	@Autowired
	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public User get(String uid) {
		return repository.findById(uid).orElseThrow(() -> new NoSuchElementException("Requested user not found"));
	}

	@Override
	public User signUp(User user) {
		if (repository.findById(user.getUid()).isEmpty())
			return repository.save(user);
		else
			throw new IllegalArgumentException("Seems like you are already registered!");
	}

	@Override
	public void delete(String uid) {
		repository.deleteById(uid);
	}

	@Override
	public User updatePreferences(String uid, Map<String, Object> preferences) {
		return repository.findById(uid).map(user -> {
			user.setPreferences(preferences);
			return repository.save(user);
		}).orElseThrow(() -> new NoSuchElementException("Requested user not found"));
	}

	@Override
	public Map<String, Object> getPreferences(String uid) {
		return repository.findById(uid).orElseThrow(() -> new NoSuchElementException("Requested user not found"))
				.getPreferences();
	}
}
