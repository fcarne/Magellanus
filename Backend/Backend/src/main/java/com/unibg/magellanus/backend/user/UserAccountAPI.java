package com.unibg.magellanus.backend.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.unibg.magellanus.backend.user.model.User;

public interface UserAccountAPI {
	public ResponseEntity<Void> checkIfExists(String uid);

	public ResponseEntity<Void> signUp(User user);

	public ResponseEntity<Void> delete(String uid);

	public ResponseEntity<Void> updatePreferences(String uid, Map<String, Object> preferences);

	public Map<String, Object> getPreferences(String uid);

}
