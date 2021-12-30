package com.unibg.magellanus.backend.usermanager;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface UserAccountAPI {
	public ResponseEntity<String> signUp(String email);

	public ResponseEntity<String> updatePreferences(Map<String, Object> preferences);

	public Map<String, Object> getPreferences();

	public ResponseEntity<String> delete();

}
