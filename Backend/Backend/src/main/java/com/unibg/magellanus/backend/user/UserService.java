package com.unibg.magellanus.backend.user;

import java.util.Map;

import com.unibg.magellanus.backend.user.model.User;

public interface UserService {
	public boolean checkIfExists(String uid);

	public User signUp(User user);

	public boolean delete(String uid);

	public User updatePreferences(String uid, Map<String, Object> preferences);

	public Map<String, Object> getPreferences(String uid);

}
