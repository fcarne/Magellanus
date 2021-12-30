package com.unibg.magellanus.backend.usermanager;

import java.util.Map;

public interface UserService {
	public User signUp(String email);

	public User updatePreferences(Map<String, Object> preferences);

	public Map<String, Object> getPreferences();

	public void deleteUser();
}
