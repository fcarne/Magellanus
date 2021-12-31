package com.unibg.magellanus.backend.usermanager;

import java.util.Map;

public interface UserService {
	public User signUp(User user);

	public User updatePreferences(Map<String, Object> preferences);

	public Map<String, Object> getPreferences();

	public boolean deleteUser();
}
