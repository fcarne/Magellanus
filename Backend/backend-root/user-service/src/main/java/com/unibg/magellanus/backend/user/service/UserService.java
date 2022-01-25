package com.unibg.magellanus.backend.user.service;

import java.util.Map;
import java.util.NoSuchElementException;

public interface UserService {
	public User get(String uid);

	public User signUp(User user) throws IllegalArgumentException;

	public void delete(String uid) throws IllegalArgumentException;

	public User updatePreferences(String uid, Map<String, Object> preferences) throws NoSuchElementException;

	public Map<String, Object> getPreferences(String uid) throws NoSuchElementException;

}
