package com.unibg.magellanus.backend.usermanager;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public class User {
	@Transient
	public static final String SEQUENCE_NAME = "users_sequence";

	@Id
	private long id;
	private String email;
	private Map<String, Object> preferences;

	public User(String email, Map<String, Object> preferences) {
		this.email = email;
		this.preferences = preferences;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<String, Object> getPreferences() {
		return preferences;
	}

	public void setPreferences(Map<String, Object> preferences) {
		this.preferences = preferences;
	}

}
