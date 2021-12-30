package com.unibg.magellanus.backend.usermanager.auth.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.unibg.magellanus.backend.usermanager.User;
import com.unibg.magellanus.backend.usermanager.UserRepository;
import com.unibg.magellanus.backend.usermanager.auth.TokenVerifier;

@Component
public class FirebaseTokenVerifier implements TokenVerifier<FirebaseToken> {

	UserRepository repository;

	@Autowired
	public FirebaseTokenVerifier(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public FirebaseToken verifyToken(String tokenString) throws FirebaseAuthException {
		return FirebaseAuth.getInstance().verifyIdToken(tokenString);
	}

	@Override
	public User tokenToUser(FirebaseToken token) {
		if (token == null)
			return null;
		List<User> users = repository.findByEmail(token.getEmail());
		if (users.size() != 1)
			return null;
		else
			return users.get(0);
	}

	@Configuration
	public static class FirebaseConfig {
		@Primary
		@Bean
		public void firebaseInit() {
			try (InputStream inputStream = new ClassPathResource("firebase_config.json").getInputStream()) {
				FirebaseOptions options = FirebaseOptions.builder()
						.setCredentials(GoogleCredentials.fromStream(inputStream)).build();
				if (FirebaseApp.getApps().isEmpty()) {
					FirebaseApp.initializeApp(options);
				}
				System.out.println("Firebase Initialize");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
