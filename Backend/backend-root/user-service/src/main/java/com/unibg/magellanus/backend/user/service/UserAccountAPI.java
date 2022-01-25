package com.unibg.magellanus.backend.user.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface UserAccountAPI {
	public User getUser(String uid);

	public ResponseEntity<Void> signUp(User user);

	public ResponseEntity<Void> deleteMe();

	public ResponseEntity<Void> updatePreferencesMe(Map<String, Object> preferences);

	public Map<String, Object> getPreferencesMe();

}
